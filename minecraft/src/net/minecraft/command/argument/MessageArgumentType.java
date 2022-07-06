package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.network.message.CommandArgumentSigner;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class MessageArgumentType implements TextConvertibleArgumentType<MessageArgumentType.MessageFormat> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
	static final Logger LOGGER = LogUtils.getLogger();

	public static MessageArgumentType message() {
		return new MessageArgumentType();
	}

	public static Text getMessage(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		MessageArgumentType.MessageFormat messageFormat = context.getArgument(name, MessageArgumentType.MessageFormat.class);
		return messageFormat.format(context.getSource());
	}

	public static MessageArgumentType.SignedMessage getSignedMessage(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		MessageArgumentType.MessageFormat messageFormat = context.getArgument(name, MessageArgumentType.MessageFormat.class);
		Text text = messageFormat.format(context.getSource());
		CommandArgumentSigner commandArgumentSigner = context.getSource().getSigner();
		MessageSignature messageSignature = commandArgumentSigner.getArgumentSignature(name);
		boolean bl = commandArgumentSigner.isPreviewSigned(name);
		return new MessageArgumentType.SignedMessage(messageFormat.contents, text, messageSignature, bl);
	}

	public MessageArgumentType.MessageFormat parse(StringReader stringReader) throws CommandSyntaxException {
		return MessageArgumentType.MessageFormat.parse(stringReader, true);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public Text toText(MessageArgumentType.MessageFormat messageFormat) {
		return Text.literal(messageFormat.getContents());
	}

	public CompletableFuture<Text> decorate(ServerCommandSource serverCommandSource, MessageArgumentType.MessageFormat messageFormat) throws CommandSyntaxException {
		return messageFormat.decorate(serverCommandSource);
	}

	@Override
	public Class<MessageArgumentType.MessageFormat> getFormatClass() {
		return MessageArgumentType.MessageFormat.class;
	}

	static void handleResolvingFailure(ServerCommandSource source, CompletableFuture<?> future) {
		future.exceptionally(throwable -> {
			LOGGER.error("Encountered unexpected exception while resolving chat message argument from '{}'", source.getDisplayName().getString(), throwable);
			return null;
		});
	}

	public static class MessageFormat {
		final String contents;
		private final MessageArgumentType.MessageSelector[] selectors;

		public MessageFormat(String contents, MessageArgumentType.MessageSelector[] selectors) {
			this.contents = contents;
			this.selectors = selectors;
		}

		public String getContents() {
			return this.contents;
		}

		public MessageArgumentType.MessageSelector[] getSelectors() {
			return this.selectors;
		}

		CompletableFuture<Text> decorate(ServerCommandSource source) throws CommandSyntaxException {
			Text text = this.format(source);
			CompletableFuture<Text> completableFuture = source.getServer().getMessageDecorator().decorate(source.getPlayer(), text);
			MessageArgumentType.handleResolvingFailure(source, completableFuture);
			return completableFuture;
		}

		Text format(ServerCommandSource source) throws CommandSyntaxException {
			return this.format(source, source.hasPermissionLevel(2));
		}

		public Text format(ServerCommandSource source, boolean canUseSelectors) throws CommandSyntaxException {
			if (this.selectors.length != 0 && canUseSelectors) {
				MutableText mutableText = Text.literal(this.contents.substring(0, this.selectors[0].getStart()));
				int i = this.selectors[0].getStart();

				for (MessageArgumentType.MessageSelector messageSelector : this.selectors) {
					Text text = messageSelector.format(source);
					if (i < messageSelector.getStart()) {
						mutableText.append(this.contents.substring(i, messageSelector.getStart()));
					}

					if (text != null) {
						mutableText.append(text);
					}

					i = messageSelector.getEnd();
				}

				if (i < this.contents.length()) {
					mutableText.append(this.contents.substring(i));
				}

				return mutableText;
			} else {
				return Text.literal(this.contents);
			}
		}

		public static MessageArgumentType.MessageFormat parse(StringReader reader, boolean canUseSelectors) throws CommandSyntaxException {
			String string = reader.getString().substring(reader.getCursor(), reader.getTotalLength());
			if (!canUseSelectors) {
				reader.setCursor(reader.getTotalLength());
				return new MessageArgumentType.MessageFormat(string, new MessageArgumentType.MessageSelector[0]);
			} else {
				List<MessageArgumentType.MessageSelector> list = Lists.<MessageArgumentType.MessageSelector>newArrayList();
				int i = reader.getCursor();

				while (true) {
					int j;
					EntitySelector entitySelector;
					while (true) {
						if (!reader.canRead()) {
							return new MessageArgumentType.MessageFormat(string, (MessageArgumentType.MessageSelector[])list.toArray(new MessageArgumentType.MessageSelector[0]));
						}

						if (reader.peek() == '@') {
							j = reader.getCursor();

							try {
								EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader);
								entitySelector = entitySelectorReader.read();
								break;
							} catch (CommandSyntaxException var8) {
								if (var8.getType() != EntitySelectorReader.MISSING_EXCEPTION && var8.getType() != EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
									throw var8;
								}

								reader.setCursor(j + 1);
							}
						} else {
							reader.skip();
						}
					}

					list.add(new MessageArgumentType.MessageSelector(j - i, reader.getCursor() - i, entitySelector));
				}
			}
		}
	}

	public static class MessageSelector {
		private final int start;
		private final int end;
		private final EntitySelector selector;

		public MessageSelector(int start, int end, EntitySelector selector) {
			this.start = start;
			this.end = end;
			this.selector = selector;
		}

		public int getStart() {
			return this.start;
		}

		public int getEnd() {
			return this.end;
		}

		public EntitySelector getSelector() {
			return this.selector;
		}

		@Nullable
		public Text format(ServerCommandSource source) throws CommandSyntaxException {
			return EntitySelector.getNames(this.selector.getEntities(source));
		}
	}

	public static record SignedMessage(String plain, Text formatted, MessageSignature signature, boolean signedPreview) {
		public CompletableFuture<FilteredMessage<net.minecraft.network.message.SignedMessage>> decorate(ServerCommandSource source) {
			CompletableFuture<FilteredMessage<net.minecraft.network.message.SignedMessage>> completableFuture = this.filter(source, this.formatted)
				.thenComposeAsync(filtered -> {
					MessageDecorator messageDecorator = source.getServer().getMessageDecorator();
					return messageDecorator.decorateChat(source.getPlayer(), filtered, this.signature, this.signedPreview);
				}, source.getServer())
				.thenApply(decorated -> {
					net.minecraft.network.message.SignedMessage signedMessage = this.getVerifiable(decorated);
					if (signedMessage != null) {
						this.logInvalidSignatureWarning(source, signedMessage);
					}

					return decorated;
				});
			MessageArgumentType.handleResolvingFailure(source, completableFuture);
			return completableFuture;
		}

		/**
		 * {@return the verifiable part of {@code message}, or {@code null} when there is none}
		 * 
		 * @implNote If the preview is signed, the decorated message will be returned, and
		 * if it's unsigned or unpreviewed but {@linkplain MessageSignature#canVerify
		 * verifiable}, the plain, undecorated message will be returned. If neither is true,
		 * this returns {@code null}, since the message cannot be verified in any way.
		 */
		@Nullable
		private net.minecraft.network.message.SignedMessage getVerifiable(FilteredMessage<net.minecraft.network.message.SignedMessage> decorated) {
			if (this.signature.canVerify()) {
				return this.signedPreview ? decorated.raw() : net.minecraft.network.message.SignedMessage.of(this.plain, this.signature);
			} else {
				return null;
			}
		}

		private void logInvalidSignatureWarning(ServerCommandSource source, net.minecraft.network.message.SignedMessage message) {
			if (!message.verify(source)) {
				MessageArgumentType.LOGGER.warn("{} sent message with invalid signature: '{}'", source.getDisplayName().getString(), message.signedContent().getString());
			}

			if (message.isExpiredOnServer(Instant.now())) {
				MessageArgumentType.LOGGER
					.warn(
						"{} sent expired chat: '{}'. Is the client/server system time unsynchronized?", source.getDisplayName().getString(), message.signedContent().getString()
					);
			}
		}

		private CompletableFuture<FilteredMessage<Text>> filter(ServerCommandSource source, Text message) {
			ServerPlayerEntity serverPlayerEntity = source.getPlayer();
			return serverPlayerEntity != null
				? serverPlayerEntity.getTextStream().filterText(message)
				: CompletableFuture.completedFuture(FilteredMessage.permitted(message));
		}
	}
}
