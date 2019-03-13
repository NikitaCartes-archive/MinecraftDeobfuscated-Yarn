package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.VideoSettingsListWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class VideoSettingsScreen extends Screen {
	private final Screen parent;
	protected String title = "Video Settings";
	private final GameOptions settings;
	private VideoSettingsListWidget field_2639;
	private static final GameOption[] MENU_OPTIONS = new GameOption[]{
		GameOption.GRAPHICS,
		GameOption.field_1933,
		GameOption.AO,
		GameOption.field_1935,
		GameOption.VSYNC,
		GameOption.VIEW_BOBBING,
		GameOption.GUI_SCALE,
		GameOption.field_18192,
		GameOption.field_1945,
		GameOption.RENDER_CLOUDS,
		GameOption.FULLSCREEN,
		GameOption.PARTICLES,
		GameOption.field_18190,
		GameOption.ENTITY_SHADOWS,
		GameOption.field_18189
	};

	public VideoSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.videoTitle");
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight - 27, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				VideoSettingsScreen.this.client.field_1690.write();
				VideoSettingsScreen.this.client.window.method_4475();
				VideoSettingsScreen.this.client.method_1507(VideoSettingsScreen.this.parent);
			}
		});
		this.field_2639 = new VideoSettingsListWidget(this.client, this.screenWidth, this.screenHeight, 32, this.screenHeight - 32, 25, MENU_OPTIONS);
		this.listeners.add(this.field_2639);
	}

	@Override
	public void onClosed() {
		this.client.field_1690.write();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = this.settings.guiScale;
		if (super.mouseClicked(d, e, i)) {
			if (this.settings.guiScale != j) {
				this.client.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		int j = this.settings.guiScale;
		if (super.mouseReleased(d, e, i)) {
			return true;
		} else if (this.field_2639.mouseReleased(d, e, i)) {
			if (this.settings.guiScale != j) {
				this.client.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.field_2639.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 5, 16777215);
		super.draw(i, j, f);
	}
}
