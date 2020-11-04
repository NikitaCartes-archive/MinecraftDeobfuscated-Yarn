/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import org.jetbrains.annotations.Nullable;

/**
 * A GUI interface which handles keyboard and mouse callbacks for child GUI elements.
 * The implementation of a parent element can decide whether a child element receives keyboard and mouse callbacks.
 */
@Environment(value=EnvType.CLIENT)
public interface ParentElement
extends Element {
    /**
     * Gets a list of all child GUI elements.
     */
    public List<? extends Element> children();

    default public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        for (Element element : this.children()) {
            if (!element.isMouseOver(mouseX, mouseY)) continue;
            return Optional.of(element);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element element : this.children()) {
            if (!element.mouseClicked(mouseX, mouseY, button)) continue;
            this.setFocused(element);
            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    default public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseReleased(mouseX, mouseY, button)).isPresent();
    }

    @Override
    default public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.getFocused() != null && this.isDragging() && button == 0) {
            return this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, amount)).isPresent();
    }

    @Override
    default public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    default public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    default public boolean charTyped(char chr, int keyCode) {
        return this.getFocused() != null && this.getFocused().charTyped(chr, keyCode);
    }

    @Nullable
    public Element getFocused();

    public void setFocused(@Nullable Element var1);

    default public void setInitialFocus(@Nullable Element element) {
        this.setFocused(element);
        element.changeFocus(true);
    }

    default public void focusOn(@Nullable Element element) {
        this.setFocused(element);
    }

    @Override
    default public boolean changeFocus(boolean lookForwards) {
        Supplier<Element> supplier;
        BooleanSupplier booleanSupplier;
        boolean bl;
        Element element = this.getFocused();
        boolean bl2 = bl = element != null;
        if (bl && element.changeFocus(lookForwards)) {
            return true;
        }
        List<? extends Element> list = this.children();
        int i = list.indexOf(element);
        int j = bl && i >= 0 ? i + (lookForwards ? 1 : 0) : (lookForwards ? 0 : list.size());
        ListIterator<? extends Element> listIterator = list.listIterator(j);
        BooleanSupplier booleanSupplier2 = lookForwards ? listIterator::hasNext : (booleanSupplier = listIterator::hasPrevious);
        Supplier<Element> supplier2 = lookForwards ? listIterator::next : (supplier = listIterator::previous);
        while (booleanSupplier.getAsBoolean()) {
            Element element2 = supplier.get();
            if (!element2.changeFocus(lookForwards)) continue;
            this.setFocused(element2);
            return true;
        }
        this.setFocused(null);
        return false;
    }
}

