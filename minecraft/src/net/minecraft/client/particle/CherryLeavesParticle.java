package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class CherryLeavesParticle extends SpriteBillboardParticle {
	private static final float field_43372 = 0.0025F;
	private static final int field_43373 = 300;
	private static final int field_43366 = 300;
	private static final float field_43367 = 0.25F;
	private static final float field_43368 = 2.0F;
	private float field_43369;
	private final float field_43370;
	private final float field_43371;

	protected CherryLeavesParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
		super(world, x, y, z);
		this.setSprite(spriteProvider.getSprite(this.random.nextInt(12), 12));
		this.field_43369 = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
		this.field_43370 = this.random.nextFloat();
		this.field_43371 = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
		this.maxAge = 300;
		this.gravityStrength = 7.5E-4F;
		float f = this.random.nextBoolean() ? 0.05F : 0.075F;
		this.scale = f;
		this.setBoundingBoxSpacing(f, f);
		this.velocityMultiplier = 1.0F;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.maxAge-- <= 0) {
			this.markDead();
		}

		if (!this.dead) {
			float f = (float)(300 - this.maxAge);
			float g = Math.min(f / 300.0F, 1.0F);
			double d = Math.cos(Math.toRadians((double)(this.field_43370 * 60.0F))) * 2.0 * Math.pow((double)g, 1.25);
			double e = Math.sin(Math.toRadians((double)(this.field_43370 * 60.0F))) * 2.0 * Math.pow((double)g, 1.25);
			this.velocityX += d * 0.0025F;
			this.velocityZ += e * 0.0025F;
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.field_43369 = this.field_43369 + this.field_43371 / 20.0F;
			this.prevAngle = this.angle;
			this.angle = this.angle + this.field_43369 / 20.0F;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.onGround || this.maxAge < 299 && (this.velocityX == 0.0 || this.velocityZ == 0.0)) {
				this.markDead();
			}

			if (!this.dead) {
				this.velocityX = this.velocityX * (double)this.velocityMultiplier;
				this.velocityY = this.velocityY * (double)this.velocityMultiplier;
				this.velocityZ = this.velocityZ * (double)this.velocityMultiplier;
			}
		}
	}
}
