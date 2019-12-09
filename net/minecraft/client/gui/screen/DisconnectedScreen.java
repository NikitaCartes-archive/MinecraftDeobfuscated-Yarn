/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    private final Text reason;
    private List<String> reasonFormatted;
    private final Screen parent;
    private int reasonHeight;

    public DisconnectedScreen(Screen parent, String title, Text reason) {
        super(new TranslatableText(title, new Object[0]));
        this.parent = parent;
        this.reason = reason;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.reasonFormatted = this.font.wrapStringToWidthAsList(this.reason.asFormattedString(), this.width - 50);
        this.reasonHeight = this.reasonFormatted.size() * this.font.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + this.font.fontHeight, this.height - 30), 200, 20, I18n.translate("gui.toMenu", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, this.height / 2 - this.reasonHeight / 2 - this.font.fontHeight * 2, 0xAAAAAA);
        int i = this.height / 2 - this.reasonHeight / 2;
        if (this.reasonFormatted != null) {
            for (String string : this.reasonFormatted) {
                this.drawCenteredString(this.font, string, this.width / 2, i, 0xFFFFFF);
                i += this.font.fontHeight;
            }
        }
        super.render(mouseX, mouseY, delta);
    }
}

