/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
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
    private static final String CENTERED_LINE_PREFIX = "[C]";
    private static final String OBFUSCATION_PLACEHOLDER = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
    private final boolean endCredits;
    private final Runnable finishAction;
    private float time;
    private List<OrderedText> credits;
    private IntSet centeredLines;
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
        this.centeredLines = new IntOpenHashSet();
        Resource resource = null;
        try {
            String string4;
            BufferedReader bufferedReader;
            InputStream inputStream;
            int i = 274;
            if (this.endCredits) {
                int j;
                Object string;
                resource = this.client.getResourceManager().getResource(new Identifier("texts/end.txt"));
                inputStream = resource.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string = bufferedReader.readLine()) != null) {
                    string = ((String)string).replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                    while ((j = ((String)string).indexOf(OBFUSCATION_PLACEHOLDER)) != -1) {
                        String string2 = ((String)string).substring(0, j);
                        String string3 = ((String)string).substring(j + OBFUSCATION_PLACEHOLDER.length());
                        string = string2 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string3;
                    }
                    this.credits.addAll(this.client.textRenderer.wrapLines(new LiteralText((String)string), 274));
                    this.credits.add(OrderedText.EMPTY);
                }
                inputStream.close();
                for (j = 0; j < 8; ++j) {
                    this.credits.add(OrderedText.EMPTY);
                }
            }
            inputStream = this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((string4 = bufferedReader.readLine()) != null) {
                boolean bl;
                string4 = string4.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                if ((string4 = string4.replaceAll("\t", "    ")).startsWith(CENTERED_LINE_PREFIX)) {
                    string4 = string4.substring(3);
                    bl = true;
                } else {
                    bl = false;
                }
                List<OrderedText> list = this.client.textRenderer.wrapLines(new LiteralText(string4), 274);
                for (OrderedText orderedText : list) {
                    if (bl) {
                        this.centeredLines.add(this.credits.size());
                    }
                    this.credits.add(orderedText);
                }
                this.credits.add(OrderedText.EMPTY);
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
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
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, this.getZOffset()).texture(0.0f, f * 0.015625f).color(j, j, j, 1.0f).next();
        bufferBuilder.vertex(i, this.height, this.getZOffset()).texture((float)i * 0.015625f, f * 0.015625f).color(j, j, j, 1.0f).next();
        bufferBuilder.vertex(i, 0.0, this.getZOffset()).texture((float)i * 0.015625f, g * 0.015625f).color(j, j, j, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, g * 0.015625f).color(j, j, j, 1.0f).next();
        tessellator.draw();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int m;
        this.renderBackground(mouseX, mouseY, delta);
        int i = 274;
        int j = this.width / 2 - 137;
        int k = this.height + 50;
        this.time += delta;
        float f = -this.time * this.speed;
        matrices.push();
        matrices.translate(0.0, f, 0.0);
        RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        this.drawWithOutline(j, k, (integer, integer2) -> {
            this.drawTexture(matrices, integer + 0, (int)integer2, 0, 0, 155, 44);
            this.drawTexture(matrices, integer + 155, (int)integer2, 0, 45, 155, 44);
        });
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);
        CreditsScreen.drawTexture(matrices, j + 88, k + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        int l = k + 100;
        for (m = 0; m < this.credits.size(); ++m) {
            float g;
            if (m == this.credits.size() - 1 && (g = (float)l + f - (float)(this.height / 2 - 6)) < 0.0f) {
                matrices.translate(0.0, -g, 0.0);
            }
            if ((float)l + f + 12.0f + 8.0f > 0.0f && (float)l + f < (float)this.height) {
                OrderedText orderedText = this.credits.get(m);
                if (this.centeredLines.contains(m)) {
                    this.textRenderer.drawWithShadow(matrices, orderedText, (float)(j + (274 - this.textRenderer.getWidth(orderedText)) / 2), (float)l, 0xFFFFFF);
                } else {
                    this.textRenderer.random.setSeed((long)((float)((long)m * 4238972211L) + this.time / 4.0f));
                    this.textRenderer.drawWithShadow(matrices, orderedText, (float)j, (float)l, 0xFFFFFF);
                }
            }
            l += 12;
        }
        matrices.pop();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        m = this.width;
        int n = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, n, this.getZOffset()).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(m, n, this.getZOffset()).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(m, 0.0, this.getZOffset()).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        super.render(matrices, mouseX, mouseY, delta);
    }
}

