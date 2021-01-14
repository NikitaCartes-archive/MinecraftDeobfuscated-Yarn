/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

public abstract class AbstractSittingPhase
extends AbstractPhase {
    public AbstractSittingPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public boolean isSittingOrHovering() {
        return true;
    }

    @Override
    public float modifyDamageTaken(DamageSource damageSource, float damage) {
        if (damageSource.getSource() instanceof PersistentProjectileEntity) {
            damageSource.getSource().setOnFireFor(1);
            return 0.0f;
        }
        return super.modifyDamageTaken(damageSource, damage);
    }
}

