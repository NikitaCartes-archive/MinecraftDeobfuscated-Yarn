package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class AnimatedParticle extends SpriteBillboardParticle {
	protected final SpriteProvider spriteProvider;
	private final float upwardsAcceleration;
	private float resistance = 0.91F;
	private float targetColorRed;
	private float targetColorGreen;
	private float targetColorBlue;
	private boolean changesColor;

	protected AnimatedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
		super(world, x, y, z);
		this.spriteProvider = spriteProvider;
		this.upwardsAcceleration = upwardsAcceleration;
	}

	public void setColor(int rgbHex) {
		float f = (float)((rgbHex & 0xFF0000) >> 16) / 255.0F;
		float g = (float)((rgbHex & 0xFF00) >> 8) / 255.0F;
		float h = (float)((rgbHex & 0xFF) >> 0) / 255.0F;
		float i = 1.0F;
		this.setColor(f * 1.0F, g * 1.0F, h * 1.0F);
	}

	public void setTargetColor(int rgbHex) {
		this.targetColorRed = (float)((rgbHex & 0xFF0000) >> 16) / 255.0F;
		this.targetColorGreen = (float)((rgbHex & 0xFF00) >> 8) / 255.0F;
		this.targetColorBlue = (float)((rgbHex & 0xFF) >> 0) / 255.0F;
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
	public int getBrightness(float tint) {
		return 15728880;
	}

	protected void setResistance(float resistance) {
		this.resistance = resistance;
	}
}
