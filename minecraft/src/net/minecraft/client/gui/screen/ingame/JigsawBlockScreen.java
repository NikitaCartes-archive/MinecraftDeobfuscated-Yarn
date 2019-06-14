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
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JigsawBlockScreen extends Screen {
	private final JigsawBlockEntity jigsaw;
	private TextFieldWidget attachmentTypeField;
	private TextFieldWidget targetPoolField;
	private TextFieldWidget finalStateField;
	private ButtonWidget field_19103;

	public JigsawBlockScreen(JigsawBlockEntity jigsawBlockEntity) {
		super(NarratorManager.EMPTY);
		this.jigsaw = jigsawBlockEntity;
	}

	@Override
	public void tick() {
		this.attachmentTypeField.tick();
		this.targetPoolField.tick();
		this.finalStateField.tick();
	}

	private void onDone() {
		this.updateServer();
		this.minecraft.method_1507(null);
	}

	private void onCancel() {
		this.minecraft.method_1507(null);
	}

	private void updateServer() {
		this.minecraft
			.method_1562()
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
		this.method_20085(this.targetPoolField);
		this.method_20118();
	}

	protected void method_20118() {
		this.field_19103.active = Identifier.isValid(this.attachmentTypeField.getText()) & Identifier.isValid(this.targetPoolField.getText());
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.attachmentTypeField.getText();
		String string2 = this.targetPoolField.getText();
		String string3 = this.finalStateField.getText();
		this.init(minecraftClient, i, j);
		this.attachmentTypeField.setText(string);
		this.targetPoolField.setText(string2);
		this.finalStateField.setText(string3);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.onDone();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawString(this.font, I18n.translate("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
		this.targetPoolField.render(i, j, f);
		this.drawString(this.font, I18n.translate("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
		this.attachmentTypeField.render(i, j, f);
		this.drawString(this.font, I18n.translate("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
		this.finalStateField.render(i, j, f);
		super.render(i, j, f);
	}
}
