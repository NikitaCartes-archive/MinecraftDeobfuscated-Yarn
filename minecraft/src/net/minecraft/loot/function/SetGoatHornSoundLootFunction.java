package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;

public class SetGoatHornSoundLootFunction extends ConditionalLootFunction {
	SetGoatHornSoundLootFunction(LootCondition[] lootConditions) {
		super(lootConditions);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_GOAT_HORN_SOUND;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		GoatHornItem.setRandomSoundVariant(stack, context.getRandom());
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder() {
		return builder(SetGoatHornSoundLootFunction::new);
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetGoatHornSoundLootFunction> {
		public void toJson(JsonObject jsonObject, SetGoatHornSoundLootFunction setGoatHornSoundLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setGoatHornSoundLootFunction, jsonSerializationContext);
		}

		public SetGoatHornSoundLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			return new SetGoatHornSoundLootFunction(lootConditions);
		}
	}
}
