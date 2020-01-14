/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class class_4749
extends Screen {
    private final Screen field_21842;
    private final Text field_21843 = new TranslatableText("multiplayerWarning.header", new Object[0]).formatted(Formatting.BOLD);
    private final Text field_21844 = new TranslatableText("multiplayerWarning.message", new Object[0]);
    private final Text field_21845 = new TranslatableText("multiplayerWarning.check", new Object[0]);
    private final Text field_21846 = new TranslatableText("gui.proceed", new Object[0]);
    private final Text field_21847 = new TranslatableText("gui.back", new Object[0]);
    private CheckboxWidget field_21848;
    private final List<String> field_21849 = Lists.newArrayList();

    public class_4749(Screen screen) {
        super(NarratorManager.EMPTY);
        this.field_21842 = screen;
    }

    @Override
    protected void init() {
        super.init();
        this.field_21849.clear();
        this.field_21849.addAll(this.font.wrapStringToWidthAsList(this.field_21844.asFormattedString(), this.width - 50));
        int i = (this.field_21849.size() + 1) * this.font.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, this.field_21846.asFormattedString(), buttonWidget -> {
            if (this.field_21848.isChecked()) {
                this.minecraft.options.field_21840 = true;
                this.minecraft.options.write();
            }
            this.minecraft.openScreen(new MultiplayerScreen(this.field_21842));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.field_21847.asFormattedString(), buttonWidget -> this.minecraft.openScreen(this.field_21842)));
        this.field_21848 = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.field_21845.asFormattedString(), false);
        this.addButton(this.field_21848);
    }

    @Override
    public String getNarrationMessage() {
        return this.field_21843.getString() + "\n" + this.field_21844.getString();
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, this.field_21843.asFormattedString(), this.width / 2, 30, 0xFFFFFF);
        int k = 70;
        for (String string : this.field_21849) {
            this.drawCenteredString(this.font, string, this.width / 2, k, 0xFFFFFF);
            k += this.font.fontHeight;
        }
        super.render(i, j, f);
    }
}

