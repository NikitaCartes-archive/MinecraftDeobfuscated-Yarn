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
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen
extends Screen {
    protected TextFieldWidget consoleCommandTextField;
    protected TextFieldWidget previousOutputTextField;
    protected ButtonWidget doneButton;
    protected ButtonWidget cancelButton;
    protected ButtonWidget toggleTrackingOutputButton;
    protected boolean trackingOutput;
    private CommandSuggestor commandSuggestor;

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
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.commitAndClose()));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.onClose()));
        this.toggleTrackingOutputButton = this.addButton(new ButtonWidget(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, "O", buttonWidget -> {
            CommandBlockExecutor commandBlockExecutor;
            commandBlockExecutor.shouldTrackOutput(!(commandBlockExecutor = this.getCommandExecutor()).isTrackingOutput());
            this.updateTrackedOutput();
        }));
        this.consoleCommandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.translate("advMode.command", new Object[0])){

            @Override
            protected String getNarrationMessage() {
                return super.getNarrationMessage() + AbstractCommandBlockScreen.this.commandSuggestor.method_23958();
            }
        };
        this.consoleCommandTextField.setMaxLength(32500);
        this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
        this.children.add(this.consoleCommandTextField);
        this.previousOutputTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, I18n.translate("advMode.previousOutput", new Object[0]));
        this.previousOutputTextField.setMaxLength(32500);
        this.previousOutputTextField.setEditable(false);
        this.previousOutputTextField.setText("-");
        this.children.add(this.previousOutputTextField);
        this.setInitialFocus(this.consoleCommandTextField);
        this.consoleCommandTextField.setSelected(true);
        this.commandSuggestor = new CommandSuggestor(this.minecraft, this, this.consoleCommandTextField, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.commandSuggestor.setWindowActive(true);
        this.commandSuggestor.refresh();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.consoleCommandTextField.getText();
        this.init(client, width, height);
        this.consoleCommandTextField.setText(string);
        this.commandSuggestor.refresh();
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

    protected abstract void syncSettingsToServer(CommandBlockExecutor var1);

    @Override
    public void onClose() {
        this.getCommandExecutor().shouldTrackOutput(this.trackingOutput);
        this.minecraft.openScreen(null);
    }

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
        if (keyCode == 257 || keyCode == 335) {
            this.commitAndClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (this.commandSuggestor.mouseScrolled(amount)) {
            return true;
        }
        return super.mouseScrolled(d, e, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.commandSuggestor.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.translate("advMode.setCommand", new Object[0]), this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.font, I18n.translate("advMode.command", new Object[0]), this.width / 2 - 150, 40, 0xA0A0A0);
        this.consoleCommandTextField.render(mouseX, mouseY, delta);
        int i = 75;
        if (!this.previousOutputTextField.getText().isEmpty()) {
            this.drawString(this.font, I18n.translate("advMode.previousOutput", new Object[0]), this.width / 2 - 150, (i += 5 * this.font.fontHeight + 1 + this.getTrackOutputButtonHeight() - 135) + 4, 0xA0A0A0);
            this.previousOutputTextField.render(mouseX, mouseY, delta);
        }
        super.render(mouseX, mouseY, delta);
        this.commandSuggestor.render(mouseX, mouseY);
    }
}

