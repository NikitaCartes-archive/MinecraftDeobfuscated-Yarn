package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AnvilBlock extends FallingBlock {
	public static final DirectionProperty field_9883 = HorizontalFacingBlock.field_11177;
	private static final VoxelShape field_9882 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final VoxelShape field_9885 = Block.method_9541(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
	private static final VoxelShape field_9888 = Block.method_9541(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
	private static final VoxelShape field_9884 = Block.method_9541(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
	private static final VoxelShape field_9891 = Block.method_9541(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
	private static final VoxelShape field_9889 = Block.method_9541(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
	private static final VoxelShape field_9886 = Block.method_9541(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
	private static final VoxelShape field_9887 = VoxelShapes.method_17786(field_9882, field_9885, field_9888, field_9884);
	private static final VoxelShape field_9892 = VoxelShapes.method_17786(field_9882, field_9891, field_9889, field_9886);
	private static final TranslatableTextComponent field_17349 = new TranslatableTextComponent("container.repair");

	public AnvilBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9883, Direction.NORTH));
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_9883, itemPlacementContext.method_8042().rotateYClockwise());
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		playerEntity.openContainer(blockState.method_17526(world, blockPos));
		return true;
	}

	@Nullable
	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		return new ClientDummyContainerProvider(
			(i, playerInventory, playerEntity) -> new AnvilContainer(i, playerInventory, BlockContext.method_17392(world, blockPos)), field_17349
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.method_11654(field_9883);
		return direction.getAxis() == Direction.Axis.X ? field_9887 : field_9892;
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity fallingBlockEntity) {
		fallingBlockEntity.setHurtEntities(true);
	}

	@Override
	public void method_10127(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		world.method_8535(1031, blockPos, 0);
	}

	@Override
	public void method_10129(World world, BlockPos blockPos) {
		world.method_8535(1029, blockPos, 0);
	}

	@Nullable
	public static BlockState method_9346(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10535) {
			return Blocks.field_10105.method_9564().method_11657(field_9883, blockState.method_11654(field_9883));
		} else {
			return block == Blocks.field_10105 ? Blocks.field_10414.method_9564().method_11657(field_9883, blockState.method_11654(field_9883)) : null;
		}
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_9883, rotation.method_10503(blockState.method_11654(field_9883)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9883);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
