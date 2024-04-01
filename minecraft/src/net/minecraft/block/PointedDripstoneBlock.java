package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.enums.Thickness;
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
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class PointedDripstoneBlock extends Block implements LandingBlock, Waterloggable {
	public static final MapCodec<PointedDripstoneBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.BOOL.fieldOf("organic").forGetter(pointedDripstoneBlock -> pointedDripstoneBlock.field_50858), createSettingsCodec())
				.apply(instance, PointedDripstoneBlock::new)
	);
	public static final DirectionProperty VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;
	public static final EnumProperty<Thickness> THICKNESS = Properties.THICKNESS;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final int field_31205 = 11;
	private static final int field_31207 = 2;
	private static final float field_31208 = 0.02F;
	private static final float field_31209 = 0.12F;
	private static final int field_31210 = 11;
	private static final float WATER_DRIP_CHANCE = 0.17578125F;
	private static final float LAVA_DRIP_CHANCE = 0.05859375F;
	private static final double field_31213 = 0.6;
	private static final float field_31214 = 1.0F;
	private static final int field_31215 = 40;
	private static final int field_31200 = 6;
	private static final float field_31201 = 2.0F;
	private static final int field_31202 = 2;
	private static final float field_33566 = 5.0F;
	private static final float field_33567 = 0.011377778F;
	private static final int MAX_STALACTITE_GROWTH = 7;
	private static final int STALACTITE_FLOOR_SEARCH_RANGE = 10;
	private static final float field_31203 = 0.6875F;
	private static final VoxelShape TIP_MERGE_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	private static final VoxelShape UP_TIP_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
	private static final VoxelShape DOWN_TIP_SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
	private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	private static final VoxelShape FRUSTUM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
	private static final float field_31204 = 0.125F;
	private static final VoxelShape DRIP_COLLISION_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	private final boolean field_50858;

	public boolean method_59134(BlockState blockState) {
		return this.field_50858 ? blockState.isIn(BlockTags.POTATOSTONE_BASE) : blockState.isOf(Blocks.DRIPSTONE_BLOCK);
	}

	@Override
	public MapCodec<PointedDripstoneBlock> getCodec() {
		return CODEC;
	}

	public PointedDripstoneBlock(boolean bl, AbstractBlock.Settings settings) {
		super(settings);
		this.field_50858 = bl;
		this.setDefaultState(
			this.stateManager.getDefaultState().with(VERTICAL_DIRECTION, Direction.UP).with(THICKNESS, Thickness.TIP).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(VERTICAL_DIRECTION, THICKNESS, WATERLOGGED);
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return this.canPlaceAtWithDirection(world, pos, state.get(VERTICAL_DIRECTION));
	}

	public Block method_59133() {
		return this.field_50858 ? Blocks.TERRE_DE_POMME : Blocks.DRIPSTONE_BLOCK;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (direction != Direction.UP && direction != Direction.DOWN) {
			return state;
		} else {
			Direction direction2 = state.get(VERTICAL_DIRECTION);
			if (direction2 == Direction.DOWN && world.getBlockTickScheduler().isQueued(pos, this)) {
				return state;
			} else if (direction == direction2.getOpposite() && !this.canPlaceAt(state, world, pos)) {
				if (direction2 == Direction.DOWN) {
					world.scheduleBlockTick(pos, this, 2);
				} else {
					world.scheduleBlockTick(pos, this, 1);
				}

				return state;
			} else {
				boolean bl = state.get(THICKNESS) == Thickness.TIP_MERGE;
				Thickness thickness = this.getThickness(world, pos, direction2, bl);
				return state.with(THICKNESS, thickness);
			}
		}
	}

	@Override
	protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (!world.isClient) {
			BlockPos blockPos = hit.getBlockPos();
			if (projectile.canModifyAt(world, blockPos)
				&& projectile.canBreakBlocks(world)
				&& projectile instanceof TridentEntity
				&& projectile.getVelocity().length() > 0.6) {
				world.breakBlock(blockPos, true);
			}
		}
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (state.get(VERTICAL_DIRECTION) == Direction.UP && state.get(THICKNESS) == Thickness.TIP) {
			entity.handleFallDamage(fallDistance + 2.0F, 2.0F, world.getDamageSources().stalagmite());
		} else {
			super.onLandedUpon(world, state, pos, entity, fallDistance);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (this.canDrip(state)) {
			float f = random.nextFloat();
			if (!(f > 0.12F)) {
				this.getFluid(world, pos, state)
					.filter(fluid -> f < 0.02F || isFluidLiquid(fluid.fluid))
					.ifPresent(fluid -> createParticle(world, pos, state, fluid.fluid));
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (this.isPointingUp(state) && !this.canPlaceAt(state, world, pos)) {
			world.breakBlock(pos, true);
		} else {
			this.spawnFallingBlock(state, world, pos);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.dripTick(state, world, pos, random.nextFloat());
		float f = 0.011377778F;
		if (this.field_50858) {
			f = 0.2F;
		}

		if (random.nextFloat() < f) {
			if (this.field_50858) {
				this.method_59129(world, pos);
				this.method_59132(world, pos);
			} else if (this.isHeldByPointedDripstone(state, world, pos)) {
				this.tryGrow(state, world, pos, random);
			}
		}
	}

	@VisibleForTesting
	public void dripTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, float f) {
		if (!(f > 0.17578125F) || !(f > 0.05859375F)) {
			if (this.isHeldByPointedDripstone(blockState, serverWorld, blockPos)) {
				Optional<PointedDripstoneBlock.DrippingFluid> optional = this.getFluid(serverWorld, blockPos, blockState);
				if (!optional.isEmpty()) {
					Fluid fluid = ((PointedDripstoneBlock.DrippingFluid)optional.get()).fluid;
					float g;
					if (fluid == Fluids.WATER) {
						g = 0.17578125F;
					} else {
						if (fluid != Fluids.LAVA) {
							return;
						}

						g = 0.05859375F;
					}

					if (!(f >= g)) {
						BlockPos blockPos2 = this.getTipPos(blockState, serverWorld, blockPos, 11, false);
						if (blockPos2 != null) {
							if (((PointedDripstoneBlock.DrippingFluid)optional.get()).sourceState.isOf(Blocks.MUD) && fluid == Fluids.WATER) {
								BlockState blockState2 = Blocks.CLAY.getDefaultState();
								serverWorld.setBlockState(((PointedDripstoneBlock.DrippingFluid)optional.get()).pos, blockState2);
								Block.pushEntitiesUpBeforeBlockChange(
									((PointedDripstoneBlock.DrippingFluid)optional.get()).sourceState, blockState2, serverWorld, ((PointedDripstoneBlock.DrippingFluid)optional.get()).pos
								);
								serverWorld.emitGameEvent(GameEvent.BLOCK_CHANGE, ((PointedDripstoneBlock.DrippingFluid)optional.get()).pos, GameEvent.Emitter.of(blockState2));
								serverWorld.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS, blockPos2, 0);
							} else {
								BlockPos blockPos3 = getCauldronPos(serverWorld, blockPos2, fluid);
								if (blockPos3 != null) {
									serverWorld.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS, blockPos2, 0);
									int i = blockPos2.getY() - blockPos3.getY();
									int j = 50 + i;
									BlockState blockState3 = serverWorld.getBlockState(blockPos3);
									serverWorld.scheduleBlockTick(blockPos3, blockState3.getBlock(), j);
								}
							}
						}
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		WorldAccess worldAccess = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		Direction direction = ctx.getVerticalPlayerLookDirection().getOpposite();
		Direction direction2 = this.getDirectionToPlaceAt(worldAccess, blockPos, direction);
		if (direction2 == null) {
			return null;
		} else {
			boolean bl = !ctx.shouldCancelInteraction();
			Thickness thickness = this.getThickness(worldAccess, blockPos, direction2, bl);
			return thickness == null
				? null
				: this.getDefaultState()
					.with(VERTICAL_DIRECTION, direction2)
					.with(THICKNESS, thickness)
					.with(WATERLOGGED, Boolean.valueOf(worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER));
		}
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
	protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Override
	protected float getMaxHorizontalModelOffset() {
		return 0.125F;
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
		if (!fallingBlockEntity.isSilent()) {
			world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_LANDS, pos, 0);
		}
	}

	@Override
	public DamageSource getDamageSource(Entity attacker) {
		return attacker.getDamageSources().fallingStalactite(attacker);
	}

	private void spawnFallingBlock(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		BlockState blockState2 = blockState;

		while (this.isPointingDown(blockState2)) {
			FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(serverWorld, mutable, blockState2);
			if (this.isTip(blockState2, true)) {
				int i = Math.max(1 + blockPos.getY() - mutable.getY(), 6);
				float f = 1.0F * (float)i;
				fallingBlockEntity.setHurtEntities(f, 40);
				break;
			}

			mutable.move(Direction.DOWN);
			blockState2 = serverWorld.getBlockState(mutable);
		}
	}

	@VisibleForTesting
	public void tryGrow(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		BlockState blockState2 = serverWorld.getBlockState(blockPos.up(1));
		BlockState blockState3 = serverWorld.getBlockState(blockPos.up(2));
		if (this.canGrow(blockState2, blockState3)) {
			BlockPos blockPos2 = this.getTipPos(blockState, serverWorld, blockPos, 7, false);
			if (blockPos2 != null) {
				BlockState blockState4 = serverWorld.getBlockState(blockPos2);
				if (this.canDrip(blockState4) && this.canGrow(blockState4, serverWorld, blockPos2)) {
					if (random.nextBoolean()) {
						this.tryGrow(serverWorld, blockPos2, Direction.DOWN);
					} else {
						this.tryGrowStalagmite(serverWorld, blockPos2);
					}
				}
			}
		}
	}

	public void method_59129(ServerWorld serverWorld, BlockPos blockPos) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int i = 0; i < 3; i++) {
			BlockState blockState = serverWorld.getBlockState(mutable);
			if (this.isTip(blockState, Direction.UP) && !serverWorld.getBlockState(mutable.add(0, -24, 0)).isOf(this)) {
				this.tryGrow(serverWorld, mutable, Direction.UP);
				return;
			}

			mutable.move(Direction.UP);
		}
	}

	public void method_59132(ServerWorld serverWorld, BlockPos blockPos) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int i = 0; i < 3; i++) {
			BlockState blockState = serverWorld.getBlockState(mutable);
			if (this.isTip(blockState, Direction.DOWN) && !serverWorld.getBlockState(mutable.add(0, 24, 0)).isOf(this)) {
				this.tryGrow(serverWorld, mutable, Direction.DOWN);
				return;
			}

			mutable.move(Direction.DOWN);
		}
	}

	private void tryGrowStalagmite(ServerWorld serverWorld, BlockPos blockPos) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int i = 0; i < 10; i++) {
			mutable.move(Direction.DOWN);
			BlockState blockState = serverWorld.getBlockState(mutable);
			if (!blockState.getFluidState().isEmpty()) {
				return;
			}

			if (this.isTip(blockState, Direction.UP) && this.canGrow(blockState, serverWorld, mutable)) {
				this.tryGrow(serverWorld, mutable, Direction.UP);
				return;
			}

			if (this.canPlaceAtWithDirection(serverWorld, mutable, Direction.UP) && !serverWorld.isWater(mutable.down())) {
				this.tryGrow(serverWorld, mutable.down(), Direction.UP);
				return;
			}

			if (!canDripThrough(serverWorld, mutable, blockState)) {
				return;
			}
		}
	}

	private void tryGrow(ServerWorld serverWorld, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = serverWorld.getBlockState(blockPos2);
		if (this.isTip(blockState, direction.getOpposite())) {
			this.growMerged(blockState, serverWorld, blockPos2);
		} else if (blockState.isAir() || blockState.isOf(Blocks.WATER)) {
			this.place(serverWorld, blockPos2, direction, Thickness.TIP);
		}
	}

	private void place(WorldAccess worldAccess, BlockPos blockPos, Direction direction, Thickness thickness) {
		BlockState blockState = this.getDefaultState()
			.with(VERTICAL_DIRECTION, direction)
			.with(THICKNESS, thickness)
			.with(WATERLOGGED, Boolean.valueOf(worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER));
		worldAccess.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
	}

	private void growMerged(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		BlockPos blockPos3;
		BlockPos blockPos2;
		if (blockState.get(VERTICAL_DIRECTION) == Direction.UP) {
			blockPos2 = blockPos;
			blockPos3 = blockPos.up();
		} else {
			blockPos3 = blockPos;
			blockPos2 = blockPos.down();
		}

		this.place(worldAccess, blockPos3, Direction.DOWN, Thickness.TIP_MERGE);
		this.place(worldAccess, blockPos2, Direction.UP, Thickness.TIP_MERGE);
	}

	public static void createParticle(World world, BlockPos blockPos, BlockState blockState) {
		if (blockState.getBlock() instanceof PointedDripstoneBlock pointedDripstoneBlock) {
			pointedDripstoneBlock.getFluid(world, blockPos, blockState).ifPresent(fluid -> createParticle(world, blockPos, blockState, fluid.fluid));
		}
	}

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
	private BlockPos getTipPos(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, int i, boolean bl) {
		if (this.isTip(blockState, bl)) {
			return blockPos;
		} else {
			Direction direction = blockState.get(VERTICAL_DIRECTION);
			BiPredicate<BlockPos, BlockState> biPredicate = (blockPosx, blockStatex) -> blockStatex.isOf(this) && blockStatex.get(VERTICAL_DIRECTION) == direction;
			return (BlockPos)searchInDirection(worldAccess, blockPos, direction.getDirection(), biPredicate, blockStatex -> this.isTip(blockStatex, bl), i).orElse(null);
		}
	}

	@Nullable
	private Direction getDirectionToPlaceAt(WorldView worldView, BlockPos blockPos, Direction direction) {
		Direction direction2;
		if (this.canPlaceAtWithDirection(worldView, blockPos, direction)) {
			direction2 = direction;
		} else {
			if (!this.canPlaceAtWithDirection(worldView, blockPos, direction.getOpposite())) {
				return null;
			}

			direction2 = direction.getOpposite();
		}

		return direction2;
	}

	private Thickness getThickness(WorldView worldView, BlockPos blockPos, Direction direction, boolean bl) {
		Direction direction2 = direction.getOpposite();
		BlockState blockState = worldView.getBlockState(blockPos.offset(direction));
		if (this.isPointedDripstoneFacingDirection(blockState, direction2)) {
			return !bl && blockState.get(THICKNESS) != Thickness.TIP_MERGE ? Thickness.TIP : Thickness.TIP_MERGE;
		} else if (!this.isPointedDripstoneFacingDirection(blockState, direction)) {
			return Thickness.TIP;
		} else {
			Thickness thickness = blockState.get(THICKNESS);
			if (thickness != Thickness.TIP && thickness != Thickness.TIP_MERGE) {
				BlockState blockState2 = worldView.getBlockState(blockPos.offset(direction2));
				return !this.isPointedDripstoneFacingDirection(blockState2, direction) ? Thickness.BASE : Thickness.MIDDLE;
			} else {
				return Thickness.FRUSTUM;
			}
		}
	}

	public boolean canDrip(BlockState blockState) {
		return this.isPointingDown(blockState) && blockState.get(THICKNESS) == Thickness.TIP && !(Boolean)blockState.get(WATERLOGGED);
	}

	private boolean canGrow(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos) {
		Direction direction = blockState.get(VERTICAL_DIRECTION);
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = serverWorld.getBlockState(blockPos2);
		if (!blockState2.getFluidState().isEmpty()) {
			return false;
		} else {
			return blockState2.isAir() ? true : this.isTip(blockState2, direction.getOpposite());
		}
	}

	private Optional<BlockPos> getSupportingPos(World world, BlockPos blockPos, BlockState blockState, int i) {
		Direction direction = blockState.get(VERTICAL_DIRECTION);
		BiPredicate<BlockPos, BlockState> biPredicate = (blockPosx, blockStatex) -> blockStatex.isOf(this) && blockStatex.get(VERTICAL_DIRECTION) == direction;
		return searchInDirection(world, blockPos, direction.getOpposite().getDirection(), biPredicate, blockStatex -> !blockStatex.isOf(this), i);
	}

	private boolean canPlaceAtWithDirection(WorldView worldView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		BlockState blockState = worldView.getBlockState(blockPos2);
		return (this.field_50858 ? this.method_59134(blockState) : blockState.isSideSolidFullSquare(worldView, blockPos2, direction))
			|| this.isPointedDripstoneFacingDirection(blockState, direction);
	}

	private boolean isTip(BlockState blockState, boolean bl) {
		if (!blockState.isOf(this)) {
			return false;
		} else {
			Thickness thickness = blockState.get(THICKNESS);
			return thickness == Thickness.TIP || bl && thickness == Thickness.TIP_MERGE;
		}
	}

	private boolean isTip(BlockState blockState, Direction direction) {
		return this.isTip(blockState, false) && blockState.get(VERTICAL_DIRECTION) == direction;
	}

	private boolean isPointingDown(BlockState blockState) {
		return this.isPointedDripstoneFacingDirection(blockState, Direction.DOWN);
	}

	private boolean isPointingUp(BlockState blockState) {
		return this.isPointedDripstoneFacingDirection(blockState, Direction.UP);
	}

	private boolean isHeldByPointedDripstone(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		return this.isPointingDown(blockState) && !worldView.getBlockState(blockPos.up()).isOf(this);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	private boolean isPointedDripstoneFacingDirection(BlockState blockState, Direction direction) {
		return blockState.isOf(this) && blockState.get(VERTICAL_DIRECTION) == direction;
	}

	@Nullable
	private static BlockPos getCauldronPos(World world, BlockPos pos, Fluid fluid) {
		Predicate<BlockState> predicate = state -> state.getBlock() instanceof AbstractCauldronBlock
				&& ((AbstractCauldronBlock)state.getBlock()).canBeFilledByDripstone(fluid);
		BiPredicate<BlockPos, BlockState> biPredicate = (posx, state) -> canDripThrough(world, posx, state);
		return (BlockPos)searchInDirection(world, pos, Direction.DOWN.getDirection(), biPredicate, predicate, 11).orElse(null);
	}

	@Nullable
	public static BlockPos getDripPos(Block block, World world, BlockPos blockPos) {
		if (block instanceof PointedDripstoneBlock pointedDripstoneBlock) {
			BiPredicate biPredicate = (posx, state) -> canDripThrough(world, posx, state);
			return (BlockPos)searchInDirection(world, blockPos, Direction.UP.getDirection(), biPredicate, pointedDripstoneBlock::canDrip, 11).orElse(null);
		} else {
			return null;
		}
	}

	public Fluid getDripFluid(ServerWorld serverWorld, BlockPos blockPos) {
		return (Fluid)this.getFluid(serverWorld, blockPos, serverWorld.getBlockState(blockPos))
			.map(fluid -> fluid.fluid)
			.filter(PointedDripstoneBlock::isFluidLiquid)
			.orElse(Fluids.EMPTY);
	}

	private Optional<PointedDripstoneBlock.DrippingFluid> getFluid(World world, BlockPos blockPos, BlockState blockState) {
		return !this.isPointingDown(blockState) ? Optional.empty() : this.getSupportingPos(world, blockPos, blockState, 11).map(posx -> {
			BlockPos blockPosx = posx.up();
			BlockState blockStatex = world.getBlockState(blockPosx);
			Fluid fluid;
			if (blockStatex.isOf(Blocks.MUD) && !world.getDimension().ultrawarm()) {
				fluid = Fluids.WATER;
			} else {
				fluid = world.getFluidState(blockPosx).getFluid();
			}

			return new PointedDripstoneBlock.DrippingFluid(blockPosx, fluid, blockStatex);
		});
	}

	/**
	 * {@return whether the provided {@code fluid} is liquid, namely lava or water}
	 */
	private static boolean isFluidLiquid(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.WATER;
	}

	private boolean canGrow(BlockState blockState, BlockState blockState2) {
		return blockState.isOf(Blocks.DRIPSTONE_BLOCK) && blockState2.isOf(Blocks.WATER) && blockState2.getFluidState().isStill();
	}

	private static Fluid getDripFluid(World world, Fluid fluid) {
		if (fluid.matchesType(Fluids.EMPTY)) {
			return world.getDimension().ultrawarm() ? Fluids.LAVA : Fluids.WATER;
		} else {
			return fluid;
		}
	}

	private static Optional<BlockPos> searchInDirection(
		WorldAccess world,
		BlockPos pos,
		Direction.AxisDirection direction,
		BiPredicate<BlockPos, BlockState> continuePredicate,
		Predicate<BlockState> stopPredicate,
		int range
	) {
		Direction direction2 = Direction.get(direction, Direction.Axis.Y);
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 1; i < range; i++) {
			mutable.move(direction2);
			BlockState blockState = world.getBlockState(mutable);
			if (stopPredicate.test(blockState)) {
				return Optional.of(mutable.toImmutable());
			}

			if (world.isOutOfHeightLimit(mutable.getY()) || !continuePredicate.test(mutable, blockState)) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

	/**
	 * {@return whether it can drip through the block {@code block} at {@code pos}}
	 * 
	 * @apiNote This is used for checking which block can obstruct the stalagmites
	 * growing or the cauldrons filling with liquids.
	 */
	private static boolean canDripThrough(BlockView world, BlockPos pos, BlockState state) {
		if (state.isAir()) {
			return true;
		} else if (state.isOpaqueFullCube(world, pos)) {
			return false;
		} else if (!state.getFluidState().isEmpty()) {
			return false;
		} else {
			VoxelShape voxelShape = state.getCollisionShape(world, pos);
			return !VoxelShapes.matchesAnywhere(DRIP_COLLISION_SHAPE, voxelShape, BooleanBiFunction.AND);
		}
	}

	static record DrippingFluid(BlockPos pos, Fluid fluid, BlockState sourceState) {
	}
}
