package net.jahus.elytrarocket.mixin;

import net.jahus.elytrarocket.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin {

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void elytrarocket$cancelUse(
			Level level,
			Player player,
			InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir
	) {
		boolean fallFlying = player.isFallFlying();
		boolean inWater = player.isInWater();

		if (!ModConfig.shouldCancel(fallFlying, inWater)) {
			return;
		}

		if (!level.isClientSide()) {
			String msg = ModConfig.message();
			if (!msg.isEmpty()) {
				player.sendOverlayMessage(Component.literal(msg));
			}
		}

		cir.setReturnValue(InteractionResult.FAIL);
	}
}
