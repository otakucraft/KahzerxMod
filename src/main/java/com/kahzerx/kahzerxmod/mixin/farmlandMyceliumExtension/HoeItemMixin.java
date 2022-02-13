package com.kahzerx.kahzerxmod.mixin.farmlandMyceliumExtension;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.kahzerx.kahzerxmod.extensions.farmlandMyceliumExtension.FarmlandMyceliumExtension;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.item.HoeItem.createTillAction;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin extends MiningToolItem {
    protected HoeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, Tag<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }
    private static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> ACTIONS;
    static {
        ACTIONS = Maps.newHashMap(ImmutableMap.of(Blocks.MYCELIUM, Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState()))));
    }

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onGet(Map instance, Object o) {
        Block b = (Block) o;
        if (b == Blocks.MYCELIUM && FarmlandMyceliumExtension.isExtensionEnabled) {
            return ACTIONS.get(o);
        }
        return instance.get(o);
    }
}
