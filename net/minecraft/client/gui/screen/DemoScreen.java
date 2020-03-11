/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DemoScreen
extends Screen {
    private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");

    public DemoScreen() {
        super(new TranslatableText("demo.help.title", new Object[0]));
    }

    @Override
    protected void init() {
        int i = -16;
        this.addButton(new ButtonWidget(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.buy", new Object[0]), buttonWidget -> {
            buttonWidget.active = false;
            Util.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later", new Object[0]), buttonWidget -> {
            this.client.openScreen(null);
            this.client.mouse.lockCursor();
        }));
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(DEMO_BG);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.drawTexture(i, j, 0, 0, 248, 166);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        this.textRenderer.draw(this.title.asFormattedString(), i, j, 0x1F1F1F);
        GameOptions gameOptions = this.client.options;
        this.textRenderer.draw(I18n.translate("demo.help.movementShort", gameOptions.keyForward.getLocalizedName(), gameOptions.keyLeft.getLocalizedName(), gameOptions.keyBack.getLocalizedName(), gameOptions.keyRight.getLocalizedName()), i, j += 12, 0x4F4F4F);
        this.textRenderer.draw(I18n.translate("demo.help.movementMouse", new Object[0]), i, j + 12, 0x4F4F4F);
        this.textRenderer.draw(I18n.translate("demo.help.jump", gameOptions.keyJump.getLocalizedName()), i, j + 24, 0x4F4F4F);
        this.textRenderer.draw(I18n.translate("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()), i, j + 36, 0x4F4F4F);
        this.textRenderer.drawTrimmed(I18n.translate("demo.help.fullWrapped", new Object[0]), i, j + 68, 218, 0x1F1F1F);
        super.render(mouseX, mouseY, delta);
    }
}

