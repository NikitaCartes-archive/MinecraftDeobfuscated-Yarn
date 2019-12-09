/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.util.Identifier;

public interface LootFunction
extends LootContextAware,
BiFunction<ItemStack, LootContext, ItemStack> {
    public static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> itemDropper, LootContext context) {
        return stack -> itemDropper.accept((ItemStack)itemApplier.apply((ItemStack)stack, context));
    }

    public static abstract class Factory<T extends LootFunction> {
        private final Identifier id;
        private final Class<T> functionClass;

        protected Factory(Identifier id, Class<T> clazz) {
            this.id = id;
            this.functionClass = clazz;
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

