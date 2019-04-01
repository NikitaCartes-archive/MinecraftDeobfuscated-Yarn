package net.minecraft.realms;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;

@Environment(EnvType.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
	private final String title;
	private final class_2561 reason;
	private List<String> lines;
	private final RealmsScreen parent;
	private int textHeight;

	public DisconnectedRealmsScreen(RealmsScreen realmsScreen, String string, class_2561 arg) {
		this.parent = realmsScreen;
		this.title = getLocalizedString(string);
		this.reason = arg;
	}

	@Override
	public void init() {
		Realms.setConnectedToRealms(false);
		Realms.clearResourcePack();
		this.lines = this.fontSplit(this.reason.method_10863(), this.width() - 50);
		this.textHeight = this.lines.size() * this.fontLineHeight();
		this.buttonsAdd(
			new RealmsButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), getLocalizedString("gui.back")) {
				@Override
				public void onPress() {
					Realms.setScreen(DisconnectedRealmsScreen.this.parent);
				}
			}
		);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
		int k = this.height() / 2 - this.textHeight / 2;
		if (this.lines != null) {
			for (String string : this.lines) {
				this.drawCenteredString(string, this.width() / 2, k, 16777215);
				k += this.fontLineHeight();
			}
		}

		super.render(i, j, f);
	}
}
