package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.UsernameReportScreen;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class UsernameAbuseReport extends AbuseReport {
	private final String username;

	UsernameAbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid, String username) {
		super(reportId, currentTime, reportedPlayerUuid);
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public UsernameAbuseReport copy() {
		UsernameAbuseReport usernameAbuseReport = new UsernameAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid, this.username);
		usernameAbuseReport.opinionComments = this.opinionComments;
		return usernameAbuseReport;
	}

	@Override
	public Screen createReportScreen(Screen parent, AbuseReportContext context) {
		return new UsernameReportScreen(parent, context, this);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder extends AbuseReport.Builder<UsernameAbuseReport> {
		public Builder(UsernameAbuseReport report, AbuseReportLimits limits) {
			super(report, limits);
		}

		public Builder(UUID reportedPlayerUuid, String username, AbuseReportLimits limits) {
			super(new UsernameAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, username), limits);
		}

		@Override
		public boolean hasEnoughInfo() {
			return StringUtils.isNotEmpty(this.getOpinionComments());
		}

		@Nullable
		@Override
		public AbuseReport.ValidationError validate() {
			return this.report.opinionComments.length() > this.limits.maxOpinionCommentsLength() ? AbuseReport.ValidationError.COMMENTS_TOO_LONG : null;
		}

		@Override
		public Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context) {
			AbuseReport.ValidationError validationError = this.validate();
			if (validationError != null) {
				return Either.right(validationError);
			} else {
				ReportedEntity reportedEntity = new ReportedEntity(this.report.reportedPlayerUuid);
				com.mojang.authlib.minecraft.report.AbuseReport abuseReport = com.mojang.authlib.minecraft.report.AbuseReport.name(
					this.report.opinionComments, reportedEntity, this.report.currentTime
				);
				return Either.left(new AbuseReport.ReportWithId(this.report.reportId, AbuseReportType.USERNAME, abuseReport));
			}
		}
	}
}
