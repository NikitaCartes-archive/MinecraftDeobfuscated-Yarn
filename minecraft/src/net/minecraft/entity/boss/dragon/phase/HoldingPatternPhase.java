package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class HoldingPatternPhase extends AbstractPhase {
	private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility();
	@Nullable
	private Path path;
	@Nullable
	private Vec3d pathTarget;
	private boolean shouldFindNewPath;

	public HoldingPatternPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public PhaseType<HoldingPatternPhase> getType() {
		return PhaseType.HOLDING_PATTERN;
	}

	@Override
	public void serverTick() {
		double d = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.tickInRange();
		}
	}

	@Override
	public void beginPhase() {
		this.path = null;
		this.pathTarget = null;
	}

	@Nullable
	@Override
	public Vec3d getPathTarget() {
		return this.pathTarget;
	}

	private void tickInRange() {
		if (this.path != null && this.path.isFinished()) {
			BlockPos blockPos = this.dragon
				.getWorld()
				.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPortalFeature.offsetOrigin(this.dragon.getFightOrigin())));
			int i = this.dragon.getFight() == null ? 0 : this.dragon.getFight().getAliveEndCrystals();
			if (this.dragon.getRandom().nextInt(i + 3) == 0) {
				this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
				return;
			}

			PlayerEntity playerEntity = this.dragon
				.getWorld()
				.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, this.dragon, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			double d;
			if (playerEntity != null) {
				d = blockPos.getSquaredDistance(playerEntity.getPos()) / 512.0;
			} else {
				d = 64.0;
			}

			if (playerEntity != null && (this.dragon.getRandom().nextInt((int)(d + 2.0)) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
				this.strafePlayer(playerEntity);
				return;
			}
		}

		if (this.path == null || this.path.isFinished()) {
			int j = this.dragon.getNearestPathNodeIndex();
			int ix = j;
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.shouldFindNewPath = !this.shouldFindNewPath;
				ix = j + 6;
			}

			if (this.shouldFindNewPath) {
				ix++;
			} else {
				ix--;
			}

			if (this.dragon.getFight() != null && this.dragon.getFight().getAliveEndCrystals() >= 0) {
				ix %= 12;
				if (ix < 0) {
					ix += 12;
				}
			} else {
				ix -= 12;
				ix &= 7;
				ix += 12;
			}

			this.path = this.dragon.findPath(j, ix, null);
			if (this.path != null) {
				this.path.next();
			}
		}

		this.followPath();
	}

	private void strafePlayer(PlayerEntity player) {
		this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
		this.dragon.getPhaseManager().create(PhaseType.STRAFE_PLAYER).setTargetEntity(player);
	}

	private void followPath() {
		if (this.path != null && !this.path.isFinished()) {
			Vec3i vec3i = this.path.getCurrentNodePos();
			this.path.next();
			double d = (double)vec3i.getX();
			double e = (double)vec3i.getZ();

			double f;
			do {
				f = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
			} while (f < (double)vec3i.getY());

			this.pathTarget = new Vec3d(d, f, e);
		}
	}

	@Override
	public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {
		if (player != null && this.dragon.canTarget(player)) {
			this.strafePlayer(player);
		}
	}
}
