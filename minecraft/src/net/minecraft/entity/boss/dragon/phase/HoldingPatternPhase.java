package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class HoldingPatternPhase extends AbstractPhase {
	private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
	private Path field_7043;
	private Vec3d field_7045;
	private boolean field_7044;

	public HoldingPatternPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public PhaseType<HoldingPatternPhase> getType() {
		return PhaseType.field_7069;
	}

	@Override
	public void serverTick() {
		double d = this.field_7045 == null ? 0.0 : this.field_7045.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.method_6841();
		}
	}

	@Override
	public void beginPhase() {
		this.field_7043 = null;
		this.field_7045 = null;
	}

	@Nullable
	@Override
	public Vec3d method_6851() {
		return this.field_7045;
	}

	private void method_6841() {
		if (this.field_7043 != null && this.field_7043.isFinished()) {
			BlockPos blockPos = this.dragon.field_6002.getTopPosition(Heightmap.Type.field_13203, new BlockPos(EndPortalFeature.ORIGIN));
			int i = this.dragon.method_6829() == null ? 0 : this.dragon.method_6829().getAliveEndCrystals();
			if (this.dragon.getRand().nextInt(i + 3) == 0) {
				this.dragon.getPhaseManager().setPhase(PhaseType.field_7071);
				return;
			}

			double d = 64.0;
			PlayerEntity playerEntity = this.dragon
				.field_6002
				.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			if (playerEntity != null) {
				d = blockPos.getSquaredDistance(playerEntity.method_19538(), true) / 512.0;
			}

			if (playerEntity != null
				&& !playerEntity.abilities.invulnerable
				&& (this.dragon.getRand().nextInt(MathHelper.abs((int)d) + 2) == 0 || this.dragon.getRand().nextInt(i + 2) == 0)) {
				this.method_6843(playerEntity);
				return;
			}
		}

		if (this.field_7043 == null || this.field_7043.isFinished()) {
			int j = this.dragon.method_6818();
			int ix = j;
			if (this.dragon.getRand().nextInt(8) == 0) {
				this.field_7044 = !this.field_7044;
				ix = j + 6;
			}

			if (this.field_7044) {
				ix++;
			} else {
				ix--;
			}

			if (this.dragon.method_6829() != null && this.dragon.method_6829().getAliveEndCrystals() >= 0) {
				ix %= 12;
				if (ix < 0) {
					ix += 12;
				}
			} else {
				ix -= 12;
				ix &= 7;
				ix += 12;
			}

			this.field_7043 = this.dragon.method_6833(j, ix, null);
			if (this.field_7043 != null) {
				this.field_7043.next();
			}
		}

		this.method_6842();
	}

	private void method_6843(PlayerEntity playerEntity) {
		this.dragon.getPhaseManager().setPhase(PhaseType.field_7076);
		this.dragon.getPhaseManager().create(PhaseType.field_7076).method_6862(playerEntity);
	}

	private void method_6842() {
		if (this.field_7043 != null && !this.field_7043.isFinished()) {
			Vec3d vec3d = this.field_7043.method_35();
			this.field_7043.next();
			double d = vec3d.x;
			double e = vec3d.z;

			double f;
			do {
				f = vec3d.y + (double)(this.dragon.getRand().nextFloat() * 20.0F);
			} while (f < vec3d.y);

			this.field_7045 = new Vec3d(d, f, e);
		}
	}

	@Override
	public void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity) {
		if (playerEntity != null && !playerEntity.abilities.invulnerable) {
			this.method_6843(playerEntity);
		}
	}
}
