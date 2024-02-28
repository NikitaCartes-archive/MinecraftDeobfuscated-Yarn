package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class SimpleOptionsScreen extends GameOptionsScreen {
	protected final SimpleOption<?>[] options;
	@Nullable
	private ClickableWidget narratorButton;
	protected OptionListWidget buttonList;

	public SimpleOptionsScreen(Screen parent, GameOptions gameOptions, Text title, SimpleOption<?>[] options) {
		super(parent, gameOptions, title);
		this.options = options;
	}

	@Override
	protected void init() {
		this.buttonList = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height, this));
		this.buttonList.addAll(this.options);
		this.narratorButton = this.buttonList.getWidgetFor(this.gameOptions.getNarrator());
		if (this.narratorButton != null) {
			this.narratorButton.active = this.client.getNarratorManager().isActive();
		}

		super.init();
	}

	@Override
	protected void initTabNavigation() {
		super.initTabNavigation();
		this.buttonList.position(this.width, this.layout);
	}

	public void updateNarratorButtonText() {
		if (this.narratorButton instanceof CyclingButtonWidget) {
			((CyclingButtonWidget)this.narratorButton).setValue(this.gameOptions.getNarrator().getValue());
		}
	}
}
