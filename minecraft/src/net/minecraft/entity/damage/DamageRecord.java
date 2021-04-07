package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class DamageRecord {
	private final DamageSource damageSource;
	private final int entityAge;
	private final float damage;
	private final float entityHealth;
	@Nullable
	private final String fallDeathSuffix;
	private final float fallDistance;

	public DamageRecord(DamageSource damageSource, int entityAge, float entityOriginalHealth, float damage, @Nullable String fallDeathSuffix, float fallDistance) {
		this.damageSource = damageSource;
		this.entityAge = entityAge;
		this.damage = damage;
		this.entityHealth = entityOriginalHealth;
		this.fallDeathSuffix = fallDeathSuffix;
		this.fallDistance = fallDistance;
	}

	public DamageSource getDamageSource() {
		return this.damageSource;
	}

	public int getEntityAge() {
		return this.entityAge;
	}

	public float getDamage() {
		return this.damage;
	}

	public float getEntityHealth() {
		return this.entityHealth;
	}

	public float getNewEntityHealth() {
		return this.entityHealth - this.damage;
	}

	public boolean isAttackerLiving() {
		return this.damageSource.getAttacker() instanceof LivingEntity;
	}

	@Nullable
	public String getFallDeathSuffix() {
		return this.fallDeathSuffix;
	}

	@Nullable
	public Text getAttackerName() {
		return this.getDamageSource().getAttacker() == null ? null : this.getDamageSource().getAttacker().getDisplayName();
	}

	@Nullable
	public Entity getAttacker() {
		return this.getDamageSource().getAttacker();
	}

	public float getFallDistance() {
		return this.damageSource == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
	}
}
