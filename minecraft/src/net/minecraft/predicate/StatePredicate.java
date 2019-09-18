package net.minecraft.predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;

public class StatePredicate {
	public static final StatePredicate ANY = new StatePredicate(ImmutableList.of());
	private final List<StatePredicate.Condition> conditions;

	private static StatePredicate.Condition createPredicate(String string, JsonElement jsonElement) {
		if (jsonElement.isJsonPrimitive()) {
			String string2 = jsonElement.getAsString();
			return new StatePredicate.ExactValueCondition(string, string2);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			String string3 = jsonObject.has("min") ? asNullableString(jsonObject.get("min")) : null;
			String string4 = jsonObject.has("max") ? asNullableString(jsonObject.get("max")) : null;
			return (StatePredicate.Condition)(string3 != null && string3.equals(string4)
				? new StatePredicate.ExactValueCondition(string, string3)
				: new StatePredicate.RangedValueCondition(string, string3, string4));
		}
	}

	@Nullable
	private static String asNullableString(JsonElement jsonElement) {
		return jsonElement.isJsonNull() ? null : jsonElement.getAsString();
	}

	private StatePredicate(List<StatePredicate.Condition> list) {
		this.conditions = ImmutableList.copyOf(list);
	}

	public <S extends State<S>> boolean test(StateManager<?, S> stateManager, S state) {
		for (StatePredicate.Condition condition : this.conditions) {
			if (!condition.test(stateManager, state)) {
				return false;
			}
		}

		return true;
	}

	public boolean test(BlockState blockState) {
		return this.test(blockState.getBlock().getStateFactory(), blockState);
	}

	public boolean test(FluidState fluidState) {
		return this.test(fluidState.getFluid().getStateFactory(), fluidState);
	}

	public void check(StateManager<?, ?> stateManager, Consumer<String> consumer) {
		this.conditions.forEach(condition -> condition.reportMissing(stateManager, consumer));
	}

	public static StatePredicate fromJson(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "properties");
			List<StatePredicate.Condition> list = Lists.<StatePredicate.Condition>newArrayList();

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				list.add(createPredicate((String)entry.getKey(), (JsonElement)entry.getValue()));
			}

			return new StatePredicate(list);
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (!this.conditions.isEmpty()) {
				this.conditions.forEach(condition -> jsonObject.add(condition.getKey(), condition.toJson()));
			}

			return jsonObject;
		}
	}

	public static class Builder {
		private final List<StatePredicate.Condition> conditons = Lists.<StatePredicate.Condition>newArrayList();

		private Builder() {
		}

		public static StatePredicate.Builder create() {
			return new StatePredicate.Builder();
		}

		public StatePredicate.Builder exactMatch(Property<?> property, String string) {
			this.conditons.add(new StatePredicate.ExactValueCondition(property.getName(), string));
			return this;
		}

		public StatePredicate.Builder exactMatch(Property<Integer> property, int i) {
			return this.exactMatch(property, Integer.toString(i));
		}

		public StatePredicate.Builder exactMatch(Property<Boolean> property, boolean bl) {
			return this.exactMatch(property, Boolean.toString(bl));
		}

		public <T extends Comparable<T> & StringIdentifiable> StatePredicate.Builder exactMatch(Property<T> property, T comparable) {
			return this.exactMatch(property, comparable.asString());
		}

		public StatePredicate build() {
			return new StatePredicate(this.conditons);
		}
	}

	abstract static class Condition {
		private final String key;

		public Condition(String string) {
			this.key = string;
		}

		public <S extends State<S>> boolean test(StateManager<?, S> stateManager, S state) {
			Property<?> property = stateManager.getProperty(this.key);
			return property == null ? false : this.test(state, property);
		}

		protected abstract <T extends Comparable<T>> boolean test(State<?> state, Property<T> property);

		public abstract JsonElement toJson();

		public String getKey() {
			return this.key;
		}

		public void reportMissing(StateManager<?, ?> stateManager, Consumer<String> consumer) {
			Property<?> property = stateManager.getProperty(this.key);
			if (property == null) {
				consumer.accept(this.key);
			}
		}
	}

	static class ExactValueCondition extends StatePredicate.Condition {
		private final String value;

		public ExactValueCondition(String string, String string2) {
			super(string);
			this.value = string2;
		}

		@Override
		protected <T extends Comparable<T>> boolean test(State<?> state, Property<T> property) {
			T comparable = state.get(property);
			Optional<T> optional = property.parse(this.value);
			return optional.isPresent() && comparable.compareTo(optional.get()) == 0;
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive(this.value);
		}
	}

	static class RangedValueCondition extends StatePredicate.Condition {
		@Nullable
		private final String min;
		@Nullable
		private final String max;

		public RangedValueCondition(String string, @Nullable String string2, @Nullable String string3) {
			super(string);
			this.min = string2;
			this.max = string3;
		}

		@Override
		protected <T extends Comparable<T>> boolean test(State<?> state, Property<T> property) {
			T comparable = state.get(property);
			if (this.min != null) {
				Optional<T> optional = property.parse(this.min);
				if (!optional.isPresent() || comparable.compareTo(optional.get()) < 0) {
					return false;
				}
			}

			if (this.max != null) {
				Optional<T> optional = property.parse(this.max);
				if (!optional.isPresent() || comparable.compareTo(optional.get()) > 0) {
					return false;
				}
			}

			return true;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.min != null) {
				jsonObject.addProperty("min", this.min);
			}

			if (this.max != null) {
				jsonObject.addProperty("max", this.max);
			}

			return jsonObject;
		}
	}
}
