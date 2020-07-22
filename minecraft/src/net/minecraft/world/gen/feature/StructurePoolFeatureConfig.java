package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.structure.pool.StructurePool;

/**
 * A feature config that specifies a starting pool and a size for the first two parameters of
 * {@link net.minecraft.structure.pool.StructurePoolBasedGenerator#addPieces(net.minecraft.util.Identifier, int, net.minecraft.structure.pool.StructurePoolBasedGenerator.PieceFactory, net.minecraft.world.gen.chunk.ChunkGenerator, net.minecraft.structure.StructureManager, net.minecraft.util.math.BlockPos, java.util.List, java.util.Random, boolean, boolean)}.
 */
public class StructurePoolFeatureConfig implements FeatureConfig {
	public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool),
					Codec.intRange(0, 7).fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)
				)
				.apply(instance, StructurePoolFeatureConfig::new)
	);
	private final Supplier<StructurePool> startPool;
	private final int size;

	public StructurePoolFeatureConfig(Supplier<StructurePool> supplier, int size) {
		this.startPool = supplier;
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public Supplier<StructurePool> getStartPool() {
		return this.startPool;
	}
}
