package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

/**
 * A feature config that specifies a starting pool and a size for the first two parameters of
 * {@link net.minecraft.structure.pool.StructurePoolBasedGenerator#addPieces(net.minecraft.util.Identifier, int, net.minecraft.structure.pool.StructurePoolBasedGenerator.PieceFactory, net.minecraft.world.gen.chunk.ChunkGenerator, net.minecraft.structure.StructureManager, net.minecraft.util.math.BlockPos, java.util.List, java.util.Random, boolean, boolean)}.
 */
public class StructurePoolFeatureConfig implements FeatureConfig {
	public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool),
					Codec.INT.fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)
				)
				.apply(instance, StructurePoolFeatureConfig::new)
	);
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
