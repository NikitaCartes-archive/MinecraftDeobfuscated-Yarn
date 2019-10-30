package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JsonHelper;

public class EntityFlagsPredicate {
	public static final EntityFlagsPredicate ANY = new EntityFlagsPredicate.Builder().build();
	@Nullable
	private final Boolean isOnFire;
	@Nullable
	private final Boolean isSneaking;
	@Nullable
	private final Boolean isSprinting;
	@Nullable
	private final Boolean isSwimming;
	@Nullable
	private final Boolean isBaby;

	public EntityFlagsPredicate(
		@Nullable Boolean isOnFire, @Nullable Boolean isSneaking, @Nullable Boolean isSprinting, @Nullable Boolean isSwimming, @Nullable Boolean boolean_
	) {
		this.isOnFire = isOnFire;
		this.isSneaking = isSneaking;
		this.isSprinting = isSprinting;
		this.isSwimming = isSwimming;
		this.isBaby = boolean_;
	}

	public boolean test(Entity entity) {
		if (this.isOnFire != null && entity.isOnFire() != this.isOnFire) {
			return false;
		} else if (this.isSneaking != null && entity.isInSneakingPose() != this.isSneaking) {
			return false;
		} else if (this.isSprinting != null && entity.isSprinting() != this.isSprinting) {
			return false;
		} else {
			return this.isSwimming != null && entity.isSwimming() != this.isSwimming
				? false
				: this.isBaby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isBaby() == this.isBaby;
		}
	}

	@Nullable
	private static Boolean deserializeBoolean(JsonObject json, String key) {
		return json.has(key) ? JsonHelper.getBoolean(json, key) : null;
	}

	public static EntityFlagsPredicate deserialize(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "entity flags");
			Boolean boolean_ = deserializeBoolean(jsonObject, "is_on_fire");
			Boolean boolean2 = deserializeBoolean(jsonObject, "is_sneaking");
			Boolean boolean3 = deserializeBoolean(jsonObject, "is_sprinting");
			Boolean boolean4 = deserializeBoolean(jsonObject, "is_swimming");
			Boolean boolean5 = deserializeBoolean(jsonObject, "is_baby");
			return new EntityFlagsPredicate(boolean_, boolean2, boolean3, boolean4, boolean5);
		} else {
			return ANY;
		}
	}

	private void serializeBoolean(JsonObject json, String key, @Nullable Boolean boolean_) {
		if (boolean_ != null) {
			json.addProperty(key, boolean_);
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			this.serializeBoolean(jsonObject, "is_on_fire", this.isOnFire);
			this.serializeBoolean(jsonObject, "is_sneaking", this.isSneaking);
			this.serializeBoolean(jsonObject, "is_sprinting", this.isSprinting);
			this.serializeBoolean(jsonObject, "is_swimming", this.isSwimming);
			this.serializeBoolean(jsonObject, "is_baby", this.isBaby);
			return jsonObject;
		}
	}

	public static class Builder {
		@Nullable
		private Boolean isOnFire;
		@Nullable
		private Boolean isSneaking;
		@Nullable
		private Boolean isSprinting;
		@Nullable
		private Boolean isSwimming;
		@Nullable
		private Boolean isBaby;

		public static EntityFlagsPredicate.Builder create() {
			return new EntityFlagsPredicate.Builder();
		}

		public EntityFlagsPredicate.Builder onFire(@Nullable Boolean boolean_) {
			this.isOnFire = boolean_;
			return this;
		}

		public EntityFlagsPredicate build() {
			return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
		}
	}
}
