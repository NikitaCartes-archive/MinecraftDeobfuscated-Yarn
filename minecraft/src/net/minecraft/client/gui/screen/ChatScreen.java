package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class ChatScreen extends Screen {
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
	private String field_2389 = "";
	private int messageHistorySize = -1;
	protected TextFieldWidget chatField;
	private String originalChatText = "";
	protected final List<String> commandExceptions = Lists.<String>newArrayList();
	protected int commandExceptionsX;
	protected int commandExceptionsWidth;
	private ParseResults<CommandSource> parseResults;
	private CompletableFuture<Suggestions> suggestionsFuture;
	private ChatScreen.SuggestionWindow suggestionsWindow;
	private boolean field_2380;
	private boolean completingSuggestion;

	public ChatScreen(String originalChatText) {
		super(NarratorManager.EMPTY);
		this.originalChatText = originalChatText;
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.messageHistorySize = this.minecraft.inGameHud.getChatHud().getMessageHistory().size();
		this.chatField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.translate("chat.editBox"));
		this.chatField.setMaxLength(256);
		this.chatField.setHasBorder(false);
		this.chatField.setText(this.originalChatText);
		this.chatField.setRenderTextProvider(this::getRenderText);
		this.chatField.setChangedListener(this::onChatFieldChanged);
		this.children.add(this.chatField);
		this.updateCommand();
		this.setInitialFocus(this.chatField);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.chatField.getText();
		this.init(client, width, height);
		this.setText(string);
		this.updateCommand();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		this.minecraft.inGameHud.getChatHud().method_1820();
	}

	@Override
	public void tick() {
		this.chatField.tick();
	}

	private void onChatFieldChanged(String string) {
		String string2 = this.chatField.getText();
		this.field_2380 = !string2.equals(this.originalChatText);
		this.updateCommand();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.suggestionsWindow != null && this.suggestionsWindow.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			if (keyCode == 258) {
				this.field_2380 = true;
				this.showSuggestions();
			}

			if (super.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			} else if (keyCode == 256) {
				this.minecraft.openScreen(null);
				return true;
			} else if (keyCode == 257 || keyCode == 335) {
				String string = this.chatField.getText().trim();
				if (!string.isEmpty()) {
					this.sendMessage(string);
				}

				this.minecraft.openScreen(null);
				return true;
			} else if (keyCode == 265) {
				this.setChatFromHistory(-1);
				return true;
			} else if (keyCode == 264) {
				this.setChatFromHistory(1);
				return true;
			} else if (keyCode == 266) {
				this.minecraft.inGameHud.getChatHud().scroll((double)(this.minecraft.inGameHud.getChatHud().getVisibleLineCount() - 1));
				return true;
			} else if (keyCode == 267) {
				this.minecraft.inGameHud.getChatHud().scroll((double)(-this.minecraft.inGameHud.getChatHud().getVisibleLineCount() + 1));
				return true;
			} else {
				return false;
			}
		}
	}

	public void showSuggestions() {
		if (this.suggestionsFuture != null && this.suggestionsFuture.isDone()) {
			int i = 0;
			Suggestions suggestions = (Suggestions)this.suggestionsFuture.join();
			if (!suggestions.getList().isEmpty()) {
				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(this.chatField.getCharacterX(suggestions.getRange().getStart()), 0, this.width - i);
				this.suggestionsWindow = new ChatScreen.SuggestionWindow(j, this.height - 12, i, suggestions);
			}
		}
	}

	private static int getLastWhitespaceIndex(String string) {
		if (Strings.isNullOrEmpty(string)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = WHITESPACE_PATTERN.matcher(string);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	private void updateCommand() {
		String string = this.chatField.getText();
		if (this.parseResults != null && !this.parseResults.getReader().getString().equals(string)) {
			this.parseResults = null;
		}

		if (!this.completingSuggestion) {
			this.chatField.setSuggestion(null);
			this.suggestionsWindow = null;
		}

		this.commandExceptions.clear();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
			CommandDispatcher<CommandSource> commandDispatcher = this.minecraft.player.networkHandler.getCommandDispatcher();
			if (this.parseResults == null) {
				this.parseResults = commandDispatcher.parse(stringReader, this.minecraft.player.networkHandler.getCommandSource());
			}

			int i = this.chatField.getCursor();
			if (i >= 1 && (this.suggestionsWindow == null || !this.completingSuggestion)) {
				this.suggestionsFuture = commandDispatcher.getCompletionSuggestions(this.parseResults, i);
				this.suggestionsFuture.thenRun(() -> {
					if (this.suggestionsFuture.isDone()) {
						this.updateCommandFeedback();
					}
				});
			}
		} else {
			int i = getLastWhitespaceIndex(string);
			Collection<String> collection = this.minecraft.player.networkHandler.getCommandSource().getPlayerNames();
			this.suggestionsFuture = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string, i));
		}
	}

	private void updateCommandFeedback() {
		if (((Suggestions)this.suggestionsFuture.join()).isEmpty()
			&& !this.parseResults.getExceptions().isEmpty()
			&& this.chatField.getCursor() == this.chatField.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parseResults.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.commandExceptions.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.commandExceptions.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.commandExceptionsX = 0;
		this.commandExceptionsWidth = this.width;
		if (this.commandExceptions.isEmpty()) {
			this.method_2107(Formatting.GRAY);
		}

		this.suggestionsWindow = null;
		if (this.field_2380 && this.minecraft.options.autoSuggestions) {
			this.showSuggestions();
		}
	}

	private String getRenderText(String string, int cursorPosition) {
		return this.parseResults != null ? getRenderText(this.parseResults, string, cursorPosition) : string;
	}

	public static String getRenderText(ParseResults<CommandSource> parseResults, String typedText, int cursorPosition) {
		Formatting[] formattings = new Formatting[]{Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD};
		String string = Formatting.GRAY.toString();
		StringBuilder stringBuilder = new StringBuilder(string);
		int i = 0;
		int j = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++j >= formattings.length) {
				j = 0;
			}

			int k = Math.max(parsedArgument.getRange().getStart() - cursorPosition, 0);
			if (k >= typedText.length()) {
				break;
			}

			int l = Math.min(parsedArgument.getRange().getEnd() - cursorPosition, typedText.length());
			if (l > 0) {
				stringBuilder.append(typedText, i, k);
				stringBuilder.append(formattings[j]);
				stringBuilder.append(typedText, k, l);
				stringBuilder.append(string);
				i = l;
			}
		}

		if (parseResults.getReader().canRead()) {
			int m = Math.max(parseResults.getReader().getCursor() - cursorPosition, 0);
			if (m < typedText.length()) {
				int n = Math.min(m + parseResults.getReader().getRemainingLength(), typedText.length());
				stringBuilder.append(typedText, i, m);
				stringBuilder.append(Formatting.RED);
				stringBuilder.append(typedText, m, n);
				i = n;
			}
		}

		stringBuilder.append(typedText, i, typedText.length());
		return stringBuilder.toString();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		if (amount > 1.0) {
			amount = 1.0;
		}

		if (amount < -1.0) {
			amount = -1.0;
		}

		if (this.suggestionsWindow != null && this.suggestionsWindow.mouseScrolled(amount)) {
			return true;
		} else {
			if (!hasShiftDown()) {
				amount *= 7.0;
			}

			this.minecraft.inGameHud.getChatHud().scroll(amount);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.suggestionsWindow != null && this.suggestionsWindow.mouseClicked((int)mouseX, (int)mouseY, button)) {
			return true;
		} else {
			if (button == 0) {
				Text text = this.minecraft.inGameHud.getChatHud().getText(mouseX, mouseY);
				if (text != null && this.handleComponentClicked(text)) {
					return true;
				}
			}

			return this.chatField.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected void insertText(String string, boolean bl) {
		if (bl) {
			this.chatField.setText(string);
		} else {
			this.chatField.write(string);
		}
	}

	public void setChatFromHistory(int i) {
		int j = this.messageHistorySize + i;
		int k = this.minecraft.inGameHud.getChatHud().getMessageHistory().size();
		j = MathHelper.clamp(j, 0, k);
		if (j != this.messageHistorySize) {
			if (j == k) {
				this.messageHistorySize = k;
				this.chatField.setText(this.field_2389);
			} else {
				if (this.messageHistorySize == k) {
					this.field_2389 = this.chatField.getText();
				}

				this.chatField.setText((String)this.minecraft.inGameHud.getChatHud().getMessageHistory().get(j));
				this.suggestionsWindow = null;
				this.messageHistorySize = j;
				this.field_2380 = false;
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.setFocused(this.chatField);
		this.chatField.method_1876(true);
		fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getTextBackgroundColor(Integer.MIN_VALUE));
		this.chatField.render(mouseX, mouseY, delta);
		if (this.suggestionsWindow != null) {
			this.suggestionsWindow.draw(mouseX, mouseY);
		} else {
			int i = 0;

			for (String string : this.commandExceptions) {
				fill(
					this.commandExceptionsX - 1,
					this.height - 14 - 13 - 12 * i,
					this.commandExceptionsX + this.commandExceptionsWidth + 1,
					this.height - 2 - 13 - 12 * i,
					-16777216
				);
				this.font.drawWithShadow(string, (float)this.commandExceptionsX, (float)(this.height - 14 - 13 + 2 - 12 * i), -1);
				i++;
			}
		}

		Text text = this.minecraft.inGameHud.getChatHud().getText((double)mouseX, (double)mouseY);
		if (text != null && text.getStyle().getHoverEvent() != null) {
			this.renderComponentHoverEffect(text, mouseX, mouseY);
		}

		super.render(mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void method_2107(Formatting formatting) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.parseResults.getContext();
		SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.chatField.getCursor());
		Map<CommandNode<CommandSource>, String> map = this.minecraft
			.player
			.networkHandler
			.getCommandDispatcher()
			.getSmartUsage(suggestionContext.parent, this.minecraft.player.networkHandler.getCommandSource());
		List<String> list = Lists.<String>newArrayList();
		int i = 0;

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(formatting + (String)entry.getValue());
				i = Math.max(i, this.font.getStringWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.commandExceptions.addAll(list);
			this.commandExceptionsX = MathHelper.clamp(this.chatField.getCharacterX(suggestionContext.startPos), 0, this.width - i);
			this.commandExceptionsWidth = i;
		}
	}

	@Nullable
	private static String suggestSuffix(String typed, String suggestion) {
		return suggestion.startsWith(typed) ? suggestion.substring(typed.length()) : null;
	}

	private void setText(String text) {
		this.chatField.setText(text);
	}

	@Environment(EnvType.CLIENT)
	class SuggestionWindow {
		private final Rect2i area;
		private final Suggestions suggestions;
		private final String input;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;

		private SuggestionWindow(int x, int y, int width, Suggestions suggestions) {
			this.area = new Rect2i(x - 1, y - 3 - Math.min(suggestions.getList().size(), 10) * 12, width + 1, Math.min(suggestions.getList().size(), 10) * 12);
			this.suggestions = suggestions;
			this.input = ChatScreen.this.chatField.getText();
			this.select(0);
		}

		public void draw(int mouseX, int mouseY) {
			int i = Math.min(this.suggestions.getList().size(), 10);
			int j = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.suggestions.getList().size() > this.inWindowIndex + i;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)mouseX || this.mouse.y != (float)mouseY;
			if (bl4) {
				this.mouse = new Vec2f((float)mouseX, (float)mouseY);
			}

			if (bl3) {
				DrawableHelper.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), -805306368);
				DrawableHelper.fill(
					this.area.getX(),
					this.area.getY() + this.area.getHeight(),
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + this.area.getHeight() + 1,
					-805306368
				);
				if (bl) {
					for (int k = 0; k < this.area.getWidth(); k++) {
						if (k % 2 == 0) {
							DrawableHelper.fill(this.area.getX() + k, this.area.getY() - 1, this.area.getX() + k + 1, this.area.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int kx = 0; kx < this.area.getWidth(); kx++) {
						if (kx % 2 == 0) {
							DrawableHelper.fill(
								this.area.getX() + kx, this.area.getY() + this.area.getHeight(), this.area.getX() + kx + 1, this.area.getY() + this.area.getHeight() + 1, -1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int l = 0; l < i; l++) {
				Suggestion suggestion = (Suggestion)this.suggestions.getList().get(l + this.inWindowIndex);
				DrawableHelper.fill(this.area.getX(), this.area.getY() + 12 * l, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * l + 12, -805306368);
				if (mouseX > this.area.getX()
					&& mouseX < this.area.getX() + this.area.getWidth()
					&& mouseY > this.area.getY() + 12 * l
					&& mouseY < this.area.getY() + 12 * l + 12) {
					if (bl4) {
						this.select(l + this.inWindowIndex);
					}

					bl5 = true;
				}

				ChatScreen.this.font
					.drawWithShadow(
						suggestion.getText(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * l), l + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.getList().get(this.selection)).getTooltip();
				if (message != null) {
					ChatScreen.this.renderTooltip(Texts.toText(message).asFormattedString(), mouseX, mouseY);
				}
			}
		}

		public boolean mouseClicked(int x, int y, int button) {
			if (!this.area.contains(x, y)) {
				return false;
			} else {
				int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
				if (i >= 0 && i < this.suggestions.getList().size()) {
					this.select(i);
					this.complete();
				}

				return true;
			}
		}

		public boolean mouseScrolled(double amount) {
			int i = (int)(
				ChatScreen.this.minecraft.mouse.getX() * (double)ChatScreen.this.minecraft.window.getScaledWidth() / (double)ChatScreen.this.minecraft.window.getWidth()
			);
			int j = (int)(
				ChatScreen.this.minecraft.mouse.getY() * (double)ChatScreen.this.minecraft.window.getScaledHeight() / (double)ChatScreen.this.minecraft.window.getHeight()
			);
			if (this.area.contains(i, j)) {
				this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - amount), 0, Math.max(this.suggestions.getList().size() - 10, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode == 265) {
				this.scroll(-1);
				this.completed = false;
				return true;
			} else if (keyCode == 264) {
				this.scroll(1);
				this.completed = false;
				return true;
			} else if (keyCode == 258) {
				if (this.completed) {
					this.scroll(Screen.hasShiftDown() ? -1 : 1);
				}

				this.complete();
				return true;
			} else if (keyCode == 256) {
				this.close();
				return true;
			} else {
				return false;
			}
		}

		public void scroll(int offset) {
			this.select(this.selection + offset);
			int i = this.inWindowIndex;
			int j = this.inWindowIndex + 10 - 1;
			if (this.selection < i) {
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.getList().size() - 10, 0));
			} else if (this.selection > j) {
				this.inWindowIndex = MathHelper.clamp(this.selection + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
			}
		}

		public void select(int index) {
			this.selection = index;
			if (this.selection < 0) {
				this.selection = this.selection + this.suggestions.getList().size();
			}

			if (this.selection >= this.suggestions.getList().size()) {
				this.selection = this.selection - this.suggestions.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
			ChatScreen.this.chatField.setSuggestion(ChatScreen.suggestSuffix(ChatScreen.this.chatField.getText(), suggestion.apply(this.input)));
		}

		public void complete() {
			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
			ChatScreen.this.completingSuggestion = true;
			ChatScreen.this.setText(suggestion.apply(this.input));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			ChatScreen.this.chatField.setSelectionStart(i);
			ChatScreen.this.chatField.method_1884(i);
			this.select(this.selection);
			ChatScreen.this.completingSuggestion = false;
			this.completed = true;
		}

		public void close() {
			ChatScreen.this.suggestionsWindow = null;
		}
	}
}
