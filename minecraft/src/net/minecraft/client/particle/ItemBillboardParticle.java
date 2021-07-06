package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class ItemBillboardParticle extends SpriteBillboardParticle {
	ItemBillboardParticle(ClientWorld clientWorld, double d, double e, double f, ItemConvertible itemConvertible) {
		super(clientWorld, d, e, f);
		this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModels().getModelParticleSprite(itemConvertible));
		this.gravityStrength = 0.0F;
		this.maxAge = 80;
		this.collidesWithWorld = false;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.TERRAIN_SHEET;
	}

	@Override
	public float getSize(float tickDelta) {
		return 0.5F;
	}

	@Environment(EnvType.CLIENT)
	public static class BarrierFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new ItemBillboardParticle(clientWorld, d, e, f, Blocks.BARRIER.asItem());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LightFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new ItemBillboardParticle(clientWorld, d, e, f, Items.LIGHT);
		}
	}
}
