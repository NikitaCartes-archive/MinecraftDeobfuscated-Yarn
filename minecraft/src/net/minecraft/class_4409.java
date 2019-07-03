package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4409 extends RealmsScreen {
	private final class_4410 field_19982;
	private RealmsLabel field_19983;
	private RealmsEditBox field_19984;
	private Boolean field_19985 = true;
	private Integer field_19986 = 0;
	String[] field_19981;
	private final int field_19987 = 0;
	private final int field_19988 = 1;
	private final int field_19989 = 4;
	private RealmsButton field_19990;
	private RealmsButton field_19991;
	private RealmsButton field_19992;
	private String field_19993 = getLocalizedString("mco.backup.button.reset");

	public class_4409(class_4410 arg) {
		this.field_19982 = arg;
	}

	public class_4409(class_4410 arg, String string) {
		this(arg);
		this.field_19993 = string;
	}

	@Override
	public void tick() {
		this.field_19984.tick();
		super.tick();
	}

	@Override
	public void init() {
		this.field_19981 = new String[]{
			getLocalizedString("generator.default"),
			getLocalizedString("generator.flat"),
			getLocalizedString("generator.largeBiomes"),
			getLocalizedString("generator.amplified")
		};
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 8, class_4359.method_21072(12), 97, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4409.this.field_19982);
			}
		});
		this.buttonsAdd(this.field_19990 = new RealmsButton(1, this.width() / 2 - 102, class_4359.method_21072(12), 97, 20, this.field_19993) {
			@Override
			public void onPress() {
				class_4409.this.method_21357();
			}
		});
		this.field_19984 = this.newEditBox(4, this.width() / 2 - 100, class_4359.method_21072(2), 200, 20, getLocalizedString("mco.reset.world.seed"));
		this.field_19984.setMaxLength(32);
		this.field_19984.setValue("");
		this.addWidget(this.field_19984);
		this.focusOn(this.field_19984);
		this.buttonsAdd(this.field_19991 = new RealmsButton(2, this.width() / 2 - 102, class_4359.method_21072(4), 205, 20, this.method_21361()) {
			@Override
			public void onPress() {
				class_4409.this.field_19986 = (class_4409.this.field_19986 + 1) % class_4409.this.field_19981.length;
				this.setMessage(class_4409.this.method_21361());
			}
		});
		this.buttonsAdd(this.field_19992 = new RealmsButton(3, this.width() / 2 - 102, class_4359.method_21072(6) - 2, 205, 20, this.method_21363()) {
			@Override
			public void onPress() {
				class_4409.this.field_19985 = !class_4409.this.field_19985;
				this.setMessage(class_4409.this.method_21363());
			}
		});
		this.field_19983 = new RealmsLabel(getLocalizedString("mco.reset.world.generate"), this.width() / 2, 17, 16777215);
		this.addWidget(this.field_19983);
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_19982);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void method_21357() {
		this.field_19982.method_21372(new class_4410.class_4413(this.field_19984.getValue(), this.field_19986, this.field_19985));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_19983.render(this);
		this.drawString(getLocalizedString("mco.reset.world.seed"), this.width() / 2 - 100, class_4359.method_21072(1), 10526880);
		this.field_19984.render(i, j, f);
		super.render(i, j, f);
	}

	private String method_21361() {
		String string = getLocalizedString("selectWorld.mapType");
		return string + " " + this.field_19981[this.field_19986];
	}

	private String method_21363() {
		return getLocalizedString("selectWorld.mapFeatures") + " " + getLocalizedString(this.field_19985 ? "mco.configure.world.on" : "mco.configure.world.off");
	}
}
