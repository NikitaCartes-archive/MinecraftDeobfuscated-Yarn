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

    public ResourcePackListWidget(MinecraftClient client, int width, int height, Text title) {
        super(client, width, height, 32, height - 55 + 4, 36);
        this.client = client;
        this.centerListVertically = false;
        client.textRenderer.getClass();
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
        this.title = title;
    }

    @Override
    protected void renderHeader(int x, int y, Tessellator tessellator) {
        Text text = new LiteralText("").append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        this.client.textRenderer.draw(text.asFormattedString(), x + this.width / 2 - this.client.textRenderer.getStringWidth(text.asFormattedString()) / 2, Math.min(this.top + 3, y), 0xFFFFFF);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.right - 6;
    }

    public void add(ResourcePackEntry entry) {
        this.addEntry(entry);
        entry.resourcePackList = this;
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResourcePackEntry
    extends AlwaysSelectedEntryListWidget.Entry<ResourcePackEntry> {
        private ResourcePackListWidget resourcePackList;
        protected final MinecraftClient client;
        protected final ResourcePackOptionsScreen screen;
        private final ClientResourcePackProfile pack;

        public ResourcePackEntry(ResourcePackListWidget listWidget, ResourcePackOptionsScreen screen, ClientResourcePackProfile pack) {
            this.screen = screen;
            this.client = MinecraftClient.getInstance();
            this.pack = pack;
            this.resourcePackList = listWidget;
        }

        public void enable(SelectedResourcePackListWidget list) {
            this.getPack().getInitialPosition().insert(list.children(), this, ResourcePackEntry::getPack, true);
            this.method_24232(list);
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
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
            int i;
            ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
            if (!resourcePackCompatibility.isCompatible()) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.fill(x - 1, y - 1, x + width - 9, y + height + 1, -8978432);
            }
            this.drawIcon();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            String string = this.getDisplayName();
            String string2 = this.getDescription();
            if (this.isMoveable() && (this.client.options.touchscreen || hovering)) {
                this.client.getTextureManager().bindTexture(RESOURCE_PACKS_LOCATION);
                DrawableHelper.fill(x, y, x + 32, y + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                i = mouseX - x;
                int j = mouseY - y;
                if (!resourcePackCompatibility.isCompatible()) {
                    string = INCOMPATIBLE.asFormattedString();
                    string2 = resourcePackCompatibility.getNotification().asFormattedString();
                }
                if (this.isSelectable()) {
                    if (i < 32) {
                        DrawableHelper.drawTexture(x, y, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(x, y, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.isRemovable()) {
                        if (i < 16) {
                            DrawableHelper.drawTexture(x, y, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(x, y, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveUp()) {
                        if (i < 32 && i > 16 && j < 16) {
                            DrawableHelper.drawTexture(x, y, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(x, y, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveDown()) {
                        if (i < 32 && i > 16 && j > 16) {
                            DrawableHelper.drawTexture(x, y, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(x, y, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            if ((i = this.client.textRenderer.getStringWidth(string)) > 157) {
                string = this.client.textRenderer.trimToWidth(string, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
            }
            this.client.textRenderer.drawWithShadow(string, x + 32 + 2, y + 1, 0xFFFFFF);
            List<String> list = this.client.textRenderer.wrapStringToWidthAsList(string2, 157);
            for (int k = 0; k < 2 && k < list.size(); ++k) {
                this.client.textRenderer.drawWithShadow(list.get(k), x + 32 + 2, y + 12 + 10 * k, 0x808080);
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
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double d = mouseX - (double)this.resourcePackList.getRowLeft();
            double e = mouseY - (double)this.resourcePackList.getRowTop(this.resourcePackList.children().indexOf(this));
            if (this.isMoveable() && d <= 32.0) {
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
                if (d < 16.0 && this.isRemovable()) {
                    this.getScreen().disable(this);
                    return true;
                }
                if (d > 16.0 && e < 16.0 && this.canMoveUp()) {
                    List<ResourcePackEntry> list = this.resourcePackList.children();
                    int i = list.indexOf(this);
                    list.remove(i);
                    list.add(i - 1, this);
                    this.getScreen().markDirty();
                    return true;
                }
                if (d > 16.0 && e > 16.0 && this.canMoveDown()) {
                    List<ResourcePackEntry> list = this.resourcePackList.children();
                    int i = list.indexOf(this);
                    list.remove(i);
                    list.add(i + 1, this);
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

