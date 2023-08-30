package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.SkinReportScreen;
import net.minecraft.client.util.SkinTextures;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class SkinAbuseReport extends AbuseReport {
	final Supplier<SkinTextures> skinSupplier;

	SkinAbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid, Supplier<SkinTextures> skinSupplier) {
		super(reportId, currentTime, reportedPlayerUuid);
		this.skinSupplier = skinSupplier;
	}

	public Supplier<SkinTextures> getSkinSupplier() {
		return this.skinSupplier;
	}

	public SkinAbuseReport copy() {
		SkinAbuseReport skinAbuseReport = new SkinAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid, this.skinSupplier);
		skinAbuseReport.opinionComments = this.opinionComments;
		skinAbuseReport.reason = this.reason;
		return skinAbuseReport;
	}

	@Override
	public Screen createReportScreen(Screen parent, AbuseReportContext context) {
		return new SkinReportScreen(parent, context, this);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder extends AbuseReport.Builder<SkinAbuseReport> {
		public Builder(SkinAbuseReport report, AbuseReportLimits limits) {
			super(report, limits);
		}

		public Builder(UUID reportedPlayerUuid, Supplier<SkinTextures> skinSupplier, AbuseReportLimits limits) {
			super(new SkinAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, skinSupplier), limits);
		}

		@Override
		public boolean hasEnoughInfo() {
			return StringUtils.isNotEmpty(this.getOpinionComments()) || this.getReason() != null;
		}

		@Nullable
		@Override
		public AbuseReport.ValidationError validate() {
			if (this.report.reason == null) {
				return AbuseReport.ValidationError.NO_REASON;
			} else {
				return this.report.opinionComments.length() > this.limits.maxOpinionCommentsLength() ? AbuseReport.ValidationError.COMMENTS_TOO_LONG : null;
			}
		}

		@Override
		public Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context) {
			AbuseReport.ValidationError validationError = this.validate();
			if (validationError != null) {
				return Either.right(validationError);
			} else {
				String string = ((AbuseReportReason)Objects.requireNonNull(this.report.reason)).getId();
				ReportedEntity reportedEntity = new ReportedEntity(this.report.reportedPlayerUuid);
				SkinTextures skinTextures = (SkinTextures)this.report.skinSupplier.get();
				String string2 = skinTextures.textureUrl();
				com.mojang.authlib.minecraft.report.AbuseReport abuseReport = com.mojang.authlib.minecraft.report.AbuseReport.skin(
					this.report.opinionComments, string, string2, reportedEntity, this.report.currentTime
				);
				return Either.left(new AbuseReport.ReportWithId(this.report.reportId, AbuseReportType.SKIN, abuseReport));
			}
		}
	}
}
