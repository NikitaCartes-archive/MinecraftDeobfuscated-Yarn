package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends ConfirmScreen {
	private static final Text COPY = Text.translatable("chat.copy");
	private static final Text WARNING = Text.translatable("chat.link.warning");
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkScreen(BooleanConsumer callback, String link, boolean trusted) {
		super(callback, Text.translatable(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm"), Text.literal(link));
		this.yesTranslated = (Text)(trusted ? Text.translatable("chat.link.open") : ScreenTexts.YES);
		this.noTranslated = trusted ? ScreenTexts.CANCEL : ScreenTexts.NO;
		this.drawWarning = !trusted;
		this.link = link;
	}

	@Override
	protected void addButtons(int y) {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated, button -> this.callback.accept(true)));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height / 6 + 96, 100, 20, COPY, button -> {
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
			drawCenteredText(matrices, this.textRenderer, WARNING, this.width / 2, 110, 16764108);
		}
	}
}
