package net.minecraft.client.gui.screen.report;

import java.util.UUID;
import javax.annotation.Nullable;
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
	private static final Text field_52851 = Text.translatable("gui.abuseReport.name.comment_box_label");
	@Nullable
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
			.add(
				new TextWidget(Text.translatable("gui.abuseReport.name.reporting", text), this.textRenderer), positioner -> positioner.alignHorizontalCenter().margin(0, 8)
			);
		this.commentsBox = this.createCommentsBox(280, 9 * 8, comments -> {
			this.reportBuilder.setOpinionComments(comments);
			this.onChange();
		});
		this.layout.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.commentsBox, field_52851, positioner -> positioner.marginBottom(12)));
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (super.mouseReleased(mouseX, mouseY, button)) {
			return true;
		} else {
			return this.commentsBox != null ? this.commentsBox.mouseReleased(mouseX, mouseY, button) : false;
		}
	}
}
