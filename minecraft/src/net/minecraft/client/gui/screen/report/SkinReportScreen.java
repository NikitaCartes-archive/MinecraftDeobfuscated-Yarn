package net.minecraft.client.gui.screen.report;

import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.PlayerSkinWidget;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.SkinAbuseReport;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkinReportScreen extends ReportScreen<SkinAbuseReport.Builder> {
	private static final int SKIN_WIDGET_WIDTH = 85;
	private static final int REASON_BUTTON_AND_COMMENTS_BOX_WIDTH = 178;
	private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.skin.title");
	private EditBoxWidget commentsBox;
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
	protected void addContent() {
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
		this.commentsBox = this.createCommentsBox(178, 9 * 8, comments -> {
			this.reportBuilder.setOpinionComments(comments);
			this.onChange();
		});
		directionalLayoutWidget2.add(
			LayoutWidgets.createLabeledWidget(this.textRenderer, this.commentsBox, MORE_COMMENTS_TEXT, positioner -> positioner.marginBottom(12))
		);
	}

	@Override
	protected void onChange() {
		AbuseReportReason abuseReportReason = this.reportBuilder.getReason();
		if (abuseReportReason != null) {
			this.selectReasonButton.setMessage(abuseReportReason.getText());
		} else {
			this.selectReasonButton.setMessage(SELECT_REASON_TEXT);
		}

		super.onChange();
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button) ? true : this.commentsBox.mouseReleased(mouseX, mouseY, button);
	}
}
