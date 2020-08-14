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
	private boolean field_7056;
	private Path field_7054;
	private Vec3d target;

	public TakeoffPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick() {
		if (!this.field_7056 && this.field_7054 != null) {
			BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
			if (!blockPos.isWithinDistance(this.dragon.getPos(), 10.0)) {
				this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
			}
		} else {
			this.field_7056 = false;
			this.method_6858();
		}
	}

	@Override
	public void beginPhase() {
		this.field_7056 = true;
		this.field_7054 = null;
		this.target = null;
	}

	private void method_6858() {
		int i = this.dragon.getNearestPathNodeIndex();
		Vec3d vec3d = this.dragon.method_6834(1.0F);
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

		this.field_7054 = this.dragon.findPath(i, j, null);
		this.method_6859();
	}

	private void method_6859() {
		if (this.field_7054 != null) {
			this.field_7054.next();
			if (!this.field_7054.isFinished()) {
				Vec3i vec3i = this.field_7054.method_31032();
				this.field_7054.next();

				double d;
				do {
					d = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
				} while (d < (double)vec3i.getY());

				this.target = new Vec3d((double)vec3i.getX(), d, (double)vec3i.getZ());
			}
		}
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.target;
	}

	@Override
	public PhaseType<TakeoffPhase> getType() {
		return PhaseType.TAKEOFF;
	}
}
