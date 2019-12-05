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
import net.minecraft.client.resource.ClientResourcePackProfile;
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

    public void add(ResourcePackEntry resourcePackEntry) {
        this.addEntry(resourcePackEntry);
        resourcePackEntry.resourcePackList = this;
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResourcePackEntry
    extends AlwaysSelectedEntryListWidget.Entry<ResourcePackEntry> {
        private ResourcePackListWidget resourcePackList;
        protected final MinecraftClient client;
        protected final ResourcePackOptionsScreen screen;
        private final ClientResourcePackProfile pack;

        public ResourcePackEntry(ResourcePackListWidget resourcePackListWidget, ResourcePackOptionsScreen resourcePackOptionsScreen, ClientResourcePackProfile clientResourcePackProfile) {
            this.screen = resourcePackOptionsScreen;
            this.client = MinecraftClient.getInstance();
            this.pack = clientResourcePackProfile;
            this.resourcePackList = resourcePackListWidget;
        }

        public void enable(SelectedResourcePackListWidget selectedResourcePackListWidget) {
            this.getPack().getInitialPosition().insert(selectedResourcePackListWidget.children(), this, ResourcePackEntry::getPack, true);
            this.method_24232(selectedResourcePackListWidget);
        }

        public void method_24232(SelectedResourcePackListWidget selectedResourcePackListWidget) {
            this.resourcePackList = selectedResourcePackListWidget;
        }

        protected void drawIcon() {
            this.pack.drawIcon(this.client.getTextureManager());
        }

        protected ResourcePackCompatibility getCompatibility() {
            return this.pack.getCompatibility();
        }

        protected String getDescription() {
            return this.pack.getDescription().asFormattedString();
        }

        protected String getDisplayName() {
            return this.pack.getDisplayName().asFormattedString();
        }

        public ClientResourcePackProfile getPack() {
            return this.pack;
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
            if (this.isMoveable() && (this.client.options.touchscreen || bl)) {
                this.client.getTextureManager().bindTexture(RESOURCE_PACKS_LOCATION);
                DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                p = n - k;
                int q = o - j;
                if (!resourcePackCompatibility.isCompatible()) {
                    string = INCOMPATIBLE.asFormattedString();
                    string2 = resourcePackCompatibility.getNotification().asFormattedString();
                }
                if (this.isSelectable()) {
                    if (p < 32) {
                        DrawableHelper.blit(k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.blit(k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.isRemovable()) {
                        if (p < 16) {
                            DrawableHelper.blit(k, j, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.blit(k, j, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveUp()) {
                        if (p < 32 && p > 16 && q < 16) {
                            DrawableHelper.blit(k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.blit(k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveDown()) {
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

        protected boolean isMoveable() {
            return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
        }

        protected boolean isSelectable() {
            return !this.screen.isEnabled(this);
        }

        protected boolean isRemovable() {
            return this.screen.isEnabled(this) && !this.pack.isAlwaysEnabled();
        }

        protected boolean canMoveUp() {
            List list = this.resourcePackList.children();
            int i = list.indexOf(this);
            return i > 0 && !((ResourcePackEntry)list.get((int)(i - 1))).pack.isPinned();
        }

        protected boolean canMoveDown() {
            List list = this.resourcePackList.children();
            int i = list.indexOf(this);
            return i >= 0 && i < list.size() - 1 && !((ResourcePackEntry)list.get((int)(i + 1))).pack.isPinned();
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double)this.resourcePackList.getRowLeft();
            double g = e - (double)this.resourcePackList.getRowTop(this.resourcePackList.children().indexOf(this));
            if (this.isMoveable() && f <= 32.0) {
                if (this.isSelectable()) {
                    this.getScreen().markDirty();
                    ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
                    if (resourcePackCompatibility.isCompatible()) {
                        this.getScreen().enable(this);
                    } else {
                        Text text = resourcePackCompatibility.getConfirmMessage();
                        this.client.openScreen(new ConfirmScreen(bl -> {
                            this.client.openScreen(this.getScreen());
                            if (bl) {
                                this.getScreen().enable(this);
                            }
                        }, INCOMPATIBLE_CONFIRM, text));
                    }
                    return true;
                }
                if (f < 16.0 && this.isRemovable()) {
                    this.getScreen().disable(this);
                    return true;
                }
                if (f > 16.0 && g < 16.0 && this.canMoveUp()) {
                    List<ResourcePackEntry> list = this.resourcePackList.children();
                    int j = list.indexOf(this);
                    list.remove(j);
                    list.add(j - 1, this);
                    this.getScreen().markDirty();
                    return true;
                }
                if (f > 16.0 && g > 16.0 && this.canMoveDown()) {
                    List<ResourcePackEntry> list = this.resourcePackList.children();
                    int j = list.indexOf(this);
                    list.remove(j);
                    list.add(j + 1, this);
                    this.getScreen().markDirty();
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

