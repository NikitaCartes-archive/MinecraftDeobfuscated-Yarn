/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.scoreboard;

import net.minecraft.world.PersistentState;

public class ScoreboardSynchronizer
implements Runnable {
    private final PersistentState compound;

    public ScoreboardSynchronizer(PersistentState persistentState) {
        this.compound = persistentState;
    }

    @Override
    public void run() {
        this.compound.markDirty();
    }
}

