/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.TextConvertibleArgumentType;
import net.minecraft.network.ChatDecorator;
import net.minecraft.network.MessageSender;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.CommandArgumentSigner;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MessageArgumentType
implements TextConvertibleArgumentType<MessageFormat> {
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
        CommandArgumentSigner commandArgumentSigner = context.getSource().getSigner();
        ChatMessageSignature chatMessageSignature = commandArgumentSigner.getArgumentSignature(name);
        boolean bl = commandArgumentSigner.isPreviewSigned(name);
        MessageSender messageSender = context.getSource().getChatMessageSender();
        if (chatMessageSignature.canVerifyFrom(messageSender.uuid())) {
            return new SignedMessage(messageFormat.contents, text, chatMessageSignature, bl);
        }
        return new SignedMessage(messageFormat.contents, text, ChatMessageSignature.none(), false);
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
    public Text toText(MessageFormat messageFormat) {
        return Text.literal(messageFormat.getContents());
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
            CompletableFuture<Text> completableFuture = source.getServer().getChatDecorator().decorate(source.getPlayer(), text);
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

    public record SignedMessage(String plain, Text formatted, ChatMessageSignature signature, boolean signedPreview) {
        public CompletableFuture<FilteredMessage<SignedChatMessage>> decorate(ServerCommandSource source) {
            CompletionStage completableFuture = ((CompletableFuture)this.filter(source, this.formatted).thenComposeAsync(filtered -> {
                ChatDecorator chatDecorator = source.getServer().getChatDecorator();
                return chatDecorator.decorateChat(source.getPlayer(), (FilteredMessage<Text>)filtered, this.signature, this.signedPreview);
            }, (Executor)source.getServer())).thenApply(decorated -> {
                SignedChatMessage signedChatMessage = this.getVerifiable((FilteredMessage<SignedChatMessage>)decorated);
                if (signedChatMessage != null) {
                    this.logInvalidSignatureWarning(source, signedChatMessage);
                }
                return decorated;
            });
            MessageArgumentType.handleResolvingFailure(source, completableFuture);
            return completableFuture;
        }

        @Nullable
        private SignedChatMessage getVerifiable(FilteredMessage<SignedChatMessage> decorated) {
            if (this.signature.canVerify()) {
                return this.signedPreview ? decorated.raw() : SignedChatMessage.of(this.plain, this.signature);
            }
            return null;
        }

        private void logInvalidSignatureWarning(ServerCommandSource source, SignedChatMessage message) {
            if (!message.verify(source)) {
                LOGGER.warn("{} sent message with invalid signature: '{}'", (Object)source.getDisplayName().getString(), (Object)message.signedContent().getString());
            }
        }

        private CompletableFuture<FilteredMessage<Text>> filter(ServerCommandSource source, Text message) {
            ServerPlayerEntity serverPlayerEntity = source.getPlayer();
            if (serverPlayerEntity != null) {
                return serverPlayerEntity.getTextStream().filterText(message);
            }
            return CompletableFuture.completedFuture(FilteredMessage.permitted(message));
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

