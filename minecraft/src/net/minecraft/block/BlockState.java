package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockState extends AbstractBlock.AbstractBlockState {
	public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		super(block, immutableMap);
	}

	@Override
	protected BlockState asBlockState() {
		return this;
	}

	public static <T> Dynamic<T> serialize(DynamicOps<T> ops, BlockState state) {
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
		T object;
		if (immutableMap.isEmpty()) {
			object = ops.createMap(ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.BLOCK.getId(state.getBlock()).toString())));
		} else {
			object = ops.createMap(
				ImmutableMap.of(
					ops.createString("Name"),
					ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()),
					ops.createString("Properties"),
					ops.createMap(
						(Map<T, T>)immutableMap.entrySet()
							.stream()
							.map(
								entry -> Pair.of(
										ops.createString(((Property)entry.getKey()).getName()),
										ops.createString(State.nameValue((Property<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
									)
							)
							.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
					)
				)
			);
		}

		return new Dynamic<>(ops, object);
	}

	public static <T> BlockState deserialize(Dynamic<T> dynamic) {
		Block block = Registry.BLOCK.get(new Identifier((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
		Map<String, String> map = dynamic.get("Properties").asMap(dynamicx -> dynamicx.asString(""), dynamicx -> dynamicx.asString(""));
		BlockState blockState = block.getDefaultState();
		StateManager<Block, BlockState> stateManager = block.getStateManager();

		for (Entry<String, String> entry : map.entrySet()) {
			String string = (String)entry.getKey();
			Property<?> property = stateManager.getProperty(string);
			if (property != null) {
				blockState = State.tryRead(blockState, property, string, dynamic.toString(), (String)entry.getValue());
			}
		}

		return blockState;
	}
}
