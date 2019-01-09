package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_436 extends class_437 {
	private final class_437 field_2548;
	private class_339 field_2547;
	private class_339 field_2549;
	private String field_2545 = "survival";
	private boolean field_2546;

	public class_436(class_437 arg) {
		this.field_2548 = arg;
	}

	@Override
	protected void method_2224() {
		this.method_2219(new class_339(101, this.field_2561 / 2 - 155, this.field_2559 - 28, 150, 20, class_1074.method_4662("lanServer.start")) {
			@Override
			public void method_1826(double d, double e) {
				class_436.this.field_2563.method_1507(null);
				int i = class_3521.method_15302();
				class_2561 lv;
				if (class_436.this.field_2563.method_1576().method_3763(class_1934.method_8385(class_436.this.field_2545), class_436.this.field_2546, i)) {
					lv = new class_2588("commands.publish.started", i);
				} else {
					lv = new class_2588("commands.publish.failed");
				}

				class_436.this.field_2563.field_1705.method_1743().method_1812(lv);
			}
		});
		this.method_2219(new class_339(102, this.field_2561 / 2 + 5, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_436.this.field_2563.method_1507(class_436.this.field_2548);
			}
		});
		this.field_2549 = this.method_2219(new class_339(104, this.field_2561 / 2 - 155, 100, 150, 20, class_1074.method_4662("selectWorld.gameMode")) {
			@Override
			public void method_1826(double d, double e) {
				if ("spectator".equals(class_436.this.field_2545)) {
					class_436.this.field_2545 = "creative";
				} else if ("creative".equals(class_436.this.field_2545)) {
					class_436.this.field_2545 = "adventure";
				} else if ("adventure".equals(class_436.this.field_2545)) {
					class_436.this.field_2545 = "survival";
				} else {
					class_436.this.field_2545 = "spectator";
				}

				class_436.this.method_2204();
			}
		});
		this.field_2547 = this.method_2219(new class_339(103, this.field_2561 / 2 + 5, 100, 150, 20, class_1074.method_4662("selectWorld.allowCommands")) {
			@Override
			public void method_1826(double d, double e) {
				class_436.this.field_2546 = !class_436.this.field_2546;
				class_436.this.method_2204();
			}
		});
		this.method_2204();
	}

	private void method_2204() {
		this.field_2549.field_2074 = class_1074.method_4662("selectWorld.gameMode") + ": " + class_1074.method_4662("selectWorld.gameMode." + this.field_2545);
		this.field_2547.field_2074 = class_1074.method_4662("selectWorld.allowCommands") + " ";
		if (this.field_2546) {
			this.field_2547.field_2074 = this.field_2547.field_2074 + class_1074.method_4662("options.on");
		} else {
			this.field_2547.field_2074 = this.field_2547.field_2074 + class_1074.method_4662("options.off");
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("lanServer.title"), this.field_2561 / 2, 50, 16777215);
		this.method_1789(this.field_2554, class_1074.method_4662("lanServer.otherPlayers"), this.field_2561 / 2, 82, 16777215);
		super.method_2214(i, j, f);
	}
}
