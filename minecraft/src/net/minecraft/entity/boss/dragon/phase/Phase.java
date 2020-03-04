package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Phase {
	boolean isSittingOrHovering();

	void clientTick();

	void serverTick();

	void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player);

	void beginPhase();

	void endPhase();

	float getMaxYAcceleration();

	float method_6847();

	PhaseType<? extends Phase> getType();

	@Nullable
	Vec3d getTarget();

	float modifyDamageTaken(DamageSource damageSource, float f);
}
