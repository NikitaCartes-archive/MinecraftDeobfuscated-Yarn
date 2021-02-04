package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
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

public class PointedDripstoneBlock extends Block implements LandingBlock, Waterloggable {
	public static final DirectionProperty VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;
	public static final EnumProperty<Thickness> THICKNESS = Properties.THICKNESS;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final VoxelShape TIP_MERGE_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	private static final VoxelShape UP_TIP_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
	private static final VoxelShape DOWN_TIP_SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
	private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	private static final VoxelShape FRUSTUM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	public PointedDripstoneBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(VERTICAL_DIRECTION, Direction.UP).with(THICKNESS, Thickness.TIP).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(VERTICAL_DIRECTION, THICKNESS, WATERLOGGED);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canPlaceAtWithDirection(world, pos, state.get(VERTICAL_DIRECTION));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (direction != Direction.UP && direction != Direction.DOWN) {
			return state;
		} else if (world.getBlockTickScheduler().isScheduled(pos, this)) {
			return state;
		} else {
			Direction direction2 = state.get(VERTICAL_DIRECTION);
			if (direction != direction2.getOpposite() || canPlaceAtWithDirection(world, pos, direction2)) {
				boolean bl = state.get(THICKNESS) == Thickness.TIP_MERGE;
				Thickness thickness = getThickness(world, pos, direction2, bl);
				return thickness == null ? getFluidBlockState(state) : state.with(THICKNESS, thickness);
			} else if (direction2 == Direction.DOWN) {
				this.scheduleFall(state, world, pos);
				return state;
			} else {
				return getFluidBlockState(state);
			}
		}
	}

	private static BlockState getFluidBlockState(BlockState state) {
		return state.get(WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (projectile instanceof TridentEntity && projectile.getVelocity().length() > 0.6) {
			world.breakBlock(hit.getBlockPos(), true);
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.get(VERTICAL_DIRECTION) == Direction.UP && blockState.get(THICKNESS) == Thickness.TIP) {
			entity.handleFallDamage(distance + 2.0F, 2.0F, DamageSource.STALAGMITE);
		} else {
			super.onLandedUpon(world, pos, entity, distance);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (canDrip(state)) {
			float f = random.nextFloat();
			if (!(f > 0.12F)) {
				method_33276(world, pos, state).filter(fluid -> f < 0.02F || isFluidLiquid(fluid)).ifPresent(fluid -> createParticle(world, pos, state, fluid));
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		spawnFallingBlock(state, world, pos);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		dripTick(state, world, pos, random.nextFloat());
	}

	@VisibleForTesting
	public static void dripTick(BlockState state, ServerWorld world, BlockPos pos, float dripChance) {
		if (!(dripChance > 0.17578125F) || !(dripChance > 0.05859375F)) {
			if (isHeldByPointedDripstone(state, world, pos)) {
				Fluid fluid = getDripFluid(world, pos);
				float f;
				if (fluid == Fluids.WATER) {
					f = 0.17578125F;
				} else {
					if (fluid != Fluids.LAVA) {
						return;
					}

					f = 0.05859375F;
				}

				if (!(dripChance >= f)) {
					BlockPos blockPos = getTipPos(state, world, pos, 10);
					if (blockPos != null) {
						BlockPos blockPos2 = getCauldronPos(world, blockPos, fluid);
						if (blockPos2 != null) {
							world.syncWorldEvent(1504, blockPos, 0);
							int i = blockPos.getY() - blockPos2.getY();
							int j = 50 + i;
							BlockState blockState = world.getBlockState(blockPos2);
							world.getBlockTickScheduler().schedule(blockPos2, blockState.getBlock(), j);
						}
					}
				}
			}
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		WorldAccess worldAccess = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		Direction direction = ctx.getVerticalPlayerLookDirection().getOpposite();
		Direction direction2 = getDirectionToPlaceAt(worldAccess, blockPos, direction);
		if (direction2 == null) {
			return null;
		} else {
			boolean bl = !ctx.shouldCancelInteraction();
			Thickness thickness = getThickness(worldAccess, blockPos, direction2, bl);
			return thickness == null
				? null
				: this.getDefaultState()
					.with(VERTICAL_DIRECTION, direction2)
					.with(THICKNESS, thickness)
					.with(WATERLOGGED, Boolean.valueOf(worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER));
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Thickness thickness = state.get(THICKNESS);
		VoxelShape voxelShape;
		if (thickness == Thickness.TIP_MERGE) {
			voxelShape = TIP_MERGE_SHAPE;
		} else if (thickness == Thickness.TIP) {
			if (state.get(VERTICAL_DIRECTION) == Direction.DOWN) {
				voxelShape = DOWN_TIP_SHAPE;
			} else {
				voxelShape = UP_TIP_SHAPE;
			}
		} else if (thickness == Thickness.FRUSTUM) {
			voxelShape = BASE_SHAPE;
		} else if (thickness == Thickness.MIDDLE) {
			voxelShape = FRUSTUM_SHAPE;
		} else {
			voxelShape = MIDDLE_SHAPE;
		}

		Vec3d vec3d = state.getModelOffset(world, pos);
		return voxelShape.offset(vec3d.x, 0.0, vec3d.z);
	}

	@Override
	public AbstractBlock.OffsetType getOffsetType() {
		return AbstractBlock.OffsetType.XZ;
	}

	@Override
	public float getMaxModelOffset() {
		return 0.125F;
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
		if (!fallingBlockEntity.isSilent()) {
			world.syncWorldEvent(1045, pos, 0);
		}
	}

	@Override
	public DamageSource getDamageSource() {
		return DamageSource.FALLING_STALACTITE;
	}

	@Override
	public Predicate<Entity> getEntityPredicate() {
		return EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY);
	}

	private void scheduleFall(BlockState state, WorldAccess world, BlockPos pos) {
		BlockPos blockPos = getTipPos(state, world, pos, Integer.MAX_VALUE);
		if (blockPos != null) {
			BlockPos.Mutable mutable = blockPos.mutableCopy();

			while (isPointingDown(world.getBlockState(mutable))) {
				world.getBlockTickScheduler().schedule(mutable, this, 2);
				mutable.move(Direction.UP);
			}
		}
	}

	private static int method_32900(ServerWorld serverWorld, BlockPos blockPos, int i) {
		int j = 1;
		BlockPos.Mutable mutable = blockPos.mutableCopy().move(Direction.UP);

		while (j < i && isPointingDown(serverWorld.getBlockState(mutable))) {
			j++;
			mutable.move(Direction.UP);
		}

		return j;
	}

	private static void spawnFallingBlock(BlockState state, ServerWorld world, BlockPos pos) {
		Vec3d vec3d = Vec3d.ofBottomCenter(pos);
		FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, vec3d.x, vec3d.y, vec3d.z, state);
		if (isTip(state)) {
			int i = method_32900(world, pos, 6);
			float f = 1.0F * (float)i;
			fallingBlockEntity.setHurtEntities(f, 40);
		}

		world.spawnEntity(fallingBlockEntity);
	}

	@Environment(EnvType.CLIENT)
	public static void createParticle(World world, BlockPos pos, BlockState state) {
		method_33276(world, pos, state).ifPresent(fluid -> createParticle(world, pos, state, fluid));
	}

	@Environment(EnvType.CLIENT)
	private static void createParticle(World world, BlockPos pos, BlockState state, Fluid fluid) {
		Vec3d vec3d = state.getModelOffset(world, pos);
		double d = 0.0625;
		double e = (double)pos.getX() + 0.5 + vec3d.x;
		double f = (double)((float)(pos.getY() + 1) - 0.6875F) - 0.0625;
		double g = (double)pos.getZ() + 0.5 + vec3d.z;
		Fluid fluid2 = getDripFluid(world, fluid);
		ParticleEffect particleEffect = fluid2.isIn(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
		world.addParticle(particleEffect, e, f, g, 0.0, 0.0, 0.0);
	}

	@Nullable
	private static BlockPos getTipPos(BlockState state, WorldAccess world, BlockPos pos, int i) {
		if (isTip(state)) {
			return pos;
		} else {
			Direction direction = state.get(VERTICAL_DIRECTION);
			Predicate<BlockState> predicate = blockState -> blockState.isOf(Blocks.POINTED_DRIPSTONE) && blockState.get(VERTICAL_DIRECTION) == direction;
			return (BlockPos)method_33272(world, pos, direction.getDirection(), predicate, PointedDripstoneBlock::isTip, i).orElse(null);
		}
	}

	@Nullable
	private static Direction getDirectionToPlaceAt(WorldView world, BlockPos pos, Direction direction) {
		Direction direction2;
		if (canPlaceAtWithDirection(world, pos, direction)) {
			direction2 = direction;
		} else {
			if (!canPlaceAtWithDirection(world, pos, direction.getOpposite())) {
				return null;
			}

			direction2 = direction.getOpposite();
		}

		return direction2;
	}

	@Nullable
	private static Thickness getThickness(WorldView world, BlockPos pos, Direction direction, boolean tryMerge) {
		Direction direction2 = direction.getOpposite();
		BlockState blockState = world.getBlockState(pos.offset(direction));
		if (isPointedDripstoneFacingDirection(blockState, direction2)) {
			return !tryMerge && blockState.get(THICKNESS) != Thickness.TIP_MERGE ? Thickness.TIP : Thickness.TIP_MERGE;
		} else if (!isPointedDripstoneFacingDirection(blockState, direction)) {
			return Thickness.TIP;
		} else {
			Thickness thickness = blockState.get(THICKNESS);
			if (thickness != Thickness.TIP && thickness != Thickness.TIP_MERGE) {
				BlockState blockState2 = world.getBlockState(pos.offset(direction2));
				return !isPointedDripstoneFacingDirection(blockState2, direction) ? Thickness.BASE : Thickness.MIDDLE;
			} else {
				return Thickness.FRUSTUM;
			}
		}
	}

	public static boolean canDrip(BlockState state) {
		return isPointingDown(state) && state.get(THICKNESS) == Thickness.TIP && !(Boolean)state.get(WATERLOGGED);
	}

	private static Optional<BlockPos> getSupportingPos(World world, BlockPos pos, BlockState state, int i) {
		Direction direction = state.get(VERTICAL_DIRECTION);
		Predicate<BlockState> predicate = blockState -> blockState.isOf(Blocks.POINTED_DRIPSTONE) && blockState.get(VERTICAL_DIRECTION) == direction;
		return method_33272(world, pos, direction.getOpposite().getDirection(), predicate, blockState -> !blockState.isOf(Blocks.POINTED_DRIPSTONE), i);
	}

	private static boolean canPlaceAtWithDirection(WorldView world, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, direction) || isPointedDripstoneFacingDirection(blockState, direction);
	}

	private static boolean isTip(BlockState state) {
		if (!state.isOf(Blocks.POINTED_DRIPSTONE)) {
			return false;
		} else {
			Thickness thickness = state.get(THICKNESS);
			return thickness == Thickness.TIP || thickness == Thickness.TIP_MERGE;
		}
	}

	private static boolean isPointingDown(BlockState state) {
		return isPointedDripstoneFacingDirection(state, Direction.DOWN);
	}

	private static boolean isHeldByPointedDripstone(BlockState state, WorldView world, BlockPos pos) {
		return isPointingDown(state) && !world.getBlockState(pos.up()).isOf(Blocks.POINTED_DRIPSTONE);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	private static boolean isPointedDripstoneFacingDirection(BlockState state, Direction direction) {
		return state.isOf(Blocks.POINTED_DRIPSTONE) && state.get(VERTICAL_DIRECTION) == direction;
	}

	@Nullable
	private static BlockPos getCauldronPos(World world, BlockPos pos, Fluid fluid) {
		Predicate<BlockState> predicate = state -> state.getBlock() instanceof AbstractCauldronBlock
				&& ((AbstractCauldronBlock)state.getBlock()).canBeFilledByDripstone(fluid);
		return (BlockPos)method_33272(world, pos, Direction.DOWN.getDirection(), AbstractBlock.AbstractBlockState::isAir, predicate, 10).orElse(null);
	}

	@Nullable
	public static BlockPos getDripPos(World world, BlockPos pos) {
		return (BlockPos)method_33272(world, pos, Direction.UP.getDirection(), AbstractBlock.AbstractBlockState::isAir, PointedDripstoneBlock::canDrip, 10)
			.orElse(null);
	}

	public static Fluid getDripFluid(World world, BlockPos pos) {
		return (Fluid)method_33276(world, pos, world.getBlockState(pos)).filter(PointedDripstoneBlock::isFluidLiquid).orElse(Fluids.EMPTY);
	}

	private static Optional<Fluid> method_33276(World world, BlockPos blockPos, BlockState blockState) {
		return !isPointingDown(blockState)
			? Optional.empty()
			: getSupportingPos(world, blockPos, blockState, 10).map(blockPosx -> world.getFluidState(blockPosx.up()).getFluid());
	}

	/**
	 * Returns whether the provided {@code fluid} is liquid, namely lava or water.
	 */
	private static boolean isFluidLiquid(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.WATER;
	}

	@Environment(EnvType.CLIENT)
	private static Fluid getDripFluid(World world, Fluid fluid) {
		if (fluid.matchesType(Fluids.EMPTY)) {
			return world.getDimension().isUltrawarm() ? Fluids.LAVA : Fluids.WATER;
		} else {
			return fluid;
		}
	}

	private static Optional<BlockPos> method_33272(
		WorldAccess worldAccess, BlockPos blockPos, Direction.AxisDirection axisDirection, Predicate<BlockState> predicate, Predicate<BlockState> predicate2, int i
	) {
		Direction direction = Direction.get(axisDirection, Direction.Axis.Y);
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int j = 0; j < i; j++) {
			mutable.move(direction);
			BlockState blockState = worldAccess.getBlockState(mutable);
			if (predicate2.test(blockState)) {
				return Optional.of(mutable);
			}

			if (worldAccess.isOutOfHeightLimit(mutable.getY()) || !predicate.test(blockState)) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}
}
