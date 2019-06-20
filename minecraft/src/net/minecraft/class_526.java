package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_526 extends class_437 {
	protected final class_437 field_3221;
	private String field_3222;
	private class_4185 field_3219;
	private class_4185 field_3224;
	private class_4185 field_3215;
	private class_4185 field_3216;
	protected class_342 field_3220;
	private class_528 field_3218;

	public class_526(class_437 arg) {
		super(new class_2588("selectWorld.title"));
		this.field_3221 = arg;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return super.mouseScrolled(d, e, f);
	}

	@Override
	public void tick() {
		this.field_3220.method_1865();
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_3220 = new class_342(this.font, this.width / 2 - 100, 22, 200, 20, this.field_3220, class_1074.method_4662("selectWorld.search"));
		this.field_3220.method_1863(string -> this.field_3218.method_2750(() -> string, false));
		this.field_3218 = new class_528(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> this.field_3220.method_1882(), this.field_3218);
		this.children.add(this.field_3220);
		this.children.add(this.field_3218);
		this.field_3224 = this.addButton(
			new class_4185(
				this.width / 2 - 154,
				this.height - 52,
				150,
				20,
				class_1074.method_4662("selectWorld.select"),
				arg -> this.field_3218.method_20159().ifPresent(class_528.class_4272::method_20164)
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 + 4, this.height - 52, 150, 20, class_1074.method_4662("selectWorld.create"), arg -> this.minecraft.method_1507(new class_525(this))
			)
		);
		this.field_3215 = this.addButton(
			new class_4185(
				this.width / 2 - 154,
				this.height - 28,
				72,
				20,
				class_1074.method_4662("selectWorld.edit"),
				arg -> this.field_3218.method_20159().ifPresent(class_528.class_4272::method_20171)
			)
		);
		this.field_3219 = this.addButton(
			new class_4185(
				this.width / 2 - 76,
				this.height - 28,
				72,
				20,
				class_1074.method_4662("selectWorld.delete"),
				arg -> this.field_3218.method_20159().ifPresent(class_528.class_4272::method_20169)
			)
		);
		this.field_3216 = this.addButton(
			new class_4185(
				this.width / 2 + 4,
				this.height - 28,
				72,
				20,
				class_1074.method_4662("selectWorld.recreate"),
				arg -> this.field_3218.method_20159().ifPresent(class_528.class_4272::method_20173)
			)
		);
		this.addButton(
			new class_4185(this.width / 2 + 82, this.height - 28, 72, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(this.field_3221))
		);
		this.method_19940(false);
		this.method_20085(this.field_3220);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k) ? true : this.field_3220.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.field_3220.charTyped(c, i);
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_3222 = null;
		this.field_3218.render(i, j, f);
		this.field_3220.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 8, 16777215);
		super.render(i, j, f);
		if (this.field_3222 != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3222)), i, j);
		}
	}

	public void method_2739(String string) {
		this.field_3222 = string;
	}

	public void method_19940(boolean bl) {
		this.field_3224.active = bl;
		this.field_3219.active = bl;
		this.field_3215.active = bl;
		this.field_3216.active = bl;
	}

	@Override
	public void removed() {
		if (this.field_3218 != null) {
			this.field_3218.children().forEach(class_528.class_4272::close);
		}
	}
}
