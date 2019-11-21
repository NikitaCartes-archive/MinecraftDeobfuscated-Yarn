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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class CommandSuggestor {
	private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(\\s+)");
	private final MinecraftClient client;
	private final Screen owner;
	private final TextFieldWidget textField;
	private final TextRenderer textRenderer;
	private final boolean slashRequired;
	private final boolean suggestingWhenEmpty;
	private final int inWindowIndexOffset;
	private final int maxSuggestionSize;
	private final boolean chatScreenSized;
	private final int color;
	private final List<String> messages = Lists.<String>newArrayList();
	private int x;
	private int width;
	private ParseResults<CommandSource> parse;
	private CompletableFuture<Suggestions> pendingSuggestions;
	private CommandSuggestor.SuggestionWindow window;
	private boolean windowActive;
	private boolean completingSuggestions;

	public CommandSuggestor(
		MinecraftClient client,
		Screen owner,
		TextFieldWidget textField,
		TextRenderer textRenderer,
		boolean slashRequired,
		boolean suggestingWhenEmpty,
		int inWindowIndexOffset,
		int maxSuggestionSize,
		boolean chatScreenSized,
		int color
	) {
		this.client = client;
		this.owner = owner;
		this.textField = textField;
		this.textRenderer = textRenderer;
		this.slashRequired = slashRequired;
		this.suggestingWhenEmpty = suggestingWhenEmpty;
		this.inWindowIndexOffset = inWindowIndexOffset;
		this.maxSuggestionSize = maxSuggestionSize;
		this.chatScreenSized = chatScreenSized;
		this.color = color;
		textField.setRenderTextProvider(this::provideRenderText);
	}

	public void setWindowActive(boolean windowActive) {
		this.windowActive = windowActive;
		if (!windowActive) {
			this.window = null;
		}
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.window != null && this.window.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (this.owner.getFocused() == this.textField && keyCode == 258) {
			this.showSuggestions(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean mouseScrolled(double amount) {
		return this.window != null && this.window.mouseScrolled(MathHelper.clamp(amount, -1.0, 1.0));
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.window != null && this.window.mouseClicked((int)mouseX, (int)mouseY, button);
	}

	public void showSuggestions(boolean narrateFirstSuggestion) {
		if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
			Suggestions suggestions = (Suggestions)this.pendingSuggestions.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.textRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i
				);
				int k = this.chatScreenSized ? this.owner.height - 12 : 72;
				this.window = new CommandSuggestor.SuggestionWindow(j, k, i, suggestions, narrateFirstSuggestion);
			}
		}
	}

	public void refresh() {
		String string = this.textField.getText();
		if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
			this.parse = null;
		}

		if (!this.completingSuggestions) {
			this.textField.setSuggestion(null);
			this.window = null;
		}

		this.messages.clear();
		StringReader stringReader = new StringReader(string);
		boolean bl = stringReader.canRead() && stringReader.peek() == '/';
		if (bl) {
			stringReader.skip();
		}

		boolean bl2 = this.slashRequired || bl;
		int i = this.textField.getCursor();
		if (bl2) {
			CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
			if (this.parse == null) {
				this.parse = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
			}

			int j = this.suggestingWhenEmpty ? stringReader.getCursor() : 1;
			if (i >= j && (this.window == null || !this.completingSuggestions)) {
				this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, i);
				this.pendingSuggestions.thenRun(() -> {
					if (this.pendingSuggestions.isDone()) {
						this.show();
					}
				});
			}
		} else {
			String string2 = string.substring(0, i);
			int j = getLastPlayerNameStart(string2);
			Collection<String> collection = this.client.player.networkHandler.getCommandSource().getPlayerNames();
			this.pendingSuggestions = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string2, j));
		}
	}

	private static int getLastPlayerNameStart(String input) {
		if (Strings.isNullOrEmpty(input)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = BACKSLASH_S_PATTERN.matcher(input);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	public void show() {
		if (this.textField.getCursor() == this.textField.getText().length()) {
			if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.parse.getExceptions().isEmpty()) {
				int i = 0;

				for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parse.getExceptions().entrySet()) {
					CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
					if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
						i++;
					} else {
						this.messages.add(commandSyntaxException.getMessage());
					}
				}

				if (i > 0) {
					this.messages.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
				}
			} else if (this.parse.getReader().canRead()) {
				this.messages.add(CommandManager.getException(this.parse).getMessage());
			}
		}

		this.x = 0;
		this.width = this.owner.width;
		if (this.messages.isEmpty()) {
			this.showUsages(Formatting.GRAY);
		}

		this.window = null;
		if (this.windowActive && this.client.options.autoSuggestions) {
			this.showSuggestions(false);
		}
	}

	private void showUsages(Formatting formatting) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.parse.getContext();
		SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
		Map<CommandNode<CommandSource>, String> map = this.client
			.player
			.networkHandler
			.getCommandDispatcher()
			.getSmartUsage(suggestionContext.parent, this.client.player.networkHandler.getCommandSource());
		List<String> list = Lists.<String>newArrayList();
		int i = 0;

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(formatting + (String)entry.getValue());
				i = Math.max(i, this.textRenderer.getStringWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.messages.addAll(list);
			this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
			this.width = i;
		}
	}

	private String provideRenderText(String original, int firstCharacterIndex) {
		return this.parse != null ? highlight(this.parse, original, firstCharacterIndex) : original;
	}

	@Nullable
	private static String getSuggestionSuffix(String original, String suggestion) {
		return suggestion.startsWith(original) ? suggestion.substring(original.length()) : null;
	}

	public static String highlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex) {
		Formatting[] formattings = new Formatting[]{Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD};
		String string = Formatting.GRAY.toString();
		StringBuilder stringBuilder = new StringBuilder(string);
		int i = 0;
		int j = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++j >= formattings.length) {
				j = 0;
			}

			int k = Math.max(parsedArgument.getRange().getStart() - firstCharacterIndex, 0);
			if (k >= original.length()) {
				break;
			}

			int l = Math.min(parsedArgument.getRange().getEnd() - firstCharacterIndex, original.length());
			if (l > 0) {
				stringBuilder.append(original, i, k);
				stringBuilder.append(formattings[j]);
				stringBuilder.append(original, k, l);
				stringBuilder.append(string);
				i = l;
			}
		}

		if (parse.getReader().canRead()) {
			int m = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0);
			if (m < original.length()) {
				int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
				stringBuilder.append(original, i, m);
				stringBuilder.append(Formatting.RED);
				stringBuilder.append(original, m, n);
				i = n;
			}
		}

		stringBuilder.append(original, i, original.length());
		return stringBuilder.toString();
	}

	public void render(int mouseX, int mouseY) {
		if (this.window != null) {
			this.window.render(mouseX, mouseY);
		} else {
			int i = 0;

			for (String string : this.messages) {
				int j = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * i : 72 + 12 * i;
				DrawableHelper.fill(this.x - 1, j, this.x + this.width + 1, j + 12, this.color);
				this.textRenderer.drawWithShadow(string, (float)this.x, (float)(j + 2), -1);
				i++;
			}
		}
	}

	public String method_23958() {
		return this.window != null ? "\n" + this.window.getNarration() : "";
	}

	@Environment(EnvType.CLIENT)
	public class SuggestionWindow {
		private final Rect2i area;
		private final Suggestions suggestions;
		private final String typedText;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;
		private int lastNarrationIndex;

		private SuggestionWindow(int x, int y, int width, Suggestions suggestions, boolean narrateFirstSuggestion) {
			int i = x - 1;
			int j = CommandSuggestor.this.chatScreenSized ? y - 3 - Math.min(suggestions.getList().size(), CommandSuggestor.this.maxSuggestionSize) * 12 : y;
			this.area = new Rect2i(i, j, width + 1, Math.min(suggestions.getList().size(), CommandSuggestor.this.maxSuggestionSize) * 12);
			this.suggestions = suggestions;
			this.typedText = CommandSuggestor.this.textField.getText();
			this.lastNarrationIndex = narrateFirstSuggestion ? -1 : 0;
			this.select(0);
		}

		public void render(int mouseX, int mouseY) {
			int i = Math.min(this.suggestions.getList().size(), CommandSuggestor.this.maxSuggestionSize);
			int j = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.suggestions.getList().size() > this.inWindowIndex + i;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)mouseX || this.mouse.y != (float)mouseY;
			if (bl4) {
				this.mouse = new Vec2f((float)mouseX, (float)mouseY);
			}

			if (bl3) {
				DrawableHelper.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), CommandSuggestor.this.color);
				DrawableHelper.fill(
					this.area.getX(),
					this.area.getY() + this.area.getHeight(),
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + this.area.getHeight() + 1,
					CommandSuggestor.this.color
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
				DrawableHelper.fill(
					this.area.getX(), this.area.getY() + 12 * l, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * l + 12, CommandSuggestor.this.color
				);
				if (mouseX > this.area.getX()
					&& mouseX < this.area.getX() + this.area.getWidth()
					&& mouseY > this.area.getY() + 12 * l
					&& mouseY < this.area.getY() + 12 * l + 12) {
					if (bl4) {
						this.select(l + this.inWindowIndex);
					}

					bl5 = true;
				}

				CommandSuggestor.this.textRenderer
					.drawWithShadow(
						suggestion.getText(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * l), l + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.getList().get(this.selection)).getTooltip();
				if (message != null) {
					CommandSuggestor.this.owner.renderTooltip(Texts.toText(message).asFormattedString(), mouseX, mouseY);
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
				CommandSuggestor.this.client.mouse.getX()
					* (double)CommandSuggestor.this.client.getWindow().getScaledWidth()
					/ (double)CommandSuggestor.this.client.getWindow().getWidth()
			);
			int j = (int)(
				CommandSuggestor.this.client.mouse.getY()
					* (double)CommandSuggestor.this.client.getWindow().getScaledHeight()
					/ (double)CommandSuggestor.this.client.getWindow().getHeight()
			);
			if (this.area.contains(i, j)) {
				this.inWindowIndex = MathHelper.clamp(
					(int)((double)this.inWindowIndex - amount), 0, Math.max(this.suggestions.getList().size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
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
				this.discard();
				return true;
			} else {
				return false;
			}
		}

		public void scroll(int offset) {
			this.select(this.selection + offset);
			int i = this.inWindowIndex;
			int j = this.inWindowIndex + CommandSuggestor.this.maxSuggestionSize - 1;
			if (this.selection < i) {
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.getList().size() - CommandSuggestor.this.maxSuggestionSize, 0));
			} else if (this.selection > j) {
				this.inWindowIndex = MathHelper.clamp(
					this.selection + CommandSuggestor.this.inWindowIndexOffset - CommandSuggestor.this.maxSuggestionSize,
					0,
					Math.max(this.suggestions.getList().size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
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
			CommandSuggestor.this.textField
				.setSuggestion(CommandSuggestor.getSuggestionSuffix(CommandSuggestor.this.textField.getText(), suggestion.apply(this.typedText)));
			if (NarratorManager.INSTANCE.isActive() && this.lastNarrationIndex != this.selection) {
				NarratorManager.INSTANCE.narrate(this.getNarration());
			}
		}

		public void complete() {
			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
			CommandSuggestor.this.completingSuggestions = true;
			CommandSuggestor.this.textField.setText(suggestion.apply(this.typedText));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			CommandSuggestor.this.textField.setSelectionStart(i);
			CommandSuggestor.this.textField.setSelectionEnd(i);
			this.select(this.selection);
			CommandSuggestor.this.completingSuggestions = false;
			this.completed = true;
		}

		private String getNarration() {
			this.lastNarrationIndex = this.selection;
			List<Suggestion> list = this.suggestions.getList();
			Suggestion suggestion = (Suggestion)list.get(this.selection);
			Message message = suggestion.getTooltip();
			return message != null
				? I18n.translate("narration.suggestion.tooltip", this.selection + 1, list.size(), suggestion.getText(), message.getString())
				: I18n.translate("narration.suggestion", this.selection + 1, list.size(), suggestion.getText());
		}

		public void discard() {
			CommandSuggestor.this.window = null;
		}
	}
}
