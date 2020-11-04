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
	private Vec3d target;

	public LandingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void clientTick() {
		Vec3d vec3d = this.dragon.method_6834(1.0F).normalize();
		vec3d.rotateY((float) (-Math.PI / 4));
		double d = this.dragon.partHead.getX();
		double e = this.dragon.partHead.getBodyY(0.5);
		double f = this.dragon.partHead.getZ();

		for (int i = 0; i < 8; i++) {
			Random random = this.dragon.getRandom();
			double g = d + random.nextGaussian() / 2.0;
			double h = e + random.nextGaussian() / 2.0;
			double j = f + random.nextGaussian() / 2.0;
			Vec3d vec3d2 = this.dragon.getVelocity();
			this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, g, h, j, -vec3d.x * 0.08F + vec3d2.x, -vec3d.y * 0.3F + vec3d2.y, -vec3d.z * 0.08F + vec3d2.z);
			vec3d.rotateY((float) (Math.PI / 16));
		}
	}

	@Override
	public void serverTick() {
		if (this.target == null) {
			this.target = Vec3d.ofBottomCenter(this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN));
		}

		if (this.target.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0) {
			this.dragon.getPhaseManager().create(PhaseType.SITTING_FLAMING).method_6857();
			this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
		}
	}

	@Override
	public float getMaxYAcceleration() {
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
		this.target = null;
	}

	@Nullable
	@Override
	public Vec3d getTarget() {
		return this.target;
	}

	@Override
	public PhaseType<LandingPhase> getType() {
		return PhaseType.LANDING;
	}
}
