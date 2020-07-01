/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.class_5425;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.gen.feature.StructureFeature;

public interface ServerWorldAccess
extends class_5425 {
    public long getSeed();

    public Stream<? extends StructureStart<?>> method_30275(ChunkSectionPos var1, StructureFeature<?> var2);
}

