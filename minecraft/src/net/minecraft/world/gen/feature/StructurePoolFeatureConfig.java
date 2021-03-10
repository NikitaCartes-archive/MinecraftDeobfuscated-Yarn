package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.structure.pool.StructurePool;

/**
 * A feature config that specifies a starting pool and a size for {@linkplain
 * net.minecraft.structure.pool.StructurePoolBasedGenerator#method_30419
 * method_30419}.
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

	public StructurePoolFeatureConfig(Supplier<StructurePool> startPool, int size) {
		this.startPool = startPool;
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public Supplier<StructurePool> getStartPool() {
		return this.startPool;
	}
}
