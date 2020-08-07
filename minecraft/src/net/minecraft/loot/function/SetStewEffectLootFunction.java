package net.minecraft.loot.function;

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
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SetStewEffectLootFunction extends ConditionalLootFunction {
	private final Map<StatusEffect, UniformLootTableRange> effects;

	private SetStewEffectLootFunction(LootCondition[] conditions, Map<StatusEffect, UniformLootTableRange> effects) {
		super(conditions);
		this.effects = ImmutableMap.copyOf(effects);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.field_25224;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.getItem() == Items.field_8766 && !this.effects.isEmpty()) {
			Random random = context.getRandom();
			int i = random.nextInt(this.effects.size());
			Entry<StatusEffect, UniformLootTableRange> entry = Iterables.get(this.effects.entrySet(), i);
			StatusEffect statusEffect = (StatusEffect)entry.getKey();
			int j = ((UniformLootTableRange)entry.getValue()).next(random);
			if (!statusEffect.isInstant()) {
				j *= 20;
			}

			SuspiciousStewItem.addEffectToStew(stack, statusEffect, j);
			return stack;
		} else {
			return stack;
		}
	}

	public static SetStewEffectLootFunction.Builder builder() {
		return new SetStewEffectLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetStewEffectLootFunction.Builder> {
		private final Map<StatusEffect, UniformLootTableRange> map = Maps.<StatusEffect, UniformLootTableRange>newHashMap();

		protected SetStewEffectLootFunction.Builder method_639() {
			return this;
		}

		public SetStewEffectLootFunction.Builder withEffect(StatusEffect effect, UniformLootTableRange durationRange) {
			this.map.put(effect, durationRange);
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetStewEffectLootFunction(this.getConditions(), this.map);
		}
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetStewEffectLootFunction> {
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
						.getOrEmpty(new Identifier(string))
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
