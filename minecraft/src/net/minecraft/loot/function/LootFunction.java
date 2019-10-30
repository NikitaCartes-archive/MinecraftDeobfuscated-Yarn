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

public interface LootFunction extends LootContextAware, BiFunction<ItemStack, LootContext, ItemStack> {
	static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> itemDropper, LootContext context) {
		return stack -> itemDropper.accept(itemApplier.apply(stack, context));
	}

	public interface Builder {
		LootFunction build();
	}

	public abstract static class Factory<T extends LootFunction> {
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

		public abstract void toJson(JsonObject json, T function, JsonSerializationContext context);

		public abstract T fromJson(JsonObject json, JsonDeserializationContext context);
	}
}
