/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionType;

public interface RegistryTracker {
    public <E> Optional<MutableRegistry<E>> get(RegistryKey<Registry<E>> var1);

    @Environment(value=EnvType.CLIENT)
    public Registry<DimensionType> getDimensionTypeRegistry();

    public static Modifiable create() {
        return DimensionType.addRegistryDefaults(new Modifiable());
    }

    public static final class Modifiable
    implements RegistryTracker {
        public static final Codec<Modifiable> CODEC = ((MapCodec)SimpleRegistry.method_29098(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.CODEC).xmap(Modifiable::new, modifiable -> modifiable.registry).fieldOf("dimension")).codec();
        private final SimpleRegistry<DimensionType> registry;

        public Modifiable() {
            this(new SimpleRegistry<DimensionType>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental()));
        }

        private Modifiable(SimpleRegistry<DimensionType> registry) {
            this.registry = registry;
        }

        public void addDimensionType(RegistryKey<DimensionType> registryKey, DimensionType dimensionType) {
            this.registry.add(registryKey, dimensionType);
        }

        @Override
        public <E> Optional<MutableRegistry<E>> get(RegistryKey<Registry<E>> reference) {
            if (Objects.equals(reference, Registry.DIMENSION_TYPE_KEY)) {
                return Optional.of(this.registry);
            }
            return Optional.empty();
        }

        @Override
        public Registry<DimensionType> getDimensionTypeRegistry() {
            return this.registry;
        }
    }
}

