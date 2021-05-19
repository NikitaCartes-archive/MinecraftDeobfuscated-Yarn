package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends ConfirmScreen {
	private static final Text copy = new TranslatableText("chat.copy");
	private static final Text warning = new TranslatableText("chat.link.warning");
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkScreen(BooleanConsumer callback, String link, boolean trusted) {
		super(callback, new TranslatableText(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm"), new LiteralText(link));
		this.yesTranslated = (Text)(trusted ? new TranslatableText("chat.link.open") : ScreenTexts.YES);
		this.noTranslated = trusted ? ScreenTexts.CANCEL : ScreenTexts.NO;
		this.drawWarning = !trusted;
		this.link = link;
	}

	@Override
	protected void method_37051(int i) {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated, button -> this.callback.accept(true)));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height / 6 + 96, 100, 20, copy, button -> {
			this.copyToClipboard();
			this.callback.accept(false);
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noTranslated, button -> this.callback.accept(false)));
	}

	public void copyToClipboard() {
		this.client.keyboard.setClipboard(this.link);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		if (this.drawWarning) {
			drawCenteredText(matrices, this.textRenderer, warning, this.width / 2, 110, 16764108);
		}
	}
}
