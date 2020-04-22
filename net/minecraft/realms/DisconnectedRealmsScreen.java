/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DisconnectedRealmsScreen
extends RealmsScreen {
    private final String title;
    private final Text reason;
    @Nullable
    private List<Text> lines;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen parent, String titleTranslationKey, Text reason) {
        this.parent = parent;
        this.title = I18n.translate(titleTranslationKey, new Object[0]);
        this.reason = reason;
    }

    @Override
    public void init() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setConnectedToRealms(false);
        minecraftClient.getResourcePackDownloader().clear();
        Realms.narrateNow(this.title + ": " + this.reason.getString());
        this.lines = this.textRenderer.wrapLines(this.reason, this.width - 50);
        this.textHeight = this.lines.size() * this.textRenderer.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.textRenderer.fontHeight, 200, 20, ScreenTexts.BACK, buttonWidget -> minecraftClient.openScreen(this.parent)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            MinecraftClient.getInstance().openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredString(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - this.textRenderer.fontHeight * 2, 0xAAAAAA);
        int i = this.height / 2 - this.textHeight / 2;
        if (this.lines != null) {
            for (Text text : this.lines) {
                this.method_27534(matrices, this.textRenderer, text, this.width / 2, i, 0xFFFFFF);
                i += this.textRenderer.fontHeight;
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

