package net.jahus.elytrarocket.mixin;

import net.jahus.elytrarocket.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Scales (or removes) the elytra flight boost applied by an attached firework rocket.
 */
@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {

	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
			)
	)
	private void elytrarocket$scaleBoost(LivingEntity entity, Vec3 postBoostVelocity) {
		if (!ModConfig.enabled() || !entity.isFallFlying()) {
			entity.setDeltaMovement(postBoostVelocity);
			return;
		}

		double mult = ModConfig.boostMultiplier();

		if (mult >= 1.0D) {
			entity.setDeltaMovement(postBoostVelocity);
			return;
		}

		if (mult <= 0.0D) {
			// Keep current velocity — no rocket boost.
			return;
		}

		Vec3 pre = entity.getDeltaMovement();

		// Blend from pre-boost to full vanilla post-boost velocity.
		entity.setDeltaMovement(pre.add(postBoostVelocity.subtract(pre).scale(mult)));
	}
}
