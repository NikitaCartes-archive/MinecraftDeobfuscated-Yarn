package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

public class MessageArgumentType implements ArgumentType<MessageArgumentType.MessageFormat> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

	public static MessageArgumentType create() {
		return new MessageArgumentType();
	}

	public static TextComponent getMessage(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<MessageArgumentType.MessageFormat>getArgument(string, MessageArgumentType.MessageFormat.class)
			.format(commandContext.getSource(), commandContext.getSource().hasPermissionLevel(2));
	}

	public MessageArgumentType.MessageFormat method_9338(StringReader stringReader) throws CommandSyntaxException {
		return MessageArgumentType.MessageFormat.parse(stringReader, true);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class MessageFormat {
		private final String contents;
		private final MessageArgumentType.MessageSelector[] selectors;

		public MessageFormat(String string, MessageArgumentType.MessageSelector[] messageSelectors) {
			this.contents = string;
			this.selectors = messageSelectors;
		}

		public TextComponent format(ServerCommandSource serverCommandSource, boolean bl) throws CommandSyntaxException {
			if (this.selectors.length != 0 && bl) {
				TextComponent textComponent = new StringTextComponent(this.contents.substring(0, this.selectors[0].getStart()));
				int i = this.selectors[0].getStart();

				for (MessageArgumentType.MessageSelector messageSelector : this.selectors) {
					TextComponent textComponent2 = messageSelector.format(serverCommandSource);
					if (i < messageSelector.getStart()) {
						textComponent.append(this.contents.substring(i, messageSelector.getStart()));
					}

					if (textComponent2 != null) {
						textComponent.append(textComponent2);
					}

					i = messageSelector.getEnd();
				}

				if (i < this.contents.length()) {
					textComponent.append(this.contents.substring(i, this.contents.length()));
				}

				return textComponent;
			} else {
				return new StringTextComponent(this.contents);
			}
		}

		public static MessageArgumentType.MessageFormat parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
			String string = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
			if (!bl) {
				stringReader.setCursor(stringReader.getTotalLength());
				return new MessageArgumentType.MessageFormat(string, new MessageArgumentType.MessageSelector[0]);
			} else {
				List<MessageArgumentType.MessageSelector> list = Lists.<MessageArgumentType.MessageSelector>newArrayList();
				int i = stringReader.getCursor();

				while (true) {
					int j;
					EntitySelector entitySelector;
					while (true) {
						if (!stringReader.canRead()) {
							return new MessageArgumentType.MessageFormat(
								string, (MessageArgumentType.MessageSelector[])list.toArray(new MessageArgumentType.MessageSelector[list.size()])
							);
						}

						if (stringReader.peek() == '@') {
							j = stringReader.getCursor();

							try {
								EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
								entitySelector = entitySelectorReader.read();
								break;
							} catch (CommandSyntaxException var8) {
								if (var8.getType() != EntitySelectorReader.MISSING_EXCEPTION && var8.getType() != EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
									throw var8;
								}

								stringReader.setCursor(j + 1);
							}
						} else {
							stringReader.skip();
						}
					}

					list.add(new MessageArgumentType.MessageSelector(j - i, stringReader.getCursor() - i, entitySelector));
				}
			}
		}
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
		public TextComponent format(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
			return EntitySelector.getNames(this.selector.getEntities(serverCommandSource));
		}
	}
}
