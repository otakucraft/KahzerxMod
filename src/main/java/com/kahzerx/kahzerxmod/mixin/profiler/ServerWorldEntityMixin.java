package com.kahzerx.kahzerxmod.mixin.profiler;

import com.kahzerx.kahzerxmod.profiler.EntityProfiler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class ServerWorldEntityMixin {
    @Redirect(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    private void onTick(Entity instance) {
        instance.tick();
        EntityProfiler.addEntity(instance, (ServerWorld) (Object) this);
    }
}
