package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class LevelSelectScreen extends Screen {
	protected final Screen parent;
	private String tooltipText;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	protected TextFieldWidget searchBox;
	private LevelListWidget levelList;

	public LevelSelectScreen(Screen screen) {
		super(new TranslatableTextComponent("selectWorld.title"));
		this.parent = screen;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return super.mouseScrolled(d, e, f);
	}

	@Override
	public void tick() {
		this.searchBox.tick();
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, I18n.translate("selectWorld.search"));
		this.searchBox.setChangedListener(string -> this.levelList.filter(() -> string, false));
		this.levelList = new LevelListWidget(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.levelList);
		this.children.add(this.searchBox);
		this.children.add(this.levelList);
		this.selectButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 52,
				150,
				20,
				I18n.translate("selectWorld.select"),
				buttonWidget -> this.levelList.method_20159().ifPresent(LevelListWidget.LevelItem::play)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create"), buttonWidget -> this.minecraft.openScreen(new NewLevelScreen(this))
			)
		);
		this.editButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 28,
				72,
				20,
				I18n.translate("selectWorld.edit"),
				buttonWidget -> this.levelList.method_20159().ifPresent(LevelListWidget.LevelItem::edit)
			)
		);
		this.deleteButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 76,
				this.height - 28,
				72,
				20,
				I18n.translate("selectWorld.delete"),
				buttonWidget -> this.levelList.method_20159().ifPresent(LevelListWidget.LevelItem::delete)
			)
		);
		this.recreateButton = this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height - 28,
				72,
				20,
				I18n.translate("selectWorld.recreate"),
				buttonWidget -> this.levelList.method_20159().ifPresent(LevelListWidget.LevelItem::recreate)
			)
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.worldSelected(false);
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k) ? true : this.searchBox.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.searchBox.charTyped(c, i);
	}

	@Override
	public void render(int i, int j, float f) {
		this.tooltipText = null;
		this.levelList.render(i, j, f);
		this.searchBox.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
		super.render(i, j, f);
		if (this.tooltipText != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.tooltipText)), i, j);
		}
	}

	public void setTooltip(String string) {
		this.tooltipText = string;
	}

	public void worldSelected(boolean bl) {
		this.selectButton.active = bl;
		this.deleteButton.active = bl;
		this.editButton.active = bl;
		this.recreateButton.active = bl;
	}

	@Override
	public void removed() {
		if (this.levelList != null) {
			this.levelList.children().forEach(LevelListWidget.LevelItem::close);
		}
	}
}
