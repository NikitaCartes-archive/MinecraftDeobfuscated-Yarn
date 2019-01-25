package net.minecraft.world.loot.function;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.context.LootContext;

public class LootFunctions {
	private static final Map<Identifier, LootFunction.Factory<?>> byId = Maps.<Identifier, LootFunction.Factory<?>>newHashMap();
	private static final Map<Class<? extends LootFunction>, LootFunction.Factory<?>> byClass = Maps.<Class<? extends LootFunction>, LootFunction.Factory<?>>newHashMap();
	public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (itemStack, lootContext) -> itemStack;

	public static <T extends LootFunction> void register(LootFunction.Factory<? extends T> factory) {
		Identifier identifier = factory.getId();
		Class<T> class_ = (Class<T>)factory.getFunctionClass();
		if (byId.containsKey(identifier)) {
			throw new IllegalArgumentException("Can't re-register item function name " + identifier);
		} else if (byClass.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item function class " + class_.getName());
		} else {
			byId.put(identifier, factory);
			byClass.put(class_, factory);
		}
	}

	public static LootFunction.Factory<?> get(Identifier identifier) {
		LootFunction.Factory<?> factory = (LootFunction.Factory<?>)byId.get(identifier);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown loot item function '" + identifier + "'");
		} else {
			return factory;
		}
	}

	public static <T extends LootFunction> LootFunction.Factory<T> getFactory(T lootFunction) {
		LootFunction.Factory<T> factory = (LootFunction.Factory<T>)byClass.get(lootFunction.getClass());
		if (factory == null) {
			throw new IllegalArgumentException("Unknown loot item function " + lootFunction);
		} else {
			return factory;
		}
	}

	public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] biFunctions) {
		switch (biFunctions.length) {
			case 0:
				return NOOP;
			case 1:
				return biFunctions[0];
			case 2:
				BiFunction<ItemStack, LootContext, ItemStack> biFunction = biFunctions[0];
				BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = biFunctions[1];
				return (itemStack, lootContext) -> (ItemStack)biFunction2.apply(biFunction.apply(itemStack, lootContext), lootContext);
			default:
				return (itemStack, lootContext) -> {
					for (BiFunction<ItemStack, LootContext, ItemStack> biFunctionx : biFunctions) {
						itemStack = (ItemStack)biFunctionx.apply(itemStack, lootContext);
					}

					return itemStack;
				};
		}
	}

	static {
		register(new SetCountLootFunction.Factory());
		register(new EnchantWithLevelsLootFunction.Factory());
		register(new EnchantRandomlyLootFunction.Factory());
		register(new SetTagLootFunction.Factory());
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
	}

	public static class Factory implements JsonDeserializer<LootFunction>, JsonSerializer<LootFunction> {
		public LootFunction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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

		public JsonElement serialize(LootFunction lootFunction, Type type, JsonSerializationContext jsonSerializationContext) {
			LootFunction.Factory<LootFunction> factory = LootFunctions.getFactory(lootFunction);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("function", factory.getId().toString());
			factory.toJson(jsonObject, lootFunction, jsonSerializationContext);
			return jsonObject;
		}
	}
}
