/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

public class SetGoatHornSoundLootFunction
extends ConditionalLootFunction {
    SetGoatHornSoundLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_GOAT_HORN_SOUND;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        GoatHornItem.setRandomSoundVariant(stack, context.getRandom());
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return SetGoatHornSoundLootFunction.builder(SetGoatHornSoundLootFunction::new);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetGoatHornSoundLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetGoatHornSoundLootFunction setGoatHornSoundLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setGoatHornSoundLootFunction, jsonSerializationContext);
        }

        @Override
        public SetGoatHornSoundLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new SetGoatHornSoundLootFunction(lootConditions);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

