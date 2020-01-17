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
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class BlockPileFeatureConfig
implements FeatureConfig {
    public final BlockStateProvider stateProvider;

    public BlockPileFeatureConfig(BlockStateProvider blockStateProvider) {
        this.stateProvider = blockStateProvider;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("state_provider"), this.stateProvider.serialize(ops));
        return new Dynamic<T>(ops, ops.createMap(builder.build()));
    }

    public static <T> BlockPileFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockStateProviderType<T> blockStateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new BlockPileFeatureConfig((BlockStateProvider)blockStateProviderType.deserialize(dynamic.get("state_provider").orElseEmptyMap()));
    }
}

