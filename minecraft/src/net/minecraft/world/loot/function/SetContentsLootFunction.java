package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.entry.LootEntry;

public class SetContentsLootFunction extends ConditionalLootFunction {
	private final List<LootEntry> entries;

	private SetContentsLootFunction(LootCondition[] lootConditions, List<LootEntry> list) {
		super(lootConditions);
		this.entries = ImmutableList.copyOf(list);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.isEmpty()) {
			return itemStack;
		} else {
			DefaultedList<ItemStack> defaultedList = DefaultedList.of();
			this.entries
				.forEach(lootEntry -> lootEntry.expand(lootContext, lootChoice -> lootChoice.drop(LootSupplier.limitedConsumer(defaultedList::add), lootContext)));
			CompoundTag compoundTag = new CompoundTag();
			Inventories.toTag(compoundTag, defaultedList);
			CompoundTag compoundTag2 = itemStack.getOrCreateTag();
			compoundTag2.put("BlockEntityTag", compoundTag.copyFrom(compoundTag2.getCompound("BlockEntityTag")));
			return itemStack;
		}
	}

	@Override
	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		super.check(lootTableReporter, function, set, lootContextType);

		for (int i = 0; i < this.entries.size(); i++) {
			((LootEntry)this.entries.get(i)).check(lootTableReporter.makeChild(".entry[" + i + "]"), function, set, lootContextType);
		}
	}

	public static SetContentsLootFunction.Builer builder() {
		return new SetContentsLootFunction.Builer();
	}

	public static class Builer extends ConditionalLootFunction.Builder<SetContentsLootFunction.Builer> {
		private final List<LootEntry> entries = Lists.<LootEntry>newArrayList();

		protected SetContentsLootFunction.Builer method_603() {
			return this;
		}

		public SetContentsLootFunction.Builer withEntry(LootEntry.Builder<?> builder) {
			this.entries.add(builder.build());
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetContentsLootFunction(this.getConditions(), this.entries);
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetContentsLootFunction> {
		protected Factory() {
			super(new Identifier("set_contents"), SetContentsLootFunction.class);
		}

		public void method_604(JsonObject jsonObject, SetContentsLootFunction setContentsLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setContentsLootFunction, jsonSerializationContext);
			jsonObject.add("entries", jsonSerializationContext.serialize(setContentsLootFunction.entries));
		}

		public SetContentsLootFunction method_605(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootEntry[].class);
			return new SetContentsLootFunction(lootConditions, Arrays.asList(lootEntrys));
		}
	}
}
