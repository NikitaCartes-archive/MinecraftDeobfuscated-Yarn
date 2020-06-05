package net.minecraft.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.DimensionType;

public interface RegistryTracker {
	<E> Optional<MutableRegistry<E>> get(RegistryKey<Registry<E>> reference);

	@Environment(EnvType.CLIENT)
	Registry<DimensionType> getDimensionTypeRegistry();

	static RegistryTracker.Modifiable create() {
		return DimensionType.addRegistryDefaults(new RegistryTracker.Modifiable());
	}

	public static final class Modifiable implements RegistryTracker {
		public static final Codec<RegistryTracker.Modifiable> CODEC = SimpleRegistry.method_29098(
				Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.CODEC
			)
			.<RegistryTracker.Modifiable>xmap(RegistryTracker.Modifiable::new, modifiable -> modifiable.registry)
			.fieldOf("dimension")
			.codec();
		private final SimpleRegistry<DimensionType> registry;

		public Modifiable() {
			this(new SimpleRegistry<>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental()));
		}

		private Modifiable(SimpleRegistry<DimensionType> registry) {
			this.registry = registry;
		}

		public void addDimensionType(RegistryKey<DimensionType> registryKey, DimensionType dimensionType) {
			this.registry.add(registryKey, dimensionType);
		}

		@Override
		public <E> Optional<MutableRegistry<E>> get(RegistryKey<Registry<E>> reference) {
			return Objects.equals(reference, Registry.DIMENSION_TYPE_KEY) ? Optional.of(this.registry) : Optional.empty();
		}

		@Override
		public Registry<DimensionType> getDimensionTypeRegistry() {
			return this.registry;
		}
	}
}
