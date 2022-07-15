package net.minecraft.client.report;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MessagesListAdder {
	private static final int MAX_CONTIGUOUS_CONTEXT_MESSAGES = 4;
	private final ChatLog log;
	private final Predicate<ReceivedMessage> reportablePredicate;
	private int logMaxIndex;

	public MessagesListAdder(ChatLog log, Predicate<ReceivedMessage> reportablePredicate) {
		this.log = log;
		this.reportablePredicate = reportablePredicate;
		this.logMaxIndex = log.getMaxIndex();
	}

	public void add(int minAmount, MessagesListAdder.MessagesList messagesList) {
		int i = 0;

		while (i < minAmount) {
			GroupedMessagesCollector.GroupedMessages groupedMessages = this.collectGroupedMessages();
			if (groupedMessages == null) {
				break;
			}

			if (groupedMessages.type().isContext()) {
				i += addContextMessages(groupedMessages.messages(), messagesList);
			} else {
				messagesList.addMessages(groupedMessages.messages());
				i += groupedMessages.messages().size();
			}
		}
	}

	private static int addContextMessages(List<ChatLog.IndexedEntry<ReceivedMessage>> messages, MessagesListAdder.MessagesList messagesList) {
		int i = 8;
		if (messages.size() > 8) {
			int j = messages.size() - 8;
			messagesList.addMessages(messages.subList(0, 4));
			messagesList.addText(Text.translatable("gui.chatSelection.fold", j));
			messagesList.addMessages(messages.subList(messages.size() - 4, messages.size()));
			return 9;
		} else {
			messagesList.addMessages(messages);
			return messages.size();
		}
	}

	@Nullable
	private GroupedMessagesCollector.GroupedMessages collectGroupedMessages() {
		GroupedMessagesCollector groupedMessagesCollector = new GroupedMessagesCollector(message -> this.getReportType((ReceivedMessage)message.entry()));
		OptionalInt optionalInt = this.log
			.streamBackward(this.logMaxIndex)
			.streamIndexedEntries()
			.map(indexedEntry -> indexedEntry.cast(ReceivedMessage.class))
			.filter(Objects::nonNull)
			.takeWhile(groupedMessagesCollector::add)
			.mapToInt(ChatLog.IndexedEntry::index)
			.reduce((acc, cur) -> cur);
		if (optionalInt.isPresent()) {
			this.logMaxIndex = this.log.getPreviousIndex(optionalInt.getAsInt());
		}

		return groupedMessagesCollector.collect();
	}

	private GroupedMessagesCollector.ReportType getReportType(ReceivedMessage message) {
		return this.reportablePredicate.test(message) ? GroupedMessagesCollector.ReportType.REPORTABLE : GroupedMessagesCollector.ReportType.CONTEXT;
	}

	@Environment(EnvType.CLIENT)
	public interface MessagesList {
		default void addMessages(Iterable<ChatLog.IndexedEntry<ReceivedMessage>> messages) {
			for (ChatLog.IndexedEntry<ReceivedMessage> indexedEntry : messages) {
				this.addMessage(indexedEntry.index(), indexedEntry.entry());
			}
		}

		void addMessage(int index, ReceivedMessage message);

		void addText(Text text);
	}
}
