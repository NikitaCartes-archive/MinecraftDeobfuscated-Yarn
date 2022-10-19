/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ContextMessageCollector;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MessagesListAdder {
    private final ChatLog log;
    private final ContextMessageCollector contextMessageCollector;
    private final Predicate<ReceivedMessage.ChatMessage> reportablePredicate;
    private int maxLogIndex;
    private int foldedMessageCount;
    @Nullable
    private SignedMessage lastMessage;

    public MessagesListAdder(AbuseReportContext context, Predicate<ReceivedMessage.ChatMessage> reportablePredicate) {
        this.log = context.chatLog();
        this.contextMessageCollector = new ContextMessageCollector(context.sender().getLimits().leadingContextMessageCount());
        this.reportablePredicate = reportablePredicate;
        this.maxLogIndex = this.log.getMaxIndex();
    }

    public void add(int minAmount, MessagesList messages) {
        ChatLogEntry chatLogEntry;
        int i = 0;
        while (i < minAmount && (chatLogEntry = this.log.get(this.maxLogIndex)) != null) {
            ReceivedMessage.ChatMessage chatMessage;
            int j = this.maxLogIndex--;
            if (!(chatLogEntry instanceof ReceivedMessage.ChatMessage) || (chatMessage = (ReceivedMessage.ChatMessage)chatLogEntry).message().equals(this.lastMessage)) continue;
            if (this.tryAdd(chatMessage)) {
                if (this.foldedMessageCount > 0) {
                    messages.addText(Text.translatable("gui.chatSelection.fold", this.foldedMessageCount));
                    this.foldedMessageCount = 0;
                }
                messages.addMessage(j, chatMessage);
                ++i;
            } else {
                ++this.foldedMessageCount;
            }
            this.lastMessage = chatMessage.message();
        }
    }

    private boolean tryAdd(ReceivedMessage.ChatMessage message) {
        SignedMessage signedMessage = message.message();
        boolean bl = this.contextMessageCollector.tryLink(signedMessage);
        if (this.reportablePredicate.test(message)) {
            this.contextMessageCollector.add(signedMessage);
            return true;
        }
        return bl;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface MessagesList {
        public void addMessage(int var1, ReceivedMessage.ChatMessage var2);

        public void addText(Text var1);
    }
}

