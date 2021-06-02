/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureWorldAccess
extends ServerWorldAccess {
    public long getSeed();

    public Stream<? extends StructureStart<?>> getStructures(ChunkSectionPos var1, StructureFeature<?> var2);

    default public boolean method_37368(BlockPos blockPos) {
        return true;
    }
}

