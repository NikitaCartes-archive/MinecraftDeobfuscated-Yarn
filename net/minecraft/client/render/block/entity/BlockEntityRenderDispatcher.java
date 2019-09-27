/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
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
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
    private final Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = Maps.newHashMap();
    public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
    private final BufferBuilder field_20988 = new BufferBuilder(256);
    private TextRenderer fontRenderer;
    public TextureManager textureManager;
    public World world;
    public Camera cameraEntity;
    public HitResult hitResult;

    private BlockEntityRenderDispatcher() {
        this.method_23078(BlockEntityType.SIGN, new SignBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.MOB_SPAWNER, new MobSpawnerBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.PISTON, new PistonBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.CHEST, new ChestBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.ENDER_CHEST, new ChestBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.TRAPPED_CHEST, new ChestBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.ENCHANTING_TABLE, new EnchantingTableBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.LECTERN, new LecternBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.END_PORTAL, new EndPortalBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.END_GATEWAY, new EndGatewayBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.BEACON, new BeaconBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.SKULL, new SkullBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.BANNER, new BannerBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.STRUCTURE_BLOCK, new StructureBlockBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.SHULKER_BOX, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel(), this));
        this.method_23078(BlockEntityType.BED, new BedBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.CONDUIT, new ConduitBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.BELL, new BellBlockEntityRenderer(this));
        this.method_23078(BlockEntityType.CAMPFIRE, new CampfireBlockEntityRenderer(this));
    }

    private <E extends BlockEntity> void method_23078(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer) {
        this.renderers.put(blockEntityType, blockEntityRenderer);
    }

    @Nullable
    public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
        return this.renderers.get(blockEntity.getType());
    }

    public void configure(World world, TextureManager textureManager, TextRenderer textRenderer, Camera camera, HitResult hitResult) {
        if (this.world != world) {
            this.setWorld(world);
        }
        this.textureManager = textureManager;
        this.cameraEntity = camera;
        this.fontRenderer = textRenderer;
        this.hitResult = hitResult;
    }

    public <E extends BlockEntity> void render(E blockEntity, float f, class_4587 arg, class_4597 arg2, double d, double e, double g) {
        if (!(blockEntity.getSquaredDistance(this.cameraEntity.getPos().x, this.cameraEntity.getPos().y, this.cameraEntity.getPos().z) < blockEntity.getSquaredRenderDistance())) {
            return;
        }
        BlockEntityRenderer blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer == null) {
            return;
        }
        if (!blockEntity.hasWorld() || !blockEntity.getType().supports(blockEntity.getCachedState().getBlock())) {
            return;
        }
        BlockPos blockPos = blockEntity.getPos();
        BlockEntityRenderDispatcher.renderEntity(blockEntity, () -> BlockEntityRenderDispatcher.method_23079(blockEntityRenderer, blockEntity, (double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - g, f, arg, arg2));
    }

    private static <T extends BlockEntity> void method_23079(BlockEntityRenderer<T> blockEntityRenderer, T blockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2) {
        World world = blockEntity.getWorld();
        int i = world != null ? world.getLightmapIndex(blockEntity.getPos()) : 0xF000F0;
        blockEntityRenderer.render(blockEntity, d, e, f, g, arg, arg2, i);
    }

    @Deprecated
    public <E extends BlockEntity> void renderEntity(E blockEntity, class_4587 arg, int i) {
        class_4597.class_4598 lv = class_4597.method_22991(this.field_20988);
        this.method_23077(blockEntity, arg, lv, i);
        lv.method_22993();
    }

    public <E extends BlockEntity> boolean method_23077(E blockEntity, class_4587 arg, class_4597 arg2, int i) {
        BlockEntityRenderer blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer == null) {
            return true;
        }
        BlockEntityRenderDispatcher.renderEntity(blockEntity, () -> blockEntityRenderer.render(blockEntity, 0.0, 0.0, 0.0, 0.0f, arg, arg2, i));
        return false;
    }

    private static void renderEntity(BlockEntity blockEntity, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering Block Entity");
            CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
            blockEntity.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.cameraEntity = null;
        }
    }

    public TextRenderer getFontRenderer() {
        return this.fontRenderer;
    }
}

