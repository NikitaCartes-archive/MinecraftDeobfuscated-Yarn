package net.minecraft.client.gui.screen.world;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private static final Text WORLD_TITLE = Text.translatable("symlink_warning.title.world").formatted(Formatting.BOLD);
	private static final Text WORLD_MESSAGE = Text.translatable("symlink_warning.message.world", "https://aka.ms/MinecraftSymLinks");
	private static final Text PACK_TITLE = Text.translatable("symlink_warning.title.pack").formatted(Formatting.BOLD);
	private static final Text PACK_MESSAGE = Text.translatable("symlink_warning.message.pack", "https://aka.ms/MinecraftSymLinks");
	private final Text message;
	private final String link;
	@Nullable
	private final Screen parent;
	private final GridWidget grid = new GridWidget().setRowSpacing(10);

	public SymlinkWarningScreen(Text title, Text message, String link, @Nullable Screen parent) {
		super(title);
		this.message = message;
		this.link = link;
		this.parent = parent;
	}

	public static Screen world(@Nullable Screen parent) {
		return new SymlinkWarningScreen(WORLD_TITLE, WORLD_MESSAGE, "https://aka.ms/MinecraftSymLinks", parent);
	}

	public static Screen pack(@Nullable Screen parent) {
		return new SymlinkWarningScreen(PACK_TITLE, PACK_MESSAGE, "https://aka.ms/MinecraftSymLinks", parent);
	}

	@Override
	protected void init() {
		super.init();
		this.grid.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = this.grid.createAdder(1);
		adder.add(new TextWidget(this.title, this.textRenderer));
		adder.add(new MultilineTextWidget(this.message, this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
		int i = 120;
		GridWidget gridWidget = new GridWidget().setColumnSpacing(5);
		GridWidget.Adder adder2 = gridWidget.createAdder(3);
		adder2.add(ButtonWidget.builder(ScreenTexts.OPEN_LINK, button -> Util.getOperatingSystem().open(this.link)).size(120, 20).build());
		adder2.add(ButtonWidget.builder(ScreenTexts.COPY_LINK_TO_CLIPBOARD, button -> this.client.keyboard.setClipboard(this.link)).size(120, 20).build());
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
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), this.message);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
