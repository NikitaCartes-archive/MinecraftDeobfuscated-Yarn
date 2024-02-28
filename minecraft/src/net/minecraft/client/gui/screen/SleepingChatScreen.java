package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SleepingChatScreen extends ChatScreen {
	private ButtonWidget stopSleepingButton;

	public SleepingChatScreen() {
		super("");
	}

	@Override
	protected void init() {
		super.init();
		this.stopSleepingButton = ButtonWidget.builder(Text.translatable("multiplayer.stopSleeping"), button -> this.stopSleeping())
			.dimensions(this.width / 2 - 100, this.height - 40, 200, 20)
			.build();
		this.addDrawableChild(this.stopSleepingButton);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
			this.stopSleepingButton.render(context, mouseX, mouseY, delta);
		} else {
			super.render(context, mouseX, mouseY, delta);
		}
	}

	@Override
	public void close() {
		this.stopSleeping();
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return !this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer()) ? true : super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.stopSleeping();
		}

		if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
			return true;
		} else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.sendMessage(this.chatField.getText(), true);
			this.chatField.setText("");
			this.client.inGameHud.getChatHud().resetScroll();
			return true;
		}
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
