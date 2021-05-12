package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;

public class SetNbtLootFunction extends ConditionalLootFunction {
	final NbtCompound nbt;

	SetNbtLootFunction(LootCondition[] lootConditions, NbtCompound nbtCompound) {
		super(lootConditions);
		this.nbt = nbtCompound;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_NBT;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.getOrCreateTag().copyFrom(this.nbt);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(NbtCompound nbt) {
		return builder(conditions -> new SetNbtLootFunction(conditions, nbt));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetNbtLootFunction> {
		public void toJson(JsonObject jsonObject, SetNbtLootFunction setNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setNbtLootFunction, jsonSerializationContext);
			jsonObject.addProperty("tag", setNbtLootFunction.nbt.toString());
		}

		public SetNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(JsonHelper.getString(jsonObject, "tag"));
				return new SetNbtLootFunction(lootConditions, nbtCompound);
			} catch (CommandSyntaxException var5) {
				throw new JsonSyntaxException(var5.getMessage());
			}
		}
	}
}
