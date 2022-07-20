/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.GroupedMessagesCollector;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MessagesListAdder<T extends ReceivedMessage> {
    private static final int MAX_CONTIGUOUS_CONTEXT_MESSAGES = 4;
    private final ChatLog log;
    private final Predicate<T> reportablePredicate;
    private int logMaxIndex;
    final Class<T> field_39903;

    public MessagesListAdder(ChatLog log, Predicate<T> reportablePredicate, Class<T> class_) {
        this.log = log;
        this.reportablePredicate = reportablePredicate;
        this.logMaxIndex = log.getMaxIndex();
        this.field_39903 = class_;
    }

    public void add(int minAmount, MessagesList<T> messagesList) {
        GroupedMessagesCollector.GroupedMessages<T> groupedMessages;
        int i = 0;
        while (i < minAmount && (groupedMessages = this.collectGroupedMessages()) != null) {
            if (groupedMessages.type().isContext()) {
                i += this.addContextMessages(groupedMessages.messages(), messagesList);
                continue;
            }
            messagesList.addMessages(groupedMessages.messages());
            i += groupedMessages.messages().size();
        }
    }

    private int addContextMessages(List<ChatLog.IndexedEntry<T>> list, MessagesList<T> messagesList) {
        int i = 8;
        if (list.size() > 8) {
            int j = list.size() - 8;
            messagesList.addMessages(list.subList(0, 4));
            messagesList.addText(Text.translatable("gui.chatSelection.fold", j));
            messagesList.addMessages(list.subList(list.size() - 4, list.size()));
            return 9;
        }
        messagesList.addMessages(list);
        return list.size();
    }

    @Nullable
    private GroupedMessagesCollector.GroupedMessages<T> collectGroupedMessages() {
        GroupedMessagesCollector groupedMessagesCollector = new GroupedMessagesCollector(message -> this.getReportType((ReceivedMessage)message.entry()));
        OptionalInt optionalInt = this.log.streamBackward(this.logMaxIndex).streamIndexedEntries().map(indexedEntry -> indexedEntry.cast(this.field_39903)).filter(Objects::nonNull).takeWhile(groupedMessagesCollector::add).mapToInt(ChatLog.IndexedEntry::index).reduce((acc, cur) -> cur);
        if (optionalInt.isPresent()) {
            this.logMaxIndex = this.log.getPreviousIndex(optionalInt.getAsInt());
        }
        return groupedMessagesCollector.collect();
    }

    private GroupedMessagesCollector.ReportType getReportType(T message) {
        return this.reportablePredicate.test(message) ? GroupedMessagesCollector.ReportType.REPORTABLE : GroupedMessagesCollector.ReportType.CONTEXT;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface MessagesList<T extends ReceivedMessage> {
        default public void addMessages(Iterable<ChatLog.IndexedEntry<T>> messages) {
            for (ChatLog.IndexedEntry<T> indexedEntry : messages) {
                this.addMessage(indexedEntry.index(), (ReceivedMessage)indexedEntry.entry());
            }
        }

        public void addMessage(int var1, T var2);

        public void addText(Text var1);
    }
}

