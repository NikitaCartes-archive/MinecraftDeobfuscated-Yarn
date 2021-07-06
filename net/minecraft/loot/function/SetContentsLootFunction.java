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
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class SetContentsLootFunction
extends ConditionalLootFunction {
    final List<LootPoolEntry> entries;

    SetContentsLootFunction(LootCondition[] lootConditions, List<LootPoolEntry> list) {
        super(lootConditions);
        this.entries = ImmutableList.copyOf(list);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_CONTENTS;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        this.entries.forEach(entry -> entry.expand(context, choice -> choice.generateLoot(LootTable.processStacks(defaultedList::add), context)));
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, defaultedList);
        NbtCompound nbtCompound2 = stack.getOrCreateNbt();
        nbtCompound2.put("BlockEntityTag", nbtCompound.copyFrom(nbtCompound2.getCompound("BlockEntityTag")));
        return stack;
    }

    @Override
    public void validate(LootTableReporter reporter) {
        super.validate(reporter);
        for (int i = 0; i < this.entries.size(); ++i) {
            this.entries.get(i).validate(reporter.makeChild(".entry[" + i + "]"));
        }
    }

    public static Builer builder() {
        return new Builer();
    }

    public static class Builer
    extends ConditionalLootFunction.Builder<Builer> {
        private final List<LootPoolEntry> entries = Lists.newArrayList();

        @Override
        protected Builer getThisBuilder() {
            return this;
        }

        public Builer withEntry(LootPoolEntry.Builder<?> entryBuilder) {
            this.entries.add(entryBuilder.build());
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

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetContentsLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetContentsLootFunction setContentsLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setContentsLootFunction, jsonSerializationContext);
            jsonObject.add("entries", jsonSerializationContext.serialize(setContentsLootFunction.entries));
        }

        @Override
        public SetContentsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootPoolEntry[] lootPoolEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootPoolEntry[].class);
            return new SetContentsLootFunction(lootConditions, Arrays.asList(lootPoolEntrys));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

