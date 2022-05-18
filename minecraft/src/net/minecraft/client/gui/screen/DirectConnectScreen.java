package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DirectConnectScreen extends Screen {
	private static final Text ENTER_IP_TEXT = Text.translatable("addServer.enterIp");
	private ButtonWidget selectServerButton;
	private final ServerInfo serverEntry;
	private TextFieldWidget addressField;
	private final BooleanConsumer callback;
	private final Screen parent;

	public DirectConnectScreen(Screen parent, BooleanConsumer callback, ServerInfo server) {
		super(Text.translatable("selectServer.direct"));
		this.parent = parent;
		this.serverEntry = server;
		this.callback = callback;
	}

	@Override
	public void tick() {
		this.addressField.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.selectServerButton.active || this.getFocused() != this.addressField || keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.saveAndClose();
			return true;
		}
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.selectServerButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, Text.translatable("selectServer.select"), button -> this.saveAndClose())
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, button -> this.callback.accept(false)));
		this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, Text.translatable("addServer.enterIp"));
		this.addressField.setMaxLength(128);
		this.addressField.setTextFieldFocused(true);
		this.addressField.setText(this.client.options.lastServer);
		this.addressField.setChangedListener(text -> this.onAddressFieldChanged());
		this.addSelectableChild(this.addressField);
		this.setInitialFocus(this.addressField);
		this.onAddressFieldChanged();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.addressField.getText();
		this.init(client, width, height);
		this.addressField.setText(string);
	}

	private void saveAndClose() {
		this.serverEntry.address = this.addressField.getText();
		this.callback.accept(true);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
		this.client.options.lastServer = this.addressField.getText();
		this.client.options.write();
	}

	private void onAddressFieldChanged() {
		this.selectServerButton.active = ServerAddress.isValid(this.addressField.getText());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, ENTER_IP_TEXT, this.width / 2 - 100, 100, 10526880);
		this.addressField.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
