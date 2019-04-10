package net.minecraft.world.loot;

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
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
	private final LootEntry[] entries;
	private final LootCondition[] conditions;
	private final Predicate<LootContext> predicate;
	private final LootFunction[] functions;
	private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
	private final LootTableRange rollsRange;
	private final UniformLootTableRange bonusRollsRange;

	private LootPool(
		LootEntry[] lootEntrys,
		LootCondition[] lootConditions,
		LootFunction[] lootFunctions,
		LootTableRange lootTableRange,
		UniformLootTableRange uniformLootTableRange
	) {
		this.entries = lootEntrys;
		this.conditions = lootConditions;
		this.predicate = LootConditions.joinAnd(lootConditions);
		this.functions = lootFunctions;
		this.javaFunctions = LootFunctions.join(lootFunctions);
		this.rollsRange = lootTableRange;
		this.bonusRollsRange = uniformLootTableRange;
	}

	private void supplyOnce(Consumer<ItemStack> consumer, LootContext lootContext) {
		Random random = lootContext.getRandom();
		List<LootChoice> list = Lists.<LootChoice>newArrayList();
		MutableInt mutableInt = new MutableInt();

		for (LootEntry lootEntry : this.entries) {
			lootEntry.expand(lootContext, lootChoicex -> {
				int i = lootChoicex.getWeight(lootContext.getLuck());
				if (i > 0) {
					list.add(lootChoicex);
					mutableInt.add(i);
				}
			});
		}

		int i = list.size();
		if (mutableInt.intValue() != 0 && i != 0) {
			if (i == 1) {
				((LootChoice)list.get(0)).drop(consumer, lootContext);
			} else {
				int j = random.nextInt(mutableInt.intValue());

				for (LootChoice lootChoice : list) {
					j -= lootChoice.getWeight(lootContext.getLuck());
					if (j < 0) {
						lootChoice.drop(consumer, lootContext);
						return;
					}
				}
			}
		}
	}

	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		if (this.predicate.test(lootContext)) {
			Consumer<ItemStack> consumer2 = LootFunction.apply(this.javaFunctions, consumer, lootContext);
			Random random = lootContext.getRandom();
			int i = this.rollsRange.next(random) + MathHelper.floor(this.bonusRollsRange.nextFloat(random) * lootContext.getLuck());

			for (int j = 0; j < i; j++) {
				this.supplyOnce(consumer2, lootContext);
			}
		}
	}

	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].check(lootTableReporter.makeChild(".condition[" + i + "]"), function, set, lootContextType);
		}

		for (int i = 0; i < this.functions.length; i++) {
			this.functions[i].check(lootTableReporter.makeChild(".functions[" + i + "]"), function, set, lootContextType);
		}

		for (int i = 0; i < this.entries.length; i++) {
			this.entries[i].check(lootTableReporter.makeChild(".entries[" + i + "]"), function, set, lootContextType);
		}
	}

	public static LootPool.Builder builder() {
		return new LootPool.Builder();
	}

	public static class Builder implements FunctionConsumerBuilder<LootPool.Builder>, ConditionConsumerBuilder<LootPool.Builder> {
		private final List<LootEntry> entries = Lists.<LootEntry>newArrayList();
		private final List<LootCondition> conditions = Lists.<LootCondition>newArrayList();
		private final List<LootFunction> functions = Lists.<LootFunction>newArrayList();
		private LootTableRange rollsRange = new UniformLootTableRange(1.0F);
		private UniformLootTableRange bonusRollsRange = new UniformLootTableRange(0.0F, 0.0F);

		public LootPool.Builder withRolls(LootTableRange lootTableRange) {
			this.rollsRange = lootTableRange;
			return this;
		}

		public LootPool.Builder method_354() {
			return this;
		}

		public LootPool.Builder withEntry(LootEntry.Builder<?> builder) {
			this.entries.add(builder.build());
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
			if (this.rollsRange == null) {
				throw new IllegalArgumentException("Rolls not set");
			} else {
				return new LootPool(
					(LootEntry[])this.entries.toArray(new LootEntry[0]),
					(LootCondition[])this.conditions.toArray(new LootCondition[0]),
					(LootFunction[])this.functions.toArray(new LootFunction[0]),
					this.rollsRange,
					this.bonusRollsRange
				);
			}
		}
	}

	public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
		public LootPool method_358(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot pool");
			LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootEntry[].class);
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
			LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
			LootTableRange lootTableRange = LootTableRanges.deserialize(jsonObject.get("rolls"), jsonDeserializationContext);
			UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(
				jsonObject, "bonus_rolls", new UniformLootTableRange(0.0F, 0.0F), jsonDeserializationContext, UniformLootTableRange.class
			);
			return new LootPool(lootEntrys, lootConditions, lootFunctions, lootTableRange, uniformLootTableRange);
		}

		public JsonElement method_357(LootPool lootPool, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rolls", LootTableRanges.serialize(lootPool.rollsRange, jsonSerializationContext));
			jsonObject.add("entries", jsonSerializationContext.serialize(lootPool.entries));
			if (lootPool.bonusRollsRange.getMinValue() != 0.0F && lootPool.bonusRollsRange.getMaxValue() != 0.0F) {
				jsonObject.add("bonus_rolls", jsonSerializationContext.serialize(lootPool.bonusRollsRange));
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
