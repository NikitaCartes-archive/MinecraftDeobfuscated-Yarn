package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GraphicsConfirmationScreen extends Screen {
	private final StringRenderable message;
	private final ImmutableList<GraphicsConfirmationScreen.ChoiceButton> choiceButtons;
	private List<StringRenderable> lines;
	private int field_25678;
	private int field_25679;

	protected GraphicsConfirmationScreen(Text title, List<StringRenderable> messageParts, ImmutableList<GraphicsConfirmationScreen.ChoiceButton> choiceButtons) {
		super(title);
		this.message = StringRenderable.concat(messageParts);
		this.choiceButtons = choiceButtons;
	}

	@Override
	public String getNarrationMessage() {
		return super.getNarrationMessage() + ". " + this.message.getString();
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);

		for (GraphicsConfirmationScreen.ChoiceButton choiceButton : this.choiceButtons) {
			this.field_25679 = Math.max(this.field_25679, 20 + this.textRenderer.getWidth(choiceButton.message) + 20);
		}

		int i = 5 + this.field_25679 + 5;
		int j = i * this.choiceButtons.size();
		this.lines = this.textRenderer.wrapLines(this.message, j);
		int k = this.lines.size() * 9;
		this.field_25678 = (int)((double)height / 2.0 - (double)k / 2.0);
		int l = this.field_25678 + k + 9 * 2;
		int m = (int)((double)width / 2.0 - (double)j / 2.0);

		for (GraphicsConfirmationScreen.ChoiceButton choiceButton2 : this.choiceButtons) {
			this.addButton(new ButtonWidget(m, l, this.field_25679, 20, choiceButton2.message, choiceButton2.pressAction));
			m += i;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.field_25678 - 9 * 2, -1);
		int i = this.field_25678;

		for (StringRenderable stringRenderable : this.lines) {
			this.drawCenteredText(matrices, this.textRenderer, stringRenderable, this.width / 2, i, -1);
			i += 9;
		}

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
