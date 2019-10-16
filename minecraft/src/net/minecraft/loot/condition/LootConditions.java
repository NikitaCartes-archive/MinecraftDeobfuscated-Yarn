package net.minecraft.loot.condition;

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
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootConditions {
	private static final Map<Identifier, LootCondition.Factory<?>> byId = Maps.<Identifier, LootCondition.Factory<?>>newHashMap();
	private static final Map<Class<? extends LootCondition>, LootCondition.Factory<?>> byClass = Maps.<Class<? extends LootCondition>, LootCondition.Factory<?>>newHashMap();

	public static <T extends LootCondition> void register(LootCondition.Factory<? extends T> factory) {
		Identifier identifier = factory.getId();
		Class<T> class_ = (Class<T>)factory.getConditionClass();
		if (byId.containsKey(identifier)) {
			throw new IllegalArgumentException("Can't re-register item condition name " + identifier);
		} else if (byClass.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item condition class " + class_.getName());
		} else {
			byId.put(identifier, factory);
			byClass.put(class_, factory);
		}
	}

	public static LootCondition.Factory<?> get(Identifier identifier) {
		LootCondition.Factory<?> factory = (LootCondition.Factory<?>)byId.get(identifier);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown loot item condition '" + identifier + "'");
		} else {
			return factory;
		}
	}

	public static <T extends LootCondition> LootCondition.Factory<T> getFactory(T lootCondition) {
		LootCondition.Factory<T> factory = (LootCondition.Factory<T>)byClass.get(lootCondition.getClass());
		if (factory == null) {
			throw new IllegalArgumentException("Unknown loot item condition " + lootCondition);
		} else {
			return factory;
		}
	}

	public static <T> Predicate<T> joinAnd(Predicate<T>[] predicates) {
		switch (predicates.length) {
			case 0:
				return object -> true;
			case 1:
				return predicates[0];
			case 2:
				return predicates[0].and(predicates[1]);
			default:
				return object -> {
					for (Predicate<T> predicate : predicates) {
						if (!predicate.test(object)) {
							return false;
						}
					}

					return true;
				};
		}
	}

	public static <T> Predicate<T> joinOr(Predicate<T>[] predicates) {
		switch (predicates.length) {
			case 0:
				return object -> false;
			case 1:
				return predicates[0];
			case 2:
				return predicates[0].or(predicates[1]);
			default:
				return object -> {
					for (Predicate<T> predicate : predicates) {
						if (predicate.test(object)) {
							return true;
						}
					}

					return false;
				};
		}
	}

	static {
		register(new InvertedLootCondition.Factory());
		register(new AlternativeLootCondition.Factory());
		register(new RandomChanceLootCondition.Factory());
		register(new RandomChanceWithLootingLootCondition.Factory());
		register(new EntityPropertiesLootCondition.Factory());
		register(new KilledByPlayerLootCondition.Factory());
		register(new EntityScoresLootCondition.Factory());
		register(new BlockStatePropertyLootCondition.Factory());
		register(new MatchToolLootCondition.Factory());
		register(new TableBonusLootCondition.Factory());
		register(new SurvivesExplosionLootCondition.Factory());
		register(new DamageSourcePropertiesLootCondition.Factory());
		register(new LocationCheckLootCondition.Factory());
		register(new WeatherCheckLootCondition.Factory());
		register(new ReferenceLootCondition.Factory());
		register(new TimeCheckLootCondition.Factory());
	}

	public static class Factory implements JsonDeserializer<LootCondition>, JsonSerializer<LootCondition> {
		public LootCondition method_930(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "condition");
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "condition"));

			LootCondition.Factory<?> factory;
			try {
				factory = LootConditions.get(identifier);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown condition '" + identifier + "'");
			}

			return factory.fromJson(jsonObject, jsonDeserializationContext);
		}

		public JsonElement method_931(LootCondition lootCondition, Type type, JsonSerializationContext jsonSerializationContext) {
			LootCondition.Factory<LootCondition> factory = LootConditions.getFactory(lootCondition);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("condition", factory.getId().toString());
			factory.toJson(jsonObject, lootCondition, jsonSerializationContext);
			return jsonObject;
		}
	}
}
