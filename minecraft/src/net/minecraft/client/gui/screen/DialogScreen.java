package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public class DialogScreen extends Screen {
	private static final int BUTTON_PADDING = 20;
	private static final int BUTTON_MARGIN = 5;
	private static final int BUTTON_HEIGHT = 20;
	private final Text narrationMessage;
	private final List<Text> message;
	private final ImmutableList<DialogScreen.ChoiceButton> choiceButtons;
	private MultilineText lines = MultilineText.EMPTY;
	private int linesY;
	private int buttonWidth;

	public DialogScreen(Text title, List<Text> messages, ImmutableList<DialogScreen.ChoiceButton> choiceButtons) {
		super(title);
		this.message = messages;
		this.narrationMessage = ScreenTexts.joinSentences(title, Texts.join(messages, ScreenTexts.EMPTY));
		this.choiceButtons = choiceButtons;
	}

	@Override
	public Text getNarratedTitle() {
		return this.narrationMessage;
	}

	@Override
	public void init() {
		for (DialogScreen.ChoiceButton choiceButton : this.choiceButtons) {
			this.buttonWidth = Math.max(this.buttonWidth, 20 + this.textRenderer.getWidth(choiceButton.message) + 20);
		}

		int i = 5 + this.buttonWidth + 5;
		int j = i * this.choiceButtons.size();
		this.lines = MultilineText.create(this.textRenderer, j, (Text[])this.message.toArray(new Text[0]));
		int k = this.lines.count() * 9;
		this.linesY = (int)((double)this.height / 2.0 - (double)k / 2.0);
		int l = this.linesY + k + 9 * 2;
		int m = (int)((double)this.width / 2.0 - (double)j / 2.0);

		for (DialogScreen.ChoiceButton choiceButton2 : this.choiceButtons) {
			this.addDrawableChild(ButtonWidget.builder(choiceButton2.message, choiceButton2.pressAction).dimensions(m, l, this.buttonWidth, 20).build());
			m += i;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.linesY - 9 * 2, Colors.WHITE);
		this.lines.drawCenterWithShadow(context, this.width / 2, this.linesY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static final class ChoiceButton {
		final Text message;
		final ButtonWidget.PressAction pressAction;

		public ChoiceButton(Text message, ButtonWidget.PressAction pressAction) {
			this.message = message;
			this.pressAction = pressAction;
		}
	}
}
