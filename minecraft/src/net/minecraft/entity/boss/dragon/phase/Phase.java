package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Phase {
	boolean method_6848();

	void clientTick();

	void serverTick();

	void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity);

	void beginPhase();

	void endPhase();

	float method_6846();

	float method_6847();

	PhaseType<? extends Phase> getType();

	@Nullable
	Vec3d getTarget();

	float modifyDamageTaken(DamageSource damageSource, float f);
}
