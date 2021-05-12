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

	private static StatePredicate.Condition createPredicate(String key, JsonElement json) {
		if (json.isJsonPrimitive()) {
			String string = json.getAsString();
			return new StatePredicate.ExactValueCondition(key, string);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(json, "value");
			String string2 = jsonObject.has("min") ? asNullableString(jsonObject.get("min")) : null;
			String string3 = jsonObject.has("max") ? asNullableString(jsonObject.get("max")) : null;
			return (StatePredicate.Condition)(string2 != null && string2.equals(string3)
				? new StatePredicate.ExactValueCondition(key, string2)
				: new StatePredicate.RangedValueCondition(key, string2, string3));
		}
	}

	@Nullable
	private static String asNullableString(JsonElement json) {
		return json.isJsonNull() ? null : json.getAsString();
	}

	StatePredicate(List<StatePredicate.Condition> conditions) {
		this.conditions = ImmutableList.copyOf(conditions);
	}

	public <S extends State<?, S>> boolean test(StateManager<?, S> stateManager, S container) {
		for (StatePredicate.Condition condition : this.conditions) {
			if (!condition.test(stateManager, container)) {
				return false;
			}
		}

		return true;
	}

	public boolean test(BlockState state) {
		return this.test(state.getBlock().getStateManager(), state);
	}

	public boolean test(FluidState state) {
		return this.test(state.getFluid().getStateManager(), state);
	}

	public void check(StateManager<?, ?> factory, Consumer<String> reporter) {
		this.conditions.forEach(condition -> condition.reportMissing(factory, reporter));
	}

	public static StatePredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "properties");
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
		private final List<StatePredicate.Condition> conditions = Lists.<StatePredicate.Condition>newArrayList();

		private Builder() {
		}

		public static StatePredicate.Builder create() {
			return new StatePredicate.Builder();
		}

		public StatePredicate.Builder exactMatch(Property<?> property, String valueName) {
			this.conditions.add(new StatePredicate.ExactValueCondition(property.getName(), valueName));
			return this;
		}

		public StatePredicate.Builder exactMatch(Property<Integer> property, int value) {
			return this.exactMatch(property, Integer.toString(value));
		}

		public StatePredicate.Builder exactMatch(Property<Boolean> property, boolean value) {
			return this.exactMatch(property, Boolean.toString(value));
		}

		public <T extends Comparable<T> & StringIdentifiable> StatePredicate.Builder exactMatch(Property<T> property, T value) {
			return this.exactMatch(property, value.asString());
		}

		public StatePredicate build() {
			return new StatePredicate(this.conditions);
		}
	}

	abstract static class Condition {
		private final String key;

		public Condition(String key) {
			this.key = key;
		}

		public <S extends State<?, S>> boolean test(StateManager<?, S> stateManager, S state) {
			Property<?> property = stateManager.getProperty(this.key);
			return property == null ? false : this.test(state, property);
		}

		protected abstract <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property);

		public abstract JsonElement toJson();

		public String getKey() {
			return this.key;
		}

		public void reportMissing(StateManager<?, ?> factory, Consumer<String> reporter) {
			Property<?> property = factory.getProperty(this.key);
			if (property == null) {
				reporter.accept(this.key);
			}
		}
	}

	static class ExactValueCondition extends StatePredicate.Condition {
		private final String value;

		public ExactValueCondition(String key, String value) {
			super(key);
			this.value = value;
		}

		@Override
		protected <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property) {
			T comparable = state.get(property);
			Optional<T> optional = property.parse(this.value);
			return optional.isPresent() && comparable.compareTo((Comparable)optional.get()) == 0;
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

		public RangedValueCondition(String key, @Nullable String min, @Nullable String max) {
			super(key);
			this.min = min;
			this.max = max;
		}

		@Override
		protected <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property) {
			T comparable = state.get(property);
			if (this.min != null) {
				Optional<T> optional = property.parse(this.min);
				if (!optional.isPresent() || comparable.compareTo((Comparable)optional.get()) < 0) {
					return false;
				}
			}

			if (this.max != null) {
				Optional<T> optional = property.parse(this.max);
				if (!optional.isPresent() || comparable.compareTo((Comparable)optional.get()) > 0) {
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
