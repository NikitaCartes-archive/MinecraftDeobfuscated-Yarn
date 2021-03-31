/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EntityEffectPredicate {
    public static final EntityEffectPredicate EMPTY = new EntityEffectPredicate(Collections.emptyMap());
    private final Map<StatusEffect, EffectData> effects;

    public EntityEffectPredicate(Map<StatusEffect, EffectData> effects) {
        this.effects = effects;
    }

    public static EntityEffectPredicate create() {
        return new EntityEffectPredicate(Maps.newLinkedHashMap());
    }

    public EntityEffectPredicate withEffect(StatusEffect statusEffect) {
        this.effects.put(statusEffect, new EffectData());
        return this;
    }

    public EntityEffectPredicate withEffect(StatusEffect statusEffect, EffectData data) {
        this.effects.put(statusEffect, data);
        return this;
    }

    public boolean test(Entity entity) {
        if (this == EMPTY) {
            return true;
        }
        if (entity instanceof LivingEntity) {
            return this.test(((LivingEntity)entity).getActiveStatusEffects());
        }
        return false;
    }

    public boolean test(LivingEntity livingEntity) {
        if (this == EMPTY) {
            return true;
        }
        return this.test(livingEntity.getActiveStatusEffects());
    }

    public boolean test(Map<StatusEffect, StatusEffectInstance> effects) {
        if (this == EMPTY) {
            return true;
        }
        for (Map.Entry<StatusEffect, EffectData> entry : this.effects.entrySet()) {
            StatusEffectInstance statusEffectInstance = effects.get(entry.getKey());
            if (entry.getValue().test(statusEffectInstance)) continue;
            return false;
        }
        return true;
    }

    public static EntityEffectPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "effects");
        LinkedHashMap<StatusEffect, EffectData> map = Maps.newLinkedHashMap();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Identifier identifier = new Identifier(entry.getKey());
            StatusEffect statusEffect = Registry.STATUS_EFFECT.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + identifier + "'"));
            EffectData effectData = EffectData.fromJson(JsonHelper.asObject(entry.getValue(), entry.getKey()));
            map.put(statusEffect, effectData);
        }
        return new EntityEffectPredicate(map);
    }

    public JsonElement toJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<StatusEffect, EffectData> entry : this.effects.entrySet()) {
            jsonObject.add(Registry.STATUS_EFFECT.getId(entry.getKey()).toString(), entry.getValue().toJson());
        }
        return jsonObject;
    }

    public static class EffectData {
        private final NumberRange.IntRange amplifier;
        private final NumberRange.IntRange duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;

        public EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, @Nullable Boolean ambient, @Nullable Boolean visible) {
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.visible = visible;
        }

        public EffectData() {
            this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, null, null);
        }

        public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
            if (statusEffectInstance == null) {
                return false;
            }
            if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
                return false;
            }
            if (!this.duration.test(statusEffectInstance.getDuration())) {
                return false;
            }
            if (this.ambient != null && this.ambient.booleanValue() != statusEffectInstance.isAmbient()) {
                return false;
            }
            return this.visible == null || this.visible.booleanValue() == statusEffectInstance.shouldShowParticles();
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("amplifier", this.amplifier.toJson());
            jsonObject.add("duration", this.duration.toJson());
            jsonObject.addProperty("ambient", this.ambient);
            jsonObject.addProperty("visible", this.visible);
            return jsonObject;
        }

        public static EffectData fromJson(JsonObject json) {
            NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(json.get("amplifier"));
            NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(json.get("duration"));
            Boolean boolean_ = json.has("ambient") ? Boolean.valueOf(JsonHelper.getBoolean(json, "ambient")) : null;
            Boolean boolean2 = json.has("visible") ? Boolean.valueOf(JsonHelper.getBoolean(json, "visible")) : null;
            return new EffectData(intRange, intRange2, boolean_, boolean2);
        }
    }
}

