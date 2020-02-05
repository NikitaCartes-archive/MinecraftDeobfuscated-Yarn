package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.explosion.Explosion;

public class BedBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<BedPart> PART = Properties.BED_PART;
	public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;
	protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
	protected static final VoxelShape LEG_1_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
	protected static final VoxelShape LEG_2_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
	protected static final VoxelShape LEG_3_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
	protected static final VoxelShape LEG_4_SHAPE = Block.createCuboidShape(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_3_SHAPE);
	protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_2_SHAPE, LEG_4_SHAPE);
	protected static final VoxelShape WEST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_2_SHAPE);
	protected static final VoxelShape EAST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_3_SHAPE, LEG_4_SHAPE);
	private final DyeColor color;

	public BedBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
		this.setDefaultState(this.stateManager.getDefaultState().with(PART, BedPart.FOOT).with(OCCUPIED, Boolean.valueOf(false)));
	}

	@Override
	public MaterialColor getMapColor(BlockState state, BlockView view, BlockPos pos) {
		return state.get(PART) == BedPart.FOOT ? this.color.getMaterialColor() : MaterialColor.WEB;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Direction getDirection(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() instanceof BedBlock ? blockState.get(FACING) : null;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.CONSUME;
		} else {
			if (state.get(PART) != BedPart.HEAD) {
				pos = pos.offset(state.get(FACING));
				state = world.getBlockState(pos);
				if (state.getBlock() != this) {
					return ActionResult.CONSUME;
				}
			}

			if (!world.dimension.canPlayersSleep() || world.getBiome(pos) == Biomes.NETHER_WASTES) {
				world.removeBlock(pos, false);
				BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
				if (world.getBlockState(blockPos).getBlock() == this) {
					world.removeBlock(blockPos, false);
				}

				world.createExplosion(
					null,
					DamageSource.netherBed(),
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					5.0F,
					true,
					Explosion.DestructionType.DESTROY
				);
				return ActionResult.SUCCESS;
			} else if ((Boolean)state.get(OCCUPIED)) {
				if (!this.method_22357(world, pos)) {
					player.addChatMessage(new TranslatableText("block.minecraft.bed.occupied"), true);
				}

				return ActionResult.SUCCESS;
			} else {
				player.trySleep(pos).ifLeft(sleepFailureReason -> {
					if (sleepFailureReason != null) {
						player.addChatMessage(sleepFailureReason.toText(), true);
					}
				});
				return ActionResult.SUCCESS;
			}
		}
	}

	private boolean method_22357(World world, BlockPos blockPos) {
		List<VillagerEntity> list = world.getEntities(VillagerEntity.class, new Box(blockPos), LivingEntity::isSleeping);
		if (list.isEmpty()) {
			return false;
		} else {
			((VillagerEntity)list.get(0)).wakeUp();
			return true;
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		super.onLandedUpon(world, pos, entity, distance * 0.5F);
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.bypassesLandingEffects()) {
			super.onEntityLand(world, entity);
		} else {
			this.method_21838(entity);
		}
	}

	private void method_21838(Entity entity) {
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.y < 0.0) {
			double d = entity instanceof LivingEntity ? 1.0 : 0.8;
			entity.setVelocity(vec3d.x, -vec3d.y * 0.66F * d, vec3d.z);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
			return neighborState.getBlock() == this && neighborState.get(PART) != state.get(PART)
				? state.with(OCCUPIED, neighborState.get(OCCUPIED))
				: Blocks.AIR.getDefaultState();
		} else {
			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		}
	}

	private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
		return part == BedPart.FOOT ? direction : direction.getOpposite();
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BedPart bedPart = state.get(PART);
		BlockPos blockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.get(FACING)));
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && blockState.get(PART) != bedPart) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
			world.playLevelEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			if (!world.isClient && !player.isCreative()) {
				ItemStack itemStack = player.getMainHandStack();
				dropStacks(state, world, pos, null, player, itemStack);
				dropStacks(blockState, world, blockPos, null, player, itemStack);
			}

			player.incrementStat(Stats.MINED.getOrCreateStat(this));
		}

		super.onBreak(world, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getPlayerFacing();
		BlockPos blockPos = ctx.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		return ctx.getWorld().getBlockState(blockPos2).canReplace(ctx) ? this.getDefaultState().with(FACING, direction) : null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		Direction direction = method_24163(state).getOpposite();
		switch (direction) {
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case WEST:
				return WEST_SHAPE;
			default:
				return EAST_SHAPE;
		}
	}

	public static Direction method_24163(BlockState blockState) {
		Direction direction = blockState.get(FACING);
		return blockState.get(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
	}

	@Environment(EnvType.CLIENT)
	public static DoubleBlockProperties.Type method_24164(BlockState blockState) {
		BedPart bedPart = blockState.get(PART);
		return bedPart == BedPart.HEAD ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
	}

	public static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, WorldView worldView, BlockPos pos, int index) {
		Direction direction = worldView.getBlockState(pos).get(FACING);
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for (int l = 0; l <= 1; l++) {
			int m = i - direction.getOffsetX() * l - 1;
			int n = k - direction.getOffsetZ() * l - 1;
			int o = m + 2;
			int p = n + 2;

			for (int q = m; q <= o; q++) {
				for (int r = n; r <= p; r++) {
					BlockPos blockPos = new BlockPos(q, j, r);
					Optional<Vec3d> optional = canWakeUpAt(type, worldView, blockPos);
					if (optional.isPresent()) {
						if (index <= 0) {
							return optional;
						}

						index--;
					}
				}
			}
		}

		return Optional.empty();
	}

	protected static Optional<Vec3d> canWakeUpAt(EntityType<?> type, WorldView worldView, BlockPos pos) {
		VoxelShape voxelShape = worldView.getBlockState(pos).getCollisionShape(worldView, pos);
		if (voxelShape.getMaximum(Direction.Axis.Y) > 0.4375) {
			return Optional.empty();
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable(pos);

			while (mutable.getY() >= 0 && pos.getY() - mutable.getY() <= 2 && worldView.getBlockState(mutable).getCollisionShape(worldView, mutable).isEmpty()) {
				mutable.setOffset(Direction.DOWN);
			}

			VoxelShape voxelShape2 = worldView.getBlockState(mutable).getCollisionShape(worldView, mutable);
			if (voxelShape2.isEmpty()) {
				return Optional.empty();
			} else {
				double d = (double)mutable.getY() + voxelShape2.getMaximum(Direction.Axis.Y) + 2.0E-7;
				if ((double)pos.getY() - d > 2.0) {
					return Optional.empty();
				} else {
					float f = type.getWidth() / 2.0F;
					Vec3d vec3d = new Vec3d((double)mutable.getX() + 0.5, d, (double)mutable.getZ() + 0.5);
					return worldView.doesNotCollide(
							new Box(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)type.getHeight(), vec3d.z + (double)f)
						)
						? Optional.of(vec3d)
						: Optional.empty();
				}
			}
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART, OCCUPIED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new BedBlockEntity(this.color);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient) {
			BlockPos blockPos = pos.offset(state.get(FACING));
			world.setBlockState(blockPos, state.with(PART, BedPart.HEAD), 3);
			world.updateNeighbors(pos, Blocks.AIR);
			state.updateNeighborStates(world, pos, 3);
		}
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		BlockPos blockPos = pos.offset(state.get(FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
		return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}
}
