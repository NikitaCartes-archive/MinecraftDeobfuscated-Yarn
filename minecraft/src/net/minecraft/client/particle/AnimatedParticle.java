package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class AnimatedParticle extends SpriteBillboardParticle {
	protected final SpriteProvider spriteProvider;
	private final float upwardsAcceleration;
	private float resistance = 0.91F;
	private float targetColorRed;
	private float targetColorGreen;
	private float targetColorBlue;
	private boolean changesColor;

	protected AnimatedParticle(World world, double d, double e, double f, SpriteProvider spriteProvider, float g) {
		super(world, d, e, f);
		this.spriteProvider = spriteProvider;
		this.upwardsAcceleration = g;
	}

	public void setColor(int i) {
		float f = (float)((i & 0xFF0000) >> 16) / 255.0F;
		float g = (float)((i & 0xFF00) >> 8) / 255.0F;
		float h = (float)((i & 0xFF) >> 0) / 255.0F;
		float j = 1.0F;
		this.setColor(f * 1.0F, g * 1.0F, h * 1.0F);
	}

	public void setTargetColor(int i) {
		this.targetColorRed = (float)((i & 0xFF0000) >> 16) / 255.0F;
		this.targetColorGreen = (float)((i & 0xFF00) >> 8) / 255.0F;
		this.targetColorBlue = (float)((i & 0xFF) >> 0) / 255.0F;
		this.changesColor = true;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
			if (this.age > this.maxAge / 2) {
				this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
				if (this.changesColor) {
					this.colorRed = this.colorRed + (this.targetColorRed - this.colorRed) * 0.2F;
					this.colorGreen = this.colorGreen + (this.targetColorGreen - this.colorGreen) * 0.2F;
					this.colorBlue = this.colorBlue + (this.targetColorBlue - this.colorBlue) * 0.2F;
				}
			}

			this.velocityY = this.velocityY + (double)this.upwardsAcceleration;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX = this.velocityX * (double)this.resistance;
			this.velocityY = this.velocityY * (double)this.resistance;
			this.velocityZ = this.velocityZ * (double)this.resistance;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Override
	public int getColorMultiplier(float f) {
		return 15728880;
	}

	protected void setResistance(float f) {
		this.resistance = f;
	}
}
