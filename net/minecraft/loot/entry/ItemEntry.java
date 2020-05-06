/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ItemEntry
extends LeafEntry {
    private final Item item;

    private ItemEntry(Item item, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.item = item;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        lootConsumer.accept(new ItemStack(this.item));
    }

    public static LeafEntry.Builder<?> builder(ItemConvertible drop) {
        return ItemEntry.builder((int weight, int quality, LootCondition[] conditions, LootFunction[] functions) -> new ItemEntry(drop.asItem(), weight, quality, conditions, functions));
    }

    public static class Serializer
    extends LeafEntry.Serializer<ItemEntry> {
        public Serializer() {
            super(new Identifier("item"), ItemEntry.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, ItemEntry itemEntry, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, itemEntry, jsonSerializationContext);
            Identifier identifier = Registry.ITEM.getId(itemEntry.item);
            if (identifier == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + itemEntry.item);
            }
            jsonObject.addProperty("name", identifier.toString());
        }

        @Override
        protected ItemEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
            Item item = JsonHelper.getItem(jsonObject, "name");
            return new ItemEntry(item, i, j, lootConditions, lootFunctions);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
            return this.fromJson(entryJson, context, weight, quality, conditions, functions);
        }
    }
}

