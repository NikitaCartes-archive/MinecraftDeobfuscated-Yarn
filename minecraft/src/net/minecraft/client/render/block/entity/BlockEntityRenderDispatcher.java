package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderDispatcher implements SynchronousResourceReloadListener {
	private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
	private final BufferBuilder bufferBuilder = new BufferBuilder(256);
	private final TextRenderer textRenderer;
	private final class_5599 field_27746;
	public World world;
	public Camera camera;
	public HitResult crosshairTarget;
	private final Supplier<BlockRenderManager> field_27747;

	public BlockEntityRenderDispatcher(TextRenderer textRenderer, class_5599 arg, Supplier<BlockRenderManager> supplier) {
		this.textRenderer = textRenderer;
		this.field_27746 = arg;
		this.field_27747 = supplier;
	}

	@Nullable
	public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
		return (BlockEntityRenderer<E>)this.renderers.get(blockEntity.getType());
	}

	public void configure(World world, Camera camera, HitResult hitResult) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.camera = camera;
		this.crosshairTarget = hitResult;
	}

	public <E extends BlockEntity> void render(E blockEntity, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider) {
		if (Vec3d.ofCenter(blockEntity.getPos()).isInRange(this.camera.getPos(), blockEntity.getSquaredRenderDistance())) {
			BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
			if (blockEntityRenderer != null) {
				if (blockEntity.hasWorld() && blockEntity.getType().supports(blockEntity.getCachedState())) {
					runReported(blockEntity, () -> render(blockEntityRenderer, blockEntity, tickDelta, matrix, vertexConsumerProvider));
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
			i = 15728880;
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
	public void apply(ResourceManager manager) {
		BlockEntityRendererFactory.Context context = new BlockEntityRendererFactory.Context(
			this, (BlockRenderManager)this.field_27747.get(), this.field_27746, this.textRenderer
		);
		this.renderers = BlockEntityRendererFactories.reload(context);
	}
}
