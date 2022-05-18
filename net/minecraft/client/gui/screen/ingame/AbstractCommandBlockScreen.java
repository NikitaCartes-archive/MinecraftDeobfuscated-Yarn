/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
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

@Environment(value=EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen
extends Screen {
    private static final Text SET_COMMAND_TEXT = Text.translatable("advMode.setCommand");
    private static final Text COMMAND_TEXT = Text.translatable("advMode.command");
    private static final Text PREVIOUS_OUTPUT_TEXT = Text.translatable("advMode.previousOutput");
    protected TextFieldWidget consoleCommandTextField;
    protected TextFieldWidget previousOutputTextField;
    protected ButtonWidget doneButton;
    protected ButtonWidget cancelButton;
    protected CyclingButtonWidget<Boolean> toggleTrackingOutputButton;
    CommandSuggestor commandSuggestor;

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
        this.doneButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, ScreenTexts.DONE, button -> this.commitAndClose()));
        this.cancelButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, ScreenTexts.CANCEL, button -> this.close()));
        boolean bl = this.getCommandExecutor().isTrackingOutput();
        this.toggleTrackingOutputButton = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("O"), Text.literal("X")).initially(bl).omitKeyText().build(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, Text.translatable("advMode.trackOutput"), (button, trackOutput) -> {
            CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
            commandBlockExecutor.setTrackOutput((boolean)trackOutput);
            this.setPreviousOutputText((boolean)trackOutput);
        }));
        this.consoleCommandTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 50, 300, 20, (Text)Text.translatable("advMode.command")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(AbstractCommandBlockScreen.this.commandSuggestor.getNarration());
            }
        };
        this.consoleCommandTextField.setMaxLength(32500);
        this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField);
        this.previousOutputTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, Text.translatable("advMode.previousOutput"));
        this.previousOutputTextField.setMaxLength(32500);
        this.previousOutputTextField.setEditable(false);
        this.previousOutputTextField.setText("-");
        this.addSelectableChild(this.previousOutputTextField);
        this.setInitialFocus(this.consoleCommandTextField);
        this.consoleCommandTextField.setTextFieldFocused(true);
        this.commandSuggestor = new CommandSuggestor(this.client, this, this.consoleCommandTextField, this.textRenderer, true, true, 0, 7, false, Integer.MIN_VALUE);
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

    protected abstract void syncSettingsToServer(CommandBlockExecutor var1);

    private void onCommandChanged(String text) {
        this.commandSuggestor.refresh();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.commitAndClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.commandSuggestor.mouseScrolled(amount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.commandSuggestor.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        AbstractCommandBlockScreen.drawCenteredText(matrices, this.textRenderer, SET_COMMAND_TEXT, this.width / 2, 20, 0xFFFFFF);
        AbstractCommandBlockScreen.drawTextWithShadow(matrices, this.textRenderer, COMMAND_TEXT, this.width / 2 - 150, 40, 0xA0A0A0);
        this.consoleCommandTextField.render(matrices, mouseX, mouseY, delta);
        int i = 75;
        if (!this.previousOutputTextField.getText().isEmpty()) {
            AbstractCommandBlockScreen.drawTextWithShadow(matrices, this.textRenderer, PREVIOUS_OUTPUT_TEXT, this.width / 2 - 150, (i += 5 * this.textRenderer.fontHeight + 1 + this.getTrackOutputButtonHeight() - 135) + 4, 0xA0A0A0);
            this.previousOutputTextField.render(matrices, mouseX, mouseY, delta);
        }
        super.render(matrices, mouseX, mouseY, delta);
        this.commandSuggestor.render(matrices, mouseX, mouseY);
    }
}

