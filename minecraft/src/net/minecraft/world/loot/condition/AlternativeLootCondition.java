package net.minecraft.world.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;

public class AlternativeLootCondition implements LootCondition {
	private final LootCondition[] terms;
	private final Predicate<LootContext> predicate;

	private AlternativeLootCondition(LootCondition[] lootConditions) {
		this.terms = lootConditions;
		this.predicate = LootConditions.joinOr(lootConditions);
	}

	public final boolean method_825(LootContext lootContext) {
		return this.predicate.test(lootContext);
	}

	@Override
	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		LootCondition.super.check(lootTableReporter, function, set, lootContextType);

		for (int i = 0; i < this.terms.length; i++) {
			this.terms[i].check(lootTableReporter.makeChild(".term[" + i + "]"), function, set, lootContextType);
		}
	}

	public static AlternativeLootCondition.Builder or(LootCondition.Builder... builders) {
		return new AlternativeLootCondition.Builder(builders);
	}

	public static class Builder implements LootCondition.Builder {
		private final List<LootCondition> field_1248 = Lists.<LootCondition>newArrayList();

		public Builder(LootCondition.Builder... builders) {
			for (LootCondition.Builder builder : builders) {
				this.field_1248.add(builder.build());
			}
		}

		@Override
		public AlternativeLootCondition.Builder or(LootCondition.Builder builder) {
			this.field_1248.add(builder.build());
			return this;
		}

		@Override
		public LootCondition build() {
			return new AlternativeLootCondition((LootCondition[])this.field_1248.toArray(new LootCondition[0]));
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
