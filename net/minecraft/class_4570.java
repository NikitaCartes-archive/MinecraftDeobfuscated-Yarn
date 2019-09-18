/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
public interface class_4570
extends ParameterConsumer,
Predicate<LootContext> {
    public static final class_4570 field_20766 = lootContext -> false;

    public static abstract class Factory<T extends class_4570> {
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

        public abstract void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2);
    }

    @FunctionalInterface
    public static interface Builder {
        public class_4570 build();

        default public Builder invert() {
            return InvertedLootCondition.builder(this);
        }

        default public AlternativeLootCondition.Builder withCondition(Builder builder) {
            return AlternativeLootCondition.builder(this, builder);
        }
    }
}

