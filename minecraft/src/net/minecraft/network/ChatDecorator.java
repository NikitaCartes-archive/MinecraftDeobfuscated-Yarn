package net.minecraft.network;

import javax.annotation.Nullable;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Chat decorator decorates the chat server-side. Currently, only one chat decorator
 * can exist at a time. The chat decorator that is currently used can be obtained by
 * {@link net.minecraft.server.MinecraftServer#getChatDecorator}.
 * 
 * <p>For the chat decorator to produce a signed message, <strong>both the server
 * and the sender's client need to have chat previews enabled</strong>, Otherwise, the decorated
 * content is considered unsigned, and if the clients require chat messages to be signed
 * via the {@linkplain net.minecraft.client.option.GameOptions#getOnlyShowSignedChat
 * "Only Show Signed Chat" option}, they will see the undecorated message. Therefore,
 * chat decorator is <strong>not recommended for censoring messages</strong>.
 * 
 * <p>It is <strong>very important that the decorator return the same text when previewed
 * and sent</strong>. If this is not followed correctly, the server detects that the client
 * sent a forged text and discards the message. For example, a decorator that appends the
 * time the decoration was applied would be likely to fail, since there is usually a delay
 * between the previewing and the submission. One way to solve this issue is to make it
 * cache the result on preview, so that when the sent message needs decorating, the cached
 * value can be used.
 */
@FunctionalInterface
public interface ChatDecorator {
	/**
	 * An empty chat decorator that does not decorate anything.
	 */
	ChatDecorator NOOP = (sender, message) -> message;

	@Deprecated
	static ChatDecorator testRainbowChat() {
		return (sender, message) -> {
			String string = message.getString().trim();
			int i = string.length();
			float f = Math.nextDown(1.0F) * (float)i;
			MutableText mutableText = Text.literal(String.valueOf(string.charAt(0)))
				.fillStyle(Style.EMPTY.withColor(MathHelper.hsvToRgb(Math.nextDown(1.0F), 1.0F, 1.0F)));

			for (int j = 1; j < i; j++) {
				mutableText.append(Text.literal(String.valueOf(string.charAt(j))).fillStyle(Style.EMPTY.withColor(MathHelper.hsvToRgb((float)j / f, 1.0F, 1.0F))));
			}

			return mutableText;
		};
	}

	/**
	 * {@return the decorated {@code message}}
	 */
	Text decorate(@Nullable ServerPlayerEntity sender, Text message);

	/**
	 * {@return the decorated signed chat message from undecorated {@code message}}
	 * 
	 * <p>If {@code previewed} is false, the returned message will have the original
	 * content as signed and the decorated content as unsigned. This means that if the
	 * received player requires signed chat message, they will see the original content.
	 * 
	 * @param previewed whether the decoration was previewed by the sender's client
	 */
	default SignedChatMessage decorate(@Nullable ServerPlayerEntity sender, Text message, ChatMessageSignature signature, boolean previewed) {
		Text text = this.decorate(sender, message);
		if (message.equals(text)) {
			return SignedChatMessage.of(message, signature);
		} else {
			return !previewed ? SignedChatMessage.of(message, signature).withUnsigned(text) : SignedChatMessage.of(text, signature);
		}
	}

	/**
	 * {@return the signed chat message with unsigned content set as decorated {@code message}}
	 * 
	 * @apiNote This is used by various commands that send messages, such as {@link
	 * net.minecraft.server.command.MeCommand}.
	 * 
	 * <p>If the received player requires signed chat message, they will see the original content.
	 */
	default SignedChatMessage decorate(@Nullable ServerPlayerEntity sender, SignedChatMessage message) {
		return this.decorate(sender, message.signedContent(), message.signature(), false);
	}
}
