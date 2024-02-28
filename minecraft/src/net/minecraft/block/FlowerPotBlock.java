package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class FlowerPotBlock extends Block {
	public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Registries.BLOCK.getCodec().fieldOf("potted").forGetter(block -> block.content), createSettingsCodec())
				.apply(instance, FlowerPotBlock::new)
	);
	private static final Map<Block, Block> CONTENT_TO_POTTED = Maps.<Block, Block>newHashMap();
	public static final float field_31095 = 3.0F;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	private final Block content;

	@Override
	public MapCodec<FlowerPotBlock> getCodec() {
		return CODEC;
	}

	public FlowerPotBlock(Block content, AbstractBlock.Settings settings) {
		super(settings);
		this.content = content;
		CONTENT_TO_POTTED.put(content, this);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockState blockState = (stack.getItem() instanceof BlockItem blockItem
				? (Block)CONTENT_TO_POTTED.getOrDefault(blockItem.getBlock(), Blocks.AIR)
				: Blocks.AIR)
			.getDefaultState();
		if (blockState.isAir()) {
			return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else if (!this.isEmpty()) {
			return ItemActionResult.CONSUME;
		} else {
			world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			player.incrementStat(Stats.POT_FLOWER);
			stack.decrementUnlessCreative(1, player);
			return ItemActionResult.success(world.isClient);
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (this.isEmpty()) {
			return ActionResult.CONSUME;
		} else {
			ItemStack itemStack = new ItemStack(this.content);
			if (!player.giveItemStack(itemStack)) {
				player.dropItem(itemStack, false);
			}

			world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), Block.NOTIFY_ALL);
			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			return ActionResult.success(world.isClient);
		}
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return this.isEmpty() ? super.getPickStack(world, pos, state) : new ItemStack(this.content);
	}

	private boolean isEmpty() {
		return this.content == Blocks.AIR;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	public Block getContent() {
		return this.content;
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
