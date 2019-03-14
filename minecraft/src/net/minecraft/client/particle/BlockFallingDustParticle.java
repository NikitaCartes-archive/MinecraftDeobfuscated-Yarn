package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockFallingDustParticle extends SpriteBillboardParticle {
	private final float field_3809;
	private final SpriteProvider field_17808;

	private BlockFallingDustParticle(World world, double d, double e, double f, float g, float h, float i, SpriteProvider spriteProvider) {
		super(world, d, e, f);
		this.field_17808 = spriteProvider;
		this.colorRed = g;
		this.colorGreen = h;
		this.colorBlue = i;
		float j = 0.9F;
		this.scale *= 0.67499995F;
		int k = (int)(32.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)Math.max((float)k * 0.9F, 1.0F);
		this.method_18142(spriteProvider);
		this.field_3809 = ((float)Math.random() - 0.5F) * 0.1F;
		this.angle = (float)Math.random() * (float) (Math.PI * 2);
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float method_18132(float f) {
		return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17808);
			this.prevAngle = this.angle;
			this.angle = this.angle + (float) Math.PI * this.field_3809 * 2.0F;
			if (this.onGround) {
				this.prevAngle = this.angle = 0.0F;
			}

			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityY -= 0.003F;
			this.velocityY = Math.max(this.velocityY, -0.14F);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<BlockStateParticleParameters> {
		private final SpriteProvider field_17809;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17809 = spriteProvider;
		}

		@Nullable
		public Particle method_3033(
			BlockStateParticleParameters blockStateParticleParameters, World world, double d, double e, double f, double g, double h, double i
		) {
			BlockState blockState = blockStateParticleParameters.getBlockState();
			if (!blockState.isAir() && blockState.getRenderType() == BlockRenderType.field_11455) {
				return null;
			} else {
				int j = MinecraftClient.getInstance().getBlockColorMap().method_1691(blockState, world, new BlockPos(d, e, f));
				if (blockState.getBlock() instanceof FallingBlock) {
					j = ((FallingBlock)blockState.getBlock()).getColor(blockState);
				}

				float k = (float)(j >> 16 & 0xFF) / 255.0F;
				float l = (float)(j >> 8 & 0xFF) / 255.0F;
				float m = (float)(j & 0xFF) / 255.0F;
				return new BlockFallingDustParticle(world, d, e, f, k, l, m, this.field_17809);
			}
		}
	}
}
