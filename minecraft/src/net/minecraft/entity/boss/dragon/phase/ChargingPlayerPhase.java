package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChargingPlayerPhase extends AbstractPhase {
	private static final Logger LOGGER = LogManager.getLogger();
	private Vec3d target;
	private int field_7037;

	public ChargingPlayerPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick() {
		if (this.target == null) {
			LOGGER.warn("Aborting charge player as no target was set.");
			this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
		} else if (this.field_7037 > 0 && this.field_7037++ >= 10) {
			this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
		} else {
			double d = this.target.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
			if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
				this.field_7037++;
			}
		}
	}

	@Override
	public void beginPhase() {
		this.target = null;
		this.field_7037 = 0;
	}

	public void setTarget(Vec3d target) {
		this.target = target;
	}

	@Override
	public float getMaxYAcceleration() {
		return 3.0F;
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.target;
	}

	@Override
	public PhaseType<ChargingPlayerPhase> getType() {
		return PhaseType.CHARGING_PLAYER;
	}
}
