package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EnderChestBlock extends BlockWithEntity implements Waterloggable {
	public static final DirectionProperty field_10966 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_10968 = Properties.field_12508;
	protected static final VoxelShape field_10967 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	public static final TranslatableTextComponent field_17363 = new TranslatableTextComponent("container.enderchest");

	protected EnderChestBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10966, Direction.NORTH).method_11657(field_10968, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10967;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		return this.method_9564()
			.method_11657(field_10966, itemPlacementContext.method_8042().getOpposite())
			.method_11657(field_10968, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		EnderChestInventory enderChestInventory = playerEntity.method_7274();
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (enderChestInventory != null && blockEntity instanceof EnderChestBlockEntity) {
			BlockPos blockPos2 = blockPos.up();
			if (world.method_8320(blockPos2).method_11621(world, blockPos2)) {
				return true;
			} else if (world.isClient) {
				return true;
			} else {
				EnderChestBlockEntity enderChestBlockEntity = (EnderChestBlockEntity)blockEntity;
				enderChestInventory.method_7661(enderChestBlockEntity);
				playerEntity.openContainer(
					new ClientDummyContainerProvider(
						(i, playerInventory, playerEntityx) -> GenericContainer.method_19245(i, playerInventory, enderChestInventory), field_17363
					)
				);
				playerEntity.method_7281(Stats.field_15424);
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new EnderChestBlockEntity();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		for (int i = 0; i < 3; i++) {
			int j = random.nextInt(2) * 2 - 1;
			int k = random.nextInt(2) * 2 - 1;
			double d = (double)blockPos.getX() + 0.5 + 0.25 * (double)j;
			double e = (double)((float)blockPos.getY() + random.nextFloat());
			double f = (double)blockPos.getZ() + 0.5 + 0.25 * (double)k;
			double g = (double)(random.nextFloat() * (float)j);
			double h = ((double)random.nextFloat() - 0.5) * 0.125;
			double l = (double)(random.nextFloat() * (float)k);
			world.method_8406(ParticleTypes.field_11214, d, e, f, g, h, l);
		}
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_10966, rotation.method_10503(blockState.method_11654(field_10966)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_10966)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10966, field_10968);
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_10968) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_10968)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
