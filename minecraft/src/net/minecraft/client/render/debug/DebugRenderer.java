package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_860;
import net.minecraft.class_871;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

@Environment(EnvType.CLIENT)
public class DebugRenderer {
	public final PathfindingDebugRenderer pathfindingDebugRenderer;
	public final DebugRenderer.Renderer waterDebugRenderer;
	public final DebugRenderer.Renderer chunkBorderDebugRenderer;
	public final DebugRenderer.Renderer heightmapDebugRenderer;
	public final DebugRenderer.Renderer voxelDebugRenderer;
	public final DebugRenderer.Renderer neighborUpdateDebugRenderer;
	public final CaveDebugRenderer caveDebugRenderer;
	public final StructureDebugRenderer structureDebugRenderer;
	public final DebugRenderer.Renderer skyLightDebugRenderer;
	public final DebugRenderer.Renderer worldGenAttemptDebugRenderer;
	public final DebugRenderer.Renderer field_4517;
	public final DebugRenderer.Renderer field_4533;
	public final PointOfInterestDebugRenderer pointsOfInterestDebugRenderer;
	public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
	public final BrainDebugRenderer brainDebugRenderer;
	private boolean showChunkBorder;
	private boolean showPathfinding;
	private boolean showWater;
	private boolean showHeightmap;
	private boolean showVoxels;
	private boolean showNeighborUpdates;
	private boolean showCaves;
	private boolean showStructures;
	private boolean showSkyLight;
	private boolean showWorldGenAttempts;
	private boolean field_4518;
	private boolean field_18775;
	private boolean field_18776;

	public DebugRenderer(MinecraftClient minecraftClient) {
		this.pathfindingDebugRenderer = new PathfindingDebugRenderer(minecraftClient);
		this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
		this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
		this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
		this.voxelDebugRenderer = new VoxelDebugRenderer(minecraftClient);
		this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
		this.caveDebugRenderer = new CaveDebugRenderer(minecraftClient);
		this.structureDebugRenderer = new StructureDebugRenderer(minecraftClient);
		this.skyLightDebugRenderer = new SkyLightDebugRenderer(minecraftClient);
		this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer(minecraftClient);
		this.field_4517 = new class_871(minecraftClient);
		this.field_4533 = new class_860(minecraftClient);
		this.pointsOfInterestDebugRenderer = new PointOfInterestDebugRenderer(minecraftClient);
		this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(minecraftClient);
		this.brainDebugRenderer = new BrainDebugRenderer(minecraftClient);
	}

	public boolean shouldRender() {
		return this.showChunkBorder
			|| this.showPathfinding
			|| this.showWater
			|| this.showHeightmap
			|| this.showVoxels
			|| this.showNeighborUpdates
			|| this.showSkyLight
			|| this.showWorldGenAttempts
			|| this.field_4518
			|| this.field_18775
			|| this.field_18776;
	}

	public boolean toggleShowChunkBorder() {
		this.showChunkBorder = !this.showChunkBorder;
		return this.showChunkBorder;
	}

	public void renderDebuggers(long l) {
		if (this.showPathfinding) {
			this.pathfindingDebugRenderer.render(l);
		}

		if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
			this.chunkBorderDebugRenderer.render(l);
		}

		if (this.showWater) {
			this.waterDebugRenderer.render(l);
		}

		if (this.showHeightmap) {
			this.heightmapDebugRenderer.render(l);
		}

		if (this.showVoxels) {
			this.voxelDebugRenderer.render(l);
		}

		if (this.showNeighborUpdates) {
			this.neighborUpdateDebugRenderer.render(l);
		}

		if (this.showCaves) {
			this.caveDebugRenderer.render(l);
		}

		if (this.showStructures) {
			this.structureDebugRenderer.render(l);
		}

		if (this.showSkyLight) {
			this.skyLightDebugRenderer.render(l);
		}

		if (this.showWorldGenAttempts) {
			this.worldGenAttemptDebugRenderer.render(l);
		}

		if (this.field_4518) {
			this.field_4517.render(l);
		}

		if (this.field_18775) {
			this.pointsOfInterestDebugRenderer.render(l);
		}

		if (this.field_18776) {
			this.goalSelectorDebugRenderer.render(l);
		}

		if (this.field_18776) {
			this.brainDebugRenderer.render(l);
		}
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
		Camera camera = minecraftClient.gameRenderer.method_19418();
		if (camera.isReady() && minecraftClient.getEntityRenderManager() != null && minecraftClient.getEntityRenderManager().settings != null) {
			TextRenderer textRenderer = minecraftClient.textRenderer;
			double j = camera.getPos().x;
			double k = camera.getPos().y;
			double l = camera.getPos().z;
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(d - j), (float)(e - k) + 0.07F, (float)(f - l));
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(g, -g, g);
			EntityRenderDispatcher entityRenderDispatcher = minecraftClient.getEntityRenderManager();
			GlStateManager.rotatef(-entityRenderDispatcher.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-entityRenderDispatcher.field_4677, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
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
			GlStateManager.enableLighting();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.enableDepthTest();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Renderer {
		void render(long l);
	}
}
