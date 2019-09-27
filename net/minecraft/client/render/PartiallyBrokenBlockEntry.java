/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class PartiallyBrokenBlockEntry
implements Comparable<PartiallyBrokenBlockEntry> {
    private final int breakingEntityId;
    private final BlockPos pos;
    private int stage;
    private int lastUpdateTicks;

    public PartiallyBrokenBlockEntry(int i, BlockPos blockPos) {
        this.breakingEntityId = i;
        this.pos = blockPos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setStage(int i) {
        if (i > 10) {
            i = 10;
        }
        this.stage = i;
    }

    public int getStage() {
        return this.stage;
    }

    public void setLastUpdateTicks(int i) {
        this.lastUpdateTicks = i;
    }

    public int getLastUpdateTicks() {
        return this.lastUpdateTicks;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)object;
        return this.breakingEntityId == partiallyBrokenBlockEntry.breakingEntityId;
    }

    public int hashCode() {
        return Integer.hashCode(this.breakingEntityId);
    }

    public int method_23269(PartiallyBrokenBlockEntry partiallyBrokenBlockEntry) {
        if (this.stage != partiallyBrokenBlockEntry.stage) {
            return Integer.compare(this.stage, partiallyBrokenBlockEntry.stage);
        }
        return Integer.compare(this.breakingEntityId, partiallyBrokenBlockEntry.breakingEntityId);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.method_23269((PartiallyBrokenBlockEntry)object);
    }
}

