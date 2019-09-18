package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;

public class InvertedLootCondition implements class_4570 {
	private final class_4570 term;

	private InvertedLootCondition(class_4570 arg) {
		this.term = arg;
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
		class_4570.super.check(lootTableReporter);
		this.term.check(lootTableReporter);
	}

	public static class_4570.Builder builder(class_4570.Builder builder) {
		InvertedLootCondition invertedLootCondition = new InvertedLootCondition(builder.build());
		return () -> invertedLootCondition;
	}

	public static class Factory extends class_4570.Factory<InvertedLootCondition> {
		public Factory() {
			super(new Identifier("inverted"), InvertedLootCondition.class);
		}

		public void method_892(JsonObject jsonObject, InvertedLootCondition invertedLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("term", jsonSerializationContext.serialize(invertedLootCondition.term));
		}

		public InvertedLootCondition method_891(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_4570 lv = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, class_4570.class);
			return new InvertedLootCondition(lv);
		}
	}
}
