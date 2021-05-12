package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class SetCountLootFunction extends ConditionalLootFunction {
	final LootNumberProvider countRange;
	final boolean add;

	SetCountLootFunction(LootCondition[] lootConditions, LootNumberProvider lootNumberProvider, boolean bl) {
		super(lootConditions);
		this.countRange = lootNumberProvider;
		this.add = bl;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_COUNT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.countRange.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		int i = this.add ? stack.getCount() : 0;
		stack.setCount(MathHelper.clamp(i + this.countRange.nextInt(context), 0, stack.getMaxCount()));
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider countRange) {
		return builder(conditions -> new SetCountLootFunction(conditions, countRange, false));
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider countRange, boolean add) {
		return builder(conditions -> new SetCountLootFunction(conditions, countRange, add));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetCountLootFunction> {
		public void toJson(JsonObject jsonObject, SetCountLootFunction setCountLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setCountLootFunction, jsonSerializationContext);
			jsonObject.add("count", jsonSerializationContext.serialize(setCountLootFunction.countRange));
			jsonObject.addProperty("add", setCountLootFunction.add);
		}

		public SetCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, LootNumberProvider.class);
			boolean bl = JsonHelper.getBoolean(jsonObject, "add", false);
			return new SetCountLootFunction(lootConditions, lootNumberProvider, bl);
		}
	}
}
