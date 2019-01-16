package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameter;
import net.minecraft.world.loot.context.Parameters;

public class BlockStatePropertyLootCondition implements LootCondition {
	private final Block block;
	private final Map<Property<?>, Object> properties;
	private final Predicate<BlockState> predicate;

	private BlockStatePropertyLootCondition(Block block, Map<Property<?>, Object> map) {
		this.block = block;
		this.properties = ImmutableMap.copyOf(map);
		this.predicate = method_898(block, map);
	}

	private static Predicate<BlockState> method_898(Block block, Map<Property<?>, Object> map) {
		int i = map.size();
		if (i == 0) {
			return blockState -> blockState.getBlock() == block;
		} else if (i == 1) {
			Entry<Property<?>, Object> entry = (Entry<Property<?>, Object>)map.entrySet().iterator().next();
			Property<?> property = (Property<?>)entry.getKey();
			Object object = entry.getValue();
			return blockState -> blockState.getBlock() == block && object.equals(blockState.get(property));
		} else {
			Predicate<BlockState> predicate = blockState -> blockState.getBlock() == block;

			for (Entry<Property<?>, Object> entry2 : map.entrySet()) {
				Property<?> property2 = (Property<?>)entry2.getKey();
				Object object2 = entry2.getValue();
				predicate = predicate.and(blockState -> object2.equals(blockState.get(property2)));
			}

			return predicate;
		}
	}

	@Override
	public Set<Parameter<?>> getRequiredParameters() {
		return ImmutableSet.of(Parameters.field_1224);
	}

	public boolean method_899(LootContext lootContext) {
		BlockState blockState = lootContext.get(Parameters.field_1224);
		return blockState != null && this.predicate.test(blockState);
	}

	public static BlockStatePropertyLootCondition.class_213 method_900(Block block) {
		return new BlockStatePropertyLootCondition.class_213(block);
	}

	public static class Factory extends LootCondition.Factory<BlockStatePropertyLootCondition> {
		private static <T extends Comparable<T>> String method_908(Property<T> property, Object object) {
			return property.getValueAsString((T)object);
		}

		protected Factory() {
			super(new Identifier("block_state_property"), BlockStatePropertyLootCondition.class);
		}

		public void method_909(
			JsonObject jsonObject, BlockStatePropertyLootCondition blockStatePropertyLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.addProperty("block", Registry.BLOCK.getId(blockStatePropertyLootCondition.block).toString());
			JsonObject jsonObject2 = new JsonObject();
			blockStatePropertyLootCondition.properties.forEach((property, object) -> jsonObject2.addProperty(property.getName(), method_908(property, object)));
			jsonObject.add("properties", jsonObject2);
		}

		public BlockStatePropertyLootCondition method_910(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			Block block = Registry.BLOCK.get(identifier);
			if (block == null) {
				throw new IllegalArgumentException("Can't find block " + identifier);
			} else {
				StateFactory<Block, BlockState> stateFactory = block.getStateFactory();
				Map<Property<?>, Object> map = Maps.<Property<?>, Object>newHashMap();
				if (jsonObject.has("properties")) {
					JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "properties");
					jsonObject2.entrySet()
						.forEach(
							entry -> {
								String string = (String)entry.getKey();
								Property<?> property = stateFactory.getProperty(string);
								if (property == null) {
									throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(block) + " does not have property '" + string + "'");
								} else {
									String string2 = JsonHelper.asString((JsonElement)entry.getValue(), "value");
									Object object = property.getValue(string2)
										.orElseThrow(
											() -> new IllegalArgumentException("Block " + Registry.BLOCK.getId(block) + " property '" + string + "' does not have value '" + string2 + "'")
										);
									map.put(property, object);
								}
							}
						);
				}

				return new BlockStatePropertyLootCondition(block, map);
			}
		}
	}

	public static class class_213 implements LootCondition.Builder {
		private final Block field_1290;
		private final Set<Property<?>> field_1289;
		private final Map<Property<?>, Object> field_1291 = Maps.<Property<?>, Object>newHashMap();

		public class_213(Block block) {
			this.field_1290 = block;
			this.field_1289 = Sets.newIdentityHashSet();
			this.field_1289.addAll(block.getStateFactory().getProperties());
		}

		public <T extends Comparable<T>> BlockStatePropertyLootCondition.class_213 method_907(Property<T> property, T comparable) {
			if (!this.field_1289.contains(property)) {
				throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(this.field_1290) + " does not have property '" + property + "'");
			} else if (!property.getValues().contains(comparable)) {
				throw new IllegalArgumentException(
					"Block " + Registry.BLOCK.getId(this.field_1290) + " property '" + property + "' does not have value '" + comparable + "'"
				);
			} else {
				this.field_1291.put(property, comparable);
				return this;
			}
		}

		@Override
		public LootCondition build() {
			return new BlockStatePropertyLootCondition(this.field_1290, this.field_1291);
		}
	}
}
