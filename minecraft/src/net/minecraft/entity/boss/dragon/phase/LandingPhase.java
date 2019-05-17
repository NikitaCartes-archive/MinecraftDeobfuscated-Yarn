package net.minecraft.entity.boss.dragon.phase;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class LandingPhase extends AbstractPhase {
	private Vec3d field_7046;

	public LandingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void clientTick() {
		Vec3d vec3d = this.dragon.method_6834(1.0F).normalize();
		vec3d.rotateY((float) (-Math.PI / 4));
		double d = this.dragon.partHead.x;
		double e = this.dragon.partHead.y + (double)(this.dragon.partHead.getHeight() / 2.0F);
		double f = this.dragon.partHead.z;

		for (int i = 0; i < 8; i++) {
			Random random = this.dragon.getRand();
			double g = d + random.nextGaussian() / 2.0;
			double h = e + random.nextGaussian() / 2.0;
			double j = f + random.nextGaussian() / 2.0;
			Vec3d vec3d2 = this.dragon.getVelocity();
			this.dragon.world.addParticle(ParticleTypes.field_11216, g, h, j, -vec3d.x * 0.08F + vec3d2.x, -vec3d.y * 0.3F + vec3d2.y, -vec3d.z * 0.08F + vec3d2.z);
			vec3d.rotateY((float) (Math.PI / 16));
		}
	}

	@Override
	public void serverTick() {
		if (this.field_7046 == null) {
			this.field_7046 = new Vec3d(this.dragon.world.getTopPosition(Heightmap.Type.field_13203, EndPortalFeature.ORIGIN));
		}

		if (this.field_7046.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z) < 1.0) {
			this.dragon.getPhaseManager().create(PhaseType.field_7072).method_6857();
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7081);
		}
	}

	@Override
	public float method_6846() {
		return 1.5F;
	}

	@Override
	public float method_6847() {
		float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.dragon.getVelocity())) + 1.0F;
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
		return PhaseType.field_7067;
	}
}
