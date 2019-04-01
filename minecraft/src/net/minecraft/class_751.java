package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_751 {
	private final class_2960[] field_3952 = new class_2960[6];

	public class_751(class_2960 arg) {
		for (int i = 0; i < 6; i++) {
			this.field_3952[i] = new class_2960(arg.method_12836(), arg.method_12832() + '_' + i + ".png");
		}
	}

	public void method_3156(class_310 arg, float f, float g, float h) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.multMatrix(class_1159.method_4929(85.0, (float)arg.field_1704.method_4489() / (float)arg.field_1704.method_4506(), 0.05F, 10.0F));
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		int i = 2;

		for (int j = 0; j < 4; j++) {
			GlStateManager.pushMatrix();
			float k = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
			float l = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
			float m = 0.0F;
			GlStateManager.translatef(k, l, 0.0F);
			GlStateManager.rotatef(f, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);

			for (int n = 0; n < 6; n++) {
				arg.method_1531().method_4618(this.field_3952[n]);
				lv2.method_1328(7, class_290.field_1575);
				int o = Math.round(255.0F * h) / (j + 1);
				if (n == 0) {
					lv2.method_1315(-1.0, -1.0, 1.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, 1.0, 1.0).method_1312(0.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, 1.0, 1.0).method_1312(1.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, -1.0, 1.0).method_1312(1.0, 0.0).method_1323(255, 255, 255, o).method_1344();
				}

				if (n == 1) {
					lv2.method_1315(1.0, -1.0, 1.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, 1.0, 1.0).method_1312(0.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, 1.0, -1.0).method_1312(1.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, -1.0, -1.0).method_1312(1.0, 0.0).method_1323(255, 255, 255, o).method_1344();
				}

				if (n == 2) {
					lv2.method_1315(1.0, -1.0, -1.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, 1.0, -1.0).method_1312(0.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, 1.0, -1.0).method_1312(1.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, -1.0, -1.0).method_1312(1.0, 0.0).method_1323(255, 255, 255, o).method_1344();
				}

				if (n == 3) {
					lv2.method_1315(-1.0, -1.0, -1.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, 1.0, -1.0).method_1312(0.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, 1.0, 1.0).method_1312(1.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, -1.0, 1.0).method_1312(1.0, 0.0).method_1323(255, 255, 255, o).method_1344();
				}

				if (n == 4) {
					lv2.method_1315(-1.0, -1.0, -1.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, -1.0, 1.0).method_1312(0.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, -1.0, 1.0).method_1312(1.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, -1.0, -1.0).method_1312(1.0, 0.0).method_1323(255, 255, 255, o).method_1344();
				}

				if (n == 5) {
					lv2.method_1315(-1.0, 1.0, 1.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(-1.0, 1.0, -1.0).method_1312(0.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, 1.0, -1.0).method_1312(1.0, 1.0).method_1323(255, 255, 255, o).method_1344();
					lv2.method_1315(1.0, 1.0, 1.0).method_1312(1.0, 0.0).method_1323(255, 255, 255, o).method_1344();
				}

				lv.method_1350();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		lv2.method_1331(0.0, 0.0, 0.0);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepthTest();
	}

	public CompletableFuture<Void> method_18143(class_1060 arg, Executor executor) {
		CompletableFuture<?>[] completableFutures = new CompletableFuture[6];

		for (int i = 0; i < completableFutures.length; i++) {
			completableFutures[i] = arg.method_18168(this.field_3952[i], executor);
		}

		return CompletableFuture.allOf(completableFutures);
	}
}
