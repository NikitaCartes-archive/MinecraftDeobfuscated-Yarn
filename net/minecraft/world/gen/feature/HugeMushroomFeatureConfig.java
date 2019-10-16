/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class HugeMushroomFeatureConfig
implements FeatureConfig {
    public final StateProvider capProvider;
    public final StateProvider stemProvider;
    public final int field_21232;

    public HugeMushroomFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, int i) {
        this.capProvider = stateProvider;
        this.stemProvider = stateProvider2;
        this.field_21232 = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("cap_provider"), this.capProvider.serialize(dynamicOps)).put(dynamicOps.createString("stem_provider"), this.stemProvider.serialize(dynamicOps)).put(dynamicOps.createString("foliage_radius"), dynamicOps.createInt(this.field_21232));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build()));
    }

    public static <T> HugeMushroomFeatureConfig method_23407(Dynamic<T> dynamic) {
        StateProviderType<T> stateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        StateProviderType<T> stateProviderType2 = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new HugeMushroomFeatureConfig((StateProvider)stateProviderType.deserialize(dynamic.get("cap_provider").orElseEmptyMap()), (StateProvider)stateProviderType2.deserialize(dynamic.get("stem_provider").orElseEmptyMap()), dynamic.get("foliage_radius").asInt(2));
    }
}

