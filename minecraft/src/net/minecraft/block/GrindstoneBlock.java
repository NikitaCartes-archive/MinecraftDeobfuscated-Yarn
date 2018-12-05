package net.minecraft.block;

import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.StateFactory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class GrindstoneBlock extends WallMountedBlock {
	public static final VoxelShape field_16379 = Block.createCubeShape(2.0, 0.0, 6.0, 4.0, 7.0, 10.0);
	public static final VoxelShape field_16392 = Block.createCubeShape(12.0, 0.0, 6.0, 14.0, 7.0, 10.0);
	public static final VoxelShape field_16366 = Block.createCubeShape(2.0, 7.0, 5.0, 4.0, 13.0, 11.0);
	public static final VoxelShape field_16339 = Block.createCubeShape(12.0, 7.0, 5.0, 14.0, 13.0, 11.0);
	public static final VoxelShape field_16348 = VoxelShapes.method_1084(field_16379, field_16366);
	public static final VoxelShape field_16365 = VoxelShapes.method_1084(field_16392, field_16339);
	public static final VoxelShape field_16385 = VoxelShapes.method_1084(field_16348, field_16365);
	public static final VoxelShape field_16380 = VoxelShapes.method_1084(field_16385, Block.createCubeShape(4.0, 4.0, 2.0, 12.0, 16.0, 14.0));
	public static final VoxelShape field_16373 = Block.createCubeShape(6.0, 0.0, 2.0, 10.0, 7.0, 4.0);
	public static final VoxelShape field_16346 = Block.createCubeShape(6.0, 0.0, 12.0, 10.0, 7.0, 14.0);
	public static final VoxelShape field_16343 = Block.createCubeShape(5.0, 7.0, 2.0, 11.0, 13.0, 4.0);
	public static final VoxelShape field_16374 = Block.createCubeShape(5.0, 7.0, 12.0, 11.0, 13.0, 14.0);
	public static final VoxelShape field_16386 = VoxelShapes.method_1084(field_16373, field_16343);
	public static final VoxelShape field_16378 = VoxelShapes.method_1084(field_16346, field_16374);
	public static final VoxelShape field_16362 = VoxelShapes.method_1084(field_16386, field_16378);
	public static final VoxelShape field_16338 = VoxelShapes.method_1084(field_16362, Block.createCubeShape(2.0, 4.0, 4.0, 14.0, 16.0, 12.0));
	public static final VoxelShape field_16352 = Block.createCubeShape(2.0, 6.0, 0.0, 4.0, 10.0, 7.0);
	public static final VoxelShape field_16377 = Block.createCubeShape(12.0, 6.0, 0.0, 14.0, 10.0, 7.0);
	public static final VoxelShape field_16393 = Block.createCubeShape(2.0, 5.0, 7.0, 4.0, 11.0, 13.0);
	public static final VoxelShape field_16371 = Block.createCubeShape(12.0, 5.0, 7.0, 14.0, 11.0, 13.0);
	public static final VoxelShape field_16340 = VoxelShapes.method_1084(field_16352, field_16393);
	public static final VoxelShape field_16354 = VoxelShapes.method_1084(field_16377, field_16371);
	public static final VoxelShape field_16369 = VoxelShapes.method_1084(field_16340, field_16354);
	public static final VoxelShape field_16399 = VoxelShapes.method_1084(field_16369, Block.createCubeShape(4.0, 2.0, 4.0, 12.0, 14.0, 16.0));
	public static final VoxelShape field_16363 = Block.createCubeShape(2.0, 6.0, 7.0, 4.0, 10.0, 16.0);
	public static final VoxelShape field_16347 = Block.createCubeShape(12.0, 6.0, 7.0, 14.0, 10.0, 16.0);
	public static final VoxelShape field_16401 = Block.createCubeShape(2.0, 5.0, 3.0, 4.0, 11.0, 9.0);
	public static final VoxelShape field_16367 = Block.createCubeShape(12.0, 5.0, 3.0, 14.0, 11.0, 9.0);
	public static final VoxelShape field_16388 = VoxelShapes.method_1084(field_16363, field_16401);
	public static final VoxelShape field_16396 = VoxelShapes.method_1084(field_16347, field_16367);
	public static final VoxelShape field_16368 = VoxelShapes.method_1084(field_16388, field_16396);
	public static final VoxelShape field_16356 = VoxelShapes.method_1084(field_16368, Block.createCubeShape(4.0, 2.0, 0.0, 12.0, 14.0, 12.0));
	public static final VoxelShape field_16342 = Block.createCubeShape(7.0, 6.0, 2.0, 16.0, 10.0, 4.0);
	public static final VoxelShape field_16358 = Block.createCubeShape(7.0, 6.0, 12.0, 16.0, 10.0, 14.0);
	public static final VoxelShape field_16390 = Block.createCubeShape(3.0, 5.0, 2.0, 9.0, 11.0, 4.0);
	public static final VoxelShape field_16382 = Block.createCubeShape(3.0, 5.0, 12.0, 9.0, 11.0, 14.0);
	public static final VoxelShape field_16359 = VoxelShapes.method_1084(field_16342, field_16390);
	public static final VoxelShape field_16351 = VoxelShapes.method_1084(field_16358, field_16382);
	public static final VoxelShape field_16344 = VoxelShapes.method_1084(field_16359, field_16351);
	public static final VoxelShape field_16376 = VoxelShapes.method_1084(field_16344, Block.createCubeShape(0.0, 2.0, 4.0, 12.0, 14.0, 12.0));
	public static final VoxelShape field_16394 = Block.createCubeShape(0.0, 6.0, 2.0, 9.0, 10.0, 4.0);
	public static final VoxelShape field_16375 = Block.createCubeShape(0.0, 6.0, 12.0, 9.0, 10.0, 14.0);
	public static final VoxelShape field_16345 = Block.createCubeShape(7.0, 5.0, 2.0, 13.0, 11.0, 4.0);
	public static final VoxelShape field_16350 = Block.createCubeShape(7.0, 5.0, 12.0, 13.0, 11.0, 14.0);
	public static final VoxelShape field_16372 = VoxelShapes.method_1084(field_16394, field_16345);
	public static final VoxelShape field_16381 = VoxelShapes.method_1084(field_16375, field_16350);
	public static final VoxelShape field_16391 = VoxelShapes.method_1084(field_16372, field_16381);
	public static final VoxelShape field_16370 = VoxelShapes.method_1084(field_16391, Block.createCubeShape(4.0, 2.0, 4.0, 16.0, 14.0, 12.0));
	public static final VoxelShape field_16341 = Block.createCubeShape(2.0, 9.0, 6.0, 4.0, 16.0, 10.0);
	public static final VoxelShape field_16355 = Block.createCubeShape(12.0, 9.0, 6.0, 14.0, 16.0, 10.0);
	public static final VoxelShape field_16384 = Block.createCubeShape(2.0, 3.0, 5.0, 4.0, 9.0, 11.0);
	public static final VoxelShape field_16400 = Block.createCubeShape(12.0, 3.0, 5.0, 14.0, 9.0, 11.0);
	public static final VoxelShape field_16364 = VoxelShapes.method_1084(field_16341, field_16384);
	public static final VoxelShape field_16349 = VoxelShapes.method_1084(field_16355, field_16400);
	public static final VoxelShape field_16397 = VoxelShapes.method_1084(field_16364, field_16349);
	public static final VoxelShape field_16361 = VoxelShapes.method_1084(field_16397, Block.createCubeShape(4.0, 0.0, 2.0, 12.0, 12.0, 14.0));
	public static final VoxelShape field_16387 = Block.createCubeShape(6.0, 9.0, 2.0, 10.0, 16.0, 4.0);
	public static final VoxelShape field_16398 = Block.createCubeShape(6.0, 9.0, 12.0, 10.0, 16.0, 14.0);
	public static final VoxelShape field_16357 = Block.createCubeShape(5.0, 3.0, 2.0, 11.0, 9.0, 4.0);
	public static final VoxelShape field_16353 = Block.createCubeShape(5.0, 3.0, 12.0, 11.0, 9.0, 14.0);
	public static final VoxelShape field_16395 = VoxelShapes.method_1084(field_16387, field_16357);
	public static final VoxelShape field_16360 = VoxelShapes.method_1084(field_16398, field_16353);
	public static final VoxelShape field_16389 = VoxelShapes.method_1084(field_16395, field_16360);
	public static final VoxelShape field_16383 = VoxelShapes.method_1084(field_16389, Block.createCubeShape(2.0, 0.0, 4.0, 14.0, 12.0, 12.0));

	protected GrindstoneBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11177, Direction.NORTH).with(field_11007, WallMountLocation.field_12471));
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	private VoxelShape method_16119(BlockState blockState) {
		Direction direction = blockState.get(field_11177);
		switch ((WallMountLocation)blockState.get(field_11007)) {
			case field_12475:
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					return field_16338;
				}

				return field_16380;
			case field_12471:
				if (direction == Direction.NORTH) {
					return field_16356;
				} else if (direction == Direction.SOUTH) {
					return field_16399;
				} else {
					if (direction == Direction.EAST) {
						return field_16370;
					}

					return field_16376;
				}
			case field_12473:
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					return field_16383;
				}

				return field_16361;
			default:
				return field_16338;
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_16119(blockState);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.method_16119(blockState);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return true;
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!world.isRemote) {
			playerEntity.openContainer(new GrindstoneBlock.class_3804(world, blockPos));
		}

		return true;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11177, rotation.method_10503(blockState.get(field_11177)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_11177)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_11007);
	}

	public static class class_3804 implements ContainerProvider {
		private final World field_16795;
		private final BlockPos field_16796;

		public class_3804(World world, BlockPos blockPos) {
			this.field_16795 = world;
			this.field_16796 = blockPos;
		}

		@Override
		public TextComponent getName() {
			return new TranslatableTextComponent(Blocks.field_16337.getTranslationKey());
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new GrindstoneContainer(playerInventory, this.field_16795, this.field_16796);
		}

		@Override
		public String getContainerId() {
			return "minecraft:grindstone";
		}
	}
}
