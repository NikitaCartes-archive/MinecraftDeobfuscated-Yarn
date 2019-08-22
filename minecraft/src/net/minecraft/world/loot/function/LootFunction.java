package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.ParameterConsumer;

public interface LootFunction extends ParameterConsumer, BiFunction<ItemStack, LootContext, ItemStack> {
	static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> biFunction, Consumer<ItemStack> consumer, LootContext lootContext) {
		return itemStack -> consumer.accept(biFunction.apply(itemStack, lootContext));
	}

	public interface Builder {
		LootFunction build();
	}

	public abstract static class Factory<T extends LootFunction> {
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

		public abstract void toJson(JsonObject jsonObject, T lootFunction, JsonSerializationContext jsonSerializationContext);

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}
}
