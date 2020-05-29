package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public interface DimensionTracker {
	@Environment(EnvType.CLIENT)
	Registry<DimensionType> getRegistry();

	@Environment(EnvType.CLIENT)
	static DimensionTracker.Modifiable create() {
		return DimensionType.addDefaults(new DimensionTracker.Modifiable());
	}

	public static final class Modifiable implements DimensionTracker {
		public static final Codec<DimensionTracker.Modifiable> CODEC = SimpleRegistry.method_29098(
				Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.field_24756
			)
			.<DimensionTracker.Modifiable>xmap(DimensionTracker.Modifiable::new, modifiable -> modifiable.registry)
			.fieldOf("dimension")
			.codec();
		private final SimpleRegistry<DimensionType> registry;

		public Modifiable() {
			this(new SimpleRegistry<>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental()));
		}

		private Modifiable(SimpleRegistry<DimensionType> registry) {
			this.registry = registry;
		}

		public void add(RegistryKey<DimensionType> registryKey, DimensionType dimensionType) {
			this.registry.add(registryKey, dimensionType);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Registry<DimensionType> getRegistry() {
			return this.registry;
		}
	}
}
