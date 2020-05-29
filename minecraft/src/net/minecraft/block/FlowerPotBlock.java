package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FlowerPotBlock extends Block {
	private static final Map<Block, Block> CONTENT_TO_POTTED = Maps.<Block, Block>newHashMap();
	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	private final Block content;

	public FlowerPotBlock(Block content, AbstractBlock.Settings settings) {
		super(settings);
		this.content = content;
		CONTENT_TO_POTTED.put(content, this);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		Block block = item instanceof BlockItem ? (Block)CONTENT_TO_POTTED.getOrDefault(((BlockItem)item).getBlock(), Blocks.AIR) : Blocks.AIR;
		boolean bl = block == Blocks.AIR;
		boolean bl2 = this.content == Blocks.AIR;
		if (bl != bl2) {
			if (bl2) {
				world.setBlockState(pos, block.getDefaultState(), 3);
				player.incrementStat(Stats.POT_FLOWER);
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			} else {
				ItemStack itemStack2 = new ItemStack(this.content);
				if (itemStack.isEmpty()) {
					player.setStackInHand(hand, itemStack2);
				} else if (!player.giveItemStack(itemStack2)) {
					player.dropItem(itemStack2, false);
				}

				world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
			}

			return ActionResult.method_29236(world.isClient);
		} else {
			return ActionResult.CONSUME;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return this.content == Blocks.AIR ? super.getPickStack(world, pos, state) : new ItemStack(this.content);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	public Block getContent() {
		return this.content;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
