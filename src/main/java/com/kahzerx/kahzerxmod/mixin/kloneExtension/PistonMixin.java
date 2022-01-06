package com.kahzerx.kahzerxmod.mixin.kloneExtension;

import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlockEntity.class)
public abstract class PistonMixin {
    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
    private static PistonBehavior moveKlones(Entity instance, World world, BlockPos pos, float f, PistonBlockEntity blockEntity) {
        if (instance instanceof KlonePlayerEntity && blockEntity.getPushedBlock().isOf(Blocks.SLIME_BLOCK)) {
            Vec3d vec3d = instance.getVelocity();
            double x = vec3d.x;
            double y = vec3d.y;
            double z = vec3d.z;
            Direction direction = blockEntity.getMovementDirection();
            switch (direction.getAxis()) {
                case X -> x = direction.getOffsetX();
                case Y -> y = direction.getOffsetY();
                case Z -> z = direction.getOffsetZ();
            }
            instance.setVelocity(x, y, z);
        }
        return instance.getPistonBehavior();
    }
}
