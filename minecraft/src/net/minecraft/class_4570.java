package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.AlternativeLootCondition;
import net.minecraft.world.loot.condition.InvertedLootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.ParameterConsumer;

@FunctionalInterface
public interface class_4570 extends ParameterConsumer, Predicate<LootContext> {
	class_4570 field_20766 = lootContext -> false;

	@FunctionalInterface
	public interface Builder {
		class_4570 build();

		default class_4570.Builder invert() {
			return InvertedLootCondition.builder(this);
		}

		default AlternativeLootCondition.Builder withCondition(class_4570.Builder builder) {
			return AlternativeLootCondition.builder(this, builder);
		}
	}

	public abstract static class Factory<T extends class_4570> {
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

		public abstract void toJson(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}
}
