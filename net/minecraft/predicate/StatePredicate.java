/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public class StatePredicate {
    public static final StatePredicate ANY = new StatePredicate(ImmutableList.of());
    private final List<Condition> conditions;

    private static Condition createPredicate(String string, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            String string2 = jsonElement.getAsString();
            return new ExactValueCondition(string, string2);
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
        String string3 = jsonObject.has("min") ? StatePredicate.asNullableString(jsonObject.get("min")) : null;
        String string4 = jsonObject.has("max") ? StatePredicate.asNullableString(jsonObject.get("max")) : null;
        return string3 != null && string3.equals(string4) ? new ExactValueCondition(string, string3) : new RangedValueCondition(string, string3, string4);
    }

    @Nullable
    private static String asNullableString(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        }
        return jsonElement.getAsString();
    }

    private StatePredicate(List<Condition> list) {
        this.conditions = ImmutableList.copyOf(list);
    }

    public <S extends State<S>> boolean test(StateManager<?, S> stateManager, S state) {
        for (Condition condition : this.conditions) {
            if (condition.test(stateManager, state)) continue;
            return false;
        }
        return true;
    }

    public boolean test(BlockState blockState) {
        return this.test(blockState.getBlock().getStateManager(), blockState);
    }

    public boolean test(FluidState fluidState) {
        return this.test(fluidState.getFluid().getStateManager(), fluidState);
    }

    public void check(StateManager<?, ?> stateManager, Consumer<String> consumer) {
        this.conditions.forEach(condition -> condition.reportMissing(stateManager, consumer));
    }

    public static StatePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "properties");
        ArrayList<Condition> list = Lists.newArrayList();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            list.add(StatePredicate.createPredicate(entry.getKey(), entry.getValue()));
        }
        return new StatePredicate(list);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!this.conditions.isEmpty()) {
            this.conditions.forEach(condition -> jsonObject.add(condition.getKey(), condition.toJson()));
        }
        return jsonObject;
    }

    public static class Builder {
        private final List<Condition> conditons = Lists.newArrayList();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder exactMatch(Property<?> property, String string) {
            this.conditons.add(new ExactValueCondition(property.getName(), string));
            return this;
        }

        public Builder exactMatch(Property<Integer> property, int i) {
            return this.exactMatch((Property)property, (Comparable<T> & StringIdentifiable)Integer.toString(i));
        }

        public Builder exactMatch(Property<Boolean> property, boolean bl) {
            return this.exactMatch((Property)property, (Comparable<T> & StringIdentifiable)Boolean.toString(bl));
        }

        public <T extends Comparable<T> & StringIdentifiable> Builder exactMatch(Property<T> property, T comparable) {
            return this.exactMatch(property, (T)((StringIdentifiable)comparable).asString());
        }

        public StatePredicate build() {
            return new StatePredicate(this.conditons);
        }
    }

    static class RangedValueCondition
    extends Condition {
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
            Optional<T> optional;
            T comparable = state.get(property);
            if (!(this.min == null || (optional = property.parse(this.min)).isPresent() && comparable.compareTo(optional.get()) >= 0)) {
                return false;
            }
            return this.max == null || (optional = property.parse(this.max)).isPresent() && comparable.compareTo(optional.get()) <= 0;
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

    static class ExactValueCondition
    extends Condition {
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

    static abstract class Condition {
        private final String key;

        public Condition(String string) {
            this.key = string;
        }

        public <S extends State<S>> boolean test(StateManager<?, S> stateManager, S state) {
            Property<?> property = stateManager.getProperty(this.key);
            if (property == null) {
                return false;
            }
            return this.test(state, property);
        }

        protected abstract <T extends Comparable<T>> boolean test(State<?> var1, Property<T> var2);

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
}

