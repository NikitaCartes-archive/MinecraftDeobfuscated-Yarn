/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SetContentsLootFunction
extends ConditionalLootFunction {
    private final List<LootEntry> entries;

    private SetContentsLootFunction(LootCondition[] lootConditions, List<LootEntry> list) {
        super(lootConditions);
        this.entries = ImmutableList.copyOf(list);
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        this.entries.forEach(lootEntry -> lootEntry.expand(lootContext, lootChoice -> lootChoice.drop(LootTable.limitedConsumer(defaultedList::add), lootContext)));
        CompoundTag compoundTag = new CompoundTag();
        Inventories.toTag(compoundTag, defaultedList);
        CompoundTag compoundTag2 = itemStack.getOrCreateTag();
        compoundTag2.put("BlockEntityTag", compoundTag.copyFrom(compoundTag2.getCompound("BlockEntityTag")));
        return itemStack;
    }

    @Override
    public void check(LootTableReporter lootTableReporter) {
        super.check(lootTableReporter);
        for (int i = 0; i < this.entries.size(); ++i) {
            this.entries.get(i).check(lootTableReporter.makeChild(".entry[" + i + "]"));
        }
    }

    public static Builer builder() {
        return new Builer();
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetContentsLootFunction> {
        protected Factory() {
            super(new Identifier("set_contents"), SetContentsLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetContentsLootFunction setContentsLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setContentsLootFunction, jsonSerializationContext);
            jsonObject.add("entries", jsonSerializationContext.serialize(setContentsLootFunction.entries));
        }

        @Override
        public SetContentsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootEntry[].class);
            return new SetContentsLootFunction(lootConditions, Arrays.asList(lootEntrys));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }

    public static class Builer
    extends ConditionalLootFunction.Builder<Builer> {
        private final List<LootEntry> entries = Lists.newArrayList();

        @Override
        protected Builer getThisBuilder() {
            return this;
        }

        public Builer withEntry(LootEntry.Builder<?> builder) {
            this.entries.add(builder.build());
            return this;
        }

        @Override
        public LootFunction build() {
            return new SetContentsLootFunction(this.getConditions(), this.entries);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

