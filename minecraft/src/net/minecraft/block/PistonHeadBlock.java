package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class PistonHeadBlock extends FacingBlock {
	public static final EnumProperty<PistonType> field_12224 = Properties.field_12492;
	public static final BooleanProperty field_12227 = Properties.field_12535;
	protected static final VoxelShape field_12222 = Block.method_9541(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12214 = Block.method_9541(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
	protected static final VoxelShape field_12228 = Block.method_9541(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12213 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
	protected static final VoxelShape field_12230 = Block.method_9541(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12220 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
	protected static final VoxelShape field_12215 = Block.method_9541(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape field_12226 = Block.method_9541(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
	protected static final VoxelShape field_12221 = Block.method_9541(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape field_12229 = Block.method_9541(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
	protected static final VoxelShape field_12218 = Block.method_9541(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape field_12223 = Block.method_9541(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
	protected static final VoxelShape field_12231 = Block.method_9541(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape field_12217 = Block.method_9541(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape field_12216 = Block.method_9541(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape field_12225 = Block.method_9541(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape field_12219 = Block.method_9541(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape field_12212 = Block.method_9541(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	public PistonHeadBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10927, Direction.field_11043)
				.method_11657(field_12224, PistonType.field_12637)
				.method_11657(field_12227, Boolean.valueOf(false))
		);
	}

	private VoxelShape method_11520(BlockState blockState) {
		switch ((Direction)blockState.method_11654(field_10927)) {
			case field_11033:
			default:
				return field_12220;
			case field_11036:
				return field_12230;
			case field_11043:
				return field_12213;
			case field_11035:
				return field_12228;
			case field_11039:
				return field_12214;
			case field_11034:
				return field_12222;
		}
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.method_1084(this.method_11520(blockState), this.method_11519(blockState));
	}

	private VoxelShape method_11519(BlockState blockState) {
		boolean bl = (Boolean)blockState.method_11654(field_12227);
		switch ((Direction)blockState.method_11654(field_10927)) {
			case field_11033:
			default:
				return bl ? field_12217 : field_12226;
			case field_11036:
				return bl ? field_12231 : field_12215;
			case field_11043:
				return bl ? field_12225 : field_12229;
			case field_11035:
				return bl ? field_12216 : field_12221;
			case field_11039:
				return bl ? field_12212 : field_12223;
			case field_11034:
				return bl ? field_12219 : field_12218;
		}
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && playerEntity.abilities.creativeMode) {
			BlockPos blockPos2 = blockPos.offset(((Direction)blockState.method_11654(field_10927)).getOpposite());
			Block block = world.method_8320(blockPos2).getBlock();
			if (block == Blocks.field_10560 || block == Blocks.field_10615) {
				world.clearBlockState(blockPos2, false);
			}
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			super.method_9536(blockState, world, blockPos, blockState2, bl);
			Direction direction = ((Direction)blockState.method_11654(field_10927)).getOpposite();
			blockPos = blockPos.offset(direction);
			BlockState blockState3 = world.method_8320(blockPos);
			if ((blockState3.getBlock() == Blocks.field_10560 || blockState3.getBlock() == Blocks.field_10615)
				&& (Boolean)blockState3.method_11654(PistonBlock.field_12191)) {
				method_9497(blockState3, world, blockPos);
				world.clearBlockState(blockPos, false);
			}
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction.getOpposite() == blockState.method_11654(field_10927) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.method_8320(blockPos.offset(((Direction)blockState.method_11654(field_10927)).getOpposite())).getBlock();
		return block == Blocks.field_10560 || block == Blocks.field_10615 || block == Blocks.field_10008;
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (blockState.canPlaceAt(world, blockPos)) {
			BlockPos blockPos3 = blockPos.offset(((Direction)blockState.method_11654(field_10927)).getOpposite());
			world.method_8320(blockPos3).neighborUpdate(world, blockPos3, block, blockPos2, false);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(blockState.method_11654(field_12224) == PistonType.field_12634 ? Blocks.field_10615 : Blocks.field_10560);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10927, blockRotation.rotate(blockState.method_11654(field_10927)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_10927)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10927, field_12224, field_12227);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
