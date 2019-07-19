package net.minecraft.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class AlternativeLootCondition implements LootCondition {
	private final LootCondition[] terms;
	private final Predicate<LootContext> predicate;

	private AlternativeLootCondition(LootCondition[] terms) {
		this.terms = terms;
		this.predicate = LootConditions.joinOr(terms);
	}

	public final boolean test(LootContext lootContext) {
		return this.predicate.test(lootContext);
	}

	@Override
	public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
		LootCondition.super.check(reporter, supplierGetter, parentLootTables, contextType);

		for (int i = 0; i < this.terms.length; i++) {
			this.terms[i].check(reporter.makeChild(".term[" + i + "]"), supplierGetter, parentLootTables, contextType);
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

		public void toJson(JsonObject jsonObject, AlternativeLootCondition alternativeLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("terms", jsonSerializationContext.serialize(alternativeLootCondition.terms));
		}

		public AlternativeLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, LootCondition[].class);
			return new AlternativeLootCondition(lootConditions);
		}
	}
}
