package com.kahzerx.kahzerxmod.mixin.scoreboardExtension;


import com.kahzerx.kahzerxmod.extensions.scoreboardExtension.ScoreboardExtension;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySummonArgumentType.class)
public class EntitySummonMixin {
    @Inject(method = "validate", at = @At(value = "HEAD"), cancellable = true)
    private static void onValidate(Identifier id, CallbackInfoReturnable<Identifier> cir) throws CommandSyntaxException {
        if (ScoreboardExtension.isExtensionEnabled) {
            Registry.ENTITY_TYPE.getOrEmpty(id).filter(entityType -> entityType.isSummonable() || entityType == EntityType.PLAYER).orElseThrow(() -> new DynamicCommandExceptionType(id_ -> Text.translatable("entity.notFound", id_)).create(id));
            cir.setReturnValue(id);
        }
    }
}