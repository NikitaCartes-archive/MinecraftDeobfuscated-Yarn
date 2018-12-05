package net.minecraft.client.gui.menu.settings;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.VideoSettingsListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.settings.GameOptions;

@Environment(EnvType.CLIENT)
public class VideoSettingsGui extends Gui {
	private final Gui parent;
	protected String title = "Video Settings";
	private final GameOptions settings;
	private VideoSettingsListWidget field_2639;
	private static final GameOptions.Option[] MENU_OPTIONS = new GameOptions.Option[]{
		GameOptions.Option.GRAPHICS,
		GameOptions.Option.RENDER_DISTANCE,
		GameOptions.Option.AO,
		GameOptions.Option.FRAMERATE_LIMIT,
		GameOptions.Option.VSYNC,
		GameOptions.Option.VIEW_BOBBING,
		GameOptions.Option.GUI_SCALE,
		GameOptions.Option.ATTACK_INDICATOR,
		GameOptions.Option.GAMMA,
		GameOptions.Option.RENDER_CLOUDS,
		GameOptions.Option.FULLSCREEN,
		GameOptions.Option.PARTICLES,
		GameOptions.Option.MIPMAP_LEVELS,
		GameOptions.Option.ENTITY_SHADOWS,
		GameOptions.Option.BIOME_BLEND_RADIUS
	};

	public VideoSettingsGui(Gui gui, GameOptions gameOptions) {
		this.parent = gui;
		this.settings = gameOptions;
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.field_2639;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.videoTitle");
		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height - 27, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				VideoSettingsGui.this.client.options.write();
				VideoSettingsGui.this.client.window.method_4475();
				VideoSettingsGui.this.client.openGui(VideoSettingsGui.this.parent);
			}
		});
		this.field_2639 = new VideoSettingsListWidget(this.client, this.width, this.height, 32, this.height - 32, 25, MENU_OPTIONS);
		this.listeners.add(this.field_2639);
	}

	@Override
	public void close() {
		this.client.options.write();
		super.close();
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
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 5, 16777215);
		super.draw(i, j, f);
	}
}
