package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Spawner;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TrialSpawnerBlock extends BlockWithEntity {
	public static final MapCodec<TrialSpawnerBlock> CODEC = createCodec(TrialSpawnerBlock::new);
	public static final EnumProperty<TrialSpawnerState> TRIAL_SPAWNER_STATE = Properties.TRIAL_SPAWNER_STATE;

	@Override
	public MapCodec<TrialSpawnerBlock> getCodec() {
		return CODEC;
	}

	public TrialSpawnerBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(TRIAL_SPAWNER_STATE, TrialSpawnerState.INACTIVE));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TRIAL_SPAWNER_STATE);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TrialSpawnerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world instanceof ServerWorld serverWorld
			? validateTicker(type, BlockEntityType.TRIAL_SPAWNER, (worldx, pos, statex, blockEntity) -> blockEntity.getSpawner().tickServer(serverWorld, pos))
			: validateTicker(type, BlockEntityType.TRIAL_SPAWNER, (worldx, pos, statex, blockEntity) -> blockEntity.getSpawner().tickClient(worldx, pos));
	}

	@Override
	public void appendTooltip(
		ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options, @Nullable DynamicRegistryManager registryManager
	) {
		super.appendTooltip(stack, world, tooltip, options, registryManager);
		Spawner.appendSpawnDataToTooltip(stack, tooltip, "spawn_data");
	}
}
