package net.minecraft.world.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;

public class AlternativeLootCondition implements class_4570 {
	private final class_4570[] terms;
	private final Predicate<LootContext> predicate;

	private AlternativeLootCondition(class_4570[] args) {
		this.terms = args;
		this.predicate = LootConditions.joinOr(args);
	}

	public final boolean method_825(LootContext lootContext) {
		return this.predicate.test(lootContext);
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		class_4570.super.check(lootTableReporter);

		for (int i = 0; i < this.terms.length; i++) {
			this.terms[i].check(lootTableReporter.makeChild(".term[" + i + "]"));
		}
	}

	public static AlternativeLootCondition.Builder builder(class_4570.Builder... builders) {
		return new AlternativeLootCondition.Builder(builders);
	}

	public static class Builder implements class_4570.Builder {
		private final List<class_4570> terms = Lists.<class_4570>newArrayList();

		public Builder(class_4570.Builder... builders) {
			for (class_4570.Builder builder : builders) {
				this.terms.add(builder.build());
			}
		}

		@Override
		public AlternativeLootCondition.Builder withCondition(class_4570.Builder builder) {
			this.terms.add(builder.build());
			return this;
		}

		@Override
		public class_4570 build() {
			return new AlternativeLootCondition((class_4570[])this.terms.toArray(new class_4570[0]));
		}
	}

	public static class Factory extends class_4570.Factory<AlternativeLootCondition> {
		public Factory() {
			super(new Identifier("alternative"), AlternativeLootCondition.class);
		}

		public void method_828(JsonObject jsonObject, AlternativeLootCondition alternativeLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("terms", jsonSerializationContext.serialize(alternativeLootCondition.terms));
		}

		public AlternativeLootCondition method_829(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_4570[] lvs = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, class_4570[].class);
			return new AlternativeLootCondition(lvs);
		}
	}
}
