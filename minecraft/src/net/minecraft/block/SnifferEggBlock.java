package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class SnifferEggBlock extends Block {
	public static final MapCodec<SnifferEggBlock> CODEC = createCodec(SnifferEggBlock::new);
	public static final int FINAL_HATCH_STAGE = 2;
	public static final IntProperty HATCH = Properties.HATCH;
	private static final int HATCHING_TIME = 24000;
	private static final int BOOSTED_HATCHING_TIME = 12000;
	private static final int MAX_RANDOM_CRACK_TIME_OFFSET = 300;
	private static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 2.0, 15.0, 16.0, 14.0);

	@Override
	public MapCodec<SnifferEggBlock> getCodec() {
		return CODEC;
	}

	public SnifferEggBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HATCH, Integer.valueOf(0)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HATCH);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	public int getHatchStage(BlockState state) {
		return (Integer)state.get(HATCH);
	}

	private boolean isReadyToHatch(BlockState state) {
		return this.getHatchStage(state) == 2;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!this.isReadyToHatch(state)) {
			world.playSound(null, pos, SoundEvents.BLOCK_SNIFFER_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
			world.setBlockState(pos, state.with(HATCH, Integer.valueOf(this.getHatchStage(state) + 1)), Block.NOTIFY_LISTENERS);
		} else {
			world.playSound(null, pos, SoundEvents.BLOCK_SNIFFER_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
			world.breakBlock(pos, false);
			SnifferEntity snifferEntity = EntityType.SNIFFER.create(world);
			if (snifferEntity != null) {
				Vec3d vec3d = pos.toCenterPos();
				snifferEntity.setBaby(true);
				snifferEntity.refreshPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
				world.spawnEntity(snifferEntity);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		boolean bl = isAboveHatchBooster(world, pos);
		if (!world.isClient() && bl) {
			world.syncWorldEvent(WorldEvents.SNIFFER_EGG_CRACKS, pos, 0);
		}

		int i = bl ? 12000 : 24000;
		int j = i / 3;
		world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(state));
		world.scheduleBlockTick(pos, this, j + world.random.nextInt(300));
	}

	@Override
	public boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	public static boolean isAboveHatchBooster(BlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).isIn(BlockTags.SNIFFER_EGG_HATCH_BOOST);
	}
}
