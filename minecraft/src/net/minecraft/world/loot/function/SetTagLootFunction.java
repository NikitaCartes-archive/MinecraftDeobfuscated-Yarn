package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class SetTagLootFunction extends ConditionalLootFunction {
	private final CompoundTag tag;

	private SetTagLootFunction(LootCondition[] lootConditions, CompoundTag compoundTag) {
		super(lootConditions);
		this.tag = compoundTag;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		itemStack.getOrCreateTag().copyFrom(this.tag);
		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> method_677(CompoundTag compoundTag) {
		return create(lootConditions -> new SetTagLootFunction(lootConditions, compoundTag));
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetTagLootFunction> {
		public Factory() {
			super(new Identifier("set_nbt"), SetTagLootFunction.class);
		}

		public void method_678(JsonObject jsonObject, SetTagLootFunction setTagLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setTagLootFunction, jsonSerializationContext);
			jsonObject.addProperty("tag", setTagLootFunction.tag.toString());
		}

		public SetTagLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			try {
				CompoundTag compoundTag = JsonLikeTagParser.parse(JsonHelper.getString(jsonObject, "tag"));
				return new SetTagLootFunction(lootConditions, compoundTag);
			} catch (CommandSyntaxException var5) {
				throw new JsonSyntaxException(var5.getMessage());
			}
		}
	}
}
