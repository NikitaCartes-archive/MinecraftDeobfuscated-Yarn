package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockCrackParticle extends SpriteBillboardParticle {
	private final BlockState block;
	private BlockPos blockPos;
	private final float field_17884;
	private final float field_17885;

	public BlockCrackParticle(World world, double d, double e, double f, double g, double h, double i, BlockState blockState) {
		super(world, d, e, f, g, h, i);
		this.block = blockState;
		this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(blockState));
		this.gravityStrength = 1.0F;
		this.colorRed = 0.6F;
		this.colorGreen = 0.6F;
		this.colorBlue = 0.6F;
		this.scale /= 2.0F;
		this.field_17884 = this.random.nextFloat() * 3.0F;
		this.field_17885 = this.random.nextFloat() * 3.0F;
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.TERRAIN_SHEET;
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
	protected float getMinU() {
		return this.sprite.getU((double)((this.field_17884 + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	protected float getMaxU() {
		return this.sprite.getU((double)(this.field_17884 / 4.0F * 16.0F));
	}

	@Override
	protected float getMinV() {
		return this.sprite.getV((double)(this.field_17885 / 4.0F * 16.0F));
	}

	@Override
	protected float getMaxV() {
		return this.sprite.getV((double)((this.field_17885 + 1.0F) / 4.0F * 16.0F));
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
		public Particle method_3109(
			BlockStateParticleParameters blockStateParticleParameters, World world, double d, double e, double f, double g, double h, double i
		) {
			BlockState blockState = blockStateParticleParameters.getBlockState();
			return !blockState.isAir() && blockState.getBlock() != Blocks.field_10008
				? new BlockCrackParticle(world, d, e, f, g, h, i, blockState).setBlockPosFromPosition()
				: null;
		}
	}
}
