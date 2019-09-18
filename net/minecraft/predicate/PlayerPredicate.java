/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerPredicate {
    public static final PlayerPredicate ANY = new Builder().build();
    private final NumberRange.IntRange experienceLevel;
    private final GameMode gamemode;
    private final Map<Stat<?>, NumberRange.IntRange> stats;
    private final Object2BooleanMap<Identifier> recipes;
    private final Map<Identifier, AdvancementPredicate> advancements;

    private static AdvancementPredicate criterionFromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            boolean bl = jsonElement.getAsBoolean();
            return new CompletedAdvancementPredicate(bl);
        }
        Object2BooleanOpenHashMap<String> object2BooleanMap = new Object2BooleanOpenHashMap<String>();
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "criterion data");
        jsonObject.entrySet().forEach(entry -> {
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
            object2BooleanMap.put((String)entry.getKey(), bl);
        });
        return new AdvancementCriteriaPredicate(object2BooleanMap);
    }

    private PlayerPredicate(NumberRange.IntRange intRange, GameMode gameMode, Map<Stat<?>, NumberRange.IntRange> map, Object2BooleanMap<Identifier> object2BooleanMap, Map<Identifier, AdvancementPredicate> map2) {
        this.experienceLevel = intRange;
        this.gamemode = gameMode;
        this.stats = map;
        this.recipes = object2BooleanMap;
        this.advancements = map2;
    }

    public boolean test(Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof ServerPlayerEntity)) {
            return false;
        }
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
        if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
            return false;
        }
        if (this.gamemode != GameMode.NOT_SET && this.gamemode != serverPlayerEntity.interactionManager.getGameMode()) {
            return false;
        }
        ServerStatHandler statHandler = serverPlayerEntity.getStatHandler();
        for (Map.Entry<Stat<?>, NumberRange.IntRange> entry : this.stats.entrySet()) {
            int n = statHandler.getStat(entry.getKey());
            if (entry.getValue().test(n)) continue;
            return false;
        }
        ServerRecipeBook recipeBook = serverPlayerEntity.getRecipeBook();
        for (Object2BooleanMap.Entry entry : this.recipes.object2BooleanEntrySet()) {
            if (recipeBook.contains((Identifier)entry.getKey()) == entry.getBooleanValue()) continue;
            return false;
        }
        if (!this.advancements.isEmpty()) {
            PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementManager();
            ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementManager();
            for (Map.Entry<Identifier, AdvancementPredicate> entry3 : this.advancements.entrySet()) {
                Advancement advancement = serverAdvancementLoader.get(entry3.getKey());
                if (advancement != null && entry3.getValue().test(playerAdvancementTracker.getProgress(advancement))) continue;
                return false;
            }
        }
        return true;
    }

    public static PlayerPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "player");
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
        String string = JsonHelper.getString(jsonObject, "gamemode", "");
        GameMode gameMode = GameMode.byName(string, GameMode.NOT_SET);
        HashMap<Stat<?>, NumberRange.IntRange> map = Maps.newHashMap();
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
                Stat<?> stat = PlayerPredicate.getStat(statType, identifier2);
                NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("value"));
                map.put(stat, intRange2);
            }
        }
        Object2BooleanOpenHashMap<Identifier> object2BooleanMap = new Object2BooleanOpenHashMap<Identifier>();
        JsonObject jsonObject3 = JsonHelper.getObject(jsonObject, "recipes", new JsonObject());
        for (Map.Entry entry : jsonObject3.entrySet()) {
            Identifier identifier3 = new Identifier((String)entry.getKey());
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "recipe present");
            object2BooleanMap.put(identifier3, bl);
        }
        HashMap<Identifier, AdvancementPredicate> map2 = Maps.newHashMap();
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "advancements", new JsonObject());
        for (Map.Entry<String, JsonElement> entry2 : jsonObject2.entrySet()) {
            Identifier identifier4 = new Identifier(entry2.getKey());
            AdvancementPredicate advancementPredicate = PlayerPredicate.criterionFromJson(entry2.getValue());
            map2.put(identifier4, advancementPredicate);
        }
        return new PlayerPredicate(intRange, gameMode, map, object2BooleanMap, map2);
    }

    private static <T> Stat<T> getStat(StatType<T> statType, Identifier identifier) {
        Registry<T> registry = statType.getRegistry();
        T object = registry.get(identifier);
        if (object == null) {
            throw new JsonParseException("Unknown object " + identifier + " for stat type " + Registry.STAT_TYPE.getId(statType));
        }
        return statType.getOrCreateStat(object);
    }

    private static <T> Identifier getStatId(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }

    public JsonElement toJson() {
        JsonObject jsonObject2;
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("level", this.experienceLevel.toJson());
        if (this.gamemode != GameMode.NOT_SET) {
            jsonObject.addProperty("gamemode", this.gamemode.getName());
        }
        if (!this.stats.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            this.stats.forEach((stat, intRange) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", Registry.STAT_TYPE.getId(stat.getType()).toString());
                jsonObject.addProperty("stat", PlayerPredicate.getStatId(stat).toString());
                jsonObject.add("value", intRange.toJson());
                jsonArray.add(jsonObject);
            });
            jsonObject.add("stats", jsonArray);
        }
        if (!this.recipes.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.recipes.forEach((identifier, boolean_) -> jsonObject2.addProperty(identifier.toString(), (Boolean)boolean_));
            jsonObject.add("recipes", jsonObject2);
        }
        if (!this.advancements.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.advancements.forEach((identifier, advancementPredicate) -> jsonObject2.add(identifier.toString(), advancementPredicate.toJson()));
            jsonObject.add("advancements", jsonObject2);
        }
        return jsonObject;
    }

    public static class Builder {
        private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
        private GameMode gamemode = GameMode.NOT_SET;
        private final Map<Stat<?>, NumberRange.IntRange> stats = Maps.newHashMap();
        private final Object2BooleanMap<Identifier> recipes = new Object2BooleanOpenHashMap<Identifier>();
        private final Map<Identifier, AdvancementPredicate> advancements = Maps.newHashMap();

        public PlayerPredicate build() {
            return new PlayerPredicate(this.experienceLevel, this.gamemode, this.stats, this.recipes, this.advancements);
        }
    }

    static class AdvancementCriteriaPredicate
    implements AdvancementPredicate {
        private final Object2BooleanMap<String> criteria;

        public AdvancementCriteriaPredicate(Object2BooleanMap<String> object2BooleanMap) {
            this.criteria = object2BooleanMap;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            this.criteria.forEach(jsonObject::addProperty);
            return jsonObject;
        }

        public boolean method_22504(AdvancementProgress advancementProgress) {
            for (Object2BooleanMap.Entry entry : this.criteria.object2BooleanEntrySet()) {
                CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
                if (criterionProgress != null && criterionProgress.isObtained() == entry.getBooleanValue()) continue;
                return false;
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.method_22504((AdvancementProgress)object);
        }
    }

    static class CompletedAdvancementPredicate
    implements AdvancementPredicate {
        private final boolean done;

        public CompletedAdvancementPredicate(boolean bl) {
            this.done = bl;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.done);
        }

        public boolean method_22505(AdvancementProgress advancementProgress) {
            return advancementProgress.isDone() == this.done;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.method_22505((AdvancementProgress)object);
        }
    }

    static interface AdvancementPredicate
    extends Predicate<AdvancementProgress> {
        public JsonElement toJson();
    }
}

