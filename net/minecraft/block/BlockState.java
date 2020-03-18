/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockState
extends AbstractBlock.AbstractBlockState {
    public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(block, immutableMap);
    }

    @Override
    protected BlockState asBlockState() {
        return this;
    }

    public static <T> Dynamic<T> serialize(DynamicOps<T> ops, BlockState state) {
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
        Object object = immutableMap.isEmpty() ? ops.createMap(ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()))) : ops.createMap(ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()), ops.createString("Properties"), ops.createMap(immutableMap.entrySet().stream().map(entry -> Pair.of(ops.createString(((Property)entry.getKey()).getName()), ops.createString(State.nameValue((Property)entry.getKey(), (Comparable)entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        return new Dynamic<T>(ops, object);
    }

    public static <T> BlockState deserialize(Dynamic<T> dynamic2) {
        Block block = Registry.BLOCK.get(new Identifier(dynamic2.getElement("Name").flatMap(dynamic2.getOps()::getStringValue).orElse("minecraft:air")));
        Map<String, String> map = dynamic2.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        BlockState blockState = block.getDefaultState();
        StateManager<Block, BlockState> stateManager = block.getStateManager();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            Property<?> property = stateManager.getProperty(string);
            if (property == null) continue;
            blockState = State.tryRead(blockState, property, string, dynamic2.toString(), entry.getValue());
        }
        return blockState;
    }
}

