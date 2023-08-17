package net.minecraft.client.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class AbuseReport {
	protected final UUID reportId;
	protected final Instant currentTime;
	protected final UUID reportedPlayerUuid;
	protected String opinionComments = "";
	@Nullable
	protected AbuseReportReason reason;

	public AbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid) {
		this.reportId = reportId;
		this.currentTime = currentTime;
		this.reportedPlayerUuid = reportedPlayerUuid;
	}

	public boolean playerUuidEquals(UUID uuid) {
		return uuid.equals(this.reportedPlayerUuid);
	}

	public abstract AbuseReport copy();

	public abstract Screen createReportScreen(Screen parent, AbuseReportContext context);

	@Environment(EnvType.CLIENT)
	public abstract static class Builder<R extends AbuseReport> {
		protected final R report;
		protected final AbuseReportLimits limits;

		protected Builder(R report, AbuseReportLimits limits) {
			this.report = report;
			this.limits = limits;
		}

		public R getReport() {
			return this.report;
		}

		public UUID getReportedPlayerUuid() {
			return this.report.reportedPlayerUuid;
		}

		public String getOpinionComments() {
			return this.report.opinionComments;
		}

		public void setOpinionComments(String opinionComments) {
			this.report.opinionComments = opinionComments;
		}

		@Nullable
		public AbuseReportReason getReason() {
			return this.report.reason;
		}

		public void setReason(AbuseReportReason reason) {
			this.report.reason = reason;
		}

		public abstract boolean hasEnoughInfo();

		@Nullable
		public abstract AbuseReport.ValidationError validate();

		public abstract Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context);
	}

	@Environment(EnvType.CLIENT)
	public static record ReportWithId(UUID id, AbuseReportType reportType, com.mojang.authlib.minecraft.report.AbuseReport report) {
	}

	@Environment(EnvType.CLIENT)
	public static record ValidationError(Text message) {
		public static final AbuseReport.ValidationError NO_REASON = new AbuseReport.ValidationError(Text.translatable("gui.abuseReport.send.no_reason"));
		public static final AbuseReport.ValidationError NO_REPORTED_MESSAGES = new AbuseReport.ValidationError(
			Text.translatable("gui.chatReport.send.no_reported_messages")
		);
		public static final AbuseReport.ValidationError TOO_MANY_MESSAGES = new AbuseReport.ValidationError(
			Text.translatable("gui.chatReport.send.too_many_messages")
		);
		public static final AbuseReport.ValidationError COMMENTS_TOO_LONG = new AbuseReport.ValidationError(
			Text.translatable("gui.abuseReport.send.comment_too_long")
		);

		public Tooltip createTooltip() {
			return Tooltip.of(this.message);
		}
	}
}
