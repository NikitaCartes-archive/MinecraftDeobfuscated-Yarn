package net.minecraft.client.gui.menu;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class DisconnectedGui extends Gui {
	private final String title;
	private final TextComponent reason;
	private List<String> reasonFormatted;
	private final Gui parent;
	private int reasonHeight;

	public DisconnectedGui(Gui gui, String string, TextComponent textComponent) {
		this.parent = gui;
		this.title = I18n.translate(string);
		this.reason = textComponent;
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	protected void onInitialized() {
		this.reasonFormatted = this.fontRenderer.wrapStringToWidthAsList(this.reason.getFormattedText(), this.width - 50);
		this.reasonHeight = this.reasonFormatted.size() * this.fontRenderer.fontHeight;
		this.addButton(
			new ButtonWidget(
				0, this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + this.fontRenderer.fontHeight, this.height - 30), I18n.translate("gui.toMenu")
			) {
				@Override
				public void onPressed(double d, double e) {
					DisconnectedGui.this.client.openGui(DisconnectedGui.this.parent);
				}
			}
		);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, this.height / 2 - this.reasonHeight / 2 - this.fontRenderer.fontHeight * 2, 11184810);
		int k = this.height / 2 - this.reasonHeight / 2;
		if (this.reasonFormatted != null) {
			for (String string : this.reasonFormatted) {
				this.drawStringCentered(this.fontRenderer, string, this.width / 2, k, 16777215);
				k += this.fontRenderer.fontHeight;
			}
		}

		super.draw(i, j, f);
	}
}
