package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class JukeboxBlock extends BlockWithEntity {
	public static final BooleanProperty field_11180 = Properties.field_12544;

	protected JukeboxBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11180, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.method_11654(field_11180)) {
			this.removeRecord(world, blockPos);
			blockState = blockState.method_11657(field_11180, Boolean.valueOf(false));
			world.method_8652(blockPos, blockState, 2);
			return true;
		} else {
			return false;
		}
	}

	public void method_10276(IWorld iWorld, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		BlockEntity blockEntity = iWorld.method_8321(blockPos);
		if (blockEntity instanceof JukeboxBlockEntity) {
			((JukeboxBlockEntity)blockEntity).setRecord(itemStack.copy());
			iWorld.method_8652(blockPos, blockState.method_11657(field_11180, Boolean.valueOf(true)), 2);
		}
	}

	private void removeRecord(World world, BlockPos blockPos) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof JukeboxBlockEntity) {
				JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
				ItemStack itemStack = jukeboxBlockEntity.getRecord();
				if (!itemStack.isEmpty()) {
					world.playLevelEvent(1010, blockPos, 0);
					jukeboxBlockEntity.clear();
					float f = 0.7F;
					double d = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					double e = (double)(world.random.nextFloat() * 0.7F) + 0.060000002F + 0.6;
					double g = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
					ItemStack itemStack2 = itemStack.copy();
					ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack2);
					itemEntity.setToDefaultPickupDelay();
					world.spawnEntity(itemEntity);
				}
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			this.removeRecord(world, blockPos);
			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new JukeboxBlockEntity();
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof JukeboxBlockEntity) {
			Item item = ((JukeboxBlockEntity)blockEntity).getRecord().getItem();
			if (item instanceof MusicDiscItem) {
				return ((MusicDiscItem)item).getComparatorOutput();
			}
		}

		return 0;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11180);
	}
}
