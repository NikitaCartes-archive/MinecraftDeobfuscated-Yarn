package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;

public class SetNbtLootFunction extends ConditionalLootFunction {
	private final CompoundTag tag;

	private SetNbtLootFunction(LootCondition[] conditions, CompoundTag tag) {
		super(conditions);
		this.tag = tag;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_NBT;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.getOrCreateTag().copyFrom(this.tag);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(CompoundTag tag) {
		return builder(conditions -> new SetNbtLootFunction(conditions, tag));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetNbtLootFunction> {
		public void toJson(JsonObject jsonObject, SetNbtLootFunction setNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setNbtLootFunction, jsonSerializationContext);
			jsonObject.addProperty("tag", setNbtLootFunction.tag.toString());
		}

		public SetNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			try {
				CompoundTag compoundTag = StringNbtReader.parse(JsonHelper.getString(jsonObject, "tag"));
				return new SetNbtLootFunction(lootConditions, compoundTag);
			} catch (CommandSyntaxException var5) {
				throw new JsonSyntaxException(var5.getMessage());
			}
		}
	}
}
