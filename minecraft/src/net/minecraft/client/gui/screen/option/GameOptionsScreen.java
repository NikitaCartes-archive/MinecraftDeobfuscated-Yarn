package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class GameOptionsScreen extends Screen {
	protected final Screen parent;
	protected final GameOptions gameOptions;
	@Nullable
	protected OptionListWidget body;
	public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
		super(title);
		this.parent = parent;
		this.gameOptions = gameOptions;
	}

	@Override
	protected void init() {
		this.initHeader();
		this.initBody();
		this.initFooter();
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	protected void initHeader() {
		this.layout.addHeader(this.title, this.textRenderer);
	}

	protected void initBody() {
		this.body = this.layout.addBody(new OptionListWidget(this.client, this.width, this));
		this.addOptions();
		if (this.body.getWidgetFor(this.gameOptions.getNarrator()) instanceof CyclingButtonWidget cyclingButtonWidget) {
			this.narratorToggleButton = cyclingButtonWidget;
			this.narratorToggleButton.active = this.client.getNarratorManager().isActive();
		}
	}

	protected abstract void addOptions();

	protected void initFooter() {
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		if (this.body != null) {
			this.body.position(this.width, this.layout);
		}
	}

	@Override
	public void removed() {
		this.client.options.write();
	}

	@Override
	public void close() {
		if (this.body != null) {
			this.body.applyAllPendingValues();
		}

		this.client.setScreen(this.parent);
	}
}
