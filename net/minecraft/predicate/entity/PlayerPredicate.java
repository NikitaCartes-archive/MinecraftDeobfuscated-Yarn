/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerPredicate
implements TypeSpecificPredicate {
    public static final int field_33928 = 100;
    private final NumberRange.IntRange experienceLevel;
    @Nullable
    private final GameMode gameMode;
    private final Map<Stat<?>, NumberRange.IntRange> stats;
    private final Object2BooleanMap<Identifier> recipes;
    private final Map<Identifier, AdvancementPredicate> advancements;
    private final EntityPredicate lookingAt;

    private static AdvancementPredicate criterionFromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            boolean bl = json.getAsBoolean();
            return new CompletedAdvancementPredicate(bl);
        }
        Object2BooleanOpenHashMap<String> object2BooleanMap = new Object2BooleanOpenHashMap<String>();
        JsonObject jsonObject = JsonHelper.asObject(json, "criterion data");
        jsonObject.entrySet().forEach(entry -> {
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
            object2BooleanMap.put((String)entry.getKey(), bl);
        });
        return new AdvancementCriteriaPredicate(object2BooleanMap);
    }

    PlayerPredicate(NumberRange.IntRange experienceLevel, @Nullable GameMode gameMode, Map<Stat<?>, NumberRange.IntRange> stats, Object2BooleanMap<Identifier> recipes, Map<Identifier, AdvancementPredicate> advancements, EntityPredicate lookingAt) {
        this.experienceLevel = experienceLevel;
        this.gameMode = gameMode;
        this.stats = stats;
        this.recipes = recipes;
        this.advancements = advancements;
        this.lookingAt = lookingAt;
    }

    @Override
    public boolean test(Entity entity2, ServerWorld world, @Nullable Vec3d pos) {
        if (!(entity2 instanceof ServerPlayerEntity)) {
            return false;
        }
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
        if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
            return false;
        }
        if (this.gameMode != null && this.gameMode != serverPlayerEntity.interactionManager.getGameMode()) {
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
            PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
            ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();
            for (Map.Entry<Identifier, AdvancementPredicate> entry3 : this.advancements.entrySet()) {
                Advancement advancement = serverAdvancementLoader.get(entry3.getKey());
                if (advancement != null && entry3.getValue().test(playerAdvancementTracker.getProgress(advancement))) continue;
                return false;
            }
        }
        if (this.lookingAt != EntityPredicate.ANY) {
            Vec3d vec3d = serverPlayerEntity.getEyePos();
            Vec3d vec3d2 = serverPlayerEntity.getRotationVec(1.0f);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);
            EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(serverPlayerEntity.world, serverPlayerEntity, vec3d, vec3d3, new Box(vec3d, vec3d3).expand(1.0), entity -> !entity.isSpectator(), 0.0f);
            if (entityHitResult == null || entityHitResult.getType() != HitResult.Type.ENTITY) {
                return false;
            }
            Entity entity22 = entityHitResult.getEntity();
            if (!this.lookingAt.test(serverPlayerEntity, entity22) || !serverPlayerEntity.canSee(entity22)) {
                return false;
            }
        }
        return true;
    }

    public static PlayerPredicate fromJson(JsonObject json) {
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(json.get("level"));
        String string = JsonHelper.getString(json, "gamemode", "");
        GameMode gameMode = GameMode.byName(string, null);
        HashMap<Stat<?>, NumberRange.IntRange> map = Maps.newHashMap();
        JsonArray jsonArray = JsonHelper.getArray(json, "stats", null);
        if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "stats entry");
                Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "type"));
                StatType<?> statType = Registries.STAT_TYPE.get(identifier);
                if (statType == null) {
                    throw new JsonParseException("Invalid stat type: " + identifier);
                }
                Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "stat"));
                Stat<?> stat = PlayerPredicate.getStat(statType, identifier2);
                NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("value"));
                map.put(stat, intRange2);
            }
        }
        Object2BooleanOpenHashMap<Identifier> object2BooleanMap = new Object2BooleanOpenHashMap<Identifier>();
        JsonObject jsonObject2 = JsonHelper.getObject(json, "recipes", new JsonObject());
        for (Map.Entry entry : jsonObject2.entrySet()) {
            Identifier identifier3 = new Identifier((String)entry.getKey());
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "recipe present");
            object2BooleanMap.put(identifier3, bl);
        }
        HashMap<Identifier, AdvancementPredicate> map2 = Maps.newHashMap();
        JsonObject jsonObject = JsonHelper.getObject(json, "advancements", new JsonObject());
        for (Map.Entry entry : jsonObject.entrySet()) {
            Identifier identifier4 = new Identifier((String)entry.getKey());
            AdvancementPredicate advancementPredicate = PlayerPredicate.criterionFromJson((JsonElement)entry.getValue());
            map2.put(identifier4, advancementPredicate);
        }
        EntityPredicate entityPredicate = EntityPredicate.fromJson(json.get("looking_at"));
        return new PlayerPredicate(intRange, gameMode, map, object2BooleanMap, map2, entityPredicate);
    }

    private static <T> Stat<T> getStat(StatType<T> type, Identifier id) {
        Registry<T> registry = type.getRegistry();
        T object = registry.get(id);
        if (object == null) {
            throw new JsonParseException("Unknown object " + id + " for stat type " + Registries.STAT_TYPE.getId(type));
        }
        return type.getOrCreateStat(object);
    }

    private static <T> Identifier getStatId(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }

    @Override
    public JsonObject typeSpecificToJson() {
        JsonObject jsonObject2;
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("level", this.experienceLevel.toJson());
        if (this.gameMode != null) {
            jsonObject.addProperty("gamemode", this.gameMode.getName());
        }
        if (!this.stats.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            this.stats.forEach((stat, intRange) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", Registries.STAT_TYPE.getId(stat.getType()).toString());
                jsonObject.addProperty("stat", PlayerPredicate.getStatId(stat).toString());
                jsonObject.add("value", intRange.toJson());
                jsonArray.add(jsonObject);
            });
            jsonObject.add("stats", jsonArray);
        }
        if (!this.recipes.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.recipes.forEach((id, boolean_) -> jsonObject2.addProperty(id.toString(), (Boolean)boolean_));
            jsonObject.add("recipes", jsonObject2);
        }
        if (!this.advancements.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.advancements.forEach((id, advancementPredicate) -> jsonObject2.add(id.toString(), advancementPredicate.toJson()));
            jsonObject.add("advancements", jsonObject2);
        }
        jsonObject.add("looking_at", this.lookingAt.toJson());
        return jsonObject;
    }

    @Override
    public TypeSpecificPredicate.Deserializer getDeserializer() {
        return TypeSpecificPredicate.Deserializers.PLAYER;
    }

    static class CompletedAdvancementPredicate
    implements AdvancementPredicate {
        private final boolean done;

        public CompletedAdvancementPredicate(boolean done) {
            this.done = done;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.done);
        }

        @Override
        public boolean test(AdvancementProgress advancementProgress) {
            return advancementProgress.isDone() == this.done;
        }

        @Override
        public /* synthetic */ boolean test(Object progress) {
            return this.test((AdvancementProgress)progress);
        }
    }

    static class AdvancementCriteriaPredicate
    implements AdvancementPredicate {
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

        @Override
        public boolean test(AdvancementProgress advancementProgress) {
            for (Object2BooleanMap.Entry entry : this.criteria.object2BooleanEntrySet()) {
                CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
                if (criterionProgress != null && criterionProgress.isObtained() == entry.getBooleanValue()) continue;
                return false;
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object progress) {
            return this.test((AdvancementProgress)progress);
        }
    }

    static interface AdvancementPredicate
    extends Predicate<AdvancementProgress> {
        public JsonElement toJson();
    }

    public static class Builder {
        private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
        @Nullable
        private GameMode gameMode;
        private final Map<Stat<?>, NumberRange.IntRange> stats = Maps.newHashMap();
        private final Object2BooleanMap<Identifier> recipes = new Object2BooleanOpenHashMap<Identifier>();
        private final Map<Identifier, AdvancementPredicate> advancements = Maps.newHashMap();
        private EntityPredicate lookingAt = EntityPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder experienceLevel(NumberRange.IntRange experienceLevel) {
            this.experienceLevel = experienceLevel;
            return this;
        }

        public Builder stat(Stat<?> stat, NumberRange.IntRange value) {
            this.stats.put(stat, value);
            return this;
        }

        public Builder recipe(Identifier id, boolean unlocked) {
            this.recipes.put(id, unlocked);
            return this;
        }

        public Builder gameMode(GameMode gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder lookingAt(EntityPredicate lookingAt) {
            this.lookingAt = lookingAt;
            return this;
        }

        public Builder advancement(Identifier id, boolean done) {
            this.advancements.put(id, new CompletedAdvancementPredicate(done));
            return this;
        }

        public Builder advancement(Identifier id, Map<String, Boolean> criteria) {
            this.advancements.put(id, new AdvancementCriteriaPredicate(new Object2BooleanOpenHashMap<String>(criteria)));
            return this;
        }

        public PlayerPredicate build() {
            return new PlayerPredicate(this.experienceLevel, this.gameMode, this.stats, this.recipes, this.advancements, this.lookingAt);
        }
    }
}

