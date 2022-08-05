/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SetGoatHornSoundLootFunction
extends ConditionalLootFunction {
    final TagKey<Instrument> options;

    SetGoatHornSoundLootFunction(LootCondition[] conditions, TagKey<Instrument> options) {
        super(conditions);
        this.options = options;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_INSTRUMENT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        GoatHornItem.setRandomInstrumentFromTag(stack, this.options, context.getRandom());
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(TagKey<Instrument> options) {
        return SetGoatHornSoundLootFunction.builder((LootCondition[] conditions) -> new SetGoatHornSoundLootFunction((LootCondition[])conditions, options));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetGoatHornSoundLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetGoatHornSoundLootFunction setGoatHornSoundLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setGoatHornSoundLootFunction, jsonSerializationContext);
            jsonObject.addProperty("options", "#" + setGoatHornSoundLootFunction.options.id());
        }

        @Override
        public SetGoatHornSoundLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            String string = JsonHelper.getString(jsonObject, "options");
            if (!string.startsWith("#")) {
                throw new JsonSyntaxException("Inline tag value not supported: " + string);
            }
            return new SetGoatHornSoundLootFunction(lootConditions, TagKey.of(Registry.INSTRUMENT_KEY, new Identifier(string.substring(1))));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

