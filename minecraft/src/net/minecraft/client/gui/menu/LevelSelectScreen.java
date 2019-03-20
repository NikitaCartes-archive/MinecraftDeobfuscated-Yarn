package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LevelListWidget;
import net.minecraft.client.gui.widget.LevelSelectEntryWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LevelSelectScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Screen parent;
	protected String title = "Select world";
	private String field_3222;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget editButton;
	private ButtonWidget recreateButton;
	public TextFieldWidget searchBox;
	private LevelListWidget levelList;

	public LevelSelectScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return super.mouseScrolled(d, e, f);
	}

	@Override
	public void update() {
		this.searchBox.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.title = I18n.translate("selectWorld.title");
		this.searchBox = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 22, 200, 20, this.searchBox);
		this.searchBox.setChangedListener(string -> this.levelList.filter(() -> string, false));
		this.levelList = new LevelListWidget(
			this, this.client, this.screenWidth, this.screenHeight, 48, this.screenHeight - 64, 36, () -> this.searchBox.getText(), this.levelList
		);
		this.listeners.add(this.searchBox);
		this.listeners.add(this.levelList);
		this.selectButton = this.addButton(new ButtonWidget(this.screenWidth / 2 - 154, this.screenHeight - 52, 150, 20, I18n.translate("selectWorld.select")) {
			@Override
			public void onPressed() {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectScreen.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.loadLevel();
				}
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 4, this.screenHeight - 52, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void onPressed() {
				LevelSelectScreen.this.client.openScreen(new NewLevelScreen(LevelSelectScreen.this));
			}
		});
		this.editButton = this.addButton(new ButtonWidget(this.screenWidth / 2 - 154, this.screenHeight - 28, 72, 20, I18n.translate("selectWorld.edit")) {
			@Override
			public void onPressed() {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectScreen.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.method_2756();
				}
			}
		});
		this.deleteButton = this.addButton(new ButtonWidget(this.screenWidth / 2 - 76, this.screenHeight - 28, 72, 20, I18n.translate("selectWorld.delete")) {
			@Override
			public void onPressed() {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectScreen.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.method_2755();
				}
			}
		});
		this.recreateButton = this.addButton(new ButtonWidget(this.screenWidth / 2 + 4, this.screenHeight - 28, 72, 20, I18n.translate("selectWorld.recreate")) {
			@Override
			public void onPressed() {
				LevelSelectEntryWidget levelSelectEntryWidget = LevelSelectScreen.this.levelList.method_2753();
				if (levelSelectEntryWidget != null) {
					levelSelectEntryWidget.method_2757();
				}
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 82, this.screenHeight - 28, 72, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed() {
				LevelSelectScreen.this.client.openScreen(LevelSelectScreen.this.parent);
			}
		});
		this.selectButton.active = false;
		this.deleteButton.active = false;
		this.editButton.active = false;
		this.recreateButton.active = false;
		this.focusOn(this.searchBox);
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
		this.field_3222 = null;
		this.levelList.render(i, j, f);
		this.searchBox.render(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 8, 16777215);
		super.render(i, j, f);
		if (this.field_3222 != null) {
			this.drawTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3222)), i, j);
		}
	}

	public void method_2739(String string) {
		this.field_3222 = string;
	}

	public void method_2746(@Nullable LevelSelectEntryWidget levelSelectEntryWidget) {
		boolean bl = levelSelectEntryWidget != null;
		this.selectButton.active = bl;
		this.deleteButton.active = bl;
		this.editButton.active = bl;
		this.recreateButton.active = bl;
	}

	@Override
	public void onClosed() {
		if (this.levelList != null) {
			this.levelList.getInputListeners().forEach(LevelSelectEntryWidget::close);
		}
	}
}
