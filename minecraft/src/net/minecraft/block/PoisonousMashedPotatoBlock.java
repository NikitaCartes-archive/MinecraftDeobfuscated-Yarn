package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class PoisonousMashedPotatoBlock extends Block {
	public static final MapCodec<PoisonousMashedPotatoBlock> CODEC = createCodec(PoisonousMashedPotatoBlock::new);
	private static final float HORIZONTAL_MOVEMENT_SLOWNESS = 0.9F;
	private static final float VERTICAL_MOVEMENT_SLOWNESS = 1.5F;
	public static final int MAX_LAYERS = 8;
	public static final IntProperty LAYERS = Properties.LAYERS;
	protected static final VoxelShape[] LAYERS_TO_SHAPE = SnowBlock.LAYERS_TO_SHAPE;

	@Override
	protected MapCodec<PoisonousMashedPotatoBlock> getCodec() {
		return CODEC;
	}

	protected PoisonousMashedPotatoBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LAYERS, Integer.valueOf(1)));
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return true;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return LAYERS_TO_SHAPE[state.get(LAYERS)];
	}

	@Override
	protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return LAYERS_TO_SHAPE[state.get(LAYERS)];
	}

	@Override
	protected boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return state.get(LAYERS) == 8 ? 0.2F : 1.0F;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		if (blockState.isIn(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
			return false;
		} else {
			return blockState.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)
				? true
				: Block.isFaceFullSquare(blockState.getCollisionShape(world, pos.down()), Direction.UP) || blockState.isOf(this) && (Integer)blockState.get(LAYERS) == 8;
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context) {
		int i = (Integer)state.get(LAYERS);
		if (!context.getStack().isOf(this.asItem()) || i >= 8) {
			return i == 1;
		} else {
			return context.canReplaceExisting() ? context.getSide() == Direction.UP : true;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (blockState.isOf(this)) {
			int i = (Integer)blockState.get(LAYERS);
			return blockState.with(LAYERS, Integer.valueOf(Math.min(8, i + 1)));
		} else {
			return super.getPlacementState(ctx);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LAYERS);
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!(entity instanceof LivingEntity) || entity.getBlockStateAtPos().isOf(this)) {
			entity.slowMovement(state, new Vec3d(0.9F, 1.5, 0.9F));
		}

		int i = (Integer)state.get(LAYERS);
		if (entity instanceof LivingEntity livingEntity) {
			livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STICKY, 100 + i * 20));
		}
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return stack.isOf(this.asItem()) ? ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!player.canConsume(false)) {
			return ActionResult.PASS;
		} else {
			player.getHungerManager().add(2, 0.1F);
			int i = (Integer)state.get(LAYERS);
			world.emitGameEvent(player, GameEvent.EAT, pos);
			if (i > 1) {
				world.setBlockState(pos, state.with(LAYERS, Integer.valueOf(i - 1)), Block.NOTIFY_ALL);
			} else {
				world.removeBlock(pos, false);
				world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
			}

			return ActionResult.success(world.isClient());
		}
	}
}
