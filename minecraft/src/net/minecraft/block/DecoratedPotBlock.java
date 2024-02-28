package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class DecoratedPotBlock extends BlockWithEntity implements Waterloggable {
	public static final MapCodec<DecoratedPotBlock> CODEC = createCodec(DecoratedPotBlock::new);
	public static final Identifier SHERDS_DYNAMIC_DROP_ID = new Identifier("sherds");
	private static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
	private static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty CRACKED = Properties.CRACKED;
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	@Override
	public MapCodec<DecoratedPotBlock> getCodec() {
		return CODEC;
	}

	protected DecoratedPotBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)).with(CRACKED, Boolean.valueOf(false))
		);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState()
			.with(FACING, ctx.getHorizontalPlayerFacing())
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER))
			.with(CRACKED, Boolean.valueOf(false));
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
			if (world.isClient) {
				return ItemActionResult.CONSUME;
			} else {
				ItemStack itemStack = decoratedPotBlockEntity.getStack();
				if (!stack.isEmpty() && (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount())) {
					decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.POSITIVE);
					player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
					ItemStack itemStack2 = player.isCreative() ? stack.copyWithCount(1) : stack.split(1);
					float f;
					if (decoratedPotBlockEntity.isEmpty()) {
						decoratedPotBlockEntity.setStack(itemStack2);
						f = (float)itemStack2.getCount() / (float)itemStack2.getMaxCount();
					} else {
						itemStack.increment(1);
						f = (float)itemStack.getCount() / (float)itemStack.getMaxCount();
					}

					world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 0.7F + 0.5F * f);
					if (world instanceof ServerWorld serverWorld) {
						serverWorld.spawnParticles(ParticleTypes.DUST_PLUME, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);
					}

					decoratedPotBlockEntity.markDirty();
					world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
					return ItemActionResult.SUCCESS;
				} else {
					return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				}
			}
		} else {
			return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
			world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
			decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.NEGATIVE);
			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, CRACKED);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DecoratedPotBlockEntity(pos, state);
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		ItemScatterer.onStateReplaced(state, newState, world, pos);
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
		if (blockEntity instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
			builder.addDynamicDrop(SHERDS_DYNAMIC_DROP_ID, lootConsumer -> {
				for (Item item : decoratedPotBlockEntity.getSherds().stream()) {
					lootConsumer.accept(item.getDefaultStack());
				}
			});
		}

		return super.getDroppedStacks(state, builder);
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		ItemStack itemStack = player.getMainHandStack();
		BlockState blockState = state;
		if (itemStack.isIn(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasSilkTouch(itemStack)) {
			blockState = state.with(CRACKED, Boolean.valueOf(true));
			world.setBlockState(pos, blockState, Block.NO_REDRAW);
		}

		return super.onBreak(world, pos, blockState, player);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected BlockSoundGroup getSoundGroup(BlockState state) {
		return state.get(CRACKED) ? BlockSoundGroup.DECORATED_POT_SHATTER : BlockSoundGroup.DECORATED_POT;
	}

	@Override
	public void appendTooltip(
		ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options, @Nullable DynamicRegistryManager registryManager
	) {
		super.appendTooltip(stack, world, tooltip, options, registryManager);
		Sherds sherds = stack.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
		if (!sherds.equals(Sherds.DEFAULT)) {
			tooltip.add(ScreenTexts.EMPTY);
			Stream.of(sherds.front(), sherds.left(), sherds.right(), sherds.back())
				.forEach(sherd -> tooltip.add(new ItemStack((ItemConvertible)sherd.orElse(Items.BRICK), 1).getName().copyContentOnly().formatted(Formatting.GRAY)));
		}
	}

	@Override
	protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		BlockPos blockPos = hit.getBlockPos();
		if (!world.isClient && projectile.canModifyAt(world, blockPos) && projectile.canBreakBlocks(world)) {
			world.setBlockState(blockPos, state.with(CRACKED, Boolean.valueOf(true)), Block.NO_REDRAW);
			world.breakBlock(blockPos, true, projectile);
		}
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity
			? decoratedPotBlockEntity.asStack()
			: super.getPickStack(world, pos, state);
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}
}
