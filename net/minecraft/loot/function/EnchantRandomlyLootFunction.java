/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

public class EnchantRandomlyLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final List<Enchantment> enchantments;

    EnchantRandomlyLootFunction(LootCondition[] conditions, Collection<Enchantment> enchantments) {
        super(conditions);
        this.enchantments = ImmutableList.copyOf(enchantments);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Enchantment enchantment2;
        AbstractRandom abstractRandom = context.getRandom();
        if (this.enchantments.isEmpty()) {
            boolean bl = stack.isOf(Items.BOOK);
            List list = Registry.ENCHANTMENT.stream().filter(Enchantment::isAvailableForRandomSelection).filter(enchantment -> bl || enchantment.isAcceptableItem(stack)).collect(Collectors.toList());
            if (list.isEmpty()) {
                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)stack);
                return stack;
            }
            enchantment2 = (Enchantment)list.get(abstractRandom.nextInt(list.size()));
        } else {
            enchantment2 = this.enchantments.get(abstractRandom.nextInt(this.enchantments.size()));
        }
        return EnchantRandomlyLootFunction.addEnchantmentToStack(stack, enchantment2, abstractRandom);
    }

    private static ItemStack addEnchantmentToStack(ItemStack stack, Enchantment enchantment, AbstractRandom random) {
        int i = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        if (stack.isOf(Items.BOOK)) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
        } else {
            stack.addEnchantment(enchantment, i);
        }
        return stack;
    }

    public static Builder create() {
        return new Builder();
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return EnchantRandomlyLootFunction.builder(conditions -> new EnchantRandomlyLootFunction((LootCondition[])conditions, (Collection<Enchantment>)ImmutableList.of()));
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Set<Enchantment> enchantments = Sets.newHashSet();

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder add(Enchantment enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        @Override
        public LootFunction build() {
            return new EnchantRandomlyLootFunction(this.getConditions(), this.enchantments);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<EnchantRandomlyLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, EnchantRandomlyLootFunction enchantRandomlyLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, enchantRandomlyLootFunction, jsonSerializationContext);
            if (!enchantRandomlyLootFunction.enchantments.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Enchantment enchantment : enchantRandomlyLootFunction.enchantments) {
                    Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
                    if (identifier == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }
                    jsonArray.add(new JsonPrimitive(identifier.toString()));
                }
                jsonObject.add("enchantments", jsonArray);
            }
        }

        @Override
        public EnchantRandomlyLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            ArrayList<Enchantment> list = Lists.newArrayList();
            if (jsonObject.has("enchantments")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "enchantments");
                for (JsonElement jsonElement : jsonArray) {
                    String string = JsonHelper.asString(jsonElement, "enchantment");
                    Enchantment enchantment = Registry.ENCHANTMENT.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
                    list.add(enchantment);
                }
            }
            return new EnchantRandomlyLootFunction(lootConditions, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

