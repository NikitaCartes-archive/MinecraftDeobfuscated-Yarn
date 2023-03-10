/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TabNavigationWidget
extends AbstractParentElement
implements Drawable,
Element,
Selectable {
    private static final int field_42489 = -1;
    private static final int field_43076 = 400;
    private static final int field_43077 = 24;
    private static final int field_43078 = 14;
    private static final Text USAGE_NARRATION_TEXT = Text.translatable("narration.tab_navigation.usage");
    private final GridWidget grid;
    private int tabNavWidth;
    private final TabManager tabManager;
    private final ImmutableList<Tab> tabs;
    private final ImmutableList<TabButtonWidget> tabButtons;

    TabNavigationWidget(int x, TabManager tabManager, Iterable<Tab> tabs) {
        this.tabNavWidth = x;
        this.tabManager = tabManager;
        this.tabs = ImmutableList.copyOf(tabs);
        this.grid = new GridWidget(0, 0);
        this.grid.getMainPositioner().alignHorizontalCenter();
        ImmutableList.Builder builder = ImmutableList.builder();
        int i = 0;
        for (Tab tab : tabs) {
            builder.add(this.grid.add(new TabButtonWidget(tabManager, tab, 0, 24), 0, i++));
        }
        this.tabButtons = builder.build();
    }

    public static Builder builder(TabManager tabManager, int width) {
        return new Builder(tabManager, width);
    }

    public void setWidth(int width) {
        this.tabNavWidth = width;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (this.getFocused() != null) {
            this.getFocused().setFocused(focused);
        }
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
        if (focused instanceof TabButtonWidget) {
            TabButtonWidget tabButtonWidget = (TabButtonWidget)focused;
            this.tabManager.setCurrentTab(tabButtonWidget.getTab(), true);
        }
    }

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        TabButtonWidget tabButtonWidget;
        if (!this.isFocused() && (tabButtonWidget = this.getCurrentTabButton()) != null) {
            return GuiNavigationPath.of(this, GuiNavigationPath.of(tabButtonWidget));
        }
        if (navigation instanceof GuiNavigation.Tab) {
            return null;
        }
        return super.getNavigationPath(navigation);
    }

    @Override
    public List<? extends Element> children() {
        return this.tabButtons;
    }

    @Override
    public Selectable.SelectionType getType() {
        return this.tabButtons.stream().map(ClickableWidget::getType).max(Comparator.naturalOrder()).orElse(Selectable.SelectionType.NONE);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        Optional<TabButtonWidget> optional = this.tabButtons.stream().filter(ClickableWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.getCurrentTabButton()));
        optional.ifPresent(button -> {
            this.appendNarrations(builder.nextMessage(), (TabButtonWidget)button);
            button.appendNarrations(builder);
        });
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, USAGE_NARRATION_TEXT);
        }
    }

    protected void appendNarrations(NarrationMessageBuilder builder, TabButtonWidget button) {
        int i;
        if (this.tabs.size() > 1 && (i = this.tabButtons.indexOf(button)) != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.tab", i + 1, this.tabs.size()));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        TabNavigationWidget.fill(matrices, 0, 0, this.tabNavWidth, 24, -16777216);
        RenderSystem.setShaderTexture(0, CreateWorldScreen.HEADER_SEPARATOR_TEXTURE);
        TabNavigationWidget.drawTexture(matrices, 0, this.grid.getY() + this.grid.getHeight() - 2, 0.0f, 0.0f, this.tabNavWidth, 2, 32, 2);
        for (TabButtonWidget tabButtonWidget : this.tabButtons) {
            tabButtonWidget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return this.grid.getNavigationFocus();
    }

    public void init() {
        int i = Math.min(400, this.tabNavWidth) - 28;
        int j = MathHelper.roundUpToMultiple(i / this.tabs.size(), 2);
        for (TabButtonWidget tabButtonWidget : this.tabButtons) {
            tabButtonWidget.setWidth(j);
        }
        this.grid.refreshPositions();
        this.grid.setX(MathHelper.roundUpToMultiple((this.tabNavWidth - i) / 2, 2));
        this.grid.setY(0);
    }

    public void selectTab(int index, boolean clickSound) {
        if (this.isFocused()) {
            this.setFocused((Element)this.tabButtons.get(index));
        } else {
            this.tabManager.setCurrentTab((Tab)this.tabs.get(index), clickSound);
        }
    }

    public boolean trySwitchTabsWithKey(int keyCode) {
        int i;
        if (Screen.hasControlDown() && (i = this.getTabForKey(keyCode)) != -1) {
            this.selectTab(MathHelper.clamp(i, 0, this.tabs.size() - 1), true);
            return true;
        }
        return false;
    }

    private int getTabForKey(int keyCode) {
        int i;
        if (keyCode >= 49 && keyCode <= 57) {
            return keyCode - 49;
        }
        if (keyCode == 258 && (i = this.getCurrentTabIndex()) != -1) {
            int j = Screen.hasShiftDown() ? i - 1 : i + 1;
            return Math.floorMod(j, this.tabs.size());
        }
        return -1;
    }

    private int getCurrentTabIndex() {
        Tab tab = this.tabManager.getCurrentTab();
        int i = this.tabs.indexOf(tab);
        return i != -1 ? i : -1;
    }

    @Nullable
    private TabButtonWidget getCurrentTabButton() {
        int i = this.getCurrentTabIndex();
        return i != -1 ? (TabButtonWidget)this.tabButtons.get(i) : null;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final int width;
        private final TabManager tabManager;
        private final List<Tab> tabs = new ArrayList<Tab>();

        Builder(TabManager tabManager, int width) {
            this.tabManager = tabManager;
            this.width = width;
        }

        public Builder tabs(Tab ... tabs) {
            Collections.addAll(this.tabs, tabs);
            return this;
        }

        public TabNavigationWidget build() {
            return new TabNavigationWidget(this.width, this.tabManager, this.tabs);
        }
    }
}

