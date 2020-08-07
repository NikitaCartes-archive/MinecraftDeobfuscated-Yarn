package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;

public class SetCountLootFunction extends ConditionalLootFunction {
	private final LootTableRange countRange;

	private SetCountLootFunction(LootCondition[] conditions, LootTableRange countRange) {
		super(conditions);
		this.countRange = countRange;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.field_25214;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.setCount(this.countRange.next(context.getRandom()));
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootTableRange countRange) {
		return builder(conditions -> new SetCountLootFunction(conditions, countRange));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetCountLootFunction> {
		public void method_623(JsonObject jsonObject, SetCountLootFunction setCountLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setCountLootFunction, jsonSerializationContext);
			jsonObject.add("count", LootTableRanges.toJson(setCountLootFunction.countRange, jsonSerializationContext));
		}

		public SetCountLootFunction method_622(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootTableRange lootTableRange = LootTableRanges.fromJson(jsonObject.get("count"), jsonDeserializationContext);
			return new SetCountLootFunction(lootConditions, lootTableRange);
		}
	}
}
