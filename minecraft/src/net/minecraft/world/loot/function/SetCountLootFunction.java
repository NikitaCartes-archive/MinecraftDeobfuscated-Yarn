package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.LootTableRanges;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class SetCountLootFunction extends ConditionalLootFunction {
	private final LootTableRange countRange;

	private SetCountLootFunction(LootCondition[] lootConditions, LootTableRange lootTableRange) {
		super(lootConditions);
		this.countRange = lootTableRange;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		itemStack.setCount(this.countRange.next(lootContext.getRandom()));
		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootTableRange lootTableRange) {
		return builder(lootConditions -> new SetCountLootFunction(lootConditions, lootTableRange));
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetCountLootFunction> {
		protected Factory() {
			super(new Identifier("set_count"), SetCountLootFunction.class);
		}

		public void method_623(JsonObject jsonObject, SetCountLootFunction setCountLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setCountLootFunction, jsonSerializationContext);
			jsonObject.add("count", LootTableRanges.serialize(setCountLootFunction.countRange, jsonSerializationContext));
		}

		public SetCountLootFunction method_622(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootTableRange lootTableRange = LootTableRanges.deserialize(jsonObject.get("count"), jsonDeserializationContext);
			return new SetCountLootFunction(lootConditions, lootTableRange);
		}
	}
}
