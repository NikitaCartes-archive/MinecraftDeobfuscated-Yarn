package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4394 extends RealmsScreen {
	private final RealmsScreen field_19874;
	private String field_19875;
	private String field_19876;

	public class_4394(class_4355 arg, RealmsScreen realmsScreen) {
		this.field_19874 = realmsScreen;
		this.method_21280(arg);
	}

	public class_4394(String string, RealmsScreen realmsScreen) {
		this.field_19874 = realmsScreen;
		this.method_21282(string);
	}

	public class_4394(String string, String string2, RealmsScreen realmsScreen) {
		this.field_19874 = realmsScreen;
		this.method_21283(string, string2);
	}

	private void method_21280(class_4355 arg) {
		if (arg.field_19606 == -1) {
			this.field_19875 = "An error occurred (" + arg.field_19604 + "):";
			this.field_19876 = arg.field_19605;
		} else {
			this.field_19875 = "Realms (" + arg.field_19606 + "):";
			String string = "mco.errorMessage." + arg.field_19606;
			String string2 = getLocalizedString(string);
			this.field_19876 = string2.equals(string) ? arg.field_19607 : string2;
		}
	}

	private void method_21282(String string) {
		this.field_19875 = "An error occurred: ";
		this.field_19876 = string;
	}

	private void method_21283(String string, String string2) {
		this.field_19875 = string;
		this.field_19876 = string2;
	}

	@Override
	public void init() {
		Realms.narrateNow(this.field_19875 + ": " + this.field_19876);
		this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
			@Override
			public void onPress() {
				Realms.setScreen(class_4394.this.field_19874);
			}
		});
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.field_19875, this.width() / 2, 80, 16777215);
		this.drawCenteredString(this.field_19876, this.width() / 2, 100, 16711680);
		super.render(i, j, f);
	}
}
