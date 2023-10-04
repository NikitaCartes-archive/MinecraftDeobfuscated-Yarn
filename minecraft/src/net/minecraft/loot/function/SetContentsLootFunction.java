package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;

public class SetContentsLootFunction extends ConditionalLootFunction {
	public static final Codec<SetContentsLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<RegistryEntry<BlockEntityType<?>>, List<LootPoolEntry>>and(
					instance.group(
						Registries.BLOCK_ENTITY_TYPE.createEntryCodec().fieldOf("type").forGetter(function -> function.type),
						LootPoolEntryTypes.CODEC.listOf().fieldOf("entries").forGetter(function -> function.entries)
					)
				)
				.apply(instance, SetContentsLootFunction::new)
	);
	private final RegistryEntry<BlockEntityType<?>> type;
	private final List<LootPoolEntry> entries;

	SetContentsLootFunction(List<LootCondition> conditions, RegistryEntry<BlockEntityType<?>> blockEntityType, List<LootPoolEntry> entries) {
		super(conditions);
		this.type = blockEntityType;
		this.entries = List.copyOf(entries);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_CONTENTS;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			DefaultedList<ItemStack> defaultedList = DefaultedList.of();
			this.entries
				.forEach(entry -> entry.expand(context, choice -> choice.generateLoot(LootTable.processStacks(context.getWorld(), defaultedList::add), context)));
			NbtCompound nbtCompound = new NbtCompound();
			Inventories.writeNbt(nbtCompound, defaultedList);
			NbtCompound nbtCompound2 = BlockItem.getBlockEntityNbt(stack);
			if (nbtCompound2 == null) {
				nbtCompound2 = nbtCompound;
			} else {
				nbtCompound2.copyFrom(nbtCompound);
			}

			BlockItem.setBlockEntityNbt(stack, this.type.value(), nbtCompound2);
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

	public static SetContentsLootFunction.Builder builder(BlockEntityType<?> type) {
		return new SetContentsLootFunction.Builder(type);
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetContentsLootFunction.Builder> {
		private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();
		private final BlockEntityType<?> type;

		public Builder(BlockEntityType<?> type) {
			this.type = type;
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
			return new SetContentsLootFunction(this.getConditions(), this.type.getRegistryEntry(), this.entries.build());
		}
	}
}
