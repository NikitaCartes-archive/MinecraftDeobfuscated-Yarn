package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4296 extends class_437 {
	private static final Random field_19247 = new Random();
	private static final class_2960 field_19248 = new class_2960("textures/gui/demo_background.png");
	private boolean field_19249;
	private final int field_19250 = field_19247.nextInt(42) + 1;

	public class_4296() {
		super(new class_2588("ui.next_disk.title"));
	}

	@Override
	protected void init() {
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_19248);
		int i = (this.width - 248) / 2;
		int j = (this.height - 166) / 2;
		this.blit(i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		int k = (this.width - 248) / 2 + 10;
		int l = (this.height - 166) / 2 + 8;
		this.font.method_1729(this.title.method_10863(), (float)k, (float)l, 2039583);
		l += 48;
		this.font.method_1729(class_1074.method_4662("ui.next_disk.contents", this.field_19250), (float)k, (float)l, 5197647);
		if (this.field_19249) {
			this.font.method_1729(class_1074.method_4662("ui.next_disk.wrong_contents"), (float)k, (float)(l + 12), 16711680);
		}

		super.render(i, j, f);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 257) {
			this.minecraft.method_1507(null);
		} else {
			this.field_19249 = true;
		}

		return true;
	}
}
