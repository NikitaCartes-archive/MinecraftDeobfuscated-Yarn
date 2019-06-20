package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4288 extends class_437 {
	private final class_437 field_19245;
	private class_353 field_19246;
	private static final class_316[] field_19247 = new class_316[]{
		class_316.field_1944, class_316.field_1963, class_316.field_18191, class_316.field_19243, class_316.field_1930
	};

	public class_4288(class_437 arg) {
		super(new class_2588("options.mouse_settings.title"));
		this.field_19245 = arg;
	}

	@Override
	protected void init() {
		this.field_19246 = new class_353(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.field_19246.method_20408(field_19247);
		this.children.add(this.field_19246);
		this.addButton(new class_4185(this.width / 2 - 100, this.height - 27, 200, 20, class_1074.method_4662("gui.done"), arg -> {
			this.minecraft.field_1690.method_1640();
			this.minecraft.method_1507(this.field_19245);
		}));
	}

	@Override
	public void removed() {
		this.minecraft.field_1690.method_1640();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_19246.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 5, 16777215);
		super.render(i, j, f);
	}
}
