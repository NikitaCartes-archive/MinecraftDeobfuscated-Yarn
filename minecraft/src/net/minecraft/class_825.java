package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_825 extends class_827<class_2587> {
	private static final class_2960[] field_4356 = (class_2960[])Arrays.stream(class_1767.values())
		.sorted(Comparator.comparingInt(class_1767::method_7789))
		.map(arg -> new class_2960("textures/entity/bed/" + arg.method_7792() + ".png"))
		.toArray(class_2960[]::new);
	private final class_552 field_4355 = new class_552();

	public void method_3557(class_2587 arg, double d, double e, double f, float g, int i) {
		if (i >= 0) {
			this.method_3566(field_4368[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 4.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			class_2960 lv = field_4356[arg.method_11018().method_7789()];
			if (lv != null) {
				this.method_3566(lv);
			}
		}

		if (arg.method_11002()) {
			class_2680 lv2 = arg.method_11010();
			this.method_3558(lv2.method_11654(class_2244.field_9967) == class_2742.field_12560, d, e, f, lv2.method_11654(class_2244.field_11177));
		} else {
			this.method_3558(true, d, e, f, class_2350.field_11035);
			this.method_3558(false, d, e, f - 1.0, class_2350.field_11035);
		}

		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_3558(boolean bl, double d, double e, double f, class_2350 arg) {
		this.field_4355.method_2795(bl);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.5625F, (float)f);
		GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		GlStateManager.rotatef(180.0F + arg.method_10144(), 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		GlStateManager.enableRescaleNormal();
		this.field_4355.method_2794();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
