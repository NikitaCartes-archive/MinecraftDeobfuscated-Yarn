package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;

public class HoverPhase extends AbstractPhase {
	private Vec3d field_7042;

	public HoverPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_6855() {
		if (this.field_7042 == null) {
			this.field_7042 = new Vec3d(this.dragon.x, this.dragon.y, this.dragon.z);
		}
	}

	@Override
	public boolean method_6848() {
		return true;
	}

	@Override
	public void beginPhase() {
		this.field_7042 = null;
	}

	@Override
	public float method_6846() {
		return 1.0F;
	}

	@Nullable
	@Override
	public Vec3d method_6851() {
		return this.field_7042;
	}

	@Override
	public PhaseType<HoverPhase> method_6849() {
		return PhaseType.HOVER;
	}
}
