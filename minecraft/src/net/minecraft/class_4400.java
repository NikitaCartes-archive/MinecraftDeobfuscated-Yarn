package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4400 extends RealmsScreen {
	private final RealmsScreen field_19931;

	public class_4400(RealmsScreen realmsScreen) {
		this.field_19931 = realmsScreen;
	}

	@Override
	public void init() {
		Realms.narrateNow(getLocalizedString("mco.account.privacyinfo"));
		String string = getLocalizedString("mco.account.update");
		String string2 = getLocalizedString("gui.back");
		int i = Math.max(this.fontWidth(string), this.fontWidth(string2)) + 30;
		String string3 = getLocalizedString("mco.account.privacy.info");
		int j = (int)((double)this.fontWidth(string3) * 1.2);
		this.buttonsAdd(new RealmsButton(1, this.width() / 2 - j / 2, class_4359.method_21072(11), j, 20, string3) {
			@Override
			public void onPress() {
				class_4448.method_21570("https://minecraft.net/privacy/gdpr/");
			}
		});
		this.buttonsAdd(new RealmsButton(1, this.width() / 2 - (i + 5), class_4359.method_21072(13), i, 20, string) {
			@Override
			public void onPress() {
				class_4448.method_21570("https://minecraft.net/update-account");
			}
		});
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 5, class_4359.method_21072(13), i, 20, string2) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4400.this.field_19931);
			}
		});
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return super.mouseClicked(d, e, i);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		List<String> list = this.getLocalizedStringWithLineWidth("mco.account.privacyinfo", (int)Math.round((double)this.width() * 0.9));
		int k = 15;

		for (String string : list) {
			this.drawCenteredString(string, this.width() / 2, k, 16777215);
			k += 15;
		}

		super.render(i, j, f);
	}
}