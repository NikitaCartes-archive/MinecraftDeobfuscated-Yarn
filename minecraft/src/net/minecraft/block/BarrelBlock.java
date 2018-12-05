package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BarrelBlock extends BlockWithEntity {
	public static final DirectionProperty field_16320 = Properties.field_12525;

	public BarrelBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_16320, Direction.NORTH));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BarrelBlockEntity();
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BarrelBlockEntity) {
				((BarrelBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_16320, rotation.method_10503(blockState.get(field_16320)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_16320)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16320);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_16320, itemPlacementContext.method_7715().getOpposite());
	}
}
