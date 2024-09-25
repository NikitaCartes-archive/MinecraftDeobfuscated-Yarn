package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Phase {
	boolean isSittingOrHovering();

	void clientTick();

	void serverTick(ServerWorld world);

	void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player);

	void beginPhase();

	void endPhase();

	float getMaxYAcceleration();

	float getYawAcceleration();

	PhaseType<? extends Phase> getType();

	@Nullable
	Vec3d getPathTarget();

	float modifyDamageTaken(DamageSource damageSource, float damage);
}
