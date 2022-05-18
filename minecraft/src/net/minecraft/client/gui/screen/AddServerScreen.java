package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AddServerScreen extends Screen {
	private static final Text ENTER_NAME_TEXT = Text.translatable("addServer.enterName");
	private static final Text ENTER_IP_TEXT = Text.translatable("addServer.enterIp");
	private ButtonWidget addButton;
	private final BooleanConsumer callback;
	private final ServerInfo server;
	private TextFieldWidget addressField;
	private TextFieldWidget serverNameField;
	private final Screen parent;

	public AddServerScreen(Screen parent, BooleanConsumer callback, ServerInfo server) {
		super(Text.translatable("addServer.title"));
		this.parent = parent;
		this.callback = callback;
		this.server = server;
	}

	@Override
	public void tick() {
		this.serverNameField.tick();
		this.addressField.tick();
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.serverNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 66, 200, 20, Text.translatable("addServer.enterName"));
		this.serverNameField.setTextFieldFocused(true);
		this.serverNameField.setText(this.server.name);
		this.serverNameField.setChangedListener(serverName -> this.updateAddButton());
		this.addSelectableChild(this.serverNameField);
		this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 106, 200, 20, Text.translatable("addServer.enterIp"));
		this.addressField.setMaxLength(128);
		this.addressField.setText(this.server.address);
		this.addressField.setChangedListener(address -> this.updateAddButton());
		this.addSelectableChild(this.addressField);
		this.addDrawableChild(
			CyclingButtonWidget.<ServerInfo.ResourcePackPolicy>builder(ServerInfo.ResourcePackPolicy::getName)
				.values(ServerInfo.ResourcePackPolicy.values())
				.initially(this.server.getResourcePackPolicy())
				.build(
					this.width / 2 - 100,
					this.height / 4 + 72,
					200,
					20,
					Text.translatable("addServer.resourcePack"),
					(button, resourcePackPolicy) -> this.server.setResourcePackPolicy(resourcePackPolicy)
				)
		);
		this.addButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, Text.translatable("addServer.add"), button -> this.addAndClose())
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, ScreenTexts.CANCEL, button -> this.callback.accept(false)));
		this.updateAddButton();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.addressField.getText();
		String string2 = this.serverNameField.getText();
		this.init(client, width, height);
		this.addressField.setText(string);
		this.serverNameField.setText(string2);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	private void addAndClose() {
		this.server.name = this.serverNameField.getText();
		this.server.address = this.addressField.getText();
		this.callback.accept(true);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	private void updateAddButton() {
		this.addButton.active = ServerAddress.isValid(this.addressField.getText()) && !this.serverNameField.getText().isEmpty();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, ENTER_NAME_TEXT, this.width / 2 - 100, 53, 10526880);
		drawTextWithShadow(matrices, this.textRenderer, ENTER_IP_TEXT, this.width / 2 - 100, 94, 10526880);
		this.serverNameField.render(matrices, mouseX, mouseY, delta);
		this.addressField.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
