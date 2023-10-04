package net.minecraft.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
	public static final Codec<LootPool> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					LootPoolEntryTypes.CODEC.listOf().fieldOf("entries").forGetter(pool -> pool.entries),
					Codecs.createStrictOptionalFieldCodec(LootConditionTypes.CODEC.listOf(), "conditions", List.of()).forGetter(pool -> pool.conditions),
					Codecs.createStrictOptionalFieldCodec(LootFunctionTypes.CODEC.listOf(), "functions", List.of()).forGetter(pool -> pool.functions),
					LootNumberProviderTypes.CODEC.fieldOf("rolls").forGetter(pool -> pool.rolls),
					LootNumberProviderTypes.CODEC.fieldOf("bonus_rolls").orElse(ConstantLootNumberProvider.create(0.0F)).forGetter(pool -> pool.bonusRolls)
				)
				.apply(instance, LootPool::new)
	);
	private final List<LootPoolEntry> entries;
	private final List<LootCondition> conditions;
	private final Predicate<LootContext> predicate;
	private final List<LootFunction> functions;
	private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
	private final LootNumberProvider rolls;
	private final LootNumberProvider bonusRolls;

	LootPool(List<LootPoolEntry> entries, List<LootCondition> conditions, List<LootFunction> functions, LootNumberProvider rolls, LootNumberProvider bonusRolls) {
		this.entries = entries;
		this.conditions = conditions;
		this.predicate = LootConditionTypes.matchingAll(conditions);
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
			int i = this.rolls.nextInt(context) + MathHelper.floor(this.bonusRolls.nextFloat(context) * context.getLuck());

			for (int j = 0; j < i; j++) {
				this.supplyOnce(consumer, context);
			}
		}
	}

	public void validate(LootTableReporter reporter) {
		for (int i = 0; i < this.conditions.size(); i++) {
			((LootCondition)this.conditions.get(i)).validate(reporter.makeChild(".condition[" + i + "]"));
		}

		for (int i = 0; i < this.functions.size(); i++) {
			((LootFunction)this.functions.get(i)).validate(reporter.makeChild(".functions[" + i + "]"));
		}

		for (int i = 0; i < this.entries.size(); i++) {
			((LootPoolEntry)this.entries.get(i)).validate(reporter.makeChild(".entries[" + i + "]"));
		}

		this.rolls.validate(reporter.makeChild(".rolls"));
		this.bonusRolls.validate(reporter.makeChild(".bonusRolls"));
	}

	public static LootPool.Builder builder() {
		return new LootPool.Builder();
	}

	public static class Builder implements LootFunctionConsumingBuilder<LootPool.Builder>, LootConditionConsumingBuilder<LootPool.Builder> {
		private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();
		private final ImmutableList.Builder<LootCondition> conditions = ImmutableList.builder();
		private final ImmutableList.Builder<LootFunction> functions = ImmutableList.builder();
		private LootNumberProvider rolls = ConstantLootNumberProvider.create(1.0F);
		private LootNumberProvider bonusRollsRange = ConstantLootNumberProvider.create(0.0F);

		public LootPool.Builder rolls(LootNumberProvider rolls) {
			this.rolls = rolls;
			return this;
		}

		public LootPool.Builder getThisFunctionConsumingBuilder() {
			return this;
		}

		public LootPool.Builder bonusRolls(LootNumberProvider bonusRolls) {
			this.bonusRollsRange = bonusRolls;
			return this;
		}

		public LootPool.Builder with(LootPoolEntry.Builder<?> entry) {
			this.entries.add(entry.build());
			return this;
		}

		public LootPool.Builder conditionally(LootCondition.Builder builder) {
			this.conditions.add(builder.build());
			return this;
		}

		public LootPool.Builder apply(LootFunction.Builder builder) {
			this.functions.add(builder.build());
			return this;
		}

		public LootPool build() {
			return new LootPool(this.entries.build(), this.conditions.build(), this.functions.build(), this.rolls, this.bonusRollsRange);
		}
	}
}
