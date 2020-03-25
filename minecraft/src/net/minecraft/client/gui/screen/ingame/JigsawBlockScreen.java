package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.JigsawBlock;
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
	private TextFieldWidget field_23348;
	private TextFieldWidget field_23349;
	private TextFieldWidget field_23350;
	private TextFieldWidget finalStateField;
	private ButtonWidget field_23351;
	private ButtonWidget doneButton;
	private JigsawBlockEntity.class_4991 field_23352;

	public JigsawBlockScreen(JigsawBlockEntity jigsaw) {
		super(NarratorManager.EMPTY);
		this.jigsaw = jigsaw;
	}

	@Override
	public void tick() {
		this.field_23348.tick();
		this.field_23349.tick();
		this.field_23350.tick();
		this.finalStateField.tick();
	}

	private void onDone() {
		this.updateServer();
		this.client.openScreen(null);
	}

	private void onCancel() {
		this.client.openScreen(null);
	}

	private void updateServer() {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new UpdateJigsawC2SPacket(
					this.jigsaw.getPos(),
					new Identifier(this.field_23348.getText()),
					new Identifier(this.field_23349.getText()),
					new Identifier(this.field_23350.getText()),
					this.finalStateField.getText(),
					this.field_23352
				)
			);
	}

	@Override
	public void onClose() {
		this.onCancel();
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.onDone()));
		this.addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.onCancel()));
		this.field_23350 = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 20, 300, 20, I18n.translate("jigsaw_block.pool"));
		this.field_23350.setMaxLength(128);
		this.field_23350.setText(this.jigsaw.getTargetPool().toString());
		this.field_23350.setChangedListener(string -> this.updateDoneButtonState());
		this.children.add(this.field_23350);
		this.field_23348 = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 60, 300, 20, I18n.translate("jigsaw_block.name"));
		this.field_23348.setMaxLength(128);
		this.field_23348.setText(this.jigsaw.getAttachmentType().toString());
		this.field_23348.setChangedListener(string -> this.updateDoneButtonState());
		this.children.add(this.field_23348);
		this.field_23349 = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 100, 300, 20, I18n.translate("jigsaw_block.target"));
		this.field_23349.setMaxLength(128);
		this.field_23349.setText(this.jigsaw.method_26399().toString());
		this.field_23349.setChangedListener(string -> this.updateDoneButtonState());
		this.children.add(this.field_23349);
		this.finalStateField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 140, 300, 20, I18n.translate("jigsaw_block.final_state"));
		this.finalStateField.setMaxLength(256);
		this.finalStateField.setText(this.jigsaw.getFinalState());
		this.children.add(this.finalStateField);
		this.field_23352 = this.jigsaw.method_26400();
		int i = this.textRenderer.getStringWidth(I18n.translate("jigsaw_block.joint_label")) + 10;
		this.field_23351 = this.addButton(new ButtonWidget(this.width / 2 - 152 + i, 170, 300 - i, 20, this.method_26413(), buttonWidget -> {
			JigsawBlockEntity.class_4991[] lvs = JigsawBlockEntity.class_4991.values();
			int ix = (this.field_23352.ordinal() + 1) % lvs.length;
			this.field_23352 = lvs[ix];
			buttonWidget.setMessage(this.method_26413());
		}));
		boolean bl = JigsawBlock.method_26378(this.jigsaw.getCachedState()).getAxis().isVertical();
		this.field_23351.active = bl;
		this.field_23351.visible = bl;
		this.setInitialFocus(this.field_23350);
		this.updateDoneButtonState();
	}

	private void updateDoneButtonState() {
		this.doneButton.active = Identifier.isValid(this.field_23348.getText())
			&& Identifier.isValid(this.field_23349.getText())
			&& Identifier.isValid(this.field_23350.getText());
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.field_23348.getText();
		String string2 = this.field_23349.getText();
		String string3 = this.field_23350.getText();
		String string4 = this.finalStateField.getText();
		JigsawBlockEntity.class_4991 lv = this.field_23352;
		this.init(client, width, height);
		this.field_23348.setText(string);
		this.field_23349.setText(string2);
		this.field_23350.setText(string3);
		this.finalStateField.setText(string4);
		this.field_23352 = lv;
		this.field_23351.setMessage(this.method_26413());
	}

	private String method_26413() {
		return I18n.translate("jigsaw_block.joint." + this.field_23352.asString());
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (!this.doneButton.active || keyCode != 257 && keyCode != 335) {
			return false;
		} else {
			this.onDone();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawString(this.textRenderer, I18n.translate("jigsaw_block.pool"), this.width / 2 - 153, 10, 10526880);
		this.field_23350.render(mouseX, mouseY, delta);
		this.drawString(this.textRenderer, I18n.translate("jigsaw_block.name"), this.width / 2 - 153, 50, 10526880);
		this.field_23348.render(mouseX, mouseY, delta);
		this.drawString(this.textRenderer, I18n.translate("jigsaw_block.target"), this.width / 2 - 153, 90, 10526880);
		this.field_23349.render(mouseX, mouseY, delta);
		this.drawString(this.textRenderer, I18n.translate("jigsaw_block.final_state"), this.width / 2 - 153, 130, 10526880);
		this.finalStateField.render(mouseX, mouseY, delta);
		if (JigsawBlock.method_26378(this.jigsaw.getCachedState()).getAxis().isVertical()) {
			this.drawString(this.textRenderer, I18n.translate("jigsaw_block.joint_label"), this.width / 2 - 153, 176, 16777215);
		}

		super.render(mouseX, mouseY, delta);
	}
}
