package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class class_5500 extends GameOptionsScreen {
	private final Option[] field_26679;
	@Nullable
	private AbstractButtonWidget field_26680;
	private ButtonListWidget field_26681;

	public class_5500(Screen screen, GameOptions gameOptions, Text text, Option[] options) {
		super(screen, gameOptions, text);
		this.field_26679 = options;
	}

	@Override
	protected void init() {
		this.field_26681 = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.field_26681.addAll(this.field_26679);
		this.children.add(this.field_26681);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, buttonWidget -> this.client.openScreen(this.parent)));
		this.field_26680 = this.field_26681.method_31046(Option.NARRATOR);
		if (this.field_26680 != null) {
			this.field_26680.active = NarratorManager.INSTANCE.isActive();
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.field_26681.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		List<OrderedText> list = method_31048(this.field_26681, mouseX, mouseY);
		if (list != null) {
			this.renderTooltip(matrices, list, mouseX, mouseY);
		}
	}

	public void method_31050() {
		if (this.field_26680 != null) {
			this.field_26680.setMessage(Option.NARRATOR.getMessage(this.gameOptions));
		}
	}
}
