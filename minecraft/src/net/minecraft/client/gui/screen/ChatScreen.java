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
	private int field_2387 = -1;
	protected TextFieldWidget chatField;
	private String field_18973 = "";
	protected final List<String> commandExceptions = Lists.<String>newArrayList();
	protected int commandExceptionsX;
	protected int commandExceptionsWidth;
	private ParseResults<CommandSource> parseResults;
	private CompletableFuture<Suggestions> suggestionsFuture;
	private ChatScreen.SuggestionWindow suggestionsWindow;
	private boolean field_2380;
	private boolean completingSuggestion;

	public ChatScreen(String string) {
		super(NarratorManager.EMPTY);
		this.field_18973 = string;
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.field_2387 = this.minecraft.field_1705.method_1743().getMessageHistory().size();
		this.chatField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.translate("chat.editBox"));
		this.chatField.setMaxLength(256);
		this.chatField.setHasBorder(false);
		this.chatField.setText(this.field_18973);
		this.chatField.setRenderTextProvider(this::getRenderText);
		this.chatField.setChangedListener(this::onChatFieldChanged);
		this.children.add(this.chatField);
		this.updateCommand();
		this.method_20085(this.chatField);
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.chatField.getText();
		this.init(minecraftClient, i, j);
		this.setText(string);
		this.updateCommand();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		this.minecraft.field_1705.method_1743().method_1820();
	}

	@Override
	public void tick() {
		this.chatField.tick();
	}

	private void onChatFieldChanged(String string) {
		String string2 = this.chatField.getText();
		this.field_2380 = !string2.equals(this.field_18973);
		this.updateCommand();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.suggestionsWindow != null && this.suggestionsWindow.keyPressed(i, j, k)) {
			return true;
		} else {
			if (i == 258) {
				this.field_2380 = true;
				this.showSuggestions();
			}

			if (super.keyPressed(i, j, k)) {
				return true;
			} else if (i == 256) {
				this.minecraft.method_1507(null);
				return true;
			} else if (i == 257 || i == 335) {
				String string = this.chatField.getText().trim();
				if (!string.isEmpty()) {
					this.sendMessage(string);
				}

				this.minecraft.method_1507(null);
				return true;
			} else if (i == 265) {
				this.method_2114(-1);
				return true;
			} else if (i == 264) {
				this.method_2114(1);
				return true;
			} else if (i == 266) {
				this.minecraft.field_1705.method_1743().method_1802((double)(this.minecraft.field_1705.method_1743().getVisibleLineCount() - 1));
				return true;
			} else if (i == 267) {
				this.minecraft.field_1705.method_1743().method_1802((double)(-this.minecraft.field_1705.method_1743().getVisibleLineCount() + 1));
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
			CommandDispatcher<CommandSource> commandDispatcher = this.minecraft.field_1724.networkHandler.getCommandDispatcher();
			if (this.parseResults == null) {
				this.parseResults = commandDispatcher.parse(stringReader, this.minecraft.field_1724.networkHandler.method_2875());
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
			Collection<String> collection = this.minecraft.field_1724.networkHandler.method_2875().getPlayerNames();
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
			this.method_2107(Formatting.field_1080);
		}

		this.suggestionsWindow = null;
		if (this.field_2380 && this.minecraft.field_1690.autoSuggestions) {
			this.showSuggestions();
		}
	}

	private String getRenderText(String string, int i) {
		return this.parseResults != null ? getRenderText(this.parseResults, string, i) : string;
	}

	public static String getRenderText(ParseResults<CommandSource> parseResults, String string, int i) {
		Formatting[] formattings = new Formatting[]{
			Formatting.field_1075, Formatting.field_1054, Formatting.field_1060, Formatting.field_1076, Formatting.field_1065
		};
		String string2 = Formatting.field_1080.toString();
		StringBuilder stringBuilder = new StringBuilder(string2);
		int j = 0;
		int k = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++k >= formattings.length) {
				k = 0;
			}

			int l = Math.max(parsedArgument.getRange().getStart() - i, 0);
			if (l >= string.length()) {
				break;
			}

			int m = Math.min(parsedArgument.getRange().getEnd() - i, string.length());
			if (m > 0) {
				stringBuilder.append(string, j, l);
				stringBuilder.append(formattings[k]);
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
				stringBuilder.append(Formatting.field_1061);
				stringBuilder.append(string, n, o);
				j = o;
			}
		}

		stringBuilder.append(string, j, string.length());
		return stringBuilder.toString();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if (f > 1.0) {
			f = 1.0;
		}

		if (f < -1.0) {
			f = -1.0;
		}

		if (this.suggestionsWindow != null && this.suggestionsWindow.mouseScrolled(f)) {
			return true;
		} else {
			if (!hasShiftDown()) {
				f *= 7.0;
			}

			this.minecraft.field_1705.method_1743().method_1802(f);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.suggestionsWindow != null && this.suggestionsWindow.mouseClicked((int)d, (int)e, i)) {
			return true;
		} else {
			if (i == 0) {
				Text text = this.minecraft.field_1705.method_1743().getText(d, e);
				if (text != null && this.handleComponentClicked(text)) {
					return true;
				}
			}

			return this.chatField.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void insertText(String string, boolean bl) {
		if (bl) {
			this.chatField.setText(string);
		} else {
			this.chatField.addText(string);
		}
	}

	public void method_2114(int i) {
		int j = this.field_2387 + i;
		int k = this.minecraft.field_1705.method_1743().getMessageHistory().size();
		j = MathHelper.clamp(j, 0, k);
		if (j != this.field_2387) {
			if (j == k) {
				this.field_2387 = k;
				this.chatField.setText(this.field_2389);
			} else {
				if (this.field_2387 == k) {
					this.field_2389 = this.chatField.getText();
				}

				this.chatField.setText((String)this.minecraft.field_1705.method_1743().getMessageHistory().get(j));
				this.suggestionsWindow = null;
				this.field_2387 = j;
				this.field_2380 = false;
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.setFocused(this.chatField);
		this.chatField.method_1876(true);
		fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.field_1690.getTextBackgroundColor(Integer.MIN_VALUE));
		this.chatField.render(i, j, f);
		if (this.suggestionsWindow != null) {
			this.suggestionsWindow.draw(i, j);
		} else {
			int k = 0;

			for (String string : this.commandExceptions) {
				fill(
					this.commandExceptionsX - 1,
					this.height - 14 - 13 - 12 * k,
					this.commandExceptionsX + this.commandExceptionsWidth + 1,
					this.height - 2 - 13 - 12 * k,
					-16777216
				);
				this.font.drawWithShadow(string, (float)this.commandExceptionsX, (float)(this.height - 14 - 13 + 2 - 12 * k), -1);
				k++;
			}
		}

		Text text = this.minecraft.field_1705.method_1743().getText((double)i, (double)j);
		if (text != null && text.getStyle().getHoverEvent() != null) {
			this.renderComponentHoverEffect(text, i, j);
		}

		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void method_2107(Formatting formatting) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.parseResults.getContext();
		SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.chatField.getCursor());
		Map<CommandNode<CommandSource>, String> map = this.minecraft
			.field_1724
			.networkHandler
			.getCommandDispatcher()
			.getSmartUsage(suggestionContext.parent, this.minecraft.field_1724.networkHandler.method_2875());
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
	private static String suggestSuffix(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	private void setText(String string) {
		this.chatField.setText(string);
	}

	@Environment(EnvType.CLIENT)
	class SuggestionWindow {
		private final Rect2i field_2396;
		private final Suggestions suggestions;
		private final String input;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;

		private SuggestionWindow(int i, int j, int k, Suggestions suggestions) {
			this.field_2396 = new Rect2i(i - 1, j - 3 - Math.min(suggestions.getList().size(), 10) * 12, k + 1, Math.min(suggestions.getList().size(), 10) * 12);
			this.suggestions = suggestions;
			this.input = ChatScreen.this.chatField.getText();
			this.select(0);
		}

		public void draw(int i, int j) {
			int k = Math.min(this.suggestions.getList().size(), 10);
			int l = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.suggestions.getList().size() > this.inWindowIndex + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)i || this.mouse.y != (float)j;
			if (bl4) {
				this.mouse = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.fill(
					this.field_2396.getX(), this.field_2396.getY() - 1, this.field_2396.getX() + this.field_2396.getWidth(), this.field_2396.getY(), -805306368
				);
				DrawableHelper.fill(
					this.field_2396.getX(),
					this.field_2396.getY() + this.field_2396.getHeight(),
					this.field_2396.getX() + this.field_2396.getWidth(),
					this.field_2396.getY() + this.field_2396.getHeight() + 1,
					-805306368
				);
				if (bl) {
					for (int m = 0; m < this.field_2396.getWidth(); m++) {
						if (m % 2 == 0) {
							DrawableHelper.fill(this.field_2396.getX() + m, this.field_2396.getY() - 1, this.field_2396.getX() + m + 1, this.field_2396.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int mx = 0; mx < this.field_2396.getWidth(); mx++) {
						if (mx % 2 == 0) {
							DrawableHelper.fill(
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
				Suggestion suggestion = (Suggestion)this.suggestions.getList().get(n + this.inWindowIndex);
				DrawableHelper.fill(
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
						this.select(n + this.inWindowIndex);
					}

					bl5 = true;
				}

				ChatScreen.this.font
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_2396.getX() + 1),
						(float)(this.field_2396.getY() + 2 + 12 * n),
						n + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.getList().get(this.selection)).getTooltip();
				if (message != null) {
					ChatScreen.this.renderTooltip(Texts.toText(message).asFormattedString(), i, j);
				}
			}
		}

		public boolean mouseClicked(int i, int j, int k) {
			if (!this.field_2396.contains(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2396.getY()) / 12 + this.inWindowIndex;
				if (l >= 0 && l < this.suggestions.getList().size()) {
					this.select(l);
					this.complete();
				}

				return true;
			}
		}

		public boolean mouseScrolled(double d) {
			int i = (int)(
				ChatScreen.this.minecraft.field_1729.getX()
					* (double)ChatScreen.this.minecraft.window.getScaledWidth()
					/ (double)ChatScreen.this.minecraft.window.getWidth()
			);
			int j = (int)(
				ChatScreen.this.minecraft.field_1729.getY()
					* (double)ChatScreen.this.minecraft.window.getScaledHeight()
					/ (double)ChatScreen.this.minecraft.window.getHeight()
			);
			if (this.field_2396.contains(i, j)) {
				this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - d), 0, Math.max(this.suggestions.getList().size() - 10, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean keyPressed(int i, int j, int k) {
			if (i == 265) {
				this.scroll(-1);
				this.completed = false;
				return true;
			} else if (i == 264) {
				this.scroll(1);
				this.completed = false;
				return true;
			} else if (i == 258) {
				if (this.completed) {
					this.scroll(Screen.hasShiftDown() ? -1 : 1);
				}

				this.complete();
				return true;
			} else if (i == 256) {
				this.close();
				return true;
			} else {
				return false;
			}
		}

		public void scroll(int i) {
			this.select(this.selection + i);
			int j = this.inWindowIndex;
			int k = this.inWindowIndex + 10 - 1;
			if (this.selection < j) {
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.getList().size() - 10, 0));
			} else if (this.selection > k) {
				this.inWindowIndex = MathHelper.clamp(this.selection + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
			}
		}

		public void select(int i) {
			this.selection = i;
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
			ChatScreen.this.chatField.setCursor(i);
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
