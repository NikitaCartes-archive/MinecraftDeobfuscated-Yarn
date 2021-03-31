package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsSettingsScreen extends RealmsScreen {
	private static final int field_32124 = 212;
	private static final Text WORLD_NAME_TEXT = new TranslatableText("mco.configure.world.name");
	private static final Text WORLD_DESCRIPTION_TEXT = new TranslatableText("mco.configure.world.description");
	private final RealmsConfigureWorldScreen parent;
	private final RealmsServer serverData;
	private ButtonWidget doneButton;
	private TextFieldWidget descEdit;
	private TextFieldWidget nameEdit;
	private RealmsLabel titleLabel;

	public RealmsSettingsScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData) {
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void tick() {
		this.nameEdit.tick();
		this.descEdit.tick();
		this.doneButton.active = !this.nameEdit.getText().trim().isEmpty();
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		int i = this.width / 2 - 106;
		this.doneButton = this.addButton(
			new ButtonWidget(i - 2, row(12), 106, 20, new TranslatableText("mco.configure.world.buttons.done"), buttonWidgetx -> this.save())
		);
		this.addButton(new ButtonWidget(this.width / 2 + 2, row(12), 106, 20, ScreenTexts.CANCEL, buttonWidgetx -> this.client.openScreen(this.parent)));
		String string = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
		ButtonWidget buttonWidget = new ButtonWidget(this.width / 2 - 53, row(0), 106, 20, new TranslatableText(string), buttonWidgetx -> {
			if (this.serverData.state == RealmsServer.State.OPEN) {
				Text text = new TranslatableText("mco.configure.world.close.question.line1");
				Text text2 = new TranslatableText("mco.configure.world.close.question.line2");
				this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
					if (bl) {
						this.parent.closeTheWorld(this);
					} else {
						this.client.openScreen(this);
					}
				}, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
			} else {
				this.parent.openTheWorld(false, this);
			}
		});
		this.addButton(buttonWidget);
		this.nameEdit = new TextFieldWidget(this.client.textRenderer, i, row(4), 212, 20, null, new TranslatableText("mco.configure.world.name"));
		this.nameEdit.setMaxLength(32);
		this.nameEdit.setText(this.serverData.getName());
		this.addChild(this.nameEdit);
		this.focusOn(this.nameEdit);
		this.descEdit = new TextFieldWidget(this.client.textRenderer, i, row(8), 212, 20, null, new TranslatableText("mco.configure.world.description"));
		this.descEdit.setMaxLength(32);
		this.descEdit.setText(this.serverData.getDescription());
		this.addChild(this.descEdit);
		this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.settings.title"), this.width / 2, 17, 16777215));
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.titleLabel.render(this, matrices);
		this.textRenderer.draw(matrices, WORLD_NAME_TEXT, (float)(this.width / 2 - 106), (float)row(3), 10526880);
		this.textRenderer.draw(matrices, WORLD_DESCRIPTION_TEXT, (float)(this.width / 2 - 106), (float)row(7), 10526880);
		this.nameEdit.render(matrices, mouseX, mouseY, delta);
		this.descEdit.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}

	public void save() {
		this.parent.saveSettings(this.nameEdit.getText(), this.descEdit.getText());
	}
}
