package net.minecraft.client.gui.ingame;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
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
import net.minecraft.class_768;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class ChatGui extends Gui {
	private static final Pattern field_2377 = Pattern.compile("(\\s+)");
	private String field_2389 = "";
	private int field_2387 = -1;
	protected TextFieldWidget chatField;
	private String field_2384 = "";
	protected final List<String> field_2383 = Lists.<String>newArrayList();
	protected int field_2381;
	protected int field_2379;
	private ParseResults<CommandSource> field_2388;
	private CompletableFuture<Suggestions> field_2386;
	private ChatGui.class_409 field_2385;
	private boolean field_2380;
	private boolean field_2378;

	public ChatGui() {
	}

	public ChatGui(String string) {
		this.field_2384 = string;
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.chatField;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2387 = this.client.hudInGame.getHudChat().method_1809().size();
		this.chatField = new TextFieldWidget(0, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
		this.chatField.setMaxLength(256);
		this.chatField.method_1858(false);
		this.chatField.method_1876(true);
		this.chatField.setText(this.field_2384);
		this.chatField.method_1856(false);
		this.chatField.method_1854(this::method_2102);
		this.chatField.method_1863(this::method_2111);
		this.listeners.add(this.chatField);
		this.method_2110();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.chatField.getText();
		this.initialize(minecraftClient, i, j);
		this.method_2108(string);
		this.method_2110();
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		this.client.hudInGame.getHudChat().method_1820();
	}

	@Override
	public void update() {
		this.chatField.tick();
	}

	private void method_2111(int i, String string) {
		String string2 = this.chatField.getText();
		this.field_2380 = !string2.equals(this.field_2384);
		this.method_2110();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_2385 != null && this.field_2385.method_2124(i, j, k)) {
			return true;
		} else if (i == 256) {
			this.client.openGui(null);
			return true;
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.method_2230(string);
			}

			this.client.openGui(null);
			return true;
		} else if (i == 265) {
			this.method_2114(-1);
			return true;
		} else if (i == 264) {
			this.method_2114(1);
			return true;
		} else if (i == 266) {
			this.client.hudInGame.getHudChat().method_1802((double)(this.client.hudInGame.getHudChat().method_1813() - 1));
			return true;
		} else if (i == 267) {
			this.client.hudInGame.getHudChat().method_1802((double)(-this.client.hudInGame.getHudChat().method_1813() + 1));
			return true;
		} else {
			if (i == 258) {
				this.field_2380 = true;
				this.method_2112();
			}

			return this.chatField.keyPressed(i, j, k);
		}
	}

	public void method_2112() {
		if (this.field_2386 != null && this.field_2386.isDone()) {
			int i = 0;
			Suggestions suggestions = (Suggestions)this.field_2386.join();
			if (!suggestions.getList().isEmpty()) {
				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.fontRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(this.chatField.method_1889(suggestions.getRange().getStart()), 0, this.width - i);
				this.field_2385 = new ChatGui.class_409(j, this.height - 12, i, suggestions);
			}
		}
	}

	private static int method_2104(String string) {
		if (Strings.isNullOrEmpty(string)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = field_2377.matcher(string);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	private void method_2110() {
		this.field_2388 = null;
		if (!this.field_2378) {
			this.chatField.method_1887(null);
			this.field_2385 = null;
		}

		this.field_2383.clear();
		String string = this.chatField.getText();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
			CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.method_2886();
			this.field_2388 = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
			if (this.field_2385 == null || !this.field_2378) {
				StringReader stringReader2 = new StringReader(string.substring(0, Math.min(string.length(), this.chatField.getId())));
				if (stringReader2.canRead() && stringReader2.peek() == '/') {
					stringReader2.skip();
					ParseResults<CommandSource> parseResults = commandDispatcher.parse(stringReader2, this.client.player.networkHandler.getCommandSource());
					this.field_2386 = commandDispatcher.getCompletionSuggestions(parseResults);
					this.field_2386.thenRun(() -> {
						if (this.field_2386.isDone()) {
							this.method_2116();
						}
					});
				}
			}
		} else {
			int i = method_2104(string);
			Collection<String> collection = this.client.player.networkHandler.getCommandSource().getPlayerNames();
			this.field_2386 = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string, i));
		}
	}

	private void method_2116() {
		if (((Suggestions)this.field_2386.join()).isEmpty()
			&& !this.field_2388.getExceptions().isEmpty()
			&& this.chatField.getId() == this.chatField.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.field_2388.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.field_2383.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.field_2383.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_2381 = 0;
		this.field_2379 = this.width;
		if (this.field_2383.isEmpty()) {
			this.method_2107(TextFormat.GRAY);
		}

		this.field_2385 = null;
		if (this.field_2380 && this.client.options.autoSuggestions) {
			this.method_2112();
		}
	}

	private String method_2102(String string, int i) {
		return this.field_2388 != null ? method_2105(this.field_2388, string, i) : string;
	}

	public static String method_2105(ParseResults<CommandSource> parseResults, String string, int i) {
		TextFormat[] textFormats = new TextFormat[]{TextFormat.AQUA, TextFormat.YELLOW, TextFormat.GREEN, TextFormat.LIGHT_PURPLE, TextFormat.GOLD};
		String string2 = TextFormat.GRAY.toString();
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
				stringBuilder.append(TextFormat.RED);
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

		if (this.field_2385 != null && this.field_2385.method_2117(d)) {
			return true;
		} else {
			if (!isShiftPressed()) {
				d *= 7.0;
			}

			this.client.hudInGame.getHudChat().method_1802(d);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2385 != null && this.field_2385.method_2119((int)d, (int)e, i)) {
			return true;
		} else {
			if (i == 0) {
				TextComponent textComponent = this.client.hudInGame.getHudChat().method_1816(d, e);
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
		int k = this.client.hudInGame.getHudChat().method_1809().size();
		j = MathHelper.clamp(j, 0, k);
		if (j != this.field_2387) {
			if (j == k) {
				this.field_2387 = k;
				this.chatField.setText(this.field_2389);
			} else {
				if (this.field_2387 == k) {
					this.field_2389 = this.chatField.getText();
				}

				this.chatField.setText((String)this.client.hudInGame.getHudChat().method_1809().get(j));
				this.field_2385 = null;
				this.field_2387 = j;
				this.field_2380 = false;
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
		this.chatField.render(i, j, f);
		if (this.field_2385 != null) {
			this.field_2385.method_2120(i, j);
		} else {
			int k = 0;

			for (String string : this.field_2383) {
				drawRect(this.field_2381 - 1, this.height - 14 - 13 - 12 * k, this.field_2381 + this.field_2379 + 1, this.height - 2 - 13 - 12 * k, -16777216);
				this.fontRenderer.drawWithShadow(string, (float)this.field_2381, (float)(this.height - 14 - 13 + 2 - 12 * k), -1);
				k++;
			}
		}

		TextComponent textComponent = this.client.hudInGame.getHudChat().method_1816((double)i, (double)j);
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
		CommandContextBuilder<CommandSource> commandContextBuilder = this.field_2388.getContext();
		CommandContextBuilder<CommandSource> commandContextBuilder2 = commandContextBuilder.getLastChild();
		if (!commandContextBuilder2.getNodes().isEmpty()) {
			CommandNode<CommandSource> commandNode;
			int i;
			if (this.field_2388.getReader().canRead()) {
				Entry<CommandNode<CommandSource>, StringRange> entry = Iterables.getLast(commandContextBuilder2.getNodes().entrySet());
				commandNode = (CommandNode<CommandSource>)entry.getKey();
				i = ((StringRange)entry.getValue()).getEnd() + 1;
			} else if (commandContextBuilder2.getNodes().size() > 1) {
				Entry<CommandNode<CommandSource>, StringRange> entry = Iterables.get(
					commandContextBuilder2.getNodes().entrySet(), commandContextBuilder2.getNodes().size() - 2
				);
				commandNode = (CommandNode<CommandSource>)entry.getKey();
				i = ((StringRange)entry.getValue()).getEnd() + 1;
			} else {
				if (commandContextBuilder == commandContextBuilder2 || commandContextBuilder2.getNodes().isEmpty()) {
					return;
				}

				Entry<CommandNode<CommandSource>, StringRange> entry = Iterables.getLast(commandContextBuilder2.getNodes().entrySet());
				commandNode = (CommandNode<CommandSource>)entry.getKey();
				i = ((StringRange)entry.getValue()).getEnd() + 1;
			}

			Map<CommandNode<CommandSource>, String> map = this.client
				.player
				.networkHandler
				.method_2886()
				.getSmartUsage(commandNode, this.client.player.networkHandler.getCommandSource());
			List<String> list = Lists.<String>newArrayList();
			int j = 0;

			for (Entry<CommandNode<CommandSource>, String> entry2 : map.entrySet()) {
				if (!(entry2.getKey() instanceof LiteralCommandNode)) {
					list.add(textFormat + (String)entry2.getValue());
					j = Math.max(j, this.fontRenderer.getStringWidth((String)entry2.getValue()));
				}
			}

			if (!list.isEmpty()) {
				this.field_2383.addAll(list);
				this.field_2381 = MathHelper.clamp(this.chatField.method_1889(i) + this.fontRenderer.getStringWidth(" "), 0, this.width - j);
				this.field_2379 = j;
			}
		}
	}

	@Nullable
	private static String method_2103(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	private void method_2108(String string) {
		this.chatField.setText(string);
	}

	@Environment(EnvType.CLIENT)
	class class_409 {
		private final class_768 field_2396;
		private final Suggestions field_2390;
		private final String field_2394;
		private int field_2395;
		private int field_2392;
		private Vec2f field_2393 = Vec2f.ZERO;
		private boolean field_2391;

		private class_409(int i, int j, int k, Suggestions suggestions) {
			this.field_2396 = new class_768(i - 1, j - 3 - Math.min(suggestions.getList().size(), 10) * 12, k + 1, Math.min(suggestions.getList().size(), 10) * 12);
			this.field_2390 = suggestions;
			this.field_2394 = ChatGui.this.chatField.getText();
			this.method_2121(0);
		}

		public void method_2120(int i, int j) {
			int k = Math.min(this.field_2390.getList().size(), 10);
			int l = -5592406;
			boolean bl = this.field_2395 > 0;
			boolean bl2 = this.field_2390.getList().size() > this.field_2395 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.field_2393.x != (float)i || this.field_2393.y != (float)j;
			if (bl4) {
				this.field_2393 = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				Drawable.drawRect(
					this.field_2396.method_3321(),
					this.field_2396.method_3322() - 1,
					this.field_2396.method_3321() + this.field_2396.method_3319(),
					this.field_2396.method_3322(),
					-805306368
				);
				Drawable.drawRect(
					this.field_2396.method_3321(),
					this.field_2396.method_3322() + this.field_2396.method_3320(),
					this.field_2396.method_3321() + this.field_2396.method_3319(),
					this.field_2396.method_3322() + this.field_2396.method_3320() + 1,
					-805306368
				);
				if (bl) {
					for (int m = 0; m < this.field_2396.method_3319(); m++) {
						if (m % 2 == 0) {
							Drawable.drawRect(
								this.field_2396.method_3321() + m, this.field_2396.method_3322() - 1, this.field_2396.method_3321() + m + 1, this.field_2396.method_3322(), -1
							);
						}
					}
				}

				if (bl2) {
					for (int mx = 0; mx < this.field_2396.method_3319(); mx++) {
						if (mx % 2 == 0) {
							Drawable.drawRect(
								this.field_2396.method_3321() + mx,
								this.field_2396.method_3322() + this.field_2396.method_3320(),
								this.field_2396.method_3321() + mx + 1,
								this.field_2396.method_3322() + this.field_2396.method_3320() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int n = 0; n < k; n++) {
				Suggestion suggestion = (Suggestion)this.field_2390.getList().get(n + this.field_2395);
				Drawable.drawRect(
					this.field_2396.method_3321(),
					this.field_2396.method_3322() + 12 * n,
					this.field_2396.method_3321() + this.field_2396.method_3319(),
					this.field_2396.method_3322() + 12 * n + 12,
					-805306368
				);
				if (i > this.field_2396.method_3321()
					&& i < this.field_2396.method_3321() + this.field_2396.method_3319()
					&& j > this.field_2396.method_3322() + 12 * n
					&& j < this.field_2396.method_3322() + 12 * n + 12) {
					if (bl4) {
						this.method_2121(n + this.field_2395);
					}

					bl5 = true;
				}

				ChatGui.this.fontRenderer
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_2396.method_3321() + 1),
						(float)(this.field_2396.method_3322() + 2 + 12 * n),
						n + this.field_2395 == this.field_2392 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_2390.getList().get(this.field_2392)).getTooltip();
				if (message != null) {
					ChatGui.this.drawTooltip(TextFormatter.message(message).getFormattedText(), i, j);
				}
			}
		}

		public boolean method_2119(int i, int j, int k) {
			if (!this.field_2396.method_3318(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2396.method_3322()) / 12 + this.field_2395;
				if (l >= 0 && l < this.field_2390.getList().size()) {
					this.method_2121(l);
					this.method_2122();
				}

				return true;
			}
		}

		public boolean method_2117(double d) {
			int i = (int)(
				ChatGui.this.client.mouse.method_1603() * (double)ChatGui.this.client.window.getScaledWidth() / (double)ChatGui.this.client.window.method_4480()
			);
			int j = (int)(
				ChatGui.this.client.mouse.method_1604() * (double)ChatGui.this.client.window.getScaledHeight() / (double)ChatGui.this.client.window.method_4507()
			);
			if (this.field_2396.method_3318(i, j)) {
				this.field_2395 = MathHelper.clamp((int)((double)this.field_2395 - d), 0, Math.max(this.field_2390.getList().size() - 10, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean method_2124(int i, int j, int k) {
			if (i == 265) {
				this.method_2118(-1);
				this.field_2391 = false;
				return true;
			} else if (i == 264) {
				this.method_2118(1);
				this.field_2391 = false;
				return true;
			} else if (i == 258) {
				if (this.field_2391) {
					this.method_2118(Gui.isShiftPressed() ? -1 : 1);
				}

				this.method_2122();
				return true;
			} else if (i == 256) {
				this.method_2123();
				return true;
			} else {
				return false;
			}
		}

		public void method_2118(int i) {
			this.method_2121(this.field_2392 + i);
			int j = this.field_2395;
			int k = this.field_2395 + 10 - 1;
			if (this.field_2392 < j) {
				this.field_2395 = MathHelper.clamp(this.field_2392, 0, Math.max(this.field_2390.getList().size() - 10, 0));
			} else if (this.field_2392 > k) {
				this.field_2395 = MathHelper.clamp(this.field_2392 + 1 - 10, 0, Math.max(this.field_2390.getList().size() - 10, 0));
			}
		}

		public void method_2121(int i) {
			this.field_2392 = i;
			if (this.field_2392 < 0) {
				this.field_2392 = this.field_2392 + this.field_2390.getList().size();
			}

			if (this.field_2392 >= this.field_2390.getList().size()) {
				this.field_2392 = this.field_2392 - this.field_2390.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.field_2390.getList().get(this.field_2392);
			ChatGui.this.chatField.method_1887(ChatGui.method_2103(ChatGui.this.chatField.getText(), suggestion.apply(this.field_2394)));
		}

		public void method_2122() {
			Suggestion suggestion = (Suggestion)this.field_2390.getList().get(this.field_2392);
			ChatGui.this.field_2378 = true;
			ChatGui.this.method_2108(suggestion.apply(this.field_2394));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			ChatGui.this.chatField.method_1875(i);
			ChatGui.this.chatField.method_1884(i);
			this.method_2121(this.field_2392);
			ChatGui.this.field_2378 = false;
			this.field_2391 = true;
		}

		public void method_2123() {
			ChatGui.this.field_2385 = null;
		}
	}
}
