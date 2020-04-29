package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private static final Text header = new TranslatableText("multiplayerWarning.header").formatted(Formatting.BOLD);
	private static final Text message = new TranslatableText("multiplayerWarning.message");
	private static final Text checkMessage = new TranslatableText("multiplayerWarning.check");
	private static final Text proceedText = header.shallowCopy().append("\n").append(message);
	private CheckboxWidget checkbox;
	private final List<Text> lines = Lists.<Text>newArrayList();

	public MultiplayerWarningScreen(Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.lines.clear();
		this.lines.addAll(this.textRenderer.wrapLines(message, this.width - 50));
		int i = (this.lines.size() + 1) * 9;
		this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, ScreenTexts.PROCEED, buttonWidget -> {
			if (this.checkbox.isChecked()) {
				this.client.options.skipMultiplayerWarning = true;
				this.client.options.write();
			}

			this.client.openScreen(new MultiplayerScreen(this.parent));
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
		this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, checkMessage, false);
		this.addButton(this.checkbox);
	}

	@Override
	public String getNarrationMessage() {
		return proceedText.getString();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.drawStringWithShadow(matrices, this.textRenderer, header, this.width / 2, 30, 16777215);
		int i = 70;

		for (Text text : this.lines) {
			this.drawStringWithShadow(matrices, this.textRenderer, text, this.width / 2, i, 16777215);
			i += 9;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
