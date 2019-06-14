package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public class FluidBlock extends Block implements FluidDrainable {
	public static final IntProperty field_11278 = Properties.field_12538;
	protected final BaseFluid field_11279;
	private final List<FluidState> statesByLevel;

	protected FluidBlock(BaseFluid baseFluid, Block.Settings settings) {
		super(settings);
		this.field_11279 = baseFluid;
		this.statesByLevel = Lists.<FluidState>newArrayList();
		this.statesByLevel.add(baseFluid.method_15729(false));

		for (int i = 1; i < 8; i++) {
			this.statesByLevel.add(baseFluid.method_15728(8 - i, false));
		}

		this.statesByLevel.add(baseFluid.method_15728(8, true));
		this.method_9590(this.field_10647.method_11664().method_11657(field_11278, Integer.valueOf(0)));
	}

	@Override
	public void method_9514(BlockState blockState, World world, BlockPos blockPos, Random random) {
		world.method_8316(blockPos).onRandomTick(world, blockPos, random);
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return !this.field_11279.matches(FluidTags.field_15518);
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		int i = (Integer)blockState.method_11654(field_11278);
		return (FluidState)this.statesByLevel.get(Math.min(i, 8));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9522(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.method_11618().getFluid().matchesType(this.field_11279) ? true : super.method_9601(blockState);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public List<ItemStack> method_9560(BlockState blockState, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.method_1073();
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.field_11279.getTickRate(viewableWorld);
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (this.method_10316(world, blockPos, blockState)) {
			world.method_8405().schedule(blockPos, blockState.method_11618().getFluid(), this.getTickRate(world));
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (blockState.method_11618().isStill() || blockState2.method_11618().isStill()) {
			iWorld.method_8405().schedule(blockPos, blockState.method_11618().getFluid(), this.getTickRate(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (this.method_10316(world, blockPos, blockState)) {
			world.method_8405().schedule(blockPos, blockState.method_11618().getFluid(), this.getTickRate(world));
		}
	}

	public boolean method_10316(World world, BlockPos blockPos, BlockState blockState) {
		if (this.field_11279.matches(FluidTags.field_15518)) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.field_11033 && world.method_8316(blockPos.offset(direction)).matches(FluidTags.field_15517)) {
					bl = true;
					break;
				}
			}

			if (bl) {
				FluidState fluidState = world.method_8316(blockPos);
				if (fluidState.isStill()) {
					world.method_8501(blockPos, Blocks.field_10540.method_9564());
					this.playExtinguishSound(world, blockPos);
					return false;
				}

				if (fluidState.getHeight(world, blockPos) >= 0.44444445F) {
					world.method_8501(blockPos, Blocks.field_10445.method_9564());
					this.playExtinguishSound(world, blockPos);
					return false;
				}
			}
		}

		return true;
	}

	private void playExtinguishSound(IWorld iWorld, BlockPos blockPos) {
		iWorld.playLevelEvent(1501, blockPos, 0);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11278);
	}

	@Override
	public Fluid method_9700(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Integer)blockState.method_11654(field_11278) == 0) {
			iWorld.method_8652(blockPos, Blocks.field_10124.method_9564(), 11);
			return this.field_11279;
		} else {
			return Fluids.field_15906;
		}
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (this.field_11279.matches(FluidTags.field_15518)) {
			entity.setInLava();
		}
	}
}
