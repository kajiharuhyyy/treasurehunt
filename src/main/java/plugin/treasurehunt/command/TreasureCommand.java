package plugin.treasurehunt.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import plugin.treasurehunt.Main;
import plugin.treasurehunt.data.PlayerScore;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class TreasureCommand extends BaseCommand implements Listener {

    private Main main;
    private List<PlayerScore> playerScoreList = new ArrayList<>();
    private Material material;
    private Long startCountTime;
    private int gameTime = 540;
    private int alarm = 0;

    public TreasureCommand(Main main) {
        this.main = main;
    }


    @Override
    public boolean onExecutePlayerCommand(Player player) {
        PlayerScore nowPlayer = getPlayerScore(player);

        gameTime = 540;
        Bukkit.getScheduler().runTaskTimer(main, Runnable -> {
            if (gameTime <= 0) {
                player.sendTitle("残念！制限時間切れ…","また挑戦してね",0,60,0);
                Runnable.cancel();
                return;
            }
            alarm++;
            player.sendMessage(alarm + "分経過！");
            gameTime -= 60;
        },60 * 20, 60 * 20);

        player.setHealth(20);
        player.setFoodLevel(20);

        startCountTime = System.currentTimeMillis();

        this.material = getMaterial();

        player.sendTitle("お宝を探そう!","今回は「" + this.material + "」!"
                ,0,60,0);
        return true;
    }

    @Override
    public boolean onExecuteNPCCommand(CommandSender sender) {
        return false;
    }

    /**
     * 現在実行しているプレイヤーのスコア情報を取得する。
     * @param player　コマンドを実行したプレイヤー
     * @return 現在実行しているプレイヤーのスコア情報
     */
    private PlayerScore getPlayerScore(Player player) {
        if (playerScoreList.isEmpty()){
            return addNewPlayer(player);
        } else {
            for (PlayerScore playerScore : playerScoreList){
                if (!playerScore.getPlayerName().equals(player.getName())){
                    return addNewPlayer(player);
                } else {
                    return playerScore;
                }
            }
        }
        return null;
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
        if (!(e.getEntity() instanceof Player player)) return;
        if (playerScoreList.isEmpty() || material == null) return;
        Material picked = e.getItem().getItemStack().getType();

        for (PlayerScore playerScore : playerScoreList){
            if (playerScore.getPlayerName().equals(player.getName()) && picked == this.material) {

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

                int resultScore = playerScore.getScore() + totalScore;
                playerScore.setScore(resultScore);

                if (totalScore > 0 ) {
                    player.sendTitle("お宝発見！ スコア%d点"
                                    .formatted(resultScore),
                                    "%s %.2f秒 +%d点（時間%d / アイテム%d）"
                                    .formatted(playerScore.getPlayerName(), seconds, totalScore, timeScore, point),
                                0, 120, 0);
                    playerScore.setScore(0);
                } else {
                    player.sendTitle("残念！時間切れ...", "また見つけてね！",
                                0, 60, 0);
                    playerScore.setScore(0);
                }
                material = null;
                startCountTime = null;

                break;
            }
        }
    }


    /**
     * 新規のプレイヤー情報をリストに追加します。
     *
     * @param player　コマンドを実行したプレイヤー
     * @return 新規プレイヤー
     */
    private PlayerScore addNewPlayer(Player player) {
        PlayerScore newPlayer = new PlayerScore();
        newPlayer.setPlayerName(player.getName());
        playerScoreList.add(newPlayer);
        return newPlayer;
    }
}
