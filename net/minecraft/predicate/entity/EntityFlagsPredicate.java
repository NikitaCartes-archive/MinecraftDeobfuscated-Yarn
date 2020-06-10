/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class EntityFlagsPredicate {
    public static final EntityFlagsPredicate ANY = new Builder().build();
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

    public EntityFlagsPredicate(@Nullable Boolean isOnFire, @Nullable Boolean isSneaking, @Nullable Boolean isSprinting, @Nullable Boolean isSwimming, @Nullable Boolean isBaby) {
        this.isOnFire = isOnFire;
        this.isSneaking = isSneaking;
        this.isSprinting = isSprinting;
        this.isSwimming = isSwimming;
        this.isBaby = isBaby;
    }

    public boolean test(Entity entity) {
        if (this.isOnFire != null && entity.isOnFire() != this.isOnFire.booleanValue()) {
            return false;
        }
        if (this.isSneaking != null && entity.isInSneakingPose() != this.isSneaking.booleanValue()) {
            return false;
        }
        if (this.isSprinting != null && entity.isSprinting() != this.isSprinting.booleanValue()) {
            return false;
        }
        if (this.isSwimming != null && entity.isSwimming() != this.isSwimming.booleanValue()) {
            return false;
        }
        return this.isBaby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isBaby() == this.isBaby.booleanValue();
    }

    @Nullable
    private static Boolean nullableBooleanFromJson(JsonObject json, String key) {
        return json.has(key) ? Boolean.valueOf(JsonHelper.getBoolean(json, key)) : null;
    }

    public static EntityFlagsPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "entity flags");
        Boolean boolean_ = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_on_fire");
        Boolean boolean2 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_sneaking");
        Boolean boolean3 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_sprinting");
        Boolean boolean4 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_swimming");
        Boolean boolean5 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_baby");
        return new EntityFlagsPredicate(boolean_, boolean2, boolean3, boolean4, boolean5);
    }

    private void nullableBooleanToJson(JsonObject json, String key, @Nullable Boolean value) {
        if (value != null) {
            json.addProperty(key, value);
        }
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.nullableBooleanToJson(jsonObject, "is_on_fire", this.isOnFire);
        this.nullableBooleanToJson(jsonObject, "is_sneaking", this.isSneaking);
        this.nullableBooleanToJson(jsonObject, "is_sprinting", this.isSprinting);
        this.nullableBooleanToJson(jsonObject, "is_swimming", this.isSwimming);
        this.nullableBooleanToJson(jsonObject, "is_baby", this.isBaby);
        return jsonObject;
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

        public static Builder create() {
            return new Builder();
        }

        public Builder onFire(@Nullable Boolean onFire) {
            this.isOnFire = onFire;
            return this;
        }

        public Builder method_29935(@Nullable Boolean boolean_) {
            this.isBaby = boolean_;
            return this;
        }

        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}

