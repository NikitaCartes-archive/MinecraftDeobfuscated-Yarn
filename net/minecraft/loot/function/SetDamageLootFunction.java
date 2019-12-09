/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UniformLootTableRange durabilityRange;

    private SetDamageLootFunction(LootCondition[] contents, UniformLootTableRange durabilityRange) {
        super(contents);
        this.durabilityRange = durabilityRange;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isDamageable()) {
            float f = 1.0f - this.durabilityRange.nextFloat(context.getRandom());
            stack.setDamage(MathHelper.floor(f * (float)stack.getMaxDamage()));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)stack);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(UniformLootTableRange durabilityRange) {
        return SetDamageLootFunction.builder((LootCondition[] conditions) -> new SetDamageLootFunction((LootCondition[])conditions, durabilityRange));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetDamageLootFunction> {
        protected Factory() {
            super(new Identifier("set_damage"), SetDamageLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetDamageLootFunction setDamageLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setDamageLootFunction, jsonSerializationContext);
            jsonObject.add("damage", jsonSerializationContext.serialize(setDamageLootFunction.durabilityRange));
        }

        @Override
        public SetDamageLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new SetDamageLootFunction(lootConditions, JsonHelper.deserialize(jsonObject, "damage", jsonDeserializationContext, UniformLootTableRange.class));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

