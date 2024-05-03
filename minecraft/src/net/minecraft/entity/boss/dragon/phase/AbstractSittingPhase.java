package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;

public abstract class AbstractSittingPhase extends AbstractPhase {
	public AbstractSittingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public boolean isSittingOrHovering() {
		return true;
	}

	@Override
	public float modifyDamageTaken(DamageSource damageSource, float damage) {
		if (!(damageSource.getSource() instanceof PersistentProjectileEntity) && !(damageSource.getSource() instanceof WindChargeEntity)) {
			return super.modifyDamageTaken(damageSource, damage);
		} else {
			damageSource.getSource().setOnFireFor(1.0F);
			return 0.0F;
		}
	}
}
