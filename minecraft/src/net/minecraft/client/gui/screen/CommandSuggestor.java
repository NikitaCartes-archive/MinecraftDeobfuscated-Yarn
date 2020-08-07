package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class CommandSuggestor {
	private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(\\s+)");
	private static final Style field_25885 = Style.EMPTY.withColor(Formatting.field_1061);
	private static final Style field_25886 = Style.EMPTY.withColor(Formatting.field_1080);
	private static final List<Style> field_25887 = (List<Style>)Stream.of(
			Formatting.field_1075, Formatting.field_1054, Formatting.field_1060, Formatting.field_1076, Formatting.field_1065
		)
		.map(Style.EMPTY::withColor)
		.collect(ImmutableList.toImmutableList());
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
	private final List<OrderedText> messages = Lists.<OrderedText>newArrayList();
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
					i = Math.max(i, this.textRenderer.getWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i
				);
				int k = this.chatScreenSized ? this.owner.height - 12 : 72;
				this.window = new CommandSuggestor.SuggestionWindow(j, k, i, this.method_30104(suggestions), narrateFirstSuggestion);
			}
		}
	}

	private List<Suggestion> method_30104(Suggestions suggestions) {
		String string = this.textField.getText().substring(0, this.textField.getCursor());
		int i = getLastPlayerNameStart(string);
		String string2 = string.substring(i).toLowerCase(Locale.ROOT);
		List<Suggestion> list = Lists.<Suggestion>newArrayList();
		List<Suggestion> list2 = Lists.<Suggestion>newArrayList();

		for (Suggestion suggestion : suggestions.getList()) {
			if (!suggestion.getText().startsWith(string2) && !suggestion.getText().startsWith("minecraft:" + string2)) {
				list2.add(suggestion);
			} else {
				list.add(suggestion);
			}
		}

		list.addAll(list2);
		return list;
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

	private static OrderedText method_30505(CommandSyntaxException commandSyntaxException) {
		Text text = Texts.toText(commandSyntaxException.getRawMessage());
		String string = commandSyntaxException.getContext();
		return string == null
			? text.asOrderedText()
			: new TranslatableText("command.context.parse_error", text, commandSyntaxException.getCursor(), string).asOrderedText();
	}

	private void show() {
		if (this.textField.getCursor() == this.textField.getText().length()) {
			if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.parse.getExceptions().isEmpty()) {
				int i = 0;

				for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parse.getExceptions().entrySet()) {
					CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
					if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
						i++;
					} else {
						this.messages.add(method_30505(commandSyntaxException));
					}
				}

				if (i > 0) {
					this.messages.add(method_30505(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
				}
			} else if (this.parse.getReader().canRead()) {
				this.messages.add(method_30505(CommandManager.getException(this.parse)));
			}
		}

		this.x = 0;
		this.width = this.owner.width;
		if (this.messages.isEmpty()) {
			this.showUsages(Formatting.field_1080);
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
		List<OrderedText> list = Lists.<OrderedText>newArrayList();
		int i = 0;
		Style style = Style.EMPTY.withColor(formatting);

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(OrderedText.styledString((String)entry.getValue(), style));
				i = Math.max(i, this.textRenderer.getWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.messages.addAll(list);
			this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
			this.width = i;
		}
	}

	private OrderedText provideRenderText(String original, int firstCharacterIndex) {
		return this.parse != null ? highlight(this.parse, original, firstCharacterIndex) : OrderedText.styledString(original, Style.EMPTY);
	}

	@Nullable
	private static String getSuggestionSuffix(String original, String suggestion) {
		return suggestion.startsWith(original) ? suggestion.substring(original.length()) : null;
	}

	private static OrderedText highlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex) {
		List<OrderedText> list = Lists.<OrderedText>newArrayList();
		int i = 0;
		int j = -1;
		CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();

		for (ParsedArgument<CommandSource, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++j >= field_25887.size()) {
				j = 0;
			}

			int k = Math.max(parsedArgument.getRange().getStart() - firstCharacterIndex, 0);
			if (k >= original.length()) {
				break;
			}

			int l = Math.min(parsedArgument.getRange().getEnd() - firstCharacterIndex, original.length());
			if (l > 0) {
				list.add(OrderedText.styledString(original.substring(i, k), field_25886));
				list.add(OrderedText.styledString(original.substring(k, l), (Style)field_25887.get(j)));
				i = l;
			}
		}

		if (parse.getReader().canRead()) {
			int m = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0);
			if (m < original.length()) {
				int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
				list.add(OrderedText.styledString(original.substring(i, m), field_25886));
				list.add(OrderedText.styledString(original.substring(m, n), field_25885));
				i = n;
			}
		}

		list.add(OrderedText.styledString(original.substring(i), field_25886));
		return OrderedText.concat(list);
	}

	public void render(MatrixStack matrixStack, int i, int j) {
		if (this.window != null) {
			this.window.render(matrixStack, i, j);
		} else {
			int k = 0;

			for (OrderedText orderedText : this.messages) {
				int l = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * k : 72 + 12 * k;
				DrawableHelper.fill(matrixStack, this.x - 1, l, this.x + this.width + 1, l + 12, this.color);
				this.textRenderer.drawWithShadow(matrixStack, orderedText, (float)this.x, (float)(l + 2), -1);
				k++;
			}
		}
	}

	public String method_23958() {
		return this.window != null ? "\n" + this.window.getNarration() : "";
	}

	@Environment(EnvType.CLIENT)
	public class SuggestionWindow {
		private final Rect2i area;
		private final String typedText;
		private final List<Suggestion> field_25709;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;
		private int lastNarrationIndex;

		private SuggestionWindow(int x, int y, int width, List<Suggestion> list, boolean narrateFirstSuggestion) {
			int i = x - 1;
			int j = CommandSuggestor.this.chatScreenSized ? y - 3 - Math.min(list.size(), CommandSuggestor.this.maxSuggestionSize) * 12 : y;
			this.area = new Rect2i(i, j, width + 1, Math.min(list.size(), CommandSuggestor.this.maxSuggestionSize) * 12);
			this.typedText = CommandSuggestor.this.textField.getText();
			this.lastNarrationIndex = narrateFirstSuggestion ? -1 : 0;
			this.field_25709 = list;
			this.select(0);
		}

		public void render(MatrixStack matrixStack, int i, int j) {
			int k = Math.min(this.field_25709.size(), CommandSuggestor.this.maxSuggestionSize);
			int l = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.field_25709.size() > this.inWindowIndex + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)i || this.mouse.y != (float)j;
			if (bl4) {
				this.mouse = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.fill(
					matrixStack, this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), CommandSuggestor.this.color
				);
				DrawableHelper.fill(
					matrixStack,
					this.area.getX(),
					this.area.getY() + this.area.getHeight(),
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + this.area.getHeight() + 1,
					CommandSuggestor.this.color
				);
				if (bl) {
					for (int m = 0; m < this.area.getWidth(); m++) {
						if (m % 2 == 0) {
							DrawableHelper.fill(matrixStack, this.area.getX() + m, this.area.getY() - 1, this.area.getX() + m + 1, this.area.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int mx = 0; mx < this.area.getWidth(); mx++) {
						if (mx % 2 == 0) {
							DrawableHelper.fill(
								matrixStack,
								this.area.getX() + mx,
								this.area.getY() + this.area.getHeight(),
								this.area.getX() + mx + 1,
								this.area.getY() + this.area.getHeight() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int n = 0; n < k; n++) {
				Suggestion suggestion = (Suggestion)this.field_25709.get(n + this.inWindowIndex);
				DrawableHelper.fill(
					matrixStack,
					this.area.getX(),
					this.area.getY() + 12 * n,
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + 12 * n + 12,
					CommandSuggestor.this.color
				);
				if (i > this.area.getX() && i < this.area.getX() + this.area.getWidth() && j > this.area.getY() + 12 * n && j < this.area.getY() + 12 * n + 12) {
					if (bl4) {
						this.select(n + this.inWindowIndex);
					}

					bl5 = true;
				}

				CommandSuggestor.this.textRenderer
					.drawWithShadow(
						matrixStack,
						suggestion.getText(),
						(float)(this.area.getX() + 1),
						(float)(this.area.getY() + 2 + 12 * n),
						n + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_25709.get(this.selection)).getTooltip();
				if (message != null) {
					CommandSuggestor.this.owner.renderTooltip(matrixStack, Texts.toText(message), i, j);
				}
			}
		}

		public boolean mouseClicked(int x, int y, int button) {
			if (!this.area.contains(x, y)) {
				return false;
			} else {
				int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
				if (i >= 0 && i < this.field_25709.size()) {
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
					(int)((double)this.inWindowIndex - amount), 0, Math.max(this.field_25709.size() - CommandSuggestor.this.maxSuggestionSize, 0)
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
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.field_25709.size() - CommandSuggestor.this.maxSuggestionSize, 0));
			} else if (this.selection > j) {
				this.inWindowIndex = MathHelper.clamp(
					this.selection + CommandSuggestor.this.inWindowIndexOffset - CommandSuggestor.this.maxSuggestionSize,
					0,
					Math.max(this.field_25709.size() - CommandSuggestor.this.maxSuggestionSize, 0)
				);
			}
		}

		public void select(int index) {
			this.selection = index;
			if (this.selection < 0) {
				this.selection = this.selection + this.field_25709.size();
			}

			if (this.selection >= this.field_25709.size()) {
				this.selection = this.selection - this.field_25709.size();
			}

			Suggestion suggestion = (Suggestion)this.field_25709.get(this.selection);
			CommandSuggestor.this.textField
				.setSuggestion(CommandSuggestor.getSuggestionSuffix(CommandSuggestor.this.textField.getText(), suggestion.apply(this.typedText)));
			if (NarratorManager.INSTANCE.isActive() && this.lastNarrationIndex != this.selection) {
				NarratorManager.INSTANCE.narrate(this.getNarration());
			}
		}

		public void complete() {
			Suggestion suggestion = (Suggestion)this.field_25709.get(this.selection);
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
			Suggestion suggestion = (Suggestion)this.field_25709.get(this.selection);
			Message message = suggestion.getTooltip();
			return message != null
				? I18n.translate("narration.suggestion.tooltip", this.selection + 1, this.field_25709.size(), suggestion.getText(), message.getString())
				: I18n.translate("narration.suggestion", this.selection + 1, this.field_25709.size(), suggestion.getText());
		}

		public void discard() {
			CommandSuggestor.this.window = null;
		}
	}
}
