/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.client.report.ChatLog;
import net.minecraft.client.report.ReceivedMessage;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
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
    public ValidationError validate() {
        if (this.selections.isEmpty()) {
            return ValidationError.NO_REPORTED_MESSAGES;
        }
        if (this.selections.size() > this.limits.maxReportedMessageCount()) {
            return ValidationError.TOO_MANY_MESSAGES;
        }
        if (this.reason == null) {
            return ValidationError.NO_REASON;
        }
        if (this.opinionComments.length() > this.limits.maxOpinionCommentsLength()) {
            return ValidationError.COMMENTS_TOO_LONG;
        }
        return null;
    }

    public Either<ReportWithId, ValidationError> finalizeReport(AbuseReportContext reporter) {
        ValidationError validationError = this.validate();
        if (validationError != null) {
            return Either.right(validationError);
        }
        String string = Objects.requireNonNull(this.reason).getId();
        ReportEvidence reportEvidence = this.collectEvidence(reporter.chatLog());
        if (reportEvidence.messages.size() > this.limits.maxEvidenceMessageCount()) {
            return Either.right(ValidationError.TOO_MANY_MESSAGES);
        }
        ReportedEntity reportedEntity = new ReportedEntity(this.reportedPlayerUuid);
        AbuseReport abuseReport = new AbuseReport(CHAT, this.opinionComments, string, reportEvidence, reportedEntity, this.timestamp);
        return Either.left(new ReportWithId(this.id, abuseReport));
    }

    private ReportEvidence collectEvidence(ChatLog log) {
        IntRBTreeSet intSortedSet = new IntRBTreeSet();
        this.selections.forEach(i -> {
            IntStream intStream = this.streamNeighboringIndices(log, i);
            intStream.forEach(intSortedSet::add);
        });
        List<ReportChatMessage> list = intSortedSet.intStream().mapToObj(index -> {
            ReceivedMessage receivedMessage = log.get(index);
            if (receivedMessage instanceof ReceivedMessage.ChatMessage) {
                ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)receivedMessage;
                return this.toReportChatMessage(index, chatMessage);
            }
            return null;
        }).filter(Objects::nonNull).toList();
        return new ReportEvidence(list);
    }

    private ReportChatMessage toReportChatMessage(int index, ReceivedMessage.ChatMessage message) {
        SignedMessage signedMessage = message.message();
        Instant instant = signedMessage.signature().timestamp();
        NetworkEncryptionUtils.SignatureData signatureData = signedMessage.signature().saltSignature();
        long l = signatureData.salt();
        String string = signatureData.isSignaturePresent() ? ChatAbuseReport.base64Encode(signatureData.signature()) : null;
        String string2 = ChatAbuseReport.serializeContent(signedMessage.signedContent());
        String string3 = signedMessage.unsignedContent().map(ChatAbuseReport::serializeContent).orElse(null);
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

    @Environment(value=EnvType.CLIENT)
    public record ValidationError(Text message) {
        public static final ValidationError NO_REASON = new ValidationError(Text.translatable("gui.chatReport.send.no_reason"));
        public static final ValidationError NO_REPORTED_MESSAGES = new ValidationError(Text.translatable("gui.chatReport.send.no_reported_messages"));
        public static final ValidationError TOO_MANY_MESSAGES = new ValidationError(Text.translatable("gui.chatReport.send.too_many_messages"));
        public static final ValidationError COMMENTS_TOO_LONG = new ValidationError(Text.translatable("gui.chatReport.send.comments_too_long"));
    }

    @Environment(value=EnvType.CLIENT)
    public record ReportWithId(UUID id, AbuseReport report) {
    }
}

