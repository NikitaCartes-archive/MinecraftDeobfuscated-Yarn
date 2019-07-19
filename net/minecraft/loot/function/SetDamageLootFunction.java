/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UniformLootTableRange durabilityRange;

    private SetDamageLootFunction(LootCondition[] lootConditions, UniformLootTableRange uniformLootTableRange) {
        super(lootConditions);
        this.durabilityRange = uniformLootTableRange;
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isDamageable()) {
            float f = 1.0f - this.durabilityRange.nextFloat(lootContext.getRandom());
            itemStack.setDamage(MathHelper.floor(f * (float)itemStack.getMaxDamage()));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)itemStack);
        }
        return itemStack;
    }

    public static ConditionalLootFunction.Builder<?> builder(UniformLootTableRange uniformLootTableRange) {
        return SetDamageLootFunction.builder((LootCondition[] lootConditions) -> new SetDamageLootFunction((LootCondition[])lootConditions, uniformLootTableRange));
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
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }
}

