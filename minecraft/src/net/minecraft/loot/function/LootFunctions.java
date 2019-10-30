package net.minecraft.loot.function;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootFunctions {
	private static final Map<Identifier, LootFunction.Factory<?>> byId = Maps.<Identifier, LootFunction.Factory<?>>newHashMap();
	private static final Map<Class<? extends LootFunction>, LootFunction.Factory<?>> byClass = Maps.<Class<? extends LootFunction>, LootFunction.Factory<?>>newHashMap();
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;

	public static <T extends LootFunction> void register(LootFunction.Factory<? extends T> function) {
		Identifier identifier = function.getId();
		Class<T> class_ = (Class<T>)function.getFunctionClass();
		if (byId.containsKey(identifier)) {
			throw new IllegalArgumentException("Can't re-register item function name " + identifier);
		} else if (byClass.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item function class " + class_.getName());
		} else {
			byId.put(identifier, function);
			byClass.put(class_, function);
		}
	}

	public static LootFunction.Factory<?> get(Identifier id) {
		LootFunction.Factory<?> factory = (LootFunction.Factory<?>)byId.get(id);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown loot item function '" + id + "'");
		} else {
			return factory;
		}
	}

	public static <T extends LootFunction> LootFunction.Factory<T> getFactory(T function) {
		LootFunction.Factory<T> factory = (LootFunction.Factory<T>)byClass.get(function.getClass());
		if (factory == null) {
			throw new IllegalArgumentException("Unknown loot item function " + function);
		} else {
			return factory;
		}
	}

	public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] lootFunctions) {
		switch (lootFunctions.length) {
			case 0:
				return NOOP;
			case 1:
				return lootFunctions[0];
			case 2:
				BiFunction<ItemStack, LootContext, ItemStack> biFunction = lootFunctions[0];
				BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = lootFunctions[1];
				return (stack, context) -> (ItemStack)biFunction2.apply(biFunction.apply(stack, context), context);
			default:
				return (stack, context) -> {
					for (BiFunction<ItemStack, LootContext, ItemStack> biFunctionx : lootFunctions) {
						stack = (ItemStack)biFunctionx.apply(stack, context);
					}

					return stack;
				};
		}
	}

	static {
		register(new SetCountLootFunction.Factory());
		register(new EnchantWithLevelsLootFunction.Factory());
		register(new EnchantRandomlyLootFunction.Factory());
		register(new SetNbtLootFunction.Builder());
		register(new FurnaceSmeltLootFunction.Factory());
		register(new LootingEnchantLootFunction.Factory());
		register(new SetDamageLootFunction.Factory());
		register(new SetAttributesLootFunction.Factory());
		register(new SetNameLootFunction.Factory());
		register(new ExplorationMapLootFunction.Factory());
		register(new SetStewEffectLootFunction.Factory());
		register(new CopyNameLootFunction.Factory());
		register(new SetContentsLootFunction.Factory());
		register(new LimitCountLootFunction.Factory());
		register(new ApplyBonusLootFunction.Factory());
		register(new SetLootTableLootFunction.Factory());
		register(new ExplosionDecayLootFunction.Factory());
		register(new SetLoreLootFunction.Factory());
		register(new FillPlayerHeadLootFunction.Factory());
		register(new CopyNbtLootFunction.Factory());
		register(new CopyStateFunction.Factory());
	}

	public static class Factory implements JsonDeserializer<LootFunction>, JsonSerializer<LootFunction> {
		public LootFunction method_596(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "function");
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "function"));

			LootFunction.Factory<?> factory;
			try {
				factory = LootFunctions.get(identifier);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown function '" + identifier + "'");
			}

			return factory.fromJson(jsonObject, jsonDeserializationContext);
		}

		public JsonElement method_597(LootFunction lootFunction, Type type, JsonSerializationContext jsonSerializationContext) {
			LootFunction.Factory<LootFunction> factory = LootFunctions.getFactory(lootFunction);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("function", factory.getId().toString());
			factory.toJson(jsonObject, lootFunction, jsonSerializationContext);
			return jsonObject;
		}
	}
}
