/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private static final Text field_25889 = new TranslatableText("selectWorld.allowCommands");
    private static final Text field_25890 = new TranslatableText("selectWorld.gameMode");
    private static final Text field_26545 = new TranslatableText("lanServer.otherPlayers");
    private final Screen parent;
    private ButtonWidget buttonAllowCommands;
    private ButtonWidget buttonGameMode;
    private String gameMode = "survival";
    private boolean allowCommands;

    public OpenToLanScreen(Screen parent) {
        super(new TranslatableText("lanServer.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), buttonWidget -> {
            this.client.openScreen(null);
            int i = NetworkUtils.findLocalPort();
            TranslatableText text = this.client.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i) ? new TranslatableText("commands.publish.started", i) : new TranslatableText("commands.publish.failed");
            this.client.inGameHud.getChatHud().addMessage(text);
            this.client.updateWindowTitle();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
        this.buttonGameMode = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, LiteralText.EMPTY, buttonWidget -> {
            this.gameMode = "spectator".equals(this.gameMode) ? "creative" : ("creative".equals(this.gameMode) ? "adventure" : ("adventure".equals(this.gameMode) ? "survival" : "spectator"));
            this.updateButtonText();
        }));
        this.buttonAllowCommands = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, field_25889, buttonWidget -> {
            this.allowCommands = !this.allowCommands;
            this.updateButtonText();
        }));
        this.updateButtonText();
    }

    private void updateButtonText() {
        this.buttonGameMode.setMessage(new TranslatableText("options.generic_value", field_25890, new TranslatableText("selectWorld.gameMode." + this.gameMode)));
        this.buttonAllowCommands.setMessage(ScreenTexts.method_30619(field_25889, this.allowCommands));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        OpenToLanScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 50, 0xFFFFFF);
        OpenToLanScreen.drawCenteredText(matrices, this.textRenderer, field_26545, this.width / 2, 82, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

