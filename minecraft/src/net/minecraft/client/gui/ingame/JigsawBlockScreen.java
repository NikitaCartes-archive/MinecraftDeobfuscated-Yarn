package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JigsawBlockScreen extends Screen {
	private final JigsawBlockEntity jigsaw;
	private TextFieldWidget attachmentTypeField;
	private TextFieldWidget targetPoolField;
	private TextFieldWidget finalStateField;

	public JigsawBlockScreen(JigsawBlockEntity jigsawBlockEntity) {
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
		this.client.method_1507(null);
	}

	private void onCancel() {
		this.client.method_1507(null);
	}

	private void updateServer() {
		this.client
			.method_1562()
			.method_2883(
				new UpdateJigsawC2SPacket(
					this.jigsaw.method_11016(),
					new Identifier(this.attachmentTypeField.getText()),
					new Identifier(this.targetPoolField.getText()),
					this.finalStateField.getText()
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
		this.addButton(new class_4185(this.screenWidth / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				JigsawBlockScreen.this.onDone();
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				JigsawBlockScreen.this.onCancel();
			}
		});
		this.targetPoolField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 152, 40, 300, 20);
		this.targetPoolField.setMaxLength(128);
		this.targetPoolField.setText(this.jigsaw.method_16382().toString());
		this.listeners.add(this.targetPoolField);
		this.attachmentTypeField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 152, 80, 300, 20);
		this.attachmentTypeField.setMaxLength(128);
		this.attachmentTypeField.setText(this.jigsaw.method_16381().toString());
		this.listeners.add(this.attachmentTypeField);
		this.finalStateField = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 152, 120, 300, 20);
		this.finalStateField.setMaxLength(256);
		this.finalStateField.setText(this.jigsaw.getFinalState());
		this.listeners.add(this.finalStateField);
		this.method_18624(this.targetPoolField);
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
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.target_pool"), this.screenWidth / 2 - 153, 30, 10526880);
		this.targetPoolField.draw(i, j, f);
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.attachement_type"), this.screenWidth / 2 - 153, 70, 10526880);
		this.attachmentTypeField.draw(i, j, f);
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.final_state"), this.screenWidth / 2 - 153, 110, 10526880);
		this.finalStateField.draw(i, j, f);
		super.draw(i, j, f);
	}
}
