package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_860;
import net.minecraft.class_866;
import net.minecraft.class_871;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RenderDebug {
	public final PathfindingDebugRenderer pathfindingDebugRenderer;
	public final RenderDebug.DebugRenderer waterDebugRenderer;
	public final RenderDebug.DebugRenderer chunkBorderDebugRenderer;
	public final RenderDebug.DebugRenderer heightmapDebugRenderer;
	public final RenderDebug.DebugRenderer voxelDebugRenderer;
	public final RenderDebug.DebugRenderer neighborUpdateDebugRenderer;
	public final CaveDebugRenderer caveDebugRenderer;
	public final StructureDebugRenderer structureDebugRenderer;
	public final RenderDebug.DebugRenderer field_4536;
	public final RenderDebug.DebugRenderer worldGenAttemptDebugRenderer;
	public final RenderDebug.DebugRenderer field_4517;
	public final RenderDebug.DebugRenderer field_4533;
	private boolean showChunkBorder;
	private boolean showPathfinding;
	private boolean showWater;
	private boolean showHeightmap;
	private boolean showVoxels;
	private boolean showNeighborUpdates;
	private boolean showCaves;
	private boolean showStructures;
	private boolean field_4520;
	private boolean showWorldGenAttempts;
	private boolean field_4518;

	public RenderDebug(MinecraftClient minecraftClient) {
		this.pathfindingDebugRenderer = new PathfindingDebugRenderer(minecraftClient);
		this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
		this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
		this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
		this.voxelDebugRenderer = new VoxelDebugRenderer(minecraftClient);
		this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
		this.caveDebugRenderer = new CaveDebugRenderer(minecraftClient);
		this.structureDebugRenderer = new StructureDebugRenderer(minecraftClient);
		this.field_4536 = new class_866(minecraftClient);
		this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer(minecraftClient);
		this.field_4517 = new class_871(minecraftClient);
		this.field_4533 = new class_860(minecraftClient);
	}

	public boolean method_3710() {
		return this.showChunkBorder
			|| this.showPathfinding
			|| this.showWater
			|| this.showHeightmap
			|| this.showVoxels
			|| this.showNeighborUpdates
			|| this.field_4520
			|| this.showWorldGenAttempts
			|| this.field_4518;
	}

	public boolean toggleShowChunkBorder() {
		this.showChunkBorder = !this.showChunkBorder;
		return this.showChunkBorder;
	}

	public void renderDebuggers(float f, long l) {
		if (this.showPathfinding) {
			this.pathfindingDebugRenderer.render(f, l);
		}

		if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
			this.chunkBorderDebugRenderer.render(f, l);
		}

		if (this.showWater) {
			this.waterDebugRenderer.render(f, l);
		}

		if (this.showHeightmap) {
			this.heightmapDebugRenderer.render(f, l);
		}

		if (this.showVoxels) {
			this.voxelDebugRenderer.render(f, l);
		}

		if (this.showNeighborUpdates) {
			this.neighborUpdateDebugRenderer.render(f, l);
		}

		if (this.showCaves) {
			this.caveDebugRenderer.render(f, l);
		}

		if (this.showStructures) {
			this.structureDebugRenderer.render(f, l);
		}

		if (this.field_4520) {
			this.field_4536.render(f, l);
		}

		if (this.showWorldGenAttempts) {
			this.worldGenAttemptDebugRenderer.render(f, l);
		}

		if (this.field_4518) {
			this.field_4517.render(f, l);
		}
	}

	public static void method_3711(String string, int i, int j, int k, float f, int l) {
		method_3714(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, f, l);
	}

	public static void method_3714(String string, double d, double e, double f, float g, int i) {
		method_3712(string, d, e, f, g, i, 0.02F);
	}

	public static void method_3712(String string, double d, double e, double f, float g, int i, float h) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.player != null && minecraftClient.getEntityRenderManager() != null && minecraftClient.getEntityRenderManager().settings != null) {
			FontRenderer fontRenderer = minecraftClient.fontRenderer;
			PlayerEntity playerEntity = minecraftClient.player;
			double j = MathHelper.lerp((double)g, playerEntity.prevRenderX, playerEntity.x);
			double k = MathHelper.lerp((double)g, playerEntity.prevRenderY, playerEntity.y);
			double l = MathHelper.lerp((double)g, playerEntity.prevRenderZ, playerEntity.z);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(d - j), (float)(e - k) + 0.07F, (float)(f - l));
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(h, -h, h);
			EntityRenderDispatcher entityRenderDispatcher = minecraftClient.getEntityRenderManager();
			GlStateManager.rotatef(-entityRenderDispatcher.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(entityRenderDispatcher.settings.field_1850 == 2 ? 1 : -1) * entityRenderDispatcher.field_4677, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableDepthTest();
			GlStateManager.depthMask(true);
			GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
			fontRenderer.draw(string, (float)(-fontRenderer.getStringWidth(string) / 2), 0.0F, i);
			GlStateManager.enableLighting();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface DebugRenderer {
		void render(float f, long l);
	}
}
