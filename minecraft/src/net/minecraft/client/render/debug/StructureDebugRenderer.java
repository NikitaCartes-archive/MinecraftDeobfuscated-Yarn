package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4624;
	private final Map<Integer, Map<String, MutableIntBoundingBox>> field_4626 = Maps.<Integer, Map<String, MutableIntBoundingBox>>newHashMap();
	private final Map<Integer, Map<String, MutableIntBoundingBox>> field_4627 = Maps.<Integer, Map<String, MutableIntBoundingBox>>newHashMap();
	private final Map<Integer, Map<String, Boolean>> field_4625 = Maps.<Integer, Map<String, Boolean>>newHashMap();

	public StructureDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4624 = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.field_4624.field_1773.method_19418();
		IWorld iWorld = this.field_4624.field_1687;
		int i = iWorld.method_8401().getDimension();
		double d = lv.method_19326().x;
		double e = lv.method_19326().y;
		double f = lv.method_19326().z;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		GlStateManager.disableDepthTest();
		BlockPos blockPos = new BlockPos(lv.method_19326().x, 0.0, lv.method_19326().z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(3, VertexFormats.field_1576);
		GlStateManager.lineWidth(1.0F);
		if (this.field_4626.containsKey(i)) {
			for (MutableIntBoundingBox mutableIntBoundingBox : ((Map)this.field_4626.get(i)).values()) {
				if (blockPos.distanceTo(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ) < 500.0) {
					WorldRenderer.buildBoxOutline(
						bufferBuilder,
						(double)mutableIntBoundingBox.minX - d,
						(double)mutableIntBoundingBox.minY - e,
						(double)mutableIntBoundingBox.minZ - f,
						(double)(mutableIntBoundingBox.maxX + 1) - d,
						(double)(mutableIntBoundingBox.maxY + 1) - e,
						(double)(mutableIntBoundingBox.maxZ + 1) - f,
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
						WorldRenderer.buildBoxOutline(
							bufferBuilder,
							(double)mutableIntBoundingBox2.minX - d,
							(double)mutableIntBoundingBox2.minY - e,
							(double)mutableIntBoundingBox2.minZ - f,
							(double)(mutableIntBoundingBox2.maxX + 1) - d,
							(double)(mutableIntBoundingBox2.maxY + 1) - e,
							(double)(mutableIntBoundingBox2.maxZ + 1) - f,
							0.0F,
							1.0F,
							0.0F,
							1.0F
						);
					} else {
						WorldRenderer.buildBoxOutline(
							bufferBuilder,
							(double)mutableIntBoundingBox2.minX - d,
							(double)mutableIntBoundingBox2.minY - e,
							(double)mutableIntBoundingBox2.minZ - f,
							(double)(mutableIntBoundingBox2.maxX + 1) - d,
							(double)(mutableIntBoundingBox2.maxY + 1) - e,
							(double)(mutableIntBoundingBox2.maxZ + 1) - f,
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
