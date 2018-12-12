package net.minecraft.block;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BeaconBlock extends BlockWithEntity {
	public BeaconBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BeaconBlockEntity();
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BeaconBlockEntity) {
				playerEntity.openInventory((BeaconBlockEntity)blockEntity);
				playerEntity.increaseStat(Stats.field_15416);
			}

			return true;
		}
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BeaconBlockEntity) {
				((BeaconBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public static void method_9463(World world, BlockPos blockPos) {
		NetworkUtils.downloadExecutor.submit((Runnable)(() -> {
			for (int i = blockPos.getY() - 1; i >= 0; i--) {
				BlockPos blockPos2 = new BlockPos(blockPos.getX(), i, blockPos.getZ());
				if (!world.isSkyVisible(blockPos2)) {
					break;
				}

				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getBlock() == Blocks.field_10327) {
					world.getServer().execute(() -> {
						BlockEntity blockEntity = world.getBlockEntity(blockPos2);
						if (blockEntity instanceof BeaconBlockEntity) {
							((BeaconBlockEntity)blockEntity).method_10941();
							world.addBlockAction(blockPos2, Blocks.field_10327, 1, 0);
						}
					});
				}
			}
		}));
	}
}
