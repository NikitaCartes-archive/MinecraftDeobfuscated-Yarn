package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LevelListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LevelSelectGui extends Gui {
	private static final Logger LOGGER = LogManager.getLogger();
	protected Gui parent;
	protected String title = "Select world";
	private String field_3222;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	protected TextFieldWidget searchBox;
	private LevelListWidget levelList;

	public LevelSelectGui(Gui gui) {
		this.parent = gui;
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.levelList.mouseScrolled(d);
	}

	@Override
	public void update() {
		this.searchBox.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.title = I18n.translate("selectWorld.title");
		this.searchBox = new TextFieldWidget(0, this.fontRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox) {
			@Override
			public void setFocused(boolean bl) {
				super.setFocused(true);
			}
		};
		this.searchBox.setChangedListener((integer, string) -> this.levelList.filter(() -> string, false));
		this.levelList = new LevelListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.levelList);
		this.selectButton = this.addButton(new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectGui.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.loadLevel();
				}
			}
		});
		this.addButton(new ButtonWidget(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectGui.this.client.openGui(new NewLevelGui(LevelSelectGui.this));
			}
		});
		this.editButton = this.addButton(new ButtonWidget(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.edit")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectGui.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.method_2756();
				}
			}
		});
		this.deleteButton = this.addButton(new ButtonWidget(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectGui.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.method_2755();
				}
			}
		});
		this.recreateButton = this.addButton(new ButtonWidget(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectGui.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.method_2757();
				}
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectGui.this.client.openGui(LevelSelectGui.this.parent);
			}
		});
		this.selectButton.enabled = false;
		this.deleteButton.enabled = false;
		this.editButton.enabled = false;
		this.recreateButton.enabled = false;
		this.listeners.add(this.searchBox);
		this.listeners.add(this.levelList);
		this.searchBox.setFocused(true);
		this.searchBox.method_1856(false);
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
	public void draw(int i, int j, float f) {
		this.field_3222 = null;
		this.levelList.draw(i, j, f);
		this.searchBox.render(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
		super.draw(i, j, f);
		if (this.field_3222 != null) {
			this.drawTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3222)), i, j);
		}
	}

	public void method_2739(String string) {
		this.field_3222 = string;
	}

	public void method_2746(@Nullable LevelSelectEntryWidget levelSelectEntryWidget) {
		boolean bl = levelSelectEntryWidget != null;
		this.selectButton.enabled = bl;
		this.deleteButton.enabled = bl;
		this.editButton.enabled = bl;
		this.recreateButton.enabled = bl;
	}

	@Override
	public void onClosed() {
		if (this.levelList != null) {
			this.levelList.getEntries().forEach(LevelSelectEntryWidget::close);
		}
	}
}
