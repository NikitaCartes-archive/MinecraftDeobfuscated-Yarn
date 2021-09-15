/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public interface StructureWorldAccess
extends ServerWorldAccess {
    public long getSeed();

    public Stream<? extends StructureStart<?>> getStructures(ChunkSectionPos var1, StructureFeature<?> var2);

    /**
     * {@return {@code true} if the given position is an accessible position
     * for the {@code setBlockState} function}
     */
    default public boolean isValidForSetBlock(BlockPos pos) {
        return true;
    }

    default public void method_36972(@Nullable Supplier<String> supplier) {
    }
}

