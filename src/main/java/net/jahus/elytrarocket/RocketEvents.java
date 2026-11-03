package net.jahus.elytrarocket;

import net.jahus.elytrarocket.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge EventBus 7: cancellable listeners return {@code boolean}
 * ({@code true} = cancel). No {@code setCanceled}.
 */
@Mod.EventBusSubscriber(modid = ElytraRocketControl.MOD_ID)
public final class RocketEvents {

	private RocketEvents() {}

	@SubscribeEvent
	public static boolean onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!(event.getItemStack().getItem() instanceof FireworkRocketItem)) {
			return false;
		}

		Player player = event.getEntity();
		if (!ModConfig.shouldCancel(player.isFallFlying(), player.isInWater())) {
			return false;
		}

		if (!event.getLevel().isClientSide()) {
			String msg = ModConfig.message();
			if (!msg.isEmpty()) {
				player.sendOverlayMessage(Component.literal(msg));
			}
		}

		return true; // cancel use
	}
}
