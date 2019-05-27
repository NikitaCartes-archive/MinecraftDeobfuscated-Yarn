/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DeathScreen
extends Screen {
    private int ticksSinceDeath;
    private final Component message;
    private final boolean isHardcore;

    public DeathScreen(@Nullable Component component, boolean bl) {
        super(new TranslatableComponent(bl ? "deathScreen.title.hardcore" : "deathScreen.title", new Object[0]));
        this.message = component;
        this.isHardcore = bl;
    }

    @Override
    protected void init() {
        String string2;
        String string;
        this.ticksSinceDeath = 0;
        if (this.isHardcore) {
            string = I18n.translate("deathScreen.spectate", new Object[0]);
            string2 = I18n.translate("deathScreen." + (this.minecraft.isInSingleplayer() ? "deleteWorld" : "leaveServer"), new Object[0]);
        } else {
            string = I18n.translate("deathScreen.respawn", new Object[0]);
            string2 = I18n.translate("deathScreen.titleScreen", new Object[0]);
        }
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, string, buttonWidget -> {
            this.minecraft.player.requestRespawn();
            this.minecraft.openScreen(null);
        }));
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, string2, buttonWidget -> {
            if (this.isHardcore) {
                this.minecraft.openScreen(new TitleScreen());
                return;
            }
            ConfirmScreen confirmScreen = new ConfirmScreen(this::method_20373, new TranslatableComponent("deathScreen.quit.confirm", new Object[0]), new TextComponent(""), I18n.translate("deathScreen.titleScreen", new Object[0]), I18n.translate("deathScreen.respawn", new Object[0]));
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

    private void method_20373(boolean bl) {
        if (bl) {
            if (this.minecraft.world != null) {
                this.minecraft.world.disconnect();
            }
            this.minecraft.disconnect(new SaveLevelScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
            this.minecraft.openScreen(new TitleScreen());
        } else {
            this.minecraft.player.requestRespawn();
            this.minecraft.openScreen(null);
        }
    }

    @Override
    public void render(int i, int j, float f) {
        Component component;
        this.fillGradient(0, 0, this.width, this.height, 0x60500000, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2 / 2, 30, 0xFFFFFF);
        GlStateManager.popMatrix();
        if (this.message != null) {
            this.drawCenteredString(this.font, this.message.getFormattedText(), this.width / 2, 85, 0xFFFFFF);
        }
        this.drawCenteredString(this.font, I18n.translate("deathScreen.score", new Object[0]) + ": " + (Object)((Object)ChatFormat.YELLOW) + this.minecraft.player.getScore(), this.width / 2, 100, 0xFFFFFF);
        if (this.message != null && j > 85 && j < 85 + this.font.fontHeight && (component = this.method_2164(i)) != null && component.getStyle().getHoverEvent() != null) {
            this.renderComponentHoverEffect(component, i, j);
        }
        super.render(i, j, f);
    }

    @Nullable
    public Component method_2164(int i) {
        if (this.message == null) {
            return null;
        }
        int j = this.minecraft.textRenderer.getStringWidth(this.message.getFormattedText());
        int k = this.width / 2 - j / 2;
        int l = this.width / 2 + j / 2;
        int m = k;
        if (i < k || i > l) {
            return null;
        }
        for (Component component : this.message) {
            if ((m += this.minecraft.textRenderer.getStringWidth(TextComponentUtil.method_1849(component.getText(), false))) <= i) continue;
            return component;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        Component component;
        if (this.message != null && e > 85.0 && e < (double)(85 + this.font.fontHeight) && (component = this.method_2164((int)d)) != null && component.getStyle().getClickEvent() != null && component.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleComponentClicked(component);
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

