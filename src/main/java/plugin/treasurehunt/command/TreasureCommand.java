package plugin.treasurehunt.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;

public class TreasureCommand implements CommandExecutor, Listener {

    private Player player;
    private Material material;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player player){
            this.player = player;
            player.setHealth(20);
            player.setFoodLevel(20);

            this.material = getMaterial();

            player.sendMessage("今回は「" + this.material + "」が指定されました。");
        }
        return false;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        Material picked = e.getItem().getItemStack().getType();
        if (Objects.isNull(player)){
            return;
        }
        if (Objects.isNull(this.material)){
            return;
        }
        if (this.player.getName().equals(player.getName()) && picked == this.material){
           player.sendMessage("お宝発見!ゲーム終了!");
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
}
