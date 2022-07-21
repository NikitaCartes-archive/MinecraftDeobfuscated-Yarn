package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.CommandBlockExecutor;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
	private static final Text SET_COMMAND_TEXT = Text.translatable("advMode.setCommand");
	private static final Text COMMAND_TEXT = Text.translatable("advMode.command");
	private static final Text PREVIOUS_OUTPUT_TEXT = Text.translatable("advMode.previousOutput");
	protected TextFieldWidget consoleCommandTextField;
	protected TextFieldWidget previousOutputTextField;
	protected ButtonWidget doneButton;
	protected ButtonWidget cancelButton;
	protected CyclingButtonWidget<Boolean> toggleTrackingOutputButton;
	ChatInputSuggestor commandSuggestor;

	public AbstractCommandBlockScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public void tick() {
		this.consoleCommandTextField.tick();
	}

	abstract CommandBlockExecutor getCommandExecutor();

	abstract int getTrackOutputButtonHeight();

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.doneButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, ScreenTexts.DONE, button -> this.commitAndClose())
		);
		this.cancelButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, ScreenTexts.CANCEL, button -> this.close())
		);
		boolean bl = this.getCommandExecutor().isTrackingOutput();
		this.toggleTrackingOutputButton = this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(Text.literal("O"), Text.literal("X"))
				.initially(bl)
				.omitKeyText()
				.build(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, Text.translatable("advMode.trackOutput"), (button, trackOutput) -> {
					CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
					commandBlockExecutor.setTrackOutput(trackOutput);
					this.setPreviousOutputText(trackOutput);
				})
		);
		this.consoleCommandTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 50, 300, 20, Text.translatable("advMode.command")) {
			@Override
			protected MutableText getNarrationMessage() {
				return super.getNarrationMessage().append(AbstractCommandBlockScreen.this.commandSuggestor.getNarration());
			}
		};
		this.consoleCommandTextField.setMaxLength(32500);
		this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
		this.addSelectableChild(this.consoleCommandTextField);
		this.previousOutputTextField = new TextFieldWidget(
			this.textRenderer, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, Text.translatable("advMode.previousOutput")
		);
		this.previousOutputTextField.setMaxLength(32500);
		this.previousOutputTextField.setEditable(false);
		this.previousOutputTextField.setText("-");
		this.addSelectableChild(this.previousOutputTextField);
		this.setInitialFocus(this.consoleCommandTextField);
		this.consoleCommandTextField.setTextFieldFocused(true);
		this.commandSuggestor = new ChatInputSuggestor(this.client, this, this.consoleCommandTextField, this.textRenderer, true, true, 0, 7, false, Integer.MIN_VALUE);
		this.commandSuggestor.setWindowActive(true);
		this.commandSuggestor.refresh();
		this.setPreviousOutputText(bl);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.consoleCommandTextField.getText();
		this.init(client, width, height);
		this.consoleCommandTextField.setText(string);
		this.commandSuggestor.refresh();
	}

	protected void setPreviousOutputText(boolean trackOutput) {
		this.previousOutputTextField.setText(trackOutput ? this.getCommandExecutor().getLastOutput().getString() : "-");
	}

	protected void commitAndClose() {
		CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
		this.syncSettingsToServer(commandBlockExecutor);
		if (!commandBlockExecutor.isTrackingOutput()) {
			commandBlockExecutor.setLastOutput(null);
		}

		this.client.setScreen(null);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	protected abstract void syncSettingsToServer(CommandBlockExecutor commandExecutor);

	private void onCommandChanged(String text) {
		this.commandSuggestor.refresh();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
			return false;
		} else {
			this.commitAndClose();
			return true;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.commandSuggestor.mouseScrolled(amount) ? true : super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.commandSuggestor.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, SET_COMMAND_TEXT, this.width / 2, 20, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, COMMAND_TEXT, this.width / 2 - 150, 40, 10526880);
		this.consoleCommandTextField.render(matrices, mouseX, mouseY, delta);
		int i = 75;
		if (!this.previousOutputTextField.getText().isEmpty()) {
			i += 5 * 9 + 1 + this.getTrackOutputButtonHeight() - 135;
			drawTextWithShadow(matrices, this.textRenderer, PREVIOUS_OUTPUT_TEXT, this.width / 2 - 150, i + 4, 10526880);
			this.previousOutputTextField.render(matrices, mouseX, mouseY, delta);
		}

		super.render(matrices, mouseX, mouseY, delta);
		this.commandSuggestor.render(matrices, mouseX, mouseY);
	}
}
