package net.minecraft.client.gui.ingame;

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
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Rect2i;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class ChatScreen extends Screen {
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
	private String field_2389 = "";
	private int field_2387 = -1;
	protected TextFieldWidget chatField;
	private String field_2384 = "";
	protected final List<String> commandExceptions = Lists.<String>newArrayList();
	protected int commandExceptionsX;
	protected int commandExceptionsWidth;
	private ParseResults<CommandSource> parseResults;
	private CompletableFuture<Suggestions> suggestionsFuture;
	private ChatScreen.SuggestionWindow suggestionsWindow;
	private boolean field_2380;
	private boolean suggestionsTemporarilyDisabled;

	public ChatScreen() {
	}

	public ChatScreen(String string) {
		this.field_2384 = string;
	}

	@Nullable
	@Override
	public InputListener getFocused() {
		return this.chatField;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2387 = this.client.inGameHud.getChatHud().method_1809().size();
		this.chatField = new TextFieldWidget(this.fontRenderer, 4, this.screenHeight - 12, this.screenWidth - 4, 12);
		this.chatField.setMaxLength(256);
		this.chatField.setHasBorder(false);
		this.chatField.setFocused(true);
		this.chatField.setText(this.field_2384);
		this.chatField.method_1856(false);
		this.chatField.setRenderTextProvider(this::getRenderText);
		this.chatField.setChangedListener(this::onChatFieldChanged);
		this.listeners.add(this.chatField);
		this.updateCommand();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.chatField.getText();
		this.initialize(minecraftClient, i, j);
		this.setText(string);
		this.updateCommand();
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		this.client.inGameHud.getChatHud().method_1820();
	}

	@Override
	public void update() {
		this.chatField.tick();
	}

	private void onChatFieldChanged(String string) {
		String string2 = this.chatField.getText();
		this.field_2380 = !string2.equals(this.field_2384);
		this.updateCommand();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.suggestionsWindow != null && this.suggestionsWindow.handleKeyPress(i, j, k)) {
			return true;
		} else if (i == 256) {
			this.client.openScreen(null);
			return true;
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.sendMessage(string);
			}

			this.client.openScreen(null);
			return true;
		} else if (i == 265) {
			this.method_2114(-1);
			return true;
		} else if (i == 264) {
			this.method_2114(1);
			return true;
		} else if (i == 266) {
			this.client.inGameHud.getChatHud().method_1802((double)(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1));
			return true;
		} else if (i == 267) {
			this.client.inGameHud.getChatHud().method_1802((double)(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1));
			return true;
		} else {
			if (i == 258) {
				this.field_2380 = true;
				this.openSuggestionsWindow();
			}

			return this.chatField.keyPressed(i, j, k);
		}
	}

	public void openSuggestionsWindow() {
		if (this.suggestionsFuture != null && this.suggestionsFuture.isDone()) {
			int i = 0;
			Suggestions suggestions = (Suggestions)this.suggestionsFuture.join();
			if (!suggestions.getList().isEmpty()) {
				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.fontRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(this.chatField.method_1889(suggestions.getRange().getStart()), 0, this.screenWidth - i);
				this.suggestionsWindow = new ChatScreen.SuggestionWindow(j, this.screenHeight - 12, i, suggestions);
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

		if (!this.suggestionsTemporarilyDisabled) {
			this.chatField.setSuggestion(null);
			this.suggestionsWindow = null;
		}

		this.commandExceptions.clear();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
			CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
			if (this.parseResults == null) {
				this.parseResults = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
			}

			int i = this.chatField.getCursor();
			if (i >= 1 && (this.suggestionsWindow == null || !this.suggestionsTemporarilyDisabled)) {
				this.suggestionsFuture = commandDispatcher.getCompletionSuggestions(this.parseResults, i);
				this.suggestionsFuture.thenRun(() -> {
					if (this.suggestionsFuture.isDone()) {
						this.updateSuggestionsAndExceptions();
					}
				});
			}
		} else {
			int i = getLastWhitespaceIndex(string);
			Collection<String> collection = this.client.player.networkHandler.getCommandSource().getPlayerNames();
			this.suggestionsFuture = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string, i));
		}
	}

	private void updateSuggestionsAndExceptions() {
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
		this.commandExceptionsWidth = this.screenWidth;
		if (this.commandExceptions.isEmpty()) {
			this.method_2107(TextFormat.field_1080);
		}

		this.suggestionsWindow = null;
		if (this.field_2380 && this.client.options.autoSuggestions) {
			this.openSuggestionsWindow();
		}
	}

	private String getRenderText(String string, int i) {
		return this.parseResults != null ? getRenderText(this.parseResults, string, i) : string;
	}

	public static String getRenderText(ParseResults<CommandSource> parseResults, String string, int i) {
		TextFormat[] textFormats = new TextFormat[]{
			TextFormat.field_1075, TextFormat.field_1054, TextFormat.field_1060, TextFormat.field_1076, TextFormat.field_1065
		};
		String string2 = TextFormat.field_1080.toString();
		StringBuilder stringBuilder = new StringBuilder(string2);
		int j = 0;
		int k = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++k >= textFormats.length) {
				k = 0;
			}

			int l = Math.max(parsedArgument.getRange().getStart() - i, 0);
			if (l >= string.length()) {
				break;
			}

			int m = Math.min(parsedArgument.getRange().getEnd() - i, string.length());
			if (m > 0) {
				stringBuilder.append(string, j, l);
				stringBuilder.append(textFormats[k]);
				stringBuilder.append(string, l, m);
				stringBuilder.append(string2);
				j = m;
			}
		}

		if (parseResults.getReader().canRead()) {
			int n = Math.max(parseResults.getReader().getCursor() - i, 0);
			if (n < string.length()) {
				int o = Math.min(n + parseResults.getReader().getRemainingLength(), string.length());
				stringBuilder.append(string, j, n);
				stringBuilder.append(TextFormat.field_1061);
				stringBuilder.append(string, n, o);
				j = o;
			}
		}

		stringBuilder.append(string, j, string.length());
		return stringBuilder.toString();
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (d > 1.0) {
			d = 1.0;
		}

		if (d < -1.0) {
			d = -1.0;
		}

		if (this.suggestionsWindow != null && this.suggestionsWindow.method_2117(d)) {
			return true;
		} else {
			if (!isShiftPressed()) {
				d *= 7.0;
			}

			this.client.inGameHud.getChatHud().method_1802(d);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.suggestionsWindow != null && this.suggestionsWindow.method_2119((int)d, (int)e, i)) {
			return true;
		} else {
			if (i == 0) {
				TextComponent textComponent = this.client.inGameHud.getChatHud().getTextComponentAt(d, e);
				if (textComponent != null && this.handleTextComponentClick(textComponent)) {
					return true;
				}
			}

			return this.chatField.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void method_2237(String string, boolean bl) {
		if (bl) {
			this.chatField.setText(string);
		} else {
			this.chatField.addText(string);
		}
	}

	public void method_2114(int i) {
		int j = this.field_2387 + i;
		int k = this.client.inGameHud.getChatHud().method_1809().size();
		j = MathHelper.clamp(j, 0, k);
		if (j != this.field_2387) {
			if (j == k) {
				this.field_2387 = k;
				this.chatField.setText(this.field_2389);
			} else {
				if (this.field_2387 == k) {
					this.field_2389 = this.chatField.getText();
				}

				this.chatField.setText((String)this.client.inGameHud.getChatHud().method_1809().get(j));
				this.suggestionsWindow = null;
				this.field_2387 = j;
				this.field_2380 = false;
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		drawRect(2, this.screenHeight - 14, this.screenWidth - 2, this.screenHeight - 2, Integer.MIN_VALUE);
		this.chatField.draw(i, j, f);
		if (this.suggestionsWindow != null) {
			this.suggestionsWindow.draw(i, j);
		} else {
			int k = 0;

			for (String string : this.commandExceptions) {
				drawRect(
					this.commandExceptionsX - 1,
					this.screenHeight - 14 - 13 - 12 * k,
					this.commandExceptionsX + this.commandExceptionsWidth + 1,
					this.screenHeight - 2 - 13 - 12 * k,
					-16777216
				);
				this.fontRenderer.drawWithShadow(string, (float)this.commandExceptionsX, (float)(this.screenHeight - 14 - 13 + 2 - 12 * k), -1);
				k++;
			}
		}

		TextComponent textComponent = this.client.inGameHud.getChatHud().getTextComponentAt((double)i, (double)j);
		if (textComponent != null && textComponent.getStyle().getHoverEvent() != null) {
			this.drawTextComponentHover(textComponent, i, j);
		}

		super.draw(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void method_2107(TextFormat textFormat) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.parseResults.getContext();
		SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.chatField.getCursor());
		Map<CommandNode<CommandSource>, String> map = this.client
			.player
			.networkHandler
			.getCommandDispatcher()
			.getSmartUsage(suggestionContext.parent, this.client.player.networkHandler.getCommandSource());
		List<String> list = Lists.<String>newArrayList();
		int i = 0;

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(textFormat + (String)entry.getValue());
				i = Math.max(i, this.fontRenderer.getStringWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.commandExceptions.addAll(list);
			this.commandExceptionsX = MathHelper.clamp(this.chatField.method_1889(suggestionContext.startPos), 0, this.screenWidth - i);
			this.commandExceptionsWidth = i;
		}
	}

	@Nullable
	private static String method_2103(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	private void setText(String string) {
		this.chatField.setText(string);
	}

	@Environment(EnvType.CLIENT)
	class SuggestionWindow {
		private final Rect2i field_2396;
		private final Suggestions suggestions;
		private final String typedText;
		private int field_2395;
		private int selectedSuggestionIndex;
		private Vec2f lastMousePos = Vec2f.ZERO;
		private boolean field_2391;

		private SuggestionWindow(int i, int j, int k, Suggestions suggestions) {
			this.field_2396 = new Rect2i(i - 1, j - 3 - Math.min(suggestions.getList().size(), 10) * 12, k + 1, Math.min(suggestions.getList().size(), 10) * 12);
			this.suggestions = suggestions;
			this.typedText = ChatScreen.this.chatField.getText();
			this.setSelectedSuggestionIndex(0);
		}

		public void draw(int i, int j) {
			int k = Math.min(this.suggestions.getList().size(), 10);
			int l = -5592406;
			boolean bl = this.field_2395 > 0;
			boolean bl2 = this.suggestions.getList().size() > this.field_2395 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.lastMousePos.x != (float)i || this.lastMousePos.y != (float)j;
			if (bl4) {
				this.lastMousePos = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.drawRect(
					this.field_2396.getX(), this.field_2396.getY() - 1, this.field_2396.getX() + this.field_2396.getWidth(), this.field_2396.getY(), -805306368
				);
				DrawableHelper.drawRect(
					this.field_2396.getX(),
					this.field_2396.getY() + this.field_2396.getHeight(),
					this.field_2396.getX() + this.field_2396.getWidth(),
					this.field_2396.getY() + this.field_2396.getHeight() + 1,
					-805306368
				);
				if (bl) {
					for (int m = 0; m < this.field_2396.getWidth(); m++) {
						if (m % 2 == 0) {
							DrawableHelper.drawRect(this.field_2396.getX() + m, this.field_2396.getY() - 1, this.field_2396.getX() + m + 1, this.field_2396.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int mx = 0; mx < this.field_2396.getWidth(); mx++) {
						if (mx % 2 == 0) {
							DrawableHelper.drawRect(
								this.field_2396.getX() + mx,
								this.field_2396.getY() + this.field_2396.getHeight(),
								this.field_2396.getX() + mx + 1,
								this.field_2396.getY() + this.field_2396.getHeight() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int n = 0; n < k; n++) {
				Suggestion suggestion = (Suggestion)this.suggestions.getList().get(n + this.field_2395);
				DrawableHelper.drawRect(
					this.field_2396.getX(),
					this.field_2396.getY() + 12 * n,
					this.field_2396.getX() + this.field_2396.getWidth(),
					this.field_2396.getY() + 12 * n + 12,
					-805306368
				);
				if (i > this.field_2396.getX()
					&& i < this.field_2396.getX() + this.field_2396.getWidth()
					&& j > this.field_2396.getY() + 12 * n
					&& j < this.field_2396.getY() + 12 * n + 12) {
					if (bl4) {
						this.setSelectedSuggestionIndex(n + this.field_2395);
					}

					bl5 = true;
				}

				ChatScreen.this.fontRenderer
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_2396.getX() + 1),
						(float)(this.field_2396.getY() + 2 + 12 * n),
						n + this.field_2395 == this.selectedSuggestionIndex ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.getList().get(this.selectedSuggestionIndex)).getTooltip();
				if (message != null) {
					ChatScreen.this.drawTooltip(TextFormatter.message(message).getFormattedText(), i, j);
				}
			}
		}

		public boolean method_2119(int i, int j, int k) {
			if (!this.field_2396.contains(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2396.getY()) / 12 + this.field_2395;
				if (l >= 0 && l < this.suggestions.getList().size()) {
					this.setSelectedSuggestionIndex(l);
					this.useSuggestion();
				}

				return true;
			}
		}

		public boolean method_2117(double d) {
			int i = (int)(
				ChatScreen.this.client.mouse.getX() * (double)ChatScreen.this.client.window.getScaledWidth() / (double)ChatScreen.this.client.window.getWidth()
			);
			int j = (int)(
				ChatScreen.this.client.mouse.getY() * (double)ChatScreen.this.client.window.getScaledHeight() / (double)ChatScreen.this.client.window.getHeight()
			);
			if (this.field_2396.contains(i, j)) {
				this.field_2395 = MathHelper.clamp((int)((double)this.field_2395 - d), 0, Math.max(this.suggestions.getList().size() - 10, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean handleKeyPress(int i, int j, int k) {
			if (i == 265) {
				this.scrollSelectedSuggestion(-1);
				this.field_2391 = false;
				return true;
			} else if (i == 264) {
				this.scrollSelectedSuggestion(1);
				this.field_2391 = false;
				return true;
			} else if (i == 258) {
				if (this.field_2391) {
					this.scrollSelectedSuggestion(Screen.isShiftPressed() ? -1 : 1);
				}

				this.useSuggestion();
				return true;
			} else if (i == 256) {
				this.close();
				return true;
			} else {
				return false;
			}
		}

		public void scrollSelectedSuggestion(int i) {
			this.setSelectedSuggestionIndex(this.selectedSuggestionIndex + i);
			int j = this.field_2395;
			int k = this.field_2395 + 10 - 1;
			if (this.selectedSuggestionIndex < j) {
				this.field_2395 = MathHelper.clamp(this.selectedSuggestionIndex, 0, Math.max(this.suggestions.getList().size() - 10, 0));
			} else if (this.selectedSuggestionIndex > k) {
				this.field_2395 = MathHelper.clamp(this.selectedSuggestionIndex + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
			}
		}

		public void setSelectedSuggestionIndex(int i) {
			this.selectedSuggestionIndex = i;
			if (this.selectedSuggestionIndex < 0) {
				this.selectedSuggestionIndex = this.selectedSuggestionIndex + this.suggestions.getList().size();
			}

			if (this.selectedSuggestionIndex >= this.suggestions.getList().size()) {
				this.selectedSuggestionIndex = this.selectedSuggestionIndex - this.suggestions.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selectedSuggestionIndex);
			ChatScreen.this.chatField.setSuggestion(ChatScreen.method_2103(ChatScreen.this.chatField.getText(), suggestion.apply(this.typedText)));
		}

		public void useSuggestion() {
			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selectedSuggestionIndex);
			ChatScreen.this.suggestionsTemporarilyDisabled = true;
			ChatScreen.this.setText(suggestion.apply(this.typedText));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			ChatScreen.this.chatField.setCursor(i);
			ChatScreen.this.chatField.method_1884(i);
			this.setSelectedSuggestionIndex(this.selectedSuggestionIndex);
			ChatScreen.this.suggestionsTemporarilyDisabled = false;
			this.field_2391 = true;
		}

		public void close() {
			ChatScreen.this.suggestionsWindow = null;
		}
	}
}
