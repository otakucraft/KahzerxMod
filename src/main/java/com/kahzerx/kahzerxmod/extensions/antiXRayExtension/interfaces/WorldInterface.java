package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;

import java.util.concurrent.Executor;

public interface WorldInterface {
    void initValues(Executor executor);
    ChunkBlockController getChunkBlockController();
}
