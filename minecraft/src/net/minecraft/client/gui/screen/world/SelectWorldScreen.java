package net.minecraft.client.gui.screen.world;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SelectWorldScreen extends Screen {
	protected final Screen parent;
	private List<OrderedText> tooltipText;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	protected TextFieldWidget searchBox;
	private WorldListWidget levelList;

	public SelectWorldScreen(Screen parent) {
		super(new TranslatableText("selectWorld.title"));
		this.parent = parent;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public void tick() {
		this.searchBox.tick();
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, new TranslatableText("selectWorld.search"));
		this.searchBox.setChangedListener(string -> this.levelList.filter(() -> string, false));
		this.levelList = new WorldListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.levelList);
		this.children.add(this.searchBox);
		this.children.add(this.levelList);
		this.selectButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 52,
				150,
				20,
				new TranslatableText("selectWorld.select"),
				buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::play)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height - 52,
				150,
				20,
				new TranslatableText("selectWorld.create"),
				buttonWidget -> this.client.openScreen(CreateWorldScreen.method_31130(this))
			)
		);
		this.editButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 28,
				72,
				20,
				new TranslatableText("selectWorld.edit"),
				buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::edit)
			)
		);
		this.deleteButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 76,
				this.height - 28,
				72,
				20,
				new TranslatableText("selectWorld.delete"),
				buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::delete)
			)
		);
		this.recreateButton = this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height - 28,
				72,
				20,
				new TranslatableText("selectWorld.recreate"),
				buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::recreate)
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.worldSelected(false);
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers) ? true : this.searchBox.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.parent);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return this.searchBox.charTyped(chr, keyCode);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltipText = null;
		this.levelList.render(matrices, mouseX, mouseY, delta);
		this.searchBox.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		if (this.tooltipText != null) {
			this.renderTooltip(matrices, this.tooltipText, mouseX, mouseY);
		}
	}

	public void setTooltip(List<OrderedText> list) {
		this.tooltipText = list;
	}

	public void worldSelected(boolean active) {
		this.selectButton.active = active;
		this.deleteButton.active = active;
		this.editButton.active = active;
		this.recreateButton.active = active;
	}

	@Override
	public void removed() {
		if (this.levelList != null) {
			this.levelList.children().forEach(WorldListWidget.Entry::close);
		}
	}
}
