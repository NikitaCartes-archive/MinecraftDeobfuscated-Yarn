/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DemoScreen
extends Screen {
    private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");
    private MultilineText movementText = MultilineText.EMPTY;
    private MultilineText fullWrappedText = MultilineText.EMPTY;

    public DemoScreen() {
        super(Text.translatable("demo.help.title"));
    }

    @Override
    protected void init() {
        int i = -16;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("demo.help.buy"), button -> {
            button.active = false;
            Util.getOperatingSystem().open("https://aka.ms/BuyMinecraftJava");
        }).dimensions(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("demo.help.later"), button -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }).dimensions(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
        GameOptions gameOptions = this.client.options;
        this.movementText = MultilineText.create(this.textRenderer, Text.translatable("demo.help.movementShort", gameOptions.forwardKey.getBoundKeyLocalizedText(), gameOptions.leftKey.getBoundKeyLocalizedText(), gameOptions.backKey.getBoundKeyLocalizedText(), gameOptions.rightKey.getBoundKeyLocalizedText()), Text.translatable("demo.help.movementMouse"), Text.translatable("demo.help.jump", gameOptions.jumpKey.getBoundKeyLocalizedText()), Text.translatable("demo.help.inventory", gameOptions.inventoryKey.getBoundKeyLocalizedText()));
        this.fullWrappedText = MultilineText.create(this.textRenderer, (StringVisitable)Text.translatable("demo.help.fullWrapped"), 218);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        super.renderBackground(matrices);
        RenderSystem.setShaderTexture(0, DEMO_BG);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        DemoScreen.drawTexture(matrices, i, j, 0, 0, 248, 166);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        this.textRenderer.draw(matrices, this.title, (float)i, (float)j, 0x1F1F1F);
        j = this.movementText.draw(matrices, i, j + 12, 12, 0x4F4F4F);
        this.fullWrappedText.draw(matrices, i, j + 20, this.textRenderer.fontHeight, 0x1F1F1F);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

