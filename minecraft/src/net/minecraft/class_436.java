package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_436 extends class_437 {
	private final class_437 field_2548;
	private class_4185 field_2547;
	private class_4185 field_2549;
	private String field_2545 = "survival";
	private boolean field_2546;

	public class_436(class_437 arg) {
		super(new class_2588("lanServer.title"));
		this.field_2548 = arg;
	}

	@Override
	protected void init() {
		this.addButton(new class_4185(this.width / 2 - 155, this.height - 28, 150, 20, class_1074.method_4662("lanServer.start"), arg -> {
			this.minecraft.method_1507(null);
			int i = class_3521.method_15302();
			class_2561 lv;
			if (this.minecraft.method_1576().method_3763(class_1934.method_8385(this.field_2545), this.field_2546, i)) {
				lv = new class_2588("commands.publish.started", i);
			} else {
				lv = new class_2588("commands.publish.failed");
			}

			this.minecraft.field_1705.method_1743().method_1812(lv);
		}));
		this.addButton(
			new class_4185(this.width / 2 + 5, this.height - 28, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(this.field_2548))
		);
		this.field_2549 = this.addButton(new class_4185(this.width / 2 - 155, 100, 150, 20, class_1074.method_4662("selectWorld.gameMode"), arg -> {
			if ("spectator".equals(this.field_2545)) {
				this.field_2545 = "creative";
			} else if ("creative".equals(this.field_2545)) {
				this.field_2545 = "adventure";
			} else if ("adventure".equals(this.field_2545)) {
				this.field_2545 = "survival";
			} else {
				this.field_2545 = "spectator";
			}

			this.method_2204();
		}));
		this.field_2547 = this.addButton(new class_4185(this.width / 2 + 5, 100, 150, 20, class_1074.method_4662("selectWorld.allowCommands"), arg -> {
			this.field_2546 = !this.field_2546;
			this.method_2204();
		}));
		this.method_2204();
	}

	private void method_2204() {
		this.field_2549.setMessage(class_1074.method_4662("selectWorld.gameMode") + ": " + class_1074.method_4662("selectWorld.gameMode." + this.field_2545));
		this.field_2547
			.setMessage(class_1074.method_4662("selectWorld.allowCommands") + ' ' + class_1074.method_4662(this.field_2546 ? "options.on" : "options.off"));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 50, 16777215);
		this.drawCenteredString(this.font, class_1074.method_4662("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
		super.render(i, j, f);
	}
}
