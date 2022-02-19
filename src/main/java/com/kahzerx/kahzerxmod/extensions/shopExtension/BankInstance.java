package com.kahzerx.kahzerxmod.extensions.shopExtension;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;

public class BankInstance {
    private int coins;
    private final Exchanges exchanges;
    public BankInstance(int coins, Exchanges exchanges) {
        this.coins = coins;
        this.exchanges = exchanges;
    }

    public Exchanges getExchanges() {
        return exchanges;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public static class Exchanges {
        private final HashMap<Item, Integer> exchangedItems;
        public Exchanges() {
            this.exchangedItems = Maps.newHashMap(ImmutableMap.of(
                    Items.DIAMOND, 0,
                    Items.DIAMOND_BLOCK, 0,
                    Items.NETHERITE_INGOT, 0,
                    Items.NETHERITE_BLOCK, 0,
                    Items.NETHERITE_SCRAP, 0,
                    Items.ANCIENT_DEBRIS, 0
            ));
        }

        public int getDiamond() {
            return exchangedItems.get(Items.DIAMOND);
        }

        public int getDiamondBlock() {
            return exchangedItems.get(Items.DIAMOND_BLOCK);
        }

        public int getNetheriteIngot() {
            return exchangedItems.get(Items.NETHERITE_INGOT);
        }

        public int getNetheriteBlock() {
            return exchangedItems.get(Items.NETHERITE_BLOCK);
        }

        public int getNetheriteScrap() {
            return exchangedItems.get(Items.NETHERITE_SCRAP);
        }

        public int getDebris() {
            return exchangedItems.get(Items.ANCIENT_DEBRIS);
        }

        public void setFromItem(Item item, int amount) {
            exchangedItems.put(item, amount);
        }

        public void setDiamond(int amount) {
            exchangedItems.put(Items.DIAMOND, amount);
        }

        public void setDiamondBlock(int amount) {
            exchangedItems.put(Items.DIAMOND_BLOCK, amount);
        }

        public void setNetheriteIngot(int amount) {
            exchangedItems.put(Items.NETHERITE_INGOT, amount);
        }

        public void setNetheriteBlock(int amount) {
            exchangedItems.put(Items.NETHERITE_BLOCK, amount);
        }

        public void setNetheriteScrap(int amount) {
            exchangedItems.put(Items.NETHERITE_SCRAP, amount);
        }

        public void setDebris(int amount) {
            exchangedItems.put(Items.ANCIENT_DEBRIS, amount);
        }
    }
}
