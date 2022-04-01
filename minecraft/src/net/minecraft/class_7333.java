package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;

public class class_7333 extends BlockWithEntity {
	public static final int field_38573 = 8;
	public static final BooleanProperty field_38574 = Properties.field_38609;
	private final IntProvider field_38575 = ConstantIntProvider.create(20);

	public class_7333(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_38574, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_38574);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(field_38574)) {
			world.setBlockState(pos, state.with(field_38574, Boolean.valueOf(false)), Block.NOTIFY_ALL);
		}
	}

	public static void method_42925(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random random) {
		serverWorld.setBlockState(blockPos, blockState.with(field_38574, Boolean.valueOf(true)), Block.NOTIFY_ALL);
		serverWorld.createAndScheduleBlockTick(blockPos, blockState.getBlock(), 8);
		serverWorld.spawnParticles(
			ParticleTypes.SCULK_SOUL, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.15, (double)blockPos.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0
		);
		serverWorld.playSound(null, blockPos, SoundEvents.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new class_7338(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
		return blockEntity instanceof class_7338 ? (class_7338)blockEntity : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, BlockEntityType.SCULK_CATALYST, class_7338::method_42957);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		this.method_42872(world, pos, stack, this.field_38575);
	}
}
