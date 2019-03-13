package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BarrelBlock extends BlockWithEntity {
	public static final DirectionProperty field_16320 = Properties.field_12525;
	public static final BooleanProperty field_18006 = Properties.field_12537;

	public BarrelBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_16320, Direction.NORTH).method_11657(field_18006, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof BarrelBlockEntity) {
				playerEntity.openContainer((BarrelBlockEntity)blockEntity);
				playerEntity.method_7281(Stats.field_17271);
			}

			return true;
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.method_5451(world, blockPos, (Inventory)blockEntity);
				world.method_8455(blockPos, this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Nullable
	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new BarrelBlockEntity();
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof BarrelBlockEntity) {
				((BarrelBlockEntity)blockEntity).method_17488(itemStack.method_7964());
			}
		}
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.method_7608(world.method_8321(blockPos));
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_16320, rotation.method_10503(blockState.method_11654(field_16320)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_16320)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_16320, field_18006);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_16320, itemPlacementContext.method_7715().getOpposite());
	}
}
