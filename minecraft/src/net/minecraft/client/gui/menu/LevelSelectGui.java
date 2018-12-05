package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_528;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LevelEntryWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LevelSelectGui extends Gui {
	private static final Logger LOGGER = LogManager.getLogger();
	protected Gui field_3221;
	protected String title = "Select world";
	private String field_3222;
	private ButtonWidget field_3219;
	private ButtonWidget field_3224;
	private ButtonWidget field_3215;
	private ButtonWidget field_3216;
	public TextFieldWidget field_3220;
	private class_528 field_3218;

	public LevelSelectGui(Gui gui) {
		this.field_3221 = gui;
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.field_3218.mouseScrolled(d);
	}

	@Override
	public void update() {
		this.field_3220.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.title = I18n.translate("selectWorld.title");
		this.field_3220 = new TextFieldWidget(0, this.fontRenderer, this.width / 2 - 100, 22, 200, 20, this.field_3220) {
			@Override
			public void method_1876(boolean bl) {
				super.method_1876(true);
			}
		};
		this.field_3220.method_1863((integer, string) -> this.field_3218.method_2750(() -> string, false));
		this.field_3218 = new class_528(this, this.client, this.width, this.height, 48, this.height - 64, 36, () -> this.field_3220.getText(), this.field_3218);
		this.field_3224 = this.addButton(new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select")) {
			@Override
			public void onPressed(double d, double e) {
				LevelEntryWidget levelEntryWidget = LevelSelectGui.this.field_3218.method_2753();
				if (levelEntryWidget != null) {
					levelEntryWidget.method_2768();
				}
			}
		});
		this.addButton(new ButtonWidget(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectGui.this.client.openGui(new NewLevelGui(LevelSelectGui.this));
			}
		});
		this.field_3215 = this.addButton(new ButtonWidget(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.edit")) {
			@Override
			public void onPressed(double d, double e) {
				LevelEntryWidget levelEntryWidget = LevelSelectGui.this.field_3218.method_2753();
				if (levelEntryWidget != null) {
					levelEntryWidget.method_2756();
				}
			}
		});
		this.field_3219 = this.addButton(new ButtonWidget(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete")) {
			@Override
			public void onPressed(double d, double e) {
				LevelEntryWidget levelEntryWidget = LevelSelectGui.this.field_3218.method_2753();
				if (levelEntryWidget != null) {
					levelEntryWidget.method_2755();
				}
			}
		});
		this.field_3216 = this.addButton(new ButtonWidget(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate")) {
			@Override
			public void onPressed(double d, double e) {
				LevelEntryWidget levelEntryWidget = LevelSelectGui.this.field_3218.method_2753();
				if (levelEntryWidget != null) {
					levelEntryWidget.method_2757();
				}
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				LevelSelectGui.this.client.openGui(LevelSelectGui.this.field_3221);
			}
		});
		this.field_3224.enabled = false;
		this.field_3219.enabled = false;
		this.field_3215.enabled = false;
		this.field_3216.enabled = false;
		this.listeners.add(this.field_3220);
		this.listeners.add(this.field_3218);
		this.field_3220.method_1876(true);
		this.field_3220.method_1856(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k) ? true : this.field_3220.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.field_3220.charTyped(c, i);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.field_3222 = null;
		this.field_3218.draw(i, j, f);
		this.field_3220.render(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
		super.draw(i, j, f);
		if (this.field_3222 != null) {
			this.drawTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3222)), i, j);
		}
	}

	public void method_2739(String string) {
		this.field_3222 = string;
	}

	public void method_2746(@Nullable LevelEntryWidget levelEntryWidget) {
		boolean bl = levelEntryWidget != null;
		this.field_3224.enabled = bl;
		this.field_3219.enabled = bl;
		this.field_3215.enabled = bl;
		this.field_3216.enabled = bl;
	}

	@Override
	public void onClosed() {
		if (this.field_3218 != null) {
			this.field_3218.getListeners().forEach(LevelEntryWidget::close);
		}
	}
}
