package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Phase {
	boolean method_6848();

	void method_6853();

	void method_6855();

	void method_6850(EnderCrystalEntity enderCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity);

	void beginPhase();

	void endPhase();

	float method_6846();

	float method_6847();

	PhaseType<? extends Phase> method_6849();

	@Nullable
	Vec3d method_6851();

	float modifyDamageTaken(DamageSource damageSource, float f);
}
