package net.minecraft.client.gui.screen.world;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SymlinkWarningScreen extends Screen {
	private static final Text TITLE = Text.translatable("symlink_warning.title").formatted(Formatting.BOLD);
	private static final Text MESSAGE = Text.translatable("symlink_warning.message", "https://aka.ms/MinecraftSymLinks");
	@Nullable
	private final Screen parent;
	private final GridWidget grid = new GridWidget().setRowSpacing(10);

	public SymlinkWarningScreen(@Nullable Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.grid.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = this.grid.createAdder(1);
		adder.add(new TextWidget(this.title, this.textRenderer));
		adder.add(new MultilineTextWidget(MESSAGE, this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
		int i = 120;
		GridWidget gridWidget = new GridWidget().setColumnSpacing(5);
		GridWidget.Adder adder2 = gridWidget.createAdder(3);
		adder2.add(ButtonWidget.builder(ScreenTexts.OPEN_LINK, button -> Util.getOperatingSystem().open("https://aka.ms/MinecraftSymLinks")).size(120, 20).build());
		adder2.add(
			ButtonWidget.builder(ScreenTexts.COPY_LINK_TO_CLIPBOARD, button -> this.client.keyboard.setClipboard("https://aka.ms/MinecraftSymLinks"))
				.size(120, 20)
				.build()
		);
		adder2.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).size(120, 20).build());
		adder.add(gridWidget);
		this.initTabNavigation();
		this.grid.forEachChild(this::addDrawableChild);
	}

	@Override
	protected void initTabNavigation() {
		this.grid.refreshPositions();
		SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), MESSAGE);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
