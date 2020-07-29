package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DialogScreen extends Screen {
	private final StringVisitable message;
	private final ImmutableList<DialogScreen.ChoiceButton> choiceButtons;
	private class_5489 lines = class_5489.field_26528;
	private int linesY;
	private int buttonWidth;

	protected DialogScreen(Text title, List<StringVisitable> list, ImmutableList<DialogScreen.ChoiceButton> choiceButtons) {
		super(title);
		this.message = StringVisitable.concat(list);
		this.choiceButtons = choiceButtons;
	}

	@Override
	public String getNarrationMessage() {
		return super.getNarrationMessage() + ". " + this.message.getString();
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);

		for (DialogScreen.ChoiceButton choiceButton : this.choiceButtons) {
			this.buttonWidth = Math.max(this.buttonWidth, 20 + this.textRenderer.getWidth(choiceButton.message) + 20);
		}

		int i = 5 + this.buttonWidth + 5;
		int j = i * this.choiceButtons.size();
		this.lines = class_5489.method_30890(this.textRenderer, this.message, j);
		int k = this.lines.method_30887() * 9;
		this.linesY = (int)((double)height / 2.0 - (double)k / 2.0);
		int l = this.linesY + k + 9 * 2;
		int m = (int)((double)width / 2.0 - (double)j / 2.0);

		for (DialogScreen.ChoiceButton choiceButton2 : this.choiceButtons) {
			this.addButton(new ButtonWidget(m, l, this.buttonWidth, 20, choiceButton2.message, choiceButton2.pressAction));
			m += i;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.linesY - 9 * 2, -1);
		this.lines.method_30888(matrices, this.width / 2, this.linesY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static final class ChoiceButton {
		private final Text message;
		private final ButtonWidget.PressAction pressAction;

		public ChoiceButton(Text message, ButtonWidget.PressAction pressAction) {
			this.message = message;
			this.pressAction = pressAction;
		}
	}
}
