package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4391 extends RealmsScreen {
	private final class_4325 field_19837;
	private RealmsEditBox field_19838;
	private RealmsEditBox field_19839;
	private boolean field_19840;
	private RealmsButton field_19841;

	public class_4391(class_4325 arg) {
		this.field_19837 = arg;
	}

	@Override
	public void tick() {
		if (this.field_19838 != null) {
			this.field_19838.tick();
			this.field_19841.active(this.method_21251());
		}

		if (this.field_19839 != null) {
			this.field_19839.tick();
		}
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		if (!this.field_19840) {
			this.field_19840 = true;
			this.field_19838 = this.newEditBox(3, this.width() / 2 - 100, 65, 200, 20, getLocalizedString("mco.configure.world.name"));
			this.focusOn(this.field_19838);
			this.field_19839 = this.newEditBox(4, this.width() / 2 - 100, 115, 200, 20, getLocalizedString("mco.configure.world.description"));
		}

		this.buttonsAdd(
			this.field_19841 = new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 17, 97, 20, getLocalizedString("mco.create.world")) {
				@Override
				public void onPress() {
					class_4391.this.method_21249();
				}
			}
		);
		this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, this.height() / 4 + 120 + 17, 95, 20, getLocalizedString("gui.cancel")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4391.this.field_19837);
			}
		});
		this.field_19841.active(this.method_21251());
		this.addWidget(this.field_19838);
		this.addWidget(this.field_19839);
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean charTyped(char c, int i) {
		this.field_19841.active(this.method_21251());
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		switch (i) {
			case 256:
				Realms.setScreen(this.field_19837);
				return true;
			default:
				this.field_19841.active(this.method_21251());
				return false;
		}
	}

	private void method_21249() {
		if (this.method_21251()) {
			class_4434.class_4444 lv = new class_4434.class_4444(this.field_19838.getValue(), this.field_19839.getValue(), this.field_19837);
			class_4398 lv2 = new class_4398(this.field_19837, lv);
			lv2.method_21288();
			Realms.setScreen(lv2);
		}
	}

	private boolean method_21251() {
		return this.field_19838 != null && this.field_19838.getValue() != null && !this.field_19838.getValue().trim().isEmpty();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(getLocalizedString("mco.trial.title"), this.width() / 2, 11, 16777215);
		this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 100, 52, 10526880);
		this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 100, 102, 10526880);
		if (this.field_19838 != null) {
			this.field_19838.render(i, j, f);
		}

		if (this.field_19839 != null) {
			this.field_19839.render(i, j, f);
		}

		super.render(i, j, f);
	}
}
