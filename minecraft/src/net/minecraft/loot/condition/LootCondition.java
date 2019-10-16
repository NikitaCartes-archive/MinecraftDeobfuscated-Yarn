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
	LootCondition ALWAYS_FALSE = lootContext -> false;

	@FunctionalInterface
	public interface Builder {
		LootCondition build();

		default LootCondition.Builder invert() {
			return InvertedLootCondition.builder(this);
		}

		default AlternativeLootCondition.Builder withCondition(LootCondition.Builder builder) {
			return AlternativeLootCondition.builder(this, builder);
		}
	}

	public abstract static class Factory<T extends LootCondition> {
		private final Identifier id;
		private final Class<T> conditionClass;

		protected Factory(Identifier identifier, Class<T> class_) {
			this.id = identifier;
			this.conditionClass = class_;
		}

		public Identifier getId() {
			return this.id;
		}

		public Class<T> getConditionClass() {
			return this.conditionClass;
		}

		public abstract void toJson(JsonObject jsonObject, T lootCondition, JsonSerializationContext jsonSerializationContext);

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}
}
