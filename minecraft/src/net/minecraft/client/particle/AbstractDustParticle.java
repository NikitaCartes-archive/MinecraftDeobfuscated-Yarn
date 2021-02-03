package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AbstractDustParticle<T extends AbstractDustParticleEffect> extends SpriteBillboardParticle {
	private final SpriteProvider field_28247;

	protected AbstractDustParticle(
		ClientWorld world, double d, double e, double f, double g, double h, double i, T abstractDustParticleEffect, SpriteProvider spriteProvider
	) {
		super(world, d, e, f, g, h, i);
		this.field_28786 = 0.96F;
		this.field_28787 = true;
		this.field_28247 = spriteProvider;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		float j = this.random.nextFloat() * 0.4F + 0.6F;
		this.colorRed = this.method_33076(abstractDustParticleEffect.getColor().getX(), j);
		this.colorGreen = this.method_33076(abstractDustParticleEffect.getColor().getY(), j);
		this.colorBlue = this.method_33076(abstractDustParticleEffect.getColor().getZ(), j);
		this.scale = this.scale * 0.75F * abstractDustParticleEffect.getScale();
		int k = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
		this.maxAge = (int)Math.max((float)k * abstractDustParticleEffect.getScale(), 1.0F);
		this.setSpriteForAge(spriteProvider);
	}

	protected float method_33076(float f, float g) {
		return (this.random.nextFloat() * 0.2F + 0.8F) * f * g;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteForAge(this.field_28247);
	}
}
