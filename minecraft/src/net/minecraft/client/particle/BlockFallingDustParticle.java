package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.RenderTypeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockFallingDustParticle extends Particle {
	private final float field_3810;
	private final float field_3809;

	protected BlockFallingDustParticle(World world, double d, double e, double f, float g, float h, float i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.colorRed = g;
		this.colorGreen = h;
		this.colorBlue = i;
		float j = 0.9F;
		this.size *= 0.75F;
		this.size *= 0.9F;
		this.field_3810 = this.size;
		this.maxAge = (int)(32.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * 0.9F);
		this.maxAge = Math.max(this.maxAge, 1);
		this.field_3809 = ((float)Math.random() - 0.5F) * 0.1F;
		this.field_3839 = (float)Math.random() * (float) (Math.PI * 2);
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.age + f) / (float)this.maxAge * 32.0F;
		l = MathHelper.clamp(l, 0.0F, 1.0F);
		this.size = this.field_3810 * l;
		super.buildGeometry(vertexBuffer, entity, f, g, h, i, j, k);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		this.field_3857 = this.field_3839;
		this.field_3839 = this.field_3839 + (float) Math.PI * this.field_3809 * 2.0F;
		if (this.onGround) {
			this.field_3857 = this.field_3839 = 0.0F;
		}

		this.setSpriteIndex(7 - this.age * 8 / this.maxAge);
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityY -= 0.003F;
		this.velocityY = Math.max(this.velocityY, -0.14F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements FactoryParticle<BlockStateParticle> {
		@Nullable
		public Particle createParticle(BlockStateParticle blockStateParticle, World world, double d, double e, double f, double g, double h, double i) {
			BlockState blockState = blockStateParticle.method_10278();
			if (!blockState.isAir() && blockState.getRenderType() == RenderTypeBlock.NONE) {
				return null;
			} else {
				int j = MinecraftClient.getInstance().getBlockColorMap().method_1691(blockState, world, new BlockPos(d, e, f));
				if (blockState.getBlock() instanceof FallingBlock) {
					j = ((FallingBlock)blockState.getBlock()).getColor(blockState);
				}

				float k = (float)(j >> 16 & 0xFF) / 255.0F;
				float l = (float)(j >> 8 & 0xFF) / 255.0F;
				float m = (float)(j & 0xFF) / 255.0F;
				return new BlockFallingDustParticle(world, d, e, f, k, l, m);
			}
		}
	}
}
