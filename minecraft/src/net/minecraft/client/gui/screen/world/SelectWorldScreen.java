package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.gen.GeneratorOptions;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SelectWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final GeneratorOptions DEBUG_GENERATOR_OPTIONS = new GeneratorOptions((long)"test1".hashCode(), true, false);
	protected final Screen parent;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	protected TextFieldWidget searchBox;
	private WorldListWidget levelList;

	public SelectWorldScreen(Screen parent) {
		super(Text.translatable("selectWorld.title"));
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
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.translatable("selectWorld.search"));
		this.searchBox.setChangedListener(search -> this.levelList.setSearch(search));
		this.levelList = new WorldListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, this.searchBox.getText(), this.levelList);
		this.addSelectableChild(this.searchBox);
		this.addSelectableChild(this.levelList);
		this.selectButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectWorld.select"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::play))
				.dimensions(this.width / 2 - 154, this.height - 52, 150, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> CreateWorldScreen.create(this.client, this))
				.dimensions(this.width / 2 + 4, this.height - 52, 150, 20)
				.build()
		);
		this.editButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectWorld.edit"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::edit))
				.dimensions(this.width / 2 - 154, this.height - 28, 72, 20)
				.build()
		);
		this.deleteButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("selectWorld.delete"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::deleteIfConfirmed)
				)
				.dimensions(this.width / 2 - 76, this.height - 28, 72, 20)
				.build()
		);
		this.recreateButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("selectWorld.recreate"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::recreate)
				)
				.dimensions(this.width / 2 + 4, this.height - 28, 72, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 82, this.height - 28, 72, 20).build()
		);
		this.worldSelected(false);
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers) ? true : this.searchBox.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.searchBox.charTyped(chr, modifiers);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.levelList.render(matrices, mouseX, mouseY, delta);
		this.searchBox.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
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
