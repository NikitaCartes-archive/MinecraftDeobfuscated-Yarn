package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = PistonHeadBlock.FACING;
	public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

	public PistonExtensionBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return null;
	}

	public static BlockEntity createBlockEntityPiston(BlockState pushedBlock, Direction dir, boolean extending, boolean bl) {
		return new PistonBlockEntity(pushedBlock, dir, extending, bl);
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}
		}
	}

	@Override
	public void onBroken(IWorld world, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof PistonBlock && (Boolean)blockState.get(PistonBlock.EXTENDED)) {
			world.removeBlock(blockPos, false);
		}
	}

	@Override
	public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && world.getBlockEntity(pos) == null) {
			world.removeBlock(pos, false);
			return ActionResult.CONSUME;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		PistonBlockEntity pistonBlockEntity = this.getPistonBlockEntity(builder.getWorld(), builder.get(LootContextParameters.POSITION));
		return pistonBlockEntity == null ? Collections.emptyList() : pistonBlockEntity.getPushedBlock().getDroppedStacks(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		PistonBlockEntity pistonBlockEntity = this.getPistonBlockEntity(view, pos);
		return pistonBlockEntity != null ? pistonBlockEntity.getCollisionShape(view, pos) : VoxelShapes.empty();
	}

	@Nullable
	private PistonBlockEntity getPistonBlockEntity(BlockView world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, TYPE);
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}
}
