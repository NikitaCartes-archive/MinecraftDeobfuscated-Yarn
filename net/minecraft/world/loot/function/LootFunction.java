/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextAware;

public interface LootFunction
extends LootContextAware,
BiFunction<ItemStack, LootContext, ItemStack> {
    public static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> biFunction, Consumer<ItemStack> consumer, LootContext lootContext) {
        return itemStack -> consumer.accept((ItemStack)biFunction.apply((ItemStack)itemStack, lootContext));
    }

    public static abstract class Factory<T extends LootFunction> {
        private final Identifier id;
        private final Class<T> functionClass;

        protected Factory(Identifier identifier, Class<T> class_) {
            this.id = identifier;
            this.functionClass = class_;
        }

        public Identifier getId() {
            return this.id;
        }

        public Class<T> getFunctionClass() {
            return this.functionClass;
        }

        public abstract void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2);
    }

    public static interface Builder {
        public LootFunction build();
    }
}

