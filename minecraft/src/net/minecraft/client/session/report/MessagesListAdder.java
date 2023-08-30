package net.minecraft.client.session.report;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class MessagesListAdder {
	private final ChatLog log;
	private final ContextMessageCollector contextMessageCollector;
	private final Predicate<ReceivedMessage.ChatMessage> reportablePredicate;
	@Nullable
	private MessageLink link = null;
	private int maxLogIndex;
	private int foldedMessageCount;
	@Nullable
	private SignedMessage lastMessage;

	public MessagesListAdder(AbuseReportContext context, Predicate<ReceivedMessage.ChatMessage> reportablePredicate) {
		this.log = context.getChatLog();
		this.contextMessageCollector = new ContextMessageCollector(context.getSender().getLimits().leadingContextMessageCount());
		this.reportablePredicate = reportablePredicate;
		this.maxLogIndex = this.log.getMaxIndex();
	}

	public void add(int minAmount, MessagesListAdder.MessagesList messages) {
		int i = 0;

		while (i < minAmount) {
			ChatLogEntry chatLogEntry = this.log.get(this.maxLogIndex);
			if (chatLogEntry == null) {
				break;
			}

			int j = this.maxLogIndex--;
			if (chatLogEntry instanceof ReceivedMessage.ChatMessage chatMessage && !chatMessage.message().equals(this.lastMessage)) {
				if (this.tryAdd(messages, chatMessage)) {
					if (this.foldedMessageCount > 0) {
						messages.addText(Text.translatable("gui.chatSelection.fold", this.foldedMessageCount));
						this.foldedMessageCount = 0;
					}

					messages.addMessage(j, chatMessage);
					i++;
				} else {
					this.foldedMessageCount++;
				}

				this.lastMessage = chatMessage.message();
			}
		}
	}

	private boolean tryAdd(MessagesListAdder.MessagesList messages, ReceivedMessage.ChatMessage message) {
		SignedMessage signedMessage = message.message();
		boolean bl = this.contextMessageCollector.tryLink(signedMessage);
		if (this.reportablePredicate.test(message)) {
			this.contextMessageCollector.add(signedMessage);
			if (this.link != null && !this.link.linksTo(signedMessage.link())) {
				messages.addText(Text.translatable("gui.chatSelection.join", message.profile().getName()).formatted(Formatting.YELLOW));
			}

			this.link = signedMessage.link();
			return true;
		} else {
			return bl;
		}
	}

	@Environment(EnvType.CLIENT)
	public interface MessagesList {
		void addMessage(int index, ReceivedMessage.ChatMessage message);

		void addText(Text text);
	}
}
