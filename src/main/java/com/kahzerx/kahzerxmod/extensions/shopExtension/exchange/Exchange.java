package com.kahzerx.kahzerxmod.extensions.shopExtension.exchange;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;

public class Exchange {
    private static final HashMap<Item, Integer> VALID_EXCHANGES;
    static {
        VALID_EXCHANGES = Maps.newHashMap(ImmutableMap.of(
                Items.DIAMOND, 5,
                Items.DIAMOND_BLOCK, 50,
                Items.NETHERITE_INGOT, 50,
                Items.NETHERITE_BLOCK, 475,
                Items.NETHERITE_SCRAP, 10,
                Items.ANCIENT_DEBRIS, 10
        ));
    }

    public static boolean isValidItem(Item item) {
        return VALID_EXCHANGES.containsKey(item);
    }

    public static int getValue(Item item) {
        return VALID_EXCHANGES.get(item);
    }

    public static HashMap<Item, Integer> getValidExchanges() {
        return VALID_EXCHANGES;
    }
}
