package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JigsawBlockScreen extends Screen {
	private final JigsawBlockEntity jigsaw;
	private TextFieldWidget attachmentTypeField;
	private TextFieldWidget targetPoolField;
	private TextFieldWidget finalStateField;
	private ButtonWidget field_19103;

	public JigsawBlockScreen(JigsawBlockEntity jigsaw) {
		super(NarratorManager.EMPTY);
		this.jigsaw = jigsaw;
	}

	@Override
	public void tick() {
		this.attachmentTypeField.tick();
		this.targetPoolField.tick();
		this.finalStateField.tick();
	}

	private void onDone() {
		this.updateServer();
		this.minecraft.openScreen(null);
	}

	private void onCancel() {
		this.minecraft.openScreen(null);
	}

	private void updateServer() {
		this.minecraft
			.getNetworkHandler()
			.sendPacket(
				new UpdateJigsawC2SPacket(
					this.jigsaw.getPos(), new Identifier(this.attachmentTypeField.getText()), new Identifier(this.targetPoolField.getText()), this.finalStateField.getText()
				)
			);
	}

	@Override
	public void onClose() {
		this.onCancel();
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.field_19103 = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.onDone()));
		this.addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.onCancel()));
		this.targetPoolField = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.translate("jigsaw_block.target_pool"));
		this.targetPoolField.setMaxLength(128);
		this.targetPoolField.setText(this.jigsaw.getTargetPool().toString());
		this.targetPoolField.setChangedListener(string -> this.method_20118());
		this.children.add(this.targetPoolField);
		this.attachmentTypeField = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 300, 20, I18n.translate("jigsaw_block.attachement_type"));
		this.attachmentTypeField.setMaxLength(128);
		this.attachmentTypeField.setText(this.jigsaw.getAttachmentType().toString());
		this.attachmentTypeField.setChangedListener(string -> this.method_20118());
		this.children.add(this.attachmentTypeField);
		this.finalStateField = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 300, 20, I18n.translate("jigsaw_block.final_state"));
		this.finalStateField.setMaxLength(256);
		this.finalStateField.setText(this.jigsaw.getFinalState());
		this.children.add(this.finalStateField);
		this.setInitialFocus(this.targetPoolField);
		this.method_20118();
	}

	protected void method_20118() {
		this.field_19103.active = Identifier.isValid(this.attachmentTypeField.getText()) & Identifier.isValid(this.targetPoolField.getText());
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.attachmentTypeField.getText();
		String string2 = this.targetPoolField.getText();
		String string3 = this.finalStateField.getText();
		this.init(client, width, height);
		this.attachmentTypeField.setText(string);
		this.targetPoolField.setText(string2);
		this.finalStateField.setText(string3);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (!this.field_19103.active || keyCode != 257 && keyCode != 335) {
			return false;
		} else {
			this.onDone();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawString(this.font, I18n.translate("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
		this.targetPoolField.render(mouseX, mouseY, delta);
		this.drawString(this.font, I18n.translate("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
		this.attachmentTypeField.render(mouseX, mouseY, delta);
		this.drawString(this.font, I18n.translate("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
		this.finalStateField.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
}
