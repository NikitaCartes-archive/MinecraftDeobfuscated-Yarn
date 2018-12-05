package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements RenderDebug.DebugRenderer {
	private final MinecraftClient field_4624;
	private final Map<Integer, Map<String, MutableIntBoundingBox>> field_4626 = Maps.<Integer, Map<String, MutableIntBoundingBox>>newHashMap();
	private final Map<Integer, Map<String, MutableIntBoundingBox>> field_4627 = Maps.<Integer, Map<String, MutableIntBoundingBox>>newHashMap();
	private final Map<Integer, Map<String, Boolean>> field_4625 = Maps.<Integer, Map<String, Boolean>>newHashMap();

	public StructureDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4624 = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.field_4624.player;
		IWorld iWorld = this.field_4624.world;
		int i = iWorld.getLevelProperties().getDimension();
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.disableTexture();
		GlStateManager.disableDepthTest();
		BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(3, VertexFormats.POSITION_COLOR);
		GlStateManager.lineWidth(1.0F);
		if (this.field_4626.containsKey(i)) {
			for (MutableIntBoundingBox mutableIntBoundingBox : ((Map)this.field_4626.get(i)).values()) {
				if (blockPos.distanceTo(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ) < 500.0) {
					Renderer.method_3258(
						vertexBuffer,
						(double)mutableIntBoundingBox.minX - d,
						(double)mutableIntBoundingBox.minY - e,
						(double)mutableIntBoundingBox.minZ - g,
						(double)(mutableIntBoundingBox.maxX + 1) - d,
						(double)(mutableIntBoundingBox.maxY + 1) - e,
						(double)(mutableIntBoundingBox.maxZ + 1) - g,
						1.0F,
						1.0F,
						1.0F,
						1.0F
					);
				}
			}
		}

		if (this.field_4627.containsKey(i)) {
			for (Entry<String, MutableIntBoundingBox> entry : ((Map)this.field_4627.get(i)).entrySet()) {
				String string = (String)entry.getKey();
				MutableIntBoundingBox mutableIntBoundingBox2 = (MutableIntBoundingBox)entry.getValue();
				Boolean boolean_ = (Boolean)((Map)this.field_4625.get(i)).get(string);
				if (blockPos.distanceTo(mutableIntBoundingBox2.minX, mutableIntBoundingBox2.minY, mutableIntBoundingBox2.minZ) < 500.0) {
					if (boolean_) {
						Renderer.method_3258(
							vertexBuffer,
							(double)mutableIntBoundingBox2.minX - d,
							(double)mutableIntBoundingBox2.minY - e,
							(double)mutableIntBoundingBox2.minZ - g,
							(double)(mutableIntBoundingBox2.maxX + 1) - d,
							(double)(mutableIntBoundingBox2.maxY + 1) - e,
							(double)(mutableIntBoundingBox2.maxZ + 1) - g,
							0.0F,
							1.0F,
							0.0F,
							1.0F
						);
					} else {
						Renderer.method_3258(
							vertexBuffer,
							(double)mutableIntBoundingBox2.minX - d,
							(double)mutableIntBoundingBox2.minY - e,
							(double)mutableIntBoundingBox2.minZ - g,
							(double)(mutableIntBoundingBox2.maxX + 1) - d,
							(double)(mutableIntBoundingBox2.maxY + 1) - e,
							(double)(mutableIntBoundingBox2.maxZ + 1) - g,
							0.0F,
							0.0F,
							1.0F,
							1.0F
						);
					}
				}
			}
		}

		tessellator.draw();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	public void method_3871(MutableIntBoundingBox mutableIntBoundingBox, List<MutableIntBoundingBox> list, List<Boolean> list2, int i) {
		if (!this.field_4626.containsKey(i)) {
			this.field_4626.put(i, Maps.newHashMap());
		}

		if (!this.field_4627.containsKey(i)) {
			this.field_4627.put(i, Maps.newHashMap());
			this.field_4625.put(i, Maps.newHashMap());
		}

		((Map)this.field_4626.get(i)).put(mutableIntBoundingBox.toString(), mutableIntBoundingBox);

		for (int j = 0; j < list.size(); j++) {
			MutableIntBoundingBox mutableIntBoundingBox2 = (MutableIntBoundingBox)list.get(j);
			Boolean boolean_ = (Boolean)list2.get(j);
			((Map)this.field_4627.get(i)).put(mutableIntBoundingBox2.toString(), mutableIntBoundingBox2);
			((Map)this.field_4625.get(i)).put(mutableIntBoundingBox2.toString(), boolean_);
		}
	}
}
