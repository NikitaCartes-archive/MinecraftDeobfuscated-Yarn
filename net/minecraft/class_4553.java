/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class class_4553 {
    public static final class_4553 field_20722 = new class_4557().method_22507();
    private final NumberRange.IntRange field_20723;
    private final GameMode field_20724;
    private final Map<Stat<?>, NumberRange.IntRange> field_20725;
    private final Object2BooleanMap<Identifier> field_20726;
    private final Map<Identifier, class_4556> field_20727;

    private static class_4556 method_22503(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            boolean bl = jsonElement.getAsBoolean();
            return new class_4555(bl);
        }
        Object2BooleanOpenHashMap<String> object2BooleanMap = new Object2BooleanOpenHashMap<String>();
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "criterion data");
        jsonObject.entrySet().forEach(entry -> {
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
            object2BooleanMap.put((String)entry.getKey(), bl);
        });
        return new class_4554(object2BooleanMap);
    }

    private class_4553(NumberRange.IntRange intRange, GameMode gameMode, Map<Stat<?>, NumberRange.IntRange> map, Object2BooleanMap<Identifier> object2BooleanMap, Map<Identifier, class_4556> map2) {
        this.field_20723 = intRange;
        this.field_20724 = gameMode;
        this.field_20725 = map;
        this.field_20726 = object2BooleanMap;
        this.field_20727 = map2;
    }

    public boolean method_22497(Entity entity) {
        if (this == field_20722) {
            return true;
        }
        if (!(entity instanceof ServerPlayerEntity)) {
            return false;
        }
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
        if (!this.field_20723.test(serverPlayerEntity.experienceLevel)) {
            return false;
        }
        if (this.field_20724 != GameMode.NOT_SET && this.field_20724 != serverPlayerEntity.interactionManager.getGameMode()) {
            return false;
        }
        ServerStatHandler statHandler = serverPlayerEntity.getStatHandler();
        for (Map.Entry<Stat<?>, NumberRange.IntRange> entry : this.field_20725.entrySet()) {
            int n = statHandler.getStat(entry.getKey());
            if (entry.getValue().test(n)) continue;
            return false;
        }
        ServerRecipeBook recipeBook = serverPlayerEntity.getRecipeBook();
        for (Object2BooleanMap.Entry entry : this.field_20726.object2BooleanEntrySet()) {
            if (recipeBook.method_22845((Identifier)entry.getKey()) == entry.getBooleanValue()) continue;
            return false;
        }
        if (!this.field_20727.isEmpty()) {
            PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementManager();
            ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementManager();
            for (Map.Entry<Identifier, class_4556> entry3 : this.field_20727.entrySet()) {
                Advancement advancement = serverAdvancementLoader.get(entry3.getKey());
                if (advancement != null && entry3.getValue().test(playerAdvancementTracker.getProgress(advancement))) continue;
                return false;
            }
        }
        return true;
    }

    public static class_4553 method_22499(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return field_20722;
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
                Stat<?> stat = class_4553.method_22496(statType, identifier2);
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
        HashMap<Identifier, class_4556> map2 = Maps.newHashMap();
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "advancements", new JsonObject());
        for (Map.Entry<String, JsonElement> entry2 : jsonObject2.entrySet()) {
            Identifier identifier4 = new Identifier(entry2.getKey());
            class_4556 lv = class_4553.method_22503(entry2.getValue());
            map2.put(identifier4, lv);
        }
        return new class_4553(intRange, gameMode, map, object2BooleanMap, map2);
    }

    private static <T> Stat<T> method_22496(StatType<T> statType, Identifier identifier) {
        Registry<T> registry = statType.getRegistry();
        T object = registry.get(identifier);
        if (object == null) {
            throw new JsonParseException("Unknown object " + identifier + " for stat type " + Registry.STAT_TYPE.getId(statType));
        }
        return statType.getOrCreateStat(object);
    }

    private static <T> Identifier method_22495(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }

    public JsonElement method_22494() {
        JsonObject jsonObject2;
        if (this == field_20722) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("level", this.field_20723.serialize());
        if (this.field_20724 != GameMode.NOT_SET) {
            jsonObject.addProperty("gamemode", this.field_20724.getName());
        }
        if (!this.field_20725.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            this.field_20725.forEach((stat, intRange) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", Registry.STAT_TYPE.getId(stat.getType()).toString());
                jsonObject.addProperty("stat", class_4553.method_22495(stat).toString());
                jsonObject.add("value", intRange.serialize());
                jsonArray.add(jsonObject);
            });
            jsonObject.add("stats", jsonArray);
        }
        if (!this.field_20726.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.field_20726.forEach((identifier, boolean_) -> jsonObject2.addProperty(identifier.toString(), (Boolean)boolean_));
            jsonObject.add("recipes", jsonObject2);
        }
        if (!this.field_20727.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.field_20727.forEach((identifier, arg) -> jsonObject2.add(identifier.toString(), arg.method_22506()));
            jsonObject.add("advancements", jsonObject2);
        }
        return jsonObject;
    }

    public static class class_4557 {
        private NumberRange.IntRange field_20730 = NumberRange.IntRange.ANY;
        private GameMode field_20731 = GameMode.NOT_SET;
        private final Map<Stat<?>, NumberRange.IntRange> field_20732 = Maps.newHashMap();
        private final Object2BooleanMap<Identifier> field_20733 = new Object2BooleanOpenHashMap<Identifier>();
        private final Map<Identifier, class_4556> field_20734 = Maps.newHashMap();

        public class_4553 method_22507() {
            return new class_4553(this.field_20730, this.field_20731, this.field_20732, this.field_20733, this.field_20734);
        }
    }

    static class class_4554
    implements class_4556 {
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
            for (Object2BooleanMap.Entry entry : this.field_20728.object2BooleanEntrySet()) {
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

    static class class_4555
    implements class_4556 {
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

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.method_22505((AdvancementProgress)object);
        }
    }

    static interface class_4556
    extends Predicate<AdvancementProgress> {
        public JsonElement method_22506();
    }
}

