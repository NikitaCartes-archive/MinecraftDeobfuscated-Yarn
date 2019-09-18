/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.render.debug.CaveDebugRenderer;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.VoxelDebugRenderer;
import net.minecraft.client.render.debug.WaterDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;

@Environment(value=EnvType.CLIENT)
public class DebugRenderer {
    public final PathfindingDebugRenderer pathfindingDebugRenderer;
    public final Renderer waterDebugRenderer;
    public final Renderer chunkBorderDebugRenderer;
    public final Renderer heightmapDebugRenderer;
    public final Renderer voxelDebugRenderer;
    public final Renderer neighborUpdateDebugRenderer;
    public final CaveDebugRenderer caveDebugRenderer;
    public final StructureDebugRenderer structureDebugRenderer;
    public final Renderer skyLightDebugRenderer;
    public final Renderer worldGenAttemptDebugRenderer;
    public final Renderer blockOutlineDebugRenderer;
    public final Renderer chunkLoadingDebugRenderer;
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

    @Environment(value=EnvType.CLIENT)
    public static interface Renderer {
        default public void clear() {
        }
    }
}

