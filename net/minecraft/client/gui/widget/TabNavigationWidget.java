/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Divider;

@Environment(value=EnvType.CLIENT)
public class TabNavigationWidget
extends GridWidget {
    private int tabNavWidth;
    private final TabManager tabManager;
    private final ImmutableList<Tab> tabs;
    private final ImmutableMap<Tab, ButtonWidget> tabButtons;

    public void setWidth(int width) {
        this.tabNavWidth = width;
    }

    public static Builder builder(TabManager tabManager, int width) {
        return new Builder(tabManager, width);
    }

    TabNavigationWidget(int x, int y, int width, TabManager tabManager, Iterable<Tab> tabs) {
        super(x, y);
        this.tabNavWidth = width;
        this.tabManager = tabManager;
        this.tabs = ImmutableList.copyOf(tabs);
        ImmutableMap.Builder<Tab, ButtonWidget> builder = ImmutableMap.builder();
        int i = 0;
        for (Tab tab : tabs) {
            ButtonWidget buttonWidget = ButtonWidget.builder(tab.getTitle(), button -> this.selectTab(Optional.of(button), tab)).narrationSupplier(supplier -> Text.translatable("gui.narrate.tab", tab.getTitle())).build();
            builder.put(tab, this.add(buttonWidget, 0, i++));
        }
        this.tabButtons = builder.build();
        this.refreshPositions();
    }

    @Override
    public void refreshPositions() {
        Divider divider = new Divider(this.tabNavWidth, this.tabs.size());
        for (ButtonWidget buttonWidget : this.tabButtons.values()) {
            buttonWidget.setWidth(divider.nextInt());
        }
        super.refreshPositions();
    }

    private void selectTab(Optional<ButtonWidget> button2, Tab tab) {
        this.tabButtons.values().forEach(button -> {
            button.active = true;
        });
        button2.ifPresent(buttonWidget -> {
            buttonWidget.active = false;
        });
        this.tabManager.setCurrentTab(tab);
    }

    public void selectTab(Tab tab) {
        this.selectTab(Optional.ofNullable(this.tabButtons.get(tab)), tab);
    }

    public void selectTab(int index) {
        this.selectTab((Tab)this.tabs.get(index));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private int x = 0;
        private int y = 0;
        private int width;
        private final TabManager tabManager;
        private final List<Tab> tabs = new ArrayList<Tab>();

        Builder(TabManager tabManager, int width) {
            this.tabManager = tabManager;
            this.width = width;
        }

        public Builder tab(Tab tab) {
            this.tabs.add(tab);
            return this;
        }

        public Builder tabs(Tab ... tabs) {
            Collections.addAll(this.tabs, tabs);
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder position(int x, int y) {
            return this.x(x).y(y);
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public TabNavigationWidget build() {
            return new TabNavigationWidget(this.x, this.y, this.width, this.tabManager, this.tabs);
        }
    }
}

