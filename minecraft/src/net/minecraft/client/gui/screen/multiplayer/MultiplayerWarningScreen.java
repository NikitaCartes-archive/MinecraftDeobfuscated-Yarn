package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class MultiplayerWarningScreen extends Screen {
	private final Screen parent;
	private static final Text HEADER = new TranslatableText("multiplayerWarning.header").formatted(Formatting.BOLD);
	private static final Text MESSAGE = new TranslatableText("multiplayerWarning.message");
	private static final Text CHECK_MESSAGE = new TranslatableText("multiplayerWarning.check");
	private static final Text PROCEED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);
	private CheckboxWidget checkbox;
	private MultilineText lines = MultilineText.EMPTY;

	public MultiplayerWarningScreen(Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.lines = MultilineText.create(this.textRenderer, MESSAGE, this.width - 50);
		int i = (this.lines.count() + 1) * 9 * 2;
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, ScreenTexts.PROCEED, button -> {
			if (this.checkbox.isChecked()) {
				this.client.options.skipMultiplayerWarning = true;
				this.client.options.write();
			}

			this.client.setScreen(new MultiplayerScreen(this.parent));
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
		this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, CHECK_MESSAGE, false);
		this.addDrawableChild(this.checkbox);
	}

	@Override
	public Text getNarratedTitle() {
		return PROCEED_TEXT;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawTextWithShadow(matrices, this.textRenderer, HEADER, 25, 30, 16777215);
		this.lines.drawWithShadow(matrices, 25, 70, 9 * 2, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
