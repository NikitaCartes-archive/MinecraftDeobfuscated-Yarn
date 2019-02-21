package net.minecraft.client.gui.menu;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen {
	private final String title;
	private final TextComponent reason;
	private List<String> reasonFormatted;
	private final Screen parent;
	private int reasonHeight;

	public DisconnectedScreen(Screen screen, String string, TextComponent textComponent) {
		this.parent = screen;
		this.title = I18n.translate(string);
		this.reason = textComponent;
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	protected void onInitialized() {
		this.reasonFormatted = this.fontRenderer.wrapStringToWidthAsList(this.reason.getFormattedText(), this.screenWidth - 50);
		this.reasonHeight = this.reasonFormatted.size() * 9;
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100, Math.min(this.screenHeight / 2 + this.reasonHeight / 2 + 9, this.screenHeight - 30), I18n.translate("gui.toMenu")
			) {
				@Override
				public void onPressed(double d, double e) {
					DisconnectedScreen.this.client.openScreen(DisconnectedScreen.this.parent);
				}
			}
		);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, this.screenHeight / 2 - this.reasonHeight / 2 - 9 * 2, 11184810);
		int k = this.screenHeight / 2 - this.reasonHeight / 2;
		if (this.reasonFormatted != null) {
			for (String string : this.reasonFormatted) {
				this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
				k += 9;
			}
		}

		super.draw(i, j, f);
	}
}
