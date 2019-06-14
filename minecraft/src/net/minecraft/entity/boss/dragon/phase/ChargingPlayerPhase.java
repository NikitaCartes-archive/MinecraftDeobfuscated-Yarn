package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChargingPlayerPhase extends AbstractPhase {
	private static final Logger LOGGER = LogManager.getLogger();
	private Vec3d field_7038;
	private int field_7037;

	public ChargingPlayerPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick() {
		if (this.field_7038 == null) {
			LOGGER.warn("Aborting charge player as no target was set.");
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7069);
		} else if (this.field_7037 > 0 && this.field_7037++ >= 10) {
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7069);
		} else {
			double d = this.field_7038.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
			if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
				this.field_7037++;
			}
		}
	}

	@Override
	public void beginPhase() {
		this.field_7038 = null;
		this.field_7037 = 0;
	}

	public void method_6840(Vec3d vec3d) {
		this.field_7038 = vec3d;
	}

	@Override
	public float method_6846() {
		return 3.0F;
	}

	@Nullable
	@Override
	public Vec3d method_6851() {
		return this.field_7038;
	}

	@Override
	public PhaseType<ChargingPlayerPhase> getType() {
		return PhaseType.field_7078;
	}
}
