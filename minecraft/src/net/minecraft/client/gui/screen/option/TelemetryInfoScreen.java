package net.minecraft.client.gui.screen.option;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class TelemetryInfoScreen extends Screen {
	private static final int MARGIN = 8;
	private static final Text TITLE_TEXT = Text.translatable("telemetry_info.screen.title");
	private static final Text DESCRIPTION_TEXT = Text.translatable("telemetry_info.screen.description").formatted(Formatting.GRAY);
	private static final Text GIVE_FEEDBACK_TEXT = Text.translatable("telemetry_info.button.give_feedback");
	private static final Text SHOW_DATA_TEXT = Text.translatable("telemetry_info.button.show_data");
	private final Screen parent;
	private final GameOptions options;
	private TelemetryEventWidget telemetryEventWidget;
	private double scroll;

	public TelemetryInfoScreen(Screen parent, GameOptions options) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.options = options;
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), DESCRIPTION_TEXT);
	}

	@Override
	protected void init() {
		SimplePositioningWidget simplePositioningWidget = new SimplePositioningWidget();
		simplePositioningWidget.getMainPositioner().margin(8);
		simplePositioningWidget.setMinHeight(this.height);
		GridWidget gridWidget = simplePositioningWidget.add(new GridWidget(), simplePositioningWidget.copyPositioner().relative(0.5F, 0.0F));
		gridWidget.getMainPositioner().alignHorizontalCenter().marginBottom(8);
		GridWidget.Adder adder = gridWidget.createAdder(1);
		adder.add(new TextWidget(this.getTitle(), this.textRenderer));
		adder.add(new MultilineTextWidget(DESCRIPTION_TEXT, this.textRenderer).setMaxWidth(this.width - 16).setCentered(true));
		GridWidget gridWidget2 = this.createButtonRow(
			ButtonWidget.builder(GIVE_FEEDBACK_TEXT, this::openFeedbackPage).build(), ButtonWidget.builder(SHOW_DATA_TEXT, this::openLogDirectory).build()
		);
		adder.add(gridWidget2);
		GridWidget gridWidget3 = this.createButtonRow(this.createOptInButton(), ButtonWidget.builder(ScreenTexts.DONE, this::goBack).build());
		simplePositioningWidget.add(gridWidget3, simplePositioningWidget.copyPositioner().relative(0.5F, 1.0F));
		simplePositioningWidget.refreshPositions();
		this.telemetryEventWidget = new TelemetryEventWidget(
			0, 0, this.width - 40, gridWidget3.getY() - (gridWidget2.getY() + gridWidget2.getHeight()) - 16, this.client.textRenderer
		);
		this.telemetryEventWidget.setScrollY(this.scroll);
		this.telemetryEventWidget.setScrollConsumer(scroll -> this.scroll = scroll);
		this.setInitialFocus(this.telemetryEventWidget);
		adder.add(this.telemetryEventWidget);
		simplePositioningWidget.refreshPositions();
		SimplePositioningWidget.setPos(simplePositioningWidget, 0, 0, this.width, this.height, 0.5F, 0.0F);
		simplePositioningWidget.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
	}

	private ClickableWidget createOptInButton() {
		ClickableWidget clickableWidget = this.options
			.getTelemetryOptInExtra()
			.createWidget(this.options, 0, 0, 150, value -> this.telemetryEventWidget.refresh(value));
		clickableWidget.active = this.client.isOptionalTelemetryEnabledByApi();
		return clickableWidget;
	}

	private void goBack(ButtonWidget button) {
		this.client.setScreen(this.parent);
	}

	private void openFeedbackPage(ButtonWidget button) {
		this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
			if (confirmed) {
				Util.getOperatingSystem().open("https://aka.ms/javafeedback?ref=game");
			}

			this.client.setScreen(this);
		}, "https://aka.ms/javafeedback?ref=game", true));
	}

	private void openLogDirectory(ButtonWidget button) {
		Path path = this.client.getTelemetryManager().getLogManager();
		Util.getOperatingSystem().open(path.toUri());
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
		super.render(context, mouseX, mouseY, delta);
	}

	private GridWidget createButtonRow(ClickableWidget left, ClickableWidget right) {
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().alignHorizontalCenter().marginX(4);
		gridWidget.add(left, 0, 0);
		gridWidget.add(right, 0, 1);
		return gridWidget;
	}
}
