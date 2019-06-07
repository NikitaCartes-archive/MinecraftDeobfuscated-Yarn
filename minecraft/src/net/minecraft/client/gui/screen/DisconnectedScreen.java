package net.minecraft.client.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen {
	private final Text reason;
	private List<String> reasonFormatted;
	private final Screen parent;
	private int reasonHeight;

	public DisconnectedScreen(Screen screen, String string, Text text) {
		super(new TranslatableText(string));
		this.parent = screen;
		this.reason = text;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.reasonFormatted = this.font.wrapStringToWidthAsList(this.reason.asFormattedString(), this.width - 50);
		this.reasonHeight = this.reasonFormatted.size() * 9;
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30),
				200,
				20,
				I18n.translate("gui.toMenu"),
				buttonWidget -> this.minecraft.openScreen(this.parent)
			)
		);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, this.height / 2 - this.reasonHeight / 2 - 9 * 2, 11184810);
		int k = this.height / 2 - this.reasonHeight / 2;
		if (this.reasonFormatted != null) {
			for (String string : this.reasonFormatted) {
				this.drawCenteredString(this.font, string, this.width / 2, k, 16777215);
				k += 9;
			}
		}

		super.render(i, j, f);
	}
}
