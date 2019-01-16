package net.minecraft.client.gui.ingame;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateJigsawServerPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JigsawBlockGui extends Gui {
	private final JigsawBlockEntity jigsaw;
	private TextFieldWidget attachmentTypeField;
	private TextFieldWidget targetPoolField;
	private TextFieldWidget finalStateField;
	private final List<TextFieldWidget> textFields = Lists.<TextFieldWidget>newArrayList();

	public JigsawBlockGui(JigsawBlockEntity jigsawBlockEntity) {
		this.jigsaw = jigsawBlockEntity;
	}

	@Override
	public void update() {
		this.attachmentTypeField.tick();
		this.targetPoolField.tick();
		this.finalStateField.tick();
	}

	private void onDone() {
		this.updateServer();
		this.client.openGui(null);
	}

	private void onCancel() {
		this.client.openGui(null);
	}

	private void updateServer() {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new UpdateJigsawServerPacket(
					this.jigsaw.getPos(), new Identifier(this.attachmentTypeField.getText()), new Identifier(this.targetPoolField.getText()), this.finalStateField.getText()
				)
			);
	}

	@Override
	public void close() {
		this.onCancel();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				JigsawBlockGui.this.onDone();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				JigsawBlockGui.this.onCancel();
			}
		});
		this.textFields.clear();
		this.targetPoolField = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 40, 300, 20);
		this.targetPoolField.setMaxLength(128);
		this.targetPoolField.setText(this.jigsaw.getTargetPool().toString());
		this.textFields.add(this.targetPoolField);
		this.attachmentTypeField = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 80, 300, 20);
		this.attachmentTypeField.setMaxLength(128);
		this.attachmentTypeField.setText(this.jigsaw.getAttachmentType().toString());
		this.textFields.add(this.attachmentTypeField);
		this.finalStateField = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 120, 300, 20);
		this.finalStateField.setMaxLength(256);
		this.finalStateField.setText(this.jigsaw.method_16380());
		this.textFields.add(this.finalStateField);
		this.listeners.addAll(this.textFields);
		this.attachmentTypeField.setFocused(true);
		this.setFocused(this.attachmentTypeField);
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.attachmentTypeField.getText();
		String string2 = this.targetPoolField.getText();
		String string3 = this.finalStateField.getText();
		this.initialize(minecraftClient, i, j);
		this.attachmentTypeField.setText(string);
		this.targetPoolField.setText(string2);
		this.finalStateField.setText(string3);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			for (TextFieldWidget textFieldWidget : this.textFields) {
				textFieldWidget.setFocused(this.getFocused() == textFieldWidget);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i != 258) {
			if (i != 257 && i != 335) {
				return super.keyPressed(i, j, k);
			} else {
				this.onDone();
				return true;
			}
		} else {
			TextFieldWidget textFieldWidget = null;
			TextFieldWidget textFieldWidget2 = null;

			for (TextFieldWidget textFieldWidget3 : this.textFields) {
				if (textFieldWidget != null && textFieldWidget3.isVisible()) {
					textFieldWidget2 = textFieldWidget3;
					break;
				}

				if (textFieldWidget3.isFocused() && textFieldWidget3.isVisible()) {
					textFieldWidget = textFieldWidget3;
				}
			}

			if (textFieldWidget != null && textFieldWidget2 == null) {
				for (TextFieldWidget textFieldWidget3 : this.textFields) {
					if (textFieldWidget3.isVisible() && textFieldWidget3 != textFieldWidget) {
						textFieldWidget2 = textFieldWidget3;
						break;
					}
				}
			}

			if (textFieldWidget2 != null && textFieldWidget2 != textFieldWidget) {
				textFieldWidget.setFocused(false);
				textFieldWidget2.setFocused(true);
				this.setFocused(textFieldWidget2);
			}

			return true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
		this.targetPoolField.render(i, j, f);
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
		this.attachmentTypeField.render(i, j, f);
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
		this.finalStateField.render(i, j, f);
		super.draw(i, j, f);
	}
}
