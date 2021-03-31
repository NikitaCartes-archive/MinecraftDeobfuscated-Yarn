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
		@Nullable Boolean isProjectile,
		@Nullable Boolean isExplosion,
		@Nullable Boolean bypassesArmor,
		@Nullable Boolean bypassesInvulnerability,
		@Nullable Boolean bypassesMagic,
		@Nullable Boolean isFire,
		@Nullable Boolean isMagic,
		@Nullable Boolean isLightning,
		EntityPredicate directEntity,
		EntityPredicate sourceEntity
	) {
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
		} else if (this.isMagic != null && this.isMagic != damageSource.isMagic()) {
			return false;
		} else if (this.isLightning != null && this.isLightning != (damageSource == DamageSource.LIGHTNING_BOLT)) {
			return false;
		} else {
			return !this.directEntity.test(world, pos, damageSource.getSource()) ? false : this.sourceEntity.test(world, pos, damageSource.getAttacker());
		}
	}

	public static DamageSourcePredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "damage type");
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

	public JsonElement toJson() {
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
			jsonObject.add("direct_entity", this.directEntity.toJson());
			jsonObject.add("source_entity", this.sourceEntity.toJson());
			return jsonObject;
		}
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

		public static DamageSourcePredicate.Builder create() {
			return new DamageSourcePredicate.Builder();
		}

		public DamageSourcePredicate.Builder projectile(Boolean projectile) {
			this.isProjectile = projectile;
			return this;
		}

		public DamageSourcePredicate.Builder explosion(Boolean explosion) {
			this.isExplosion = explosion;
			return this;
		}

		public DamageSourcePredicate.Builder bypassesArmor(Boolean bypassesArmor) {
			this.bypassesArmor = bypassesArmor;
			return this;
		}

		public DamageSourcePredicate.Builder bypassesInvulnerability(Boolean bypassesInvulnerability) {
			this.bypassesInvulnerability = bypassesInvulnerability;
			return this;
		}

		public DamageSourcePredicate.Builder bypassesMagic(Boolean bypassesMagic) {
			this.bypassesMagic = bypassesMagic;
			return this;
		}

		public DamageSourcePredicate.Builder fire(Boolean fire) {
			this.isFire = fire;
			return this;
		}

		public DamageSourcePredicate.Builder magic(Boolean magic) {
			this.isMagic = magic;
			return this;
		}

		public DamageSourcePredicate.Builder lightning(Boolean lightning) {
			this.isLightning = lightning;
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate entity) {
			this.directEntity = entity;
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate.Builder entity) {
			this.directEntity = entity.build();
			return this;
		}

		public DamageSourcePredicate.Builder sourceEntity(EntityPredicate entity) {
			this.sourceEntity = entity;
			return this;
		}

		public DamageSourcePredicate.Builder sourceEntity(EntityPredicate.Builder entity) {
			this.sourceEntity = entity.build();
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
