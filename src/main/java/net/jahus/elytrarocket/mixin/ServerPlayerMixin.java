package net.jahus.elytrarocket.mixin;

import net.jahus.elytrarocket.debug.SpeedMonitor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void elytrarocket$monitor(CallbackInfo ci) {
        SpeedMonitor.tick((ServerPlayer) (Object) this);
    }
}
