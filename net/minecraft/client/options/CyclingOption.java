/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;

@Environment(value=EnvType.CLIENT)
public class CyclingOption
extends Option {
    private final BiConsumer<GameOptions, Integer> setter;
    private final BiFunction<GameOptions, CyclingOption, String> messageProvider;

    public CyclingOption(String string, BiConsumer<GameOptions, Integer> biConsumer, BiFunction<GameOptions, CyclingOption, String> biFunction) {
        super(string);
        this.setter = biConsumer;
        this.messageProvider = biFunction;
    }

    public void cycle(GameOptions gameOptions, int i) {
        this.setter.accept(gameOptions, i);
        gameOptions.write();
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions gameOptions, int i, int j, int k) {
        return new OptionButtonWidget(i, j, k, 20, this, this.getMessage(gameOptions), buttonWidget -> {
            this.cycle(gameOptions, 1);
            buttonWidget.setMessage(this.getMessage(gameOptions));
        });
    }

    public String getMessage(GameOptions gameOptions) {
        return this.messageProvider.apply(gameOptions, this);
    }
}

