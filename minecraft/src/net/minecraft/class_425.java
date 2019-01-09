package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_425 extends class_437 {
	private static final Logger field_2485 = LogManager.getLogger();
	private static final class_2960 field_2483 = new class_2960("textures/gui/title/mojang.png");
	private class_2960 field_2484;

	@Override
	protected void method_2224() {
		try {
			InputStream inputStream = this.field_2563.method_1516().method_4633().method_14405(class_3264.field_14188, field_2483);
			this.field_2484 = this.field_2563.method_1531().method_4617("logo", new class_1043(class_1011.method_4309(inputStream)));
		} catch (IOException var2) {
			field_2485.error("Unable to load logo: {}", field_2483, var2);
		}
	}

	@Override
	public void method_2234() {
		this.field_2563.method_1531().method_4615(this.field_2484);
		this.field_2484 = null;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		class_276 lv = new class_276(this.field_2561, this.field_2559, true, class_310.field_1703);
		lv.method_1235(false);
		this.field_2563.method_1531().method_4618(this.field_2484);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		lv3.method_1328(7, class_290.field_1575);
		lv3.method_1315(0.0, (double)this.field_2563.field_1704.method_4506(), 0.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, 255).method_1344();
		lv3.method_1315((double)this.field_2563.field_1704.method_4489(), (double)this.field_2563.field_1704.method_4506(), 0.0)
			.method_1312(0.0, 0.0)
			.method_1323(255, 255, 255, 255)
			.method_1344();
		lv3.method_1315((double)this.field_2563.field_1704.method_4489(), 0.0, 0.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, 255).method_1344();
		lv3.method_1315(0.0, 0.0, 0.0).method_1312(0.0, 0.0).method_1323(255, 255, 255, 255).method_1344();
		lv2.method_1350();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = 256;
		int l = 256;
		this.field_2563
			.method_1501((this.field_2563.field_1704.method_4486() - 256) / 2, (this.field_2563.field_1704.method_4502() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		lv.method_1240();
		lv.method_1237(this.field_2563.field_1704.method_4489(), this.field_2563.field_1704.method_4506());
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
	}
}
