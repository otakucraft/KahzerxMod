package com.kahzerx.kahzerxmod.mixin.endermanNoGriefExtension;

import com.kahzerx.kahzerxmod.extensions.endermanNoGriefExtension.EndermanNoGriefExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal", "net.minecraft.entity.mob.EndermanEntity$PlaceBlockGoal"})
public class EndermanEntityMixin {
    @Inject(method = "canStart", at = @At("HEAD"))
    public void onEndermanAction(CallbackInfoReturnable<Boolean> cir) {
        if (EndermanNoGriefExtension.isExtensionEnabled) {
            cir.setReturnValue(false);
        }
    }
}
