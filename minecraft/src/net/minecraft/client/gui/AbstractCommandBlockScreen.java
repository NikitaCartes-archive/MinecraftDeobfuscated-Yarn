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
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.network.chat.Components;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
	protected TextFieldWidget consoleCommandTextField;
	protected TextFieldWidget previousOutputTextField;
	protected ButtonWidget doneButton;
	protected ButtonWidget cancelButton;
	protected ButtonWidget toggleTrackingOutputButton;
	protected boolean trackingOutput;
	protected final List<String> exceptions = Lists.<String>newArrayList();
	protected int field_2757;
	protected int field_2756;
	protected ParseResults<CommandSource> parsedCommand;
	protected CompletableFuture<Suggestions> suggestionsFuture;
	protected AbstractCommandBlockScreen.SuggestionWindow suggestionWindow;
	private boolean completingSuggestion;

	public AbstractCommandBlockScreen() {
		super(NarratorManager.EMPTY);
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
		this.consoleCommandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.translate("advMode.command"));
		this.consoleCommandTextField.setMaxLength(32500);
		this.consoleCommandTextField.setRenderTextProvider(this::method_2348);
		this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
		this.children.add(this.consoleCommandTextField);
		this.previousOutputTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.method_2364(), 276, 20, I18n.translate("advMode.previousOutput"));
		this.previousOutputTextField.setMaxLength(32500);
		this.previousOutputTextField.setIsEditable(false);
		this.previousOutputTextField.setText("-");
		this.children.add(this.previousOutputTextField);
		this.setInitialFocus(this.consoleCommandTextField);
		this.consoleCommandTextField.method_1876(true);
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
		if (this.suggestionWindow != null && this.suggestionWindow.keyPressed(i, j, k)) {
			return true;
		} else if (this.getFocused() == this.consoleCommandTextField && i == 258) {
			this.showSuggestions();
			return true;
		} else if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			if (i == 258 && this.getFocused() == this.consoleCommandTextField) {
				this.showSuggestions();
			}

			return false;
		} else {
			this.commitAndClose();
			return true;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.suggestionWindow != null && this.suggestionWindow.mouseScrolled(MathHelper.clamp(f, -1.0, 1.0)) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.suggestionWindow != null && this.suggestionWindow.mouseClicked((int)d, (int)e, i) ? true : super.mouseClicked(d, e, i);
	}

	protected void updateCommand() {
		String string = this.consoleCommandTextField.getText();
		if (this.parsedCommand != null && !this.parsedCommand.getReader().getString().equals(string)) {
			this.parsedCommand = null;
		}

		if (!this.completingSuggestion) {
			this.consoleCommandTextField.setSuggestion(null);
			this.suggestionWindow = null;
		}

		this.exceptions.clear();
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
		if (j >= i && (this.suggestionWindow == null || !this.completingSuggestion)) {
			this.suggestionsFuture = commandDispatcher.getCompletionSuggestions(this.parsedCommand, j);
			this.suggestionsFuture.thenRun(() -> {
				if (this.suggestionsFuture.isDone()) {
					this.updateCommandFeedback();
				}
			});
		}
	}

	private void updateCommandFeedback() {
		if (((Suggestions)this.suggestionsFuture.join()).isEmpty()
			&& !this.parsedCommand.getExceptions().isEmpty()
			&& this.consoleCommandTextField.getCursor() == this.consoleCommandTextField.getText().length()) {
			int i = 0;

			for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parsedCommand.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.exceptions.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.exceptions.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_2757 = 0;
		this.field_2756 = this.width;
		if (this.exceptions.isEmpty()) {
			this.method_2356(ChatFormat.field_1080);
		}

		this.suggestionWindow = null;
		if (this.minecraft.options.autoSuggestions) {
			this.showSuggestions();
		}
	}

	private String method_2348(String string, int i) {
		return this.parsedCommand != null ? ChatScreen.getRenderText(this.parsedCommand, string, i) : string;
	}

	private void method_2356(ChatFormat chatFormat) {
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
				list.add(chatFormat + (String)entry.getValue());
				i = Math.max(i, this.font.getStringWidth((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.exceptions.addAll(list);
			this.field_2757 = MathHelper.clamp(
				this.consoleCommandTextField.getCharacterX(suggestionContext.startPos),
				0,
				this.consoleCommandTextField.getCharacterX(0) + this.consoleCommandTextField.method_1859() - i
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
		if (this.suggestionWindow != null) {
			this.suggestionWindow.draw(i, j);
		} else {
			k = 0;

			for (String string : this.exceptions) {
				fill(this.field_2757 - 1, 72 + 12 * k, this.field_2757 + this.field_2756 + 1, 84 + 12 * k, Integer.MIN_VALUE);
				this.font.drawWithShadow(string, (float)this.field_2757, (float)(74 + 12 * k), -1);
				k++;
			}
		}
	}

	public void showSuggestions() {
		if (this.suggestionsFuture != null && this.suggestionsFuture.isDone()) {
			Suggestions suggestions = (Suggestions)this.suggestionsFuture.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
				}

				int j = MathHelper.clamp(
					this.consoleCommandTextField.getCharacterX(suggestions.getRange().getStart()),
					0,
					this.consoleCommandTextField.getCharacterX(0) + this.consoleCommandTextField.method_1859() - i
				);
				this.suggestionWindow = new AbstractCommandBlockScreen.SuggestionWindow(j, 72, i, suggestions);
			}
		}
	}

	protected void setCommand(String string) {
		this.consoleCommandTextField.setText(string);
	}

	@Nullable
	private static String suggestSuffix(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	@Environment(EnvType.CLIENT)
	class SuggestionWindow {
		private final Rect2i area;
		private final Suggestions suggestions;
		private final String typedText;
		private int inWindowIndex;
		private int selection;
		private Vec2f mouse = Vec2f.ZERO;
		private boolean completed;

		private SuggestionWindow(int i, int j, int k, Suggestions suggestions) {
			this.area = new Rect2i(i - 1, j, k + 1, Math.min(suggestions.getList().size(), 7) * 12);
			this.suggestions = suggestions;
			this.typedText = AbstractCommandBlockScreen.this.consoleCommandTextField.getText();
			this.select(0);
		}

		public void draw(int i, int j) {
			int k = Math.min(this.suggestions.getList().size(), 7);
			int l = Integer.MIN_VALUE;
			int m = -5592406;
			boolean bl = this.inWindowIndex > 0;
			boolean bl2 = this.suggestions.getList().size() > this.inWindowIndex + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.mouse.x != (float)i || this.mouse.y != (float)j;
			if (bl4) {
				this.mouse = new Vec2f((float)i, (float)j);
			}

			if (bl3) {
				DrawableHelper.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), Integer.MIN_VALUE);
				DrawableHelper.fill(
					this.area.getX(),
					this.area.getY() + this.area.getHeight(),
					this.area.getX() + this.area.getWidth(),
					this.area.getY() + this.area.getHeight() + 1,
					Integer.MIN_VALUE
				);
				if (bl) {
					for (int n = 0; n < this.area.getWidth(); n++) {
						if (n % 2 == 0) {
							DrawableHelper.fill(this.area.getX() + n, this.area.getY() - 1, this.area.getX() + n + 1, this.area.getY(), -1);
						}
					}
				}

				if (bl2) {
					for (int nx = 0; nx < this.area.getWidth(); nx++) {
						if (nx % 2 == 0) {
							DrawableHelper.fill(
								this.area.getX() + nx, this.area.getY() + this.area.getHeight(), this.area.getX() + nx + 1, this.area.getY() + this.area.getHeight() + 1, -1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int o = 0; o < k; o++) {
				Suggestion suggestion = (Suggestion)this.suggestions.getList().get(o + this.inWindowIndex);
				DrawableHelper.fill(this.area.getX(), this.area.getY() + 12 * o, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * o + 12, Integer.MIN_VALUE);
				if (i > this.area.getX() && i < this.area.getX() + this.area.getWidth() && j > this.area.getY() + 12 * o && j < this.area.getY() + 12 * o + 12) {
					if (bl4) {
						this.select(o + this.inWindowIndex);
					}

					bl5 = true;
				}

				AbstractCommandBlockScreen.this.font
					.drawWithShadow(
						suggestion.getText(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * o), o + this.inWindowIndex == this.selection ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.suggestions.getList().get(this.selection)).getTooltip();
				if (message != null) {
					AbstractCommandBlockScreen.this.renderTooltip(Components.message(message).getFormattedText(), i, j);
				}
			}
		}

		public boolean mouseClicked(int i, int j, int k) {
			if (!this.area.contains(i, j)) {
				return false;
			} else {
				int l = (j - this.area.getY()) / 12 + this.inWindowIndex;
				if (l >= 0 && l < this.suggestions.getList().size()) {
					this.select(l);
					this.complete();
				}

				return true;
			}
		}

		public boolean mouseScrolled(double d) {
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
			if (this.area.contains(i, j)) {
				this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - d), 0, Math.max(this.suggestions.getList().size() - 7, 0));
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
				this.discard();
				return true;
			} else {
				return false;
			}
		}

		public void scroll(int i) {
			this.select(this.selection + i);
			int j = this.inWindowIndex;
			int k = this.inWindowIndex + 7 - 1;
			if (this.selection < j) {
				this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.getList().size() - 7, 0));
			} else if (this.selection > k) {
				this.inWindowIndex = MathHelper.clamp(this.selection - 7, 0, Math.max(this.suggestions.getList().size() - 7, 0));
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
			AbstractCommandBlockScreen.this.consoleCommandTextField
				.setSuggestion(
					AbstractCommandBlockScreen.suggestSuffix(AbstractCommandBlockScreen.this.consoleCommandTextField.getText(), suggestion.apply(this.typedText))
				);
		}

		public void complete() {
			Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
			AbstractCommandBlockScreen.this.completingSuggestion = true;
			AbstractCommandBlockScreen.this.setCommand(suggestion.apply(this.typedText));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			AbstractCommandBlockScreen.this.consoleCommandTextField.setCursor(i);
			AbstractCommandBlockScreen.this.consoleCommandTextField.method_1884(i);
			this.select(this.selection);
			AbstractCommandBlockScreen.this.completingSuggestion = false;
			this.completed = true;
		}

		public void discard() {
			AbstractCommandBlockScreen.this.suggestionWindow = null;
		}
	}
}
