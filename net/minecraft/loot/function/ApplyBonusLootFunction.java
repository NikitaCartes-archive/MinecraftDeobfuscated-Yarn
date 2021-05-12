/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ApplyBonusLootFunction
extends ConditionalLootFunction {
    static final Map<Identifier, FormulaFactory> FACTORIES = Maps.newHashMap();
    final Enchantment enchantment;
    final Formula formula;

    ApplyBonusLootFunction(LootCondition[] lootConditions, Enchantment enchantment, Formula formula) {
        super(lootConditions);
        this.enchantment = enchantment;
        this.formula = formula;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.APPLY_BONUS;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ItemStack itemStack = context.get(LootContextParameters.TOOL);
        if (itemStack != null) {
            int i = EnchantmentHelper.getLevel(this.enchantment, itemStack);
            int j = this.formula.getValue(context.getRandom(), stack.getCount(), i);
            stack.setCount(j);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> binomialWithBonusCount(Enchantment enchantment, float probability, int extra) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new BinomialWithBonusCount(extra, probability)));
    }

    public static ConditionalLootFunction.Builder<?> oreDrops(Enchantment enchantment) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new OreDrops()));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment enchantment) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new UniformBonusCount(1)));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment enchantment, int bonusMultiplier) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new UniformBonusCount(bonusMultiplier)));
    }

    static {
        FACTORIES.put(BinomialWithBonusCount.ID, BinomialWithBonusCount::fromJson);
        FACTORIES.put(OreDrops.ID, OreDrops::fromJson);
        FACTORIES.put(UniformBonusCount.ID, UniformBonusCount::fromJson);
    }

    static interface Formula {
        public int getValue(Random var1, int var2, int var3);

        public void toJson(JsonObject var1, JsonSerializationContext var2);

        public Identifier getId();
    }

    static final class UniformBonusCount
    implements Formula {
        public static final Identifier ID = new Identifier("uniform_bonus_count");
        private final int bonusMultiplier;

        public UniformBonusCount(int bonusMultiplier) {
            this.bonusMultiplier = bonusMultiplier;
        }

        @Override
        public int getValue(Random random, int initialCount, int enchantmentLevel) {
            return initialCount + random.nextInt(this.bonusMultiplier * enchantmentLevel + 1);
        }

        @Override
        public void toJson(JsonObject json, JsonSerializationContext context) {
            json.addProperty("bonusMultiplier", this.bonusMultiplier);
        }

        public static Formula fromJson(JsonObject json, JsonDeserializationContext context) {
            int i = JsonHelper.getInt(json, "bonusMultiplier");
            return new UniformBonusCount(i);
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static final class OreDrops
    implements Formula {
        public static final Identifier ID = new Identifier("ore_drops");

        OreDrops() {
        }

        @Override
        public int getValue(Random random, int initialCount, int enchantmentLevel) {
            if (enchantmentLevel > 0) {
                int i = random.nextInt(enchantmentLevel + 2) - 1;
                if (i < 0) {
                    i = 0;
                }
                return initialCount * (i + 1);
            }
            return initialCount;
        }

        @Override
        public void toJson(JsonObject json, JsonSerializationContext context) {
        }

        public static Formula fromJson(JsonObject json, JsonDeserializationContext context) {
            return new OreDrops();
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static final class BinomialWithBonusCount
    implements Formula {
        public static final Identifier ID = new Identifier("binomial_with_bonus_count");
        private final int extra;
        private final float probability;

        public BinomialWithBonusCount(int extra, float probability) {
            this.extra = extra;
            this.probability = probability;
        }

        @Override
        public int getValue(Random random, int initialCount, int enchantmentLevel) {
            for (int i = 0; i < enchantmentLevel + this.extra; ++i) {
                if (!(random.nextFloat() < this.probability)) continue;
                ++initialCount;
            }
            return initialCount;
        }

        @Override
        public void toJson(JsonObject json, JsonSerializationContext context) {
            json.addProperty("extra", this.extra);
            json.addProperty("probability", Float.valueOf(this.probability));
        }

        public static Formula fromJson(JsonObject json, JsonDeserializationContext context) {
            int i = JsonHelper.getInt(json, "extra");
            float f = JsonHelper.getFloat(json, "probability");
            return new BinomialWithBonusCount(i, f);
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static interface FormulaFactory {
        public Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<ApplyBonusLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, ApplyBonusLootFunction applyBonusLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, applyBonusLootFunction, jsonSerializationContext);
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(applyBonusLootFunction.enchantment).toString());
            jsonObject.addProperty("formula", applyBonusLootFunction.formula.getId().toString());
            JsonObject jsonObject2 = new JsonObject();
            applyBonusLootFunction.formula.toJson(jsonObject2, jsonSerializationContext);
            if (jsonObject2.size() > 0) {
                jsonObject.add("parameters", jsonObject2);
            }
        }

        @Override
        public ApplyBonusLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
            Enchantment enchantment = Registry.ENCHANTMENT.getOrEmpty(identifier).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + identifier));
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "formula"));
            FormulaFactory formulaFactory = FACTORIES.get(identifier2);
            if (formulaFactory == null) {
                throw new JsonParseException("Invalid formula id: " + identifier2);
            }
            Formula formula = jsonObject.has("parameters") ? formulaFactory.deserialize(JsonHelper.getObject(jsonObject, "parameters"), jsonDeserializationContext) : formulaFactory.deserialize(new JsonObject(), jsonDeserializationContext);
            return new ApplyBonusLootFunction(lootConditions, enchantment, formula);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

