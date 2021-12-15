package com.kahzerx.kahzerxmod.mixin.blockInfoExtension;

import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.*;
import com.kahzerx.kahzerxmod.utils.DateUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ContainerInteractionLoggingMixin {
    @Mixin(ScreenHandler.class)
    public static class ScreenHandlerMixin implements PlayerHandler {
        @Unique private PlayerEntity player = null;

        @Inject(method = "addSlot", at = @At("HEAD"))
        private void onAddSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
            ((SlotHandler) slot).setHandler((ScreenHandler) (Object) this);
        }

        @Inject(method = "onButtonClick", at = @At("HEAD"))
        private void onButtonClick(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
            this.player = player;
        }

        @Inject(method = "transferSlot", at = @At("HEAD"))
        private void onTransferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
            this.player =  player;
        }

        @Inject(method = "onSlotClick", at = @At("HEAD"))
        private void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
            this.player = player;
        }

        @Inject(method = "dropInventory", at = @At("HEAD"))
        private void onDrop(PlayerEntity player, Inventory inventory, CallbackInfo ci) {
            this.player = player;
        }

        @Override
        public PlayerEntity getPlayer() {
            return player;
        }
    }

    @Mixin(DoubleInventory.class)
    public static class DoubleInventoryMixin implements DoubleInventoryHelper {
        @Shadow @Final private Inventory first;

        @Shadow @Final private Inventory second;

        @Override
        public Inventory getInventory(int slot) {
            return slot >= this.first.size() ? this.second : first;
        }
    }

    @Mixin(Slot.class)
    public static abstract class SlotMixin implements SlotHandler {
        @Shadow public abstract ItemStack getStack();

        @Shadow @Final public Inventory inventory;

        @Shadow public abstract int getIndex();

        private ScreenHandler handler = null;
        private ItemStack oldStack = null;

        @Override
        public ScreenHandler getHandler() {
            return handler;
        }

        @Override
        public void setHandler(ScreenHandler handler) {
            this.handler = handler;
            this.oldStack = this.getStack() == null ? ItemStack.EMPTY : this.getStack().copy();
        }

        @Inject(method = "markDirty", at = @At("HEAD"))
        private void logChanges(CallbackInfo ci) {
            LoggedBE be = getBE();
            PlayerHandler playerHandler = (PlayerHandler) handler;

            if (be != null && playerHandler.getPlayer() != null) {
                log(playerHandler.getPlayer(), oldStack, this.getStack().copy(), be);
            }

            oldStack = this.getStack().copy();
        }

        private LoggedBE getBE() {
            Inventory inv = this.inventory;
            if (inv instanceof DoubleInventoryHelper) {
                inv = ((DoubleInventoryHelper) inv).getInventory(this.getIndex());
            }
            if (inv instanceof LocationalInventory) {
                return new LoggedBE(((LocationalInventory) inv).getLoc(), ((LocationalInventory) inv).getInvWorld());
            }
            return null;
        }

        private void log(PlayerEntity player, ItemStack stack, ItemStack newStack, LoggedBE loggedBE) {
            if (stack.isEmpty() && newStack.isEmpty()) {
                return;
            }
            if (!stack.isEmpty() && !newStack.isEmpty()) {
                if (stack.getItem() == newStack.getItem()) {
                    int oCnt = stack.getCount();
                    int nCnt = newStack.getCount();
                    if (nCnt > oCnt) {
                        log(player, ItemStack.EMPTY, new ItemStack(newStack.getItem(), nCnt - oCnt), loggedBE);
                    } else {
                        log(player, new ItemStack(newStack.getItem(), oCnt - nCnt), ItemStack.EMPTY, loggedBE);
                    }
                } else {
                    log(player, stack, ItemStack.EMPTY, loggedBE);
                    log(player, ItemStack.EMPTY, newStack, loggedBE);
                }
                return;
            }
            boolean oldEmpty = stack.isEmpty();
            ItemStack updatedStack = oldEmpty ? newStack : stack;
            BlockInfoExtension.enqueue(new BlockActionLog(
                    player.getName().getString(),
                    updatedStack.getCount(),
                    updatedStack.getItem().getName().getString(),
                    loggedBE.getPos().getX(),
                    loggedBE.getPos().getY(),
                    loggedBE.getPos().getZ(),
                    DimUtils.getWorldID(DimUtils.getDim(loggedBE.getWorld())),
                    oldEmpty ? 3 : 4,
                    DateUtils.getDate()
            ));
        }
    }

    @Mixin(LockableContainerBlockEntity.class)
    public static class LockableContainerBlockEntityMixin extends BlockEntity implements LocationalInventory {
        public LockableContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public BlockPos getLoc() {
            return this.getPos();
        }

        @Override
        public World getInvWorld() {
            return this.getWorld();
        }
    }
}
