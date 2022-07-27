package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class MessageArgumentType implements SignedArgumentType<MessageArgumentType.MessageFormat> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
	private static final Logger LOGGER = LogUtils.getLogger();

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
		SignedCommandArguments signedCommandArguments = context.getSource().getSignedArguments();
		net.minecraft.network.message.SignedMessage signedMessage = signedCommandArguments.getMessage(name);
		if (signedMessage == null) {
			DecoratedContents decoratedContents = new DecoratedContents(messageFormat.contents, text);
			return new MessageArgumentType.SignedMessage(net.minecraft.network.message.SignedMessage.ofUnsigned(decoratedContents));
		} else {
			return new MessageArgumentType.SignedMessage(MessageDecorator.attachIfNotDecorated(signedMessage, text));
		}
	}

	public MessageArgumentType.MessageFormat parse(StringReader stringReader) throws CommandSyntaxException {
		return MessageArgumentType.MessageFormat.parse(stringReader, true);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public String toSignedString(MessageArgumentType.MessageFormat messageFormat) {
		return messageFormat.getContents();
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

	public static record SignedMessage(net.minecraft.network.message.SignedMessage signedArgument) {
		public void decorate(ServerCommandSource source, Consumer<net.minecraft.network.message.SignedMessage> callback) {
			MinecraftServer minecraftServer = source.getServer();
			source.getMessageChainTaskQueue()
				.append(
					() -> {
						CompletableFuture<FilteredMessage> completableFuture = this.filterText(source, this.signedArgument.getSignedContent().plain());
						CompletableFuture<net.minecraft.network.message.SignedMessage> completableFuture2 = minecraftServer.getMessageDecorator()
							.decorate(source.getPlayer(), this.signedArgument);
						return CompletableFuture.allOf(completableFuture, completableFuture2)
							.thenAcceptAsync(
								void_ -> {
									net.minecraft.network.message.SignedMessage signedMessage = ((net.minecraft.network.message.SignedMessage)completableFuture2.join())
										.withFilterMask(((FilteredMessage)completableFuture.join()).mask());
									callback.accept(signedMessage);
								},
								minecraftServer
							);
					}
				);
		}

		private CompletableFuture<FilteredMessage> filterText(ServerCommandSource source, String text) {
			ServerPlayerEntity serverPlayerEntity = source.getPlayer();
			return serverPlayerEntity != null && this.signedArgument.canVerifyFrom(serverPlayerEntity.getUuid())
				? serverPlayerEntity.getTextStream().filterText(text)
				: CompletableFuture.completedFuture(FilteredMessage.permitted(text));
		}

		/**
		 * Sends the message's header to all players.
		 * 
		 * <p>This should be called if the message could not be sent due to an exception.
		 * See {@link net.minecraft.server.command.MessageCommand} for an example.
		 */
		public void sendHeader(ServerCommandSource source) {
			if (!this.signedArgument.createMetadata().lacksSender()) {
				this.decorate(source, message -> {
					PlayerManager playerManager = source.getServer().getPlayerManager();
					playerManager.sendMessageHeader(message, Set.of());
				});
			}
		}
	}
}
