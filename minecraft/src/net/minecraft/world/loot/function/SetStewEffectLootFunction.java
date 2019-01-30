package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class SetStewEffectLootFunction extends ConditionalLootFunction {
	private final Map<StatusEffect, UniformLootTableRange> effects;

	private SetStewEffectLootFunction(LootCondition[] lootConditions, Map<StatusEffect, UniformLootTableRange> map) {
		super(lootConditions);
		this.effects = ImmutableMap.copyOf(map);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.getItem() == Items.field_8766 && !this.effects.isEmpty()) {
			Random random = lootContext.getRandom();
			int i = random.nextInt(this.effects.size());
			Entry<StatusEffect, UniformLootTableRange> entry = Iterables.get(this.effects.entrySet(), i);
			StatusEffect statusEffect = (StatusEffect)entry.getKey();
			int j = ((UniformLootTableRange)entry.getValue()).next(random) * 20;
			SuspiciousStewItem.addEffectToStew(itemStack, statusEffect, j);
			return itemStack;
		} else {
			return itemStack;
		}
	}

	public static SetStewEffectLootFunction.Builder create() {
		return new SetStewEffectLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetStewEffectLootFunction.Builder> {
		private final Map<StatusEffect, UniformLootTableRange> map = Maps.<StatusEffect, UniformLootTableRange>newHashMap();

		protected SetStewEffectLootFunction.Builder create() {
			return this;
		}

		public SetStewEffectLootFunction.Builder withEffect(StatusEffect statusEffect, UniformLootTableRange uniformLootTableRange) {
			this.map.put(statusEffect, uniformLootTableRange);
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetStewEffectLootFunction(this.getConditions(), this.map);
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetStewEffectLootFunction> {
		public Factory() {
			super(new Identifier("set_stew_effect"), SetStewEffectLootFunction.class);
		}

		public void method_642(JsonObject jsonObject, SetStewEffectLootFunction setStewEffectLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setStewEffectLootFunction, jsonSerializationContext);
			if (!setStewEffectLootFunction.effects.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (StatusEffect statusEffect : setStewEffectLootFunction.effects.keySet()) {
					JsonObject jsonObject2 = new JsonObject();
					Identifier identifier = Registry.STATUS_EFFECT.getId(statusEffect);
					if (identifier == null) {
						throw new IllegalArgumentException("Don't know how to serialize mob effect " + statusEffect);
					}

					jsonObject2.add("type", new JsonPrimitive(identifier.toString()));
					jsonObject2.add("duration", jsonSerializationContext.serialize(setStewEffectLootFunction.effects.get(statusEffect)));
					jsonArray.add(jsonObject2);
				}

				jsonObject.add("effects", jsonArray);
			}
		}

		public SetStewEffectLootFunction method_641(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Map<StatusEffect, UniformLootTableRange> map = Maps.<StatusEffect, UniformLootTableRange>newHashMap();
			if (jsonObject.has("effects")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "effects")) {
					String string = JsonHelper.getString(jsonElement.getAsJsonObject(), "type");
					StatusEffect statusEffect = (StatusEffect)Registry.STATUS_EFFECT
						.getOptional(new Identifier(string))
						.orElseThrow(() -> new JsonSyntaxException("Unknown mob effect '" + string + "'"));
					UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(
						jsonElement.getAsJsonObject(), "duration", jsonDeserializationContext, UniformLootTableRange.class
					);
					map.put(statusEffect, uniformLootTableRange);
				}
			}

			return new SetStewEffectLootFunction(lootConditions, map);
		}
	}
}
