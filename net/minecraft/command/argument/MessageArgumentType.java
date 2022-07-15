/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.SignedArgumentType;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MessageArgumentType
implements SignedArgumentType<MessageFormat> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
    static final Logger LOGGER = LogUtils.getLogger();

    public static MessageArgumentType message() {
        return new MessageArgumentType();
    }

    public static Text getMessage(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        MessageFormat messageFormat = context.getArgument(name, MessageFormat.class);
        return messageFormat.format(context.getSource());
    }

    public static SignedMessage getSignedMessage(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        MessageFormat messageFormat = context.getArgument(name, MessageFormat.class);
        Text text = messageFormat.format(context.getSource());
        SignedCommandArguments signedCommandArguments = context.getSource().getSignedArguments();
        SignedCommandArguments.ArgumentSignature argumentSignature = signedCommandArguments.createSignature(name);
        return new SignedMessage(messageFormat.contents, text, argumentSignature);
    }

    @Override
    public MessageFormat parse(StringReader stringReader) throws CommandSyntaxException {
        return MessageFormat.parse(stringReader, true);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public String toSignedString(MessageFormat messageFormat) {
        return messageFormat.getContents();
    }

    @Override
    public CompletableFuture<Text> decorate(ServerCommandSource serverCommandSource, MessageFormat messageFormat) throws CommandSyntaxException {
        return messageFormat.decorate(serverCommandSource);
    }

    @Override
    public Class<MessageFormat> getFormatClass() {
        return MessageFormat.class;
    }

    static void handleResolvingFailure(ServerCommandSource source, CompletableFuture<?> future) {
        future.exceptionally(throwable -> {
            LOGGER.error("Encountered unexpected exception while resolving chat message argument from '{}'", (Object)source.getDisplayName().getString(), throwable);
            return null;
        });
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static class MessageFormat {
        final String contents;
        private final MessageSelector[] selectors;

        public MessageFormat(String contents, MessageSelector[] selectors) {
            this.contents = contents;
            this.selectors = selectors;
        }

        public String getContents() {
            return this.contents;
        }

        public MessageSelector[] getSelectors() {
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
            if (this.selectors.length == 0 || !canUseSelectors) {
                return Text.literal(this.contents);
            }
            MutableText mutableText = Text.literal(this.contents.substring(0, this.selectors[0].getStart()));
            int i = this.selectors[0].getStart();
            for (MessageSelector messageSelector : this.selectors) {
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
        }

        public static MessageFormat parse(StringReader reader, boolean canUseSelectors) throws CommandSyntaxException {
            String string = reader.getString().substring(reader.getCursor(), reader.getTotalLength());
            if (!canUseSelectors) {
                reader.setCursor(reader.getTotalLength());
                return new MessageFormat(string, new MessageSelector[0]);
            }
            ArrayList<MessageSelector> list = Lists.newArrayList();
            int i = reader.getCursor();
            while (reader.canRead()) {
                if (reader.peek() == '@') {
                    EntitySelector entitySelector;
                    int j = reader.getCursor();
                    try {
                        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader);
                        entitySelector = entitySelectorReader.read();
                    } catch (CommandSyntaxException commandSyntaxException) {
                        if (commandSyntaxException.getType() == EntitySelectorReader.MISSING_EXCEPTION || commandSyntaxException.getType() == EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
                            reader.setCursor(j + 1);
                            continue;
                        }
                        throw commandSyntaxException;
                    }
                    list.add(new MessageSelector(j - i, reader.getCursor() - i, entitySelector));
                    continue;
                }
                reader.skip();
            }
            return new MessageFormat(string, list.toArray(new MessageSelector[0]));
        }
    }

    public record SignedMessage(String plain, Text formatted, SignedCommandArguments.ArgumentSignature signedArgument) {
        public void decorate(ServerCommandSource source, Consumer<FilteredMessage<net.minecraft.network.message.SignedMessage>> callback) {
            source.getMessageChainTaskQueue().append(() -> ((CompletableFuture)((CompletableFuture)this.filterText(source, this.plain).thenComposeAsync(filtered -> {
                FilteredMessage<Text> filteredMessage = this.format(source, (FilteredMessage<String>)filtered);
                return this.decorate(source, (FilteredMessage<String>)filtered, filteredMessage);
            }, (Executor)source.getServer())).thenApply(message -> {
                net.minecraft.network.message.SignedMessage signedMessage = (net.minecraft.network.message.SignedMessage)message.raw();
                if (signedMessage.isExpiredOnServer(Instant.now())) {
                    LOGGER.warn("{} sent expired chat: '{}'. Is the client/server system time unsynchronized?", (Object)source.getDisplayName().getString(), (Object)signedMessage.getSignedContent().plain().getString());
                }
                return message;
            })).thenAcceptAsync(callback, (Executor)source.getServer()));
        }

        private FilteredMessage<Text> format(ServerCommandSource source, FilteredMessage<String> message) {
            return message.mapParts(rawContent -> this.formatted, filteredContent -> this.format(source, (String)filteredContent));
        }

        @Nullable
        private Text format(ServerCommandSource source, String filteredText) {
            try {
                MessageFormat messageFormat = MessageFormat.parse(new StringReader(filteredText), true);
                return messageFormat.format(source);
            } catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private CompletableFuture<FilteredMessage<net.minecraft.network.message.SignedMessage>> decorate(ServerCommandSource source, FilteredMessage<String> filtered, FilteredMessage<Text> formatted) {
            MessageDecorator messageDecorator = source.getServer().getMessageDecorator();
            ServerPlayerEntity serverPlayerEntity = source.getPlayer();
            SignedCommandArguments signedCommandArguments = source.getSignedArguments();
            MessageChain.Unpacker unpacker = signedCommandArguments.decoder();
            MessageMetadata messageMetadata = signedCommandArguments.metadata();
            MessageChain.Signature signature = new MessageChain.Signature(this.signedArgument.signature());
            if (this.signedArgument.signedPreview()) {
                return messageDecorator.decorateFiltered(serverPlayerEntity, formatted).thenApply(decoratedMessage -> unpacker.unpack(signature, messageMetadata, DecoratedContents.of(filtered, decoratedMessage), this.signedArgument.lastSeenMessages()));
            }
            FilteredMessage<net.minecraft.network.message.SignedMessage> filteredMessage = unpacker.unpack(signature, messageMetadata, DecoratedContents.of(filtered), this.signedArgument.lastSeenMessages());
            return messageDecorator.decorateFiltered(serverPlayerEntity, formatted).thenApply(decoratedMessage -> MessageDecorator.attachUnsignedDecoration(filteredMessage, decoratedMessage));
        }

        private CompletableFuture<FilteredMessage<String>> filterText(ServerCommandSource source, String text) {
            ServerPlayerEntity serverPlayerEntity = source.getPlayer();
            if (serverPlayerEntity != null) {
                return serverPlayerEntity.getTextStream().filterText(text);
            }
            return CompletableFuture.completedFuture(FilteredMessage.permitted(text));
        }

        public void sendHeader(ServerCommandSource source) {
            if (!source.getSignedArguments().metadata().lacksSender()) {
                this.decorate(source, decoratedMessage -> {
                    PlayerManager playerManager = source.getServer().getPlayerManager();
                    playerManager.sendMessageHeader((net.minecraft.network.message.SignedMessage)decoratedMessage.raw(), Set.of());
                });
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
}

