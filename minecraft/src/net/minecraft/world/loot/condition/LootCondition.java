package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.ParameterConsumer;

@FunctionalInterface
public interface LootCondition extends ParameterConsumer, Predicate<LootContext> {
	@FunctionalInterface
	public interface Builder {
		LootCondition build();

		default LootCondition.Builder invert() {
			return InvertedLootCondition.create(this);
		}

		default AlternativeLootCondition.Builder or(LootCondition.Builder builder) {
			return AlternativeLootCondition.or(this, builder);
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
