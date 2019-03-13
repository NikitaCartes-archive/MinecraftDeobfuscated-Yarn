package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class AnimatedParticle extends SpriteBillboardParticle {
	protected final class_4002 field_17866;
	private final float field_3881;
	private float field_3879 = 0.91F;
	private float targetColorRed;
	private float targetColorGreen;
	private float targetColorBlue;
	private boolean changesColor;

	protected AnimatedParticle(World world, double d, double e, double f, class_4002 arg, float g) {
		super(world, d, e, f);
		this.field_17866 = arg;
		this.field_3881 = g;
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
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17866);
			if (this.age > this.maxAge / 2) {
				this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
				if (this.changesColor) {
					this.colorRed = this.colorRed + (this.targetColorRed - this.colorRed) * 0.2F;
					this.colorGreen = this.colorGreen + (this.targetColorGreen - this.colorGreen) * 0.2F;
					this.colorBlue = this.colorBlue + (this.targetColorBlue - this.colorBlue) * 0.2F;
				}
			}

			this.velocityY = this.velocityY + (double)this.field_3881;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX = this.velocityX * (double)this.field_3879;
			this.velocityY = this.velocityY * (double)this.field_3879;
			this.velocityZ = this.velocityZ * (double)this.field_3879;
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

	protected void method_3091(float f) {
		this.field_3879 = f;
	}
}
