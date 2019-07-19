/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BlockBreakingInfo {
    private final int actorNetworkId;
    private final BlockPos pos;
    private int stage;
    private int lastUpdateTick;

    public BlockBreakingInfo(int i, BlockPos blockPos) {
        this.actorNetworkId = i;
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

    public void setLastUpdateTick(int i) {
        this.lastUpdateTick = i;
    }

    public int getLastUpdateTick() {
        return this.lastUpdateTick;
    }
}

