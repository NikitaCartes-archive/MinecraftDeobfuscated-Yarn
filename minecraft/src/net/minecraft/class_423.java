package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_423 extends class_408 {
	public class_423() {
		super("");
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new class_4185(this.width / 2 - 100, this.height - 40, 200, 20, class_1074.method_4662("multiplayer.stopSleeping"), arg -> this.method_2180()));
	}

	@Override
	public void onClose() {
		this.method_2180();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.method_2180();
		} else if (i == 257 || i == 335) {
			String string = this.field_2382.method_1882().trim();
			if (!string.isEmpty()) {
				this.minecraft.field_1724.method_3142(string);
			}

			this.field_2382.method_1852("");
			this.minecraft.field_1705.method_1743().method_1820();
			return true;
		}

		return super.keyPressed(i, j, k);
	}

	private void method_2180() {
		class_634 lv = this.minecraft.field_1724.field_3944;
		lv.method_2883(new class_2848(this.minecraft.field_1724, class_2848.class_2849.field_12986));
	}
}
