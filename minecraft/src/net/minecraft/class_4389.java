package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4389 extends RealmsScreen {
	protected RealmsScreen field_19820;
	protected String field_19821;
	private final String field_19825;
	protected String field_19822;
	protected String field_19823;
	protected int field_19824;
	private int field_19826;

	public class_4389(RealmsScreen realmsScreen, String string, String string2, int i) {
		this.field_19820 = realmsScreen;
		this.field_19821 = string;
		this.field_19825 = string2;
		this.field_19824 = i;
		this.field_19822 = getLocalizedString("gui.yes");
		this.field_19823 = getLocalizedString("gui.no");
	}

	@Override
	public void init() {
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, class_4359.method_21072(9), 100, 20, this.field_19822) {
			@Override
			public void onPress() {
				class_4389.this.field_19820.confirmResult(true, class_4389.this.field_19824);
			}
		});
		this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, class_4359.method_21072(9), 100, 20, this.field_19823) {
			@Override
			public void onPress() {
				class_4389.this.field_19820.confirmResult(false, class_4389.this.field_19824);
			}
		});
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.field_19821, this.width() / 2, class_4359.method_21072(3), 16777215);
		this.drawCenteredString(this.field_19825, this.width() / 2, class_4359.method_21072(5), 16777215);
		super.render(i, j, f);
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.field_19826 == 0) {
			for (AbstractRealmsButton<?> abstractRealmsButton : this.buttons()) {
				abstractRealmsButton.active(true);
			}
		}
	}
}
