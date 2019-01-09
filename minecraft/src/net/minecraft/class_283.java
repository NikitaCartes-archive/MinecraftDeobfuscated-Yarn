package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_283 implements AutoCloseable {
	private final class_280 field_1540;
	public final class_276 field_1536;
	public final class_276 field_1538;
	private final List<Object> field_1534 = Lists.<Object>newArrayList();
	private final List<String> field_1539 = Lists.<String>newArrayList();
	private final List<Integer> field_1533 = Lists.<Integer>newArrayList();
	private final List<Integer> field_1537 = Lists.<Integer>newArrayList();
	private class_1159 field_1535;

	public class_283(class_3300 arg, String string, class_276 arg2, class_276 arg3) throws IOException {
		this.field_1540 = new class_280(arg, string);
		this.field_1536 = arg2;
		this.field_1538 = arg3;
	}

	public void close() {
		this.field_1540.close();
	}

	public void method_1292(String string, Object object, int i, int j) {
		this.field_1539.add(this.field_1539.size(), string);
		this.field_1534.add(this.field_1534.size(), object);
		this.field_1533.add(this.field_1533.size(), i);
		this.field_1537.add(this.field_1537.size(), j);
	}

	private void method_1294() {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableFog();
		GlStateManager.disableLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.enableTexture();
		GlStateManager.bindTexture(0);
	}

	public void method_1291(class_1159 arg) {
		this.field_1535 = arg;
	}

	public void method_1293(float f) {
		this.method_1294();
		this.field_1536.method_1240();
		float g = (float)this.field_1538.field_1482;
		float h = (float)this.field_1538.field_1481;
		GlStateManager.viewport(0, 0, (int)g, (int)h);
		this.field_1540.method_1269("DiffuseSampler", this.field_1536);

		for (int i = 0; i < this.field_1534.size(); i++) {
			this.field_1540.method_1269((String)this.field_1539.get(i), this.field_1534.get(i));
			this.field_1540
				.method_1275("AuxSize" + i)
				.method_1255((float)((Integer)this.field_1533.get(i)).intValue(), (float)((Integer)this.field_1537.get(i)).intValue());
		}

		this.field_1540.method_1275("ProjMat").method_1250(this.field_1535);
		this.field_1540.method_1275("InSize").method_1255((float)this.field_1536.field_1482, (float)this.field_1536.field_1481);
		this.field_1540.method_1275("OutSize").method_1255(g, h);
		this.field_1540.method_1275("Time").method_1251(f);
		class_310 lv = class_310.method_1551();
		this.field_1540.method_1275("ScreenSize").method_1255((float)lv.field_1704.method_4489(), (float)lv.field_1704.method_4506());
		this.field_1540.method_1277();
		this.field_1538.method_1230(class_310.field_1703);
		this.field_1538.method_1235(false);
		GlStateManager.depthMask(false);
		GlStateManager.colorMask(true, true, true, true);
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		lv3.method_1328(7, class_290.field_1576);
		lv3.method_1315(0.0, 0.0, 500.0).method_1323(255, 255, 255, 255).method_1344();
		lv3.method_1315((double)g, 0.0, 500.0).method_1323(255, 255, 255, 255).method_1344();
		lv3.method_1315((double)g, (double)h, 500.0).method_1323(255, 255, 255, 255).method_1344();
		lv3.method_1315(0.0, (double)h, 500.0).method_1323(255, 255, 255, 255).method_1344();
		lv2.method_1350();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
		this.field_1540.method_1273();
		this.field_1538.method_1240();
		this.field_1536.method_1242();

		for (Object object : this.field_1534) {
			if (object instanceof class_276) {
				((class_276)object).method_1242();
			}
		}
	}

	public class_280 method_1295() {
		return this.field_1540;
	}
}
