/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.SignedArgumentType;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MessageArgumentType
implements SignedArgumentType<MessageFormat> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

    public static MessageArgumentType message() {
        return new MessageArgumentType();
    }

    public static Text getMessage(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        MessageFormat messageFormat = context.getArgument(name, MessageFormat.class);
        return messageFormat.format(context.getSource());
    }

    public static void getSignedMessage(CommandContext<ServerCommandSource> context, String name, Consumer<SignedMessage> callback) throws CommandSyntaxException {
        MessageFormat messageFormat = context.getArgument(name, MessageFormat.class);
        ServerCommandSource serverCommandSource = context.getSource();
        Text text = messageFormat.format(serverCommandSource);
        SignedCommandArguments signedCommandArguments = serverCommandSource.getSignedArguments();
        SignedMessage signedMessage = signedCommandArguments.getMessage(name);
        if (signedMessage != null) {
            MessageArgumentType.chain(callback, serverCommandSource, signedMessage.withUnsignedContent(text));
        } else {
            MessageArgumentType.chainUnsigned(callback, serverCommandSource, SignedMessage.ofUnsigned(messageFormat.contents).withUnsignedContent(text));
        }
    }

    private static void chain(Consumer<SignedMessage> callback, ServerCommandSource source, SignedMessage message) {
        MinecraftServer minecraftServer = source.getServer();
        CompletableFuture<FilteredMessage> completableFuture = MessageArgumentType.filterText(source, message);
        CompletableFuture<Text> completableFuture2 = minecraftServer.getMessageDecorator().decorate(source.getPlayer(), message.getContent());
        source.getMessageChainTaskQueue().append(executor -> CompletableFuture.allOf(completableFuture, completableFuture2).thenAcceptAsync(void_ -> {
            SignedMessage signedMessage2 = message.withUnsignedContent((Text)completableFuture2.join()).withFilterMask(((FilteredMessage)completableFuture.join()).mask());
            callback.accept(signedMessage2);
        }, executor));
    }

    private static void chainUnsigned(Consumer<SignedMessage> callback, ServerCommandSource source, SignedMessage message) {
        MinecraftServer minecraftServer = source.getServer();
        CompletableFuture<Text> completableFuture = minecraftServer.getMessageDecorator().decorate(source.getPlayer(), message.getContent());
        source.getMessageChainTaskQueue().append(executor -> completableFuture.thenAcceptAsync(content -> callback.accept(message.withUnsignedContent((Text)content)), executor));
    }

    private static CompletableFuture<FilteredMessage> filterText(ServerCommandSource source, SignedMessage message) {
        ServerPlayerEntity serverPlayerEntity = source.getPlayer();
        if (serverPlayerEntity != null && message.canVerifyFrom(serverPlayerEntity.getUuid())) {
            return serverPlayerEntity.getTextStream().filterText(message.getSignedContent());
        }
        return CompletableFuture.completedFuture(FilteredMessage.permitted(message.getSignedContent()));
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

