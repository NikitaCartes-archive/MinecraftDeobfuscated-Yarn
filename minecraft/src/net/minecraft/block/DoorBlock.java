package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class DoorBlock extends Block {
	public static final DirectionProperty field_10938 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_10945 = Properties.field_12537;
	public static final EnumProperty<DoorHinge> field_10941 = Properties.field_12520;
	public static final BooleanProperty field_10940 = Properties.field_12484;
	public static final EnumProperty<DoubleBlockHalf> field_10946 = Properties.field_12533;
	protected static final VoxelShape field_10942 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_10939 = Block.method_9541(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_10944 = Block.method_9541(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_10943 = Block.method_9541(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

	protected DoorBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10938, Direction.NORTH)
				.method_11657(field_10945, Boolean.valueOf(false))
				.method_11657(field_10941, DoorHinge.field_12588)
				.method_11657(field_10940, Boolean.valueOf(false))
				.method_11657(field_10946, DoubleBlockHalf.field_12607)
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.method_11654(field_10938);
		boolean bl = !(Boolean)blockState.method_11654(field_10945);
		boolean bl2 = blockState.method_11654(field_10941) == DoorHinge.field_12586;
		switch (direction) {
			case EAST:
			default:
				return bl ? field_10943 : (bl2 ? field_10939 : field_10942);
			case SOUTH:
				return bl ? field_10942 : (bl2 ? field_10943 : field_10944);
			case WEST:
				return bl ? field_10944 : (bl2 ? field_10942 : field_10939);
			case NORTH:
				return bl ? field_10939 : (bl2 ? field_10944 : field_10943);
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		DoubleBlockHalf doubleBlockHalf = blockState.method_11654(field_10946);
		if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.field_12607 != (direction == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.field_12607 && direction == Direction.DOWN && !blockState.method_11591(iWorld, blockPos)
				? Blocks.field_10124.method_9564()
				: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return blockState2.getBlock() == this && blockState2.method_11654(field_10946) != doubleBlockHalf
				? blockState.method_11657(field_10938, blockState2.method_11654(field_10938))
					.method_11657(field_10945, blockState2.method_11654(field_10945))
					.method_11657(field_10941, blockState2.method_11654(field_10941))
					.method_11657(field_10940, blockState2.method_11654(field_10940))
				: Blocks.field_10124.method_9564();
		}
	}

	@Override
	public void method_9556(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.method_9556(world, playerEntity, blockPos, Blocks.field_10124.method_9564(), blockEntity, itemStack);
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.method_11654(field_10946);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.field_12607 ? blockPos.up() : blockPos.down();
		BlockState blockState2 = world.method_8320(blockPos2);
		if (blockState2.getBlock() == this && blockState2.method_11654(field_10946) != doubleBlockHalf) {
			world.method_8652(blockPos2, Blocks.field_10124.method_9564(), 35);
			world.method_8444(playerEntity, 2001, blockPos2, Block.method_9507(blockState2));
			ItemStack itemStack = playerEntity.method_6047();
			if (!world.isClient && !playerEntity.isCreative()) {
				Block.method_9511(blockState, world, blockPos, null, playerEntity, itemStack);
				Block.method_9511(blockState2, world, blockPos2, null, playerEntity, itemStack);
			}
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Boolean)blockState.method_11654(field_10945);
			case field_48:
				return false;
			case field_51:
				return (Boolean)blockState.method_11654(field_10945);
			default:
				return false;
		}
	}

	private int getOpenSoundEventId() {
		return this.field_10635 == Material.METAL ? 1011 : 1012;
	}

	private int getCloseSoundEventId() {
		return this.field_10635 == Material.METAL ? 1005 : 1006;
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.method_8037();
		if (blockPos.getY() < 255 && itemPlacementContext.method_8045().method_8320(blockPos.up()).method_11587(itemPlacementContext)) {
			World world = itemPlacementContext.method_8045();
			boolean bl = world.method_8479(blockPos) || world.method_8479(blockPos.up());
			return this.method_9564()
				.method_11657(field_10938, itemPlacementContext.method_8042())
				.method_11657(field_10941, this.method_10035(itemPlacementContext))
				.method_11657(field_10940, Boolean.valueOf(bl))
				.method_11657(field_10945, Boolean.valueOf(bl))
				.method_11657(field_10946, DoubleBlockHalf.field_12607);
		} else {
			return null;
		}
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.method_8652(blockPos.up(), blockState.method_11657(field_10946, DoubleBlockHalf.field_12609), 3);
	}

	private DoorHinge method_10035(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		Direction direction = itemPlacementContext.method_8042();
		BlockPos blockPos2 = blockPos.up();
		Direction direction2 = direction.rotateYCounterclockwise();
		BlockPos blockPos3 = blockPos.method_10093(direction2);
		BlockState blockState = blockView.method_8320(blockPos3);
		BlockPos blockPos4 = blockPos2.method_10093(direction2);
		BlockState blockState2 = blockView.method_8320(blockPos4);
		Direction direction3 = direction.rotateYClockwise();
		BlockPos blockPos5 = blockPos.method_10093(direction3);
		BlockState blockState3 = blockView.method_8320(blockPos5);
		BlockPos blockPos6 = blockPos2.method_10093(direction3);
		BlockState blockState4 = blockView.method_8320(blockPos6);
		int i = (blockState.method_11603(blockView, blockPos3) ? -1 : 0)
			+ (blockState2.method_11603(blockView, blockPos4) ? -1 : 0)
			+ (blockState3.method_11603(blockView, blockPos5) ? 1 : 0)
			+ (blockState4.method_11603(blockView, blockPos6) ? 1 : 0);
		boolean bl = blockState.getBlock() == this && blockState.method_11654(field_10946) == DoubleBlockHalf.field_12607;
		boolean bl2 = blockState3.getBlock() == this && blockState3.method_11654(field_10946) == DoubleBlockHalf.field_12607;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = itemPlacementContext.method_17698();
				double d = vec3d.x - (double)blockPos.getX();
				double e = vec3d.z - (double)blockPos.getZ();
				return (j >= 0 || !(e < 0.5)) && (j <= 0 || !(e > 0.5)) && (k >= 0 || !(d > 0.5)) && (k <= 0 || !(d < 0.5)) ? DoorHinge.field_12588 : DoorHinge.field_12586;
			} else {
				return DoorHinge.field_12588;
			}
		} else {
			return DoorHinge.field_12586;
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (this.field_10635 == Material.METAL) {
			return false;
		} else {
			blockState = blockState.method_11572(field_10945);
			world.method_8652(blockPos, blockState, 10);
			world.method_8444(playerEntity, blockState.method_11654(field_10945) ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
			return true;
		}
	}

	public void method_10033(World world, BlockPos blockPos, boolean bl) {
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() == this && (Boolean)blockState.method_11654(field_10945) != bl) {
			world.method_8652(blockPos, blockState.method_11657(field_10945, Boolean.valueOf(bl)), 10);
			this.method_10036(world, blockPos, bl);
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		boolean bl = world.method_8479(blockPos)
			|| world.method_8479(blockPos.method_10093(blockState.method_11654(field_10946) == DoubleBlockHalf.field_12607 ? Direction.UP : Direction.DOWN));
		if (block != this && bl != (Boolean)blockState.method_11654(field_10940)) {
			if (bl != (Boolean)blockState.method_11654(field_10945)) {
				this.method_10036(world, blockPos, bl);
			}

			world.method_8652(blockPos, blockState.method_11657(field_10940, Boolean.valueOf(bl)).method_11657(field_10945, Boolean.valueOf(bl)), 2);
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		return blockState.method_11654(field_10946) == DoubleBlockHalf.field_12607
			? blockState2.method_11631(viewableWorld, blockPos2)
			: blockState2.getBlock() == this;
	}

	private void method_10036(World world, BlockPos blockPos, boolean bl) {
		world.method_8444(null, bl ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_10938, rotation.method_10503(blockState.method_11654(field_10938)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return mirror == Mirror.NONE ? blockState : blockState.rotate(mirror.method_10345(blockState.method_11654(field_10938))).method_11572(field_10941);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long method_9535(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(
			blockPos.getX(), blockPos.down(blockState.method_11654(field_10946) == DoubleBlockHalf.field_12607 ? 0 : 1).getY(), blockPos.getZ()
		);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10946, field_10938, field_10945, field_10941, field_10940);
	}
}
