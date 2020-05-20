/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

/**
 * A feature config that specifies a starting pool and a size for the first two parameters of
 * {@link net.minecraft.structure.pool.StructurePoolBasedGenerator#addPieces(net.minecraft.util.Identifier, int, net.minecraft.structure.pool.StructurePoolBasedGenerator.PieceFactory, net.minecraft.world.gen.chunk.ChunkGenerator, net.minecraft.structure.StructureManager, net.minecraft.util.math.BlockPos, java.util.List, java.util.Random, boolean, boolean)}.
 */
public class StructurePoolFeatureConfig
implements FeatureConfig {
    public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.field_25139.fieldOf("start_pool")).forGetter(StructurePoolFeatureConfig::getStartPool), ((MapCodec)Codec.INT.fieldOf("size")).forGetter(StructurePoolFeatureConfig::getSize)).apply((Applicative<StructurePoolFeatureConfig, ?>)instance, StructurePoolFeatureConfig::new));
    public final Identifier startPool;
    public final int size;

    public StructurePoolFeatureConfig(Identifier identifier, int size) {
        this.startPool = identifier;
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public Identifier getStartPool() {
        return this.startPool;
    }
}

