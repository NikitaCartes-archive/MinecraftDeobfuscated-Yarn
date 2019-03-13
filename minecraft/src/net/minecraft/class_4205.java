package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class class_4205 implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_18780;
	private final Map<String, List<class_4205.class_4206>> field_18781 = Maps.<String, List<class_4205.class_4206>>newHashMap();

	public void method_19430(int i, String string, BlockPos blockPos, List<class_4205.class_4206> list) {
		this.field_18781.put(string, list);
	}

	public class_4205(MinecraftClient minecraftClient) {
		this.field_18780 = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.field_18780.field_1773.method_19418();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(lv.method_19326().x, 0.0, lv.method_19326().z);
		this.field_18781.forEach((string, list) -> {
			for (int i = 0; i < list.size(); i++) {
				class_4205.class_4206 lvx = (class_4205.class_4206)list.get(i);
				if (blockPos.distanceTo(lvx.field_18782) < 160.0) {
					double d = (double)lvx.field_18782.getX() + 0.5;
					double e = (double)lvx.field_18782.getY() + 2.0 + (double)i * 0.25;
					double f = (double)lvx.field_18782.getZ() + 0.5;
					int j = lvx.field_18785 ? -16711936 : -3355444;
					DebugRenderer.method_3714(lvx.field_18784, d, e, f, j);
				}
			}
		});
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	@Environment(EnvType.CLIENT)
	public static class class_4206 {
		public final BlockPos field_18782;
		public final int field_18783;
		public final String field_18784;
		public final boolean field_18785;

		public class_4206(BlockPos blockPos, int i, String string, boolean bl) {
			this.field_18782 = blockPos;
			this.field_18783 = i;
			this.field_18784 = string;
			this.field_18785 = bl;
		}
	}
}
