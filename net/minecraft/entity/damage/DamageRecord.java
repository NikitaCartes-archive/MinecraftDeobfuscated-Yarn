/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DamageRecord {
    private final DamageSource damageSource;
    private final int entityAge;
    private final float damage;
    private final float entityHealth;
    private final String fallDeathSuffix;
    private final float fallDistance;

    public DamageRecord(DamageSource damageSource, int entityAge, float entityOriginalHealth, float damage, String fallDeathSuffix, float f) {
        this.damageSource = damageSource;
        this.entityAge = entityAge;
        this.damage = damage;
        this.entityHealth = entityOriginalHealth;
        this.fallDeathSuffix = fallDeathSuffix;
        this.fallDistance = f;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public float getDamage() {
        return this.damage;
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

    public float getFallDistance() {
        if (this.damageSource == DamageSource.OUT_OF_WORLD) {
            return Float.MAX_VALUE;
        }
        return this.fallDistance;
    }
}

