package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TextComponent;

public class DamageRecord {
	private final DamageSource field_5837;
	private final int entityAge;
	private final float damage;
	private final float entityHealth;
	private final String fallDeathSuffix;
	private final float fallDistance;

	public DamageRecord(DamageSource damageSource, int i, float f, float g, String string, float h) {
		this.field_5837 = damageSource;
		this.entityAge = i;
		this.damage = g;
		this.entityHealth = f;
		this.fallDeathSuffix = string;
		this.fallDistance = h;
	}

	public DamageSource method_5499() {
		return this.field_5837;
	}

	public float getDamage() {
		return this.damage;
	}

	public boolean isAttackerLiving() {
		return this.field_5837.method_5529() instanceof LivingEntity;
	}

	@Nullable
	public String getFallDeathSuffix() {
		return this.fallDeathSuffix;
	}

	@Nullable
	public TextComponent method_5498() {
		return this.method_5499().method_5529() == null ? null : this.method_5499().method_5529().method_5476();
	}

	public float getFallDistance() {
		return this.field_5837 == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
	}
}
