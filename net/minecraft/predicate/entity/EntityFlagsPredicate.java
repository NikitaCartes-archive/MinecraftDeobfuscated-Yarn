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

    public EntityFlagsPredicate(@Nullable Boolean boolean_, @Nullable Boolean boolean2, @Nullable Boolean boolean3, @Nullable Boolean boolean4, @Nullable Boolean boolean5) {
        this.isOnFire = boolean_;
        this.isSneaking = boolean2;
        this.isSprinting = boolean3;
        this.isSwimming = boolean4;
        this.isBaby = boolean5;
    }

    public boolean test(Entity entity) {
        if (this.isOnFire != null && entity.isOnFire() != this.isOnFire.booleanValue()) {
            return false;
        }
        if (this.isSneaking != null && entity.isSneaking() != this.isSneaking.booleanValue()) {
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
    private static Boolean deserializeBoolean(JsonObject jsonObject, String string) {
        return jsonObject.has(string) ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, string)) : null;
    }

    public static EntityFlagsPredicate deserialize(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entity flags");
        Boolean boolean_ = EntityFlagsPredicate.deserializeBoolean(jsonObject, "is_on_fire");
        Boolean boolean2 = EntityFlagsPredicate.deserializeBoolean(jsonObject, "is_sneaking");
        Boolean boolean3 = EntityFlagsPredicate.deserializeBoolean(jsonObject, "is_sprinting");
        Boolean boolean4 = EntityFlagsPredicate.deserializeBoolean(jsonObject, "is_swimming");
        Boolean boolean5 = EntityFlagsPredicate.deserializeBoolean(jsonObject, "is_baby");
        return new EntityFlagsPredicate(boolean_, boolean2, boolean3, boolean4, boolean5);
    }

    private void serializeBoolean(JsonObject jsonObject, String string, @Nullable Boolean boolean_) {
        if (boolean_ != null) {
            jsonObject.addProperty(string, boolean_);
        }
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.serializeBoolean(jsonObject, "is_on_fire", this.isOnFire);
        this.serializeBoolean(jsonObject, "is_sneaking", this.isSneaking);
        this.serializeBoolean(jsonObject, "is_sprinting", this.isSprinting);
        this.serializeBoolean(jsonObject, "is_swimming", this.isSwimming);
        this.serializeBoolean(jsonObject, "is_baby", this.isBaby);
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

        public Builder onFire(@Nullable Boolean boolean_) {
            this.isOnFire = boolean_;
            return this;
        }

        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}

