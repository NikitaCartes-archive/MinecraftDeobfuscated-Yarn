/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;

@Environment(value=EnvType.CLIENT)
public class SoundSliderWidget
extends SliderWidget {
    private final SoundCategory category;

    public SoundSliderWidget(MinecraftClient client, int x, int y, SoundCategory category, int width) {
        super(client.options, x, y, width, 20, client.options.getSoundVolume(category));
        this.category = category;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        String string = (float)this.value == (float)this.getYImage(false) ? I18n.translate("options.off", new Object[0]) : (int)((float)this.value * 100.0f) + "%";
        this.setMessage(I18n.translate("soundCategory." + this.category.getName(), new Object[0]) + ": " + string);
    }

    @Override
    protected void applyValue() {
        this.options.setSoundVolume(this.category, (float)this.value);
        this.options.write();
    }
}

