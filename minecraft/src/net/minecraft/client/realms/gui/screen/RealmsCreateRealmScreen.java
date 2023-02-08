package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
	private static final Text WORLD_NAME_TEXT = Text.translatable("mco.configure.world.name");
	private static final Text WORLD_DESCRIPTION_TEXT = Text.translatable("mco.configure.world.description");
	private final RealmsServer server;
	private final RealmsMainScreen parent;
	private TextFieldWidget nameBox;
	private TextFieldWidget descriptionBox;
	private ButtonWidget createButton;

	public RealmsCreateRealmScreen(RealmsServer server, RealmsMainScreen parent) {
		super(Text.translatable("mco.selectServer.create"));
		this.server = server;
		this.parent = parent;
	}

	@Override
	public void tick() {
		if (this.nameBox != null) {
			this.nameBox.tick();
		}

		if (this.descriptionBox != null) {
			this.descriptionBox.tick();
		}
	}

	@Override
	public void init() {
		this.createButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.create.world"), button -> this.createWorld())
				.dimensions(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent))
				.dimensions(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20)
				.build()
		);
		this.createButton.active = false;
		this.nameBox = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, 65, 200, 20, null, Text.translatable("mco.configure.world.name"));
		this.addSelectableChild(this.nameBox);
		this.setInitialFocus(this.nameBox);
		this.descriptionBox = new TextFieldWidget(
			this.client.textRenderer, this.width / 2 - 100, 115, 200, 20, null, Text.translatable("mco.configure.world.description")
		);
		this.addSelectableChild(this.descriptionBox);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		boolean bl = super.charTyped(chr, modifiers);
		this.createButton.active = this.valid();
		return bl;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setScreen(this.parent);
			return true;
		} else {
			boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
			this.createButton.active = this.valid();
			return bl;
		}
	}

	private void createWorld() {
		if (this.valid()) {
			RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
				this.parent,
				this.server,
				Text.translatable("mco.selectServer.create"),
				Text.translatable("mco.create.world.subtitle"),
				10526880,
				Text.translatable("mco.create.world.skip"),
				() -> this.client.execute(() -> this.client.setScreen(this.parent.newScreen())),
				() -> this.client.setScreen(this.parent.newScreen())
			);
			realmsResetWorldScreen.setResetTitle(Text.translatable("mco.create.world.reset.title"));
			this.client
				.setScreen(
					new RealmsLongRunningMcoTaskScreen(
						this.parent, new WorldCreationTask(this.server.id, this.nameBox.getText(), this.descriptionBox.getText(), realmsResetWorldScreen)
					)
				);
		}
	}

	private boolean valid() {
		return !this.nameBox.getText().trim().isEmpty();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 11, 16777215);
		this.textRenderer.draw(matrices, WORLD_NAME_TEXT, (float)(this.width / 2 - 100), 52.0F, 10526880);
		this.textRenderer.draw(matrices, WORLD_DESCRIPTION_TEXT, (float)(this.width / 2 - 100), 102.0F, 10526880);
		if (this.nameBox != null) {
			this.nameBox.render(matrices, mouseX, mouseY, delta);
		}

		if (this.descriptionBox != null) {
			this.descriptionBox.render(matrices, mouseX, mouseY, delta);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
