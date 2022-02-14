package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.shapes;

public class SimpleRect {
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    public SimpleRect(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void set(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void include(int x, int y) {
        this.minX = Math.min(x, this.minX);
        this.minY = Math.min(y, this.minY);
        this.maxX = Math.max(x + 1, this.maxX);
        this.maxY = Math.max(y + 1, this.maxY);
    }

    public boolean has(int x, int y) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

    public int getSize() {
        return (maxX - minX) * (maxY - minY);
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getHeight() {
        return maxY - minY;
    }
}
