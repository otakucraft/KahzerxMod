package com.kahzerx.kahzerxmod.mixin.seedExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SeedCommand.class)
public class seedCommandMixin {
    @Inject(method = "register", at = @At(value = "HEAD"), cancellable = true)
    private static void onRegisterCommand(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated, CallbackInfo ci) {
        ci.cancel();
    }
}
