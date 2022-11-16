package net.minecraft.client.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class AbuseReportContext {
	private static final int MAX_LOGS = 1024;
	private final AbuseReportSender sender;
	private final ReporterEnvironment environment;
	private final ChatLog chatLog;
	@Nullable
	private ChatAbuseReport.Draft draft;

	public AbuseReportContext(AbuseReportSender sender, ReporterEnvironment environment, ChatLog chatLog) {
		this.sender = sender;
		this.environment = environment;
		this.chatLog = chatLog;
	}

	public static AbuseReportContext create(ReporterEnvironment environment, UserApiService userApiService) {
		ChatLog chatLog = new ChatLog(1024);
		AbuseReportSender abuseReportSender = AbuseReportSender.create(environment, userApiService);
		return new AbuseReportContext(abuseReportSender, environment, chatLog);
	}

	public void tryShowDraftScreen(MinecraftClient client, @Nullable Screen parent, Runnable callback, boolean quit) {
		if (this.draft != null) {
			ChatAbuseReport.Draft draft = this.draft.copy();
			client.setScreen(
				new ConfirmScreen(
					confirmed -> {
						this.setDraft(null);
						if (confirmed) {
							client.setScreen(new ChatReportScreen(parent, this, draft));
						} else {
							callback.run();
						}
					},
					Text.translatable(quit ? "gui.chatReport.draft.quittotitle.title" : "gui.chatReport.draft.title"),
					Text.translatable(quit ? "gui.chatReport.draft.quittotitle.content" : "gui.chatReport.draft.content"),
					Text.translatable("gui.chatReport.draft.edit"),
					Text.translatable("gui.chatReport.draft.discard")
				)
			);
		} else {
			callback.run();
		}
	}

	public AbuseReportSender getSender() {
		return this.sender;
	}

	public ChatLog getChatLog() {
		return this.chatLog;
	}

	public boolean environmentEquals(ReporterEnvironment environment) {
		return Objects.equals(this.environment, environment);
	}

	public void setDraft(@Nullable ChatAbuseReport.Draft draft) {
		this.draft = draft;
	}

	public boolean hasDraft() {
		return this.draft != null;
	}

	public boolean draftPlayerUuidEquals(UUID uuid) {
		return this.hasDraft() && this.draft.playerUuidEquals(uuid);
	}
}
