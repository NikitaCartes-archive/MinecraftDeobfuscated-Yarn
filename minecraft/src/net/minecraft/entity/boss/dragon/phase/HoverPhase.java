package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class HoverPhase extends AbstractPhase {
	@Nullable
	private Vec3d target;

	public HoverPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick(ServerWorld world) {
		if (this.target == null) {
			this.target = this.dragon.getPos();
		}
	}

	@Override
	public boolean isSittingOrHovering() {
		return true;
	}

	@Override
	public void beginPhase() {
		this.target = null;
	}

	@Override
	public float getMaxYAcceleration() {
		return 1.0F;
	}

	@Nullable
	@Override
	public Vec3d getPathTarget() {
		return this.target;
	}

	@Override
	public PhaseType<HoverPhase> getType() {
		return PhaseType.HOVER;
	}
}
