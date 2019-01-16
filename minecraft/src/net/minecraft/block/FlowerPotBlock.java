package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock extends Block {
	private static final Map<Block, Block> field_11103 = Maps.<Block, Block>newHashMap();
	protected static final VoxelShape field_11102 = Block.createCubeShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	private final Block field_11101;

	public FlowerPotBlock(Block block, Block.Settings settings) {
		super(settings);
		this.field_11101 = block;
		field_11103.put(block, this);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11102;
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		Block block = item instanceof BlockItem ? (Block)field_11103.getOrDefault(((BlockItem)item).getBlock(), Blocks.field_10124) : Blocks.field_10124;
		boolean bl = block == Blocks.field_10124;
		boolean bl2 = this.field_11101 == Blocks.field_10124;
		if (bl != bl2) {
			if (bl2) {
				world.setBlockState(blockPos, block.getDefaultState(), 3);
				playerEntity.increaseStat(Stats.field_15412);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.subtractAmount(1);
				}
			} else {
				ItemStack itemStack2 = new ItemStack(this.field_11101);
				if (itemStack.isEmpty()) {
					playerEntity.setStackInHand(hand, itemStack2);
				} else if (!playerEntity.method_7270(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}

				world.setBlockState(blockPos, Blocks.field_10495.getDefaultState(), 3);
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return this.field_11101 == Blocks.field_10124 ? super.getPickStack(blockView, blockPos, blockState) : new ItemStack(this.field_11101);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public Block method_16231() {
		return this.field_11101;
	}
}
