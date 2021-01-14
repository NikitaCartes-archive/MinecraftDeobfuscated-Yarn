/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DeathScreen
extends Screen {
    private int ticksSinceDeath;
    private final Text message;
    private final boolean isHardcore;
    private Text scoreText;

    public DeathScreen(@Nullable Text message, boolean isHardcore) {
        super(new TranslatableText(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.message = message;
        this.isHardcore = isHardcore;
    }

    @Override
    protected void init() {
        this.ticksSinceDeath = 0;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.isHardcore ? new TranslatableText("deathScreen.spectate") : new TranslatableText("deathScreen.respawn"), buttonWidget -> {
            this.client.player.requestRespawn();
            this.client.openScreen(null);
        }));
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslatableText("deathScreen.titleScreen"), buttonWidget -> {
            if (this.isHardcore) {
                this.quitLevel();
                return;
            }
            ConfirmScreen confirmScreen = new ConfirmScreen(this::onConfirmQuit, new TranslatableText("deathScreen.quit.confirm"), LiteralText.EMPTY, new TranslatableText("deathScreen.titleScreen"), new TranslatableText("deathScreen.respawn"));
            this.client.openScreen(confirmScreen);
            confirmScreen.disableButtons(20);
        }));
        if (!this.isHardcore && this.client.getSession() == null) {
            buttonWidget2.active = false;
        }
        for (ClickableWidget clickableWidget : this.buttons) {
            clickableWidget.active = false;
        }
        this.scoreText = new TranslatableText("deathScreen.score").append(": ").append(new LiteralText(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void onConfirmQuit(boolean quit) {
        if (quit) {
            this.quitLevel();
        } else {
            this.client.player.requestRespawn();
            this.client.openScreen(null);
        }
    }

    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect();
        }
        this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
        this.client.openScreen(new TitleScreen());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, 0x60500000, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        DeathScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2 / 2, 30, 0xFFFFFF);
        RenderSystem.popMatrix();
        if (this.message != null) {
            DeathScreen.drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 0xFFFFFF);
        }
        DeathScreen.drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, 0xFFFFFF);
        if (this.message != null && mouseY > 85 && mouseY < 85 + this.textRenderer.fontHeight) {
            Style style = this.getTextComponentUnderMouse(mouseX);
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Nullable
    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.message == null) {
            return null;
        }
        int i = this.client.textRenderer.getWidth(this.message);
        int j = this.width / 2 - i / 2;
        int k = this.width / 2 + i / 2;
        if (mouseX < j || mouseX > k) {
            return null;
        }
        return this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (this.message != null && mouseY > 85.0 && mouseY < (double)(85 + this.textRenderer.fontHeight) && (style = this.getTextComponentUnderMouse((int)mouseX)) != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleTextClick(style);
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.ticksSinceDeath;
        if (this.ticksSinceDeath == 20) {
            for (ClickableWidget clickableWidget : this.buttons) {
                clickableWidget.active = true;
            }
        }
    }
}

