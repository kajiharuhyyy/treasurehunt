package plugin.treasurehunt.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import plugin.treasurehunt.data.PlayerScore;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class TreasureCommand implements CommandExecutor, Listener {

    private List<PlayerScore> playerScoreList = new ArrayList<>();
    private Material material;
    private Long startCountTime;



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player player){
            PlayerScore nowPlayer = getPlayerScore(player);


            player.setHealth(20);
            player.setFoodLevel(20);

            startCountTime = System.currentTimeMillis();

            this.material = getMaterial();
            player.sendTitle("お宝を探そう!","今回は「" + this.material + "!"
                    ,0,60,0);

        }
        return false;
    }

    /**
     * 現在実行しているプレイヤーのスコア情報を取得する。
     * @param player　コマンドを実行したプレイヤー
     * @return
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


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (playerScoreList.isEmpty() || material == null) return;
        Material picked = e.getItem().getItemStack().getType();

        for (PlayerScore playerScore : playerScoreList){
            if (playerScore.getPlayerName().equals(player.getName()) && picked == this.material) {
                    long endCountTime = System.currentTimeMillis() - startCountTime;
                    double seconds = endCountTime / 1000.0;
                    if(seconds < 60){
                        playerScore.setScore(playerScore.getScore() + 100);
                    } else if (seconds < 300) {
                        playerScore.setScore(playerScore.getScore() + 50);
                    }
                    if (playerScore.getScore() > 0) {
                        player.sendTitle("お宝発見！",playerScore.getPlayerName()
                                + " " + seconds + "秒" + playerScore.getScore() + "点獲得!",
                                0, 60, 0);
                        playerScore.setScore(0);
                    } else {
                        player.sendTitle("残念！発見できず...", playerScore.getPlayerName()
                                        + " " + seconds + "秒" + playerScore.getScore() + "点獲得!",
                                0, 60, 0);
                        playerScore.setScore(0);
                    }
            }
        }
    }

    /**
     * ランダムでItemを指定して、その結果を取得します。
     * @return Item
     */
    private static Material getMaterial() {
        List<Material> materialList = List.of(Material.APPLE,Material.PORKCHOP,Material.EGG);
        int random = new SplittableRandom().nextInt(3);
        return materialList.get(random);
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
