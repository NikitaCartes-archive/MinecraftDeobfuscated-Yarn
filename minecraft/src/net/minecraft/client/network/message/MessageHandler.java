package net.minecraft.client.network.message;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
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

	public MessageHandler(MinecraftClient client) {
		this.client = client;
	}

	public void onChatMessage(MessageType type, SignedMessage message, MessageSender sender) {
		if (!this.client.shouldBlockMessages(sender.profileId())) {
			boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
			SignedMessage signedMessage = bl ? message.withoutUnsigned() : message;
			Text text = type.chat().apply(signedMessage.getContent(), sender);
			PlayerListEntry playerListEntry = this.getPlayerListEntry(sender);
			MessageTrustStatus messageTrustStatus = this.getStatus(sender, signedMessage, text, playerListEntry);
			if (!messageTrustStatus.isInsecure() || !bl) {
				MessageIndicator messageIndicator = messageTrustStatus.createIndicator(signedMessage);
				this.client.inGameHud.getChatHud().queueMessage(text, messageIndicator);
				this.client.getNarratorManager().narrateChatMessage(() -> type.narration().apply(signedMessage.getContent(), sender));
				if (sender.hasProfileId()) {
					this.addToChatLog(message, sender, playerListEntry, messageTrustStatus);
				} else {
					this.addToChatLog(text, message.signature().timestamp());
				}
			}
		}
	}

	private MessageTrustStatus getStatus(MessageSender sender, SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry) {
		if (sender.hasProfileId()) {
			return this.isAlwaysTrusted(sender) ? MessageTrustStatus.SECURE : MessageTrustStatus.getStatus(message, decorated, senderEntry);
		} else {
			return MessageTrustStatus.UNKNOWN;
		}
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
				this.client.inGameHud.getChatHud().addMessage(message);
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
}
