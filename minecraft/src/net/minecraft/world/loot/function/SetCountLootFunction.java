package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.LootTableRanges;
import net.minecraft.world.loot.context.LootContext;

public class SetCountLootFunction extends ConditionalLootFunction {
	private final LootTableRange countRange;

	private SetCountLootFunction(class_4570[] args, LootTableRange lootTableRange) {
		super(args);
		this.countRange = lootTableRange;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		itemStack.setCount(this.countRange.next(lootContext.getRandom()));
		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootTableRange lootTableRange) {
		return builder(args -> new SetCountLootFunction(args, lootTableRange));
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetCountLootFunction> {
		protected Factory() {
			super(new Identifier("set_count"), SetCountLootFunction.class);
		}

		public void method_623(JsonObject jsonObject, SetCountLootFunction setCountLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setCountLootFunction, jsonSerializationContext);
			jsonObject.add("count", LootTableRanges.serialize(setCountLootFunction.countRange, jsonSerializationContext));
		}

		public SetCountLootFunction method_622(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
			LootTableRange lootTableRange = LootTableRanges.deserialize(jsonObject.get("count"), jsonDeserializationContext);
			return new SetCountLootFunction(args, lootTableRange);
		}
	}
}
