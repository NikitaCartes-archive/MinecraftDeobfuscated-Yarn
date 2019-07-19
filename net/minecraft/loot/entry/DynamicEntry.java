/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class DynamicEntry
extends LeafEntry {
    public static final Identifier instance = new Identifier("dynamic");
    private final Identifier name;

    private DynamicEntry(Identifier identifier, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
        super(i, j, lootConditions, lootFunctions);
        this.name = identifier;
    }

    @Override
    public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
        lootContext.drop(this.name, consumer);
    }

    public static LeafEntry.Builder<?> builder(Identifier identifier) {
        return DynamicEntry.builder((int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) -> new DynamicEntry(identifier, i, j, lootConditions, lootFunctions));
    }

    public static class Serializer
    extends LeafEntry.Serializer<DynamicEntry> {
        public Serializer() {
            super(new Identifier("dynamic"), DynamicEntry.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, DynamicEntry dynamicEntry, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, dynamicEntry, jsonSerializationContext);
            jsonObject.addProperty("name", dynamicEntry.name.toString());
        }

        @Override
        protected DynamicEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new DynamicEntry(identifier, i, j, lootConditions, lootFunctions);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, lootConditions, lootFunctions);
        }
    }
}

