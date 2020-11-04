package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractPhase implements Phase {
	protected final EnderDragonEntity dragon;

	public AbstractPhase(EnderDragonEntity dragon) {
		this.dragon = dragon;
	}

	@Override
	public boolean isSittingOrHovering() {
		return false;
	}

	@Override
	public void clientTick() {
	}

	@Override
	public void serverTick() {
	}

	@Override
	public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {
	}

	@Override
	public void beginPhase() {
	}

	@Override
	public void endPhase() {
	}

	@Override
	public float getMaxYAcceleration() {
		return 0.6F;
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return null;
	}

	@Override
	public float modifyDamageTaken(DamageSource damageSource, float f) {
		return f;
	}

	@Override
	public float method_6847() {
		float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.dragon.getVelocity())) + 1.0F;
		float g = Math.min(f, 40.0F);
		return 0.7F / g / f;
	}
}
