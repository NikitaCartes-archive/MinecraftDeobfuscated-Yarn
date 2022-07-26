package net.minecraft.client.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportChatMessageBody;
import com.mojang.authlib.minecraft.report.ReportChatMessageContent;
import com.mojang.authlib.minecraft.report.ReportChatMessageHeader;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.authlib.minecraft.report.ReportChatMessageBody.LastSeenSignature;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class ChatAbuseReport {
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
			ReportedEntity reportedEntity = new ReportedEntity(this.reportedPlayerUuid);
			AbuseReport abuseReport = new AbuseReport(this.opinionComments, string, reportEvidence, reportedEntity, this.timestamp);
			return Either.left(new ChatAbuseReport.ReportWithId(this.id, abuseReport));
		}
	}

	private ReportEvidence collectEvidence(ChatLog log) {
		Int2ObjectSortedMap<ReportChatMessage> int2ObjectSortedMap = new Int2ObjectRBTreeMap<>();
		this.selections.forEach(i -> {
			Int2ObjectMap<ReceivedMessage.ChatMessage> int2ObjectMap = collectEvidences(log, i, this.limits);
			Set<UUID> set = new ObjectOpenHashSet<>();

			for (Entry<ReceivedMessage.ChatMessage> entry : Int2ObjectMaps.fastIterable(int2ObjectMap)) {
				int j = entry.getIntKey();
				ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)entry.getValue();
				int2ObjectSortedMap.put(j, this.toReportChatMessage(j, chatMessage));
				set.add(chatMessage.getSenderUuid());
			}

			for (UUID uUID : set) {
				this.streamHeadersFrom(log, int2ObjectMap, uUID).forEach(entry -> {
					HeaderEntry headerEntry = (HeaderEntry)entry.entry();
					if (headerEntry instanceof ReceivedMessage.ChatMessage chatMessagex) {
						int2ObjectSortedMap.putIfAbsent(entry.index(), this.toReportChatMessage(entry.index(), chatMessagex));
					} else {
						int2ObjectSortedMap.putIfAbsent(entry.index(), this.toReportChatMessage(headerEntry));
					}
				});
			}
		});
		return new ReportEvidence(new ArrayList(int2ObjectSortedMap.values()));
	}

	private Stream<ChatLog.IndexedEntry<HeaderEntry>> streamHeadersFrom(ChatLog log, Int2ObjectMap<ReceivedMessage.ChatMessage> evidences, UUID senderUuid) {
		int i = Integer.MAX_VALUE;
		int j = Integer.MIN_VALUE;

		for (Entry<ReceivedMessage.ChatMessage> entry : Int2ObjectMaps.fastIterable(evidences)) {
			ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)entry.getValue();
			if (chatMessage.getSenderUuid().equals(senderUuid)) {
				int k = entry.getIntKey();
				i = Math.min(i, k);
				j = Math.max(j, k);
			}
		}

		return log.streamForward(i, j)
			.streamIndexedEntries()
			.map(indexedEntry -> indexedEntry.cast(HeaderEntry.class))
			.filter(Objects::nonNull)
			.filter(headerEntry -> ((HeaderEntry)headerEntry.entry()).header().sender().equals(senderUuid));
	}

	private static Int2ObjectMap<ReceivedMessage.ChatMessage> collectEvidences(ChatLog log, int selectedIndex, AbuseReportLimits abuseReportLimits) {
		int i = abuseReportLimits.leadingContextMessageCount() + 1;
		Int2ObjectMap<ReceivedMessage.ChatMessage> int2ObjectMap = new Int2ObjectOpenHashMap<>();
		collectPrecedingMessages(log, selectedIndex, (ix, chatMessage) -> {
			int2ObjectMap.put(ix, chatMessage);
			return int2ObjectMap.size() < i;
		});
		streamSucceedingMessages(log, selectedIndex, abuseReportLimits.trailingContextMessageCount())
			.forEach(indexedEntry -> int2ObjectMap.put(indexedEntry.index(), (ReceivedMessage.ChatMessage)indexedEntry.entry()));
		return int2ObjectMap;
	}

	private static Stream<ChatLog.IndexedEntry<ReceivedMessage.ChatMessage>> streamSucceedingMessages(ChatLog log, int selectedIndex, int maxCount) {
		return log.streamForward(log.getNextIndex(selectedIndex))
			.streamIndexedEntries()
			.map(indexedEntry -> indexedEntry.cast(ReceivedMessage.ChatMessage.class))
			.filter(Objects::nonNull)
			.limit((long)maxCount);
	}

	private static void collectPrecedingMessages(ChatLog log, int selectedIndex, ChatAbuseReport.IndexedMessageConsumer consumer) {
		IntPriorityQueue intPriorityQueue = new IntArrayPriorityQueue(IntComparators.OPPOSITE_COMPARATOR);
		intPriorityQueue.enqueue(selectedIndex);
		IntSet intSet = new IntOpenHashSet();
		intSet.add(selectedIndex);

		while (!intPriorityQueue.isEmpty()) {
			int i = intPriorityQueue.dequeueInt();
			if (log.get(i) instanceof ReceivedMessage.ChatMessage chatMessage) {
				if (!consumer.accept(i, chatMessage)) {
					break;
				}

				IntIterator var9 = collectIndicesUntilLastSeen(log, i, chatMessage.message()).iterator();

				while (var9.hasNext()) {
					int j = (Integer)var9.next();
					if (intSet.add(j)) {
						intPriorityQueue.enqueue(j);
					}
				}
			}
		}
	}

	private static IntCollection collectIndicesUntilLastSeen(ChatLog log, int selectedIndex, SignedMessage message) {
		Set<MessageSignatureData> set = (Set<MessageSignatureData>)message.signedBody()
			.lastSeenMessages()
			.entries()
			.stream()
			.map(LastSeenMessageList.Entry::lastSignature)
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));
		MessageSignatureData messageSignatureData = message.signedHeader().precedingSignature();
		if (messageSignatureData != null) {
			set.add(messageSignatureData);
		}

		IntList intList = new IntArrayList();
		Iterator<ChatLog.IndexedEntry<ChatLogEntry>> iterator = log.streamBackward(selectedIndex).streamIndexedEntries().iterator();

		while (iterator.hasNext() && !set.isEmpty()) {
			ChatLog.IndexedEntry<ChatLogEntry> indexedEntry = (ChatLog.IndexedEntry<ChatLogEntry>)iterator.next();
			ChatLogEntry var9 = indexedEntry.entry();
			if (var9 instanceof ReceivedMessage.ChatMessage) {
				ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)var9;
				if (set.remove(chatMessage.headerSignature())) {
					intList.add(indexedEntry.index());
				}
			}
		}

		return intList;
	}

	private ReportChatMessage toReportChatMessage(int index, ReceivedMessage.ChatMessage message) {
		SignedMessage signedMessage = message.message();
		MessageBody messageBody = signedMessage.signedBody();
		Instant instant = signedMessage.getTimestamp();
		long l = signedMessage.getSalt();
		ByteBuffer byteBuffer = signedMessage.headerSignature().toByteBuffer();
		ByteBuffer byteBuffer2 = Util.map(signedMessage.signedHeader().precedingSignature(), MessageSignatureData::toByteBuffer);
		ByteBuffer byteBuffer3 = ByteBuffer.wrap(messageBody.digest().asBytes());
		ReportChatMessageContent reportChatMessageContent = new ReportChatMessageContent(
			signedMessage.getSignedContent().plain(),
			signedMessage.getSignedContent().isDecorated() ? serializeContent(signedMessage.getSignedContent().decorated()) : null
		);
		String string = (String)signedMessage.unsignedContent().map(ChatAbuseReport::serializeContent).orElse(null);
		List<LastSeenSignature> list = messageBody.lastSeenMessages()
			.entries()
			.stream()
			.map(entry -> new LastSeenSignature(entry.profileId(), entry.lastSignature().toByteBuffer()))
			.toList();
		return new ReportChatMessage(
			new ReportChatMessageHeader(byteBuffer2, message.getSenderUuid(), byteBuffer3, byteBuffer),
			new ReportChatMessageBody(instant, l, list, reportChatMessageContent),
			string,
			this.hasSelectedMessage(index)
		);
	}

	private ReportChatMessage toReportChatMessage(HeaderEntry headerEntry) {
		ByteBuffer byteBuffer = headerEntry.headerSignature().toByteBuffer();
		ByteBuffer byteBuffer2 = Util.map(headerEntry.header().precedingSignature(), MessageSignatureData::toByteBuffer);
		return new ReportChatMessage(
			new ReportChatMessageHeader(byteBuffer2, headerEntry.header().sender(), ByteBuffer.wrap(headerEntry.bodyDigest()), byteBuffer), null, null, false
		);
	}

	private static String serializeContent(Text content) {
		return Text.Serializer.toSortedJsonString(content);
	}

	public ChatAbuseReport copy() {
		ChatAbuseReport chatAbuseReport = new ChatAbuseReport(this.id, this.timestamp, this.reportedPlayerUuid, this.limits);
		chatAbuseReport.selections.addAll(this.selections);
		chatAbuseReport.opinionComments = this.opinionComments;
		chatAbuseReport.reason = this.reason;
		return chatAbuseReport;
	}

	@Environment(EnvType.CLIENT)
	interface IndexedMessageConsumer {
		boolean accept(int index, ReceivedMessage.ChatMessage message);
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
