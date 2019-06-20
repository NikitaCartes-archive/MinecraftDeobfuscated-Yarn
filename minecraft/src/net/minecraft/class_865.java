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
	public void method_3715(long l) {
		class_4184 lv = this.field_4540.field_1773.method_19418();
		double d = (double)class_156.method_648();
		if (d - this.field_4541 > 1.0E8) {
			this.field_4541 = d;
			this.field_4542 = (List<class_265>)lv.method_19331()
				.field_6002
				.method_8600(lv.method_19331(), lv.method_19331().method_5829().method_1014(6.0), Collections.emptySet())
				.collect(Collectors.toList());
		}

		double e = lv.method_19326().field_1352;
		double f = lv.method_19326().field_1351;
		double g = lv.method_19326().field_1350;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (class_265 lv2 : this.field_4542) {
			class_761.method_3240(lv2, -e, -f, -g, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
