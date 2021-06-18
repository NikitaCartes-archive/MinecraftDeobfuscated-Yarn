package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private final Map<DimensionType, Map<String, BlockBox>> structureBoundingBoxes = Maps.<DimensionType, Map<String, BlockBox>>newIdentityHashMap();
	private final Map<DimensionType, Map<String, BlockBox>> structurePiecesBoundingBoxes = Maps.<DimensionType, Map<String, BlockBox>>newIdentityHashMap();
	private final Map<DimensionType, Map<String, Boolean>> field_4625 = Maps.<DimensionType, Map<String, Boolean>>newIdentityHashMap();
	private static final int RANGE = 500;

	public StructureDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Camera camera = this.client.gameRenderer.getCamera();
		WorldAccess worldAccess = this.client.world;
		DimensionType dimensionType = worldAccess.getDimension();
		BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
		if (this.structureBoundingBoxes.containsKey(dimensionType)) {
			for (BlockBox blockBox : ((Map)this.structureBoundingBoxes.get(dimensionType)).values()) {
				if (blockPos.isWithinDistance(blockBox.getCenter(), 500.0)) {
					WorldRenderer.drawBox(
						matrices,
						vertexConsumer,
						(double)blockBox.getMinX() - cameraX,
						(double)blockBox.getMinY() - cameraY,
						(double)blockBox.getMinZ() - cameraZ,
						(double)(blockBox.getMaxX() + 1) - cameraX,
						(double)(blockBox.getMaxY() + 1) - cameraY,
						(double)(blockBox.getMaxZ() + 1) - cameraZ,
						1.0F,
						1.0F,
						1.0F,
						1.0F,
						1.0F,
						1.0F,
						1.0F
					);
				}
			}
		}

		if (this.structurePiecesBoundingBoxes.containsKey(dimensionType)) {
			for (Entry<String, BlockBox> entry : ((Map)this.structurePiecesBoundingBoxes.get(dimensionType)).entrySet()) {
				String string = (String)entry.getKey();
				BlockBox blockBox2 = (BlockBox)entry.getValue();
				Boolean boolean_ = (Boolean)((Map)this.field_4625.get(dimensionType)).get(string);
				if (blockPos.isWithinDistance(blockBox2.getCenter(), 500.0)) {
					if (boolean_) {
						WorldRenderer.drawBox(
							matrices,
							vertexConsumer,
							(double)blockBox2.getMinX() - cameraX,
							(double)blockBox2.getMinY() - cameraY,
							(double)blockBox2.getMinZ() - cameraZ,
							(double)(blockBox2.getMaxX() + 1) - cameraX,
							(double)(blockBox2.getMaxY() + 1) - cameraY,
							(double)(blockBox2.getMaxZ() + 1) - cameraZ,
							0.0F,
							1.0F,
							0.0F,
							1.0F,
							0.0F,
							1.0F,
							0.0F
						);
					} else {
						WorldRenderer.drawBox(
							matrices,
							vertexConsumer,
							(double)blockBox2.getMinX() - cameraX,
							(double)blockBox2.getMinY() - cameraY,
							(double)blockBox2.getMinZ() - cameraZ,
							(double)(blockBox2.getMaxX() + 1) - cameraX,
							(double)(blockBox2.getMaxY() + 1) - cameraY,
							(double)(blockBox2.getMaxZ() + 1) - cameraZ,
							0.0F,
							0.0F,
							1.0F,
							1.0F,
							0.0F,
							0.0F,
							1.0F
						);
					}
				}
			}
		}
	}

	public void addStructure(BlockBox boundingBox, List<BlockBox> piecesBoundingBoxes, List<Boolean> list, DimensionType dimension) {
		if (!this.structureBoundingBoxes.containsKey(dimension)) {
			this.structureBoundingBoxes.put(dimension, Maps.newHashMap());
		}

		if (!this.structurePiecesBoundingBoxes.containsKey(dimension)) {
			this.structurePiecesBoundingBoxes.put(dimension, Maps.newHashMap());
			this.field_4625.put(dimension, Maps.newHashMap());
		}

		((Map)this.structureBoundingBoxes.get(dimension)).put(boundingBox.toString(), boundingBox);

		for (int i = 0; i < piecesBoundingBoxes.size(); i++) {
			BlockBox blockBox = (BlockBox)piecesBoundingBoxes.get(i);
			Boolean boolean_ = (Boolean)list.get(i);
			((Map)this.structurePiecesBoundingBoxes.get(dimension)).put(blockBox.toString(), blockBox);
			((Map)this.field_4625.get(dimension)).put(blockBox.toString(), boolean_);
		}
	}

	@Override
	public void clear() {
		this.structureBoundingBoxes.clear();
		this.structurePiecesBoundingBoxes.clear();
		this.field_4625.clear();
	}
}
