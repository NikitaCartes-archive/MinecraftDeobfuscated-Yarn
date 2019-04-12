package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
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
	public static Direction method_18476(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		return blockState.getBlock() instanceof BedBlock ? blockState.get(field_11177) : null;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			if (blockState.get(PART) != BedPart.field_12560) {
				blockPos = blockPos.offset(blockState.get(field_11177));
				blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() != this) {
					return true;
				}
			}

			if (!world.dimension.canPlayersSleep() || world.getBiome(blockPos) == Biomes.field_9461) {
				world.clearBlockState(blockPos, false);
				BlockPos blockPos2 = blockPos.offset(((Direction)blockState.get(field_11177)).getOpposite());
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
				playerEntity.addChatMessage(new TranslatableTextComponent("block.minecraft.bed.occupied"), true);
				return true;
			} else {
				playerEntity.trySleep(blockPos).ifLeft(sleepFailureReason -> {
					if (sleepFailureReason != null) {
						playerEntity.addChatMessage(sleepFailureReason.getText(), true);
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
		if (direction == method_9488(blockState.get(PART), blockState.get(field_11177))) {
			return blockState2.getBlock() == this && blockState2.get(PART) != blockState.get(PART)
				? blockState.with(OCCUPIED, blockState2.get(OCCUPIED))
				: Blocks.AIR.getDefaultState();
		} else {
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	private static Direction method_9488(BedPart bedPart, Direction direction) {
		return bedPart == BedPart.field_12557 ? direction : direction.getOpposite();
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.AIR.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BedPart bedPart = blockState.get(PART);
		BlockPos blockPos2 = blockPos.offset(method_9488(bedPart, blockState.get(field_11177)));
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(PART) != bedPart) {
			world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 35);
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
		Direction direction = itemPlacementContext.getPlayerHorizontalFacing();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		return itemPlacementContext.getWorld().getBlockState(blockPos2).canReplace(itemPlacementContext) ? this.getDefaultState().with(field_11177, direction) : null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.get(field_11177);
		Direction direction2 = blockState.get(PART) == BedPart.field_12560 ? direction : direction.getOpposite();
		switch (direction2) {
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

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return true;
	}

	@Nullable
	public static BlockPos method_9484(BlockView blockView, BlockPos blockPos, int i) {
		Direction direction = blockView.getBlockState(blockPos).get(field_11177);
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
					if (method_9486(blockView, blockPos2)) {
						if (i <= 0) {
							return blockPos2;
						}

						i--;
					}
				}
			}
		}

		return null;
	}

	protected static boolean method_9486(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockPos blockPos3 = blockPos.up();
		return Block.isSolidFullSquare(blockView.getBlockState(blockPos2), blockView, blockPos2, Direction.UP)
			&& blockView.getBlockState(blockPos).getCollisionShape(blockView, blockPos).isEmpty()
			&& blockView.getBlockState(blockPos3).getCollisionShape(blockView, blockPos3).isEmpty();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, PART, OCCUPIED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BedBlockEntity(this.color);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
		if (!world.isClient) {
			BlockPos blockPos2 = blockPos.offset(blockState.get(field_11177));
			world.setBlockState(blockPos2, blockState.with(PART, BedPart.field_12560), 3);
			world.updateNeighbors(blockPos, Blocks.AIR);
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
		BlockPos blockPos2 = blockPos.offset(blockState.get(field_11177), blockState.get(PART) == BedPart.field_12560 ? 0 : 1);
		return MathHelper.hashCode(blockPos2.getX(), blockPos.getY(), blockPos2.getZ());
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
