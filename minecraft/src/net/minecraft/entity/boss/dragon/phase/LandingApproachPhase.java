package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class LandingApproachPhase extends AbstractPhase {
	private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(128.0);
	private Path field_7047;
	private Vec3d field_7048;

	public LandingApproachPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public PhaseType<LandingApproachPhase> getType() {
		return PhaseType.field_7071;
	}

	@Override
	public void beginPhase() {
		this.field_7047 = null;
		this.field_7048 = null;
	}

	@Override
	public void serverTick() {
		double d = this.field_7048 == null ? 0.0 : this.field_7048.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.method_6844();
		}
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.field_7048;
	}

	private void method_6844() {
		if (this.field_7047 == null || this.field_7047.isFinished()) {
			int i = this.dragon.method_6818();
			BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.field_13203, EndPortalFeature.ORIGIN);
			PlayerEntity playerEntity = this.dragon
				.world
				.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			int j;
			if (playerEntity != null) {
				Vec3d vec3d = new Vec3d(playerEntity.x, 0.0, playerEntity.z).normalize();
				j = this.dragon.method_6822(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
			} else {
				j = this.dragon.method_6822(40.0, (double)blockPos.getY(), 0.0);
			}

			PathNode pathNode = new PathNode(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.field_7047 = this.dragon.method_6833(i, j, pathNode);
			if (this.field_7047 != null) {
				this.field_7047.next();
			}
		}

		this.method_6845();
		if (this.field_7047 != null && this.field_7047.isFinished()) {
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7067);
		}
	}

	private void method_6845() {
		if (this.field_7047 != null && !this.field_7047.isFinished()) {
			Vec3d vec3d = this.field_7047.getCurrentPosition();
			this.field_7047.next();
			double d = vec3d.x;
			double e = vec3d.z;

			double f;
			do {
				f = vec3d.y + (double)(this.dragon.getRand().nextFloat() * 20.0F);
			} while (f < vec3d.y);

			this.field_7048 = new Vec3d(d, f, e);
		}
	}
}
