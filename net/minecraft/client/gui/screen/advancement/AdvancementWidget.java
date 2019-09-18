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
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementWidget
extends DrawableHelper {
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
    private static final Pattern field_2708 = Pattern.compile("(.+) \\S+");
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

    public AdvancementWidget(AdvancementTab advancementTab, MinecraftClient minecraftClient, Advancement advancement, AdvancementDisplay advancementDisplay) {
        this.tab = advancementTab;
        this.advancement = advancement;
        this.display = advancementDisplay;
        this.client = minecraftClient;
        this.title = minecraftClient.textRenderer.trimToWidth(advancementDisplay.getTitle().asFormattedString(), 163);
        this.xPos = MathHelper.floor(advancementDisplay.getX() * 28.0f);
        this.yPos = MathHelper.floor(advancementDisplay.getY() * 27.0f);
        int i = advancement.getRequirementCount();
        int j = String.valueOf(i).length();
        int k = i > 1 ? minecraftClient.textRenderer.getStringWidth("  ") + minecraftClient.textRenderer.getStringWidth("0") * j * 2 + minecraftClient.textRenderer.getStringWidth("/") : 0;
        int l = 29 + minecraftClient.textRenderer.getStringWidth(this.title) + k;
        String string = advancementDisplay.getDescription().asFormattedString();
        this.description = this.wrapDescription(string, l);
        for (String string2 : this.description) {
            l = Math.max(l, minecraftClient.textRenderer.getStringWidth(string2));
        }
        this.width = l + 3 + 5;
    }

    private List<String> wrapDescription(String string, int i) {
        int k;
        if (string.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = this.client.textRenderer.wrapStringToWidthAsList(string, i);
        if (list.size() < 2) {
            return list;
        }
        String string2 = list.get(0);
        String string3 = list.get(1);
        int j = this.client.textRenderer.getStringWidth(string2 + ' ' + string3.split(" ")[0]);
        if (j - i <= 10) {
            return this.client.textRenderer.wrapStringToWidthAsList(string, j);
        }
        Matcher matcher = field_2708.matcher(string2);
        if (matcher.matches() && i - (k = this.client.textRenderer.getStringWidth(matcher.group(1))) <= 10) {
            return this.client.textRenderer.wrapStringToWidthAsList(string, k);
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

    public void renderLines(int i, int j, boolean bl) {
        if (this.parent != null) {
            int p;
            int k = i + this.parent.xPos + 13;
            int l = i + this.parent.xPos + 26 + 4;
            int m = j + this.parent.yPos + 13;
            int n = i + this.xPos + 13;
            int o = j + this.yPos + 13;
            int n2 = p = bl ? -16777216 : -1;
            if (bl) {
                this.hLine(l, k, m - 1, p);
                this.hLine(l + 1, k, m, p);
                this.hLine(l, k, m + 1, p);
                this.hLine(n, l - 1, o - 1, p);
                this.hLine(n, l - 1, o, p);
                this.hLine(n, l - 1, o + 1, p);
                this.vLine(l - 1, o, m, p);
                this.vLine(l + 1, o, m, p);
            } else {
                this.hLine(l, k, m, p);
                this.hLine(n, l, o, p);
                this.vLine(l, o, m, p);
            }
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderLines(i, j, bl);
        }
    }

    public void renderWidgets(int i, int j) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus advancementObtainedStatus = f >= 1.0f ? AdvancementObtainedStatus.OBTAINED : AdvancementObtainedStatus.UNOBTAINED;
            this.client.getTextureManager().method_22813(WIDGETS_TEX);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableBlend();
            this.blit(i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26);
            GuiLighting.enableForItems();
            this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderWidgets(i, j);
        }
    }

    public void setProgress(AdvancementProgress advancementProgress) {
        this.progress = advancementProgress;
    }

    public void addChild(AdvancementWidget advancementWidget) {
        this.children.add(advancementWidget);
    }

    public void method_2331(int i, int j, float f, int k, int l) {
        AdvancementObtainedStatus advancementObtainedStatus3;
        AdvancementObtainedStatus advancementObtainedStatus2;
        AdvancementObtainedStatus advancementObtainedStatus;
        boolean bl = k + i + this.xPos + this.width + 26 >= this.tab.getScreen().width;
        String string = this.progress == null ? null : this.progress.getProgressBarFraction();
        int m = string == null ? 0 : this.client.textRenderer.getStringWidth(string);
        boolean bl2 = 113 - j - this.yPos - 26 <= 6 + this.description.size() * this.client.textRenderer.fontHeight;
        float g = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
        int n = MathHelper.floor(g * (float)this.width);
        if (g >= 1.0f) {
            n = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
        } else if (n < 2) {
            n = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (n > this.width - 2) {
            n = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        }
        int o = this.width - n;
        this.client.getTextureManager().method_22813(WIDGETS_TEX);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        int p = j + this.yPos;
        int q = bl ? i + this.xPos - this.width + 26 + 6 : i + this.xPos;
        int r = 32 + this.description.size() * this.client.textRenderer.fontHeight;
        if (!this.description.isEmpty()) {
            if (bl2) {
                this.method_2324(q, p + 26 - r, this.width, r, 10, 200, 26, 0, 52);
            } else {
                this.method_2324(q, p, this.width, r, 10, 200, 26, 0, 52);
            }
        }
        this.blit(q, p, 0, advancementObtainedStatus.getSpriteIndex() * 26, n, 26);
        this.blit(q + n, p, 200 - o, advancementObtainedStatus2.getSpriteIndex() * 26, o, 26);
        this.blit(i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus3.getSpriteIndex() * 26, 26, 26);
        if (bl) {
            this.client.textRenderer.drawWithShadow(this.title, q + 5, j + this.yPos + 9, -1);
            if (string != null) {
                this.client.textRenderer.drawWithShadow(string, i + this.xPos - m, j + this.yPos + 9, -1);
            }
        } else {
            this.client.textRenderer.drawWithShadow(this.title, i + this.xPos + 32, j + this.yPos + 9, -1);
            if (string != null) {
                this.client.textRenderer.drawWithShadow(string, i + this.xPos + this.width - m - 5, j + this.yPos + 9, -1);
            }
        }
        if (bl2) {
            for (int s = 0; s < this.description.size(); ++s) {
                this.client.textRenderer.draw(this.description.get(s), q + 5, p + 26 - r + 7 + s * this.client.textRenderer.fontHeight, -5592406);
            }
        } else {
            for (int s = 0; s < this.description.size(); ++s) {
                this.client.textRenderer.draw(this.description.get(s), q + 5, j + this.yPos + 9 + 17 + s * this.client.textRenderer.fontHeight, -5592406);
            }
        }
        GuiLighting.enableForItems();
        this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
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

    public boolean method_2329(int i, int j, int k, int l) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int m = i + this.xPos;
        int n = m + 26;
        int o = j + this.yPos;
        int p = o + 26;
        return k >= m && k <= n && l >= o && l <= p;
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

