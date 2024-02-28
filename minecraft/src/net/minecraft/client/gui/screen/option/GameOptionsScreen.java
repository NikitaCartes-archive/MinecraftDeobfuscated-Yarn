package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GameOptionsScreen extends Screen {
	protected final Screen parent;
	protected final GameOptions gameOptions;
	public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
		super(title);
		this.parent = parent;
		this.gameOptions = gameOptions;
	}

	@Override
	protected void init() {
		this.initHeader();
		this.initFooter();
		this.layout.forEachChild(this::addDrawableChild);
		this.initTabNavigation();
	}

	protected void initHeader() {
		this.layout.addHeader(this.title, this.textRenderer);
	}

	protected void initFooter() {
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, buttonWidget -> this.close()).width(200).build());
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	@Override
	public void removed() {
		this.client.options.write();
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
