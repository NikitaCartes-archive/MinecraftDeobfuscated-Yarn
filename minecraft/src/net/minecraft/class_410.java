package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_410 extends class_437 {
	protected final class_411 field_2403;
	private final class_2561 field_2401;
	private final List<String> field_2404 = Lists.<String>newArrayList();
	protected String field_2402;
	protected String field_2399;
	protected final int field_2398;
	private int field_2400;

	public class_410(class_411 arg, class_2561 arg2, class_2561 arg3, int i) {
		this(arg, arg2, arg3, class_1074.method_4662("gui.yes"), class_1074.method_4662("gui.no"), i);
	}

	public class_410(class_411 arg, class_2561 arg2, class_2561 arg3, String string, String string2, int i) {
		super(arg2);
		this.field_2403 = arg;
		this.field_2401 = arg3;
		this.field_2402 = string;
		this.field_2399 = string2;
		this.field_2398 = i;
	}

	@Override
	public String getNarrationMessage() {
		return super.getNarrationMessage() + ". " + this.field_2401.getString();
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(
			new class_4185(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.field_2402, arg -> this.field_2403.confirmResult(true, this.field_2398))
		);
		this.addButton(
			new class_4185(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.field_2399, arg -> this.field_2403.confirmResult(false, this.field_2398))
		);
		this.field_2404.clear();
		this.field_2404.addAll(this.font.method_1728(this.field_2401.method_10863(), this.width - 50));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 70, 16777215);
		int k = 90;

		for (String string : this.field_2404) {
			this.drawCenteredString(this.font, string, this.width / 2, k, 16777215);
			k += 9;
		}

		super.render(i, j, f);
	}

	public void method_2125(int i) {
		this.field_2400 = i;

		for (class_339 lv : this.buttons) {
			lv.active = false;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.field_2400 == 0) {
			for (class_339 lv : this.buttons) {
				lv.active = true;
			}
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.field_2403.confirmResult(false, this.field_2398);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}
}
