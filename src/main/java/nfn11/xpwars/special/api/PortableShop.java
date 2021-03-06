package nfn11.xpwars.special.api;

import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.game.GameStore;

public interface PortableShop extends SpecialItem {

    GameStore getGameStore();

    int getDuration();

    ItemStack getItem();

    void run();

}
