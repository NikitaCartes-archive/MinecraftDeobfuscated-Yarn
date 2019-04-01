package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_403 extends class_437 {
	private final Runnable field_2345;
	protected final class_2561 field_2346;
	private final List<String> field_2348 = Lists.<String>newArrayList();
	protected final String field_2349;
	private int field_2347;

	public class_403(Runnable runnable, class_2561 arg, class_2561 arg2) {
		this(runnable, arg, arg2, "gui.back");
	}

	public class_403(Runnable runnable, class_2561 arg, class_2561 arg2, String string) {
		super(arg);
		this.field_2345 = runnable;
		this.field_2346 = arg2;
		this.field_2349 = class_1074.method_4662(string);
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new class_4185(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.field_2349, arg -> this.field_2345.run()));
		this.field_2348.clear();
		this.field_2348.addAll(this.font.method_1728(this.field_2346.method_10863(), this.width - 50));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 70, 16777215);
		int k = 90;

		for (String string : this.field_2348) {
			this.drawCenteredString(this.font, string, this.width / 2, k, 16777215);
			k += 9;
		}

		super.render(i, j, f);
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.field_2347 == 0) {
			for (class_339 lv : this.buttons) {
				lv.active = true;
			}
		}
	}
}
