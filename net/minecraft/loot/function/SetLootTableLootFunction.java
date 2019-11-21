/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SetLootTableLootFunction
extends ConditionalLootFunction {
    private final Identifier id;
    private final long seed;

    private SetLootTableLootFunction(LootCondition[] lootConditions, Identifier identifier, long l) {
        super(lootConditions);
        this.id = identifier;
        this.seed = l;
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("LootTable", this.id.toString());
        if (this.seed != 0L) {
            compoundTag.putLong("LootTableSeed", this.seed);
        }
        itemStack.getOrCreateTag().put("BlockEntityTag", compoundTag);
        return itemStack;
    }

    @Override
    public void check(LootTableReporter lootTableReporter) {
        if (lootTableReporter.hasSupplier(this.id)) {
            lootTableReporter.report("Table " + this.id + " is recursively called");
            return;
        }
        super.check(lootTableReporter);
        LootTable lootTable = lootTableReporter.getSupplier(this.id);
        if (lootTable == null) {
            lootTableReporter.report("Unknown loot table called " + this.id);
        } else {
            lootTable.check(lootTableReporter.withSupplier("->{" + this.id + "}", this.id));
        }
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetLootTableLootFunction> {
        protected Factory() {
            super(new Identifier("set_loot_table"), SetLootTableLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetLootTableLootFunction setLootTableLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setLootTableLootFunction, jsonSerializationContext);
            jsonObject.addProperty("name", setLootTableLootFunction.id.toString());
            if (setLootTableLootFunction.seed != 0L) {
                jsonObject.addProperty("seed", setLootTableLootFunction.seed);
            }
        }

        @Override
        public SetLootTableLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            long l = JsonHelper.getLong(jsonObject, "seed", 0L);
            return new SetLootTableLootFunction(lootConditions, identifier, l);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }
}

