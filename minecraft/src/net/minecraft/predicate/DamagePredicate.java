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
	private final NumberRange.Float dealt;
	private final NumberRange.Float taken;
	private final EntityPredicate field_9521;
	private final Boolean blocked;
	private final DamageSourcePredicate field_9525;

	public DamagePredicate() {
		this.dealt = NumberRange.Float.ANY;
		this.taken = NumberRange.Float.ANY;
		this.field_9521 = EntityPredicate.ANY;
		this.blocked = null;
		this.field_9525 = DamageSourcePredicate.EMPTY;
	}

	public DamagePredicate(
		NumberRange.Float float_, NumberRange.Float float2, EntityPredicate entityPredicate, @Nullable Boolean boolean_, DamageSourcePredicate damageSourcePredicate
	) {
		this.dealt = float_;
		this.taken = float2;
		this.field_9521 = entityPredicate;
		this.blocked = boolean_;
		this.field_9525 = damageSourcePredicate;
	}

	public boolean method_8838(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
		if (this == ANY) {
			return true;
		} else if (!this.dealt.matches(f)) {
			return false;
		} else if (!this.taken.matches(g)) {
			return false;
		} else if (!this.field_9521.method_8914(serverPlayerEntity, damageSource.method_5529())) {
			return false;
		} else {
			return this.blocked != null && this.blocked != bl ? false : this.field_9525.method_8847(serverPlayerEntity, damageSource);
		}
	}

	public static DamagePredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage");
			NumberRange.Float float_ = NumberRange.Float.fromJson(jsonObject.get("dealt"));
			NumberRange.Float float2 = NumberRange.Float.fromJson(jsonObject.get("taken"));
			Boolean boolean_ = jsonObject.has("blocked") ? JsonHelper.getBoolean(jsonObject, "blocked") : null;
			EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("source_entity"));
			DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.deserialize(jsonObject.get("type"));
			return new DamagePredicate(float_, float2, entityPredicate, boolean_, damageSourcePredicate);
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("dealt", this.dealt.serialize());
			jsonObject.add("taken", this.taken.serialize());
			jsonObject.add("source_entity", this.field_9521.serialize());
			jsonObject.add("type", this.field_9525.serialize());
			if (this.blocked != null) {
				jsonObject.addProperty("blocked", this.blocked);
			}

			return jsonObject;
		}
	}

	public static class Builder {
		private NumberRange.Float dealt = NumberRange.Float.ANY;
		private NumberRange.Float taken = NumberRange.Float.ANY;
		private EntityPredicate field_9528 = EntityPredicate.ANY;
		private Boolean blocked;
		private DamageSourcePredicate field_9529 = DamageSourcePredicate.EMPTY;

		public static DamagePredicate.Builder create() {
			return new DamagePredicate.Builder();
		}

		public DamagePredicate.Builder blocked(Boolean boolean_) {
			this.blocked = boolean_;
			return this;
		}

		public DamagePredicate.Builder method_8842(DamageSourcePredicate.Builder builder) {
			this.field_9529 = builder.build();
			return this;
		}

		public DamagePredicate build() {
			return new DamagePredicate(this.dealt, this.taken, this.field_9528, this.blocked, this.field_9529);
		}
	}
}
