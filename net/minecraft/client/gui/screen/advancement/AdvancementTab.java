/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementTab
extends DrawableHelper {
    private final MinecraftClient client;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement root;
    private final AdvancementDisplay display;
    private final ItemStack icon;
    private final String title;
    private final AdvancementWidget rootWidget;
    private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
    private double originX;
    private double originY;
    private int minPanX = Integer.MAX_VALUE;
    private int minPanY = Integer.MAX_VALUE;
    private int maxPanX = Integer.MIN_VALUE;
    private int maxPanY = Integer.MIN_VALUE;
    private float alpha;
    private boolean initialized;

    public AdvancementTab(MinecraftClient minecraftClient, AdvancementsScreen advancementsScreen, AdvancementTabType advancementTabType, int i, Advancement advancement, AdvancementDisplay advancementDisplay) {
        this.client = minecraftClient;
        this.screen = advancementsScreen;
        this.type = advancementTabType;
        this.index = i;
        this.root = advancement;
        this.display = advancementDisplay;
        this.icon = advancementDisplay.getIcon();
        this.title = advancementDisplay.getTitle().asFormattedString();
        this.rootWidget = new AdvancementWidget(this, minecraftClient, advancement, advancementDisplay);
        this.addWidget(this.rootWidget, advancement);
    }

    public Advancement getRoot() {
        return this.root;
    }

    public String getTitle() {
        return this.title;
    }

    public void drawBackground(int i, int j, boolean bl) {
        this.type.drawBackground(this, i, j, bl, this.index);
    }

    public void drawIcon(int i, int j, ItemRenderer itemRenderer) {
        this.type.drawIcon(i, j, this.index, itemRenderer, this.icon);
    }

    public void render() {
        if (!this.initialized) {
            this.originX = 117 - (this.maxPanX + this.minPanX) / 2;
            this.originY = 56 - (this.maxPanY + this.minPanY) / 2;
            this.initialized = true;
        }
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0f, 0.0f, 950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0f, 0.0f, -950.0f);
        RenderSystem.depthFunc(518);
        AdvancementTab.fill(234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        Identifier identifier = this.display.getBackground();
        if (identifier != null) {
            this.client.getTextureManager().bindTexture(identifier);
        } else {
            this.client.getTextureManager().bindTexture(TextureManager.MISSING_IDENTIFIER);
        }
        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);
        int k = i % 16;
        int l = j % 16;
        for (int m = -1; m <= 15; ++m) {
            for (int n = -1; n <= 8; ++n) {
                AdvancementTab.blit(k + 16 * m, l + 16 * n, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.rootWidget.renderLines(i, j, true);
        this.rootWidget.renderLines(i, j, false);
        this.rootWidget.renderWidgets(i, j);
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0f, 0.0f, -950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0f, 0.0f, 950.0f);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();
    }

    public void drawWidgetTooltip(int i, int j, int k, int l) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 200.0f);
        AdvancementTab.fill(0, 0, 234, 113, MathHelper.floor(this.alpha * 255.0f) << 24);
        boolean bl = false;
        int m = MathHelper.floor(this.originX);
        int n = MathHelper.floor(this.originY);
        if (i > 0 && i < 234 && j > 0 && j < 113) {
            for (AdvancementWidget advancementWidget : this.widgets.values()) {
                if (!advancementWidget.shouldRender(m, n, i, j)) continue;
                bl = true;
                advancementWidget.drawTooltip(m, n, this.alpha, k, l);
                break;
            }
        }
        RenderSystem.popMatrix();
        this.alpha = bl ? MathHelper.clamp(this.alpha + 0.02f, 0.0f, 0.3f) : MathHelper.clamp(this.alpha - 0.04f, 0.0f, 1.0f);
    }

    public boolean isClickOnTab(int i, int j, double d, double e) {
        return this.type.isClickOnTab(i, j, this.index, d, e);
    }

    @Nullable
    public static AdvancementTab create(MinecraftClient minecraftClient, AdvancementsScreen advancementsScreen, int i, Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return null;
        }
        for (AdvancementTabType advancementTabType : AdvancementTabType.values()) {
            if (i >= advancementTabType.getTabCount()) {
                i -= advancementTabType.getTabCount();
                continue;
            }
            return new AdvancementTab(minecraftClient, advancementsScreen, advancementTabType, i, advancement, advancement.getDisplay());
        }
        return null;
    }

    public void move(double d, double e) {
        if (this.maxPanX - this.minPanX > 234) {
            this.originX = MathHelper.clamp(this.originX + d, (double)(-(this.maxPanX - 234)), 0.0);
        }
        if (this.maxPanY - this.minPanY > 113) {
            this.originY = MathHelper.clamp(this.originY + e, (double)(-(this.maxPanY - 113)), 0.0);
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return;
        }
        AdvancementWidget advancementWidget = new AdvancementWidget(this, this.client, advancement, advancement.getDisplay());
        this.addWidget(advancementWidget, advancement);
    }

    private void addWidget(AdvancementWidget advancementWidget, Advancement advancement) {
        this.widgets.put(advancement, advancementWidget);
        int i = advancementWidget.getX();
        int j = i + 28;
        int k = advancementWidget.getY();
        int l = k + 27;
        this.minPanX = Math.min(this.minPanX, i);
        this.maxPanX = Math.max(this.maxPanX, j);
        this.minPanY = Math.min(this.minPanY, k);
        this.maxPanY = Math.max(this.maxPanY, l);
        for (AdvancementWidget advancementWidget2 : this.widgets.values()) {
            advancementWidget2.addToTree();
        }
    }

    @Nullable
    public AdvancementWidget getWidget(Advancement advancement) {
        return this.widgets.get(advancement);
    }

    public AdvancementsScreen getScreen() {
        return this.screen;
    }
}

