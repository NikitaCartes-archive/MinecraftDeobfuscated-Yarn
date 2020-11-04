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

    public DamageSourcePredicate(@Nullable Boolean isProjectile, @Nullable Boolean isExplosion, @Nullable Boolean bypassesArmor, @Nullable Boolean bypassesInvulnerability, @Nullable Boolean bypassesMagic, @Nullable Boolean isFire, @Nullable Boolean isMagic, @Nullable Boolean isLightning, EntityPredicate directEntity, EntityPredicate sourceEntity) {
        this.isProjectile = isProjectile;
        this.isExplosion = isExplosion;
        this.bypassesArmor = bypassesArmor;
        this.bypassesInvulnerability = bypassesInvulnerability;
        this.bypassesMagic = bypassesMagic;
        this.isFire = isFire;
        this.isMagic = isMagic;
        this.isLightning = isLightning;
        this.directEntity = directEntity;
        this.sourceEntity = sourceEntity;
    }

    public boolean test(ServerPlayerEntity player, DamageSource damageSource) {
        return this.test(player.getServerWorld(), player.getPos(), damageSource);
    }

    public boolean test(ServerWorld world, Vec3d pos, DamageSource damageSource) {
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
        if (this.bypassesInvulnerability != null && this.bypassesInvulnerability.booleanValue() != damageSource.isOutOfWorld()) {
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
        if (!this.directEntity.test(world, pos, damageSource.getSource())) {
            return false;
        }
        return this.sourceEntity.test(world, pos, damageSource.getAttacker());
    }

    public static DamageSourcePredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "damage type");
        Boolean boolean_ = DamageSourcePredicate.getBoolean(jsonObject, "is_projectile");
        Boolean boolean2 = DamageSourcePredicate.getBoolean(jsonObject, "is_explosion");
        Boolean boolean3 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_armor");
        Boolean boolean4 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_invulnerability");
        Boolean boolean5 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_magic");
        Boolean boolean6 = DamageSourcePredicate.getBoolean(jsonObject, "is_fire");
        Boolean boolean7 = DamageSourcePredicate.getBoolean(jsonObject, "is_magic");
        Boolean boolean8 = DamageSourcePredicate.getBoolean(jsonObject, "is_lightning");
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("direct_entity"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        return new DamageSourcePredicate(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, entityPredicate, entityPredicate2);
    }

    @Nullable
    private static Boolean getBoolean(JsonObject obj, String name) {
        return obj.has(name) ? Boolean.valueOf(JsonHelper.getBoolean(obj, name)) : null;
    }

    public JsonElement toJson() {
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
        jsonObject.add("direct_entity", this.directEntity.toJson());
        jsonObject.add("source_entity", this.sourceEntity.toJson());
        return jsonObject;
    }

    private void addProperty(JsonObject json, String key, @Nullable Boolean value) {
        if (value != null) {
            json.addProperty(key, value);
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

        public Builder projectile(Boolean projectile) {
            this.isProjectile = projectile;
            return this;
        }

        public Builder lightning(Boolean lightning) {
            this.isLightning = lightning;
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

