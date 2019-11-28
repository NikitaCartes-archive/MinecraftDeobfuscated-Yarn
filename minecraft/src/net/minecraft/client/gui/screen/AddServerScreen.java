package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.IDN;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;

@Environment(EnvType.CLIENT)
public class AddServerScreen extends Screen {
	private ButtonWidget buttonAdd;
	private final BooleanConsumer callback;
	private final ServerInfo server;
	private TextFieldWidget addressField;
	private TextFieldWidget serverNameField;
	private ButtonWidget resourcePackOptionButton;
	private final Screen field_21791;
	private final Predicate<String> addressTextFilter = string -> {
		if (ChatUtil.isEmpty(string)) {
			return true;
		} else {
			String[] strings = string.split(":");
			if (strings.length == 0) {
				return true;
			} else {
				try {
					String string2 = IDN.toASCII(strings[0]);
					return true;
				} catch (IllegalArgumentException var3) {
					return false;
				}
			}
		}
	};

	public AddServerScreen(Screen screen, BooleanConsumer booleanConsumer, ServerInfo serverInfo) {
		super(new TranslatableText("addServer.title"));
		this.field_21791 = screen;
		this.callback = booleanConsumer;
		this.server = serverInfo;
	}

	@Override
	public void tick() {
		this.serverNameField.tick();
		this.addressField.tick();
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.serverNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 66, 200, 20, I18n.translate("addServer.enterName"));
		this.serverNameField.setSelected(true);
		this.serverNameField.setText(this.server.name);
		this.serverNameField.setChangedListener(this::onClose);
		this.children.add(this.serverNameField);
		this.addressField = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, I18n.translate("addServer.enterIp"));
		this.addressField.setMaxLength(128);
		this.addressField.setText(this.server.address);
		this.addressField.setTextPredicate(this.addressTextFilter);
		this.addressField.setChangedListener(this::onClose);
		this.children.add(this.addressField);
		this.resourcePackOptionButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 72,
				200,
				20,
				I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString(),
				buttonWidget -> {
					this.server
						.setResourcePackState(ServerInfo.ResourcePackState.values()[(this.server.getResourcePack().ordinal() + 1) % ServerInfo.ResourcePackState.values().length]);
					this.resourcePackOptionButton.setMessage(I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString());
				}
			)
		);
		this.buttonAdd = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, I18n.translate("addServer.add"), buttonWidget -> this.addAndClose())
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.callback.accept(false))
		);
		this.method_24183();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.addressField.getText();
		String string2 = this.serverNameField.getText();
		this.init(client, width, height);
		this.addressField.setText(string);
		this.serverNameField.setText(string2);
	}

	private void onClose(String text) {
		this.method_24183();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	private void addAndClose() {
		this.server.name = this.serverNameField.getText();
		this.server.address = this.addressField.getText();
		this.callback.accept(true);
	}

	@Override
	public void onClose() {
		this.method_24183();
		this.minecraft.openScreen(this.field_21791);
	}

	private void method_24183() {
		String string = this.addressField.getText();
		boolean bl = !string.isEmpty() && string.split(":").length > 0 && string.indexOf(32) == -1;
		this.buttonAdd.active = bl && !this.serverNameField.getText().isEmpty();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 17, 16777215);
		this.drawString(this.font, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawString(this.font, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		this.serverNameField.render(mouseX, mouseY, delta);
		this.addressField.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
}
