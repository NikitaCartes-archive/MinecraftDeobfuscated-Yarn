/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

/**
 * A screen that is used for indicating that a task is running or has finished
 * running (either successfully or unsuccessfully). The screen has an optional
 * multi-line description and a button which can be used to close the screen.
 * The button can have a cooldown, which disables the button for a while after
 * the screen is displayed.
 */
@Environment(value=EnvType.CLIENT)
public class TaskScreen
extends Screen {
    private static final int TITLE_TEXT_Y = 80;
    private static final int DESCRIPTION_TEXT_Y = 120;
    private static final int DESCRIPTION_TEXT_WIDTH = 360;
    @Nullable
    private final Text descriptionText;
    private final Text closeButtonText;
    /**
     * The callback executed when the button or the Esc key is pressed. This can
     * have a side effect, such as cancelling a task in progress.
     */
    private final Runnable closeCallback;
    @Nullable
    private MultilineText description;
    /**
     * The button to close the screen (potentially with a side effect, such as cancelling a task).
     */
    private ButtonWidget button;
    /**
     * How long the button should be disabled after the screen is displayed in ticks.
     * Can be disabled by setting to {@code 0}.
     */
    private int buttonCooldown;

    /**
     * {@return a new screen to indicate a task is running}
     * 
     * <p>The screen has no description or button cooldown.
     */
    public static TaskScreen createRunningScreen(Text title, Text closeButtonText, Runnable closeCallback) {
        return new TaskScreen(title, null, closeButtonText, closeCallback, 0);
    }

    /**
     * {@return a new screen to indicate a task has finished running}
     * 
     * <p>The screen has a button cooldown of 20 ticks (1 second).
     */
    public static TaskScreen createResultScreen(Text title, Text descriptionText, Text closeButtonText, Runnable closeCallback) {
        return new TaskScreen(title, descriptionText, closeButtonText, closeCallback, 20);
    }

    protected TaskScreen(Text title, @Nullable Text descriptionText, Text closeButtonText, Runnable closeCallback, int buttonCooldown) {
        super(title);
        this.descriptionText = descriptionText;
        this.closeButtonText = closeButtonText;
        this.closeCallback = closeCallback;
        this.buttonCooldown = buttonCooldown;
    }

    @Override
    protected void init() {
        super.init();
        if (this.descriptionText != null) {
            this.description = MultilineText.create(this.textRenderer, (StringVisitable)this.descriptionText, 360);
        }
        int i = 150;
        int j = 20;
        int k = this.description != null ? this.description.count() : 1;
        int l = Math.max(k, 5) * this.textRenderer.fontHeight;
        int m = Math.min(120 + l, this.height - 40);
        this.button = this.addDrawableChild(ButtonWidget.builder(this.closeButtonText, buttonWidget -> this.close()).dimensions((this.width - 150) / 2, m, 150, 20).build());
    }

    @Override
    public void tick() {
        if (this.buttonCooldown > 0) {
            --this.buttonCooldown;
        }
        this.button.active = this.buttonCooldown == 0;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        TaskScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 80, 0xFFFFFF);
        if (this.description == null) {
            String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
            TaskScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, string, this.width / 2, 120, 0xA0A0A0);
        } else {
            this.description.drawCenterWithShadow(matrices, this.width / 2, 120);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.description != null && this.button.active;
    }

    @Override
    public void close() {
        this.closeCallback.run();
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.title, this.descriptionText != null ? this.descriptionText : ScreenTexts.EMPTY);
    }
}

