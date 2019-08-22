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
import net.minecraft.util.SystemUtil;

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
            SystemUtil.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later", new Object[0]), buttonWidget -> {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
        }));
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(DEMO_BG);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.blit(i, j, 0, 0, 248, 166);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        int k = (this.width - 248) / 2 + 10;
        int l = (this.height - 166) / 2 + 8;
        this.font.draw(this.title.asFormattedString(), k, l, 0x1F1F1F);
        GameOptions gameOptions = this.minecraft.options;
        this.font.draw(I18n.translate("demo.help.movementShort", gameOptions.keyForward.getLocalizedName(), gameOptions.keyLeft.getLocalizedName(), gameOptions.keyBack.getLocalizedName(), gameOptions.keyRight.getLocalizedName()), k, l += 12, 0x4F4F4F);
        this.font.draw(I18n.translate("demo.help.movementMouse", new Object[0]), k, l + 12, 0x4F4F4F);
        this.font.draw(I18n.translate("demo.help.jump", gameOptions.keyJump.getLocalizedName()), k, l + 24, 0x4F4F4F);
        this.font.draw(I18n.translate("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()), k, l + 36, 0x4F4F4F);
        this.font.drawStringBounded(I18n.translate("demo.help.fullWrapped", new Object[0]), k, l + 68, 218, 0x1F1F1F);
        super.render(i, j, f);
    }
}

