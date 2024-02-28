package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class OutOfMemoryScreen extends Screen {
	private static final Text TITLE = Text.translatable("outOfMemory.title");
	private static final Text MESSAGE = Text.translatable("outOfMemory.message");
	private static final int MAX_TEXT_WIDTH = 300;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public OutOfMemoryScreen() {
		super(TITLE);
	}

	@Override
	protected void init() {
		this.layout.addHeader(TITLE, this.textRenderer);
		this.layout.addBody(new NarratedMultilineTextWidget(300, MESSAGE, this.textRenderer));
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.TO_TITLE, button -> this.client.setScreen(new TitleScreen())).build());
		directionalLayoutWidget.add(ButtonWidget.builder(Text.translatable("menu.quit"), button -> this.client.scheduleStop()).build());
		this.layout.forEachChild(this::addDrawableChild);
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
