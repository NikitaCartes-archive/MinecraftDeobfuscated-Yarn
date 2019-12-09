/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SkinProcessor {
    private int[] pixels;
    private int width;
    private int height;

    @Nullable
    public BufferedImage process(BufferedImage image) {
        boolean bl;
        if (image == null) {
            return null;
        }
        this.width = 64;
        this.height = 64;
        BufferedImage bufferedImage = new BufferedImage(this.width, this.height, 2);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        boolean bl2 = bl = image.getHeight() == 32;
        if (bl) {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32, 64, 32);
            graphics.drawImage(bufferedImage, 24, 48, 20, 52, 4, 16, 8, 20, null);
            graphics.drawImage(bufferedImage, 28, 48, 24, 52, 8, 16, 12, 20, null);
            graphics.drawImage(bufferedImage, 20, 52, 16, 64, 8, 20, 12, 32, null);
            graphics.drawImage(bufferedImage, 24, 52, 20, 64, 4, 20, 8, 32, null);
            graphics.drawImage(bufferedImage, 28, 52, 24, 64, 0, 20, 4, 32, null);
            graphics.drawImage(bufferedImage, 32, 52, 28, 64, 12, 20, 16, 32, null);
            graphics.drawImage(bufferedImage, 40, 48, 36, 52, 44, 16, 48, 20, null);
            graphics.drawImage(bufferedImage, 44, 48, 40, 52, 48, 16, 52, 20, null);
            graphics.drawImage(bufferedImage, 36, 52, 32, 64, 48, 20, 52, 32, null);
            graphics.drawImage(bufferedImage, 40, 52, 36, 64, 44, 20, 48, 32, null);
            graphics.drawImage(bufferedImage, 44, 52, 40, 64, 40, 20, 44, 32, null);
            graphics.drawImage(bufferedImage, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        graphics.dispose();
        this.pixels = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
        this.setNoAlpha(0, 0, 32, 16);
        if (bl) {
            this.doNotchTransparencyHack(32, 0, 64, 32);
        }
        this.setNoAlpha(0, 16, 64, 32);
        this.setNoAlpha(16, 48, 48, 64);
        return bufferedImage;
    }

    private void doNotchTransparencyHack(int x0, int y0, int x1, int y1) {
        int j;
        int i;
        for (i = x0; i < x1; ++i) {
            for (j = y0; j < y1; ++j) {
                int k = this.pixels[i + j * this.width];
                if ((k >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (i = x0; i < x1; ++i) {
            for (j = y0; j < y1; ++j) {
                int n = i + j * this.width;
                this.pixels[n] = this.pixels[n] & 0xFFFFFF;
            }
        }
    }

    private void setNoAlpha(int x0, int y0, int x1, int y1) {
        for (int i = x0; i < x1; ++i) {
            for (int j = y0; j < y1; ++j) {
                int n = i + j * this.width;
                this.pixels[n] = this.pixels[n] | 0xFF000000;
            }
        }
    }
}

