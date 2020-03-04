/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.multiplayer;

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
public class MultiplayerWarningScreen
extends Screen {
    private final Screen parent;
    private final Text header = new TranslatableText("multiplayerWarning.header", new Object[0]).formatted(Formatting.BOLD);
    private final Text message = new TranslatableText("multiplayerWarning.message", new Object[0]);
    private final Text checkMessage = new TranslatableText("multiplayerWarning.check", new Object[0]);
    private final Text proceedText = new TranslatableText("gui.proceed", new Object[0]);
    private final Text backText = new TranslatableText("gui.back", new Object[0]);
    private CheckboxWidget checkbox;
    private final List<String> lines = Lists.newArrayList();

    public MultiplayerWarningScreen(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.lines.clear();
        this.lines.addAll(this.textRenderer.wrapStringToWidthAsList(this.message.asFormattedString(), this.width - 50));
        int i = (this.lines.size() + 1) * this.textRenderer.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, this.proceedText.asFormattedString(), buttonWidget -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipMultiplayerWarning = true;
                this.client.options.write();
            }
            this.client.openScreen(new MultiplayerScreen(this.parent));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.backText.asFormattedString(), buttonWidget -> this.client.openScreen(this.parent)));
        this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.checkMessage.asFormattedString(), false);
        this.addButton(this.checkbox);
    }

    @Override
    public String getNarrationMessage() {
        return this.header.getString() + "\n" + this.message.getString();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.textRenderer, this.header.asFormattedString(), this.width / 2, 30, 0xFFFFFF);
        int i = 70;
        for (String string : this.lines) {
            this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 0xFFFFFF);
            i += this.textRenderer.fontHeight;
        }
        super.render(mouseX, mouseY, delta);
    }
}

