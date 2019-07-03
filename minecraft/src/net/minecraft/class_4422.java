package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4422 extends RealmsScreen {
	private final class_4388 field_20096;
	private final RealmsServer field_20097;
	private final int field_20098 = 212;
	private RealmsButton field_20099;
	private RealmsEditBox field_20100;
	private RealmsEditBox field_20101;
	private RealmsLabel field_20102;

	public class_4422(class_4388 arg, RealmsServer realmsServer) {
		this.field_20096 = arg;
		this.field_20097 = realmsServer;
	}

	@Override
	public void tick() {
		this.field_20101.tick();
		this.field_20100.tick();
		this.field_20099.active(this.field_20101.getValue() != null && !this.field_20101.getValue().trim().isEmpty());
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		int i = this.width() / 2 - 106;
		this.buttonsAdd(this.field_20099 = new RealmsButton(1, i - 2, class_4359.method_21072(12), 106, 20, getLocalizedString("mco.configure.world.buttons.done")) {
			@Override
			public void onPress() {
				class_4422.this.method_21454();
			}
		});
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 2, class_4359.method_21072(12), 106, 20, getLocalizedString("gui.cancel")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4422.this.field_20096);
			}
		});
		this.buttonsAdd(
			new RealmsButton(
				5,
				this.width() / 2 - 53,
				class_4359.method_21072(0),
				106,
				20,
				getLocalizedString(this.field_20097.state.equals(RealmsServer.class_4320.OPEN) ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open")
			) {
				@Override
				public void onPress() {
					if (class_4422.this.field_20097.state.equals(RealmsServer.class_4320.OPEN)) {
						String string = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line1");
						String string2 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line2");
						Realms.setScreen(new class_4396(class_4422.this, class_4396.class_4397.INFO, string, string2, true, 5));
					} else {
						class_4422.this.field_20096.method_21218(false, class_4422.this);
					}
				}
			}
		);
		this.field_20101 = this.newEditBox(2, i, class_4359.method_21072(4), 212, 20, getLocalizedString("mco.configure.world.name"));
		this.field_20101.setMaxLength(32);
		if (this.field_20097.getName() != null) {
			this.field_20101.setValue(this.field_20097.getName());
		}

		this.addWidget(this.field_20101);
		this.focusOn(this.field_20101);
		this.field_20100 = this.newEditBox(3, i, class_4359.method_21072(8), 212, 20, getLocalizedString("mco.configure.world.description"));
		this.field_20100.setMaxLength(32);
		if (this.field_20097.getDescription() != null) {
			this.field_20100.setValue(this.field_20097.getDescription());
		}

		this.addWidget(this.field_20100);
		this.addWidget(this.field_20102 = new RealmsLabel(getLocalizedString("mco.configure.world.settings.title"), this.width() / 2, 17, 16777215));
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		switch (i) {
			case 5:
				if (bl) {
					this.field_20096.method_21217(this);
				} else {
					Realms.setScreen(this);
				}
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		switch (i) {
			case 256:
				Realms.setScreen(this.field_20096);
				return true;
			default:
				return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_20102.render(this);
		this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 106, class_4359.method_21072(3), 10526880);
		this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 106, class_4359.method_21072(7), 10526880);
		this.field_20101.render(i, j, f);
		this.field_20100.render(i, j, f);
		super.render(i, j, f);
	}

	public void method_21454() {
		this.field_20096.method_21215(this.field_20101.getValue(), this.field_20100.getValue());
	}
}
