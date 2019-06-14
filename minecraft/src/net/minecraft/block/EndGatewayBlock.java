package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
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
	public BlockEntity method_10123(BlockView blockView) {
		return new EndGatewayBlockEntity();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			int i = ((EndGatewayBlockEntity)blockEntity).getDrawnSidesCount();

			for (int j = 0; j < i; j++) {
				double d = (double)((float)blockPos.getX() + random.nextFloat());
				double e = (double)((float)blockPos.getY() + random.nextFloat());
				double f = (double)((float)blockPos.getZ() + random.nextFloat());
				double g = ((double)random.nextFloat() - 0.5) * 0.5;
				double h = ((double)random.nextFloat() - 0.5) * 0.5;
				double k = ((double)random.nextFloat() - 0.5) * 0.5;
				int l = random.nextInt(2) * 2 - 1;
				if (random.nextBoolean()) {
					f = (double)blockPos.getZ() + 0.5 + 0.25 * (double)l;
					k = (double)(random.nextFloat() * 2.0F * (float)l);
				} else {
					d = (double)blockPos.getX() + 0.5 + 0.25 * (double)l;
					g = (double)(random.nextFloat() * 2.0F * (float)l);
				}

				world.addParticle(ParticleTypes.field_11214, d, e, f, g, h, k);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}
}
