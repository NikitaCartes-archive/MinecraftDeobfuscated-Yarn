package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class TakeoffPhase extends AbstractPhase {
	private boolean field_7056;
	private Path field_7054;
	private Vec3d field_7055;

	public TakeoffPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick() {
		if (!this.field_7056 && this.field_7054 != null) {
			BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.field_13203, EndPortalFeature.ORIGIN);
			if (!blockPos.isWithinDistance(this.dragon.getPos(), 10.0)) {
				this.dragon.getPhaseManager().setPhase(PhaseType.field_7069);
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
		this.field_7055 = null;
	}

	private void method_6858() {
		int i = this.dragon.method_6818();
		Vec3d vec3d = this.dragon.method_6834(1.0F);
		int j = this.dragon.method_6822(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
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

		this.field_7054 = this.dragon.method_6833(i, j, null);
		this.method_6859();
	}

	private void method_6859() {
		if (this.field_7054 != null) {
			this.field_7054.next();
			if (!this.field_7054.isFinished()) {
				Vec3d vec3d = this.field_7054.getCurrentPosition();
				this.field_7054.next();

				double d;
				do {
					d = vec3d.y + (double)(this.dragon.getRand().nextFloat() * 20.0F);
				} while (d < vec3d.y);

				this.field_7055 = new Vec3d(vec3d.x, d, vec3d.z);
			}
		}
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.field_7055;
	}

	@Override
	public PhaseType<TakeoffPhase> getType() {
		return PhaseType.field_7077;
	}
}
