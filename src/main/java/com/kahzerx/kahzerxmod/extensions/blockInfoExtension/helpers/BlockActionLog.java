package com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers;

public record BlockActionLog(String player, int amount, String block, int x, int y, int z, int dim, int actionType, String date) {
    public String getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }

    public String getBlock() {
        return block;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDim() {
        return dim;
    }

    public int getActionType() {
        return actionType;
    }

    public String getDate() {
        return date;
    }
}
