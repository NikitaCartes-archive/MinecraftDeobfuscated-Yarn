package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelSummary;
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
	protected void init() {
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.translatable("selectWorld.search"));
		this.searchBox.setChangedListener(search -> this.levelList.setSearch(search));
		this.addSelectableChild(this.searchBox);
		this.levelList = this.addDrawableChild(
			new WorldListWidget(this, this.client, this.width, this.height - 112, 48, 36, this.searchBox.getText(), this.levelList)
		);
		this.selectButton = this.addDrawableChild(
			ButtonWidget.builder(LevelSummary.SELECT_WORLD_TEXT, button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::play))
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
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 82, this.height - 28, 72, 20).build()
		);
		this.worldSelected(null);
	}

	@Override
	protected void method_56131() {
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.searchBox.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
	}

	public void worldSelected(@Nullable LevelSummary levelSummary) {
		if (levelSummary == null) {
			this.selectButton.setMessage(LevelSummary.SELECT_WORLD_TEXT);
			this.selectButton.active = false;
			this.editButton.active = false;
			this.recreateButton.active = false;
			this.deleteButton.active = false;
		} else {
			this.selectButton.setMessage(levelSummary.getSelectWorldText());
			this.selectButton.active = levelSummary.isSelectable();
			this.editButton.active = levelSummary.isEditable();
			this.recreateButton.active = levelSummary.isRecreatable();
			this.deleteButton.active = levelSummary.isDeletable();
		}
	}

	@Override
	public void removed() {
		if (this.levelList != null) {
			this.levelList.children().forEach(WorldListWidget.Entry::close);
		}
	}
}
