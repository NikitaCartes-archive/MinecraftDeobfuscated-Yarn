/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ResourcePackListWidget
extends AlwaysSelectedEntryListWidget<ResourcePackEntry> {
    private static final Identifier RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
    private static final Text INCOMPATIBLE = new TranslatableText("resourcePack.incompatible", new Object[0]);
    private static final Text INCOMPATIBLE_CONFIRM = new TranslatableText("resourcePack.incompatible.confirm.title", new Object[0]);
    protected final MinecraftClient client;
    private final Text title;

    public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j, Text text) {
        super(minecraftClient, i, j, 32, j - 55 + 4, 36);
        this.client = minecraftClient;
        this.centerListVertically = false;
        minecraftClient.textRenderer.getClass();
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
        this.title = text;
    }

    @Override
    protected void renderHeader(int i, int j, Tessellator tessellator) {
        Text text = new LiteralText("").append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        this.client.textRenderer.draw(text.asFormattedString(), i + this.width / 2 - this.client.textRenderer.getStringWidth(text.asFormattedString()) / 2, Math.min(this.top + 3, j), 0xFFFFFF);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.right - 6;
    }

    public void addEntry(ResourcePackEntry resourcePackEntry) {
        this.addEntry(resourcePackEntry);
        resourcePackEntry.widget = this;
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResourcePackEntry
    extends AlwaysSelectedEntryListWidget.Entry<ResourcePackEntry> {
        private ResourcePackListWidget widget;
        protected final MinecraftClient client;
        protected final ResourcePackOptionsScreen screen;
        private final ClientResourcePackContainer packContainer;

        public ResourcePackEntry(ResourcePackListWidget resourcePackListWidget, ResourcePackOptionsScreen resourcePackOptionsScreen, ClientResourcePackContainer clientResourcePackContainer) {
            this.screen = resourcePackOptionsScreen;
            this.client = MinecraftClient.getInstance();
            this.packContainer = clientResourcePackContainer;
            this.widget = resourcePackListWidget;
        }

        public void method_20145(SelectedResourcePackListWidget selectedResourcePackListWidget) {
            this.getPackContainer().getInitialPosition().insert(selectedResourcePackListWidget.children(), this, ResourcePackEntry::getPackContainer, true);
            this.widget = selectedResourcePackListWidget;
        }

        protected void drawIcon() {
            this.packContainer.drawIcon(this.client.getTextureManager());
        }

        protected ResourcePackCompatibility getCompatibility() {
            return this.packContainer.getCompatibility();
        }

        protected String getDescription() {
            return this.packContainer.getDescription().asFormattedString();
        }

        protected String getDisplayName() {
            return this.packContainer.getDisplayName().asFormattedString();
        }

        public ClientResourcePackContainer getPackContainer() {
            return this.packContainer;
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            int p;
            ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
            if (!resourcePackCompatibility.isCompatible()) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.fill(k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
            }
            this.drawIcon();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.blit(k, j, 0.0f, 0.0f, 32, 32, 32, 32);
            String string = this.getDisplayName();
            String string2 = this.getDescription();
            if (this.method_20151() && (this.client.options.touchscreen || bl)) {
                this.client.getTextureManager().bindTexture(RESOURCE_PACKS_LOCATION);
                DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                p = n - k;
                int q = o - j;
                if (!resourcePackCompatibility.isCompatible()) {
                    string = INCOMPATIBLE.asFormattedString();
                    string2 = resourcePackCompatibility.getNotification().asFormattedString();
                }
                if (this.method_20152()) {
                    if (p < 32) {
                        DrawableHelper.blit(k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.blit(k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.method_20153()) {
                        if (p < 16) {
                            DrawableHelper.blit(k, j, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.blit(k, j, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canSortUp()) {
                        if (p < 32 && p > 16 && q < 16) {
                            DrawableHelper.blit(k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.blit(k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canSortDown()) {
                        if (p < 32 && p > 16 && q > 16) {
                            DrawableHelper.blit(k, j, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.blit(k, j, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            if ((p = this.client.textRenderer.getStringWidth(string)) > 157) {
                string = this.client.textRenderer.trimToWidth(string, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
            }
            this.client.textRenderer.drawWithShadow(string, k + 32 + 2, j + 1, 0xFFFFFF);
            List<String> list = this.client.textRenderer.wrapStringToWidthAsList(string2, 157);
            for (int r = 0; r < 2 && r < list.size(); ++r) {
                this.client.textRenderer.drawWithShadow(list.get(r), k + 32 + 2, j + 12 + 10 * r, 0x808080);
            }
        }

        protected boolean method_20151() {
            return !this.packContainer.isPositionFixed() || !this.packContainer.canBeSorted();
        }

        protected boolean method_20152() {
            return !this.screen.method_2669(this);
        }

        protected boolean method_20153() {
            return this.screen.method_2669(this) && !this.packContainer.canBeSorted();
        }

        protected boolean canSortUp() {
            List list = this.widget.children();
            int i = list.indexOf(this);
            return i > 0 && !((ResourcePackEntry)list.get((int)(i - 1))).packContainer.isPositionFixed();
        }

        protected boolean canSortDown() {
            List list = this.widget.children();
            int i = list.indexOf(this);
            return i >= 0 && i < list.size() - 1 && !((ResourcePackEntry)list.get((int)(i + 1))).packContainer.isPositionFixed();
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double)this.widget.getRowLeft();
            double g = e - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
            if (this.method_20151() && f <= 32.0) {
                if (this.method_20152()) {
                    this.getScreen().setEdited();
                    ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
                    if (resourcePackCompatibility.isCompatible()) {
                        this.getScreen().select(this);
                    } else {
                        Text text = resourcePackCompatibility.getConfirmMessage();
                        this.client.openScreen(new ConfirmScreen(bl -> {
                            this.client.openScreen(this.getScreen());
                            if (bl) {
                                this.getScreen().select(this);
                            }
                        }, INCOMPATIBLE_CONFIRM, text));
                    }
                    return true;
                }
                if (f < 16.0 && this.method_20153()) {
                    this.getScreen().remove(this);
                    return true;
                }
                if (f > 16.0 && g < 16.0 && this.canSortUp()) {
                    List<ResourcePackEntry> list = this.widget.children();
                    int j = list.indexOf(this);
                    list.remove(this);
                    list.add(j - 1, this);
                    this.getScreen().setEdited();
                    return true;
                }
                if (f > 16.0 && g > 16.0 && this.canSortDown()) {
                    List<ResourcePackEntry> list = this.widget.children();
                    int j = list.indexOf(this);
                    list.remove(this);
                    list.add(j + 1, this);
                    this.getScreen().setEdited();
                    return true;
                }
            }
            return false;
        }

        public ResourcePackOptionsScreen getScreen() {
            return this.screen;
        }
    }
}

