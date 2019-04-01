package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_474 extends class_4185 {
	private final boolean field_2851;
	private final boolean field_18977;

	public class_474(int i, int j, boolean bl, class_4185.class_4241 arg, boolean bl2) {
		super(i, j, 23, 13, "", arg);
		this.field_2851 = bl;
		this.field_18977 = bl2;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_310.method_1551().method_1531().method_4618(class_3872.field_17117);
		int k = 0;
		int l = 192;
		if (this.isHovered()) {
			k += 23;
		}

		if (!this.field_2851) {
			l += 13;
		}

		this.blit(this.x, this.y, k, l, 23, 13);
	}

	@Override
	public void playDownSound(class_1144 arg) {
		if (this.field_18977) {
			arg.method_4873(class_1109.method_4758(class_3417.field_17481, 1.0F));
		}
	}
}
