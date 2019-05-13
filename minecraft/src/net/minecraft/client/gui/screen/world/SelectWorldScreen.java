package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(EnvType.CLIENT)
public class SelectWorldScreen extends Screen {
	protected final Screen parent;
	private String tooltipText;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	protected TextFieldWidget searchBox;
	private WorldListWidget field_3218;

	public SelectWorldScreen(Screen screen) {
		super(new TranslatableComponent("selectWorld.title"));
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
		this.searchBox.setChangedListener(string -> this.field_3218.filter(() -> string, false));
		this.field_3218 = new WorldListWidget(
			this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.field_3218
		);
		this.children.add(this.searchBox);
		this.children.add(this.field_3218);
		this.selectButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 52,
				150,
				20,
				I18n.translate("selectWorld.select"),
				buttonWidget -> this.field_3218.method_20159().ifPresent(WorldListWidget.LevelItem::play)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height - 52,
				150,
				20,
				I18n.translate("selectWorld.create"),
				buttonWidget -> this.minecraft.method_1507(new CreateWorldScreen(this))
			)
		);
		this.editButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 28,
				72,
				20,
				I18n.translate("selectWorld.edit"),
				buttonWidget -> this.field_3218.method_20159().ifPresent(WorldListWidget.LevelItem::edit)
			)
		);
		this.deleteButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 76,
				this.height - 28,
				72,
				20,
				I18n.translate("selectWorld.delete"),
				buttonWidget -> this.field_3218.method_20159().ifPresent(WorldListWidget.LevelItem::delete)
			)
		);
		this.recreateButton = this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height - 28,
				72,
				20,
				I18n.translate("selectWorld.recreate"),
				buttonWidget -> this.field_3218.method_20159().ifPresent(WorldListWidget.LevelItem::recreate)
			)
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.method_1507(this.parent))
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
		this.field_3218.render(i, j, f);
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
		if (this.field_3218 != null) {
			this.field_3218.children().forEach(WorldListWidget.LevelItem::close);
		}
	}
}
