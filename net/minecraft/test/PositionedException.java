/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import net.minecraft.test.GameTestException;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PositionedException
extends GameTestException {
    private final BlockPos pos;
    private final BlockPos relativePos;
    private final long tick;

    public PositionedException(String message, BlockPos pos, BlockPos relativePos, long tick) {
        super(message);
        this.pos = pos;
        this.relativePos = relativePos;
        this.tick = tick;
    }

    @Override
    public String getMessage() {
        String string = "" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";
        return super.getMessage() + " at " + string + " (t=" + this.tick + ")";
    }

    @Nullable
    public String getDebugMessage() {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPos getRelativePos() {
        return this.relativePos;
    }

    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }
}

