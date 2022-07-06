package net.minecraft.client.network.message;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Collection;
import java.util.Deque;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.report.ChatLog;
import net.minecraft.client.report.ReceivedMessage;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class MessageHandler {
	private final MinecraftClient client;
	private final Deque<MessageHandler.class_7596> field_39796 = Queues.<MessageHandler.class_7596>newArrayDeque();
	private long field_39797;
	private long field_39798;

	public MessageHandler(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_44765() {
		if (this.field_39797 != 0L) {
			if (Util.getMeasuringTimeMs() >= this.field_39798 + this.field_39797) {
				MessageHandler.class_7596 lv = (MessageHandler.class_7596)this.field_39796.poll();

				while (lv != null && !lv.accept()) {
					lv = (MessageHandler.class_7596)this.field_39796.poll();
				}
			}
		}
	}

	public void method_44766(double d) {
		long l = (long)(d * 1000.0);
		if (l == 0L && this.field_39797 > 0L) {
			this.field_39796.forEach(MessageHandler.class_7596::accept);
			this.field_39796.clear();
		}

		this.field_39797 = l;
	}

	public void method_44769() {
		((MessageHandler.class_7596)this.field_39796.remove()).accept();
	}

	public Collection<?> method_44773() {
		return this.field_39796;
	}

	private boolean method_44775() {
		return this.field_39797 > 0L && Util.getMeasuringTimeMs() < this.field_39798 + this.field_39797;
	}

	public void onChatMessage(MessageType messageType, SignedMessage signedMessage, MessageSender messageSender) {
		boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
		SignedMessage signedMessage2 = bl ? signedMessage.withoutUnsigned() : signedMessage;
		Text text = messageType.chat().apply(signedMessage2.getContent(), messageSender);
		if (messageSender.hasProfileId()) {
			PlayerListEntry playerListEntry = this.getPlayerListEntry(messageSender);
			MessageTrustStatus messageTrustStatus = this.getStatus(messageSender, signedMessage, text, playerListEntry);
			if (bl && messageTrustStatus.isInsecure()) {
				return;
			}

			if (this.method_44775()) {
				this.field_39796
					.add((MessageHandler.class_7596)() -> this.method_44768(messageType, messageSender, signedMessage, text, playerListEntry, messageTrustStatus));
				return;
			}

			this.method_44768(messageType, messageSender, signedMessage, text, playerListEntry, messageTrustStatus);
		} else {
			if (this.method_44775()) {
				this.field_39796.add((MessageHandler.class_7596)() -> this.method_44767(messageType, messageSender, signedMessage2, text));
				return;
			}

			this.method_44767(messageType, messageSender, signedMessage2, text);
		}
	}

	private boolean method_44768(
		MessageType messageType,
		MessageSender messageSender,
		SignedMessage signedMessage,
		Text text,
		@Nullable PlayerListEntry playerListEntry,
		MessageTrustStatus messageTrustStatus
	) {
		if (this.client.shouldBlockMessages(messageSender.profileId())) {
			return false;
		} else {
			MessageIndicator messageIndicator = messageTrustStatus.createIndicator(signedMessage);
			this.client.inGameHud.getChatHud().addMessage(text, messageIndicator);
			this.method_44772(messageType, signedMessage, messageSender);
			this.addToChatLog(signedMessage, messageSender, playerListEntry, messageTrustStatus);
			this.field_39798 = Util.getMeasuringTimeMs();
			return true;
		}
	}

	private boolean method_44767(MessageType messageType, MessageSender messageSender, SignedMessage signedMessage, Text text) {
		this.client.inGameHud.getChatHud().addMessage(text, MessageIndicator.method_44751());
		this.method_44772(messageType, signedMessage, messageSender);
		this.addToChatLog(text, signedMessage.signature().timestamp());
		this.field_39798 = Util.getMeasuringTimeMs();
		return true;
	}

	private void method_44772(MessageType messageType, SignedMessage signedMessage, MessageSender messageSender) {
		this.client.getNarratorManager().narrateChatMessage(() -> messageType.narration().apply(signedMessage.getContent(), messageSender));
	}

	private MessageTrustStatus getStatus(MessageSender messageSender, SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry) {
		return this.isAlwaysTrusted(messageSender) ? MessageTrustStatus.SECURE : MessageTrustStatus.getStatus(message, decorated, senderEntry);
	}

	private void addToChatLog(SignedMessage message, MessageSender sender, @Nullable PlayerListEntry senderEntry, MessageTrustStatus trustStatus) {
		GameProfile gameProfile;
		if (senderEntry != null) {
			gameProfile = senderEntry.getProfile();
		} else {
			gameProfile = new GameProfile(sender.profileId(), sender.name().getString());
		}

		ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
		chatLog.add(ReceivedMessage.of(gameProfile, sender.name(), message, trustStatus));
	}

	@Nullable
	private PlayerListEntry getPlayerListEntry(MessageSender sender) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		return clientPlayNetworkHandler != null ? clientPlayNetworkHandler.getPlayerListEntry(sender.profileId()) : null;
	}

	public void onGameMessage(Text message, boolean overlay) {
		if (!this.client.options.getHideMatchedNames().getValue() || !this.client.shouldBlockMessages(this.extractSender(message))) {
			if (overlay) {
				this.client.inGameHud.setOverlayMessage(message, false);
			} else {
				this.client.inGameHud.getChatHud().addMessage(message, MessageIndicator.method_44751());
				this.addToChatLog(message, Instant.now());
			}

			this.client.getNarratorManager().narrate(message);
		}
	}

	private UUID extractSender(Text text) {
		String string = TextVisitFactory.removeFormattingCodes(text);
		String string2 = StringUtils.substringBetween(string, "<", ">");
		return string2 == null ? Util.NIL_UUID : this.client.getSocialInteractionsManager().getUuid(string2);
	}

	private void addToChatLog(Text message, Instant timestamp) {
		ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
		chatLog.add(ReceivedMessage.of(message, timestamp));
	}

	private boolean isAlwaysTrusted(MessageSender sender) {
		if (this.client.isInSingleplayer() && this.client.player != null) {
			UUID uUID = this.client.player.getGameProfile().getId();
			return uUID.equals(sender.profileId());
		} else {
			return false;
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface class_7596 {
		boolean accept();
	}
}
