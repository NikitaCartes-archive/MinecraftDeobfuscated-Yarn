package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_865 implements class_863.class_864 {
	private final class_310 field_4540;
	private double field_4541 = Double.MIN_VALUE;
	private List<class_265> field_4542 = Collections.emptyList();

	public class_865(class_310 arg) {
		this.field_4540 = arg;
	}

	@Override
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4540.field_1724;
		double d = (double)class_156.method_648();
		if (d - this.field_4541 > 1.0E8) {
			this.field_4541 = d;
			this.field_4542 = (List<class_265>)lv.field_6002.method_8607(lv, lv.method_5829().method_1014(6.0)).collect(Collectors.toList());
		}

		double e = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double g = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double h = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (class_265 lv2 : this.field_4542) {
			class_761.method_3240(lv2, -e, -g, -h, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
