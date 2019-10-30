package net.minecraft.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AlternativeLootCondition implements LootCondition {
	private final LootCondition[] terms;
	private final Predicate<LootContext> predicate;

	private AlternativeLootCondition(LootCondition[] terms) {
		this.terms = terms;
		this.predicate = LootConditions.joinOr(terms);
	}

	public final boolean method_825(LootContext lootContext) {
		return this.predicate.test(lootContext);
	}

	@Override
	public void check(LootTableReporter reporter) {
		LootCondition.super.check(reporter);

		for (int i = 0; i < this.terms.length; i++) {
			this.terms[i].check(reporter.makeChild(".term[" + i + "]"));
		}
	}

	public static AlternativeLootCondition.Builder builder(LootCondition.Builder... terms) {
		return new AlternativeLootCondition.Builder(terms);
	}

	public static class Builder implements LootCondition.Builder {
		private final List<LootCondition> terms = Lists.<LootCondition>newArrayList();

		public Builder(LootCondition.Builder... terms) {
			for (LootCondition.Builder builder : terms) {
				this.terms.add(builder.build());
			}
		}

		@Override
		public AlternativeLootCondition.Builder withCondition(LootCondition.Builder condition) {
			this.terms.add(condition.build());
			return this;
		}

		@Override
		public LootCondition build() {
			return new AlternativeLootCondition((LootCondition[])this.terms.toArray(new LootCondition[0]));
		}
	}

	public static class Factory extends LootCondition.Factory<AlternativeLootCondition> {
		public Factory() {
			super(new Identifier("alternative"), AlternativeLootCondition.class);
		}

		public void method_828(JsonObject jsonObject, AlternativeLootCondition alternativeLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("terms", jsonSerializationContext.serialize(alternativeLootCondition.terms));
		}

		public AlternativeLootCondition method_829(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, LootCondition[].class);
			return new AlternativeLootCondition(lootConditions);
		}
	}
}
