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
		@Nullable Boolean isOnFire, @Nullable Boolean isSneaking, @Nullable Boolean isSprinting, @Nullable Boolean isSwimming, @Nullable Boolean isBaby
	) {
		this.isOnFire = isOnFire;
		this.isSneaking = isSneaking;
		this.isSprinting = isSprinting;
		this.isSwimming = isSwimming;
		this.isBaby = isBaby;
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
	private static Boolean nullableBooleanFromJson(JsonObject json, String key) {
		return json.has(key) ? JsonHelper.getBoolean(json, key) : null;
	}

	public static EntityFlagsPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "entity flags");
			Boolean boolean_ = nullableBooleanFromJson(jsonObject, "is_on_fire");
			Boolean boolean2 = nullableBooleanFromJson(jsonObject, "is_sneaking");
			Boolean boolean3 = nullableBooleanFromJson(jsonObject, "is_sprinting");
			Boolean boolean4 = nullableBooleanFromJson(jsonObject, "is_swimming");
			Boolean boolean5 = nullableBooleanFromJson(jsonObject, "is_baby");
			return new EntityFlagsPredicate(boolean_, boolean2, boolean3, boolean4, boolean5);
		} else {
			return ANY;
		}
	}

	private void nullableBooleanToJson(JsonObject json, String key, @Nullable Boolean value) {
		if (value != null) {
			json.addProperty(key, value);
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			this.nullableBooleanToJson(jsonObject, "is_on_fire", this.isOnFire);
			this.nullableBooleanToJson(jsonObject, "is_sneaking", this.isSneaking);
			this.nullableBooleanToJson(jsonObject, "is_sprinting", this.isSprinting);
			this.nullableBooleanToJson(jsonObject, "is_swimming", this.isSwimming);
			this.nullableBooleanToJson(jsonObject, "is_baby", this.isBaby);
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

		public EntityFlagsPredicate.Builder onFire(@Nullable Boolean onFire) {
			this.isOnFire = onFire;
			return this;
		}

		public EntityFlagsPredicate.Builder sneaking(@Nullable Boolean sneaking) {
			this.isSneaking = sneaking;
			return this;
		}

		public EntityFlagsPredicate.Builder sprinting(@Nullable Boolean sprinting) {
			this.isSprinting = sprinting;
			return this;
		}

		public EntityFlagsPredicate.Builder swimming(@Nullable Boolean swimming) {
			this.isSwimming = swimming;
			return this;
		}

		public EntityFlagsPredicate.Builder isBaby(@Nullable Boolean isBaby) {
			this.isBaby = isBaby;
			return this;
		}

		public EntityFlagsPredicate build() {
			return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
		}
	}
}
