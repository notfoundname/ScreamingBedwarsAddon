package nfn11.xpwars.special.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.lib.nms.entity.EntityUtils;

import nfn11.xpwars.XPWars;
import nfn11.xpwars.special.PortableShop;
import nfn11.xpwars.utils.SpecialItemUtils;

public class PortableShopListener implements Listener {
	private static final String PORTABLE_SHOP_PREFIX = "Module:PortableShop:";

	@EventHandler
	public void onPortableShopBuy(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("portableshop")) {
			ItemStack stack = event.getStack();
			APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
		}
	}

	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		if (!Main.isPlayerInGame(player))
			return;
		Game game = Main.getPlayerGameProfile(player).getGame();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			for (Entity ent : player.getWorld().getEntities()) {
				if (ent.hasMetadata(player.getUniqueId().toString()) && ent.hasMetadata("portable-shop"))
					return;
			}
			ItemStack item = event.getItem();
			String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(item, PORTABLE_SHOP_PREFIX);
			if (unhidden != null) {
				String shop_file = unhidden.split(":")[2];
				boolean use_parent = Boolean.parseBoolean(unhidden.split(":")[3]);
				String entity_type = unhidden.split(":")[4];
				boolean enable_custom_name = Boolean.parseBoolean(unhidden.split(":")[5]);
				String custom_name = unhidden.split(":")[6];
				int duration = Integer.parseInt(unhidden.split(":")[7]);
				boolean is_baby = Boolean.parseBoolean(unhidden.split(":")[8]);

				if (EntityType.valueOf(entity_type) == null)
					return;

				if (duration < 3) {
					player.sendMessage("Duration can't be lower than 3 seconds!");
					return;
				}

				GameStore portable_shop = new GameStore(event.getClickedBlock().getLocation(), shop_file, use_parent,
						EntityType.valueOf(entity_type), custom_name, enable_custom_name, is_baby);

				LivingEntity entity = portable_shop.spawn();
				EntityUtils.disableEntityAI(entity);
				entity.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(XPWars.getInstance(), null));
				entity.setMetadata("portable-shop", new FixedMetadataValue(XPWars.getInstance(), null));

				new PortableShop(game, player, game.getTeamOfPlayer(player), entity.getLocation(), shop_file,
						custom_name, enable_custom_name, use_parent, entity, is_baby, duration);

				if (item.getAmount() > 1) {
					item.setAmount(item.getAmount() - 1);
				} else {
					player.getInventory().remove(item);
				}
				player.updateInventory();

				new BukkitRunnable() {
					@Override
					public void run() {
						if (entity.isDead() || entity == null)
							return;
						entity.remove();
					}
				}.runTaskLaterAsynchronously(XPWars.getInstance(), duration * 20);
			}
		}
	}

	private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
		return PORTABLE_SHOP_PREFIX
				+ SpecialItemUtils.getStringFromProperty("shop-file", XPWars.getConfigurator().config,
						"specials.portable-shop.shop-file", event)
				+ ":"
				+ SpecialItemUtils.getBooleanFromProperty("use-parent", XPWars.getConfigurator().config,
						"specials.portable-shop.use-parent", event)
				+ ":"
				+ SpecialItemUtils.getStringFromProperty(
						"entity-type", XPWars.getConfigurator().config, "specials.portable-shop.entity-type", event)
				+ ":"
				+ SpecialItemUtils.getBooleanFromProperty("enable-custom-name", XPWars.getConfigurator().config,
						"specials.portable-shop.enable-custom-name", event)
				+ ":"
				+ SpecialItemUtils.getStringFromProperty(
						"custom-name", XPWars.getConfigurator().config, "specials.portable-shop.custom-name", event)
				+ ":"
				+ SpecialItemUtils.getIntFromProperty("duration", XPWars.getConfigurator().config,
						"specials.portable-shop.duration", event)
				+ ":" + SpecialItemUtils.getBooleanFromProperty("baby", XPWars.getConfigurator().config,
						"specials.portable-shop.baby", event);
	}
}