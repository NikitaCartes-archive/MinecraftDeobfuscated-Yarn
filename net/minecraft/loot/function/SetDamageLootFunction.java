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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    final LootNumberProvider durabilityRange;
    final boolean add;

    SetDamageLootFunction(LootCondition[] conditons, LootNumberProvider durabilityRange, boolean add) {
        super(conditons);
        this.durabilityRange = durabilityRange;
        this.add = add;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_DAMAGE;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.durabilityRange.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isDamageable()) {
            int i = stack.getMaxDamage();
            float f = this.add ? 1.0f - (float)stack.getDamage() / (float)i : 0.0f;
            float g = 1.0f - MathHelper.clamp(this.durabilityRange.nextFloat(context) + f, 0.0f, 1.0f);
            stack.setDamage(MathHelper.floor(g * (float)i));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)stack);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider durabilityRange) {
        return SetDamageLootFunction.builder((LootCondition[] conditions) -> new SetDamageLootFunction((LootCondition[])conditions, durabilityRange, false));
    }

    public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider durabilityRange, boolean add) {
        return SetDamageLootFunction.builder((LootCondition[] conditions) -> new SetDamageLootFunction((LootCondition[])conditions, durabilityRange, add));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetDamageLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetDamageLootFunction setDamageLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setDamageLootFunction, jsonSerializationContext);
            jsonObject.add("damage", jsonSerializationContext.serialize(setDamageLootFunction.durabilityRange));
            jsonObject.addProperty("add", setDamageLootFunction.add);
        }

        @Override
        public SetDamageLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "damage", jsonDeserializationContext, LootNumberProvider.class);
            boolean bl = JsonHelper.getBoolean(jsonObject, "add", false);
            return new SetDamageLootFunction(lootConditions, lootNumberProvider, bl);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

