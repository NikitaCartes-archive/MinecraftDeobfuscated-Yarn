package net.minecraft.client.network.abusereport;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.client.report.ChatLog;
import net.minecraft.client.report.ReceivedMessage;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatAbuseReport {
	private static final String CHAT = "CHAT";
	private final UUID id;
	private final Instant timestamp;
	private final UUID reportedPlayerUuid;
	private final AbuseReportLimits limits;
	private final IntSet selections = new IntOpenHashSet();
	private String opinionComments = "";
	@Nullable
	private AbuseReportReason reason;

	private ChatAbuseReport(UUID id, Instant timestamp, UUID reportedPlayerUuid, AbuseReportLimits limits) {
		this.id = id;
		this.timestamp = timestamp;
		this.reportedPlayerUuid = reportedPlayerUuid;
		this.limits = limits;
	}

	public ChatAbuseReport(UUID reportedPlayerUuid, AbuseReportLimits limits) {
		this(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, limits);
	}

	public void setOpinionComments(String opinionComments) {
		this.opinionComments = opinionComments;
	}

	public void setReason(AbuseReportReason reason) {
		this.reason = reason;
	}

	public void toggleMessageSelection(int index) {
		if (this.selections.contains(index)) {
			this.selections.remove(index);
		} else if (this.selections.size() < this.limits.maxReportedMessageCount()) {
			this.selections.add(index);
		}
	}

	public UUID getReportedPlayerUuid() {
		return this.reportedPlayerUuid;
	}

	public IntSet getSelections() {
		return this.selections;
	}

	public String getOpinionComments() {
		return this.opinionComments;
	}

	@Nullable
	public AbuseReportReason getReason() {
		return this.reason;
	}

	public boolean hasSelectedMessage(int index) {
		return this.selections.contains(index);
	}

	@Nullable
	public ChatAbuseReport.ValidationError validate() {
		if (this.selections.isEmpty()) {
			return ChatAbuseReport.ValidationError.NO_REPORTED_MESSAGES;
		} else if (this.selections.size() > this.limits.maxReportedMessageCount()) {
			return ChatAbuseReport.ValidationError.TOO_MANY_MESSAGES;
		} else if (this.reason == null) {
			return ChatAbuseReport.ValidationError.NO_REASON;
		} else {
			return this.opinionComments.length() > this.limits.maxOpinionCommentsLength() ? ChatAbuseReport.ValidationError.COMMENTS_TOO_LONG : null;
		}
	}

	public Either<ChatAbuseReport.ReportWithId, ChatAbuseReport.ValidationError> finalizeReport(AbuseReportContext reporter) {
		ChatAbuseReport.ValidationError validationError = this.validate();
		if (validationError != null) {
			return Either.right(validationError);
		} else {
			String string = ((AbuseReportReason)Objects.requireNonNull(this.reason)).getId();
			ReportEvidence reportEvidence = this.collectEvidence(reporter.chatLog());
			if (reportEvidence.messages.size() > this.limits.maxEvidenceMessageCount()) {
				return Either.right(ChatAbuseReport.ValidationError.TOO_MANY_MESSAGES);
			} else {
				ReportedEntity reportedEntity = new ReportedEntity(this.reportedPlayerUuid);
				AbuseReport abuseReport = new AbuseReport("CHAT", this.opinionComments, string, reportEvidence, reportedEntity, this.timestamp);
				return Either.left(new ChatAbuseReport.ReportWithId(this.id, abuseReport));
			}
		}
	}

	private ReportEvidence collectEvidence(ChatLog log) {
		IntSortedSet intSortedSet = new IntRBTreeSet();
		this.selections.forEach(i -> {
			IntStream intStream = this.streamNeighboringIndices(log, i);
			intStream.forEach(intSortedSet::add);
		});
		List<ReportChatMessage> list = intSortedSet.intStream()
			.mapToObj(index -> log.get(index) instanceof ReceivedMessage.ChatMessage chatMessage ? this.toReportChatMessage(index, chatMessage) : null)
			.filter(Objects::nonNull)
			.toList();
		return new ReportEvidence(list);
	}

	private ReportChatMessage toReportChatMessage(int index, ReceivedMessage.ChatMessage message) {
		SignedMessage signedMessage = message.message();
		Instant instant = signedMessage.signature().timestamp();
		NetworkEncryptionUtils.SignatureData signatureData = signedMessage.signature().saltSignature();
		long l = signatureData.salt();
		String string = signatureData.isSignaturePresent() ? base64Encode(signatureData.signature()) : null;
		String string2 = serializeContent(signedMessage.signedContent());
		String string3 = (String)signedMessage.unsignedContent().map(ChatAbuseReport::serializeContent).orElse(null);
		return new ReportChatMessage(message.getSenderUuid(), instant, l, string, string2, string3, this.hasSelectedMessage(index));
	}

	private static String serializeContent(Text content) {
		return Text.Serializer.toSortedJsonString(content);
	}

	private static String base64Encode(byte[] bs) {
		return Base64.getEncoder().encodeToString(bs);
	}

	private IntStream streamNeighboringIndices(ChatLog log, int index) {
		int i = log.clampWithOffset(index, -this.limits.leadingContextMessageCount());
		int j = log.clampWithOffset(index, this.limits.trailingContextMessageCount());
		return log.streamForward(i, j).streamIndices();
	}

	public ChatAbuseReport copy() {
		ChatAbuseReport chatAbuseReport = new ChatAbuseReport(this.id, this.timestamp, this.reportedPlayerUuid, this.limits);
		chatAbuseReport.selections.addAll(this.selections);
		chatAbuseReport.opinionComments = this.opinionComments;
		chatAbuseReport.reason = this.reason;
		return chatAbuseReport;
	}

	@Environment(EnvType.CLIENT)
	public static record ReportWithId(UUID id, AbuseReport report) {
	}

	@Environment(EnvType.CLIENT)
	public static record ValidationError(Text message) {
		public static final ChatAbuseReport.ValidationError NO_REASON = new ChatAbuseReport.ValidationError(Text.translatable("gui.chatReport.send.no_reason"));
		public static final ChatAbuseReport.ValidationError NO_REPORTED_MESSAGES = new ChatAbuseReport.ValidationError(
			Text.translatable("gui.chatReport.send.no_reported_messages")
		);
		public static final ChatAbuseReport.ValidationError TOO_MANY_MESSAGES = new ChatAbuseReport.ValidationError(
			Text.translatable("gui.chatReport.send.too_many_messages")
		);
		public static final ChatAbuseReport.ValidationError COMMENTS_TOO_LONG = new ChatAbuseReport.ValidationError(
			Text.translatable("gui.chatReport.send.comments_too_long")
		);
	}
}
