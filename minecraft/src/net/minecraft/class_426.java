package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_426 extends class_437 {
	protected final class_437 field_2490;
	private class_426.class_4195 field_2486;
	private final class_315 field_2489;
	private final class_1076 field_2488;
	private class_349 field_2487;
	private class_4185 field_2491;

	public class_426(class_437 arg, class_315 arg2, class_1076 arg3) {
		super(new class_2588("options.language"));
		this.field_2490 = arg;
		this.field_2489 = arg2;
		this.field_2488 = arg3;
	}

	@Override
	protected void init() {
		this.field_2486 = new class_426.class_4195(this.minecraft);
		this.children.add(this.field_2486);
		this.field_2487 = this.addButton(
			new class_349(this.width / 2 - 155, this.height - 38, 150, 20, class_316.field_18185, class_316.field_18185.method_18495(this.field_2489), arg -> {
				class_316.field_18185.method_18491(this.field_2489);
				this.field_2489.method_1640();
				arg.setMessage(class_316.field_18185.method_18495(this.field_2489));
				this.minecraft.method_15993();
			})
		);
		this.field_2491 = this.addButton(new class_4185(this.width / 2 - 155 + 160, this.height - 38, 150, 20, class_1074.method_4662("gui.done"), arg -> {
			class_426.class_4195.class_4194 lv = this.field_2486.getSelected();
			if (lv != null && !lv.field_18743.getCode().equals(this.field_2488.method_4669().getCode())) {
				this.field_2488.method_4667(lv.field_18743);
				this.field_2489.field_1883 = lv.field_18743.getCode();
				this.minecraft.method_1521();
				this.font.method_1719(this.field_2488.method_4666());
				this.field_2491.setMessage(class_1074.method_4662("gui.done"));
				this.field_2487.setMessage(class_316.field_18185.method_18495(this.field_2489));
				this.field_2489.method_1640();
			}

			this.minecraft.method_1507(this.field_2490);
		}));
		super.init();
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_2486.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, "(" + class_1074.method_4662("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_4195 extends class_4280<class_426.class_4195.class_4194> {
		public class_4195(class_310 arg2) {
			super(arg2, class_426.this.width, class_426.this.height, 32, class_426.this.height - 65 + 4, 18);

			for (class_1077 lv : class_426.this.field_2488.method_4665()) {
				class_426.class_4195.class_4194 lv2 = new class_426.class_4195.class_4194(lv);
				this.addEntry(lv2);
				if (class_426.this.field_2488.method_4669().getCode().equals(lv.getCode())) {
					this.method_20100(lv2);
				}
			}

			if (this.getSelected() != null) {
				this.centerScrollOn(this.getSelected());
			}
		}

		@Override
		protected int getScrollbarPosition() {
			return super.getScrollbarPosition() + 20;
		}

		@Override
		public int getRowWidth() {
			return super.getRowWidth() + 50;
		}

		public void method_20100(@Nullable class_426.class_4195.class_4194 arg) {
			super.setSelected(arg);
			if (arg != null) {
				class_333.field_2054.method_19788(new class_2588("narrator.select", arg.field_18743).getString());
			}
		}

		@Override
		protected void renderBackground() {
			class_426.this.renderBackground();
		}

		@Override
		protected boolean isFocused() {
			return class_426.this.getFocused() == this;
		}

		@Environment(EnvType.CLIENT)
		public class class_4194 extends class_4280.class_4281<class_426.class_4195.class_4194> {
			private final class_1077 field_18743;

			public class_4194(class_1077 arg2) {
				this.field_18743 = arg2;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				class_426.this.font.method_1719(true);
				class_4195.this.drawCenteredString(class_426.this.font, this.field_18743.toString(), class_4195.this.width / 2, j + 1, 16777215);
				class_426.this.font.method_1719(class_426.this.field_2488.method_4669().method_4672());
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					this.method_19381();
					return true;
				} else {
					return false;
				}
			}

			private void method_19381() {
				class_4195.this.method_20100(this);
			}
		}
	}
}
