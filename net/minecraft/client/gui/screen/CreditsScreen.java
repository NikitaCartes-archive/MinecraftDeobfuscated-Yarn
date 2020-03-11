/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CreditsScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    private final boolean endCredits;
    private final Runnable finishAction;
    private float time;
    private List<String> credits;
    private int creditsHeight;
    private float speed = 0.5f;

    public CreditsScreen(boolean endCredits, Runnable finishAction) {
        super(NarratorManager.EMPTY);
        this.endCredits = endCredits;
        this.finishAction = finishAction;
        if (!endCredits) {
            this.speed = 0.75f;
        }
    }

    @Override
    public void tick() {
        this.client.getMusicTracker().tick();
        this.client.getSoundManager().tick(false);
        float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        if (this.time > f) {
            this.close();
        }
    }

    @Override
    public void onClose() {
        this.close();
    }

    private void close() {
        this.finishAction.run();
        this.client.openScreen(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void init() {
        if (this.credits != null) {
            return;
        }
        this.credits = Lists.newArrayList();
        Resource resource = null;
        try {
            String string5;
            BufferedReader bufferedReader;
            InputStream inputStream;
            String string = "" + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + (Object)((Object)Formatting.GREEN) + (Object)((Object)Formatting.AQUA);
            int i = 274;
            if (this.endCredits) {
                int j;
                String string2;
                resource = this.client.getResourceManager().getResource(new Identifier("texts/end.txt"));
                inputStream = resource.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string2 = bufferedReader.readLine()) != null) {
                    string2 = string2.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                    while (string2.contains(string)) {
                        j = string2.indexOf(string);
                        String string3 = string2.substring(0, j);
                        String string4 = string2.substring(j + string.length());
                        string2 = string3 + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
                    }
                    this.credits.addAll(this.client.textRenderer.wrapStringToWidthAsList(string2, 274));
                    this.credits.add("");
                }
                inputStream.close();
                for (j = 0; j < 8; ++j) {
                    this.credits.add("");
                }
            }
            inputStream = this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((string5 = bufferedReader.readLine()) != null) {
                string5 = string5.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                string5 = string5.replaceAll("\t", "    ");
                this.credits.addAll(this.client.textRenderer.wrapStringToWidthAsList(string5, 274));
                this.credits.add("");
            }
            inputStream.close();
            this.creditsHeight = this.credits.size() * 12;
            IOUtils.closeQuietly((Closeable)resource);
        } catch (Exception exception) {
            LOGGER.error("Couldn't load credits", (Throwable)exception);
        } finally {
            IOUtils.closeQuietly(resource);
        }
    }

    private void renderBackground(int mouseX, int mouseY, float tickDelta) {
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        int i = this.width;
        float f = -this.time * 0.5f * this.speed;
        float g = (float)this.height - this.time * 0.5f * this.speed;
        float h = 0.015625f;
        float j = this.time * 0.02f;
        float k = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        float l = (k - 20.0f - this.time) * 0.005f;
        if (l < j) {
            j = l;
        }
        if (j > 1.0f) {
            j = 1.0f;
        }
        j *= j;
        j = j * 96.0f / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, this.getZOffset()).texture(0.0f, f * 0.015625f).color(j, j, j, 1.0f).next();
        bufferBuilder.vertex(i, this.height, this.getZOffset()).texture((float)i * 0.015625f, f * 0.015625f).color(j, j, j, 1.0f).next();
        bufferBuilder.vertex(i, 0.0, this.getZOffset()).texture((float)i * 0.015625f, g * 0.015625f).color(j, j, j, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, g * 0.015625f).color(j, j, j, 1.0f).next();
        tessellator.draw();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int m;
        this.renderBackground(mouseX, mouseY, delta);
        int i = 274;
        int j = this.width / 2 - 137;
        int k = this.height + 50;
        this.time += delta;
        float f = -this.time * this.speed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, f, 0.0f);
        this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
        this.drawTexture(j, k, 0, 0, 155, 44);
        this.drawTexture(j + 155, k, 0, 45, 155, 44);
        this.client.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
        CreditsScreen.drawTexture(j + 88, k + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.disableAlphaTest();
        int l = k + 100;
        for (m = 0; m < this.credits.size(); ++m) {
            float g;
            if (m == this.credits.size() - 1 && (g = (float)l + f - (float)(this.height / 2 - 6)) < 0.0f) {
                RenderSystem.translatef(0.0f, -g, 0.0f);
            }
            if ((float)l + f + 12.0f + 8.0f > 0.0f && (float)l + f < (float)this.height) {
                String string = this.credits.get(m);
                if (string.startsWith("[C]")) {
                    this.textRenderer.drawWithShadow(string.substring(3), j + (274 - this.textRenderer.getStringWidth(string.substring(3))) / 2, l, 0xFFFFFF);
                } else {
                    this.textRenderer.random.setSeed((long)((float)((long)m * 4238972211L) + this.time / 4.0f));
                    this.textRenderer.drawWithShadow(string, j, l, 0xFFFFFF);
                }
            }
            l += 12;
        }
        RenderSystem.popMatrix();
        this.client.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        m = this.width;
        int n = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, n, this.getZOffset()).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(m, n, this.getZOffset()).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(m, 0.0, this.getZOffset()).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        super.render(mouseX, mouseY, delta);
    }
}

