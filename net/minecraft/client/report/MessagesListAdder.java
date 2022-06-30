/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.ChatLog;
import net.minecraft.client.report.GroupedMessagesCollector;
import net.minecraft.client.report.ReceivedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
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

    public void add(int minAmount, MessagesList messagesList) {
        GroupedMessagesCollector.GroupedMessages groupedMessages;
        int i = 0;
        while (i < minAmount && (groupedMessages = this.collectGroupedMessages()) != null) {
            if (groupedMessages.type().isContext()) {
                i += MessagesListAdder.addContextMessages(groupedMessages.messages(), messagesList);
                continue;
            }
            messagesList.addMessages(groupedMessages.messages());
            i += groupedMessages.messages().size();
        }
    }

    private static int addContextMessages(List<ReceivedMessage.IndexedMessage> messages, MessagesList messagesList) {
        int i = 8;
        if (messages.size() > 8) {
            int j = messages.size() - 8;
            messagesList.addMessages(messages.subList(0, 4));
            messagesList.addText(Text.translatable("gui.chatSelection.fold", j));
            messagesList.addMessages(messages.subList(messages.size() - 4, messages.size()));
            return 9;
        }
        messagesList.addMessages(messages);
        return messages.size();
    }

    @Nullable
    private GroupedMessagesCollector.GroupedMessages collectGroupedMessages() {
        GroupedMessagesCollector groupedMessagesCollector = new GroupedMessagesCollector(message -> this.getReportType(message.message()));
        OptionalInt optionalInt = this.log.streamBackward(this.logMaxIndex).streamIndexedMessages().takeWhile(groupedMessagesCollector::add).mapToInt(ReceivedMessage.IndexedMessage::index).reduce((acc, cur) -> cur);
        if (optionalInt.isPresent()) {
            this.logMaxIndex = this.log.getPreviousIndex(optionalInt.getAsInt());
        }
        return groupedMessagesCollector.collect();
    }

    private GroupedMessagesCollector.ReportType getReportType(ReceivedMessage message) {
        return this.reportablePredicate.test(message) ? GroupedMessagesCollector.ReportType.REPORTABLE : GroupedMessagesCollector.ReportType.CONTEXT;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface MessagesList {
        default public void addMessages(Iterable<ReceivedMessage.IndexedMessage> messages) {
            for (ReceivedMessage.IndexedMessage indexedMessage : messages) {
                this.addMessage(indexedMessage.index(), indexedMessage.message());
            }
        }

        public void addMessage(int var1, ReceivedMessage var2);

        public void addText(Text var1);
    }
}

