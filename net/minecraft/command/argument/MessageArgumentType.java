/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MessageArgumentType
implements ArgumentType<MessageFormat> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

    public static MessageArgumentType message() {
        return new MessageArgumentType();
    }

    public static Text getMessage(CommandContext<ServerCommandSource> command, String name) throws CommandSyntaxException {
        return command.getArgument(name, MessageFormat.class).format(command.getSource(), command.getSource().hasPermissionLevel(2));
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
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class MessageSelector {
        private final int start;
        private final int end;
        private final EntitySelector selector;

        public MessageSelector(int i, int j, EntitySelector entitySelector) {
            this.start = i;
            this.end = j;
            this.selector = entitySelector;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        @Nullable
        public Text format(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            return EntitySelector.getNames(this.selector.getEntities(serverCommandSource));
        }
    }

    public static class MessageFormat {
        private final String contents;
        private final MessageSelector[] selectors;

        public MessageFormat(String string, MessageSelector[] messageSelectors) {
            this.contents = string;
            this.selectors = messageSelectors;
        }

        public Text format(ServerCommandSource serverCommandSource, boolean bl) throws CommandSyntaxException {
            if (this.selectors.length == 0 || !bl) {
                return new LiteralText(this.contents);
            }
            LiteralText mutableText = new LiteralText(this.contents.substring(0, this.selectors[0].getStart()));
            int i = this.selectors[0].getStart();
            for (MessageSelector messageSelector : this.selectors) {
                Text text = messageSelector.format(serverCommandSource);
                if (i < messageSelector.getStart()) {
                    mutableText.append(this.contents.substring(i, messageSelector.getStart()));
                }
                if (text != null) {
                    mutableText.append(text);
                }
                i = messageSelector.getEnd();
            }
            if (i < this.contents.length()) {
                mutableText.append(this.contents.substring(i, this.contents.length()));
            }
            return mutableText;
        }

        public static MessageFormat parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
            String string = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
            if (!bl) {
                stringReader.setCursor(stringReader.getTotalLength());
                return new MessageFormat(string, new MessageSelector[0]);
            }
            ArrayList<MessageSelector> list = Lists.newArrayList();
            int i = stringReader.getCursor();
            while (stringReader.canRead()) {
                if (stringReader.peek() == '@') {
                    EntitySelector entitySelector;
                    int j = stringReader.getCursor();
                    try {
                        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
                        entitySelector = entitySelectorReader.read();
                    } catch (CommandSyntaxException commandSyntaxException) {
                        if (commandSyntaxException.getType() == EntitySelectorReader.MISSING_EXCEPTION || commandSyntaxException.getType() == EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
                            stringReader.setCursor(j + 1);
                            continue;
                        }
                        throw commandSyntaxException;
                    }
                    list.add(new MessageSelector(j - i, stringReader.getCursor() - i, entitySelector));
                    continue;
                }
                stringReader.skip();
            }
            return new MessageFormat(string, list.toArray(new MessageSelector[list.size()]));
        }
    }
}

