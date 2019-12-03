package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndGatewayBlock extends BlockWithEntity {
	protected EndGatewayBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new EndGatewayBlockEntity();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			int i = ((EndGatewayBlockEntity)blockEntity).getDrawnSidesCount();

			for (int j = 0; j < i; j++) {
				double d = (double)pos.getX() + (double)random.nextFloat();
				double e = (double)pos.getY() + (double)random.nextFloat();
				double f = (double)pos.getZ() + (double)random.nextFloat();
				double g = ((double)random.nextFloat() - 0.5) * 0.5;
				double h = ((double)random.nextFloat() - 0.5) * 0.5;
				double k = ((double)random.nextFloat() - 0.5) * 0.5;
				int l = random.nextInt(2) * 2 - 1;
				if (random.nextBoolean()) {
					f = (double)pos.getZ() + 0.5 + 0.25 * (double)l;
					k = (double)(random.nextFloat() * 2.0F * (float)l);
				} else {
					d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
					g = (double)(random.nextFloat() * 2.0F * (float)l);
				}

				world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, k);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canBucketPlace(BlockState state, Fluid fluid) {
		return false;
	}
}
