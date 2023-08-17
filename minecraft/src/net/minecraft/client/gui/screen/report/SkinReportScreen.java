package net.minecraft.client.gui.screen.report;

import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.PlayerSkinWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.report.AbuseReport;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.client.report.SkinAbuseReport;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;

@Environment(EnvType.CLIENT)
public class SkinReportScreen extends ReportScreen<SkinAbuseReport.Builder> {
	private static final int BOTTOM_BUTTON_WIDTH = 120;
	private static final int SKIN_WIDGET_WIDTH = 85;
	private static final int REASON_BUTTON_AND_COMMENTS_BOX_WIDTH = 178;
	private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.skin.title");
	private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(8);
	private EditBoxWidget commentsBox;
	private ButtonWidget sendButton;
	private ButtonWidget selectReasonButton;

	private SkinReportScreen(Screen parent, AbuseReportContext context, SkinAbuseReport.Builder reportBuilder) {
		super(TITLE_TEXT, parent, context, reportBuilder);
	}

	public SkinReportScreen(Screen parent, AbuseReportContext context, UUID reportedPlayerUuid, Supplier<SkinTextures> skinSupplier) {
		this(parent, context, new SkinAbuseReport.Builder(reportedPlayerUuid, skinSupplier, context.getSender().getLimits()));
	}

	public SkinReportScreen(Screen parent, AbuseReportContext context, SkinAbuseReport report) {
		this(parent, context, new SkinAbuseReport.Builder(report, context.getSender().getLimits()));
	}

	@Override
	protected void init() {
		this.layout.getMainPositioner().alignHorizontalCenter();
		this.layout.add(new TextWidget(this.title, this.textRenderer));
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.add(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
		directionalLayoutWidget.add(new PlayerSkinWidget(85, 120, this.client.getEntityModelLoader(), this.reportBuilder.getReport().getSkinSupplier()));
		DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.vertical().spacing(8));
		this.selectReasonButton = ButtonWidget.builder(
				SELECT_REASON_TEXT, button -> this.client.setScreen(new AbuseReportReasonScreen(this, this.reportBuilder.getReason(), reason -> {
						this.reportBuilder.setReason(reason);
						this.onChange();
					}))
			)
			.width(178)
			.build();
		directionalLayoutWidget2.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.selectReasonButton, OBSERVED_WHAT_TEXT));
		this.commentsBox = this.createCommentsBox(178, 9 * 8, opinionComments -> {
			this.reportBuilder.setOpinionComments(opinionComments);
			this.onChange();
		});
		directionalLayoutWidget2.add(
			LayoutWidgets.createLabeledWidget(this.textRenderer, this.commentsBox, MORE_COMMENTS_TEXT, positioner -> positioner.marginBottom(12))
		);
		DirectionalLayoutWidget directionalLayoutWidget3 = this.layout.add(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget3.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(120).build());
		this.sendButton = directionalLayoutWidget3.add(ButtonWidget.builder(SEND_TEXT, button -> this.trySend()).width(120).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
		this.onChange();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
	}

	private void onChange() {
		AbuseReportReason abuseReportReason = this.reportBuilder.getReason();
		if (abuseReportReason != null) {
			this.selectReasonButton.setMessage(abuseReportReason.getText());
		} else {
			this.selectReasonButton.setMessage(SELECT_REASON_TEXT);
		}

		AbuseReport.ValidationError validationError = this.reportBuilder.validate();
		this.sendButton.active = validationError == null;
		this.sendButton.setTooltip(Nullables.map(validationError, AbuseReport.ValidationError::createTooltip));
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button) ? true : this.commentsBox.mouseReleased(mouseX, mouseY, button);
	}
}
