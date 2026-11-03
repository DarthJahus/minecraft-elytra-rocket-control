package net.jahus.elytrarocket;

import net.jahus.elytrarocket.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ElytraRocketControl.MOD_ID)
public final class RocketEvents {

	private RocketEvents() {}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!(event.getItemStack().getItem() instanceof FireworkRocketItem)) {
			return;
		}

		Player player = event.getEntity();
		if (!ModConfig.shouldCancel(player.isFallFlying(), player.isInWater())) {
			return;
		}

		event.setCanceled(true);

		if (!event.getLevel().isClientSide()) {
			String msg = ModConfig.message();
			if (!msg.isEmpty()) {
				player.sendOverlayMessage(Component.literal(msg));
			}
		}
	}
}
