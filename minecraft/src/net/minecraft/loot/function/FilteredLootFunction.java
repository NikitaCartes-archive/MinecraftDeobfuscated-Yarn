package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.item.ItemPredicate;

public class FilteredLootFunction extends ConditionalLootFunction {
	public static final MapCodec<FilteredLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<ItemPredicate, LootFunction>and(
					instance.group(
						ItemPredicate.CODEC.fieldOf("item_filter").forGetter(lootFunction -> lootFunction.itemFilter),
						LootFunctionTypes.CODEC.fieldOf("modifier").forGetter(lootFunction -> lootFunction.modifier)
					)
				)
				.apply(instance, FilteredLootFunction::new)
	);
	private final ItemPredicate itemFilter;
	private final LootFunction modifier;

	private FilteredLootFunction(List<LootCondition> conditions, ItemPredicate itemFilter, LootFunction modifier) {
		super(conditions);
		this.itemFilter = itemFilter;
		this.modifier = modifier;
	}

	@Override
	public LootFunctionType<FilteredLootFunction> getType() {
		return LootFunctionTypes.FILTERED;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		return this.itemFilter.test(stack) ? (ItemStack)this.modifier.apply(stack, context) : stack;
	}

	@Override
	public void validate(LootTableReporter reporter) {
		super.validate(reporter);
		this.modifier.validate(reporter.makeChild(".modifier"));
	}
}
