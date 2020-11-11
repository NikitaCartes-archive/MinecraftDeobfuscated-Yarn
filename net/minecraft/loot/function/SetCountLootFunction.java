/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class SetCountLootFunction
extends ConditionalLootFunction {
    private final LootNumberProvider countRange;
    private final boolean field_27909;

    private SetCountLootFunction(LootCondition[] conditions, LootNumberProvider countRange, boolean bl) {
        super(conditions);
        this.countRange = countRange;
        this.field_27909 = bl;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_COUNT;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.countRange.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        int i = this.field_27909 ? stack.getCount() : 0;
        stack.setCount(MathHelper.clamp(i + this.countRange.nextInt(context), 0, stack.getMaxCount()));
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider countRange) {
        return SetCountLootFunction.builder((LootCondition[] conditions) -> new SetCountLootFunction((LootCondition[])conditions, countRange, false));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetCountLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetCountLootFunction setCountLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setCountLootFunction, jsonSerializationContext);
            jsonObject.add("count", jsonSerializationContext.serialize(setCountLootFunction.countRange));
            jsonObject.addProperty("add", setCountLootFunction.field_27909);
        }

        @Override
        public SetCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, LootNumberProvider.class);
            boolean bl = JsonHelper.getBoolean(jsonObject, "add", false);
            return new SetCountLootFunction(lootConditions, lootNumberProvider, bl);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

