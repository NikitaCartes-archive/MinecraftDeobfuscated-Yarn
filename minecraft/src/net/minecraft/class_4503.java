package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class class_4503 implements DebugRenderer.Renderer {
	private final Map<BlockPos, class_4503.class_4504> field_20520 = Maps.<BlockPos, class_4503.class_4504>newHashMap();

	public void method_22123(BlockPos blockPos, int i, String string, int j) {
		this.field_20520.put(blockPos, new class_4503.class_4504(i, string, SystemUtil.getMeasuringTimeMs() + (long)j));
	}

	@Override
	public void method_20414() {
		this.field_20520.clear();
	}

	@Override
	public void render(long l) {
		long m = SystemUtil.getMeasuringTimeMs();
		this.field_20520.entrySet().removeIf(entry -> m > ((class_4503.class_4504)entry.getValue()).field_20523);
		this.field_20520.forEach(this::method_22124);
	}

	private void method_22124(BlockPos blockPos, class_4503.class_4504 arg) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ONE, class_4493.class_4534.ZERO
		);
		RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		RenderSystem.disableTexture();
		DebugRenderer.method_19696(blockPos, 0.02F, arg.method_22125(), arg.method_22126(), arg.method_22127(), arg.method_22128());
		if (!arg.field_20522.isEmpty()) {
			double d = (double)blockPos.getX() + 0.5;
			double e = (double)blockPos.getY() + 1.2;
			double f = (double)blockPos.getZ() + 0.5;
			DebugRenderer.method_3712(arg.field_20522, d, e, f, -1, 0.01F, true, 0.0F, true);
		}

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	@Environment(EnvType.CLIENT)
	static class class_4504 {
		public int field_20521;
		public String field_20522;
		public long field_20523;

		public class_4504(int i, String string, long l) {
			this.field_20521 = i;
			this.field_20522 = string;
			this.field_20523 = l;
		}

		public float method_22125() {
			return (float)(this.field_20521 >> 16 & 0xFF) / 255.0F;
		}

		public float method_22126() {
			return (float)(this.field_20521 >> 8 & 0xFF) / 255.0F;
		}

		public float method_22127() {
			return (float)(this.field_20521 & 0xFF) / 255.0F;
		}

		public float method_22128() {
			return (float)(this.field_20521 >> 24 & 0xFF) / 255.0F;
		}
	}
}
