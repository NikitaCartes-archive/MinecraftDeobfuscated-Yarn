/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public class class_4559 {
    public static final class_4559 field_20736 = new class_4559(ImmutableList.of());
    private final List<class_4562> field_20737;

    private static class_4562 method_22521(String string, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            String string2 = jsonElement.getAsString();
            return new class_4561(string, string2);
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
        String string3 = jsonObject.has("min") ? class_4559.method_22522(jsonObject.get("min")) : null;
        String string4 = jsonObject.has("max") ? class_4559.method_22522(jsonObject.get("max")) : null;
        return string3 != null && string3.equals(string4) ? new class_4561(string, string3) : new class_4563(string, string3, string4);
    }

    @Nullable
    private static String method_22522(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        }
        return jsonElement.getAsString();
    }

    private class_4559(List<class_4562> list) {
        this.field_20737 = ImmutableList.copyOf(list);
    }

    public <S extends PropertyContainer<S>> boolean method_22515(StateFactory<?, S> stateFactory, S propertyContainer) {
        for (class_4562 lv : this.field_20737) {
            if (lv.method_22530(stateFactory, propertyContainer)) continue;
            return false;
        }
        return true;
    }

    public boolean method_22514(BlockState blockState) {
        return this.method_22515(blockState.getBlock().getStateFactory(), blockState);
    }

    public boolean method_22518(FluidState fluidState) {
        return this.method_22515(fluidState.getFluid().getStateFactory(), fluidState);
    }

    public void method_22516(StateFactory<?, ?> stateFactory, Consumer<String> consumer) {
        this.field_20737.forEach(arg -> arg.method_22531(stateFactory, consumer));
    }

    public static class_4559 method_22519(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return field_20736;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "properties");
        ArrayList<class_4562> list = Lists.newArrayList();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            list.add(class_4559.method_22521(entry.getKey(), entry.getValue()));
        }
        return new class_4559(list);
    }

    public JsonElement method_22513() {
        if (this == field_20736) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!this.field_20737.isEmpty()) {
            this.field_20737.forEach(arg -> jsonObject.add(arg.method_22533(), arg.method_22529()));
        }
        return jsonObject;
    }

    public static class class_4560 {
        private final List<class_4562> field_20738 = Lists.newArrayList();

        private class_4560() {
        }

        public static class_4560 method_22523() {
            return new class_4560();
        }

        public class_4560 method_22526(Property<?> property, String string) {
            this.field_20738.add(new class_4561(property.getName(), string));
            return this;
        }

        public class_4560 method_22524(Property<Integer> property, int i) {
            return this.method_22526(property, Integer.toString(i));
        }

        public class_4560 method_22527(Property<Boolean> property, boolean bl) {
            return this.method_22526(property, Boolean.toString(bl));
        }

        public <T extends Comparable<T> & StringIdentifiable> class_4560 method_22525(Property<T> property, T comparable) {
            return this.method_22526(property, ((StringIdentifiable)comparable).asString());
        }

        public class_4559 method_22528() {
            return new class_4559(this.field_20738);
        }
    }

    static class class_4563
    extends class_4562 {
        @Nullable
        private final String field_20741;
        @Nullable
        private final String field_20742;

        public class_4563(String string, @Nullable String string2, @Nullable String string3) {
            super(string);
            this.field_20741 = string2;
            this.field_20742 = string3;
        }

        @Override
        protected <T extends Comparable<T>> boolean method_22532(PropertyContainer<?> propertyContainer, Property<T> property) {
            Optional<T> optional;
            T comparable = propertyContainer.get(property);
            if (!(this.field_20741 == null || (optional = property.getValue(this.field_20741)).isPresent() && comparable.compareTo(optional.get()) >= 0)) {
                return false;
            }
            return this.field_20742 == null || (optional = property.getValue(this.field_20742)).isPresent() && comparable.compareTo(optional.get()) <= 0;
        }

        @Override
        public JsonElement method_22529() {
            JsonObject jsonObject = new JsonObject();
            if (this.field_20741 != null) {
                jsonObject.addProperty("min", this.field_20741);
            }
            if (this.field_20742 != null) {
                jsonObject.addProperty("max", this.field_20742);
            }
            return jsonObject;
        }
    }

    static class class_4561
    extends class_4562 {
        private final String field_20739;

        public class_4561(String string, String string2) {
            super(string);
            this.field_20739 = string2;
        }

        @Override
        protected <T extends Comparable<T>> boolean method_22532(PropertyContainer<?> propertyContainer, Property<T> property) {
            T comparable = propertyContainer.get(property);
            Optional<T> optional = property.getValue(this.field_20739);
            return optional.isPresent() && comparable.compareTo(optional.get()) == 0;
        }

        @Override
        public JsonElement method_22529() {
            return new JsonPrimitive(this.field_20739);
        }
    }

    static abstract class class_4562 {
        private final String field_20740;

        public class_4562(String string) {
            this.field_20740 = string;
        }

        public <S extends PropertyContainer<S>> boolean method_22530(StateFactory<?, S> stateFactory, S propertyContainer) {
            Property<?> property = stateFactory.getProperty(this.field_20740);
            if (property == null) {
                return false;
            }
            return this.method_22532(propertyContainer, property);
        }

        protected abstract <T extends Comparable<T>> boolean method_22532(PropertyContainer<?> var1, Property<T> var2);

        public abstract JsonElement method_22529();

        public String method_22533() {
            return this.field_20740;
        }

        public void method_22531(StateFactory<?, ?> stateFactory, Consumer<String> consumer) {
            Property<?> property = stateFactory.getProperty(this.field_20740);
            if (property == null) {
                consumer.accept(this.field_20740);
            }
        }
    }
}

