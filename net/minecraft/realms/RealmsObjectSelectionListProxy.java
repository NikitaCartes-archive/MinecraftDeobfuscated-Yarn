/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsObjectSelectionListProxy<E extends AlwaysSelectedEntryListWidget.Entry<E>>
extends AlwaysSelectedEntryListWidget<E> {
    private final RealmsObjectSelectionList realmsObjectSelectionList;

    public RealmsObjectSelectionListProxy(RealmsObjectSelectionList realmsObjectSelectionList, int i, int j, int k, int l, int m) {
        super(MinecraftClient.getInstance(), i, j, k, l, m);
        this.realmsObjectSelectionList = realmsObjectSelectionList;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void clear() {
        super.clearEntries();
    }

    @Override
    public boolean isFocused() {
        return this.realmsObjectSelectionList.isFocused();
    }

    protected void setSelectedItem(int i) {
        if (i == -1) {
            super.setSelected(null);
        } else if (super.getItemCount() != 0) {
            AlwaysSelectedEntryListWidget.Entry entry = (AlwaysSelectedEntryListWidget.Entry)super.getEntry(i);
            super.setSelected(entry);
        }
    }

    @Override
    public void setSelected(@Nullable E entry) {
        super.setSelected(entry);
        this.realmsObjectSelectionList.selectItem(super.children().indexOf(entry));
    }

    @Override
    public void renderBackground() {
        this.realmsObjectSelectionList.renderBackground();
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public int getMaxPosition() {
        return this.realmsObjectSelectionList.getMaxPosition();
    }

    @Override
    public int getScrollbarPosition() {
        return this.realmsObjectSelectionList.getScrollbarPosition();
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (this.realmsObjectSelectionList.mouseScrolled(d, e, amount)) {
            return true;
        }
        return super.mouseScrolled(d, e, amount);
    }

    @Override
    public int getRowWidth() {
        return this.realmsObjectSelectionList.getRowWidth();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.realmsObjectSelectionList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return RealmsObjectSelectionListProxy.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.realmsObjectSelectionList.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.realmsObjectSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    protected final int addEntry(E entry) {
        return super.addEntry(entry);
    }

    @Override
    public E remove(int i) {
        return (E)((AlwaysSelectedEntryListWidget.Entry)super.remove(i));
    }

    @Override
    public boolean removeEntry(E entry) {
        return super.removeEntry(entry);
    }

    @Override
    public void setScrollAmount(double d) {
        super.setScrollAmount(d);
    }

    public int y0() {
        return this.top;
    }

    public int y1() {
        return this.bottom;
    }

    public int headerHeight() {
        return this.headerHeight;
    }

    public int itemHeight() {
        return this.itemHeight;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.realmsObjectSelectionList.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void replaceEntries(Collection<E> collection) {
        super.replaceEntries(collection);
    }

    @Override
    public int getRowTop(int i) {
        return super.getRowTop(i);
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft();
    }
}

