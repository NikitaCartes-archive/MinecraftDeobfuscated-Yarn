package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public interface DimensionTracker {
	<E> Optional<MutableRegistry<E>> method_29726(RegistryKey<Registry<E>> registryKey);

	@Environment(EnvType.CLIENT)
	Registry<DimensionType> getRegistry();

	static DimensionTracker.Modifiable create() {
		return DimensionType.addDefaults(new DimensionTracker.Modifiable());
	}

	public static final class Modifiable implements DimensionTracker {
		public static final Codec<DimensionTracker.Modifiable> CODEC = SimpleRegistry.method_29098(
				Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.CODEC
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

		@Override
		public <E> Optional<MutableRegistry<E>> method_29726(RegistryKey<Registry<E>> registryKey) {
			return Objects.equals(registryKey, Registry.DIMENSION_TYPE_KEY) ? Optional.of(this.registry) : Optional.empty();
		}

		@Override
		public Registry<DimensionType> getRegistry() {
			return this.registry;
		}
	}
}
