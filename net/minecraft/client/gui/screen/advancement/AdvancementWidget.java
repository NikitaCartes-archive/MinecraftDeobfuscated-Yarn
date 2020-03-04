/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementWidget
extends DrawableHelper {
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
    private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(.+) \\S+");
    private final AdvancementTab tab;
    private final Advancement advancement;
    private final AdvancementDisplay display;
    private final String title;
    private final int width;
    private final List<String> description;
    private final MinecraftClient client;
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    private AdvancementProgress progress;
    private final int xPos;
    private final int yPos;

    public AdvancementWidget(AdvancementTab tab, MinecraftClient client, Advancement advancement, AdvancementDisplay display) {
        this.tab = tab;
        this.advancement = advancement;
        this.display = display;
        this.client = client;
        this.title = client.textRenderer.trimToWidth(display.getTitle().asFormattedString(), 163);
        this.xPos = MathHelper.floor(display.getX() * 28.0f);
        this.yPos = MathHelper.floor(display.getY() * 27.0f);
        int i = advancement.getRequirementCount();
        int j = String.valueOf(i).length();
        int k = i > 1 ? client.textRenderer.getStringWidth("  ") + client.textRenderer.getStringWidth("0") * j * 2 + client.textRenderer.getStringWidth("/") : 0;
        int l = 29 + client.textRenderer.getStringWidth(this.title) + k;
        String string = display.getDescription().asFormattedString();
        this.description = this.wrapDescription(string, l);
        for (String string2 : this.description) {
            l = Math.max(l, client.textRenderer.getStringWidth(string2));
        }
        this.width = l + 3 + 5;
    }

    private List<String> wrapDescription(String description, int width) {
        int j;
        if (description.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = this.client.textRenderer.wrapStringToWidthAsList(description, width);
        if (list.size() < 2) {
            return list;
        }
        String string = list.get(0);
        String string2 = list.get(1);
        int i = this.client.textRenderer.getStringWidth(string + ' ' + string2.split(" ")[0]);
        if (i - width <= 10) {
            return this.client.textRenderer.wrapStringToWidthAsList(description, i);
        }
        Matcher matcher = BACKSLASH_S_PATTERN.matcher(string);
        if (matcher.matches() && width - (j = this.client.textRenderer.getStringWidth(matcher.group(1))) <= 10) {
            return this.client.textRenderer.wrapStringToWidthAsList(description, j);
        }
        return list;
    }

    @Nullable
    private AdvancementWidget getParent(Advancement advancement) {
        while ((advancement = advancement.getParent()) != null && advancement.getDisplay() == null) {
        }
        if (advancement == null || advancement.getDisplay() == null) {
            return null;
        }
        return this.tab.getWidget(advancement);
    }

    public void renderLines(int x, int y, boolean firstPass) {
        if (this.parent != null) {
            int n;
            int i = x + this.parent.xPos + 13;
            int j = x + this.parent.xPos + 26 + 4;
            int k = y + this.parent.yPos + 13;
            int l = x + this.xPos + 13;
            int m = y + this.yPos + 13;
            int n2 = n = firstPass ? -16777216 : -1;
            if (firstPass) {
                this.drawHorizontalLine(j, i, k - 1, n);
                this.drawHorizontalLine(j + 1, i, k, n);
                this.drawHorizontalLine(j, i, k + 1, n);
                this.drawHorizontalLine(l, j - 1, m - 1, n);
                this.drawHorizontalLine(l, j - 1, m, n);
                this.drawHorizontalLine(l, j - 1, m + 1, n);
                this.drawVerticalLine(j - 1, m, k, n);
                this.drawVerticalLine(j + 1, m, k, n);
            } else {
                this.drawHorizontalLine(j, i, k, n);
                this.drawHorizontalLine(l, j, m, n);
                this.drawVerticalLine(j, m, k, n);
            }
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderLines(x, y, firstPass);
        }
    }

    public void renderWidgets(int x, int y) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus advancementObtainedStatus = f >= 1.0f ? AdvancementObtainedStatus.OBTAINED : AdvancementObtainedStatus.UNOBTAINED;
            this.client.getTextureManager().bindTexture(WIDGETS_TEX);
            this.blit(x + this.xPos + 3, y + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26);
            this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), x + this.xPos + 8, y + this.yPos + 5);
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderWidgets(x, y);
        }
    }

    public void setProgress(AdvancementProgress progress) {
        this.progress = progress;
    }

    public void addChild(AdvancementWidget widget) {
        this.children.add(widget);
    }

    public void drawTooltip(int originX, int originY, float alpha, int x, int y) {
        AdvancementObtainedStatus advancementObtainedStatus3;
        AdvancementObtainedStatus advancementObtainedStatus2;
        AdvancementObtainedStatus advancementObtainedStatus;
        boolean bl = x + originX + this.xPos + this.width + 26 >= this.tab.getScreen().width;
        String string = this.progress == null ? null : this.progress.getProgressBarFraction();
        int i = string == null ? 0 : this.client.textRenderer.getStringWidth(string);
        boolean bl2 = 113 - originY - this.yPos - 26 <= 6 + this.description.size() * this.client.textRenderer.fontHeight;
        float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
        int j = MathHelper.floor(f * (float)this.width);
        if (f >= 1.0f) {
            j = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
        } else if (j < 2) {
            j = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        }
        int k = this.width - j;
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        int l = originY + this.yPos;
        int m = bl ? originX + this.xPos - this.width + 26 + 6 : originX + this.xPos;
        int n = 32 + this.description.size() * this.client.textRenderer.fontHeight;
        if (!this.description.isEmpty()) {
            if (bl2) {
                this.method_2324(m, l + 26 - n, this.width, n, 10, 200, 26, 0, 52);
            } else {
                this.method_2324(m, l, this.width, n, 10, 200, 26, 0, 52);
            }
        }
        this.blit(m, l, 0, advancementObtainedStatus.getSpriteIndex() * 26, j, 26);
        this.blit(m + j, l, 200 - k, advancementObtainedStatus2.getSpriteIndex() * 26, k, 26);
        this.blit(originX + this.xPos + 3, originY + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus3.getSpriteIndex() * 26, 26, 26);
        if (bl) {
            this.client.textRenderer.drawWithShadow(this.title, m + 5, originY + this.yPos + 9, -1);
            if (string != null) {
                this.client.textRenderer.drawWithShadow(string, originX + this.xPos - i, originY + this.yPos + 9, -1);
            }
        } else {
            this.client.textRenderer.drawWithShadow(this.title, originX + this.xPos + 32, originY + this.yPos + 9, -1);
            if (string != null) {
                this.client.textRenderer.drawWithShadow(string, originX + this.xPos + this.width - i - 5, originY + this.yPos + 9, -1);
            }
        }
        if (bl2) {
            for (int o = 0; o < this.description.size(); ++o) {
                this.client.textRenderer.draw(this.description.get(o), m + 5, l + 26 - n + 7 + o * this.client.textRenderer.fontHeight, -5592406);
            }
        } else {
            for (int o = 0; o < this.description.size(); ++o) {
                this.client.textRenderer.draw(this.description.get(o), m + 5, originY + this.yPos + 9 + 17 + o * this.client.textRenderer.fontHeight, -5592406);
            }
        }
        this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), originX + this.xPos + 8, originY + this.yPos + 5);
    }

    protected void method_2324(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        this.blit(i, j, p, q, m, m);
        this.method_2321(i + m, j, k - m - m, m, p + m, q, n - m - m, o);
        this.blit(i + k - m, j, p + n - m, q, m, m);
        this.blit(i, j + l - m, p, q + o - m, m, m);
        this.method_2321(i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
        this.blit(i + k - m, j + l - m, p + n - m, q + o - m, m, m);
        this.method_2321(i, j + m, m, l - m - m, p, q + m, n, o - m - m);
        this.method_2321(i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
        this.method_2321(i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
    }

    protected void method_2321(int i, int j, int k, int l, int m, int n, int o, int p) {
        for (int q = 0; q < k; q += o) {
            int r = i + q;
            int s = Math.min(o, k - q);
            for (int t = 0; t < l; t += p) {
                int u = j + t;
                int v = Math.min(p, l - t);
                this.blit(r, u, m, n, s, v);
            }
        }
    }

    public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int i = originX + this.xPos;
        int j = i + 26;
        int k = originY + this.yPos;
        int l = k + 26;
        return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
    }

    public void addToTree() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.yPos;
    }

    public int getX() {
        return this.xPos;
    }
}

