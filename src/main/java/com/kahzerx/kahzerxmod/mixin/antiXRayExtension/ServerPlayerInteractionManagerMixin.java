package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerWorld world;

    @Inject(method = "processBlockBreakingAction", at = @At("TAIL"))
    public void onBreakBlock(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci) {
        ChunkBlockController.getBlockController(this.world).onPlayerLeftClickBlock(this.world, (ServerPlayerInteractionManager) (Object) this, pos, action, direction, worldHeight);
    }
}
