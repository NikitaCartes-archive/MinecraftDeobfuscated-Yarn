package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MoonCowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CopperSpleavesBlock extends Block {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 1.0, 1.0, 15.0, 15.0, 15.0);
	public static Property<Boolean> FALLING = Properties.FALLING;

	public CopperSpleavesBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FALLING, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FALLING);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onSteppedOn(world, pos, state, entity);
		if (!(entity instanceof MoonCowEntity)) {
			if (!(Boolean)state.get(FALLING) && world instanceof ServerWorld serverWorld) {
				world.setBlockState(pos, state.with(FALLING, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
				world.scheduleBlockTick(pos, this, 7);
				int i = world.random.nextInt(3) + 1;
				int j = world.random.nextInt(3) + i;
				serverWorld.playSound(null, pos, SoundEvents.BLOCK_COPPER_BREAK, SoundCategory.BLOCKS, 0.25F, 1.2F);
				serverWorld.getServer().method_51106(i, () -> serverWorld.playSound(null, pos, SoundEvents.BLOCK_COPPER_BREAK, SoundCategory.BLOCKS, 0.28F, 1.5F));
				serverWorld.getServer().method_51106(j, () -> serverWorld.playSound(null, pos, SoundEvents.BLOCK_COPPER_BREAK, SoundCategory.BLOCKS, 0.39F, 1.8F));
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		world.breakBlock(pos, false);
	}
}
