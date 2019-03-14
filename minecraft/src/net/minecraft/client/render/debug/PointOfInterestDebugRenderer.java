package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;

@Environment(EnvType.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_18786;
	private final Map<BlockPos, PointOfInterestType> pointsOfInterest = Maps.<BlockPos, PointOfInterestType>newHashMap();
	private final Set<ChunkSectionPos> field_18788 = Sets.<ChunkSectionPos>newHashSet();

	public void addPointOfInterest(BlockPos blockPos, Identifier identifier) {
		this.pointsOfInterest.put(blockPos, Registry.POINT_OF_INTEREST_TYPE.get(identifier));
	}

	public void removePointOfInterest(BlockPos blockPos, Identifier identifier) {
		this.pointsOfInterest.remove(blockPos);
	}

	public void method_19433(ChunkSectionPos chunkSectionPos) {
		this.field_18788.add(chunkSectionPos);
	}

	public void method_19435(ChunkSectionPos chunkSectionPos) {
		this.field_18788.remove(chunkSectionPos);
	}

	public PointOfInterestDebugRenderer(MinecraftClient minecraftClient) {
		this.field_18786 = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.field_18786.gameRenderer.method_19418();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
			if (blockPos.distanceTo(blockPos2) < 160.0) {
				WorldRenderer.buildBox(
					bufferBuilder,
					(double)blockPos2.getX() - d,
					(double)blockPos2.getY() - e,
					(double)blockPos2.getZ() - f,
					(double)((float)blockPos2.getX() + 1.0F) - d,
					(double)((float)blockPos2.getY() + 1.0F) - e,
					(double)((float)blockPos2.getZ() + 1.0F) - f,
					0.2F,
					0.2F,
					1.0F,
					0.5F
				);
			}
		}

		for (ChunkSectionPos chunkSectionPos : this.field_18788) {
			int i = ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkX()) + 8;
			int j = ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkY()) + 8;
			int k = ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkZ()) + 8;
			if (blockPos.distanceTo(i, j, k) < 160.0) {
				WorldRenderer.buildBox(
					bufferBuilder,
					(double)(i - 2) - d,
					(double)(j - 2) - e,
					(double)(k - 2) - f,
					(double)(i + 2) - d,
					(double)(j + 2) - e,
					(double)(k + 2) - f,
					0.2F,
					1.0F,
					0.2F,
					0.2F
				);
			}
		}

		tessellator.draw();

		for (BlockPos blockPos2x : this.pointsOfInterest.keySet()) {
			if (blockPos.distanceTo(blockPos2x) < 160.0) {
				DebugRenderer.method_3714(
					((PointOfInterestType)this.pointsOfInterest.get(blockPos2x)).register(),
					(double)blockPos2x.getX() + 0.5,
					(double)blockPos2x.getY() + 1.5,
					(double)blockPos2x.getZ() + 0.5,
					-1
				);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
