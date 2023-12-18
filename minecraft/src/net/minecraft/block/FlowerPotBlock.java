package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.class_9062;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
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
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public class_9062 method_55765(
		ItemStack itemStack, BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult
	) {
		BlockState blockState2 = (itemStack.getItem() instanceof BlockItem blockItem
				? (Block)CONTENT_TO_POTTED.getOrDefault(blockItem.getBlock(), Blocks.AIR)
				: Blocks.AIR)
			.getDefaultState();
		if (blockState2.isAir()) {
			return class_9062.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else if (!this.isEmpty()) {
			return class_9062.CONSUME;
		} else {
			world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
			world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
			playerEntity.incrementStat(Stats.POT_FLOWER);
			if (!playerEntity.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			return class_9062.method_55644(world.isClient);
		}
	}

	@Override
	public ActionResult method_55766(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
		if (this.isEmpty()) {
			return ActionResult.CONSUME;
		} else {
			ItemStack itemStack = new ItemStack(this.content);
			Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
				.filter(hand -> playerEntity.getStackInHand(hand).isEmpty())
				.findFirst()
				.ifPresentOrElse(hand -> playerEntity.setStackInHand(hand, itemStack), () -> {
					if (!playerEntity.giveItemStack(itemStack)) {
						playerEntity.dropItem(itemStack, false);
					}
				});
			world.setBlockState(blockPos, Blocks.FLOWER_POT.getDefaultState(), Block.NOTIFY_ALL);
			world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
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
	public BlockState getStateForNeighborUpdate(
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
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
