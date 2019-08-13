package net.minecraft.block;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
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

	public BedBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
		this.setDefaultState(this.stateFactory.getDefaultState().with(PART, BedPart.field_12557).with(OCCUPIED, Boolean.valueOf(false)));
	}

	@Override
	public MaterialColor getMapColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(PART) == BedPart.field_12557 ? this.color.getMaterialColor() : MaterialColor.WEB;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Direction getDirection(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		return blockState.getBlock() instanceof BedBlock ? blockState.get(FACING) : null;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			if (blockState.get(PART) != BedPart.field_12560) {
				blockPos = blockPos.offset(blockState.get(FACING));
				blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() != this) {
					return true;
				}
			}

			if (!world.dimension.canPlayersSleep() || world.getBiome(blockPos) == Biomes.field_9461) {
				world.clearBlockState(blockPos, false);
				BlockPos blockPos2 = blockPos.offset(((Direction)blockState.get(FACING)).getOpposite());
				if (world.getBlockState(blockPos2).getBlock() == this) {
					world.clearBlockState(blockPos2, false);
				}

				world.createExplosion(
					null,
					DamageSource.netherBed(),
					(double)blockPos.getX() + 0.5,
					(double)blockPos.getY() + 0.5,
					(double)blockPos.getZ() + 0.5,
					5.0F,
					true,
					Explosion.DestructionType.field_18687
				);
				return true;
			} else if ((Boolean)blockState.get(OCCUPIED)) {
				playerEntity.addChatMessage(new TranslatableText("block.minecraft.bed.occupied"), true);
				return true;
			} else {
				playerEntity.trySleep(blockPos).ifLeft(sleepFailureReason -> {
					if (sleepFailureReason != null) {
						playerEntity.addChatMessage(sleepFailureReason.toText(), true);
					}
				});
				return true;
			}
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		super.onLandedUpon(world, blockPos, entity, f * 0.5F);
	}

	@Override
	public void onEntityLand(BlockView blockView, Entity entity) {
		if (entity.isSneaking()) {
			super.onEntityLand(blockView, entity);
		} else {
			Vec3d vec3d = entity.getVelocity();
			if (vec3d.y < 0.0) {
				double d = entity instanceof LivingEntity ? 1.0 : 0.8;
				entity.setVelocity(vec3d.x, -vec3d.y * 0.66F * d, vec3d.z);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == getDirectionTowardsOtherPart(blockState.get(PART), blockState.get(FACING))) {
			return blockState2.getBlock() == this && blockState2.get(PART) != blockState.get(PART)
				? blockState.with(OCCUPIED, blockState2.get(OCCUPIED))
				: Blocks.field_10124.getDefaultState();
		} else {
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	private static Direction getDirectionTowardsOtherPart(BedPart bedPart, Direction direction) {
		return bedPart == BedPart.field_12557 ? direction : direction.getOpposite();
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.field_10124.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BedPart bedPart = blockState.get(PART);
		BlockPos blockPos2 = blockPos.offset(getDirectionTowardsOtherPart(bedPart, blockState.get(FACING)));
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(PART) != bedPart) {
			world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 35);
			world.playLevelEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			if (!world.isClient && !playerEntity.isCreative()) {
				ItemStack itemStack = playerEntity.getMainHandStack();
				dropStacks(blockState, world, blockPos, null, playerEntity, itemStack);
				dropStacks(blockState2, world, blockPos2, null, playerEntity, itemStack);
			}

			playerEntity.incrementStat(Stats.field_15427.getOrCreateStat(this));
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getPlayerFacing();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		return itemPlacementContext.getWorld().getBlockState(blockPos2).canReplace(itemPlacementContext) ? this.getDefaultState().with(FACING, direction) : null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		Direction direction = blockState.get(FACING);
		Direction direction2 = blockState.get(PART) == BedPart.field_12560 ? direction : direction.getOpposite();
		switch (direction2) {
			case field_11043:
				return NORTH_SHAPE;
			case field_11035:
				return SOUTH_SHAPE;
			case field_11039:
				return WEST_SHAPE;
			default:
				return EAST_SHAPE;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return true;
	}

	public static Optional<Vec3d> findWakeUpPosition(EntityType<?> entityType, ViewableWorld viewableWorld, BlockPos blockPos, int i) {
		Direction direction = viewableWorld.getBlockState(blockPos).get(FACING);
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();

		for (int m = 0; m <= 1; m++) {
			int n = j - direction.getOffsetX() * m - 1;
			int o = l - direction.getOffsetZ() * m - 1;
			int p = n + 2;
			int q = o + 2;

			for (int r = n; r <= p; r++) {
				for (int s = o; s <= q; s++) {
					BlockPos blockPos2 = new BlockPos(r, k, s);
					Optional<Vec3d> optional = canWakeUpAt(entityType, viewableWorld, blockPos2);
					if (optional.isPresent()) {
						if (i <= 0) {
							return optional;
						}

						i--;
					}
				}
			}
		}

		return Optional.empty();
	}

	protected static Optional<Vec3d> canWakeUpAt(EntityType<?> entityType, ViewableWorld viewableWorld, BlockPos blockPos) {
		VoxelShape voxelShape = viewableWorld.getBlockState(blockPos).getCollisionShape(viewableWorld, blockPos);
		if (voxelShape.getMaximum(Direction.Axis.field_11052) > 0.4375) {
			return Optional.empty();
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

			while (
				mutable.getY() >= 0 && blockPos.getY() - mutable.getY() <= 2 && viewableWorld.getBlockState(mutable).getCollisionShape(viewableWorld, mutable).isEmpty()
			) {
				mutable.setOffset(Direction.field_11033);
			}

			VoxelShape voxelShape2 = viewableWorld.getBlockState(mutable).getCollisionShape(viewableWorld, mutable);
			if (voxelShape2.isEmpty()) {
				return Optional.empty();
			} else {
				double d = (double)mutable.getY() + voxelShape2.getMaximum(Direction.Axis.field_11052) + 2.0E-7;
				if ((double)blockPos.getY() - d > 2.0) {
					return Optional.empty();
				} else {
					float f = entityType.getWidth() / 2.0F;
					Vec3d vec3d = new Vec3d((double)mutable.getX() + 0.5, d, (double)mutable.getZ() + 0.5);
					return viewableWorld.doesNotCollide(
							new Box(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)entityType.getHeight(), vec3d.z + (double)f)
						)
						? Optional.of(vec3d)
						: Optional.empty();
				}
			}
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART, OCCUPIED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BedBlockEntity(this.color);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
		if (!world.isClient) {
			BlockPos blockPos2 = blockPos.offset(blockState.get(FACING));
			world.setBlockState(blockPos2, blockState.with(PART, BedPart.field_12560), 3);
			world.updateNeighbors(blockPos, Blocks.field_10124);
			blockState.updateNeighborStates(world, blockPos, 3);
		}
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.offset(blockState.get(FACING), blockState.get(PART) == BedPart.field_12560 ? 0 : 1);
		return MathHelper.hashCode(blockPos2.getX(), blockPos.getY(), blockPos2.getZ());
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
