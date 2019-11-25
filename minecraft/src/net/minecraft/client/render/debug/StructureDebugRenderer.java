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
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4624;
	private final Map<DimensionType, Map<String, BlockBox>> field_4626 = Maps.<DimensionType, Map<String, BlockBox>>newIdentityHashMap();
	private final Map<DimensionType, Map<String, BlockBox>> field_4627 = Maps.<DimensionType, Map<String, BlockBox>>newIdentityHashMap();
	private final Map<DimensionType, Map<String, Boolean>> field_4625 = Maps.<DimensionType, Map<String, Boolean>>newIdentityHashMap();

	public StructureDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4624 = minecraftClient;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Camera camera = this.field_4624.gameRenderer.getCamera();
		IWorld iWorld = this.field_4624.world;
		DimensionType dimensionType = iWorld.getDimension().getType();
		BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
		if (this.field_4626.containsKey(dimensionType)) {
			for (BlockBox blockBox : ((Map)this.field_4626.get(dimensionType)).values()) {
				if (blockPos.isWithinDistance(blockBox.method_22874(), 500.0)) {
					WorldRenderer.drawBox(
						vertexConsumer,
						(double)blockBox.minX - cameraX,
						(double)blockBox.minY - cameraY,
						(double)blockBox.minZ - cameraZ,
						(double)(blockBox.maxX + 1) - cameraX,
						(double)(blockBox.maxY + 1) - cameraY,
						(double)(blockBox.maxZ + 1) - cameraZ,
						1.0F,
						1.0F,
						1.0F,
						1.0F
					);
				}
			}
		}

		if (this.field_4627.containsKey(dimensionType)) {
			for (Entry<String, BlockBox> entry : ((Map)this.field_4627.get(dimensionType)).entrySet()) {
				String string = (String)entry.getKey();
				BlockBox blockBox2 = (BlockBox)entry.getValue();
				Boolean boolean_ = (Boolean)((Map)this.field_4625.get(dimensionType)).get(string);
				if (blockPos.isWithinDistance(blockBox2.method_22874(), 500.0)) {
					if (boolean_) {
						WorldRenderer.drawBox(
							vertexConsumer,
							(double)blockBox2.minX - cameraX,
							(double)blockBox2.minY - cameraY,
							(double)blockBox2.minZ - cameraZ,
							(double)(blockBox2.maxX + 1) - cameraX,
							(double)(blockBox2.maxY + 1) - cameraY,
							(double)(blockBox2.maxZ + 1) - cameraZ,
							0.0F,
							1.0F,
							0.0F,
							1.0F
						);
					} else {
						WorldRenderer.drawBox(
							vertexConsumer,
							(double)blockBox2.minX - cameraX,
							(double)blockBox2.minY - cameraY,
							(double)blockBox2.minZ - cameraZ,
							(double)(blockBox2.maxX + 1) - cameraX,
							(double)(blockBox2.maxY + 1) - cameraY,
							(double)(blockBox2.maxZ + 1) - cameraZ,
							0.0F,
							0.0F,
							1.0F,
							1.0F
						);
					}
				}
			}
		}
	}

	public void method_3871(BlockBox blockBox, List<BlockBox> list, List<Boolean> list2, DimensionType dimensionType) {
		if (!this.field_4626.containsKey(dimensionType)) {
			this.field_4626.put(dimensionType, Maps.newHashMap());
		}

		if (!this.field_4627.containsKey(dimensionType)) {
			this.field_4627.put(dimensionType, Maps.newHashMap());
			this.field_4625.put(dimensionType, Maps.newHashMap());
		}

		((Map)this.field_4626.get(dimensionType)).put(blockBox.toString(), blockBox);

		for (int i = 0; i < list.size(); i++) {
			BlockBox blockBox2 = (BlockBox)list.get(i);
			Boolean boolean_ = (Boolean)list2.get(i);
			((Map)this.field_4627.get(dimensionType)).put(blockBox2.toString(), blockBox2);
			((Map)this.field_4625.get(dimensionType)).put(blockBox2.toString(), boolean_);
		}
	}

	@Override
	public void clear() {
		this.field_4626.clear();
		this.field_4627.clear();
		this.field_4625.clear();
	}
}
