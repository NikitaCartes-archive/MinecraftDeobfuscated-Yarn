package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.registry.RegistryEntry;

/**
 * A feature config that specifies a starting pool and a size for {@linkplain
 * net.minecraft.structure.pool.StructurePoolBasedGenerator#generate}.
 */
public class StructurePoolFeatureConfig implements FeatureConfig {
	public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool),
					Codec.intRange(0, 7).fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)
				)
				.apply(instance, StructurePoolFeatureConfig::new)
	);
	private final RegistryEntry<StructurePool> startPool;
	private final int size;

	public StructurePoolFeatureConfig(RegistryEntry<StructurePool> registryEntry, int size) {
		this.startPool = registryEntry;
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public RegistryEntry<StructurePool> getStartPool() {
		return this.startPool;
	}
}
