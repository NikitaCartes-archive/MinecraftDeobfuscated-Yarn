package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
	private final LootPoolEntry[] entries;
	private final LootCondition[] conditions;
	private final Predicate<LootContext> predicate;
	private final LootFunction[] functions;
	private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
	private final LootTableRange rolls;
	private final UniformLootTableRange bonusRolls;

	private LootPool(LootPoolEntry[] entries, LootCondition[] conditions, LootFunction[] functions, LootTableRange rolls, UniformLootTableRange bonusRolls) {
		this.entries = entries;
		this.conditions = conditions;
		this.predicate = LootConditionTypes.joinAnd(conditions);
		this.functions = functions;
		this.javaFunctions = LootFunctionTypes.join(functions);
		this.rolls = rolls;
		this.bonusRolls = bonusRolls;
	}

	private void supplyOnce(Consumer<ItemStack> lootConsumer, LootContext context) {
		Random random = context.getRandom();
		List<LootChoice> list = Lists.<LootChoice>newArrayList();
		MutableInt mutableInt = new MutableInt();

		for (LootPoolEntry lootPoolEntry : this.entries) {
			lootPoolEntry.expand(context, choice -> {
				int i = choice.getWeight(context.getLuck());
				if (i > 0) {
					list.add(choice);
					mutableInt.add(i);
				}
			});
		}

		int i = list.size();
		if (mutableInt.intValue() != 0 && i != 0) {
			if (i == 1) {
				((LootChoice)list.get(0)).generateLoot(lootConsumer, context);
			} else {
				int j = random.nextInt(mutableInt.intValue());

				for (LootChoice lootChoice : list) {
					j -= lootChoice.getWeight(context.getLuck());
					if (j < 0) {
						lootChoice.generateLoot(lootConsumer, context);
						return;
					}
				}
			}
		}
	}

	public void addGeneratedLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		if (this.predicate.test(context)) {
			Consumer<ItemStack> consumer = LootFunction.apply(this.javaFunctions, lootConsumer, context);
			Random random = context.getRandom();
			int i = this.rolls.next(random) + MathHelper.floor(this.bonusRolls.nextFloat(random) * context.getLuck());

			for (int j = 0; j < i; j++) {
				this.supplyOnce(consumer, context);
			}
		}
	}

	public void validate(LootTableReporter lootTableReporter) {
		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].validate(lootTableReporter.makeChild(".condition[" + i + "]"));
		}

		for (int i = 0; i < this.functions.length; i++) {
			this.functions[i].validate(lootTableReporter.makeChild(".functions[" + i + "]"));
		}

		for (int i = 0; i < this.entries.length; i++) {
			this.entries[i].validate(lootTableReporter.makeChild(".entries[" + i + "]"));
		}
	}

	public static LootPool.Builder builder() {
		return new LootPool.Builder();
	}

	public static class Builder implements LootFunctionConsumingBuilder<LootPool.Builder>, LootConditionConsumingBuilder<LootPool.Builder> {
		private final List<LootPoolEntry> entries = Lists.<LootPoolEntry>newArrayList();
		private final List<LootCondition> conditions = Lists.<LootCondition>newArrayList();
		private final List<LootFunction> functions = Lists.<LootFunction>newArrayList();
		private LootTableRange rolls = new UniformLootTableRange(1.0F);
		private UniformLootTableRange bonusRollsRange = new UniformLootTableRange(0.0F, 0.0F);

		public LootPool.Builder rolls(LootTableRange rolls) {
			this.rolls = rolls;
			return this;
		}

		public LootPool.Builder method_354() {
			return this;
		}

		public LootPool.Builder with(LootPoolEntry.Builder<?> entry) {
			this.entries.add(entry.build());
			return this;
		}

		public LootPool.Builder method_356(LootCondition.Builder builder) {
			this.conditions.add(builder.build());
			return this;
		}

		public LootPool.Builder method_353(LootFunction.Builder builder) {
			this.functions.add(builder.build());
			return this;
		}

		public LootPool build() {
			if (this.rolls == null) {
				throw new IllegalArgumentException("Rolls not set");
			} else {
				return new LootPool(
					(LootPoolEntry[])this.entries.toArray(new LootPoolEntry[0]),
					(LootCondition[])this.conditions.toArray(new LootCondition[0]),
					(LootFunction[])this.functions.toArray(new LootFunction[0]),
					this.rolls,
					this.bonusRollsRange
				);
			}
		}
	}

	public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
		public LootPool method_358(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot pool");
			LootPoolEntry[] lootPoolEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootPoolEntry[].class);
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
			LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
			LootTableRange lootTableRange = LootTableRanges.fromJson(jsonObject.get("rolls"), jsonDeserializationContext);
			UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(
				jsonObject, "bonus_rolls", new UniformLootTableRange(0.0F, 0.0F), jsonDeserializationContext, UniformLootTableRange.class
			);
			return new LootPool(lootPoolEntrys, lootConditions, lootFunctions, lootTableRange, uniformLootTableRange);
		}

		public JsonElement method_357(LootPool lootPool, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rolls", LootTableRanges.toJson(lootPool.rolls, jsonSerializationContext));
			jsonObject.add("entries", jsonSerializationContext.serialize(lootPool.entries));
			if (lootPool.bonusRolls.getMinValue() != 0.0F && lootPool.bonusRolls.getMaxValue() != 0.0F) {
				jsonObject.add("bonus_rolls", jsonSerializationContext.serialize(lootPool.bonusRolls));
			}

			if (!ArrayUtils.isEmpty((Object[])lootPool.conditions)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(lootPool.conditions));
			}

			if (!ArrayUtils.isEmpty((Object[])lootPool.functions)) {
				jsonObject.add("functions", jsonSerializationContext.serialize(lootPool.functions));
			}

			return jsonObject;
		}
	}
}
