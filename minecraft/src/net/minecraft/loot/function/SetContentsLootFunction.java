package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.loot.ContainerComponentModifiers;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;

public class SetContentsLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetContentsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<ContainerComponentModifier<?>, List<LootPoolEntry>>and(
					instance.group(
						ContainerComponentModifiers.MODIFIER_CODEC.fieldOf("component").forGetter(function -> function.component),
						LootPoolEntryTypes.CODEC.listOf().fieldOf("entries").forGetter(function -> function.entries)
					)
				)
				.apply(instance, SetContentsLootFunction::new)
	);
	private final ContainerComponentModifier<?> component;
	private final List<LootPoolEntry> entries;

	SetContentsLootFunction(List<LootCondition> conditions, ContainerComponentModifier<?> component, List<LootPoolEntry> entries) {
		super(conditions);
		this.component = component;
		this.entries = List.copyOf(entries);
	}

	@Override
	public LootFunctionType<SetContentsLootFunction> getType() {
		return LootFunctionTypes.SET_CONTENTS;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			java.util.stream.Stream.Builder<ItemStack> builder = Stream.builder();
			this.entries.forEach(entry -> entry.expand(context, choice -> choice.generateLoot(LootTable.processStacks(context.getWorld(), builder::add), context)));
			this.component.apply(stack, builder.build());
			return stack;
		}
	}

	@Override
	public void validate(LootTableReporter reporter) {
		super.validate(reporter);

		for (int i = 0; i < this.entries.size(); i++) {
			((LootPoolEntry)this.entries.get(i)).validate(reporter.makeChild(".entry[" + i + "]"));
		}
	}

	public static SetContentsLootFunction.Builder builder(ContainerComponentModifier<?> componentModifier) {
		return new SetContentsLootFunction.Builder(componentModifier);
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetContentsLootFunction.Builder> {
		private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();
		private final ContainerComponentModifier<?> componentModifier;

		public Builder(ContainerComponentModifier<?> componentModifier) {
			this.componentModifier = componentModifier;
		}

		protected SetContentsLootFunction.Builder getThisBuilder() {
			return this;
		}

		public SetContentsLootFunction.Builder withEntry(LootPoolEntry.Builder<?> entryBuilder) {
			this.entries.add(entryBuilder.build());
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetContentsLootFunction(this.getConditions(), this.componentModifier, this.entries.build());
		}
	}
}
