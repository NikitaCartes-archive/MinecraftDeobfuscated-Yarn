package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class JigsawBlock extends Block implements BlockEntityProvider, OperatorBlock {
	public static final MapCodec<JigsawBlock> CODEC = createCodec(JigsawBlock::new);
	public static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;

	@Override
	public MapCodec<JigsawBlock> getCodec() {
		return CODEC;
	}

	protected JigsawBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ORIENTATION, Orientation.NORTH_UP));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		Direction direction2;
		if (direction.getAxis() == Direction.Axis.Y) {
			direction2 = ctx.getHorizontalPlayerFacing().getOpposite();
		} else {
			direction2 = Direction.UP;
		}

		return this.getDefaultState().with(ORIENTATION, Orientation.byDirections(direction, direction2));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new JigsawBlockEntity(pos, state);
	}

	@Override
	public ActionResult method_55766(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof JigsawBlockEntity && playerEntity.isCreativeLevelTwoOp()) {
			playerEntity.openJigsawScreen((JigsawBlockEntity)blockEntity);
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	public static boolean attachmentMatches(StructureTemplate.StructureBlockInfo info1, StructureTemplate.StructureBlockInfo info2) {
		Direction direction = getFacing(info1.state());
		Direction direction2 = getFacing(info2.state());
		Direction direction3 = getRotation(info1.state());
		Direction direction4 = getRotation(info2.state());
		JigsawBlockEntity.Joint joint = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(info1.nbt().getString("joint"))
			.orElseGet(() -> direction.getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE);
		boolean bl = joint == JigsawBlockEntity.Joint.ROLLABLE;
		return direction == direction2.getOpposite() && (bl || direction3 == direction4) && info1.nbt().getString("target").equals(info2.nbt().getString("name"));
	}

	public static Direction getFacing(BlockState state) {
		return ((Orientation)state.get(ORIENTATION)).getFacing();
	}

	public static Direction getRotation(BlockState state) {
		return ((Orientation)state.get(ORIENTATION)).getRotation();
	}
}
