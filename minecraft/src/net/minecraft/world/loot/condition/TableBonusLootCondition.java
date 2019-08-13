package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class TableBonusLootCondition implements LootCondition {
	private final Enchantment enchantment;
	private final float[] chances;

	private TableBonusLootCondition(Enchantment enchantment, float[] fs) {
		this.enchantment = enchantment;
		this.chances = fs;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1229);
	}

	public boolean method_799(LootContext lootContext) {
		ItemStack itemStack = lootContext.get(LootContextParameters.field_1229);
		int i = itemStack != null ? EnchantmentHelper.getLevel(this.enchantment, itemStack) : 0;
		float f = this.chances[Math.min(i, this.chances.length - 1)];
		return lootContext.getRandom().nextFloat() < f;
	}

	public static LootCondition.Builder builder(Enchantment enchantment, float... fs) {
		return () -> new TableBonusLootCondition(enchantment, fs);
	}

	public static class Factory extends LootCondition.Factory<TableBonusLootCondition> {
		public Factory() {
			super(new Identifier("table_bonus"), TableBonusLootCondition.class);
		}

		public void method_805(JsonObject jsonObject, TableBonusLootCondition tableBonusLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(tableBonusLootCondition.enchantment).toString());
			jsonObject.add("chances", jsonSerializationContext.serialize(tableBonusLootCondition.chances));
		}

		public TableBonusLootCondition method_804(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
			Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT
				.getOrEmpty(identifier)
				.orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + identifier));
			float[] fs = JsonHelper.deserialize(jsonObject, "chances", jsonDeserializationContext, float[].class);
			return new TableBonusLootCondition(enchantment, fs);
		}
	}
}
