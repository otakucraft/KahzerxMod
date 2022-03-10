package com.kahzerx.kahzerxmod.extensions.blockInfoExtension;

import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers.BlockActionLog;

public class BlockInfoLoggerThread extends Thread {
    private boolean running = true;
    private final BlockInfoExtension extension;
    private boolean shouldClear = false;

    public BlockInfoLoggerThread(String name, BlockInfoExtension extension) {
        super(name);
        this.extension = extension;
    }

    @Override
    public void run() {
        while (this.running) {
            if (shouldClear) {
                extension.db.getQuery().clearLogTable();
                shouldClear = false;
            }
            if (BlockInfoExtension.queue.isEmpty()) {
                continue;
            }
            try {
                BlockActionLog action = BlockInfoExtension.queue.take();
                if (this.extension.getSettings().isEnabled()) {
                    extension.db.getQuery().logAction(action);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopLogger() {
        this.running = false;
    }

    public void setShouldClear(boolean shouldClear) {
        this.shouldClear = shouldClear;
    }
}
