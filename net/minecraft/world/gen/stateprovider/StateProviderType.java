/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.ForestFlowerStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleStateProvider;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.WeightedStateProvider;

public class StateProviderType<P extends StateProvider> {
    public static final StateProviderType<SimpleStateProvider> SIMPLE_STATE_PROVIDER = StateProviderType.register("simple_state_provider", SimpleStateProvider::new);
    public static final StateProviderType<WeightedStateProvider> WEIGHTED_STATE_PROVIDER = StateProviderType.register("weighted_state_provider", WeightedStateProvider::new);
    public static final StateProviderType<PlainsFlowerStateProvider> PLAIN_FLOWER_PROVIDER = StateProviderType.register("plain_flower_provider", PlainsFlowerStateProvider::new);
    public static final StateProviderType<ForestFlowerStateProvider> FOREST_FLOWER_PROVIDER = StateProviderType.register("forest_flower_provider", ForestFlowerStateProvider::new);
    private final Function<Dynamic<?>, P> configDeserializer;

    private static <P extends StateProvider> StateProviderType<P> register(String id, Function<Dynamic<?>, P> configDeserializer) {
        return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, id, new StateProviderType<P>(configDeserializer));
    }

    private StateProviderType(Function<Dynamic<?>, P> configDeserializer) {
        this.configDeserializer = configDeserializer;
    }

    public P deserialize(Dynamic<?> dynamic) {
        return (P)((StateProvider)this.configDeserializer.apply(dynamic));
    }
}

