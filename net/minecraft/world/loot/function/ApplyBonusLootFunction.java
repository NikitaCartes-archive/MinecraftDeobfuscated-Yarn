/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.function.ConditionalLootFunction;

public class ApplyBonusLootFunction
extends ConditionalLootFunction {
    private static final Map<Identifier, FormulaFactory> FACTORIES = Maps.newHashMap();
    private final Enchantment enchantment;
    private final Formula formula;

    private ApplyBonusLootFunction(LootCondition[] lootConditions, Enchantment enchantment, Formula formula) {
        super(lootConditions);
        this.enchantment = enchantment;
        this.formula = formula;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        ItemStack itemStack2 = lootContext.get(LootContextParameters.TOOL);
        if (itemStack2 != null) {
            int i = EnchantmentHelper.getLevel(this.enchantment, itemStack2);
            int j = this.formula.getValue(lootContext.getRandom(), itemStack.getCount(), i);
            itemStack.setCount(j);
        }
        return itemStack;
    }

    public static ConditionalLootFunction.Builder<?> binomialWithBonusCount(Enchantment enchantment, float f, int i) {
        return ApplyBonusLootFunction.builder(lootConditions -> new ApplyBonusLootFunction((LootCondition[])lootConditions, enchantment, new BinomialWithBonusCount(i, f)));
    }

    public static ConditionalLootFunction.Builder<?> oreDrops(Enchantment enchantment) {
        return ApplyBonusLootFunction.builder(lootConditions -> new ApplyBonusLootFunction((LootCondition[])lootConditions, enchantment, new OreDrops()));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment enchantment) {
        return ApplyBonusLootFunction.builder(lootConditions -> new ApplyBonusLootFunction((LootCondition[])lootConditions, enchantment, new UniformBonusCount(1)));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment enchantment, int i) {
        return ApplyBonusLootFunction.builder(lootConditions -> new ApplyBonusLootFunction((LootCondition[])lootConditions, enchantment, new UniformBonusCount(i)));
    }

    static {
        FACTORIES.put(BinomialWithBonusCount.ID, BinomialWithBonusCount::fromJson);
        FACTORIES.put(OreDrops.ID, OreDrops::fromJson);
        FACTORIES.put(UniformBonusCount.ID, UniformBonusCount::fromJson);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<ApplyBonusLootFunction> {
        public Factory() {
            super(new Identifier("apply_bonus"), ApplyBonusLootFunction.class);
        }

        public void method_469(JsonObject jsonObject, ApplyBonusLootFunction applyBonusLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.method_529(jsonObject, applyBonusLootFunction, jsonSerializationContext);
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(applyBonusLootFunction.enchantment).toString());
            jsonObject.addProperty("formula", applyBonusLootFunction.formula.getId().toString());
            JsonObject jsonObject2 = new JsonObject();
            applyBonusLootFunction.formula.toJson(jsonObject2, jsonSerializationContext);
            if (jsonObject2.size() > 0) {
                jsonObject.add("parameters", jsonObject2);
            }
        }

        public ApplyBonusLootFunction method_470(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
            Enchantment enchantment = Registry.ENCHANTMENT.getOrEmpty(identifier).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + identifier));
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "formula"));
            FormulaFactory formulaFactory = (FormulaFactory)FACTORIES.get(identifier2);
            if (formulaFactory == null) {
                throw new JsonParseException("Invalid formula id: " + identifier2);
            }
            Formula formula = jsonObject.has("parameters") ? formulaFactory.deserialize(JsonHelper.getObject(jsonObject, "parameters"), jsonDeserializationContext) : formulaFactory.deserialize(new JsonObject(), jsonDeserializationContext);
            return new ApplyBonusLootFunction(lootConditions, enchantment, formula);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.method_470(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }

    static final class OreDrops
    implements Formula {
        public static final Identifier ID = new Identifier("ore_drops");

        private OreDrops() {
        }

        @Override
        public int getValue(Random random, int i, int j) {
            if (j > 0) {
                int k = random.nextInt(j + 2) - 1;
                if (k < 0) {
                    k = 0;
                }
                return i * (k + 1);
            }
            return i;
        }

        @Override
        public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
        }

        public static Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new OreDrops();
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static final class UniformBonusCount
    implements Formula {
        public static final Identifier ID = new Identifier("uniform_bonus_count");
        private final int bonusMultiplier;

        public UniformBonusCount(int i) {
            this.bonusMultiplier = i;
        }

        @Override
        public int getValue(Random random, int i, int j) {
            return i + random.nextInt(this.bonusMultiplier * j + 1);
        }

        @Override
        public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("bonusMultiplier", this.bonusMultiplier);
        }

        public static Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int i = JsonHelper.getInt(jsonObject, "bonusMultiplier");
            return new UniformBonusCount(i);
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

        public BinomialWithBonusCount(int i, float f) {
            this.extra = i;
            this.probability = f;
        }

        @Override
        public int getValue(Random random, int i, int j) {
            for (int k = 0; k < j + this.extra; ++k) {
                if (!(random.nextFloat() < this.probability)) continue;
                ++i;
            }
            return i;
        }

        @Override
        public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("extra", this.extra);
            jsonObject.addProperty("probability", Float.valueOf(this.probability));
        }

        public static Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int i = JsonHelper.getInt(jsonObject, "extra");
            float f = JsonHelper.getFloat(jsonObject, "probability");
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

    static interface Formula {
        public int getValue(Random var1, int var2, int var3);

        public void toJson(JsonObject var1, JsonSerializationContext var2);

        public Identifier getId();
    }
}

