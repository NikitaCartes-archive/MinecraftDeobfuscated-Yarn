package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class TakeoffPhase extends AbstractPhase {
	private boolean shouldFindNewPath;
	private Path path;
	private Vec3d pathTarget;

	public TakeoffPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick() {
		if (!this.shouldFindNewPath && this.path != null) {
			BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
			if (!blockPos.isWithinDistance(this.dragon.getPos(), 10.0)) {
				this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
			}
		} else {
			this.shouldFindNewPath = false;
			this.updatePath();
		}
	}

	@Override
	public void beginPhase() {
		this.shouldFindNewPath = true;
		this.path = null;
		this.pathTarget = null;
	}

	private void updatePath() {
		int i = this.dragon.getNearestPathNodeIndex();
		Vec3d vec3d = this.dragon.getRotationVectorFromPhase(1.0F);
		int j = this.dragon.getNearestPathNodeIndex(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
		if (this.dragon.getFight() != null && this.dragon.getFight().getAliveEndCrystals() > 0) {
			j %= 12;
			if (j < 0) {
				j += 12;
			}
		} else {
			j -= 12;
			j &= 7;
			j += 12;
		}

		this.path = this.dragon.findPath(i, j, null);
		this.followPath();
	}

	private void followPath() {
		if (this.path != null) {
			this.path.next();
			if (!this.path.isFinished()) {
				Vec3i vec3i = this.path.getCurrentNodePos();
				this.path.next();

				double d;
				do {
					d = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
				} while (d < (double)vec3i.getY());

				this.pathTarget = new Vec3d((double)vec3i.getX(), d, (double)vec3i.getZ());
			}
		}
	}

	@Nullable
	@Override
	public Vec3d getPathTarget() {
		return this.pathTarget;
	}

	@Override
	public PhaseType<TakeoffPhase> getType() {
		return PhaseType.TAKEOFF;
	}
}
