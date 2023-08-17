package net.minecraft.client.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.util.Nullables;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class ChatAbuseReport extends AbuseReport {
	final IntSet selectedMessages = new IntOpenHashSet();

	ChatAbuseReport(UUID uUID, Instant instant, UUID uUID2) {
		super(uUID, instant, uUID2);
	}

	public void toggleMessageSelection(int index, AbuseReportLimits limits) {
		if (this.selectedMessages.contains(index)) {
			this.selectedMessages.remove(index);
		} else if (this.selectedMessages.size() < limits.maxReportedMessageCount()) {
			this.selectedMessages.add(index);
		}
	}

	public ChatAbuseReport copy() {
		ChatAbuseReport chatAbuseReport = new ChatAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid);
		chatAbuseReport.selectedMessages.addAll(this.selectedMessages);
		chatAbuseReport.opinionComments = this.opinionComments;
		chatAbuseReport.reason = this.reason;
		return chatAbuseReport;
	}

	@Override
	public Screen createReportScreen(Screen parent, AbuseReportContext context) {
		return new ChatReportScreen(parent, context, this);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder extends AbuseReport.Builder<ChatAbuseReport> {
		public Builder(ChatAbuseReport report, AbuseReportLimits limits) {
			super(report, limits);
		}

		public Builder(UUID reportedPlayerUuid, AbuseReportLimits limits) {
			super(new ChatAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid), limits);
		}

		public IntSet getSelectedMessages() {
			return this.report.selectedMessages;
		}

		public void toggleMessageSelection(int index) {
			this.report.toggleMessageSelection(index, this.limits);
		}

		public boolean isMessageSelected(int index) {
			return this.report.selectedMessages.contains(index);
		}

		@Override
		public boolean hasEnoughInfo() {
			return StringUtils.isNotEmpty(this.getOpinionComments()) || !this.getSelectedMessages().isEmpty() || this.getReason() != null;
		}

		@Nullable
		@Override
		public AbuseReport.ValidationError validate() {
			if (this.report.selectedMessages.isEmpty()) {
				return AbuseReport.ValidationError.NO_REPORTED_MESSAGES;
			} else if (this.report.selectedMessages.size() > this.limits.maxReportedMessageCount()) {
				return AbuseReport.ValidationError.TOO_MANY_MESSAGES;
			} else if (this.report.reason == null) {
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
				ReportEvidence reportEvidence = this.collectEvidences(context);
				ReportedEntity reportedEntity = new ReportedEntity(this.report.reportedPlayerUuid);
				com.mojang.authlib.minecraft.report.AbuseReport abuseReport = com.mojang.authlib.minecraft.report.AbuseReport.chat(
					this.report.opinionComments, string, reportEvidence, reportedEntity, this.report.currentTime
				);
				return Either.left(new AbuseReport.ReportWithId(this.report.reportId, AbuseReportType.CHAT, abuseReport));
			}
		}

		private ReportEvidence collectEvidences(AbuseReportContext context) {
			List<ReportChatMessage> list = new ArrayList();
			ContextMessageCollector contextMessageCollector = new ContextMessageCollector(this.limits.leadingContextMessageCount());
			contextMessageCollector.add(
				context.getChatLog(), this.report.selectedMessages, (index, message) -> list.add(this.toReportChatMessage(message, this.isMessageSelected(index)))
			);
			return new ReportEvidence(Lists.reverse(list));
		}

		private ReportChatMessage toReportChatMessage(ReceivedMessage.ChatMessage message, boolean selected) {
			MessageLink messageLink = message.message().link();
			MessageBody messageBody = message.message().signedBody();
			List<ByteBuffer> list = messageBody.lastSeenMessages().entries().stream().map(MessageSignatureData::toByteBuffer).toList();
			ByteBuffer byteBuffer = Nullables.map(message.message().signature(), MessageSignatureData::toByteBuffer);
			return new ReportChatMessage(
				messageLink.index(),
				messageLink.sender(),
				messageLink.sessionId(),
				messageBody.timestamp(),
				messageBody.salt(),
				list,
				messageBody.content(),
				byteBuffer,
				selected
			);
		}

		public ChatAbuseReport.Builder copy() {
			return new ChatAbuseReport.Builder(this.report.copy(), this.limits);
		}
	}
}
