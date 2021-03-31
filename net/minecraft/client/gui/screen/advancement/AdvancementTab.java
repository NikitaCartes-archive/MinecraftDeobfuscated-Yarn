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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
    private final Text title;
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

    public AdvancementTab(MinecraftClient client, AdvancementsScreen screen, AdvancementTabType type, int index, Advancement root, AdvancementDisplay display) {
        this.client = client;
        this.screen = screen;
        this.type = type;
        this.index = index;
        this.root = root;
        this.display = display;
        this.icon = display.getIcon();
        this.title = display.getTitle();
        this.rootWidget = new AdvancementWidget(this, client, root, display);
        this.addWidget(this.rootWidget, root);
    }

    public AdvancementTabType method_35734() {
        return this.type;
    }

    public int method_35735() {
        return this.index;
    }

    public Advancement getRoot() {
        return this.root;
    }

    public Text getTitle() {
        return this.title;
    }

    public AdvancementDisplay method_35736() {
        return this.display;
    }

    public void drawBackground(MatrixStack matrices, int i, int j, boolean bl) {
        this.type.drawBackground(matrices, this, i, j, bl, this.index);
    }

    public void drawIcon(int x, int y, ItemRenderer itemRenderer) {
        this.type.drawIcon(x, y, this.index, itemRenderer, this.icon);
    }

    public void render(MatrixStack matrices) {
        if (!this.initialized) {
            this.originX = 117 - (this.maxPanX + this.minPanX) / 2;
            this.originY = 56 - (this.maxPanY + this.minPanY) / 2;
            this.initialized = true;
        }
        matrices.push();
        matrices.translate(0.0, 0.0, 950.0);
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(matrices, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrices.translate(0.0, 0.0, -950.0);
        RenderSystem.depthFunc(518);
        AdvancementTab.fill(matrices, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        Identifier identifier = this.display.getBackground();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (identifier != null) {
            RenderSystem.setShaderTexture(0, identifier);
        } else {
            RenderSystem.setShaderTexture(0, TextureManager.MISSING_IDENTIFIER);
        }
        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);
        int k = i % 16;
        int l = j % 16;
        for (int m = -1; m <= 15; ++m) {
            for (int n = -1; n <= 8; ++n) {
                AdvancementTab.drawTexture(matrices, k + 16 * m, l + 16 * n, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.rootWidget.renderLines(matrices, i, j, true);
        this.rootWidget.renderLines(matrices, i, j, false);
        this.rootWidget.renderWidgets(matrices, i, j);
        RenderSystem.depthFunc(518);
        matrices.translate(0.0, 0.0, -950.0);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(matrices, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        matrices.pop();
    }

    public void drawWidgetTooltip(MatrixStack matrices, int i, int j, int k, int l) {
        matrices.push();
        matrices.translate(0.0, 0.0, -200.0);
        AdvancementTab.fill(matrices, 0, 0, 234, 113, MathHelper.floor(this.alpha * 255.0f) << 24);
        boolean bl = false;
        int m = MathHelper.floor(this.originX);
        int n = MathHelper.floor(this.originY);
        if (i > 0 && i < 234 && j > 0 && j < 113) {
            for (AdvancementWidget advancementWidget : this.widgets.values()) {
                if (!advancementWidget.shouldRender(m, n, i, j)) continue;
                bl = true;
                advancementWidget.drawTooltip(matrices, m, n, this.alpha, k, l);
                break;
            }
        }
        matrices.pop();
        this.alpha = bl ? MathHelper.clamp(this.alpha + 0.02f, 0.0f, 0.3f) : MathHelper.clamp(this.alpha - 0.04f, 0.0f, 1.0f);
    }

    public boolean isClickOnTab(int screenX, int screenY, double mouseX, double mouseY) {
        return this.type.isClickOnTab(screenX, screenY, this.index, mouseX, mouseY);
    }

    @Nullable
    public static AdvancementTab create(MinecraftClient minecraft, AdvancementsScreen screen, int index, Advancement root) {
        if (root.getDisplay() == null) {
            return null;
        }
        for (AdvancementTabType advancementTabType : AdvancementTabType.values()) {
            if (index >= advancementTabType.getTabCount()) {
                index -= advancementTabType.getTabCount();
                continue;
            }
            return new AdvancementTab(minecraft, screen, advancementTabType, index, root, root.getDisplay());
        }
        return null;
    }

    public void move(double offsetX, double offsetY) {
        if (this.maxPanX - this.minPanX > 234) {
            this.originX = MathHelper.clamp(this.originX + offsetX, (double)(-(this.maxPanX - 234)), 0.0);
        }
        if (this.maxPanY - this.minPanY > 113) {
            this.originY = MathHelper.clamp(this.originY + offsetY, (double)(-(this.maxPanY - 113)), 0.0);
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return;
        }
        AdvancementWidget advancementWidget = new AdvancementWidget(this, this.client, advancement, advancement.getDisplay());
        this.addWidget(advancementWidget, advancement);
    }

    private void addWidget(AdvancementWidget widget, Advancement advancement) {
        this.widgets.put(advancement, widget);
        int i = widget.getX();
        int j = i + 28;
        int k = widget.getY();
        int l = k + 27;
        this.minPanX = Math.min(this.minPanX, i);
        this.maxPanX = Math.max(this.maxPanX, j);
        this.minPanY = Math.min(this.minPanY, k);
        this.maxPanY = Math.max(this.maxPanY, l);
        for (AdvancementWidget advancementWidget : this.widgets.values()) {
            advancementWidget.addToTree();
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

