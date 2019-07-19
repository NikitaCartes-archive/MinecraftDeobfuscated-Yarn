package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class DamageSourcePredicate {
	public static final DamageSourcePredicate EMPTY = DamageSourcePredicate.Builder.create().build();
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

	public DamageSourcePredicate(
		@Nullable Boolean boolean_,
		@Nullable Boolean boolean2,
		@Nullable Boolean boolean3,
		@Nullable Boolean boolean4,
		@Nullable Boolean boolean5,
		@Nullable Boolean boolean6,
		@Nullable Boolean boolean7,
		@Nullable Boolean boolean8,
		EntityPredicate entityPredicate,
		EntityPredicate entityPredicate2
	) {
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

	public boolean test(ServerPlayerEntity player, DamageSource damageSource) {
		return this.test(player.getServerWorld(), new Vec3d(player.x, player.y, player.z), damageSource);
	}

	public boolean test(ServerWorld world, Vec3d pos, DamageSource damageSource) {
		if (this == EMPTY) {
			return true;
		} else if (this.isProjectile != null && this.isProjectile != damageSource.isProjectile()) {
			return false;
		} else if (this.isExplosion != null && this.isExplosion != damageSource.isExplosive()) {
			return false;
		} else if (this.bypassesArmor != null && this.bypassesArmor != damageSource.bypassesArmor()) {
			return false;
		} else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != damageSource.isOutOfWorld()) {
			return false;
		} else if (this.bypassesMagic != null && this.bypassesMagic != damageSource.isUnblockable()) {
			return false;
		} else if (this.isFire != null && this.isFire != damageSource.isFire()) {
			return false;
		} else if (this.isMagic != null && this.isMagic != damageSource.getMagic()) {
			return false;
		} else if (this.isLightning != null && this.isLightning != (damageSource == DamageSource.LIGHTNING_BOLT)) {
			return false;
		} else {
			return !this.directEntity.test(world, pos, damageSource.getSource()) ? false : this.sourceEntity.test(world, pos, damageSource.getAttacker());
		}
	}

	public static DamageSourcePredicate deserialize(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "damage type");
			Boolean boolean_ = getBoolean(jsonObject, "is_projectile");
			Boolean boolean2 = getBoolean(jsonObject, "is_explosion");
			Boolean boolean3 = getBoolean(jsonObject, "bypasses_armor");
			Boolean boolean4 = getBoolean(jsonObject, "bypasses_invulnerability");
			Boolean boolean5 = getBoolean(jsonObject, "bypasses_magic");
			Boolean boolean6 = getBoolean(jsonObject, "is_fire");
			Boolean boolean7 = getBoolean(jsonObject, "is_magic");
			Boolean boolean8 = getBoolean(jsonObject, "is_lightning");
			EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("direct_entity"));
			EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
			return new DamageSourcePredicate(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, entityPredicate, entityPredicate2);
		} else {
			return EMPTY;
		}
	}

	@Nullable
	private static Boolean getBoolean(JsonObject obj, String name) {
		return obj.has(name) ? JsonHelper.getBoolean(obj, name) : null;
	}

	public JsonElement serialize() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
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
	}

	private void addProperty(JsonObject json, String key, @Nullable Boolean boolean_) {
		if (boolean_ != null) {
			json.addProperty(key, boolean_);
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

		public static DamageSourcePredicate.Builder create() {
			return new DamageSourcePredicate.Builder();
		}

		public DamageSourcePredicate.Builder projectile(Boolean boolean_) {
			this.isProjectile = boolean_;
			return this;
		}

		public DamageSourcePredicate.Builder lightning(Boolean boolean_) {
			this.isLightning = boolean_;
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate.Builder builder) {
			this.directEntity = builder.build();
			return this;
		}

		public DamageSourcePredicate build() {
			return new DamageSourcePredicate(
				this.isProjectile,
				this.isExplosion,
				this.bypassesArmor,
				this.bypassesInvulnerability,
				this.bypassesMagic,
				this.isFire,
				this.isMagic,
				this.isLightning,
				this.directEntity,
				this.sourceEntity
			);
		}
	}
}
