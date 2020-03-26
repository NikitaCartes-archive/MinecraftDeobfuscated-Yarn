package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JigsawBlock extends Block implements BlockEntityProvider {
	public static final EnumProperty<JigsawOrientation> ORIENTATION = Properties.ORIENTATION;

	protected JigsawBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ORIENTATION, JigsawOrientation.NORTH_UP));
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
			direction2 = ctx.getPlayerFacing().getOpposite();
		} else {
			direction2 = Direction.UP;
		}

		return this.getDefaultState().with(ORIENTATION, JigsawOrientation.byDirections(direction, direction2));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new JigsawBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof JigsawBlockEntity && player.isCreativeLevelTwoOp()) {
			player.openJigsawScreen((JigsawBlockEntity)blockEntity);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	public static boolean attachmentMatches(Structure.StructureBlockInfo info1, Structure.StructureBlockInfo info2) {
		Direction direction = method_26378(info1.state);
		Direction direction2 = method_26378(info2.state);
		Direction direction3 = method_26379(info1.state);
		Direction direction4 = method_26379(info2.state);
		JigsawBlockEntity.Joint joint = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(info1.tag.getString("joint"))
			.orElseGet(() -> direction.getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE);
		boolean bl = joint == JigsawBlockEntity.Joint.ROLLABLE;
		return direction == direction2.getOpposite() && (bl || direction3 == direction4) && info1.tag.getString("target").equals(info2.tag.getString("name"));
	}

	public static Direction method_26378(BlockState blockState) {
		return ((JigsawOrientation)blockState.get(ORIENTATION)).method_26426();
	}

	public static Direction method_26379(BlockState blockState) {
		return ((JigsawOrientation)blockState.get(ORIENTATION)).method_26428();
	}
}
