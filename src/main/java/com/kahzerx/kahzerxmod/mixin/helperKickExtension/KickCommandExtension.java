package com.kahzerx.kahzerxmod.mixin.helperKickExtension;

import com.kahzerx.kahzerxmod.extensions.helperKickExtension.HelperKickExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.KickCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(KickCommand.class)
public class KickCommandExtension {
    @Redirect(method = "method_13413", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"))
    private static boolean permissionLevel(ServerCommandSource source, int level) {
        try {
            return source.hasPermissionLevel(level) || (
                    HelperKickExtension.isExtensionEnabled &&
                            HelperKickExtension.permsExtension != null &&
                            HelperKickExtension.permsExtension.extensionSettings().isEnabled() &&
                            HelperKickExtension.permsExtension.getDBPlayerPerms(source.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId()
            );
        } catch (CommandSyntaxException ignored) { }
        return false;
    }
}
