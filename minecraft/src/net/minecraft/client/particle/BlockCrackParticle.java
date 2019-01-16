package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockCrackParticle extends Particle {
	private final BlockState block;
	private BlockPos blockPos;

	protected BlockCrackParticle(World world, double d, double e, double f, double g, double h, double i, BlockState blockState) {
		super(world, d, e, f, g, h, i);
		this.block = blockState;
		this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(blockState));
		this.gravityStrength = 1.0F;
		this.colorRed = 0.6F;
		this.colorGreen = 0.6F;
		this.colorBlue = 0.6F;
		this.size /= 2.0F;
	}

	public BlockCrackParticle setBlockPos(BlockPos blockPos) {
		this.blockPos = blockPos;
		if (this.block.getBlock() == Blocks.field_10219) {
			return this;
		} else {
			this.updateColor(blockPos);
			return this;
		}
	}

	public BlockCrackParticle setBlockPosFromPosition() {
		this.blockPos = new BlockPos(this.posX, this.posY, this.posZ);
		Block block = this.block.getBlock();
		if (block == Blocks.field_10219) {
			return this;
		} else {
			this.updateColor(this.blockPos);
			return this;
		}
	}

	protected void updateColor(@Nullable BlockPos blockPos) {
		int i = MinecraftClient.getInstance().getBlockColorMap().getRenderColor(this.block, this.world, blockPos, 0);
		this.colorRed *= (float)(i >> 16 & 0xFF) / 255.0F;
		this.colorGreen *= (float)(i >> 8 & 0xFF) / 255.0F;
		this.colorBlue *= (float)(i & 0xFF) / 255.0F;
	}

	@Override
	public int getParticleGroup() {
		return 1;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = 0.1F * this.size;
		float m = this.sprite.getU((double)(this.field_3865 / 4.0F * 16.0F));
		float n = this.sprite.getU((double)((this.field_3865 + 1.0F) / 4.0F * 16.0F));
		float o = this.sprite.getV((double)(this.field_3846 / 4.0F * 16.0F));
		float p = this.sprite.getV((double)((this.field_3846 + 1.0F) / 4.0F * 16.0F));
		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - cameraX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - cameraY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - cameraZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		bufferBuilder.vertex((double)(q - g * l - j * l), (double)(r - h * l), (double)(s - i * l - k * l))
			.texture((double)m, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)(q - g * l + j * l), (double)(r + h * l), (double)(s - i * l + k * l))
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)(q + g * l + j * l), (double)(r + h * l), (double)(s + i * l + k * l))
			.texture((double)n, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)(q + g * l - j * l), (double)(r - h * l), (double)(s + i * l - k * l))
			.texture((double)n, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
	}

	@Override
	public int getColorMultiplier(float f) {
		int i = super.getColorMultiplier(f);
		int j = 0;
		if (this.world.isBlockLoaded(this.blockPos)) {
			j = this.world.getLightmapIndex(this.blockPos, 0);
		}

		return i == 0 ? j : i;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<BlockStateParticleParameters> {
		public Particle createParticle(
			BlockStateParticleParameters blockStateParticleParameters, World world, double d, double e, double f, double g, double h, double i
		) {
			BlockState blockState = blockStateParticleParameters.getBlockState();
			return !blockState.isAir() && blockState.getBlock() != Blocks.field_10008
				? new BlockCrackParticle(world, d, e, f, g, h, i, blockState).setBlockPosFromPosition()
				: null;
		}
	}
}
