package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServer;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4426 extends RealmsScreen {
	private static final Logger field_20165 = LogManager.getLogger();
	private final RealmsScreen field_20166;
	private final class_4325 field_20167;
	private final RealmsServer field_20168;
	private RealmsButton field_20169;
	private boolean field_20170;
	private final String field_20171 = "https://minecraft.net/realms/terms";

	public class_4426(RealmsScreen realmsScreen, class_4325 arg, RealmsServer realmsServer) {
		this.field_20166 = realmsScreen;
		this.field_20167 = arg;
		this.field_20168 = realmsServer;
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		int i = this.width() / 4;
		int j = this.width() / 4 - 2;
		int k = this.width() / 2 + 4;
		this.buttonsAdd(this.field_20169 = new RealmsButton(1, i, class_4359.method_21072(12), j, 20, getLocalizedString("mco.terms.buttons.agree")) {
			@Override
			public void onPress() {
				class_4426.this.method_21505();
			}
		});
		this.buttonsAdd(new RealmsButton(2, k, class_4359.method_21072(12), j, 20, getLocalizedString("mco.terms.buttons.disagree")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4426.this.field_20166);
			}
		});
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_20166);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void method_21505() {
		class_4341 lv = class_4341.method_20989();

		try {
			lv.method_21031();
			class_4398 lv2 = new class_4398(this.field_20166, new class_4434.class_4439(this.field_20167, this.field_20166, this.field_20168, new ReentrantLock()));
			lv2.method_21288();
			Realms.setScreen(lv2);
		} catch (class_4355 var3) {
			field_20165.error("Couldn't agree to TOS");
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_20170) {
			Realms.setClipboard("https://minecraft.net/realms/terms");
			class_4448.method_21570("https://minecraft.net/realms/terms");
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(getLocalizedString("mco.terms.title"), this.width() / 2, 17, 16777215);
		this.drawString(getLocalizedString("mco.terms.sentence.1"), this.width() / 2 - 120, class_4359.method_21072(5), 16777215);
		int k = this.fontWidth(getLocalizedString("mco.terms.sentence.1"));
		int l = this.width() / 2 - 121 + k;
		int m = class_4359.method_21072(5);
		int n = l + this.fontWidth("mco.terms.sentence.2") + 1;
		int o = m + 1 + this.fontLineHeight();
		if (l <= i && i <= n && m <= j && j <= o) {
			this.field_20170 = true;
			this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + k, class_4359.method_21072(5), 7107012);
		} else {
			this.field_20170 = false;
			this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + k, class_4359.method_21072(5), 3368635);
		}

		super.render(i, j, f);
	}
}
