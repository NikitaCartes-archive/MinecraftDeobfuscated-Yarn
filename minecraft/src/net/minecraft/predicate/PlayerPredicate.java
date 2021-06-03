package net.minecraft.predicate;

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
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class PlayerPredicate {
	public static final PlayerPredicate ANY = new PlayerPredicate.Builder().build();
	public static final int field_33928 = 100;
	private final NumberRange.IntRange experienceLevel;
	@Nullable
	private final GameMode gameMode;
	private final Map<Stat<?>, NumberRange.IntRange> stats;
	private final Object2BooleanMap<Identifier> recipes;
	private final Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements;
	private final EntityPredicate lookingAt;

	private static PlayerPredicate.AdvancementPredicate criterionFromJson(JsonElement json) {
		if (json.isJsonPrimitive()) {
			boolean bl = json.getAsBoolean();
			return new PlayerPredicate.CompletedAdvancementPredicate(bl);
		} else {
			Object2BooleanMap<String> object2BooleanMap = new Object2BooleanOpenHashMap<>();
			JsonObject jsonObject = JsonHelper.asObject(json, "criterion data");
			jsonObject.entrySet().forEach(entry -> {
				boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
				object2BooleanMap.put((String)entry.getKey(), bl);
			});
			return new PlayerPredicate.AdvancementCriteriaPredicate(object2BooleanMap);
		}
	}

	PlayerPredicate(
		NumberRange.IntRange experienceLevel,
		@Nullable GameMode gameMode,
		Map<Stat<?>, NumberRange.IntRange> stats,
		Object2BooleanMap<Identifier> recipes,
		Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements,
		EntityPredicate lookingAt
	) {
		this.experienceLevel = experienceLevel;
		this.gameMode = gameMode;
		this.stats = stats;
		this.recipes = recipes;
		this.advancements = advancements;
		this.lookingAt = lookingAt;
	}

	public boolean test(Entity entity) {
		if (this == ANY) {
			return true;
		} else if (!(entity instanceof ServerPlayerEntity serverPlayerEntity)) {
			return false;
		} else if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
			return false;
		} else if (this.gameMode != null && this.gameMode != serverPlayerEntity.interactionManager.getGameMode()) {
			return false;
		} else {
			StatHandler statHandler = serverPlayerEntity.getStatHandler();

			for (Entry<Stat<?>, NumberRange.IntRange> entry : this.stats.entrySet()) {
				int i = statHandler.getStat((Stat<?>)entry.getKey());
				if (!((NumberRange.IntRange)entry.getValue()).test(i)) {
					return false;
				}
			}

			RecipeBook recipeBook = serverPlayerEntity.getRecipeBook();

			for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<Identifier> entry2 : this.recipes.object2BooleanEntrySet()) {
				if (recipeBook.contains((Identifier)entry2.getKey()) != entry2.getBooleanValue()) {
					return false;
				}
			}

			if (!this.advancements.isEmpty()) {
				PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
				ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();

				for (Entry<Identifier, PlayerPredicate.AdvancementPredicate> entry3 : this.advancements.entrySet()) {
					Advancement advancement = serverAdvancementLoader.get((Identifier)entry3.getKey());
					if (advancement == null || !((PlayerPredicate.AdvancementPredicate)entry3.getValue()).test(playerAdvancementTracker.getProgress(advancement))) {
						return false;
					}
				}
			}

			if (this.lookingAt != EntityPredicate.ANY) {
				Vec3d vec3d = serverPlayerEntity.getEyePos();
				Vec3d vec3d2 = serverPlayerEntity.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);
				EntityHitResult entityHitResult = ProjectileUtil.method_37226(
					serverPlayerEntity.world, serverPlayerEntity, vec3d, vec3d3, new Box(vec3d, vec3d3).expand(1.0), entityx -> !entityx.isSpectator(), 0.0F
				);
				if (entityHitResult == null || entityHitResult.getType() != HitResult.Type.ENTITY) {
					return false;
				}

				Entity entity2 = entityHitResult.getEntity();
				if (!this.lookingAt.test(serverPlayerEntity, entity2) || !serverPlayerEntity.canSee(entity2)) {
					return false;
				}
			}

			return true;
		}
	}

	public static PlayerPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "player");
			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
			String string = JsonHelper.getString(jsonObject, "gamemode", "");
			GameMode gameMode = GameMode.byName(string, null);
			Map<Stat<?>, NumberRange.IntRange> map = Maps.<Stat<?>, NumberRange.IntRange>newHashMap();
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "stats", null);
			if (jsonArray != null) {
				for (JsonElement jsonElement : jsonArray) {
					JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "stats entry");
					Identifier identifier = new Identifier(JsonHelper.getString(jsonObject2, "type"));
					StatType<?> statType = Registry.STAT_TYPE.get(identifier);
					if (statType == null) {
						throw new JsonParseException("Invalid stat type: " + identifier);
					}

					Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject2, "stat"));
					Stat<?> stat = getStat(statType, identifier2);
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

			Map<Identifier, PlayerPredicate.AdvancementPredicate> map2 = Maps.<Identifier, PlayerPredicate.AdvancementPredicate>newHashMap();
			JsonObject jsonObject4 = JsonHelper.getObject(jsonObject, "advancements", new JsonObject());

			for (Entry<String, JsonElement> entry2 : jsonObject4.entrySet()) {
				Identifier identifier4 = new Identifier((String)entry2.getKey());
				PlayerPredicate.AdvancementPredicate advancementPredicate = criterionFromJson((JsonElement)entry2.getValue());
				map2.put(identifier4, advancementPredicate);
			}

			EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("looking_at"));
			return new PlayerPredicate(intRange, gameMode, map, object2BooleanMap, map2, entityPredicate);
		} else {
			return ANY;
		}
	}

	private static <T> Stat<T> getStat(StatType<T> type, Identifier id) {
		Registry<T> registry = type.getRegistry();
		T object = registry.get(id);
		if (object == null) {
			throw new JsonParseException("Unknown object " + id + " for stat type " + Registry.STAT_TYPE.getId(type));
		} else {
			return type.getOrCreateStat(object);
		}
	}

	private static <T> Identifier getStatId(Stat<T> stat) {
		return stat.getType().getRegistry().getId(stat.getValue());
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.experienceLevel.toJson());
			if (this.gameMode != null) {
				jsonObject.addProperty("gamemode", this.gameMode.getName());
			}

			if (!this.stats.isEmpty()) {
				JsonArray jsonArray = new JsonArray();
				this.stats.forEach((stat, intRange) -> {
					JsonObject jsonObjectx = new JsonObject();
					jsonObjectx.addProperty("type", Registry.STAT_TYPE.getId(stat.getType()).toString());
					jsonObjectx.addProperty("stat", getStatId(stat).toString());
					jsonObjectx.add("value", intRange.toJson());
					jsonArray.add(jsonObjectx);
				});
				jsonObject.add("stats", jsonArray);
			}

			if (!this.recipes.isEmpty()) {
				JsonObject jsonObject2 = new JsonObject();
				this.recipes.forEach((id, boolean_) -> jsonObject2.addProperty(id.toString(), boolean_));
				jsonObject.add("recipes", jsonObject2);
			}

			if (!this.advancements.isEmpty()) {
				JsonObject jsonObject2 = new JsonObject();
				this.advancements.forEach((id, advancementPredicate) -> jsonObject2.add(id.toString(), advancementPredicate.toJson()));
				jsonObject.add("advancements", jsonObject2);
			}

			jsonObject.add("looking_at", this.lookingAt.toJson());
			return jsonObject;
		}
	}

	static class AdvancementCriteriaPredicate implements PlayerPredicate.AdvancementPredicate {
		private final Object2BooleanMap<String> criteria;

		public AdvancementCriteriaPredicate(Object2BooleanMap<String> criteria) {
			this.criteria = criteria;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			this.criteria.forEach(jsonObject::addProperty);
			return jsonObject;
		}

		public boolean test(AdvancementProgress advancementProgress) {
			for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<String> entry : this.criteria.object2BooleanEntrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress == null || criterionProgress.isObtained() != entry.getBooleanValue()) {
					return false;
				}
			}

			return true;
		}
	}

	interface AdvancementPredicate extends Predicate<AdvancementProgress> {
		JsonElement toJson();
	}

	public static class Builder {
		private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
		@Nullable
		private GameMode gameMode;
		private final Map<Stat<?>, NumberRange.IntRange> stats = Maps.<Stat<?>, NumberRange.IntRange>newHashMap();
		private final Object2BooleanMap<Identifier> recipes = new Object2BooleanOpenHashMap<>();
		private final Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements = Maps.<Identifier, PlayerPredicate.AdvancementPredicate>newHashMap();
		private EntityPredicate lookingAt = EntityPredicate.ANY;

		public static PlayerPredicate.Builder create() {
			return new PlayerPredicate.Builder();
		}

		public PlayerPredicate.Builder experienceLevel(NumberRange.IntRange experienceLevel) {
			this.experienceLevel = experienceLevel;
			return this;
		}

		public PlayerPredicate.Builder stat(Stat<?> stat, NumberRange.IntRange value) {
			this.stats.put(stat, value);
			return this;
		}

		public PlayerPredicate.Builder recipe(Identifier id, boolean unlocked) {
			this.recipes.put(id, unlocked);
			return this;
		}

		public PlayerPredicate.Builder gameMode(GameMode gameMode) {
			this.gameMode = gameMode;
			return this;
		}

		public PlayerPredicate.Builder lookingAt(EntityPredicate lookingAt) {
			this.lookingAt = lookingAt;
			return this;
		}

		public PlayerPredicate.Builder advancement(Identifier id, boolean done) {
			this.advancements.put(id, new PlayerPredicate.CompletedAdvancementPredicate(done));
			return this;
		}

		public PlayerPredicate.Builder advancement(Identifier id, Map<String, Boolean> criteria) {
			this.advancements.put(id, new PlayerPredicate.AdvancementCriteriaPredicate(new Object2BooleanOpenHashMap<>(criteria)));
			return this;
		}

		public PlayerPredicate build() {
			return new PlayerPredicate(this.experienceLevel, this.gameMode, this.stats, this.recipes, this.advancements, this.lookingAt);
		}
	}

	static class CompletedAdvancementPredicate implements PlayerPredicate.AdvancementPredicate {
		private final boolean done;

		public CompletedAdvancementPredicate(boolean done) {
			this.done = done;
		}

		@Override
		public JsonElement toJson() {
			return new JsonPrimitive(this.done);
		}

		public boolean test(AdvancementProgress advancementProgress) {
			return advancementProgress.isDone() == this.done;
		}
	}
}
