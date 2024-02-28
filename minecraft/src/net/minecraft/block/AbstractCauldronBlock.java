package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * The base class for all cauldrons.
 * 
 * <p>Interaction with cauldrons is controlled by {@linkplain CauldronBehavior
 * cauldron behaviors}.
 * 
 * @see CauldronBlock empty cauldrons
 * @see LavaCauldronBlock cauldrons filled with lava
 * @see LeveledCauldronBlock cauldrons with varying levels of contents
 */
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
	protected final CauldronBehavior.CauldronBehaviorMap behaviorMap;

	@Override
	protected abstract MapCodec<? extends AbstractCauldronBlock> getCodec();

	/**
	 * Constructs a cauldron block.
	 * 
	 * <p>The behavior map must match {@link CauldronBehavior#createMap} by providing
	 * a nonnull value for <em>all</em> items.
	 */
	public AbstractCauldronBlock(AbstractBlock.Settings settings, CauldronBehavior.CauldronBehaviorMap behaviorMap) {
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
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		CauldronBehavior cauldronBehavior = (CauldronBehavior)this.behaviorMap.map().get(stack.getItem());
		return cauldronBehavior.interact(state, world, pos, player, hand, stack);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return RAYCAST_SHAPE;
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	/**
	 * {@return {@code true} if the specified cauldron state is completely full,
	 * {@code false} otherwise}
	 * 
	 * @param state the cauldron state to check
	 */
	public abstract boolean isFull(BlockState state);

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockPos blockPos = PointedDripstoneBlock.getDripPos(world, pos);
		if (blockPos != null) {
			Fluid fluid = PointedDripstoneBlock.getDripFluid(world, blockPos);
			if (fluid != Fluids.EMPTY && this.canBeFilledByDripstone(fluid)) {
				this.fillFromDripstone(state, world, pos, fluid);
			}
		}
	}

	/**
	 * Checks if this cauldron block can be filled with the specified fluid by dripstone.
	 * 
	 * @return {@code true} if this block can be filled, {@code false} otherwise
	 * 
	 * @param fluid the fluid to check
	 */
	protected boolean canBeFilledByDripstone(Fluid fluid) {
		return false;
	}

	/**
	 * Fills a cauldron with one level of the specified fluid if possible.
	 * 
	 * @param pos the cauldron's position
	 * @param world the world where the cauldron is located
	 * @param fluid the fluid to fill the cauldron with
	 * @param state the current cauldron state
	 */
	protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
	}
}
