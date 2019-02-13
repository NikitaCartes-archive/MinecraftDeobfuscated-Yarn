package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class EnterBlockCriterion implements Criterion<EnterBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("enter_block");
	private final Map<PlayerAdvancementTracker, EnterBlockCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, EnterBlockCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EnterBlockCriterion.Conditions> conditionsContainer
	) {
		EnterBlockCriterion.Handler handler = (EnterBlockCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new EnterBlockCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addConditon(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EnterBlockCriterion.Conditions> conditionsContainer
	) {
		EnterBlockCriterion.Handler handler = (EnterBlockCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public EnterBlockCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = null;
		if (jsonObject.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			block = (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		}

		Map<Property<?>, Object> map = null;
		if (jsonObject.has("state")) {
			if (block == null) {
				throw new JsonSyntaxException("Can't define block state without a specific block type");
			}

			StateFactory<Block, BlockState> stateFactory = block.getStateFactory();

			for (Entry<String, JsonElement> entry : JsonHelper.getObject(jsonObject, "state").entrySet()) {
				Property<?> property = stateFactory.getProperty((String)entry.getKey());
				if (property == null) {
					throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + Registry.BLOCK.getId(block) + "'");
				}

				String string = JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey());
				Optional<?> optional = property.getValue(string);
				if (!optional.isPresent()) {
					throw new JsonSyntaxException(
						"Invalid block state value '" + string + "' for property '" + (String)entry.getKey() + "' on block '" + Registry.BLOCK.getId(block) + "'"
					);
				}

				if (map == null) {
					map = Maps.<Property<?>, Object>newHashMap();
				}

				map.put(property, optional.get());
			}
		}

		return new EnterBlockCriterion.Conditions(block, map);
	}

	public void method_8885(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
		EnterBlockCriterion.Handler handler = (EnterBlockCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(blockState);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final Map<Property<?>, Object> state;

		public Conditions(@Nullable Block block, @Nullable Map<Property<?>, Object> map) {
			super(EnterBlockCriterion.ID);
			this.block = block;
			this.state = map;
		}

		public static EnterBlockCriterion.Conditions block(Block block) {
			return new EnterBlockCriterion.Conditions(block, null);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
				if (this.state != null && !this.state.isEmpty()) {
					JsonObject jsonObject2 = new JsonObject();

					for (Entry<Property<?>, ?> entry : this.state.entrySet()) {
						jsonObject2.addProperty(((Property)entry.getKey()).getName(), SystemUtil.getValueAsString((Property)entry.getKey(), entry.getValue()));
					}

					jsonObject.add("state", jsonObject2);
				}
			}

			return jsonObject;
		}

		public boolean matches(BlockState blockState) {
			if (this.block != null && blockState.getBlock() != this.block) {
				return false;
			} else {
				if (this.state != null) {
					for (Entry<Property<?>, Object> entry : this.state.entrySet()) {
						if (blockState.get((Property)entry.getKey()) != entry.getValue()) {
							return false;
						}
					}
				}

				return true;
			}
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<EnterBlockCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<EnterBlockCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addConditon(Criterion.ConditionsContainer<EnterBlockCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<EnterBlockCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(BlockState blockState) {
			List<Criterion.ConditionsContainer<EnterBlockCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<EnterBlockCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(blockState)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<EnterBlockCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<EnterBlockCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
