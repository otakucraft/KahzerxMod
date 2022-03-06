package com.kahzerx.kahzerxmod.mixin.itemFormattedExtension;

import com.kahzerx.kahzerxmod.extensions.itemFormattedExtension.ItemFormattedExtension;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCustomName(Lnet/minecraft/text/Text;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack onSetName(ItemStack instance, Text name) {
        if (ItemFormattedExtension.isExtensionEnabled) {
            return instance.setCustomName(new LiteralText(name.getString().replace("%", "§")));
        }
        return instance.setCustomName(name);
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private ItemStack onSet(Inventory instance, int i) {
        ItemStack itemStack = instance.getStack(0);
        if (ItemFormattedExtension.isExtensionEnabled) {
            return itemStack.setCustomName(new LiteralText(itemStack.getName().getString().replace("§", "%")));
        }
        return itemStack;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String onCheck(Text instance) {
        if (ItemFormattedExtension.isExtensionEnabled) {
            return instance.getString().replace("%", "§");
        }
        return instance.getString();
    }
}
