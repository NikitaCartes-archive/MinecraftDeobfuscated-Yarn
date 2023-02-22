/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class LogoDrawer
extends DrawableHelper {
    public static final Identifier LOGO_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    public static final Identifier EDITION_TEXTURE = new Identifier("textures/gui/title/edition.png");
    public static final int field_41807 = 274;
    public static final int field_41808 = 44;
    public static final int field_41809 = 30;
    private final boolean minceraft = (double)Random.create().nextFloat() < 1.0E-4;
    private final boolean ignoreAlpha;

    public LogoDrawer(boolean ignoreAlpha) {
        this.ignoreAlpha = ignoreAlpha;
    }

    public void draw(MatrixStack matrices, int screenWidth, float alpha) {
        this.draw(matrices, screenWidth, alpha, 30);
    }

    public void draw(MatrixStack matrices, int screenWidth, float alpha, int y2) {
        RenderSystem.setShaderTexture(0, LOGO_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.ignoreAlpha ? 1.0f : alpha);
        int i = screenWidth / 2 - 137;
        if (this.minceraft) {
            LogoDrawer.drawWithOutline(i, y2, (x, y) -> {
                LogoDrawer.drawTexture(matrices, x, y, 0, 0, 99, 44);
                LogoDrawer.drawTexture(matrices, x + 99, y, 129, 0, 27, 44);
                LogoDrawer.drawTexture(matrices, x + 99 + 26, y, 126, 0, 3, 44);
                LogoDrawer.drawTexture(matrices, x + 99 + 26 + 3, y, 99, 0, 26, 44);
                LogoDrawer.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
            });
        } else {
            LogoDrawer.drawWithOutline(i, y2, (x, y) -> {
                LogoDrawer.drawTexture(matrices, x, y, 0, 0, 155, 44);
                LogoDrawer.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
            });
        }
        RenderSystem.setShaderTexture(0, EDITION_TEXTURE);
        LogoDrawer.drawTexture(matrices, i + 88, y2 + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}

