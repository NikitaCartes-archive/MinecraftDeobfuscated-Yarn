/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
    private static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    private static final Text OTHER_PLAYERS_TEXT = Text.translatable("lanServer.otherPlayers");
    private static final Text PORT_TEXT = Text.translatable("lanServer.port");
    private static final Text UNAVAILABLE_PORT_TEXT = Text.translatable("lanServer.port.unavailable.new", 1024, 65535);
    private static final Text INVALID_PORT_TEXT = Text.translatable("lanServer.port.invalid.new", 1024, 65535);
    private static final int ERROR_TEXT_COLOR = 0xFF5555;
    private final Screen parent;
    private GameMode gameMode = GameMode.SURVIVAL;
    private boolean allowCommands;
    private int port = NetworkUtils.findLocalPort();
    @Nullable
    private TextFieldWidget portField;

    public OpenToLanScreen(Screen screen) {
        super(Text.translatable("lanServer.title"));
        this.parent = screen;
    }

    @Override
    protected void init() {
        IntegratedServer integratedServer = this.client.getServer();
        this.gameMode = integratedServer.getDefaultGameMode();
        this.allowCommands = integratedServer.getSaveProperties().areCommandsAllowed();
        this.addDrawableChild(CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName).values((GameMode[])new GameMode[]{GameMode.SURVIVAL, GameMode.SPECTATOR, GameMode.CREATIVE, GameMode.ADVENTURE}).initially(this.gameMode).build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button, gameMode) -> {
            this.gameMode = gameMode;
        }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.allowCommands).build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button, allowCommands) -> {
            this.allowCommands = allowCommands;
        }));
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("lanServer.start"), button -> {
            this.client.setScreen(null);
            MutableText text = integratedServer.openToLan(this.gameMode, this.allowCommands, this.port) ? PublishCommand.getStartedText(this.port) : Text.translatable("commands.publish.failed");
            this.client.inGameHud.getChatHud().addMessage(text);
            this.client.updateWindowTitle();
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build();
        this.portField = new TextFieldWidget(this.textRenderer, this.width / 2 - 75, 160, 150, 20, Text.translatable("lanServer.port"));
        this.portField.setChangedListener(portText -> {
            Text text = this.updatePort((String)portText);
            this.portField.setPlaceholder(Text.literal("" + this.port).formatted(Formatting.DARK_GRAY));
            if (text == null) {
                this.portField.setEditableColor(0xE0E0E0);
                this.portField.setTooltip(null);
                buttonWidget.active = true;
            } else {
                this.portField.setEditableColor(0xFF5555);
                this.portField.setTooltip(Tooltip.of(text));
                buttonWidget.active = false;
            }
        });
        this.portField.setPlaceholder(Text.literal("" + this.port).formatted(Formatting.DARK_GRAY));
        this.addDrawableChild(this.portField);
        this.addDrawableChild(buttonWidget);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.portField != null) {
            this.portField.tick();
        }
    }

    @Nullable
    private Text updatePort(String portText) {
        if (portText.isBlank()) {
            this.port = NetworkUtils.findLocalPort();
            return null;
        }
        try {
            this.port = Integer.parseInt(portText);
            if (this.port < 1024 || this.port > 65535) {
                return INVALID_PORT_TEXT;
            }
            if (!NetworkUtils.isPortAvailable(this.port)) {
                return UNAVAILABLE_PORT_TEXT;
            }
            return null;
        } catch (NumberFormatException numberFormatException) {
            this.port = NetworkUtils.findLocalPort();
            return INVALID_PORT_TEXT;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        OpenToLanScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 50, 0xFFFFFF);
        OpenToLanScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, OTHER_PLAYERS_TEXT, this.width / 2, 82, 0xFFFFFF);
        OpenToLanScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, PORT_TEXT, this.width / 2, 142, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

