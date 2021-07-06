/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRendererFactories {
    private static final Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> FACTORIES = Maps.newHashMap();

    private static <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> factory) {
        FACTORIES.put(type, factory);
    }

    public static Map<BlockEntityType<?>, BlockEntityRenderer<?>> reload(BlockEntityRendererFactory.Context args) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        FACTORIES.forEach((type, factory) -> {
            try {
                builder.put(type, factory.create(args));
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to create model for " + Registry.BLOCK_ENTITY_TYPE.getId((BlockEntityType<?>)type), exception);
            }
        });
        return builder.build();
    }

    static {
        BlockEntityRendererFactories.register(BlockEntityType.SIGN, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.MOB_SPAWNER, MobSpawnerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.PISTON, PistonBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.CHEST, ChestBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.ENDER_CHEST, ChestBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.TRAPPED_CHEST, ChestBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.LECTERN, LecternBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.END_PORTAL, EndPortalBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.END_GATEWAY, EndGatewayBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.BEACON, BeaconBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.SKULL, SkullBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.BANNER, BannerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.STRUCTURE_BLOCK, StructureBlockBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.BED, BedBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.CONDUIT, ConduitBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.BELL, BellBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.CAMPFIRE, CampfireBlockEntityRenderer::new);
    }
}

