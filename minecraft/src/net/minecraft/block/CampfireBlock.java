package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.class_9062;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class CampfireBlock extends BlockWithEntity implements Waterloggable {
	public static final MapCodec<CampfireBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.BOOL.fieldOf("spawn_particles").forGetter(block -> block.emitsParticles),
					Codec.intRange(0, 1000).fieldOf("fire_damage").forGetter(block -> block.fireDamage),
					createSettingsCodec()
				)
				.apply(instance, CampfireBlock::new)
	);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	/**
	 * The shape used to test whether a given block is considered 'smokey'.
	 */
	private static final VoxelShape SMOKEY_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	private static final int field_31049 = 5;
	private final boolean emitsParticles;
	private final int fireDamage;

	@Override
	public MapCodec<CampfireBlock> getCodec() {
		return CODEC;
	}

	public CampfireBlock(boolean emitsParticles, int fireDamage, AbstractBlock.Settings settings) {
		super(settings);
		this.emitsParticles = emitsParticles;
		this.fireDamage = fireDamage;
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(LIT, Boolean.valueOf(true))
				.with(SIGNAL_FIRE, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
				.with(FACING, Direction.NORTH)
		);
	}

	@Override
	public class_9062 method_55765(
		ItemStack itemStack, BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult
	) {
		if (world.getBlockEntity(blockPos) instanceof CampfireBlockEntity campfireBlockEntity) {
			ItemStack itemStack2 = playerEntity.getStackInHand(hand);
			Optional<RecipeEntry<CampfireCookingRecipe>> optional = campfireBlockEntity.getRecipeFor(itemStack2);
			if (optional.isPresent()) {
				if (!world.isClient
					&& campfireBlockEntity.addItem(
						playerEntity,
						playerEntity.getAbilities().creativeMode ? itemStack2.copy() : itemStack2,
						((CampfireCookingRecipe)((RecipeEntry)optional.get()).value()).getCookingTime()
					)) {
					playerEntity.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
					return class_9062.SUCCESS;
				}

				return class_9062.CONSUME;
			}
		}

		return class_9062.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if ((Boolean)state.get(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(world.getDamageSources().inFire(), (float)this.fireDamage);
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CampfireBlockEntity) {
				ItemScatterer.spawn(world, pos, ((CampfireBlockEntity)blockEntity).getItemsBeingCooked());
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		WorldAccess worldAccess = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		boolean bl = worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER;
		return this.getDefaultState()
			.with(WATERLOGGED, Boolean.valueOf(bl))
			.with(SIGNAL_FIRE, Boolean.valueOf(this.isSignalFireBaseBlock(worldAccess.getBlockState(blockPos.down()))))
			.with(LIT, Boolean.valueOf(!bl))
			.with(FACING, ctx.getHorizontalPlayerFacing());
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction == Direction.DOWN
			? state.with(SIGNAL_FIRE, Boolean.valueOf(this.isSignalFireBaseBlock(neighborState)))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private boolean isSignalFireBaseBlock(BlockState state) {
		return state.isOf(Blocks.HAY_BLOCK);
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
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(LIT)) {
			if (random.nextInt(10) == 0) {
				world.playSound(
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
					SoundCategory.BLOCKS,
					0.5F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.6F,
					false
				);
			}

			if (this.emitsParticles && random.nextInt(5) == 0) {
				for (int i = 0; i < random.nextInt(1) + 1; i++) {
					world.addParticle(
						ParticleTypes.LAVA,
						(double)pos.getX() + 0.5,
						(double)pos.getY() + 0.5,
						(double)pos.getZ() + 0.5,
						(double)(random.nextFloat() / 2.0F),
						5.0E-5,
						(double)(random.nextFloat() / 2.0F)
					);
				}
			}
		}
	}

	public static void extinguish(@Nullable Entity entity, WorldAccess world, BlockPos pos, BlockState state) {
		if (world.isClient()) {
			for (int i = 0; i < 20; i++) {
				spawnSmokeParticle((World)world, pos, (Boolean)state.get(SIGNAL_FIRE), true);
			}
		}

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CampfireBlockEntity) {
			((CampfireBlockEntity)blockEntity).spawnItemsBeingCooked();
		}

		world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			boolean bl = (Boolean)state.get(LIT);
			if (bl) {
				if (!world.isClient()) {
					world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				extinguish(null, world, pos, state);
			}

			world.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)).with(LIT, Boolean.valueOf(false)), Block.NOTIFY_ALL);
			world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		BlockPos blockPos = hit.getBlockPos();
		if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && !(Boolean)state.get(LIT) && !(Boolean)state.get(WATERLOGGED)) {
			world.setBlockState(blockPos, state.with(Properties.LIT, Boolean.valueOf(true)), Block.NOTIFY_ALL_AND_REDRAW);
		}
	}

	public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
		Random random = world.getRandom();
		DefaultParticleType defaultParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
		world.addImportantParticle(
			defaultParticleType,
			true,
			(double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			(double)pos.getY() + random.nextDouble() + random.nextDouble(),
			(double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			0.0,
			0.07,
			0.0
		);
		if (lotsOfSmoke) {
			world.addParticle(
				ParticleTypes.SMOKE,
				(double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1),
				(double)pos.getY() + 0.4,
				(double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1),
				0.0,
				0.005,
				0.0
			);
		}
	}

	public static boolean isLitCampfireInRange(World world, BlockPos pos) {
		for (int i = 1; i <= 5; i++) {
			BlockPos blockPos = pos.down(i);
			BlockState blockState = world.getBlockState(blockPos);
			if (isLitCampfire(blockState)) {
				return true;
			}

			boolean bl = VoxelShapes.matchesAnywhere(SMOKEY_SHAPE, blockState.getCollisionShape(world, pos, ShapeContext.absent()), BooleanBiFunction.AND);
			if (bl) {
				BlockState blockState2 = world.getBlockState(blockPos.down());
				return isLitCampfire(blockState2);
			}
		}

		return false;
	}

	public static boolean isLitCampfire(BlockState state) {
		return state.contains(LIT) && state.isIn(BlockTags.CAMPFIRES) && (Boolean)state.get(LIT);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CampfireBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (world.isClient) {
			return state.get(LIT) ? validateTicker(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::clientTick) : null;
		} else {
			return state.get(LIT)
				? validateTicker(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::litServerTick)
				: validateTicker(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::unlitServerTick);
		}
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	public static boolean canBeLit(BlockState state) {
		return state.isIn(BlockTags.CAMPFIRES, statex -> statex.contains(WATERLOGGED) && statex.contains(LIT))
			&& !(Boolean)state.get(WATERLOGGED)
			&& !(Boolean)state.get(LIT);
	}
}
