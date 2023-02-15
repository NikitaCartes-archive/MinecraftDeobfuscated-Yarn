/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.FocusedPos;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

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
    default public boolean charTyped(char chr, int modifiers) {
        return this.getFocused() != null && this.getFocused().charTyped(chr, modifiers);
    }

    @Nullable
    public Element getFocused();

    public void setFocused(@Nullable Element var1);

    @Override
    default public void setFocused(boolean focused) {
    }

    @Override
    default public boolean isFocused() {
        return this.getFocused() != null;
    }

    @Override
    @Nullable
    default public GuiNavigationPath getFocusedPath() {
        Element element = this.getFocused();
        if (element != null) {
            return GuiNavigationPath.of(this, element.getFocusedPath());
        }
        return null;
    }

    default public void focusOn(@Nullable Element element) {
        this.setFocused(element);
    }

    @Override
    @Nullable
    default public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        GuiNavigationPath guiNavigationPath;
        Element element = this.getFocused();
        if (element != null && (guiNavigationPath = element.getNavigationPath(navigation)) != null) {
            return GuiNavigationPath.of(this, guiNavigationPath);
        }
        if (navigation instanceof GuiNavigation.Tab) {
            GuiNavigation.Tab tab = (GuiNavigation.Tab)navigation;
            return this.computeNavigationPath(tab);
        }
        if (navigation instanceof GuiNavigation.Arrow) {
            GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
            return this.computeNavigationPath(arrow);
        }
        return null;
    }

    @Nullable
    private GuiNavigationPath computeNavigationPath(GuiNavigation.Tab navigation) {
        Supplier<Element> supplier;
        BooleanSupplier booleanSupplier;
        boolean bl = navigation.forward();
        Element element2 = this.getFocused();
        ArrayList<? extends Element> list = new ArrayList<Element>(this.children());
        Collections.sort(list, Comparator.comparingInt(element -> element.getNavigationOrder()));
        int i = list.indexOf(element2);
        int j = element2 != null && i >= 0 ? i + (bl ? 1 : 0) : (bl ? 0 : list.size());
        ListIterator listIterator = list.listIterator(j);
        BooleanSupplier booleanSupplier2 = bl ? listIterator::hasNext : (booleanSupplier = listIterator::hasPrevious);
        Supplier<Element> supplier2 = bl ? listIterator::next : (supplier = listIterator::previous);
        while (booleanSupplier.getAsBoolean()) {
            Element element22 = supplier.get();
            GuiNavigationPath guiNavigationPath = element22.getNavigationPath(navigation);
            if (guiNavigationPath == null) continue;
            return GuiNavigationPath.of(this, guiNavigationPath);
        }
        return null;
    }

    @Nullable
    private GuiNavigationPath computeNavigationPath(GuiNavigation.Arrow navigation) {
        Element element = this.getFocused();
        if (element == null) {
            NavigationDirection navigationDirection = navigation.direction();
            FocusedRect focusedRect = this.getNavigationFocus().getBorder(navigationDirection.getOpposite());
            return GuiNavigationPath.of(this, this.computeChildPath(focusedRect, navigationDirection, null, navigation));
        }
        FocusedRect focusedRect2 = element.getNavigationFocus();
        return GuiNavigationPath.of(this, this.computeChildPath(focusedRect2, navigation.direction(), element, navigation));
    }

    @Nullable
    private GuiNavigationPath computeChildPath(FocusedRect focus, NavigationDirection direction, @Nullable Element focused, GuiNavigation navigation) {
        NavigationAxis navigationAxis = direction.getAxis();
        NavigationAxis navigationAxis2 = navigationAxis.getOther();
        NavigationDirection navigationDirection = navigationAxis2.getPositiveDirection();
        int i = focus.getBoundingCoordinate(direction.getOpposite());
        ArrayList<Element> list = new ArrayList<Element>();
        for (Element element2 : this.children()) {
            FocusedRect focusedRect;
            if (element2 == focused || !(focusedRect = element2.getNavigationFocus()).overlaps(focus, navigationAxis2)) continue;
            int j = focusedRect.getBoundingCoordinate(direction.getOpposite());
            if (direction.isAfter(j, i)) {
                list.add(element2);
                continue;
            }
            if (j != i || !direction.isAfter(focusedRect.getBoundingCoordinate(direction), focus.getBoundingCoordinate(direction))) continue;
            list.add(element2);
        }
        Comparator<Element> comparator = Comparator.comparing(element -> element.getNavigationFocus().getBoundingCoordinate(direction.getOpposite()), direction.getComparator());
        Comparator<Element> comparator2 = Comparator.comparing(element -> element.getNavigationFocus().getBoundingCoordinate(navigationDirection.getOpposite()), navigationDirection.getComparator());
        list.sort(comparator.thenComparing(comparator2));
        for (Element element2 : list) {
            GuiNavigationPath guiNavigationPath = element2.getNavigationPath(navigation);
            if (guiNavigationPath == null) continue;
            return guiNavigationPath;
        }
        return this.computeInitialChildPath(focus, direction, focused, navigation);
    }

    @Nullable
    private GuiNavigationPath computeInitialChildPath(FocusedRect focus, NavigationDirection direction, @Nullable Element focused, GuiNavigation navigation) {
        NavigationAxis navigationAxis = direction.getAxis();
        NavigationAxis navigationAxis2 = navigationAxis.getOther();
        ArrayList<Pair> list = new ArrayList<Pair>();
        FocusedPos focusedPos = FocusedPos.of(navigationAxis, focus.getBoundingCoordinate(direction), focus.getCenter(navigationAxis2));
        for (Element element : this.children()) {
            FocusedRect focusedRect;
            FocusedPos focusedPos2;
            if (element == focused || !direction.isAfter((focusedPos2 = FocusedPos.of(navigationAxis, (focusedRect = element.getNavigationFocus()).getBoundingCoordinate(direction.getOpposite()), focusedRect.getCenter(navigationAxis2))).getComponent(navigationAxis), focusedPos.getComponent(navigationAxis))) continue;
            long l = Vector2i.distanceSquared(focusedPos.x(), focusedPos.y(), focusedPos2.x(), focusedPos2.y());
            list.add(Pair.of(element, l));
        }
        list.sort(Comparator.comparingDouble(Pair::getSecond));
        for (Pair pair : list) {
            GuiNavigationPath guiNavigationPath = ((Element)pair.getFirst()).getNavigationPath(navigation);
            if (guiNavigationPath == null) continue;
            return guiNavigationPath;
        }
        return null;
    }
}

