package net.minecraft;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_420 extends class_437 {
	private class_4185 field_2462;
	private final class_642 field_2460;
	private class_342 field_2463;
	private final BooleanConsumer field_19235;

	public class_420(BooleanConsumer booleanConsumer, class_642 arg) {
		super(new class_2588("selectServer.direct"));
		this.field_2460 = arg;
		this.field_19235 = booleanConsumer;
	}

	@Override
	public void tick() {
		this.field_2463.method_1865();
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_2462 = this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, class_1074.method_4662("selectServer.select"), arg -> this.method_2167())
		);
		this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, class_1074.method_4662("gui.cancel"), arg -> this.field_19235.accept(false))
		);
		this.field_2463 = new class_342(this.font, this.width / 2 - 100, 116, 200, 20, class_1074.method_4662("addServer.enterIp"));
		this.field_2463.method_1880(128);
		this.field_2463.method_1876(true);
		this.field_2463.method_1852(this.minecraft.field_1690.field_1864);
		this.field_2463.method_1863(string -> this.method_2169());
		this.children.add(this.field_2463);
		this.method_20085(this.field_2463);
		this.method_2169();
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2463.method_1882();
		this.init(arg, i, j);
		this.field_2463.method_1852(string);
	}

	private void method_2167() {
		this.field_2460.field_3761 = this.field_2463.method_1882();
		this.field_19235.accept(true);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
		this.minecraft.field_1690.field_1864 = this.field_2463.method_1882();
		this.minecraft.field_1690.method_1640();
	}

	private void method_2169() {
		this.field_2462.active = !this.field_2463.method_1882().isEmpty() && this.field_2463.method_1882().split(":").length > 0;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, 16777215);
		this.drawString(this.font, class_1074.method_4662("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.field_2463.render(i, j, f);
		super.render(i, j, f);
	}
}
