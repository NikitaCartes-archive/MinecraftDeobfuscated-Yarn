package net.minecraft.client.gui.screen.option;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class TelemetryInfoScreen extends Screen {
	private static final int MARGIN = 8;
	private static final Text TITLE_TEXT = Text.translatable("telemetry_info.screen.title");
	private static final Text DESCRIPTION_TEXT = Text.translatable("telemetry_info.screen.description").formatted(Formatting.GRAY);
	private static final Text PRIVACY_STATEMENT_TEXT = Text.translatable("telemetry_info.button.privacy_statement");
	private static final Text GIVE_FEEDBACK_TEXT = Text.translatable("telemetry_info.button.give_feedback");
	private static final Text SHOW_DATA_TEXT = Text.translatable("telemetry_info.button.show_data");
	private static final Text OPT_IN_DESCRIPTION_TEXT = Text.translatable("telemetry_info.opt_in.description");
	private final Screen parent;
	private final GameOptions options;
	@Nullable
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
		DirectionalLayoutWidget directionalLayoutWidget = simplePositioningWidget.add(
			DirectionalLayoutWidget.vertical(), simplePositioningWidget.copyPositioner().relative(0.5F, 0.0F)
		);
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().marginBottom(8);
		directionalLayoutWidget.add(new TextWidget(this.getTitle(), this.textRenderer));
		directionalLayoutWidget.add(new MultilineTextWidget(DESCRIPTION_TEXT, this.textRenderer).setMaxWidth(this.width - 16).setCentered(true));
		GridWidget gridWidget = this.createButtonRow(
			ButtonWidget.builder(PRIVACY_STATEMENT_TEXT, this::openPrivacyStatementPage).build(),
			ButtonWidget.builder(GIVE_FEEDBACK_TEXT, this::openFeedbackPage).build()
		);
		directionalLayoutWidget.add(gridWidget);
		LayoutWidget layoutWidget = this.getLayout();
		simplePositioningWidget.refreshPositions();
		layoutWidget.refreshPositions();
		int i = gridWidget.getY() + gridWidget.getHeight();
		int j = layoutWidget.getHeight();
		int k = this.height - i - j - 16;
		this.telemetryEventWidget = new TelemetryEventWidget(0, 0, this.width - 40, k, this.client.textRenderer);
		this.telemetryEventWidget.setScrollY(this.scroll);
		this.telemetryEventWidget.setScrollConsumer(scroll -> this.scroll = scroll);
		directionalLayoutWidget.add(this.telemetryEventWidget);
		directionalLayoutWidget.add(layoutWidget);
		simplePositioningWidget.refreshPositions();
		SimplePositioningWidget.setPos(simplePositioningWidget, 0, 0, this.width, this.height, 0.5F, 0.0F);
		simplePositioningWidget.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.telemetryEventWidget);
	}

	private LayoutWidget getLayout() {
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical();
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().marginBottom(4);
		if (this.client.isOptionalTelemetryEnabledByApi()) {
			directionalLayoutWidget.add(this.createOptInCheckbox());
		}

		directionalLayoutWidget.add(
			this.createButtonRow(ButtonWidget.builder(SHOW_DATA_TEXT, this::openLogDirectory).build(), ButtonWidget.builder(ScreenTexts.DONE, this::goBack).build())
		);
		return directionalLayoutWidget;
	}

	private ClickableWidget createOptInCheckbox() {
		SimpleOption<Boolean> simpleOption = this.options.getTelemetryOptInExtra();
		CheckboxWidget checkboxWidget = CheckboxWidget.builder(OPT_IN_DESCRIPTION_TEXT, this.client.textRenderer)
			.option(simpleOption)
			.callback(this::updateOptIn)
			.build();
		checkboxWidget.active = this.client.isOptionalTelemetryEnabledByApi();
		return checkboxWidget;
	}

	private void updateOptIn(ClickableWidget checkbox, boolean checked) {
		if (this.telemetryEventWidget != null) {
			this.telemetryEventWidget.refresh(checked);
		}
	}

	private void goBack(ButtonWidget button) {
		this.client.setScreen(this.parent);
	}

	private void openPrivacyStatementPage(ButtonWidget button) {
		ConfirmLinkScreen.open(this, "http://go.microsoft.com/fwlink/?LinkId=521839");
	}

	private void openFeedbackPage(ButtonWidget button) {
		ConfirmLinkScreen.open(this, "https://aka.ms/javafeedback?ref=game");
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
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
	}

	private GridWidget createButtonRow(ClickableWidget left, ClickableWidget right) {
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().alignHorizontalCenter().marginX(4);
		gridWidget.add(left, 0, 0);
		gridWidget.add(right, 0, 1);
		return gridWidget;
	}
}
