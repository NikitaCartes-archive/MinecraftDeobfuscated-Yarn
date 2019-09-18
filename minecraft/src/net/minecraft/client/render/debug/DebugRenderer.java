package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

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
	public final DebugRenderer.Renderer blockOutlineDebugRenderer;
	public final DebugRenderer.Renderer chunkLoadingDebugRenderer;
	public final PointOfInterestDebugRenderer pointsOfInterestDebugRenderer;
	public final RaidCenterDebugRenderer raidCenterDebugRenderer;
	public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
	public final GameTestDebugRenderer gameTestDebugRenderer;
	private boolean showChunkBorder;

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
		this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(minecraftClient);
		this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(minecraftClient);
		this.pointsOfInterestDebugRenderer = new PointOfInterestDebugRenderer(minecraftClient);
		this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(minecraftClient);
		this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(minecraftClient);
		this.gameTestDebugRenderer = new GameTestDebugRenderer();
	}

	public void reset() {
		this.pathfindingDebugRenderer.clear();
		this.waterDebugRenderer.clear();
		this.chunkBorderDebugRenderer.clear();
		this.heightmapDebugRenderer.clear();
		this.voxelDebugRenderer.clear();
		this.neighborUpdateDebugRenderer.clear();
		this.caveDebugRenderer.clear();
		this.structureDebugRenderer.clear();
		this.skyLightDebugRenderer.clear();
		this.worldGenAttemptDebugRenderer.clear();
		this.blockOutlineDebugRenderer.clear();
		this.chunkLoadingDebugRenderer.clear();
		this.pointsOfInterestDebugRenderer.clear();
		this.raidCenterDebugRenderer.clear();
		this.goalSelectorDebugRenderer.clear();
		this.gameTestDebugRenderer.clear();
	}

	public boolean toggleShowChunkBorder() {
		this.showChunkBorder = !this.showChunkBorder;
		return this.showChunkBorder;
	}

	@Environment(EnvType.CLIENT)
	public interface Renderer {
		default void clear() {
		}
	}
}
