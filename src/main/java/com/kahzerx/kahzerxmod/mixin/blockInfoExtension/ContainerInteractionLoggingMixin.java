package com.kahzerx.kahzerxmod.mixin.blockInfoExtension;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootableContainerBlockEntity.class)
public abstract class ContainerInteractionLoggingMixin extends LockableContainerBlockEntity {
    protected ContainerInteractionLoggingMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void onSetItems(int slot, ItemStack stack, CallbackInfo ci) {
//        if (stack.getItem() != Items.AIR) {
//            System.out.println("setItems");
//            System.out.println(getPos());
//            System.out.println(getName().getString().toLowerCase());
//            System.out.println(stack);
//        }
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void onRemoveItems(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
//        ItemStack stack = cir.getReturnValue();
//        if (stack.getItem() != Items.AIR) {
//            System.out.println("removeItems");
//            System.out.println(getPos());
//            System.out.println(getName().getString().toLowerCase());
//            System.out.println(cir.getReturnValue());
//        }
    }
}
