package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public interface When extends Supplier<JsonElement> {
	void validate(StateManager<?, ?> stateManager);

	static When.PropertyCondition create() {
		return new When.PropertyCondition();
	}

	static When allOf(When... conditions) {
		return new When.LogicalCondition(When.LogicalOperator.AND, Arrays.asList(conditions));
	}

	static When anyOf(When... conditions) {
		return new When.LogicalCondition(When.LogicalOperator.OR, Arrays.asList(conditions));
	}

	public static class LogicalCondition implements When {
		private final When.LogicalOperator operator;
		private final List<When> components;

		LogicalCondition(When.LogicalOperator operator, List<When> components) {
			this.operator = operator;
			this.components = components;
		}

		@Override
		public void validate(StateManager<?, ?> stateManager) {
			this.components.forEach(component -> component.validate(stateManager));
		}

		public JsonElement get() {
			JsonArray jsonArray = new JsonArray();
			this.components.stream().map(Supplier::get).forEach(jsonArray::add);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(this.operator.name, jsonArray);
			return jsonObject;
		}
	}

	public static enum LogicalOperator {
		AND("AND"),
		OR("OR");

		final String name;

		private LogicalOperator(final String name) {
			this.name = name;
		}
	}

	public static class PropertyCondition implements When {
		private final Map<Property<?>, String> properties = Maps.<Property<?>, String>newHashMap();

		private static <T extends Comparable<T>> String name(Property<T> property, Stream<T> valueStream) {
			return (String)valueStream.map(property::name).collect(Collectors.joining("|"));
		}

		private static <T extends Comparable<T>> String name(Property<T> property, T value, T[] otherValues) {
			return name(property, Stream.concat(Stream.of(value), Stream.of(otherValues)));
		}

		private <T extends Comparable<T>> void set(Property<T> property, String value) {
			String string = (String)this.properties.put(property, value);
			if (string != null) {
				throw new IllegalStateException("Tried to replace " + property + " value from " + string + " to " + value);
			}
		}

		public final <T extends Comparable<T>> When.PropertyCondition set(Property<T> property, T value) {
			this.set(property, property.name(value));
			return this;
		}

		@SafeVarargs
		public final <T extends Comparable<T>> When.PropertyCondition set(Property<T> property, T value, T... otherValues) {
			this.set(property, name(property, value, otherValues));
			return this;
		}

		public final <T extends Comparable<T>> When.PropertyCondition setNegated(Property<T> property, T value) {
			this.set(property, "!" + property.name(value));
			return this;
		}

		@SafeVarargs
		public final <T extends Comparable<T>> When.PropertyCondition setNegated(Property<T> property, T value, T... otherValues) {
			this.set(property, "!" + name(property, value, otherValues));
			return this;
		}

		public JsonElement get() {
			JsonObject jsonObject = new JsonObject();
			this.properties.forEach((property, value) -> jsonObject.addProperty(property.getName(), value));
			return jsonObject;
		}

		@Override
		public void validate(StateManager<?, ?> stateManager) {
			List<Property<?>> list = (List<Property<?>>)this.properties
				.keySet()
				.stream()
				.filter(property -> stateManager.getProperty(property.getName()) != property)
				.collect(Collectors.toList());
			if (!list.isEmpty()) {
				throw new IllegalStateException("Properties " + list + " are missing from " + stateManager);
			}
		}
	}
}
