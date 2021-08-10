package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderDispatcher implements SynchronousResourceReloader {
	private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
	private final BufferBuilder field_20988 = new BufferBuilder(256);
	private final TextRenderer textRenderer;
	private final EntityModelLoader entityModelLoader;
	public World world;
	public Camera camera;
	public HitResult crosshairTarget;
	private final Supplier<BlockRenderManager> blockRenderManager;

	public BlockEntityRenderDispatcher(TextRenderer textRenderer, EntityModelLoader entityModelLoader, Supplier<BlockRenderManager> blockRenderManager) {
		this.textRenderer = textRenderer;
		this.entityModelLoader = entityModelLoader;
		this.blockRenderManager = blockRenderManager;
	}

	@Nullable
	public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
		return (BlockEntityRenderer<E>)this.renderers.get(blockEntity.getType());
	}

	public void configure(World world, Camera camera, HitResult crosshairTarget) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.camera = camera;
		this.crosshairTarget = crosshairTarget;
	}

	public <E extends BlockEntity> void render(E blockEntity, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers) {
		BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
		if (blockEntityRenderer != null) {
			if (blockEntity.hasWorld() && blockEntity.getType().supports(blockEntity.getCachedState())) {
				if (blockEntityRenderer.isInRenderDistance(blockEntity, this.camera.getPos())) {
					runReported(blockEntity, () -> render(blockEntityRenderer, blockEntity, tickDelta, matrix, vertexConsumers));
				}
			}
		}
	}

	private static <T extends BlockEntity> void render(
		BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers
	) {
		World world = blockEntity.getWorld();
		int i;
		if (world != null) {
			i = WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos());
		} else {
			i = LightmapTextureManager.MAX_LIGHT_COORDINATE;
		}

		renderer.render(blockEntity, tickDelta, matrices, vertexConsumers, i, OverlayTexture.DEFAULT_UV);
	}

	public <E extends BlockEntity> boolean renderEntity(E entity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		BlockEntityRenderer<E> blockEntityRenderer = this.get(entity);
		if (blockEntityRenderer == null) {
			return true;
		} else {
			runReported(entity, () -> blockEntityRenderer.render(entity, 0.0F, matrix, vertexConsumerProvider, light, overlay));
			return false;
		}
	}

	private static void runReported(BlockEntity blockEntity, Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Rendering Block Entity");
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

	@Override
	public void reload(ResourceManager manager) {
		BlockEntityRendererFactory.Context context = new BlockEntityRendererFactory.Context(
			this, (BlockRenderManager)this.blockRenderManager.get(), this.entityModelLoader, this.textRenderer
		);
		this.renderers = BlockEntityRendererFactories.reload(context);
	}
}
