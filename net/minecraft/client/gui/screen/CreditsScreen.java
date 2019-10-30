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

    public CreditsScreen(boolean bl, Runnable runnable) {
        super(NarratorManager.EMPTY);
        this.endCredits = bl;
        this.finishAction = runnable;
        if (!bl) {
            this.speed = 0.75f;
        }
    }

    @Override
    public void tick() {
        this.minecraft.getMusicTracker().tick();
        this.minecraft.getSoundManager().tick(false);
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
        this.minecraft.openScreen(null);
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
                resource = this.minecraft.getResourceManager().getResource(new Identifier("texts/end.txt"));
                inputStream = resource.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string2 = bufferedReader.readLine()) != null) {
                    string2 = string2.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                    while (string2.contains(string)) {
                        j = string2.indexOf(string);
                        String string3 = string2.substring(0, j);
                        String string4 = string2.substring(j + string.length());
                        string2 = string3 + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
                    }
                    this.credits.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string2, 274));
                    this.credits.add("");
                }
                inputStream.close();
                for (j = 0; j < 8; ++j) {
                    this.credits.add("");
                }
            }
            inputStream = this.minecraft.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((string5 = bufferedReader.readLine()) != null) {
                string5 = string5.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                string5 = string5.replaceAll("\t", "    ");
                this.credits.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string5, 274));
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

    private void renderBackground(int i, int j, float f) {
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        int k = this.width;
        float g = -this.time * 0.5f * this.speed;
        float h = (float)this.height - this.time * 0.5f * this.speed;
        float l = 0.015625f;
        float m = this.time * 0.02f;
        float n = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        float o = (n - 20.0f - this.time) * 0.005f;
        if (o < m) {
            m = o;
        }
        if (m > 1.0f) {
            m = 1.0f;
        }
        m *= m;
        m = m * 96.0f / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, this.getBlitOffset()).texture(0.0f, g * 0.015625f).color(m, m, m, 1.0f).next();
        bufferBuilder.vertex(k, this.height, this.getBlitOffset()).texture((float)k * 0.015625f, g * 0.015625f).color(m, m, m, 1.0f).next();
        bufferBuilder.vertex(k, 0.0, this.getBlitOffset()).texture((float)k * 0.015625f, h * 0.015625f).color(m, m, m, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getBlitOffset()).texture(0.0f, h * 0.015625f).color(m, m, m, 1.0f).next();
        tessellator.draw();
    }

    @Override
    public void render(int i, int j, float f) {
        int o;
        this.renderBackground(i, j, f);
        int k = 274;
        int l = this.width / 2 - 137;
        int m = this.height + 50;
        this.time += f;
        float g = -this.time * this.speed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, g, 0.0f);
        this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
        this.blit(l, m, 0, 0, 155, 44);
        this.blit(l + 155, m, 0, 45, 155, 44);
        this.minecraft.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
        CreditsScreen.blit(l + 88, m + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.disableAlphaTest();
        int n = m + 100;
        for (o = 0; o < this.credits.size(); ++o) {
            float h;
            if (o == this.credits.size() - 1 && (h = (float)n + g - (float)(this.height / 2 - 6)) < 0.0f) {
                RenderSystem.translatef(0.0f, -h, 0.0f);
            }
            if ((float)n + g + 12.0f + 8.0f > 0.0f && (float)n + g < (float)this.height) {
                String string = this.credits.get(o);
                if (string.startsWith("[C]")) {
                    this.font.drawWithShadow(string.substring(3), l + (274 - this.font.getStringWidth(string.substring(3))) / 2, n, 0xFFFFFF);
                } else {
                    this.font.random.setSeed((long)((float)((long)o * 4238972211L) + this.time / 4.0f));
                    this.font.drawWithShadow(string, l, n, 0xFFFFFF);
                }
            }
            n += 12;
        }
        RenderSystem.popMatrix();
        this.minecraft.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        o = this.width;
        int p = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, p, this.getBlitOffset()).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(o, p, this.getBlitOffset()).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(o, 0.0, this.getBlitOffset()).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getBlitOffset()).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        super.render(i, j, f);
    }
}

