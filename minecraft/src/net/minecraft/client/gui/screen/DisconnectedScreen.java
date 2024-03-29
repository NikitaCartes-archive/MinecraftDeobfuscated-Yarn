package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen {
	private static final Text TO_MENU_TEXT = Text.translatable("gui.toMenu");
	private static final Text TO_TITLE_TEXT = Text.translatable("gui.toTitle");
	private final Screen parent;
	private final Text reason;
	private final Text buttonLabel;
	private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical();

	public DisconnectedScreen(Screen parent, Text title, Text reason) {
		this(parent, title, reason, TO_MENU_TEXT);
	}

	public DisconnectedScreen(Screen parent, Text title, Text reason, Text buttonLabel) {
		super(title);
		this.parent = parent;
		this.reason = reason;
		this.buttonLabel = buttonLabel;
	}

	@Override
	protected void init() {
		this.grid.getMainPositioner().alignHorizontalCenter().margin(10);
		this.grid.add(new TextWidget(this.title, this.textRenderer));
		this.grid.add(new MultilineTextWidget(this.reason, this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
		ButtonWidget buttonWidget;
		if (this.client.isMultiplayerEnabled()) {
			buttonWidget = ButtonWidget.builder(this.buttonLabel, button -> this.client.setScreen(this.parent)).build();
		} else {
			buttonWidget = ButtonWidget.builder(TO_TITLE_TEXT, button -> this.client.setScreen(new TitleScreen())).build();
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
		return ScreenTexts.joinSentences(this.title, this.reason);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
