package net.minecraft.client.gui.screen;

import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9812;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen {
	private static final Text TO_MENU_TEXT = Text.translatable("gui.toMenu");
	private static final Text TO_TITLE_TEXT = Text.translatable("gui.toTitle");
	private static final Text field_52129 = Text.translatable("gui.report_to_server");
	private static final Text field_52130 = Text.translatable("gui.open_report_dir");
	private final Screen parent;
	private final class_9812 field_52131;
	private final Text buttonLabel;
	private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical();

	public DisconnectedScreen(Screen screen, Text text, Text text2) {
		this(screen, text, new class_9812(text2));
	}

	public DisconnectedScreen(Screen screen, Text text, Text text2, Text text3) {
		this(screen, text, new class_9812(text2), text3);
	}

	public DisconnectedScreen(Screen parent, Text title, class_9812 arg) {
		this(parent, title, arg, TO_MENU_TEXT);
	}

	public DisconnectedScreen(Screen parent, Text title, class_9812 arg, Text buttonLabel) {
		super(title);
		this.parent = parent;
		this.field_52131 = arg;
		this.buttonLabel = buttonLabel;
	}

	@Override
	protected void init() {
		this.grid.getMainPositioner().alignHorizontalCenter().margin(10);
		this.grid.add(new TextWidget(this.title, this.textRenderer));
		this.grid.add(new MultilineTextWidget(this.field_52131.reason(), this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
		this.grid.getMainPositioner().margin(2);
		this.field_52131
			.bugReportLink()
			.ifPresent(string -> this.grid.add(ButtonWidget.builder(field_52129, ConfirmLinkScreen.method_60867(this, string, false)).width(200).build()));
		this.field_52131.report().ifPresent(path -> {
			URI uRI = path.getParent().toUri();
			this.grid.add(ButtonWidget.builder(field_52130, buttonWidgetx -> Util.getOperatingSystem().open(uRI)).width(200).build());
		});
		ButtonWidget buttonWidget;
		if (this.client.isMultiplayerEnabled()) {
			buttonWidget = ButtonWidget.builder(this.buttonLabel, button -> this.client.setScreen(this.parent)).width(200).build();
		} else {
			buttonWidget = ButtonWidget.builder(TO_TITLE_TEXT, button -> this.client.setScreen(new TitleScreen())).width(200).build();
		}

		this.grid.add(buttonWidget);
		this.grid.refreshPositions();
		this.grid.forEachChild(this::addDrawableChild);
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(this.title, this.field_52131.reason());
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
