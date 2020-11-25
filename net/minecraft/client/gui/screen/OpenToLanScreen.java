/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private static final Text ALLOW_COMMANDS_TEXT = new TranslatableText("selectWorld.allowCommands");
    private static final Text GAME_MODE_TEXT = new TranslatableText("selectWorld.gameMode");
    private static final Text OTHER_PLAYERS_TEXT = new TranslatableText("lanServer.otherPlayers");
    private final Screen parent;
    private GameMode gameMode = GameMode.SURVIVAL;
    private boolean allowCommands;

    public OpenToLanScreen(Screen parent) {
        super(new TranslatableText("lanServer.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(CyclingButtonWidget.method_32606(GameMode::getSimpleTranslatableName).method_32624((GameMode[])new GameMode[]{GameMode.SURVIVAL, GameMode.SPECTATOR, GameMode.CREATIVE, GameMode.ADVENTURE}).value(this.gameMode).build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (cyclingButtonWidget, gameMode) -> {
            this.gameMode = gameMode;
        }));
        this.addButton(CyclingButtonWidget.method_32613(this.allowCommands).build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (cyclingButtonWidget, boolean_) -> {
            this.allowCommands = boolean_;
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), buttonWidget -> {
            this.client.openScreen(null);
            int i = NetworkUtils.findLocalPort();
            TranslatableText text = this.client.getServer().openToLan(this.gameMode, this.allowCommands, i) ? new TranslatableText("commands.publish.started", i) : new TranslatableText("commands.publish.failed");
            this.client.inGameHud.getChatHud().addMessage(text);
            this.client.updateWindowTitle();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        OpenToLanScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 50, 0xFFFFFF);
        OpenToLanScreen.drawCenteredText(matrices, this.textRenderer, OTHER_PLAYERS_TEXT, this.width / 2, 82, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

