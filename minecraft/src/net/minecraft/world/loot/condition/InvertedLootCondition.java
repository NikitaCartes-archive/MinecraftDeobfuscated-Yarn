package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;

public class InvertedLootCondition implements LootCondition {
	private final LootCondition term;

	private InvertedLootCondition(LootCondition lootCondition) {
		this.term = lootCondition;
	}

	public final boolean method_888(LootContext lootContext) {
		return !this.term.test(lootContext);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.term.getRequiredParameters();
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		LootCondition.super.check(lootTableReporter);
		this.term.check(lootTableReporter);
	}

	public static LootCondition.Builder builder(LootCondition.Builder builder) {
		InvertedLootCondition invertedLootCondition = new InvertedLootCondition(builder.build());
		return () -> invertedLootCondition;
	}

	public static class Factory extends LootCondition.Factory<InvertedLootCondition> {
		public Factory() {
			super(new Identifier("inverted"), InvertedLootCondition.class);
		}

		public void method_892(JsonObject jsonObject, InvertedLootCondition invertedLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("term", jsonSerializationContext.serialize(invertedLootCondition.term));
		}

		public InvertedLootCondition method_891(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition lootCondition = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, LootCondition.class);
			return new InvertedLootCondition(lootCondition);
		}
	}
}
