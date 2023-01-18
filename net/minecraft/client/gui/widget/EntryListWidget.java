/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntryListWidget<E extends Entry<E>>
extends AbstractParentElement
implements Drawable,
Selectable {
    protected final MinecraftClient client;
    protected final int itemHeight;
    private final List<E> children = new Entries();
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    @Nullable
    private E selected;
    private boolean renderBackground = true;
    private boolean renderHorizontalShadows = true;
    @Nullable
    private E hoveredEntry;

    public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.itemHeight = itemHeight;
        this.left = 0;
        this.right = width;
    }

    public void setRenderSelection(boolean renderSelection) {
        this.renderSelection = renderSelection;
    }

    protected void setRenderHeader(boolean renderHeader, int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = headerHeight;
        if (!renderHeader) {
            this.headerHeight = 0;
        }
    }

    public int getRowWidth() {
        return 220;
    }

    /**
     * {@return the selected entry of this entry list, or {@code null} if there is none}
     */
    @Nullable
    public E getSelectedOrNull() {
        return this.selected;
    }

    public void setSelected(@Nullable E entry) {
        this.selected = entry;
    }

    public E getFirst() {
        return (E)((Entry)this.children.get(0));
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void setRenderHorizontalShadows(boolean renderHorizontalShadows) {
        this.renderHorizontalShadows = renderHorizontalShadows;
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
        this.selected = null;
    }

    protected void replaceEntries(Collection<E> newEntries) {
        this.clearEntries();
        this.children.addAll(newEntries);
    }

    protected E getEntry(int index) {
        return (E)((Entry)this.children().get(index));
    }

    protected int addEntry(E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected void addEntryToTop(E entry) {
        double d = (double)this.getMaxScroll() - this.getScrollAmount();
        this.children.add(0, entry);
        this.setScrollAmount((double)this.getMaxScroll() - d);
    }

    protected boolean removeEntryWithoutScrolling(E entry) {
        double d = (double)this.getMaxScroll() - this.getScrollAmount();
        boolean bl = this.removeEntry(entry);
        this.setScrollAmount((double)this.getMaxScroll() - d);
        return bl;
    }

    protected int getEntryCount() {
        return this.children().size();
    }

    protected boolean isSelectedEntry(int index) {
        return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.left + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        if (x < (double)this.getScrollbarPositionX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount()) {
            return (E)((Entry)this.children().get(n));
        }
        return null;
    }

    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
    }

    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
    }

    protected int getMaxPosition() {
        return this.getEntryCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int x, int y) {
    }

    protected void renderHeader(MatrixStack matrices, int x, int y) {
    }

    protected void renderBackground(MatrixStack matrices) {
    }

    protected void renderDecorations(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int o;
        int n;
        int m;
        int k;
        this.renderBackground(matrices);
        int i = this.getScrollbarPositionX();
        int j = i + 6;
        this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        Object v0 = this.hoveredEntry;
        if (this.renderBackground) {
            RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor(0.125f, 0.125f, 0.125f, 1.0f);
            k = 32;
            EntryListWidget.drawTexture(matrices, this.left, this.top, this.right, this.bottom + (int)this.getScrollAmount(), this.right - this.left, this.bottom - this.top, 32, 32);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        k = this.getRowLeft();
        int l = this.top + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(matrices, k, l);
        }
        this.renderList(matrices, mouseX, mouseY, delta);
        if (this.renderHorizontalShadows) {
            RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);
            m = 32;
            n = -100;
            RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
            EntryListWidget.drawTexture(matrices, this.left, 0, -100, 0.0f, 0.0f, this.width, this.top, 32, 32);
            EntryListWidget.drawTexture(matrices, this.left, this.bottom, -100, 0.0f, this.bottom, this.width, this.height - this.bottom, 32, 32);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            o = 4;
            this.fillGradient(matrices, this.left, this.top, this.right, this.top + 4, -16777216, 0);
            this.fillGradient(matrices, this.left, this.bottom - 4, this.right, this.bottom, 0, -16777216);
        }
        if ((m = this.getMaxScroll()) > 0) {
            n = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            n = MathHelper.clamp(n, 32, this.bottom - this.top - 8);
            o = (int)this.getScrollAmount() * (this.bottom - this.top - n) / m + this.top;
            if (o < this.top) {
                o = this.top;
            }
            EntryListWidget.fill(matrices, i, this.top, j, this.bottom, -16777216);
            EntryListWidget.fill(matrices, i, o, j, o + n, -8355712);
            EntryListWidget.fill(matrices, i, o, j - 1, o + n - 1, -4144960);
        }
        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E entry) {
        this.setScrollAmount(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2);
    }

    protected void ensureVisible(E entry) {
        int k;
        int i = this.getRowTop(this.children().indexOf(entry));
        int j = i - this.top - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }
        if ((k = this.bottom - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + (double)amount);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    public int method_35721() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
    }

    protected int getScrollbarPositionX() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        E entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry != null) {
            if (entry.mouseClicked(mouseX, mouseY, button)) {
                Element entry2 = this.getFocused();
                if (entry2 != entry && entry2 instanceof ParentElement) {
                    ParentElement parentElement = (ParentElement)entry2;
                    parentElement.setFocused(null);
                }
                this.setFocused((Element)entry);
                this.setDragging(true);
                return true;
            }
        } else if (button == 0) {
            this.clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (button != 0 || !this.scrolling) {
            return false;
        }
        if (mouseY < (double)this.top) {
            this.setScrollAmount(0.0);
        } else if (mouseY > (double)this.bottom) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double d = Math.max(1, this.getMaxScroll());
            int i = this.bottom - this.top;
            int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double e = Math.max(1.0, d / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + deltaY * e);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScrollAmount(this.getScrollAmount() - amount * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
        int i = this.children.indexOf(focused);
        if (i >= 0) {
            Entry entry = (Entry)this.children.get(i);
            this.setSelected(entry);
            if (this.client.getNavigationType().isKeyboard()) {
                this.ensureVisible(entry);
            }
        }
    }

    @Nullable
    protected E getNeighboringEntry(NavigationDirection direction) {
        return (E)this.getNeighboringEntry(direction, entry -> true);
    }

    @Nullable
    protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate) {
        return this.getNeighboringEntry(direction, predicate, this.getSelectedOrNull());
    }

    @Nullable
    protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate, @Nullable E selected) {
        int i;
        switch (direction) {
            default: {
                throw new IncompatibleClassChangeError();
            }
            case RIGHT: 
            case LEFT: {
                int n = 0;
                break;
            }
            case UP: {
                int n = -1;
                break;
            }
            case DOWN: {
                int n = i = 1;
            }
        }
        if (!this.children().isEmpty() && i != 0) {
            int j = selected == null ? (i > 0 ? 0 : this.children().size() - 1) : this.children().indexOf(selected) + i;
            for (int k = j; k >= 0 && k < this.children.size(); k += i) {
                Entry entry = (Entry)this.children().get(k);
                if (!predicate.test(entry)) continue;
                return (E)entry;
            }
        }
        return null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
    }

    protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight - 4;
        int l = this.getEntryCount();
        for (int m = 0; m < l; ++m) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);
            if (o < this.top || n > this.bottom) continue;
            this.renderEntry(matrices, mouseX, mouseY, delta, m, i, n, j, k);
        }
    }

    protected void renderEntry(MatrixStack matrices, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        E entry = this.getEntry(index);
        if (this.renderSelection && this.isSelectedEntry(index)) {
            int i = this.isFocused() ? -1 : -8355712;
            this.drawSelectionHighlight(matrices, y, entryWidth, entryHeight, i, -16777216);
        }
        ((Entry)entry).render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
    }

    protected void drawSelectionHighlight(MatrixStack matrices, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.left + (this.width - entryWidth) / 2;
        int j = this.left + (this.width + entryWidth) / 2;
        EntryListWidget.fill(matrices, i, y - 2, j, y + entryHeight + 2, borderColor);
        EntryListWidget.fill(matrices, i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }

    public int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    protected int getRowTop(int index) {
        return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    protected int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        if (this.hoveredEntry != null) {
            return Selectable.SelectionType.HOVERED;
        }
        return Selectable.SelectionType.NONE;
    }

    @Nullable
    protected E remove(int index) {
        Entry entry = (Entry)this.children.get(index);
        if (this.removeEntry((Entry)this.children.get(index))) {
            return (E)entry;
        }
        return null;
    }

    protected boolean removeEntry(E entry) {
        boolean bl = this.children.remove(entry);
        if (bl && entry == this.getSelectedOrNull()) {
            this.setSelected(null);
        }
        return bl;
    }

    @Nullable
    protected E getHoveredEntry() {
        return this.hoveredEntry;
    }

    void setEntryParentList(Entry<E> entry) {
        entry.parentList = this;
    }

    protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
        int i;
        List<E> list = this.children();
        if (list.size() > 1 && (i = list.indexOf(entry)) != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.list", i + 1, list.size()));
        }
    }

    @Override
    public FocusedRect getNavigationFocus() {
        return new FocusedRect(this.left, this.top, this.right - this.left, this.bottom - this.top);
    }

    @Override
    @Nullable
    public /* synthetic */ Element getFocused() {
        return this.getFocused();
    }

    @Environment(value=EnvType.CLIENT)
    class Entries
    extends AbstractList<E> {
        private final List<E> entries = Lists.newArrayList();

        Entries() {
        }

        @Override
        public E get(int i) {
            return (Entry)this.entries.get(i);
        }

        @Override
        public int size() {
            return this.entries.size();
        }

        @Override
        public E set(int i, E entry) {
            Entry entry2 = (Entry)this.entries.set(i, entry);
            EntryListWidget.this.setEntryParentList(entry);
            return entry2;
        }

        @Override
        public void add(int i, E entry) {
            this.entries.add(i, entry);
            EntryListWidget.this.setEntryParentList(entry);
        }

        @Override
        public E remove(int i) {
            return (Entry)this.entries.remove(i);
        }

        @Override
        public /* synthetic */ Object remove(int index) {
            return this.remove(index);
        }

        @Override
        public /* synthetic */ void add(int index, Object entry) {
            this.add(index, (E)((Entry)entry));
        }

        @Override
        public /* synthetic */ Object set(int index, Object entry) {
            return this.set(index, (E)((Entry)entry));
        }

        @Override
        public /* synthetic */ Object get(int index) {
            return this.get(index);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static abstract class Entry<E extends Entry<E>>
    implements Element {
        @Deprecated
        EntryListWidget<E> parentList;

        protected Entry() {
        }

        @Override
        public void setFocused(boolean focused) {
        }

        @Override
        public boolean isFocused() {
            return this.parentList.getFocused() == this;
        }

        public abstract void render(MatrixStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
        }
    }
}

