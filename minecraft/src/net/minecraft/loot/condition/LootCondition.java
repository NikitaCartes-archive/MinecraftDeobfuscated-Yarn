package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootCondition extends LootContextAware, Predicate<LootContext> {
	@FunctionalInterface
	public interface Builder {
		LootCondition build();

		default LootCondition.Builder invert() {
			return InvertedLootCondition.builder(this);
		}

		default AlternativeLootCondition.Builder or(LootCondition.Builder condition) {
			return AlternativeLootCondition.builder(this, condition);
		}
	}

	public abstract static class Factory<T extends LootCondition> {
		private final Identifier id;
		private final Class<T> conditionClass;

		protected Factory(Identifier id, Class<T> clazz) {
			this.id = id;
			this.conditionClass = clazz;
		}

		public Identifier getId() {
			return this.id;
		}

		public Class<T> getConditionClass() {
			return this.conditionClass;
		}

		public abstract void toJson(JsonObject json, T condition, JsonSerializationContext context);

		public abstract T fromJson(JsonObject json, JsonDeserializationContext context);
	}
}
