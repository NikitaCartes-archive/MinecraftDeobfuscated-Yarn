package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class class_4553 {
	public static final class_4553 field_20722 = new class_4553.class_4557().method_22507();
	private final NumberRange.IntRange field_20723;
	private final GameMode field_20724;
	private final Map<Stat<?>, NumberRange.IntRange> field_20725;
	private final Object2BooleanMap<Identifier> field_20726;
	private final Map<Identifier, class_4553.class_4556> field_20727;

	private static class_4553.class_4556 method_22503(JsonElement jsonElement) {
		if (jsonElement.isJsonPrimitive()) {
			boolean bl = jsonElement.getAsBoolean();
			return new class_4553.class_4555(bl);
		} else {
			Object2BooleanMap<String> object2BooleanMap = new Object2BooleanOpenHashMap<>();
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "criterion data");
			jsonObject.entrySet().forEach(entry -> {
				boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
				object2BooleanMap.put((String)entry.getKey(), bl);
			});
			return new class_4553.class_4554(object2BooleanMap);
		}
	}

	private class_4553(
		NumberRange.IntRange intRange,
		GameMode gameMode,
		Map<Stat<?>, NumberRange.IntRange> map,
		Object2BooleanMap<Identifier> object2BooleanMap,
		Map<Identifier, class_4553.class_4556> map2
	) {
		this.field_20723 = intRange;
		this.field_20724 = gameMode;
		this.field_20725 = map;
		this.field_20726 = object2BooleanMap;
		this.field_20727 = map2;
	}

	public boolean method_22497(Entity entity) {
		if (this == field_20722) {
			return true;
		} else if (!(entity instanceof ServerPlayerEntity)) {
			return false;
		} else {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			if (!this.field_20723.test(serverPlayerEntity.experienceLevel)) {
				return false;
			} else if (this.field_20724 != GameMode.NOT_SET && this.field_20724 != serverPlayerEntity.interactionManager.getGameMode()) {
				return false;
			} else {
				StatHandler statHandler = serverPlayerEntity.getStatHandler();

				for (Entry<Stat<?>, NumberRange.IntRange> entry : this.field_20725.entrySet()) {
					int i = statHandler.getStat((Stat<?>)entry.getKey());
					if (!((NumberRange.IntRange)entry.getValue()).test(i)) {
						return false;
					}
				}

				RecipeBook recipeBook = serverPlayerEntity.getRecipeBook();

				for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<Identifier> entry2 : this.field_20726.object2BooleanEntrySet()) {
					if (recipeBook.method_22845((Identifier)entry2.getKey()) != entry2.getBooleanValue()) {
						return false;
					}
				}

				if (!this.field_20727.isEmpty()) {
					PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementManager();
					ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementManager();

					for (Entry<Identifier, class_4553.class_4556> entry3 : this.field_20727.entrySet()) {
						Advancement advancement = serverAdvancementLoader.get((Identifier)entry3.getKey());
						if (advancement == null || !((class_4553.class_4556)entry3.getValue()).test(playerAdvancementTracker.getProgress(advancement))) {
							return false;
						}
					}
				}

				return true;
			}
		}
	}

	public static class_4553 method_22499(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "player");
			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
			String string = JsonHelper.getString(jsonObject, "gamemode", "");
			GameMode gameMode = GameMode.byName(string, GameMode.NOT_SET);
			Map<Stat<?>, NumberRange.IntRange> map = Maps.<Stat<?>, NumberRange.IntRange>newHashMap();
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "stats", null);
			if (jsonArray != null) {
				for (JsonElement jsonElement2 : jsonArray) {
					JsonObject jsonObject2 = JsonHelper.asObject(jsonElement2, "stats entry");
					Identifier identifier = new Identifier(JsonHelper.getString(jsonObject2, "type"));
					StatType<?> statType = Registry.STAT_TYPE.get(identifier);
					if (statType == null) {
						throw new JsonParseException("Invalid stat type: " + identifier);
					}

					Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject2, "stat"));
					Stat<?> stat = method_22496(statType, identifier2);
					NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("value"));
					map.put(stat, intRange2);
				}
			}

			Object2BooleanMap<Identifier> object2BooleanMap = new Object2BooleanOpenHashMap<>();
			JsonObject jsonObject3 = JsonHelper.getObject(jsonObject, "recipes", new JsonObject());

			for (Entry<String, JsonElement> entry : jsonObject3.entrySet()) {
				Identifier identifier3 = new Identifier((String)entry.getKey());
				boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "recipe present");
				object2BooleanMap.put(identifier3, bl);
			}

			Map<Identifier, class_4553.class_4556> map2 = Maps.<Identifier, class_4553.class_4556>newHashMap();
			JsonObject jsonObject4 = JsonHelper.getObject(jsonObject, "advancements", new JsonObject());

			for (Entry<String, JsonElement> entry2 : jsonObject4.entrySet()) {
				Identifier identifier4 = new Identifier((String)entry2.getKey());
				class_4553.class_4556 lv = method_22503((JsonElement)entry2.getValue());
				map2.put(identifier4, lv);
			}

			return new class_4553(intRange, gameMode, map, object2BooleanMap, map2);
		} else {
			return field_20722;
		}
	}

	private static <T> Stat<T> method_22496(StatType<T> statType, Identifier identifier) {
		Registry<T> registry = statType.getRegistry();
		T object = registry.get(identifier);
		if (object == null) {
			throw new JsonParseException("Unknown object " + identifier + " for stat type " + Registry.STAT_TYPE.getId(statType));
		} else {
			return statType.getOrCreateStat(object);
		}
	}

	private static <T> Identifier method_22495(Stat<T> stat) {
		return stat.getType().getRegistry().getId(stat.getValue());
	}

	public JsonElement method_22494() {
		if (this == field_20722) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.field_20723.serialize());
			if (this.field_20724 != GameMode.NOT_SET) {
				jsonObject.addProperty("gamemode", this.field_20724.getName());
			}

			if (!this.field_20725.isEmpty()) {
				JsonArray jsonArray = new JsonArray();
				this.field_20725.forEach((stat, intRange) -> {
					JsonObject jsonObjectx = new JsonObject();
					jsonObjectx.addProperty("type", Registry.STAT_TYPE.getId(stat.getType()).toString());
					jsonObjectx.addProperty("stat", method_22495(stat).toString());
					jsonObjectx.add("value", intRange.serialize());
					jsonArray.add(jsonObjectx);
				});
				jsonObject.add("stats", jsonArray);
			}

			if (!this.field_20726.isEmpty()) {
				JsonObject jsonObject2 = new JsonObject();
				this.field_20726.forEach((identifier, boolean_) -> jsonObject2.addProperty(identifier.toString(), boolean_));
				jsonObject.add("recipes", jsonObject2);
			}

			if (!this.field_20727.isEmpty()) {
				JsonObject jsonObject2 = new JsonObject();
				this.field_20727.forEach((identifier, arg) -> jsonObject2.add(identifier.toString(), arg.method_22506()));
				jsonObject.add("advancements", jsonObject2);
			}

			return jsonObject;
		}
	}

	static class class_4554 implements class_4553.class_4556 {
		private final Object2BooleanMap<String> field_20728;

		public class_4554(Object2BooleanMap<String> object2BooleanMap) {
			this.field_20728 = object2BooleanMap;
		}

		@Override
		public JsonElement method_22506() {
			JsonObject jsonObject = new JsonObject();
			this.field_20728.forEach(jsonObject::addProperty);
			return jsonObject;
		}

		public boolean method_22504(AdvancementProgress advancementProgress) {
			for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<String> entry : this.field_20728.object2BooleanEntrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress == null || criterionProgress.isObtained() != entry.getBooleanValue()) {
					return false;
				}
			}

			return true;
		}
	}

	static class class_4555 implements class_4553.class_4556 {
		private final boolean field_20729;

		public class_4555(boolean bl) {
			this.field_20729 = bl;
		}

		@Override
		public JsonElement method_22506() {
			return new JsonPrimitive(this.field_20729);
		}

		public boolean method_22505(AdvancementProgress advancementProgress) {
			return advancementProgress.isDone() == this.field_20729;
		}
	}

	interface class_4556 extends Predicate<AdvancementProgress> {
		JsonElement method_22506();
	}

	public static class class_4557 {
		private NumberRange.IntRange field_20730 = NumberRange.IntRange.ANY;
		private GameMode field_20731 = GameMode.NOT_SET;
		private final Map<Stat<?>, NumberRange.IntRange> field_20732 = Maps.<Stat<?>, NumberRange.IntRange>newHashMap();
		private final Object2BooleanMap<Identifier> field_20733 = new Object2BooleanOpenHashMap<>();
		private final Map<Identifier, class_4553.class_4556> field_20734 = Maps.<Identifier, class_4553.class_4556>newHashMap();

		public class_4553 method_22507() {
			return new class_4553(this.field_20730, this.field_20731, this.field_20732, this.field_20733, this.field_20734);
		}
	}
}
