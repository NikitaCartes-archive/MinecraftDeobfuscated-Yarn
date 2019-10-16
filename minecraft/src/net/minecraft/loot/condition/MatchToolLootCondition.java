package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

public class MatchToolLootCondition implements LootCondition {
	private final ItemPredicate predicate;

	public MatchToolLootCondition(ItemPredicate itemPredicate) {
		this.predicate = itemPredicate;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.TOOL);
	}

	public boolean method_946(LootContext lootContext) {
		ItemStack itemStack = lootContext.get(LootContextParameters.TOOL);
		return itemStack != null && this.predicate.test(itemStack);
	}

	public static LootCondition.Builder builder(ItemPredicate.Builder builder) {
		return () -> new MatchToolLootCondition(builder.build());
	}

	public static class Factory extends LootCondition.Factory<MatchToolLootCondition> {
		protected Factory() {
			super(new Identifier("match_tool"), MatchToolLootCondition.class);
		}

		public void method_948(JsonObject jsonObject, MatchToolLootCondition matchToolLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", matchToolLootCondition.predicate.serialize());
		}

		public MatchToolLootCondition method_949(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("predicate"));
			return new MatchToolLootCondition(itemPredicate);
		}
	}
}
