package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.class_3033;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;

public class LandingPhase extends AbstractPhase {
	private Vec3d field_7046;

	public LandingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_6853() {
		Vec3d vec3d = this.dragon.method_6834(1.0F).normalize();
		vec3d.rotateY((float) (-Math.PI / 4));
		double d = this.dragon.partHead.x;
		double e = this.dragon.partHead.y + (double)(this.dragon.partHead.height / 2.0F);
		double f = this.dragon.partHead.z;

		for (int i = 0; i < 8; i++) {
			double g = d + this.dragon.getRand().nextGaussian() / 2.0;
			double h = e + this.dragon.getRand().nextGaussian() / 2.0;
			double j = f + this.dragon.getRand().nextGaussian() / 2.0;
			this.dragon
				.world
				.method_8406(
					ParticleTypes.field_11216,
					g,
					h,
					j,
					-vec3d.x * 0.08F + this.dragon.velocityX,
					-vec3d.y * 0.3F + this.dragon.velocityY,
					-vec3d.z * 0.08F + this.dragon.velocityZ
				);
			vec3d.rotateY((float) (Math.PI / 16));
		}
	}

	@Override
	public void method_6855() {
		if (this.field_7046 == null) {
			this.field_7046 = new Vec3d(this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, class_3033.field_13600));
		}

		if (this.field_7046.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z) < 1.0) {
			this.dragon.getPhaseManager().create(PhaseType.SITTING_FLAMING).method_6857();
			this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
		}
	}

	@Override
	public float method_6846() {
		return 1.5F;
	}

	@Override
	public float method_6847() {
		float f = MathHelper.sqrt(this.dragon.velocityX * this.dragon.velocityX + this.dragon.velocityZ * this.dragon.velocityZ) + 1.0F;
		float g = Math.min(f, 40.0F);
		return g / f;
	}

	@Override
	public void beginPhase() {
		this.field_7046 = null;
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.field_7046;
	}

	@Override
	public PhaseType<LandingPhase> getType() {
		return PhaseType.LANDING;
	}
}
