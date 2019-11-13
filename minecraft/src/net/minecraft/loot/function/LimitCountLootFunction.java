package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LimitCountLootFunction extends ConditionalLootFunction {
	private final BoundedIntUnaryOperator limit;

	private LimitCountLootFunction(LootCondition[] conditions, BoundedIntUnaryOperator limit) {
		super(conditions);
		this.limit = limit;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		int i = this.limit.applyAsInt(stack.getCount());
		stack.setCount(i);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator limit) {
		return builder(conditions -> new LimitCountLootFunction(conditions, limit));
	}

	public static class Factory extends ConditionalLootFunction.Factory<LimitCountLootFunction> {
		protected Factory() {
			super(new Identifier("limit_count"), LimitCountLootFunction.class);
		}

		public void method_510(JsonObject jsonObject, LimitCountLootFunction limitCountLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, limitCountLootFunction, jsonSerializationContext);
			jsonObject.add("limit", jsonSerializationContext.serialize(limitCountLootFunction.limit));
		}

		public LimitCountLootFunction method_509(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "limit", jsonDeserializationContext, BoundedIntUnaryOperator.class);
			return new LimitCountLootFunction(lootConditions, boundedIntUnaryOperator);
		}
	}
}