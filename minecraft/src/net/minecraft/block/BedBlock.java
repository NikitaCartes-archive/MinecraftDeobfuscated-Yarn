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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class BedBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<BedPart> field_9967 = Properties.BED_PART;
	public static final BooleanProperty field_9968 = Properties.OCCUPIED;
	protected static final VoxelShape field_16788 = Block.createCubeShape(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
	protected static final VoxelShape field_16782 = Block.createCubeShape(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
	protected static final VoxelShape field_16784 = Block.createCubeShape(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
	protected static final VoxelShape field_16786 = Block.createCubeShape(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
	protected static final VoxelShape field_16789 = Block.createCubeShape(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape field_16787 = VoxelShapes.union(field_16788, VoxelShapes.union(field_16782, field_16786));
	protected static final VoxelShape field_16785 = VoxelShapes.union(field_16788, VoxelShapes.union(field_16784, field_16789));
	protected static final VoxelShape field_16783 = VoxelShapes.union(field_16788, VoxelShapes.union(field_16782, field_16784));
	protected static final VoxelShape field_16790 = VoxelShapes.union(field_16788, VoxelShapes.union(field_16786, field_16789));
	private final DyeColor color;

	public BedBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_9967, BedPart.field_12557).with(field_9968, Boolean.valueOf(false)));
	}

	@Override
	public MaterialColor getMaterialColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_9967) == BedPart.field_12557 ? this.color.getMaterialColor() : MaterialColor.WEB;
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return true;
		} else {
			if (blockState.get(field_9967) != BedPart.field_12560) {
				blockPos = blockPos.offset(blockState.get(field_11177));
				blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() != this) {
					return true;
				}
			}

			if (world.dimension.method_12448() && world.getBiome(blockPos) != Biomes.field_9461) {
				if ((Boolean)blockState.get(field_9968)) {
					PlayerEntity playerEntity2 = this.method_9485(world, blockPos);
					if (playerEntity2 != null) {
						playerEntity.addChatMessage(new TranslatableTextComponent("block.minecraft.bed.occupied"), true);
						return true;
					}

					blockState = blockState.with(field_9968, Boolean.valueOf(false));
					world.setBlockState(blockPos, blockState, 4);
				}

				PlayerEntity.SleepResult sleepResult = playerEntity.trySleep(blockPos);
				if (sleepResult == PlayerEntity.SleepResult.SUCCESS) {
					blockState = blockState.with(field_9968, Boolean.valueOf(true));
					world.setBlockState(blockPos, blockState, 4);
					return true;
				} else {
					if (sleepResult == PlayerEntity.SleepResult.WRONG_TIME) {
						playerEntity.addChatMessage(new TranslatableTextComponent("block.minecraft.bed.no_sleep"), true);
					} else if (sleepResult == PlayerEntity.SleepResult.NOT_SAFE) {
						playerEntity.addChatMessage(new TranslatableTextComponent("block.minecraft.bed.not_safe"), true);
					} else if (sleepResult == PlayerEntity.SleepResult.TOO_FAR_AWAY) {
						playerEntity.addChatMessage(new TranslatableTextComponent("block.minecraft.bed.too_far_away"), true);
					}

					return true;
				}
			} else {
				world.clearBlockState(blockPos);
				BlockPos blockPos2 = blockPos.offset(((Direction)blockState.get(field_11177)).getOpposite());
				if (world.getBlockState(blockPos2).getBlock() == this) {
					world.clearBlockState(blockPos2);
				}

				world.createExplosion(
					null, DamageSource.netherBed(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 5.0F, true, true
				);
				return true;
			}
		}
	}

	@Nullable
	private PlayerEntity method_9485(World world, BlockPos blockPos) {
		for (PlayerEntity playerEntity : world.players) {
			if (playerEntity.isSleeping() && playerEntity.sleepingPos.equals(blockPos)) {
				return playerEntity;
			}
		}

		return null;
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		super.onLandedUpon(world, blockPos, entity, f * 0.5F);
	}

	@Override
	public void onEntityLand(BlockView blockView, Entity entity) {
		if (entity.isSneaking()) {
			super.onEntityLand(blockView, entity);
		} else if (entity.velocityY < 0.0) {
			entity.velocityY = -entity.velocityY * 0.66F;
			if (!(entity instanceof LivingEntity)) {
				entity.velocityY *= 0.8;
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == method_9488(blockState.get(field_9967), blockState.get(field_11177))) {
			return blockState2.getBlock() == this && blockState2.get(field_9967) != blockState.get(field_9967)
				? blockState.with(field_9968, blockState2.get(field_9968))
				: Blocks.field_10124.getDefaultState();
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
		super.afterBreak(world, playerEntity, blockPos, Blocks.field_10124.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			world.removeBlockEntity(blockPos);
		}
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BedPart bedPart = blockState.get(field_9967);
		BlockPos blockPos2 = blockPos.offset(method_9488(bedPart, blockState.get(field_11177)));
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(field_9967) != bedPart) {
			world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 35);
			world.fireWorldEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			if (!world.isClient && !playerEntity.isCreative()) {
				ItemStack itemStack = playerEntity.getMainHandStack();
				dropStacks(blockState, world, blockPos, null, playerEntity, itemStack);
				dropStacks(blockState2, world, blockPos2, null, playerEntity, itemStack);
			}

			playerEntity.incrementStat(Stats.field_15427.method_14956(this));
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getPlayerHorizontalFacing();
		BlockPos blockPos = itemPlacementContext.getPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		return itemPlacementContext.getWorld().getBlockState(blockPos2).method_11587(itemPlacementContext)
			? this.getDefaultState().with(field_11177, direction)
			: null;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.get(field_11177);
		Direction direction2 = blockState.get(field_9967) == BedPart.field_12560 ? direction : direction.getOpposite();
		switch (direction2) {
			case NORTH:
				return field_16787;
			case SOUTH:
				return field_16785;
			case WEST:
				return field_16783;
			default:
				return field_16790;
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
		return blockView.getBlockState(blockPos2).hasSolidTopSurface(blockView, blockPos2)
			&& !blockView.getBlockState(blockPos).getMaterial().method_15799()
			&& !blockView.getBlockState(blockPos.up()).getMaterial().method_15799();
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
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_9967, field_9968);
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
			world.setBlockState(blockPos2, blockState.with(field_9967, BedPart.field_12560), 3);
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
		BlockPos blockPos2 = blockPos.offset(blockState.get(field_11177), blockState.get(field_9967) == BedPart.field_12560 ? 0 : 1);
		return MathHelper.hashCode(blockPos2.getX(), blockPos.getY(), blockPos2.getZ());
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
