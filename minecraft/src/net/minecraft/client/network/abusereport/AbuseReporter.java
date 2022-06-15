package net.minecraft.client.network.abusereport;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.chat.ChatLog;
import net.minecraft.client.network.chat.ChatLogImpl;

@Environment(EnvType.CLIENT)
public record AbuseReporter(AbuseReportSender sender, ReporterEnvironment environment, ChatLog chatLog) {
	private static final int MAX_LOGS = 1024;

	public static AbuseReporter create(ReporterEnvironment environment, UserApiService userApiService) {
		ChatLogImpl chatLogImpl = new ChatLogImpl(1024);
		AbuseReportSender abuseReportSender = AbuseReportSender.create(environment, userApiService);
		return new AbuseReporter(abuseReportSender, environment, chatLogImpl);
	}

	public boolean environmentEquals(ReporterEnvironment environment) {
		return Objects.equals(this.environment, environment);
	}
}
