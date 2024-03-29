package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SpawnerBlock extends BlockWithEntity {
	public static final MapCodec<SpawnerBlock> CODEC = createCodec(SpawnerBlock::new);

	@Override
	public MapCodec<SpawnerBlock> getCodec() {
		return CODEC;
	}

	protected SpawnerBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MobSpawnerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, BlockEntityType.MOB_SPAWNER, world.isClient ? MobSpawnerBlockEntity::clientTick : MobSpawnerBlockEntity::serverTick);
	}

	@Override
	protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		super.onStacksDropped(state, world, pos, tool, dropExperience);
		if (dropExperience) {
			int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);
			this.dropExperience(world, pos, i);
		}
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void appendTooltip(
		ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options, @Nullable DynamicRegistryManager registryManager
	) {
		super.appendTooltip(stack, world, tooltip, options, registryManager);
		Spawner.appendSpawnDataToTooltip(stack, tooltip, "SpawnData");
	}
}
