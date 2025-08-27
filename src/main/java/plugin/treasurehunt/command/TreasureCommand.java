package plugin.treasurehunt.command;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import plugin.treasurehunt.Main;
import plugin.treasurehunt.data.ExecutingPlayer;
import plugin.treasurehunt.mapper.PlayerScoreMapper;
import plugin.treasurehunt.mapper.data.PlayerScore;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * 制限時間以内にランダムで指定されたお宝を手にいれるゲームを起動するコマンドです。
 * 見つけた時間によってスコアが変わり、見つけたお宝によってスコアが変動します。
 * 結果はプレイヤー名、点数、日時などで保存されます。
 */
public class TreasureCommand extends BaseCommand implements Listener {

    public static final int GAME_TIME = 540;
    public static final String LIST = "list";

    private Main main;
    private org.bukkit.scheduler.BukkitTask timerTask;
    private List<ExecutingPlayer> executingPlayerList = new ArrayList<>();
    private Material material;
    private Long startCountTime;
    private int alarm = 0;
    private boolean timeoutNotified = false; // 二重表示ガード（任意）

    private SqlSessionFactory sqlSessionFactory;

    public TreasureCommand(Main main) {
        this.main = main;

        try {
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 1 && LIST.equals(args[0])) {
            try (SqlSession session = sqlSessionFactory.openSession()) {
                PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
                List<PlayerScore> playerScoreList = mapper.selectList();

                for(PlayerScore playerScore : playerScoreList) {

                    player.sendMessage("%d | %s | %d | %.2f秒 | %s"
                            .formatted(playerScore.getId(), playerScore.getPlayerName(),
                                    playerScore.getScore(), playerScore.getElapsedSec(),
                                    playerScore.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                }
            }
            return false;
        }

        ExecutingPlayer nowExecutingPlayer = getPlayerScore(player);

        alarm = 0;
        nowExecutingPlayer.setGameTime(GAME_TIME);

        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }

        gamePlay(player, nowExecutingPlayer);

        player.setHealth(20);
        player.setFoodLevel(20);

        startCountTime = System.currentTimeMillis();

        this.material = getMaterial();

        player.sendTitle("お宝を探そう!","今回は「" + this.material + "」!"
                ,0,60,0);
        return true;
    }


    @Override
    public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    /**
     * 現在実行しているプレイヤーのスコア情報を取得する。
     * @param player　コマンドを実行したプレイヤー
     * @return 現在実行しているプレイヤーのスコア情報
     */
    private ExecutingPlayer getPlayerScore(Player player) {
        ExecutingPlayer executingPlayer = new ExecutingPlayer(player.getName());
        if (executingPlayerList.isEmpty()){
            executingPlayer = addNewPlayer(player);
        } else {
            executingPlayer = executingPlayerList.stream()
                    .findFirst()
                    .map(ps -> ps.getPlayerName().equals(player.getName())
                            ? ps
                            : addNewPlayer(player)).orElse(executingPlayer);
        }
        executingPlayer.setGameTime(GAME_TIME);
        executingPlayer.setScore(0);
        return executingPlayer;
    }

    /**
     * 新規のプレイヤー情報をリストに追加します。
     *
     * @param player　コマンドを実行したプレイヤー
     * @return 新規プレイヤー
     */
    private ExecutingPlayer addNewPlayer(Player player) {
        ExecutingPlayer newPlayer = new ExecutingPlayer(player.getName());
        executingPlayerList.add(newPlayer);
        return newPlayer;
    }

    /**
     * ゲームを実行します。1分ごとに知らせがあり、10分経ったら時間切れを表示します。
     *
     * @param player　コマンドを実行したプレイヤー
     * @param nowExecutingPlayer　プレイヤースコア情報
     */
    private void gamePlay(Player player, ExecutingPlayer nowExecutingPlayer) {
        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }
        timeoutNotified = false;

        timerTask = Bukkit.getScheduler().runTaskTimer(main, () -> {
            int remain = nowExecutingPlayer.getGameTime();
            if (remain <= 0) {
                if (!timeoutNotified) {
                    player.sendTitle("残念！時間切れ…", "また挑戦してね！", 0, 60, 0);
                    timeoutNotified = true;
                }

                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                return;
            }
            alarm++;
            player.sendMessage(alarm + "分経過！");
            nowExecutingPlayer.setGameTime(remain - 60);
        }, 60 * 20L, 60 * 20L);
    }



    /**
     * ランダムでItemを指定して、その結果を取得します。
     * @return Item
     */
    private static Material getMaterial() {
        List<Material> materialList = List.of(Material.APPLE,Material.PORKCHOP,Material.EGG);
        int random = new SplittableRandom().nextInt(materialList.size());
        return materialList.get(random);
    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player) || executingPlayerList.isEmpty() || material == null) return;
        Material picked = e.getItem().getItemStack().getType();

        executingPlayerList.stream()
                .filter(p -> p.getPlayerName().equals(player.getName()))
                .findFirst()
                .ifPresent(p -> {
                    if (picked == this.material) {

                        long endCountTime = System.currentTimeMillis() - startCountTime;
                        double seconds = endCountTime / 1000.0;

                        int timeScore = 0;
                        if (seconds < 60) {
                            timeScore = 100;
                        } else if (seconds < 300) {
                            timeScore = 50;
                        }

                        int point = switch (this.material) {
                            case APPLE -> 10;
                            case PORKCHOP -> 20;
                            case EGG -> 30;
                            default -> 0;
                        };

                        int totalScore = timeScore + point;

                        int resultScore = p.getScore() + totalScore;
                        p.setScore(resultScore);

                        if (totalScore > 0 ) {
                            player.sendTitle("お宝発見！ スコア%d点"
                                            .formatted(resultScore),
                                    "%.2f秒 （時間%d / アイテム%d）"
                                            .formatted(seconds, timeScore, point),
                                    0, 120, 0);
                        } else {
                            player.sendTitle("残念！時間切れ...", "また見つけてね！",
                                    0, 60, 0);
                        }

                        //　スコア登録処理
                        try (SqlSession session = sqlSessionFactory.openSession(true)) {
                            PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
                            mapper.insert(new PlayerScore(p.getPlayerName(), resultScore, seconds));
                        }

                        p.setScore(0);
                    }


                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                    }
                    material = null;
                    startCountTime = null;
                });
    }
}
