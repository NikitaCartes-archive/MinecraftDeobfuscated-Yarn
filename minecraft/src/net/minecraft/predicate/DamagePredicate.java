package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;

public class DamagePredicate {
	public static final DamagePredicate ANY = DamagePredicate.Builder.create().build();
	private final NumberRange.FloatRange field_9523;
	private final NumberRange.FloatRange field_9524;
	private final EntityPredicate sourceEntity;
	private final Boolean blocked;
	private final DamageSourcePredicate type;

	public DamagePredicate() {
		this.field_9523 = NumberRange.FloatRange.ANY;
		this.field_9524 = NumberRange.FloatRange.ANY;
		this.sourceEntity = EntityPredicate.ANY;
		this.blocked = null;
		this.type = DamageSourcePredicate.EMPTY;
	}

	public DamagePredicate(
		NumberRange.FloatRange floatRange,
		NumberRange.FloatRange floatRange2,
		EntityPredicate entityPredicate,
		@Nullable Boolean boolean_,
		DamageSourcePredicate damageSourcePredicate
	) {
		this.field_9523 = floatRange;
		this.field_9524 = floatRange2;
		this.sourceEntity = entityPredicate;
		this.blocked = boolean_;
		this.type = damageSourcePredicate;
	}

	public boolean test(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
		if (this == ANY) {
			return true;
		} else if (!this.field_9523.matches(f)) {
			return false;
		} else if (!this.field_9524.matches(g)) {
			return false;
		} else if (!this.sourceEntity.test(serverPlayerEntity, damageSource.getAttacker())) {
			return false;
		} else {
			return this.blocked != null && this.blocked != bl ? false : this.type.test(serverPlayerEntity, damageSource);
		}
	}

	public static DamagePredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage");
			NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("dealt"));
			NumberRange.FloatRange floatRange2 = NumberRange.FloatRange.fromJson(jsonObject.get("taken"));
			Boolean boolean_ = jsonObject.has("blocked") ? JsonHelper.getBoolean(jsonObject, "blocked") : null;
			EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("source_entity"));
			DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.deserialize(jsonObject.get("type"));
			return new DamagePredicate(floatRange, floatRange2, entityPredicate, boolean_, damageSourcePredicate);
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("dealt", this.field_9523.serialize());
			jsonObject.add("taken", this.field_9524.serialize());
			jsonObject.add("source_entity", this.sourceEntity.serialize());
			jsonObject.add("type", this.type.serialize());
			if (this.blocked != null) {
				jsonObject.addProperty("blocked", this.blocked);
			}

			return jsonObject;
		}
	}

	public static class Builder {
		private NumberRange.FloatRange field_9530 = NumberRange.FloatRange.ANY;
		private NumberRange.FloatRange field_9527 = NumberRange.FloatRange.ANY;
		private EntityPredicate sourceEntity = EntityPredicate.ANY;
		private Boolean blocked;
		private DamageSourcePredicate type = DamageSourcePredicate.EMPTY;

		public static DamagePredicate.Builder create() {
			return new DamagePredicate.Builder();
		}

		public DamagePredicate.Builder blocked(Boolean boolean_) {
			this.blocked = boolean_;
			return this;
		}

		public DamagePredicate.Builder type(DamageSourcePredicate.Builder builder) {
			this.type = builder.build();
			return this;
		}

		public DamagePredicate build() {
			return new DamagePredicate(this.field_9530, this.field_9527, this.sourceEntity, this.blocked, this.type);
		}
	}
}
