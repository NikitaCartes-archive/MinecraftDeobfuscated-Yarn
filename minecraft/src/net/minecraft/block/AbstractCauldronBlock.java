package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractCauldronBlock extends Block {
	private static final int field_30989 = 2;
	private static final int field_30990 = 4;
	private static final int field_30991 = 3;
	private static final int field_30992 = 2;
	protected static final int field_30988 = 4;
	private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
	protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
		VoxelShapes.fullCube(),
		VoxelShapes.union(
			createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
			createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
			createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
			RAYCAST_SHAPE
		),
		BooleanBiFunction.ONLY_FIRST
	);
	private final Map<Item, CauldronBehavior> behaviorMap;

	public AbstractCauldronBlock(AbstractBlock.Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
		super(settings);
		this.behaviorMap = behaviorMap;
	}

	protected double getFluidHeight(BlockState state) {
		return 0.0;
	}

	protected boolean isEntityTouchingFluid(BlockState state, BlockPos pos, Entity entity) {
		return entity.getY() < (double)pos.getY() + this.getFluidHeight(state) && entity.getBoundingBox().maxY > (double)pos.getY() + 0.25;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		CauldronBehavior cauldronBehavior = (CauldronBehavior)this.behaviorMap.get(itemStack.getItem());
		return cauldronBehavior.interact(state, world, pos, player, hand, itemStack);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return RAYCAST_SHAPE;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	public abstract boolean isFull(BlockState state);

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockPos blockPos = PointedDripstoneBlock.getDripPos(world, pos);
		if (blockPos != null) {
			Fluid fluid = PointedDripstoneBlock.getDripFluid(world, blockPos);
			if (fluid != Fluids.EMPTY && this.canBeFilledByDripstone(fluid)) {
				this.fillFromDripstone(state, world, pos, fluid);
			}
		}
	}

	protected boolean canBeFilledByDripstone(Fluid fluid) {
		return false;
	}

	protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
	}
}
