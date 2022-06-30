package net.minecraft.client.report;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.datafixers.util.Unit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public interface AbuseReportSender {
	static AbuseReportSender create(ReporterEnvironment environment, UserApiService userApiService) {
		return new AbuseReportSender.Impl(environment, userApiService);
	}

	CompletableFuture<Unit> send(UUID id, AbuseReport report);

	boolean canSendReports();

	default AbuseReportLimits getLimits() {
		return AbuseReportLimits.DEFAULTS;
	}

	@Environment(EnvType.CLIENT)
	public static class AbuseReportException extends TextifiedException {
		public AbuseReportException(Text text, Throwable throwable) {
			super(text, throwable);
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Impl(ReporterEnvironment environment, UserApiService userApiService) implements AbuseReportSender {
		private static final Text SERVICE_UNAVAILABLE_ERROR_TEXT = Text.translatable("gui.abuseReport.send.service_unavailable");
		private static final Text HTTP_ERROR_TEXT = Text.translatable("gui.abuseReport.send.http_error");
		private static final Text JSON_ERROR_TEXT = Text.translatable("gui.abuseReport.send.json_error");

		@Override
		public CompletableFuture<Unit> send(UUID id, AbuseReport report) {
			return CompletableFuture.supplyAsync(
				() -> {
					AbuseReportRequest abuseReportRequest = new AbuseReportRequest(
						id, report, this.environment.toClientInfo(), this.environment.toThirdPartyServerInfo(), this.environment.toRealmInfo()
					);

					try {
						this.userApiService.reportAbuse(abuseReportRequest);
						return Unit.INSTANCE;
					} catch (MinecraftClientHttpException var6) {
						Text text = this.getErrorText(var6);
						throw new CompletionException(new AbuseReportSender.AbuseReportException(text, var6));
					} catch (MinecraftClientException var7) {
						Text textx = this.getErrorText(var7);
						throw new CompletionException(new AbuseReportSender.AbuseReportException(textx, var7));
					}
				},
				Util.getIoWorkerExecutor()
			);
		}

		@Override
		public boolean canSendReports() {
			return this.userApiService.canSendReports();
		}

		private Text getErrorText(MinecraftClientHttpException exception) {
			return Text.translatable("gui.abuseReport.send.error_message", exception.getMessage());
		}

		private Text getErrorText(MinecraftClientException exception) {
			return switch (exception.getType()) {
				case SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE_ERROR_TEXT;
				case HTTP_ERROR -> HTTP_ERROR_TEXT;
				case JSON_ERROR -> JSON_ERROR_TEXT;
			};
		}

		@Override
		public AbuseReportLimits getLimits() {
			return this.userApiService.getAbuseReportLimits();
		}
	}
}
