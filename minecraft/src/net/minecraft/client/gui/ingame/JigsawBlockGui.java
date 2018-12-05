package net.minecraft.client.gui.ingame;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3753;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JigsawBlockGui extends Gui {
	private final JigsawBlockEntity field_16522;
	private TextFieldWidget field_16520;
	private TextFieldWidget field_16518;
	private TextFieldWidget field_16519;
	private final List<TextFieldWidget> field_16521 = Lists.<TextFieldWidget>newArrayList();

	public JigsawBlockGui(JigsawBlockEntity jigsawBlockEntity) {
		this.field_16522 = jigsawBlockEntity;
	}

	@Override
	public void update() {
		this.field_16520.tick();
		this.field_16518.tick();
		this.field_16519.tick();
	}

	private void method_16346() {
		this.method_16348();
		this.client.openGui(null);
	}

	private void method_16349() {
		this.client.openGui(null);
	}

	private void method_16348() {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new class_3753(
					this.field_16522.getPos(), new Identifier(this.field_16520.getText()), new Identifier(this.field_16518.getText()), this.field_16519.getText()
				)
			);
	}

	@Override
	public void close() {
		this.method_16349();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				JigsawBlockGui.this.method_16346();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				JigsawBlockGui.this.method_16349();
			}
		});
		this.field_16521.clear();
		this.field_16518 = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 40, 300, 20);
		this.field_16518.setMaxLength(128);
		this.field_16518.setText(this.field_16522.method_16382().toString());
		this.field_16521.add(this.field_16518);
		this.field_16520 = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 80, 300, 20);
		this.field_16520.setMaxLength(128);
		this.field_16520.setText(this.field_16522.method_16381().toString());
		this.field_16521.add(this.field_16520);
		this.field_16519 = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 152, 120, 300, 20);
		this.field_16519.setMaxLength(256);
		this.field_16519.setText(this.field_16522.method_16380());
		this.field_16521.add(this.field_16519);
		this.listeners.addAll(this.field_16521);
		this.field_16520.method_1876(true);
		this.setFocused(this.field_16520);
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.field_16520.getText();
		String string2 = this.field_16518.getText();
		String string3 = this.field_16519.getText();
		this.initialize(minecraftClient, i, j);
		this.field_16520.setText(string);
		this.field_16518.setText(string2);
		this.field_16519.setText(string3);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			for (TextFieldWidget textFieldWidget : this.field_16521) {
				textFieldWidget.method_1876(this.getFocused() == textFieldWidget);
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
				this.method_16346();
				return true;
			}
		} else {
			TextFieldWidget textFieldWidget = null;
			TextFieldWidget textFieldWidget2 = null;

			for (TextFieldWidget textFieldWidget3 : this.field_16521) {
				if (textFieldWidget != null && textFieldWidget3.getVisible()) {
					textFieldWidget2 = textFieldWidget3;
					break;
				}

				if (textFieldWidget3.isFocused() && textFieldWidget3.getVisible()) {
					textFieldWidget = textFieldWidget3;
				}
			}

			if (textFieldWidget != null && textFieldWidget2 == null) {
				for (TextFieldWidget textFieldWidget3 : this.field_16521) {
					if (textFieldWidget3.getVisible() && textFieldWidget3 != textFieldWidget) {
						textFieldWidget2 = textFieldWidget3;
						break;
					}
				}
			}

			if (textFieldWidget2 != null && textFieldWidget2 != textFieldWidget) {
				textFieldWidget.method_1876(false);
				textFieldWidget2.method_1876(true);
				this.setFocused(textFieldWidget2);
			}

			return true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
		this.field_16518.render(i, j, f);
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
		this.field_16520.render(i, j, f);
		this.drawString(this.fontRenderer, I18n.translate("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
		this.field_16519.render(i, j, f);
		super.draw(i, j, f);
	}
}
