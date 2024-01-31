package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;

@Environment(EnvType.CLIENT)
public class BlockEntityRendererFactories {
	private static final Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> FACTORIES = Maps.<BlockEntityType<?>, BlockEntityRendererFactory<?>>newHashMap();

	private static <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> factory) {
		FACTORIES.put(type, factory);
	}

	public static Map<BlockEntityType<?>, BlockEntityRenderer<?>> reload(BlockEntityRendererFactory.Context args) {
		Builder<BlockEntityType<?>, BlockEntityRenderer<?>> builder = ImmutableMap.builder();
		FACTORIES.forEach((type, factory) -> {
			try {
				builder.put(type, factory.create(args));
			} catch (Exception var5) {
				throw new IllegalStateException("Failed to create model for " + Registries.BLOCK_ENTITY_TYPE.getId(type), var5);
			}
		});
		return builder.build();
	}

	static {
		register(BlockEntityType.SIGN, SignBlockEntityRenderer::new);
		register(BlockEntityType.HANGING_SIGN, HangingSignBlockEntityRenderer::new);
		register(BlockEntityType.MOB_SPAWNER, MobSpawnerBlockEntityRenderer::new);
		register(BlockEntityType.PISTON, PistonBlockEntityRenderer::new);
		register(BlockEntityType.CHEST, ChestBlockEntityRenderer::new);
		register(BlockEntityType.ENDER_CHEST, ChestBlockEntityRenderer::new);
		register(BlockEntityType.TRAPPED_CHEST, ChestBlockEntityRenderer::new);
		register(BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntityRenderer::new);
		register(BlockEntityType.LECTERN, LecternBlockEntityRenderer::new);
		register(BlockEntityType.END_PORTAL, EndPortalBlockEntityRenderer::new);
		register(BlockEntityType.END_GATEWAY, EndGatewayBlockEntityRenderer::new);
		register(BlockEntityType.BEACON, BeaconBlockEntityRenderer::new);
		register(BlockEntityType.SKULL, SkullBlockEntityRenderer::new);
		register(BlockEntityType.BANNER, BannerBlockEntityRenderer::new);
		register(BlockEntityType.STRUCTURE_BLOCK, StructureBlockBlockEntityRenderer::new);
		register(BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntityRenderer::new);
		register(BlockEntityType.BED, BedBlockEntityRenderer::new);
		register(BlockEntityType.CONDUIT, ConduitBlockEntityRenderer::new);
		register(BlockEntityType.BELL, BellBlockEntityRenderer::new);
		register(BlockEntityType.CAMPFIRE, CampfireBlockEntityRenderer::new);
		register(BlockEntityType.BRUSHABLE_BLOCK, BrushableBlockEntityRenderer::new);
		register(BlockEntityType.DECORATED_POT, DecoratedPotBlockEntityRenderer::new);
		register(BlockEntityType.TRIAL_SPAWNER, TrialSpawnerBlockEntityRenderer::new);
		register(BlockEntityType.VAULT, VaultBlockEntityRenderer::new);
	}
}
