package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3742 extends class_437 {
	private final class_3751 field_16522;
	private class_342 field_16520;
	private class_342 field_16518;
	private class_342 field_16519;
	private class_4185 field_19103;

	public class_3742(class_3751 arg) {
		super(class_333.field_18967);
		this.field_16522 = arg;
	}

	@Override
	public void tick() {
		this.field_16520.method_1865();
		this.field_16518.method_1865();
		this.field_16519.method_1865();
	}

	private void method_16346() {
		this.method_16348();
		this.minecraft.method_1507(null);
	}

	private void method_16349() {
		this.minecraft.method_1507(null);
	}

	private void method_16348() {
		this.minecraft
			.method_1562()
			.method_2883(
				new class_3753(
					this.field_16522.method_11016(),
					new class_2960(this.field_16520.method_1882()),
					new class_2960(this.field_16518.method_1882()),
					this.field_16519.method_1882()
				)
			);
	}

	@Override
	public void onClose() {
		this.method_16349();
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_19103 = this.addButton(new class_4185(this.width / 2 - 4 - 150, 210, 150, 20, class_1074.method_4662("gui.done"), arg -> this.method_16346()));
		this.addButton(new class_4185(this.width / 2 + 4, 210, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.method_16349()));
		this.field_16518 = new class_342(this.font, this.width / 2 - 152, 40, 300, 20);
		this.field_16518.method_1880(128);
		this.field_16518.method_1852(this.field_16522.method_16382().toString());
		this.field_16518.method_1863(string -> this.method_20118());
		this.children.add(this.field_16518);
		this.field_16520 = new class_342(this.font, this.width / 2 - 152, 80, 300, 20);
		this.field_16520.method_1880(128);
		this.field_16520.method_1852(this.field_16522.method_16381().toString());
		this.field_16520.method_1863(string -> this.method_20118());
		this.children.add(this.field_16520);
		this.field_16519 = new class_342(this.font, this.width / 2 - 152, 120, 300, 20);
		this.field_16519.method_1880(256);
		this.field_16519.method_1852(this.field_16522.method_16380());
		this.children.add(this.field_16519);
		this.method_20085(this.field_16518);
		this.method_20118();
	}

	protected void method_20118() {
		this.field_19103.active = class_2960.method_20207(this.field_16520.method_1882()) & class_2960.method_20207(this.field_16518.method_1882());
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_16520.method_1882();
		String string2 = this.field_16518.method_1882();
		String string3 = this.field_16519.method_1882();
		this.init(arg, i, j);
		this.field_16520.method_1852(string);
		this.field_16518.method_1852(string2);
		this.field_16519.method_1852(string3);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.method_16346();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawString(this.font, class_1074.method_4662("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
		this.field_16518.render(i, j, f);
		this.drawString(this.font, class_1074.method_4662("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
		this.field_16520.render(i, j, f);
		this.drawString(this.font, class_1074.method_4662("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
		this.field_16519.render(i, j, f);
		super.render(i, j, f);
	}
}
