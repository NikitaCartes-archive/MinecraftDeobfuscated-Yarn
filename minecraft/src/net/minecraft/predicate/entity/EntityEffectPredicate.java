package net.minecraft.predicate.entity;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;

public class EntityEffectPredicate {
	public static final EntityEffectPredicate EMPTY = new EntityEffectPredicate(Collections.emptyMap());
	private final Map<StatusEffect, EntityEffectPredicate.class_2103> effects;

	public EntityEffectPredicate(Map<StatusEffect, EntityEffectPredicate.class_2103> map) {
		this.effects = map;
	}

	public static EntityEffectPredicate create() {
		return new EntityEffectPredicate(Maps.<StatusEffect, EntityEffectPredicate.class_2103>newHashMap());
	}

	public EntityEffectPredicate method_9065(StatusEffect statusEffect) {
		this.effects.put(statusEffect, new EntityEffectPredicate.class_2103());
		return this;
	}

	public boolean test(Entity entity) {
		if (this == EMPTY) {
			return true;
		} else {
			return entity instanceof LivingEntity ? this.test(((LivingEntity)entity).method_6088()) : false;
		}
	}

	public boolean test(LivingEntity livingEntity) {
		return this == EMPTY ? true : this.test(livingEntity.method_6088());
	}

	public boolean test(Map<StatusEffect, StatusEffectInstance> map) {
		if (this == EMPTY) {
			return true;
		} else {
			for (Entry<StatusEffect, EntityEffectPredicate.class_2103> entry : this.effects.entrySet()) {
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)map.get(entry.getKey());
				if (!((EntityEffectPredicate.class_2103)entry.getValue()).test(statusEffectInstance)) {
					return false;
				}
			}

			return true;
		}
	}

	public static EntityEffectPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "effects");
			Map<StatusEffect, EntityEffectPredicate.class_2103> map = Maps.<StatusEffect, EntityEffectPredicate.class_2103>newHashMap();

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				Identifier identifier = new Identifier((String)entry.getKey());
				StatusEffect statusEffect = Registry.STATUS_EFFECT.get(identifier);
				if (statusEffect == null) {
					throw new JsonSyntaxException("Unknown effect '" + identifier + "'");
				}

				EntityEffectPredicate.class_2103 lv = EntityEffectPredicate.class_2103.deserialize(
					JsonHelper.asObject((JsonElement)entry.getValue(), (String)entry.getKey())
				);
				map.put(statusEffect, lv);
			}

			return new EntityEffectPredicate(map);
		} else {
			return EMPTY;
		}
	}

	public JsonElement serialize() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();

			for (Entry<StatusEffect, EntityEffectPredicate.class_2103> entry : this.effects.entrySet()) {
				jsonObject.add(Registry.STATUS_EFFECT.getId((StatusEffect)entry.getKey()).toString(), ((EntityEffectPredicate.class_2103)entry.getValue()).serialize());
			}

			return jsonObject;
		}
	}

	public static class class_2103 {
		private final NumberRange.Integer amplifier;
		private final NumberRange.Integer duration;
		@Nullable
		private final Boolean ambient;
		@Nullable
		private final Boolean visible;

		public class_2103(NumberRange.Integer integer, NumberRange.Integer integer2, @Nullable Boolean boolean_, @Nullable Boolean boolean2) {
			this.amplifier = integer;
			this.duration = integer2;
			this.ambient = boolean_;
			this.visible = boolean2;
		}

		public class_2103() {
			this(NumberRange.Integer.ANY, NumberRange.Integer.ANY, null, null);
		}

		public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
			if (statusEffectInstance == null) {
				return false;
			} else if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
				return false;
			} else if (!this.duration.test(statusEffectInstance.getDuration())) {
				return false;
			} else {
				return this.ambient != null && this.ambient != statusEffectInstance.isAmbient()
					? false
					: this.visible == null || this.visible == statusEffectInstance.shouldShowParticles();
			}
		}

		public JsonElement serialize() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("amplifier", this.amplifier.serialize());
			jsonObject.add("duration", this.duration.serialize());
			jsonObject.addProperty("ambient", this.ambient);
			jsonObject.addProperty("visible", this.visible);
			return jsonObject;
		}

		public static EntityEffectPredicate.class_2103 deserialize(JsonObject jsonObject) {
			NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("amplifier"));
			NumberRange.Integer integer2 = NumberRange.Integer.fromJson(jsonObject.get("duration"));
			Boolean boolean_ = jsonObject.has("ambient") ? JsonHelper.getBoolean(jsonObject, "ambient") : null;
			Boolean boolean2 = jsonObject.has("visible") ? JsonHelper.getBoolean(jsonObject, "visible") : null;
			return new EntityEffectPredicate.class_2103(integer, integer2, boolean_, boolean2);
		}
	}
}
