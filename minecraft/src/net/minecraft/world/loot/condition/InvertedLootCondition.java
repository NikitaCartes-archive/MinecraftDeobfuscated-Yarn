package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextType;

public class InvertedLootCondition implements LootCondition {
	private final LootCondition field_1283;

	private InvertedLootCondition(LootCondition lootCondition) {
		this.field_1283 = lootCondition;
	}

	public final boolean method_888(LootContext lootContext) {
		return !this.field_1283.test(lootContext);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.field_1283.getRequiredParameters();
	}

	@Override
	public void method_292(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		LootCondition.super.method_292(lootTableReporter, function, set, lootContextType);
		this.field_1283.method_292(lootTableReporter, function, set, lootContextType);
	}

	public static LootCondition.Builder method_889(LootCondition.Builder builder) {
		InvertedLootCondition invertedLootCondition = new InvertedLootCondition(builder.build());
		return () -> invertedLootCondition;
	}

	public static class Factory extends LootCondition.Factory<InvertedLootCondition> {
		public Factory() {
			super(new Identifier("inverted"), InvertedLootCondition.class);
		}

		public void method_892(JsonObject jsonObject, InvertedLootCondition invertedLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("term", jsonSerializationContext.serialize(invertedLootCondition.field_1283));
		}

		public InvertedLootCondition method_891(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition lootCondition = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, LootCondition.class);
			return new InvertedLootCondition(lootCondition);
		}
	}
}
