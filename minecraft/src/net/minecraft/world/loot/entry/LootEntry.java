package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;

public abstract class LootEntry implements EntryCombiner {
	protected final class_4570[] conditions;
	private final Predicate<LootContext> conditionPredicate;

	protected LootEntry(class_4570[] args) {
		this.conditions = args;
		this.conditionPredicate = LootConditions.joinAnd(args);
	}

	public void check(LootTableReporter lootTableReporter) {
		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].check(lootTableReporter.makeChild(".condition[" + i + "]"));
		}
	}

	protected final boolean test(LootContext lootContext) {
		return this.conditionPredicate.test(lootContext);
	}

	public abstract static class Builder<T extends LootEntry.Builder<T>> implements ConditionConsumerBuilder<T> {
		private final List<class_4570> children = Lists.<class_4570>newArrayList();

		protected abstract T getThisBuilder();

		public T method_421(class_4570.Builder builder) {
			this.children.add(builder.build());
			return this.getThisBuilder();
		}

		public final T method_416() {
			return this.getThisBuilder();
		}

		protected class_4570[] getConditions() {
			return (class_4570[])this.children.toArray(new class_4570[0]);
		}

		public AlternativeEntry.Builder withChild(LootEntry.Builder<?> builder) {
			return new AlternativeEntry.Builder(this, builder);
		}

		public abstract LootEntry build();
	}

	public abstract static class Serializer<T extends LootEntry> {
		private final Identifier id;
		private final Class<T> type;

		protected Serializer(Identifier identifier, Class<T> class_) {
			this.id = identifier;
			this.type = class_;
		}

		public Identifier getIdentifier() {
			return this.id;
		}

		public Class<T> getType() {
			return this.type;
		}

		public abstract void toJson(JsonObject jsonObject, T lootEntry, JsonSerializationContext jsonSerializationContext);

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args);
	}
}
