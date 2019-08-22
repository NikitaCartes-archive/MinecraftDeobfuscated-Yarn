package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class SetNbtLootFunction extends ConditionalLootFunction {
	private final CompoundTag tag;

	private SetNbtLootFunction(LootCondition[] lootConditions, CompoundTag compoundTag) {
		super(lootConditions);
		this.tag = compoundTag;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		itemStack.getOrCreateTag().copyFrom(this.tag);
		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> builder(CompoundTag compoundTag) {
		return builder(lootConditions -> new SetNbtLootFunction(lootConditions, compoundTag));
	}

	public static class Builder extends ConditionalLootFunction.Factory<SetNbtLootFunction> {
		public Builder() {
			super(new Identifier("set_nbt"), SetNbtLootFunction.class);
		}

		public void method_678(JsonObject jsonObject, SetNbtLootFunction setNbtLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setNbtLootFunction, jsonSerializationContext);
			jsonObject.addProperty("tag", setNbtLootFunction.tag.toString());
		}

		public SetNbtLootFunction method_679(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			try {
				CompoundTag compoundTag = StringNbtReader.parse(JsonHelper.getString(jsonObject, "tag"));
				return new SetNbtLootFunction(lootConditions, compoundTag);
			} catch (CommandSyntaxException var5) {
				throw new JsonSyntaxException(var5.getMessage());
			}
		}
	}
}
