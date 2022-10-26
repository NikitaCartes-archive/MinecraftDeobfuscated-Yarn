package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class SimpleOptionsScreen extends GameOptionsScreen {
	protected final SimpleOption<?>[] options;
	@Nullable
	private ClickableWidget narratorButton;
	private ButtonListWidget buttonList;

	public SimpleOptionsScreen(Screen parent, GameOptions gameOptions, Text title, SimpleOption<?>[] options) {
		super(parent, gameOptions, title);
		this.options = options;
	}

	@Override
	protected void init() {
		this.buttonList = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.buttonList.addAll(this.options);
		this.addSelectableChild(this.buttonList);
		this.initFooter();
		this.narratorButton = this.buttonList.getButtonFor(this.gameOptions.getNarrator());
		if (this.narratorButton != null) {
			this.narratorButton.active = this.client.getNarratorManager().isActive();
		}
	}

	protected void initFooter() {
		this.addDrawableChild(
			ButtonWidget.createBuilder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent))
				.setPositionAndSize(this.width / 2 - 100, this.height - 27, 200, 20)
				.build()
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.render(matrices, this.buttonList, mouseX, mouseY, delta);
	}

	public void updateNarratorButtonText() {
		if (this.narratorButton instanceof CyclingButtonWidget) {
			((CyclingButtonWidget)this.narratorButton).setValue(this.gameOptions.getNarrator().getValue());
		}
	}
}
