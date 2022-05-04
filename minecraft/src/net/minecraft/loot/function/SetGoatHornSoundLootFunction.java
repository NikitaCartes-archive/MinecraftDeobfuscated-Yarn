package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SetGoatHornSoundLootFunction extends ConditionalLootFunction {
	final TagKey<Instrument> field_39184;

	SetGoatHornSoundLootFunction(LootCondition[] lootConditions, TagKey<Instrument> tagKey) {
		super(lootConditions);
		this.field_39184 = tagKey;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_INSTRUMENT;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		GoatHornItem.setRandomInstrumentFromTag(stack, this.field_39184, context.getRandom());
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(TagKey<Instrument> tagKey) {
		return builder(lootConditions -> new SetGoatHornSoundLootFunction(lootConditions, tagKey));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetGoatHornSoundLootFunction> {
		public void toJson(JsonObject jsonObject, SetGoatHornSoundLootFunction setGoatHornSoundLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setGoatHornSoundLootFunction, jsonSerializationContext);
			jsonObject.addProperty("options", "#" + setGoatHornSoundLootFunction.field_39184.id());
		}

		public SetGoatHornSoundLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			String string = JsonHelper.getString(jsonObject, "options");
			if (!string.startsWith("#")) {
				throw new JsonSyntaxException("Inline tag value not supported: " + string);
			} else {
				return new SetGoatHornSoundLootFunction(lootConditions, TagKey.of(Registry.INSTRUMENT_KEY, new Identifier(string.substring(1))));
			}
		}
	}
}
