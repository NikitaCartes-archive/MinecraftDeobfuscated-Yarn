/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public interface StructurePlacement {
    public static final Codec<StructurePlacement> TYPE_CODEC = Registry.STRUCTURE_PLACEMENT.getCodec().dispatch(StructurePlacement::getType, StructurePlacementType::codec);

    public boolean isStartChunk(ChunkGenerator var1, long var2, int var4, int var5);

    public StructurePlacementType<?> getType();
}

