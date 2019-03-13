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
	private final EntityPredicate field_9534;
	private final EntityPredicate field_9539;

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
		this.field_9534 = entityPredicate;
		this.field_9539 = entityPredicate2;
	}

	public boolean method_8847(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
		return this.method_8845(serverPlayerEntity.getServerWorld(), new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z), damageSource);
	}

	public boolean method_8845(ServerWorld serverWorld, Vec3d vec3d, DamageSource damageSource) {
		if (this == EMPTY) {
			return true;
		} else if (this.isProjectile != null && this.isProjectile != damageSource.isProjectile()) {
			return false;
		} else if (this.isExplosion != null && this.isExplosion != damageSource.isExplosive()) {
			return false;
		} else if (this.bypassesArmor != null && this.bypassesArmor != damageSource.doesBypassArmor()) {
			return false;
		} else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != damageSource.doesDamageToCreative()) {
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
			return !this.field_9534.method_8909(serverWorld, vec3d, damageSource.method_5526())
				? false
				: this.field_9539.method_8909(serverWorld, vec3d, damageSource.method_5529());
		}
	}

	public static DamageSourcePredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage type");
			Boolean boolean_ = getBoolean(jsonObject, "is_projectile");
			Boolean boolean2 = getBoolean(jsonObject, "is_explosion");
			Boolean boolean3 = getBoolean(jsonObject, "bypasses_armor");
			Boolean boolean4 = getBoolean(jsonObject, "bypasses_invulnerability");
			Boolean boolean5 = getBoolean(jsonObject, "bypasses_magic");
			Boolean boolean6 = getBoolean(jsonObject, "is_fire");
			Boolean boolean7 = getBoolean(jsonObject, "is_magic");
			Boolean boolean8 = getBoolean(jsonObject, "is_lightning");
			EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("direct_entity"));
			EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("source_entity"));
			return new DamageSourcePredicate(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, entityPredicate, entityPredicate2);
		} else {
			return EMPTY;
		}
	}

	@Nullable
	private static Boolean getBoolean(JsonObject jsonObject, String string) {
		return jsonObject.has(string) ? JsonHelper.getBoolean(jsonObject, string) : null;
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
			jsonObject.add("direct_entity", this.field_9534.serialize());
			jsonObject.add("source_entity", this.field_9539.serialize());
			return jsonObject;
		}
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
		private EntityPredicate field_9544 = EntityPredicate.ANY;
		private EntityPredicate field_9545 = EntityPredicate.ANY;

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

		public DamageSourcePredicate.Builder method_8854(EntityPredicate.Builder builder) {
			this.field_9544 = builder.build();
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
				this.field_9544,
				this.field_9545
			);
		}
	}
}
