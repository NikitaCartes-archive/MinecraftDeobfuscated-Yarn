/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;

public interface StructureWorldAccess
extends ServerWorldAccess {
    public long getSeed();

    /**
     * {@return {@code true} if the given position is an accessible position
     * for the {@code setBlockState} function}
     */
    default public boolean isValidForSetBlock(BlockPos pos) {
        return true;
    }

    default public void setCurrentlyGeneratingStructureName(@Nullable Supplier<String> structureName) {
    }
}

