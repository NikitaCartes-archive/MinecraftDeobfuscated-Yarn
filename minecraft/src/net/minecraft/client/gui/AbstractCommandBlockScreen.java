package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.server.command.CommandSource;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
	protected TextFieldWidget consoleCommandTextField;
	protected TextFieldWidget previousOutputTextField;
	protected ButtonWidget doneButton;
	protected ButtonWidget cancelButton;
	protected ButtonWidget toggleTrackingOutputButton;
	protected boolean trackingOutput;
	protected final List<String> field_2761 = Lists.<String>newArrayList();
	protected int field_2757;
	protected int field_2756;
	protected ParseResults<CommandSource> parsedCommand;
	protected CompletableFuture<Suggestions> field_2754;
	protected AbstractCommandBlockScreen.class_464 field_2759;
	private boolean suggestionsDisabled;

	public AbstractCommandBlockScreen() {
		super(NarratorManager.field_18967);
	}

	@Override
	public void tick() {
		this.consoleCommandTextField.tick();
	}

	abstract CommandBlockExecutor getCommandExecutor();

	abstract int method_2364();

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.doneButton = this.addButton(
			new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.commitAndClose())
		);
		this.cancelButton = this.addButton(
			new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.onClose())
		);
		this.toggleTrackingOutputButton = this.addButton(new ButtonWidget(this.width / 2 + 150 - 20, this.method_2364(), 20, 20, "O", buttonWidget -> {
			CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
			commandBlockExecutor.shouldTrackOutput(!commandBlockExecutor.isTrackingOutput());
			this.updateTrackedOutput();
		}));
		this.consoleCommandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20);
		this.consoleCommandTextField.setMaxLength(32500);
		this.consoleCommandTextField.setRenderTextProvider(this::method_2348);
		this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
		this.children.add(this.consoleCommandTextField);
		this.previousOutputTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.method_2364(), 276, 20);
		this.previousOutputTextField.setMaxLength(32500);
		this.previousOutputTextField.setIsEditable(false);
		this.previousOutputTextField.setText("-");
		this.children.add(this.previousOutputTextField);
		this.method_20085(this.consoleCommandTextField);
		this.consoleCommandTextField.setFocused(true);
		this.updateCommand();
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.consoleCommandTextField.getText();
		this.init(minecraftClient, i, j);
		this.setCommand(string);
		this.updateCommand();
	}

	protected void updateTrackedOutput() {
		if (this.getCommandExecutor().isTrackingOutput()) {
			this.toggleTrackingOutputButton.setMessage("O");
			this.previousOutputTextField.setText(this.getCommandExecutor().getLastOutput().getString());
		} else {
			this.toggleTrackingOutputButton.setMessage("X");
			this.previousOutputTextField.setText("-");
		}
	}

	protected void commitAndClose() {
		CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
		this.syncSettingsToServer(commandBlockExecutor);
		if (!commandBlockExecutor.isTrackingOutput()) {
			commandBlockExecutor.setLastOutput(null);
		}

		this.minecraft.openScreen(null);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	protected abstract void syncSettingsToServer(CommandBlockExecutor commandBlockExecutor);

	@Override
	public void onClose() {
		this.getCommandExecutor().shouldTrackOutput(this.trackingOutput);
		this.minecraft.openScreen(null);
	}

	private void onCommandChanged(String string) {
		this.updateCommand();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_2759 != null && this.field_2759.method_2377(i, j, k)) {
			return true;
		} else if (this.getFocused() == this.consoleCommandTextField && i == 258) {
			this.method_2357();
			return true;
		} else if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			if (i == 258 && this.getFocused() == this.consoleCommandTextField) {
				this.method_2357();
			}

			return false;
		} else {
			this.commitAndClose();
			return true;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2759 != null && this.field_2759.method_2370(MathHelper.clamp(f, -1.0, 1.0)) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_2759 != null && this.field_2759.method_2372((int)d, (int)e, i) ? true : super.mouseClicked(d, e, i);
	}

	protected void updateCommand() {
		String string = this.consoleCommandTextField.getText();
		if (this.parsedCommand != null && !this.parsedCommand.getReader().getString().equals(string)) {
			this.parsedCommand = null;
		}

		if (!this.suggestionsDisabled) {
			this.consoleCommandTextField.setSuggestion(null);
			this.field_2759 = null;
		}

		this.field_2761.clear();
		CommandDispatcher<CommandSource> commandDispatcher = this.minecraft.player.networkHandler.getCommandDispatcher();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		int i = stringReader.getCursor();
		if (this.parsedCommand == null) {
			this.parsedCommand = commandDispatcher.parse(stringReader, this.minecraft.player.networkHandler.getCommandSource());
		}

		int j = this.consoleCommandTextField.getCursor();
		if (j >= i && (this.field_2759 == null || !this.suggestionsDisabled)) {
			this.field_2754 = commandDispatcher.getCompletionSuggestions(this.parsedCommand, j);
			this.field_2754.thenRun(() -> {
				if (this.field_2754.isDone()) {
					this.method_2354();
				}
			});
		}
	}

	private void method_2354() {
		if (((Suggestions)this.field_2754.join()).isEmpty()
			&& !this.parsedCommand.getExceptions().isEmpty()
			&& this.consoleCommandTextField.getCursor() == this.consoleCommandTextField.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parsedCommand.getExceptions().entrySet()) {
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
			this.method_2356(TextFormat.field_1080);
		}

		this.field_2759 = null;
		if (this.minecraft.options.autoSuggestions) {
			this.method_2357();
		}
	}

	private String method_2348(String string, int i) {
		return this.parsedCommand != null ? ChatScreen.getRenderText(this.parsedCommand, string, i) : string;
	}

	private void method_2356(TextFormat textFormat) {
		CommandContextBuilder<CommandSource> commandContextBuilder = this.parsedCommand.getContext();
		SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.consoleCommandTextField.getCursor());
		Map<CommandNode<CommandSource>, String> map = this.minecraft
			.player
			.networkHandler
			.getCommandDispatcher()
			.getSmartUsage(suggestionContext.parent, this.minecraft.player.networkHandler.getCommandSource());
		List<String> list = Lists.<String>newArrayList();
		int i = 0;

		for (Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(textFormat + (String)entry.getValue());
				i = Math.max(i, this.font.getStringWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.field_2761.addAll(list);
			this.field_2757 = MathHelper.clamp(
				this.consoleCommandTextField.method_1889(suggestionContext.startPos),
				0,
				this.consoleCommandTextField.method_1889(0) + this.consoleCommandTextField.method_1859() - i
			);
			this.field_2756 = i;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawString(this.font, I18n.translate("advMode.command"), this.width / 2 - 150, 40, 10526880);
		this.consoleCommandTextField.render(i, j, f);
		int k = 75;
		if (!this.previousOutputTextField.getText().isEmpty()) {
			k += 5 * 9 + 1 + this.method_2364() - 135;
			this.drawString(this.font, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, k + 4, 10526880);
			this.previousOutputTextField.render(i, j, f);
		}

		super.render(i, j, f);
		if (this.field_2759 != null) {
			this.field_2759.method_2373(i, j);
		} else {
			k = 0;

			for (String string : this.field_2761) {
				fill(this.field_2757 - 1, 72 + 12 * k, this.field_2757 + this.field_2756 + 1, 84 + 12 * k, Integer.MIN_VALUE);
				this.font.drawWithShadow(string, (float)this.field_2757, (float)(74 + 12 * k), -1);
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
					i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.consoleCommandTextField.method_1889(suggestions.getRange().getStart()),
					0,
					this.consoleCommandTextField.method_1889(0) + this.consoleCommandTextField.method_1859() - i
				);
				this.field_2759 = new AbstractCommandBlockScreen.class_464(j, 72, i, suggestions);
			}
		}
	}

	protected void setCommand(String string) {
		this.consoleCommandTextField.setText(string);
	}

	@Nullable
	private static String method_2350(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	@Environment(EnvType.CLIENT)
	class class_464 {
		private final Rect2i field_2771;
		private final Suggestions field_2764;
		private final String field_2768;
		private int field_2769;
		private int field_2766;
		private Vec2f field_2767 = Vec2f.ZERO;
		private boolean field_2765;

		private class_464(int i, int j, int k, Suggestions suggestions) {
			this.field_2771 = new Rect2i(i - 1, j, k + 1, Math.min(suggestions.getList().size(), 7) * 12);
			this.field_2764 = suggestions;
			this.field_2768 = AbstractCommandBlockScreen.this.consoleCommandTextField.getText();
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
				DrawableHelper.fill(
					this.field_2771.getX(), this.field_2771.getY() - 1, this.field_2771.getX() + this.field_2771.getWidth(), this.field_2771.getY(), Integer.MIN_VALUE
				);
				DrawableHelper.fill(
					this.field_2771.getX(),
					this.field_2771.getY() + this.field_2771.getHeight(),
					this.field_2771.getX() + this.field_2771.getWidth(),
					this.field_2771.getY() + this.field_2771.getHeight() + 1,
					Integer.MIN_VALUE
				);
				if (bl) {
					for (int n = 0; n < this.field_2771.getWidth(); n++) {
						if (n % 2 == 0) {
							DrawableHelper.fill(this.field_2771.getX() + n, this.field_2771.getY() - 1, this.field_2771.getX() + n + 1, this.field_2771.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int nx = 0; nx < this.field_2771.getWidth(); nx++) {
						if (nx % 2 == 0) {
							DrawableHelper.fill(
								this.field_2771.getX() + nx,
								this.field_2771.getY() + this.field_2771.getHeight(),
								this.field_2771.getX() + nx + 1,
								this.field_2771.getY() + this.field_2771.getHeight() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int o = 0; o < k; o++) {
				Suggestion suggestion = (Suggestion)this.field_2764.getList().get(o + this.field_2769);
				DrawableHelper.fill(
					this.field_2771.getX(),
					this.field_2771.getY() + 12 * o,
					this.field_2771.getX() + this.field_2771.getWidth(),
					this.field_2771.getY() + 12 * o + 12,
					Integer.MIN_VALUE
				);
				if (i > this.field_2771.getX()
					&& i < this.field_2771.getX() + this.field_2771.getWidth()
					&& j > this.field_2771.getY() + 12 * o
					&& j < this.field_2771.getY() + 12 * o + 12) {
					if (bl4) {
						this.method_2374(o + this.field_2769);
					}

					bl5 = true;
				}

				AbstractCommandBlockScreen.this.font
					.drawWithShadow(
						suggestion.getText(),
						(float)(this.field_2771.getX() + 1),
						(float)(this.field_2771.getY() + 2 + 12 * o),
						o + this.field_2769 == this.field_2766 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_2764.getList().get(this.field_2766)).getTooltip();
				if (message != null) {
					AbstractCommandBlockScreen.this.renderTooltip(TextFormatter.message(message).getFormattedText(), i, j);
				}
			}
		}

		public boolean method_2372(int i, int j, int k) {
			if (!this.field_2771.contains(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2771.getY()) / 12 + this.field_2769;
				if (l >= 0 && l < this.field_2764.getList().size()) {
					this.method_2374(l);
					this.method_2375();
				}

				return true;
			}
		}

		public boolean method_2370(double d) {
			int i = (int)(
				AbstractCommandBlockScreen.this.minecraft.mouse.getX()
					* (double)AbstractCommandBlockScreen.this.minecraft.window.getScaledWidth()
					/ (double)AbstractCommandBlockScreen.this.minecraft.window.getWidth()
			);
			int j = (int)(
				AbstractCommandBlockScreen.this.minecraft.mouse.getY()
					* (double)AbstractCommandBlockScreen.this.minecraft.window.getScaledHeight()
					/ (double)AbstractCommandBlockScreen.this.minecraft.window.getHeight()
			);
			if (this.field_2771.contains(i, j)) {
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
					this.method_2371(Screen.hasShiftDown() ? -1 : 1);
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
			AbstractCommandBlockScreen.this.consoleCommandTextField
				.setSuggestion(AbstractCommandBlockScreen.method_2350(AbstractCommandBlockScreen.this.consoleCommandTextField.getText(), suggestion.apply(this.field_2768)));
		}

		public void method_2375() {
			Suggestion suggestion = (Suggestion)this.field_2764.getList().get(this.field_2766);
			AbstractCommandBlockScreen.this.suggestionsDisabled = true;
			AbstractCommandBlockScreen.this.setCommand(suggestion.apply(this.field_2768));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			AbstractCommandBlockScreen.this.consoleCommandTextField.setCursor(i);
			AbstractCommandBlockScreen.this.consoleCommandTextField.method_1884(i);
			this.method_2374(this.field_2766);
			AbstractCommandBlockScreen.this.suggestionsDisabled = false;
			this.field_2765 = true;
		}

		public void method_2376() {
			AbstractCommandBlockScreen.this.field_2759 = null;
		}
	}
}
