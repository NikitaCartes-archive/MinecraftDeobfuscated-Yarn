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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
    private final Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = Maps.newHashMap();
    public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
    private final BufferBuilder bufferBuilder = new BufferBuilder(256);
    private TextRenderer textRenderer;
    public TextureManager textureManager;
    public World world;
    public Camera camera;
    public HitResult crosshairTarget;

    private BlockEntityRenderDispatcher() {
        this.register(BlockEntityType.SIGN, new SignBlockEntityRenderer(this));
        this.register(BlockEntityType.MOB_SPAWNER, new MobSpawnerBlockEntityRenderer(this));
        this.register(BlockEntityType.PISTON, new PistonBlockEntityRenderer(this));
        this.register(BlockEntityType.CHEST, new ChestBlockEntityRenderer(this));
        this.register(BlockEntityType.ENDER_CHEST, new ChestBlockEntityRenderer(this));
        this.register(BlockEntityType.TRAPPED_CHEST, new ChestBlockEntityRenderer(this));
        this.register(BlockEntityType.ENCHANTING_TABLE, new EnchantingTableBlockEntityRenderer(this));
        this.register(BlockEntityType.LECTERN, new LecternBlockEntityRenderer(this));
        this.register(BlockEntityType.END_PORTAL, new EndPortalBlockEntityRenderer(this));
        this.register(BlockEntityType.END_GATEWAY, new EndGatewayBlockEntityRenderer(this));
        this.register(BlockEntityType.BEACON, new BeaconBlockEntityRenderer(this));
        this.register(BlockEntityType.SKULL, new SkullBlockEntityRenderer(this));
        this.register(BlockEntityType.BANNER, new BannerBlockEntityRenderer(this));
        this.register(BlockEntityType.STRUCTURE_BLOCK, new StructureBlockBlockEntityRenderer(this));
        this.register(BlockEntityType.SHULKER_BOX, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel(), this));
        this.register(BlockEntityType.BED, new BedBlockEntityRenderer(this));
        this.register(BlockEntityType.CONDUIT, new ConduitBlockEntityRenderer(this));
        this.register(BlockEntityType.BELL, new BellBlockEntityRenderer(this));
        this.register(BlockEntityType.CAMPFIRE, new CampfireBlockEntityRenderer(this));
    }

    private <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer) {
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
        this.camera = camera;
        this.textRenderer = textRenderer;
        this.crosshairTarget = hitResult;
    }

    public <E extends BlockEntity> void render(E blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        if (!(blockEntity.getSquaredDistance(this.camera.getPos().x, this.camera.getPos().y, this.camera.getPos().z) < blockEntity.getSquaredRenderDistance())) {
            return;
        }
        BlockEntityRenderer blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer == null) {
            return;
        }
        if (!blockEntity.hasWorld() || !blockEntity.getType().supports(blockEntity.getCachedState().getBlock())) {
            return;
        }
        BlockEntityRenderDispatcher.runReported(blockEntity, () -> BlockEntityRenderDispatcher.render(blockEntityRenderer, blockEntity, f, matrixStack, vertexConsumerProvider));
    }

    private static <T extends BlockEntity> void render(BlockEntityRenderer<T> blockEntityRenderer, T blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        World world = blockEntity.getWorld();
        int i = world != null ? WorldRenderer.method_23794(world, blockEntity.getPos()) : 0xF000F0;
        blockEntityRenderer.render(blockEntity, f, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
    }

    @Deprecated
    public <E extends BlockEntity> void renderEntity(E blockEntity, MatrixStack matrixStack) {
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(this.bufferBuilder);
        this.renderEntity(blockEntity, matrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV);
        immediate.draw();
    }

    public <E extends BlockEntity> boolean renderEntity(E blockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockEntityRenderer blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer == null) {
            return true;
        }
        BlockEntityRenderDispatcher.runReported(blockEntity, () -> blockEntityRenderer.render(blockEntity, 0.0f, matrixStack, vertexConsumerProvider, i, j));
        return false;
    }

    private static void runReported(BlockEntity blockEntity, Runnable runnable) {
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
            this.camera = null;
        }
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}

