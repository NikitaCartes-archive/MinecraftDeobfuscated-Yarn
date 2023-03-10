/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class SleepingChatScreen
extends ChatScreen {
    private ButtonWidget stopSleepingButton;

    public SleepingChatScreen() {
        super("");
    }

    @Override
    protected void init() {
        super.init();
        this.stopSleepingButton = ButtonWidget.builder(Text.translatable("multiplayer.stopSleeping"), button -> this.stopSleeping()).dimensions(this.width / 2 - 100, this.height - 40, 200, 20).build();
        this.addDrawableChild(this.stopSleepingButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
            this.stopSleepingButton.render(matrices, mouseX, mouseY, delta);
            return;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.stopSleeping();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.stopSleeping();
        }
        if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (this.sendMessage(this.chatField.getText(), true)) {
                this.client.setScreen(null);
                this.chatField.setText("");
                this.client.inGameHud.getChatHud().resetScroll();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void stopSleeping() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
    }

    public void closeChatIfEmpty() {
        if (this.chatField.getText().isEmpty()) {
            this.client.setScreen(null);
        } else {
            this.client.setScreen(new ChatScreen(this.chatField.getText()));
        }
    }
}

