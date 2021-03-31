/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;

public class SetNbtLootFunction
extends ConditionalLootFunction {
    private final NbtCompound nbt;

    private SetNbtLootFunction(LootCondition[] conditions, NbtCompound nbt) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_NBT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        stack.getOrCreateTag().copyFrom(this.nbt);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(NbtCompound nbt) {
        return SetNbtLootFunction.builder((LootCondition[] conditions) -> new SetNbtLootFunction((LootCondition[])conditions, nbt));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetNbtLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetNbtLootFunction setNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setNbtLootFunction, jsonSerializationContext);
            jsonObject.addProperty("tag", setNbtLootFunction.nbt.toString());
        }

        @Override
        public SetNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            try {
                NbtCompound nbtCompound = StringNbtReader.parse(JsonHelper.getString(jsonObject, "tag"));
                return new SetNbtLootFunction(lootConditions, nbtCompound);
            } catch (CommandSyntaxException commandSyntaxException) {
                throw new JsonSyntaxException(commandSyntaxException.getMessage());
            }
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

