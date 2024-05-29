package net.minecraft.client.gui.screen.world;

import java.net.URI;
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
import net.minecraft.util.Urls;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SymlinkWarningScreen extends Screen {
	private static final Text WORLD_TITLE = Text.translatable("symlink_warning.title.world").formatted(Formatting.BOLD);
	private static final Text WORLD_MESSAGE = Text.translatable("symlink_warning.message.world", Text.of(Urls.MINECRAFT_SYMLINKS));
	private static final Text PACK_TITLE = Text.translatable("symlink_warning.title.pack").formatted(Formatting.BOLD);
	private static final Text PACK_MESSAGE = Text.translatable("symlink_warning.message.pack", Text.of(Urls.MINECRAFT_SYMLINKS));
	private final Text message;
	private final URI link;
	private final Runnable onClose;
	private final GridWidget grid = new GridWidget().setRowSpacing(10);

	public SymlinkWarningScreen(Text title, Text message, URI link, Runnable onClose) {
		super(title);
		this.message = message;
		this.link = link;
		this.onClose = onClose;
	}

	public static Screen world(Runnable onClose) {
		return new SymlinkWarningScreen(WORLD_TITLE, WORLD_MESSAGE, Urls.MINECRAFT_SYMLINKS, onClose);
	}

	public static Screen pack(Runnable onClose) {
		return new SymlinkWarningScreen(PACK_TITLE, PACK_MESSAGE, Urls.MINECRAFT_SYMLINKS, onClose);
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
		adder2.add(ButtonWidget.builder(ScreenTexts.COPY_LINK_TO_CLIPBOARD, button -> this.client.keyboard.setClipboard(this.link.toString())).size(120, 20).build());
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
		this.onClose.run();
	}
}
