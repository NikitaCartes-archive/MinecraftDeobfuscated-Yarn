package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class HoldingPatternPhase extends AbstractPhase {
	private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
	private Path field_7043;
	private Vec3d target;
	private boolean field_7044;

	public HoldingPatternPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public PhaseType<HoldingPatternPhase> getType() {
		return PhaseType.HOLDING_PATTERN;
	}

	@Override
	public void serverTick() {
		double d = this.target == null ? 0.0 : this.target.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.method_6841();
		}
	}

	@Override
	public void beginPhase() {
		this.field_7043 = null;
		this.target = null;
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.target;
	}

	private void method_6841() {
		if (this.field_7043 != null && this.field_7043.isFinished()) {
			BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPortalFeature.ORIGIN));
			int i = this.dragon.getFight() == null ? 0 : this.dragon.getFight().getAliveEndCrystals();
			if (this.dragon.getRandom().nextInt(i + 3) == 0) {
				this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
				return;
			}

			double d = 64.0;
			PlayerEntity playerEntity = this.dragon
				.world
				.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			if (playerEntity != null) {
				d = blockPos.getSquaredDistance(playerEntity.getPos(), true) / 512.0;
			}

			if (playerEntity != null
				&& !playerEntity.getAbilities().invulnerable
				&& (this.dragon.getRandom().nextInt(MathHelper.abs((int)d) + 2) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
				this.method_6843(playerEntity);
				return;
			}
		}

		if (this.field_7043 == null || this.field_7043.isFinished()) {
			int j = this.dragon.getNearestPathNodeIndex();
			int ix = j;
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.field_7044 = !this.field_7044;
				ix = j + 6;
			}

			if (this.field_7044) {
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

			this.field_7043 = this.dragon.findPath(j, ix, null);
			if (this.field_7043 != null) {
				this.field_7043.next();
			}
		}

		this.method_6842();
	}

	private void method_6843(PlayerEntity playerEntity) {
		this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
		this.dragon.getPhaseManager().create(PhaseType.STRAFE_PLAYER).method_6862(playerEntity);
	}

	private void method_6842() {
		if (this.field_7043 != null && !this.field_7043.isFinished()) {
			Vec3i vec3i = this.field_7043.method_31032();
			this.field_7043.next();
			double d = (double)vec3i.getX();
			double e = (double)vec3i.getZ();

			double f;
			do {
				f = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
			} while (f < (double)vec3i.getY());

			this.target = new Vec3d(d, f, e);
		}
	}

	@Override
	public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {
		if (player != null && !player.getAbilities().invulnerable) {
			this.method_6843(player);
		}
	}
}
