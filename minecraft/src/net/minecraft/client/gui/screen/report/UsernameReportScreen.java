package net.minecraft.client.gui.screen.report;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.UsernameAbuseReport;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class UsernameReportScreen extends ReportScreen<UsernameAbuseReport.Builder> {
	private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.name.title");
	private EditBoxWidget commentsBox;

	private UsernameReportScreen(Screen parent, AbuseReportContext context, UsernameAbuseReport.Builder reportBuilder) {
		super(TITLE_TEXT, parent, context, reportBuilder);
	}

	public UsernameReportScreen(Screen parent, AbuseReportContext context, UUID reportedPlayerUuid, String username) {
		this(parent, context, new UsernameAbuseReport.Builder(reportedPlayerUuid, username, context.getSender().getLimits()));
	}

	public UsernameReportScreen(Screen parent, AbuseReportContext context, UsernameAbuseReport report) {
		this(parent, context, new UsernameAbuseReport.Builder(report, context.getSender().getLimits()));
	}

	@Override
	protected void addContent() {
		Text text = Text.literal(this.reportBuilder.getReport().getUsername()).formatted(Formatting.YELLOW);
		this.layout
			.add(new TextWidget(Text.translatable("gui.abuseReport.name.reporting", text), this.textRenderer), positioner -> positioner.alignLeft().margin(0, 8));
		this.commentsBox = this.createCommentsBox(280, 9 * 8, comments -> {
			this.reportBuilder.setOpinionComments(comments);
			this.onChange();
		});
		this.layout.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.commentsBox, MORE_COMMENTS_TEXT, positioner -> positioner.marginBottom(12)));
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button) ? true : this.commentsBox.mouseReleased(mouseX, mouseY, button);
	}
}
