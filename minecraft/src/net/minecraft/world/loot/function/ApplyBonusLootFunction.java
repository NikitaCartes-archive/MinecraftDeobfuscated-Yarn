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

public class ApplyBonusLootFunction extends ConditionalLootFunction {
	private static final Map<Identifier, ApplyBonusLootFunction.FormulaFactory> FACTORIES = Maps.<Identifier, ApplyBonusLootFunction.FormulaFactory>newHashMap();
	private final Enchantment enchantment;
	private final ApplyBonusLootFunction.Formula formula;

	private ApplyBonusLootFunction(LootCondition[] lootConditions, Enchantment enchantment, ApplyBonusLootFunction.Formula formula) {
		super(lootConditions);
		this.enchantment = enchantment;
		this.formula = formula;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1229);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		ItemStack itemStack2 = lootContext.method_296(LootContextParameters.field_1229);
		if (itemStack2 != null) {
			int i = EnchantmentHelper.getLevel(this.enchantment, itemStack2);
			int j = this.formula.getValue(lootContext.getRandom(), itemStack.getAmount(), i);
			itemStack.setAmount(j);
		}

		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> method_463(Enchantment enchantment, float f, int i) {
		return create(lootConditions -> new ApplyBonusLootFunction(lootConditions, enchantment, new ApplyBonusLootFunction.BinomialWithBonusCount(i, f)));
	}

	public static ConditionalLootFunction.Builder<?> method_455(Enchantment enchantment) {
		return create(lootConditions -> new ApplyBonusLootFunction(lootConditions, enchantment, new ApplyBonusLootFunction.OreDrops()));
	}

	public static ConditionalLootFunction.Builder<?> method_456(Enchantment enchantment) {
		return create(lootConditions -> new ApplyBonusLootFunction(lootConditions, enchantment, new ApplyBonusLootFunction.UniformBonusCount(1)));
	}

	public static ConditionalLootFunction.Builder<?> method_461(Enchantment enchantment, int i) {
		return create(lootConditions -> new ApplyBonusLootFunction(lootConditions, enchantment, new ApplyBonusLootFunction.UniformBonusCount(i)));
	}

	static {
		FACTORIES.put(ApplyBonusLootFunction.BinomialWithBonusCount.field_1013, ApplyBonusLootFunction.BinomialWithBonusCount::fromJson);
		FACTORIES.put(ApplyBonusLootFunction.OreDrops.field_1015, ApplyBonusLootFunction.OreDrops::fromJson);
		FACTORIES.put(ApplyBonusLootFunction.UniformBonusCount.field_1016, ApplyBonusLootFunction.UniformBonusCount::fromJson);
	}

	static final class BinomialWithBonusCount implements ApplyBonusLootFunction.Formula {
		public static final Identifier field_1013 = new Identifier("binomial_with_bonus_count");
		private final int field_1014;
		private final float field_1012;

		public BinomialWithBonusCount(int i, float f) {
			this.field_1014 = i;
			this.field_1012 = f;
		}

		@Override
		public int getValue(Random random, int i, int j) {
			for (int k = 0; k < j + this.field_1014; k++) {
				if (random.nextFloat() < this.field_1012) {
					i++;
				}
			}

			return i;
		}

		@Override
		public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("extra", this.field_1014);
			jsonObject.addProperty("probability", this.field_1012);
		}

		public static ApplyBonusLootFunction.Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			int i = JsonHelper.getInt(jsonObject, "extra");
			float f = JsonHelper.getFloat(jsonObject, "probability");
			return new ApplyBonusLootFunction.BinomialWithBonusCount(i, f);
		}

		@Override
		public Identifier method_466() {
			return field_1013;
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<ApplyBonusLootFunction> {
		public Factory() {
			super(new Identifier("apply_bonus"), ApplyBonusLootFunction.class);
		}

		public void method_469(JsonObject jsonObject, ApplyBonusLootFunction applyBonusLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, applyBonusLootFunction, jsonSerializationContext);
			jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.method_10221(applyBonusLootFunction.enchantment).toString());
			jsonObject.addProperty("formula", applyBonusLootFunction.formula.method_466().toString());
			JsonObject jsonObject2 = new JsonObject();
			applyBonusLootFunction.formula.toJson(jsonObject2, jsonSerializationContext);
			if (jsonObject2.size() > 0) {
				jsonObject.add("parameters", jsonObject2);
			}
		}

		public ApplyBonusLootFunction method_470(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
			Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT
				.method_17966(identifier)
				.orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + identifier));
			Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "formula"));
			ApplyBonusLootFunction.FormulaFactory formulaFactory = (ApplyBonusLootFunction.FormulaFactory)ApplyBonusLootFunction.FACTORIES.get(identifier2);
			if (formulaFactory == null) {
				throw new JsonParseException("Invalid formula id: " + identifier2);
			} else {
				ApplyBonusLootFunction.Formula formula;
				if (jsonObject.has("parameters")) {
					formula = formulaFactory.deserialize(JsonHelper.getObject(jsonObject, "parameters"), jsonDeserializationContext);
				} else {
					formula = formulaFactory.deserialize(new JsonObject(), jsonDeserializationContext);
				}

				return new ApplyBonusLootFunction(lootConditions, enchantment, formula);
			}
		}
	}

	interface Formula {
		int getValue(Random random, int i, int j);

		void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext);

		Identifier method_466();
	}

	interface FormulaFactory {
		ApplyBonusLootFunction.Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}

	static final class OreDrops implements ApplyBonusLootFunction.Formula {
		public static final Identifier field_1015 = new Identifier("ore_drops");

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
			} else {
				return i;
			}
		}

		@Override
		public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
		}

		public static ApplyBonusLootFunction.Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new ApplyBonusLootFunction.OreDrops();
		}

		@Override
		public Identifier method_466() {
			return field_1015;
		}
	}

	static final class UniformBonusCount implements ApplyBonusLootFunction.Formula {
		public static final Identifier field_1016 = new Identifier("uniform_bonus_count");
		private final int field_1017;

		public UniformBonusCount(int i) {
			this.field_1017 = i;
		}

		@Override
		public int getValue(Random random, int i, int j) {
			return i + random.nextInt(this.field_1017 * j + 1);
		}

		@Override
		public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("bonusMultiplier", this.field_1017);
		}

		public static ApplyBonusLootFunction.Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			int i = JsonHelper.getInt(jsonObject, "bonusMultiplier");
			return new ApplyBonusLootFunction.UniformBonusCount(i);
		}

		@Override
		public Identifier method_466() {
			return field_1016;
		}
	}
}
