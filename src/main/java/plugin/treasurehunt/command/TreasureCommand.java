package plugin.treasurehunt.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import plugin.treasurehunt.data.PlayerScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;

public class TreasureCommand implements CommandExecutor, Listener {

    private List<PlayerScore> playerScoreList = new ArrayList<>();
    private Material material;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player player){
            if (playerScoreList.isEmpty()){
                addNewPlayer(player);
            } else {
                for (PlayerScore playerScore : playerScoreList){
                    if (!playerScore.getPlayerName().equals(player.getName())){
                        addNewPlayer(player);
                    }
                }
            }


            player.setHealth(20);
            player.setFoodLevel(20);

            this.material = getMaterial();

            player.sendMessage("今回は「" + this.material + "」が指定されました。");
        }
        return false;
    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (playerScoreList.isEmpty() || material == null) return;
        Material picked = e.getItem().getItemStack().getType();

        for (PlayerScore playerScore : playerScoreList){
            if (playerScore.getPlayerName().equals(player.getName()) && picked == this.material) {
                    playerScore.setScore(playerScore.getScore() + 10);
                    player.sendMessage("お宝発見!ゲーム終了!" + playerScore.getScore() + "点獲得!");
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
     */
    private void addNewPlayer(Player player) {
        PlayerScore newPlayer = new PlayerScore();
        newPlayer.setPlayerName(player.getName());
        playerScoreList.add(newPlayer);
    }
}
