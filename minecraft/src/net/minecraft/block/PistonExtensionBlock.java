package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameters;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty field_12196 = PistonHeadBlock.field_10927;
	public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.field_12224;

	public PistonExtensionBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_12196, Direction.NORTH).with(TYPE, PistonType.field_12637));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return null;
	}

	public static BlockEntity method_11489(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		return new PistonBlockEntity(blockState, direction, bl, bl2);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).method_11513();
			} else {
				super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			}
		}
	}

	@Override
	public void onBroken(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.method_10093(((Direction)blockState.get(field_12196)).getOpposite());
		BlockState blockState2 = iWorld.getBlockState(blockPos2);
		if (blockState2.getBlock() instanceof PistonBlock && (Boolean)blockState2.get(PistonBlock.field_12191)) {
			iWorld.clearBlockState(blockPos2);
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!world.isRemote && world.getBlockEntity(blockPos) == null) {
			world.clearBlockState(blockPos);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		PistonBlockEntity pistonBlockEntity = this.getBlockEntityPiston(builder.getWorld(), builder.get(Parameters.field_1232));
		return pistonBlockEntity == null ? Collections.emptyList() : pistonBlockEntity.getPushedBlock().getDroppedStacks(builder);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		PistonBlockEntity pistonBlockEntity = this.getBlockEntityPiston(blockView, blockPos);
		return pistonBlockEntity != null ? pistonBlockEntity.method_11512(blockView, blockPos) : VoxelShapes.empty();
	}

	@Nullable
	private PistonBlockEntity getBlockEntityPiston(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_12196, rotation.method_10503(blockState.get(field_12196)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_12196)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_12196, TYPE);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
