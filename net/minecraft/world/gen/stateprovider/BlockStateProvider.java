/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class BlockStateProvider
extends StateProvider {
    private final Block block;

    public BlockStateProvider(Block block) {
        super(StateProviderType.SIMPLE_STATE_PROVIDER);
        this.block = block;
    }

    public <T> BlockStateProvider(Dynamic<T> configDeserializer) {
        this(BlockState.deserialize(configDeserializer.get("state").orElseEmptyMap()).getBlock());
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        Direction.Axis axis = Direction.Axis.pickRandomAxis(random);
        return (BlockState)this.block.getDefaultState().with(PillarBlock.AXIS, axis);
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString())).put(ops.createString("state"), BlockState.serialize(ops, this.block.getDefaultState()).getValue());
        return new Dynamic<T>(ops, ops.createMap(builder.build())).getValue();
    }
}

