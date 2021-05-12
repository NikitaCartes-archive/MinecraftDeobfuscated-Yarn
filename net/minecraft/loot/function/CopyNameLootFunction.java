/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Nameable;

public class CopyNameLootFunction
extends ConditionalLootFunction {
    final Source source;

    CopyNameLootFunction(LootCondition[] lootConditions, Source source) {
        super(lootConditions);
        this.source = source;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.COPY_NAME;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.source.parameter);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Nameable nameable;
        Object object = context.get(this.source.parameter);
        if (object instanceof Nameable && (nameable = (Nameable)object).hasCustomName()) {
            stack.setCustomName(nameable.getDisplayName());
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(Source source) {
        return CopyNameLootFunction.builder((LootCondition[] conditions) -> new CopyNameLootFunction((LootCondition[])conditions, source));
    }

    public static enum Source {
        THIS("this", LootContextParameters.THIS_ENTITY),
        KILLER("killer", LootContextParameters.KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY);

        public final String name;
        public final LootContextParameter<?> parameter;

        private Source(String name, LootContextParameter<?> parameter) {
            this.name = name;
            this.parameter = parameter;
        }

        public static Source get(String name) {
            for (Source source : Source.values()) {
                if (!source.name.equals(name)) continue;
                return source;
            }
            throw new IllegalArgumentException("Invalid name source " + name);
        }
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<CopyNameLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, CopyNameLootFunction copyNameLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, copyNameLootFunction, jsonSerializationContext);
            jsonObject.addProperty("source", copyNameLootFunction.source.name);
        }

        @Override
        public CopyNameLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            Source source = Source.get(JsonHelper.getString(jsonObject, "source"));
            return new CopyNameLootFunction(lootConditions, source);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

