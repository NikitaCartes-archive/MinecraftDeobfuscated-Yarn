/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SetLootTableLootFunction
extends ConditionalLootFunction {
    final Identifier id;
    final long seed;
    final BlockEntityType<?> type;

    SetLootTableLootFunction(LootCondition[] conditions, Identifier id, long seed, BlockEntityType<?> type) {
        super(conditions);
        this.id = id;
        this.seed = seed;
        this.type = type;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_LOOT_TABLE;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        }
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null) {
            nbtCompound = new NbtCompound();
        }
        nbtCompound.putString("LootTable", this.id.toString());
        if (this.seed != 0L) {
            nbtCompound.putLong("LootTableSeed", this.seed);
        }
        BlockItem.setBlockEntityNbt(stack, this.type, nbtCompound);
        return stack;
    }

    @Override
    public void validate(LootTableReporter reporter) {
        if (reporter.hasTable(this.id)) {
            reporter.report("Table " + this.id + " is recursively called");
            return;
        }
        super.validate(reporter);
        LootTable lootTable = reporter.getTable(this.id);
        if (lootTable == null) {
            reporter.report("Unknown loot table called " + this.id);
        } else {
            lootTable.validate(reporter.withTable("->{" + this.id + "}", this.id));
        }
    }

    public static ConditionalLootFunction.Builder<?> builder(BlockEntityType<?> type, Identifier id) {
        return SetLootTableLootFunction.builder(conditions -> new SetLootTableLootFunction((LootCondition[])conditions, id, 0L, type));
    }

    public static ConditionalLootFunction.Builder<?> builder(BlockEntityType<?> type, Identifier id, long seed) {
        return SetLootTableLootFunction.builder(conditions -> new SetLootTableLootFunction((LootCondition[])conditions, id, seed, type));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetLootTableLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetLootTableLootFunction setLootTableLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setLootTableLootFunction, jsonSerializationContext);
            jsonObject.addProperty("name", setLootTableLootFunction.id.toString());
            jsonObject.addProperty("type", Registry.BLOCK_ENTITY_TYPE.getId(setLootTableLootFunction.type).toString());
            if (setLootTableLootFunction.seed != 0L) {
                jsonObject.addProperty("seed", setLootTableLootFunction.seed);
            }
        }

        @Override
        public SetLootTableLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            long l = JsonHelper.getLong(jsonObject, "seed", 0L);
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "type"));
            BlockEntityType<?> blockEntityType = Registry.BLOCK_ENTITY_TYPE.getOrEmpty(identifier2).orElseThrow(() -> new JsonSyntaxException("Unknown block entity type id '" + identifier2 + "'"));
            return new SetLootTableLootFunction(lootConditions, identifier, l, blockEntityType);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

