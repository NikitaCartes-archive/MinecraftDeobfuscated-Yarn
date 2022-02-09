/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class VariantSetting<T> {
    final String key;
    final Function<T, JsonElement> writer;

    public VariantSetting(String key, Function<T, JsonElement> writer) {
        this.key = key;
        this.writer = writer;
    }

    public Value evaluate(T value) {
        return new Value(value);
    }

    public String toString() {
        return this.key;
    }

    public class Value {
        private final T value;

        public Value(T value) {
            this.value = value;
        }

        public VariantSetting<T> getParent() {
            return VariantSetting.this;
        }

        public void writeTo(JsonObject json) {
            json.add(VariantSetting.this.key, VariantSetting.this.writer.apply(this.value));
        }

        public String toString() {
            return VariantSetting.this.key + "=" + this.value;
        }
    }
}

