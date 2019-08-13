package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;

public abstract class AbstractSittingPhase extends AbstractPhase {
	public AbstractSittingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public boolean method_6848() {
		return true;
	}

	@Override
	public float modifyDamageTaken(DamageSource damageSource, float f) {
		if (damageSource.getSource() instanceof ProjectileEntity) {
			damageSource.getSource().setOnFireFor(1);
			return 0.0F;
		} else {
			return super.modifyDamageTaken(damageSource, f);
		}
	}
}
