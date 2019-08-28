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
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
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

    public DeathScreen(@Nullable Text text, boolean bl) {
        super(new TranslatableText(bl ? "deathScreen.title.hardcore" : "deathScreen.title", new Object[0]));
        this.message = text;
        this.isHardcore = bl;
    }

    @Override
    protected void init() {
        this.ticksSinceDeath = 0;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.isHardcore ? I18n.translate("deathScreen.spectate", new Object[0]) : I18n.translate("deathScreen.respawn", new Object[0]), buttonWidget -> {
            this.minecraft.player.requestRespawn();
            this.minecraft.openScreen(null);
        }));
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, I18n.translate("deathScreen.titleScreen", new Object[0]), buttonWidget -> {
            if (this.isHardcore) {
                this.method_22364();
                return;
            }
            ConfirmScreen confirmScreen = new ConfirmScreen(this::onConfirmQuit, new TranslatableText("deathScreen.quit.confirm", new Object[0]), new LiteralText(""), I18n.translate("deathScreen.titleScreen", new Object[0]), I18n.translate("deathScreen.respawn", new Object[0]));
            this.minecraft.openScreen(confirmScreen);
            confirmScreen.disableButtons(20);
        }));
        if (!this.isHardcore && this.minecraft.getSession() == null) {
            buttonWidget2.active = false;
        }
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            abstractButtonWidget.active = false;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void onConfirmQuit(boolean bl) {
        if (bl) {
            this.method_22364();
        } else {
            this.minecraft.player.requestRespawn();
            this.minecraft.openScreen(null);
        }
    }

    private void method_22364() {
        if (this.minecraft.world != null) {
            this.minecraft.world.disconnect();
        }
        this.minecraft.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel", new Object[0])));
        this.minecraft.openScreen(new TitleScreen());
    }

    @Override
    public void render(int i, int j, float f) {
        Text text;
        this.fillGradient(0, 0, this.width, this.height, 0x60500000, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2 / 2, 30, 0xFFFFFF);
        RenderSystem.popMatrix();
        if (this.message != null) {
            this.drawCenteredString(this.font, this.message.asFormattedString(), this.width / 2, 85, 0xFFFFFF);
        }
        this.drawCenteredString(this.font, I18n.translate("deathScreen.score", new Object[0]) + ": " + (Object)((Object)Formatting.YELLOW) + this.minecraft.player.getScore(), this.width / 2, 100, 0xFFFFFF);
        if (this.message != null && j > 85 && j < 85 + this.font.fontHeight && (text = this.getTextComponentUnderMouse(i)) != null && text.getStyle().getHoverEvent() != null) {
            this.renderComponentHoverEffect(text, i, j);
        }
        super.render(i, j, f);
    }

    @Nullable
    public Text getTextComponentUnderMouse(int i) {
        if (this.message == null) {
            return null;
        }
        int j = this.minecraft.textRenderer.getStringWidth(this.message.asFormattedString());
        int k = this.width / 2 - j / 2;
        int l = this.width / 2 + j / 2;
        int m = k;
        if (i < k || i > l) {
            return null;
        }
        for (Text text : this.message) {
            if ((m += this.minecraft.textRenderer.getStringWidth(TextComponentUtil.getRenderChatMessage(text.asString(), false))) <= i) continue;
            return text;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        Text text;
        if (this.message != null && e > 85.0 && e < (double)(85 + this.font.fontHeight) && (text = this.getTextComponentUnderMouse((int)d)) != null && text.getStyle().getClickEvent() != null && text.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleComponentClicked(text);
            return false;
        }
        return super.mouseClicked(d, e, i);
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
            for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
                abstractButtonWidget.active = true;
            }
        }
    }
}

