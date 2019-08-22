/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate EMPTY = Builder.create().build();
    private final Boolean isProjectile;
    private final Boolean isExplosion;
    private final Boolean bypassesArmor;
    private final Boolean bypassesInvulnerability;
    private final Boolean bypassesMagic;
    private final Boolean isFire;
    private final Boolean isMagic;
    private final Boolean isLightning;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;

    public DamageSourcePredicate(@Nullable Boolean boolean_, @Nullable Boolean boolean2, @Nullable Boolean boolean3, @Nullable Boolean boolean4, @Nullable Boolean boolean5, @Nullable Boolean boolean6, @Nullable Boolean boolean7, @Nullable Boolean boolean8, EntityPredicate entityPredicate, EntityPredicate entityPredicate2) {
        this.isProjectile = boolean_;
        this.isExplosion = boolean2;
        this.bypassesArmor = boolean3;
        this.bypassesInvulnerability = boolean4;
        this.bypassesMagic = boolean5;
        this.isFire = boolean6;
        this.isMagic = boolean7;
        this.isLightning = boolean8;
        this.directEntity = entityPredicate;
        this.sourceEntity = entityPredicate2;
    }

    public boolean test(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
        return this.test(serverPlayerEntity.getServerWorld(), new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z), damageSource);
    }

    public boolean test(ServerWorld serverWorld, Vec3d vec3d, DamageSource damageSource) {
        if (this == EMPTY) {
            return true;
        }
        if (this.isProjectile != null && this.isProjectile.booleanValue() != damageSource.isProjectile()) {
            return false;
        }
        if (this.isExplosion != null && this.isExplosion.booleanValue() != damageSource.isExplosive()) {
            return false;
        }
        if (this.bypassesArmor != null && this.bypassesArmor.booleanValue() != damageSource.bypassesArmor()) {
            return false;
        }
        if (this.bypassesInvulnerability != null && this.bypassesInvulnerability.booleanValue() != damageSource.doesDamageToCreative()) {
            return false;
        }
        if (this.bypassesMagic != null && this.bypassesMagic.booleanValue() != damageSource.isUnblockable()) {
            return false;
        }
        if (this.isFire != null && this.isFire.booleanValue() != damageSource.isFire()) {
            return false;
        }
        if (this.isMagic != null && this.isMagic.booleanValue() != damageSource.getMagic()) {
            return false;
        }
        if (this.isLightning != null && this.isLightning != (damageSource == DamageSource.LIGHTNING_BOLT)) {
            return false;
        }
        if (!this.directEntity.test(serverWorld, vec3d, damageSource.getSource())) {
            return false;
        }
        return this.sourceEntity.test(serverWorld, vec3d, damageSource.getAttacker());
    }

    public static DamageSourcePredicate deserialize(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage type");
        Boolean boolean_ = DamageSourcePredicate.getBoolean(jsonObject, "is_projectile");
        Boolean boolean2 = DamageSourcePredicate.getBoolean(jsonObject, "is_explosion");
        Boolean boolean3 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_armor");
        Boolean boolean4 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_invulnerability");
        Boolean boolean5 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_magic");
        Boolean boolean6 = DamageSourcePredicate.getBoolean(jsonObject, "is_fire");
        Boolean boolean7 = DamageSourcePredicate.getBoolean(jsonObject, "is_magic");
        Boolean boolean8 = DamageSourcePredicate.getBoolean(jsonObject, "is_lightning");
        EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("direct_entity"));
        EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("source_entity"));
        return new DamageSourcePredicate(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, entityPredicate, entityPredicate2);
    }

    @Nullable
    private static Boolean getBoolean(JsonObject jsonObject, String string) {
        return jsonObject.has(string) ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, string)) : null;
    }

    public JsonElement serialize() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.addProperty(jsonObject, "is_projectile", this.isProjectile);
        this.addProperty(jsonObject, "is_explosion", this.isExplosion);
        this.addProperty(jsonObject, "bypasses_armor", this.bypassesArmor);
        this.addProperty(jsonObject, "bypasses_invulnerability", this.bypassesInvulnerability);
        this.addProperty(jsonObject, "bypasses_magic", this.bypassesMagic);
        this.addProperty(jsonObject, "is_fire", this.isFire);
        this.addProperty(jsonObject, "is_magic", this.isMagic);
        this.addProperty(jsonObject, "is_lightning", this.isLightning);
        jsonObject.add("direct_entity", this.directEntity.serialize());
        jsonObject.add("source_entity", this.sourceEntity.serialize());
        return jsonObject;
    }

    private void addProperty(JsonObject jsonObject, String string, @Nullable Boolean boolean_) {
        if (boolean_ != null) {
            jsonObject.addProperty(string, boolean_);
        }
    }

    public static class Builder {
        private Boolean isProjectile;
        private Boolean isExplosion;
        private Boolean bypassesArmor;
        private Boolean bypassesInvulnerability;
        private Boolean bypassesMagic;
        private Boolean isFire;
        private Boolean isMagic;
        private Boolean isLightning;
        private EntityPredicate directEntity = EntityPredicate.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder projectile(Boolean boolean_) {
            this.isProjectile = boolean_;
            return this;
        }

        public Builder lightning(Boolean boolean_) {
            this.isLightning = boolean_;
            return this;
        }

        public Builder directEntity(EntityPredicate.Builder builder) {
            this.directEntity = builder.build();
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}

