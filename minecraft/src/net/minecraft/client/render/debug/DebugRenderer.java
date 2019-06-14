package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DebugRenderer {
	public final PathfindingDebugRenderer field_4523;
	public final DebugRenderer.Renderer waterDebugRenderer;
	public final DebugRenderer.Renderer chunkBorderDebugRenderer;
	public final DebugRenderer.Renderer heightmapDebugRenderer;
	public final DebugRenderer.Renderer voxelDebugRenderer;
	public final DebugRenderer.Renderer neighborUpdateDebugRenderer;
	public final CaveDebugRenderer caveDebugRenderer;
	public final StructureDebugRenderer field_4539;
	public final DebugRenderer.Renderer skyLightDebugRenderer;
	public final DebugRenderer.Renderer worldGenAttemptDebugRenderer;
	public final DebugRenderer.Renderer blockOutlineDebugRenderer;
	public final DebugRenderer.Renderer chunkLoadingDebugRenderer;
	public final PointOfInterestDebugRenderer field_18777;
	public final RaidCenterDebugRenderer field_19325;
	public final GoalSelectorDebugRenderer field_18778;
	private boolean showChunkBorder;

	public DebugRenderer(MinecraftClient minecraftClient) {
		this.field_4523 = new PathfindingDebugRenderer(minecraftClient);
		this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
		this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
		this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
		this.voxelDebugRenderer = new VoxelDebugRenderer(minecraftClient);
		this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
		this.caveDebugRenderer = new CaveDebugRenderer(minecraftClient);
		this.field_4539 = new StructureDebugRenderer(minecraftClient);
		this.skyLightDebugRenderer = new SkyLightDebugRenderer(minecraftClient);
		this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer(minecraftClient);
		this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(minecraftClient);
		this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(minecraftClient);
		this.field_18777 = new PointOfInterestDebugRenderer(minecraftClient);
		this.field_19325 = new RaidCenterDebugRenderer(minecraftClient);
		this.field_18778 = new GoalSelectorDebugRenderer(minecraftClient);
	}

	public void method_20413() {
		this.field_4523.method_20414();
		this.waterDebugRenderer.method_20414();
		this.chunkBorderDebugRenderer.method_20414();
		this.heightmapDebugRenderer.method_20414();
		this.voxelDebugRenderer.method_20414();
		this.neighborUpdateDebugRenderer.method_20414();
		this.caveDebugRenderer.method_20414();
		this.field_4539.method_20414();
		this.skyLightDebugRenderer.method_20414();
		this.worldGenAttemptDebugRenderer.method_20414();
		this.blockOutlineDebugRenderer.method_20414();
		this.chunkLoadingDebugRenderer.method_20414();
		this.field_18777.method_20414();
		this.field_19325.method_20414();
		this.field_18778.method_20414();
	}

	public boolean shouldRender() {
		return this.showChunkBorder;
	}

	public boolean toggleShowChunkBorder() {
		this.showChunkBorder = !this.showChunkBorder;
		return this.showChunkBorder;
	}

	public void renderDebuggers(long l) {
		if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
			this.chunkBorderDebugRenderer.render(l);
		}
	}

	public static Optional<Entity> method_19694(@Nullable Entity entity, int i) {
		if (entity == null) {
			return Optional.empty();
		} else {
			Vec3d vec3d = entity.method_5836(1.0F);
			Vec3d vec3d2 = entity.method_5828(1.0F).multiply((double)i);
			Vec3d vec3d3 = vec3d.add(vec3d2);
			Box box = entity.method_5829().method_18804(vec3d2).expand(1.0);
			int j = i * i;
			Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.collides();
			EntityHitResult entityHitResult = ProjectileUtil.method_18075(entity, vec3d, vec3d3, box, predicate, (double)j);
			if (entityHitResult == null) {
				return Optional.empty();
			} else {
				return vec3d.squaredDistanceTo(entityHitResult.method_17784()) > (double)j ? Optional.empty() : Optional.of(entityHitResult.getEntity());
			}
		}
	}

	public static void method_19697(BlockPos blockPos, BlockPos blockPos2, float f, float g, float h, float i) {
		Camera camera = MinecraftClient.getInstance().field_1773.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			Box box = new Box(blockPos, blockPos2).method_997(vec3d);
			method_19695(box, f, g, h, i);
		}
	}

	public static void method_19696(BlockPos blockPos, float f, float g, float h, float i, float j) {
		Camera camera = MinecraftClient.getInstance().field_1773.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			Box box = new Box(blockPos).method_997(vec3d).expand((double)f);
			method_19695(box, g, h, i, j);
		}
	}

	public static void method_19695(Box box, float f, float g, float h, float i) {
		method_19692(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, f, g, h, i);
	}

	public static void method_19692(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(5, VertexFormats.field_1576);
		WorldRenderer.buildBox(bufferBuilder, d, e, f, g, h, i, j, k, l, m);
		tessellator.draw();
	}

	public static void method_3711(String string, int i, int j, int k, int l) {
		method_3714(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, l);
	}

	public static void method_3714(String string, double d, double e, double f, int i) {
		method_19429(string, d, e, f, i, 0.02F);
	}

	public static void method_19429(String string, double d, double e, double f, int i, float g) {
		method_3712(string, d, e, f, i, g, true, 0.0F, false);
	}

	public static void method_3712(String string, double d, double e, double f, int i, float g, boolean bl, float h, boolean bl2) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Camera camera = minecraftClient.field_1773.getCamera();
		if (camera.isReady() && minecraftClient.method_1561().gameOptions != null) {
			TextRenderer textRenderer = minecraftClient.field_1772;
			double j = camera.getPos().x;
			double k = camera.getPos().y;
			double l = camera.getPos().z;
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(d - j), (float)(e - k) + 0.07F, (float)(f - l));
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(g, -g, g);
			EntityRenderDispatcher entityRenderDispatcher = minecraftClient.method_1561();
			GlStateManager.rotatef(-entityRenderDispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-entityRenderDispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.enableTexture();
			if (bl2) {
				GlStateManager.disableDepthTest();
			} else {
				GlStateManager.enableDepthTest();
			}

			GlStateManager.depthMask(true);
			GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
			float m = bl ? (float)(-textRenderer.getStringWidth(string)) / 2.0F : 0.0F;
			m -= h / g;
			textRenderer.draw(string, m, 0.0F, i);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableDepthTest();
			GlStateManager.popMatrix();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Renderer {
		void render(long l);

		default void method_20414() {
		}
	}
}
