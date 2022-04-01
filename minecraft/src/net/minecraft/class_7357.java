package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class class_7357 extends SpriteBillboardParticle {
	private final SpriteProvider field_38665;

	class_7357(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(clientWorld, d, e, f, g, h, i);
		this.velocityMultiplier = 0.96F;
		this.field_38665 = spriteProvider;
		this.scale(1.5F);
		this.collidesWithWorld = false;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public int getBrightness(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteForAge(this.field_38665);
	}

	@Environment(EnvType.CLIENT)
	public static record class_7358(SpriteProvider sprite) implements ParticleFactory<class_7365> {
		public Particle createParticle(class_7365 arg, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			class_7357 lv = new class_7357(clientWorld, d, e, f, g, h, i, this.sprite);
			lv.setAlpha(1.0F);
			lv.setVelocity(g, h, i);
			lv.prevAngle = arg.roll();
			lv.angle = arg.roll();
			lv.setMaxAge(clientWorld.random.nextInt(12) + 8);
			return lv;
		}
	}
}
