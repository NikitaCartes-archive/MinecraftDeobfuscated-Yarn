package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SelectWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final Screen parent;
	@Nullable
	private List<OrderedText> tooltip;
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
		this.client.keyboard.setRepeatEvents(true);
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.translatable("selectWorld.search"));
		this.searchBox.setChangedListener(search -> this.levelList.filter(search));
		this.levelList = new WorldListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, this.getSearchFilter(), this.levelList);
		this.addSelectableChild(this.searchBox);
		this.addSelectableChild(this.levelList);
		this.selectButton = this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 52,
				150,
				20,
				Text.translatable("selectWorld.select"),
				button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::play)
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 + 4, this.height - 52, 150, 20, Text.translatable("selectWorld.create"), button -> CreateWorldScreen.create(this.client, this)
			)
		);
		this.editButton = this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 28,
				72,
				20,
				Text.translatable("selectWorld.edit"),
				button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::edit)
			)
		);
		this.deleteButton = this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 76,
				this.height - 28,
				72,
				20,
				Text.translatable("selectWorld.delete"),
				button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::deleteIfConfirmed)
			)
		);
		this.recreateButton = this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height - 28,
				72,
				20,
				Text.translatable("selectWorld.recreate"),
				button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::recreate)
			)
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
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
		this.tooltip = null;
		this.levelList.render(matrices, mouseX, mouseY, delta);
		this.searchBox.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		if (this.tooltip != null) {
			this.renderOrderedTooltip(matrices, this.tooltip, mouseX, mouseY);
		}
	}

	public void setTooltip(List<OrderedText> tooltip) {
		this.tooltip = tooltip;
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

	public Supplier<String> getSearchFilter() {
		return () -> this.searchBox.getText();
	}
}
