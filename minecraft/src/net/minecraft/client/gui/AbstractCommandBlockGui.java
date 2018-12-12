package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_768;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandSource;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public abstract class AbstractCommandBlockGui extends Gui {
	protected TextFieldWidget consoleCommandTextField;
	protected TextFieldWidget previousOutputTextField;
	protected ButtonWidget doneButton;
	protected ButtonWidget cancelButton;
	protected ButtonWidget toggleTrackingOutputButton;
	protected boolean field_2752;
	protected final List<String> field_2761 = Lists.<String>newArrayList();
	protected int field_2757;
	protected int field_2756;
	protected ParseResults<CommandSource> field_2758;
	protected CompletableFuture<Suggestions> field_2754;
	protected AbstractCommandBlockGui.class_464 field_2759;
	private boolean field_2750;

	@Override
	public void update() {
		this.consoleCommandTextField.tick();
	}

	abstract CommandBlockExecutor getCommandExecutor();

	abstract int method_2364();

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				AbstractCommandBlockGui.this.method_2359();
			}
		});
		this.cancelButton = this.addButton(new ButtonWidget(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				AbstractCommandBlockGui.this.method_2358();
			}
		});
		this.toggleTrackingOutputButton = this.addButton(new ButtonWidget(4, this.width / 2 + 150 - 20, this.method_2364(), 20, 20, "O") {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockExecutor commandBlockExecutor = AbstractCommandBlockGui.this.getCommandExecutor();
				commandBlockExecutor.shouldTrackOutput(!commandBlockExecutor.isTrackingOutput());
				AbstractCommandBlockGui.this.method_2368();
			}
		});
		this.consoleCommandTextField = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 150, 50, 300, 20) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(bl);
				if (bl) {
					AbstractCommandBlockGui.this.previousOutputTextField.setFocused(false);
				}
			}
		};
		this.consoleCommandTextField.setMaxLength(32500);
		this.consoleCommandTextField.method_1854(this::method_2348);
		this.consoleCommandTextField.setChangedListener(this::method_2360);
		this.listeners.add(this.consoleCommandTextField);
		this.previousOutputTextField = new TextFieldWidget(3, this.fontRenderer, this.width / 2 - 150, this.method_2364(), 276, 20) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(bl);
				if (bl) {
					AbstractCommandBlockGui.this.consoleCommandTextField.setFocused(false);
				}
			}
		};
		this.previousOutputTextField.setMaxLength(32500);
		this.previousOutputTextField.setIsEditable(false);
		this.previousOutputTextField.setText("-");
		this.listeners.add(this.previousOutputTextField);
		this.consoleCommandTextField.setFocused(true);
		this.setFocused(this.consoleCommandTextField);
		this.method_2353();
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.consoleCommandTextField.getText();
		this.initialize(minecraftClient, i, j);
		this.method_2346(string);
		this.method_2353();
	}

	protected void method_2368() {
		if (this.getCommandExecutor().isTrackingOutput()) {
			this.toggleTrackingOutputButton.text = "O";
			this.previousOutputTextField.setText(this.getCommandExecutor().getLastOutput().getString());
		} else {
			this.toggleTrackingOutputButton.text = "X";
			this.previousOutputTextField.setText("-");
		}
	}

	protected void method_2359() {
		CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
		this.method_2352(commandBlockExecutor);
		if (!commandBlockExecutor.isTrackingOutput()) {
			commandBlockExecutor.setLastOutput(null);
		}

		this.client.openGui(null);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	protected abstract void method_2352(CommandBlockExecutor commandBlockExecutor);

	protected void method_2358() {
		this.getCommandExecutor().shouldTrackOutput(this.field_2752);
		this.client.openGui(null);
	}

	@Override
	public void close() {
		this.method_2358();
	}

	private void method_2360(int i, String string) {
		this.method_2353();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 257 || i == 335) {
			this.method_2359();
			return true;
		} else if (this.field_2759 != null && this.field_2759.method_2377(i, j, k)) {
			return true;
		} else {
			if (i == 258) {
				this.method_2357();
			}

			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.field_2759 != null && this.field_2759.method_2370(MathHelper.clamp(d, -1.0, 1.0)) ? true : super.mouseScrolled(d);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_2759 != null && this.field_2759.method_2372((int)d, (int)e, i) ? true : super.mouseClicked(d, e, i);
	}

	protected void method_2353() {
		this.field_2758 = null;
		if (!this.field_2750) {
			this.consoleCommandTextField.setSuggestion(null);
			this.field_2759 = null;
		}

		this.field_2761.clear();
		CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.method_2886();
		String string = this.consoleCommandTextField.getText();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		this.field_2758 = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
		if (this.field_2759 == null || !this.field_2750) {
			StringReader stringReader2 = new StringReader(string.substring(0, Math.min(string.length(), this.consoleCommandTextField.getCursor())));
			if (stringReader2.canRead() && stringReader2.peek() == '/') {
				stringReader2.skip();
			}

			ParseResults<CommandSource> parseResults = commandDispatcher.parse(stringReader2, this.client.player.networkHandler.getCommandSource());
			this.field_2754 = commandDispatcher.getCompletionSuggestions(parseResults);
			this.field_2754.thenRun(() -> {
				if (this.field_2754.isDone()) {
					this.method_2354();
				}
			});
		}
	}

	private void method_2354() {
		if (((Suggestions)this.field_2754.join()).isEmpty()
			&& !this.field_2758.getExceptions().isEmpty()
			&& this.consoleCommandTextField.getCursor() == this.consoleCommandTextField.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.field_2758.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.field_2761.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.field_2761.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_2757 = 0;
		this.field_2756 = this.width;
		if (this.field_2761.isEmpty()) {
			this.method_2356(TextFormat.GRAY);
		}

		this.field_2759 = null;
		if (this.client.field_1690.autoSuggestions) {
			this.method_2357();
		}
	}

	private String method_2348(String string, int i) {
		return this.field_2758 != null ? ChatGui.method_2105(this.field_2758, string, i) : string;
	}

	private void method_2356(TextFormat textFormat) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.field_2758.getContext();
		CommandContextBuilder<CommandSource> commandContextBuilder2 = commandContextBuilder.getLastChild();
		if (!commandContextBuilder2.getNodes().isEmpty()) {
			CommandNode<CommandSource> commandNode;
			int i;
			if (this.field_2758.getReader().canRead()) {
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
				this.field_2761.addAll(list);
				this.field_2757 = MathHelper.clamp(
					this.consoleCommandTextField.method_1889(i) + this.fontRenderer.getStringWidth(" "),
					0,
					this.consoleCommandTextField.method_1889(0) + this.fontRenderer.getStringWidth(" ") + this.consoleCommandTextField.method_1859() - j
				);
				this.field_2756 = j;
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("advMode.command"), this.width / 2 - 150, 40, 10526880);
		this.consoleCommandTextField.render(i, j, f);
		int k = 75;
		if (!this.previousOutputTextField.getText().isEmpty()) {
			k += 5 * this.fontRenderer.fontHeight + 1 + this.method_2364() - 135;
			this.drawString(this.fontRenderer, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, k + 4, 10526880);
			this.previousOutputTextField.render(i, j, f);
		}

		super.draw(i, j, f);
		if (this.field_2759 != null) {
			this.field_2759.method_2373(i, j);
		} else {
			k = 0;

			for (String string : this.field_2761) {
				drawRect(this.field_2757 - 1, 72 + 12 * k, this.field_2757 + this.field_2756 + 1, 84 + 12 * k, Integer.MIN_VALUE);
				this.fontRenderer.drawWithShadow(string, (float)this.field_2757, (float)(74 + 12 * k), -1);
				k++;
			}
		}
	}

	public void method_2357() {
		if (this.field_2754 != null && this.field_2754.isDone()) {
			Suggestions suggestions = (Suggestions)this.field_2754.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.fontRenderer.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.consoleCommandTextField.method_1889(suggestions.getRange().getStart()) + this.fontRenderer.getStringWidth(" "),
					0,
					this.consoleCommandTextField.method_1889(0) + this.fontRenderer.getStringWidth(" ") + this.consoleCommandTextField.method_1859() - i
				);
				this.field_2759 = new AbstractCommandBlockGui.class_464(j, 72, i, suggestions);
			}
		}
	}

	protected void method_2346(String string) {
		this.consoleCommandTextField.setText(string);
	}

	@Nullable
	private static String method_2350(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	@Environment(EnvType.CLIENT)
	class class_464 {
		private final class_768 field_2771;
		private final Suggestions field_2764;
		private final String field_2768;
		private int field_2769;
		private int field_2766;
		private Vec2f field_2767 = Vec2f.ZERO;
		private boolean field_2765;

		private class_464(int i, int j, int k, Suggestions suggestions) {
			this.field_2771 = new class_768(i - 1, j, k + 1, Math.min(suggestions.getList().size(), 7) * 12);
			this.field_2764 = suggestions;
			this.field_2768 = AbstractCommandBlockGui.this.consoleCommandTextField.getText();
			this.method_2374(0);
		}

		public void method_2373(int i, int j) {
			int k = Math.min(this.field_2764.getList().size(), 7);
			int l = Integer.MIN_VALUE;
			int m = -5592406;
			boolean bl = this.field_2769 > 0;
			boolean bl2 = this.field_2764.getList().size() > this.field_2769 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.field_2767.x != (float)i || this.field_2767.y != (float)j;
			if (bl4) {
				this.field_2767 = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				Drawable.drawRect(
					this.field_2771.method_3321(),
					this.field_2771.method_3322() - 1,
					this.field_2771.method_3321() + this.field_2771.method_3319(),
					this.field_2771.method_3322(),
					Integer.MIN_VALUE
				);
				Drawable.drawRect(
					this.field_2771.method_3321(),
					this.field_2771.method_3322() + this.field_2771.method_3320(),
					this.field_2771.method_3321() + this.field_2771.method_3319(),
					this.field_2771.method_3322() + this.field_2771.method_3320() + 1,
					Integer.MIN_VALUE
				);
				if (bl) {
					for (int n = 0; n < this.field_2771.method_3319(); n++) {
						if (n % 2 == 0) {
							Drawable.drawRect(
								this.field_2771.method_3321() + n, this.field_2771.method_3322() - 1, this.field_2771.method_3321() + n + 1, this.field_2771.method_3322(), -1
							);
						}
					}
				}

				if (bl2) {
					for (int nx = 0; nx < this.field_2771.method_3319(); nx++) {
						if (nx % 2 == 0) {
							Drawable.drawRect(
								this.field_2771.method_3321() + nx,
								this.field_2771.method_3322() + this.field_2771.method_3320(),
								this.field_2771.method_3321() + nx + 1,
								this.field_2771.method_3322() + this.field_2771.method_3320() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int o = 0; o < k; o++) {
				Suggestion suggestion = (Suggestion)this.field_2764.getList().get(o + this.field_2769);
				Drawable.drawRect(
					this.field_2771.method_3321(),
					this.field_2771.method_3322() + 12 * o,
					this.field_2771.method_3321() + this.field_2771.method_3319(),
					this.field_2771.method_3322() + 12 * o + 12,
					Integer.MIN_VALUE
				);
				if (i > this.field_2771.method_3321()
					&& i < this.field_2771.method_3321() + this.field_2771.method_3319()
					&& j > this.field_2771.method_3322() + 12 * o
					&& j < this.field_2771.method_3322() + 12 * o + 12) {
					if (bl4) {
						this.method_2374(o + this.field_2769);
					}

					bl5 = true;
				}

				AbstractCommandBlockGui.this.fontRenderer
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_2771.method_3321() + 1),
						(float)(this.field_2771.method_3322() + 2 + 12 * o),
						o + this.field_2769 == this.field_2766 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_2764.getList().get(this.field_2766)).getTooltip();
				if (message != null) {
					AbstractCommandBlockGui.this.drawTooltip(TextFormatter.message(message).getFormattedText(), i, j);
				}
			}
		}

		public boolean method_2372(int i, int j, int k) {
			if (!this.field_2771.method_3318(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2771.method_3322()) / 12 + this.field_2769;
				if (l >= 0 && l < this.field_2764.getList().size()) {
					this.method_2374(l);
					this.method_2375();
				}

				return true;
			}
		}

		public boolean method_2370(double d) {
			int i = (int)(
				AbstractCommandBlockGui.this.client.field_1729.getX()
					* (double)AbstractCommandBlockGui.this.client.window.getScaledWidth()
					/ (double)AbstractCommandBlockGui.this.client.window.method_4480()
			);
			int j = (int)(
				AbstractCommandBlockGui.this.client.field_1729.getY()
					* (double)AbstractCommandBlockGui.this.client.window.getScaledHeight()
					/ (double)AbstractCommandBlockGui.this.client.window.method_4507()
			);
			if (this.field_2771.method_3318(i, j)) {
				this.field_2769 = MathHelper.clamp((int)((double)this.field_2769 - d), 0, Math.max(this.field_2764.getList().size() - 7, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean method_2377(int i, int j, int k) {
			if (i == 265) {
				this.method_2371(-1);
				this.field_2765 = false;
				return true;
			} else if (i == 264) {
				this.method_2371(1);
				this.field_2765 = false;
				return true;
			} else if (i == 258) {
				if (this.field_2765) {
					this.method_2371(Gui.isShiftPressed() ? -1 : 1);
				}

				this.method_2375();
				return true;
			} else if (i == 256) {
				this.method_2376();
				return true;
			} else {
				return false;
			}
		}

		public void method_2371(int i) {
			this.method_2374(this.field_2766 + i);
			int j = this.field_2769;
			int k = this.field_2769 + 7 - 1;
			if (this.field_2766 < j) {
				this.field_2769 = MathHelper.clamp(this.field_2766, 0, Math.max(this.field_2764.getList().size() - 7, 0));
			} else if (this.field_2766 > k) {
				this.field_2769 = MathHelper.clamp(this.field_2766 - 7, 0, Math.max(this.field_2764.getList().size() - 7, 0));
			}
		}

		public void method_2374(int i) {
			this.field_2766 = i;
			if (this.field_2766 < 0) {
				this.field_2766 = this.field_2766 + this.field_2764.getList().size();
			}

			if (this.field_2766 >= this.field_2764.getList().size()) {
				this.field_2766 = this.field_2766 - this.field_2764.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.field_2764.getList().get(this.field_2766);
			AbstractCommandBlockGui.this.consoleCommandTextField
				.setSuggestion(AbstractCommandBlockGui.method_2350(AbstractCommandBlockGui.this.consoleCommandTextField.getText(), suggestion.apply(this.field_2768)));
		}

		public void method_2375() {
			Suggestion suggestion = (Suggestion)this.field_2764.getList().get(this.field_2766);
			AbstractCommandBlockGui.this.field_2750 = true;
			AbstractCommandBlockGui.this.method_2346(suggestion.apply(this.field_2768));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			AbstractCommandBlockGui.this.consoleCommandTextField.setCursor(i);
			AbstractCommandBlockGui.this.consoleCommandTextField.method_1884(i);
			this.method_2374(this.field_2766);
			AbstractCommandBlockGui.this.field_2750 = false;
			this.field_2765 = true;
		}

		public void method_2376() {
			AbstractCommandBlockGui.this.field_2759 = null;
		}
	}
}
