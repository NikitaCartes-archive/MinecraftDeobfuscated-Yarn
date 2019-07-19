/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ButtonListWidget
extends ElementListWidget<class_354> {
    public ButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.centerListVertically = false;
    }

    public int addSingleOptionEntry(Option option) {
        return this.addEntry(class_354.create(this.minecraft.options, this.width, option));
    }

    public void addOptionEntry(Option option, @Nullable Option option2) {
        this.addEntry(class_354.create(this.minecraft.options, this.width, option, option2));
    }

    public void addAll(Option[] options) {
        for (int i = 0; i < options.length; i += 2) {
            this.addOptionEntry(options[i], i < options.length - 1 ? options[i + 1] : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_354
    extends ElementListWidget.Entry<class_354> {
        private final List<AbstractButtonWidget> buttons;

        private class_354(List<AbstractButtonWidget> list) {
            this.buttons = list;
        }

        public static class_354 create(GameOptions gameOptions, int i, Option option) {
            return new class_354(ImmutableList.of(option.createButton(gameOptions, i / 2 - 155, 0, 310)));
        }

        public static class_354 create(GameOptions gameOptions, int i, Option option, @Nullable Option option2) {
            AbstractButtonWidget abstractButtonWidget = option.createButton(gameOptions, i / 2 - 155, 0, 150);
            if (option2 == null) {
                return new class_354(ImmutableList.of(abstractButtonWidget));
            }
            return new class_354(ImmutableList.of(abstractButtonWidget, option2.createButton(gameOptions, i / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.buttons.forEach(abstractButtonWidget -> {
                abstractButtonWidget.y = j;
                abstractButtonWidget.render(n, o, f);
            });
        }

        @Override
        public List<? extends Element> children() {
            return this.buttons;
        }
    }
}

