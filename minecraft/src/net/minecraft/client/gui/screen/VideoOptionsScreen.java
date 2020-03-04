package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.FullScreenOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends GameOptionsScreen {
	private ButtonListWidget list;
	private static final Option[] OPTIONS = new Option[]{
		Option.GRAPHICS,
		Option.RENDER_DISTANCE,
		Option.AO,
		Option.FRAMERATE_LIMIT,
		Option.VSYNC,
		Option.VIEW_BOBBING,
		Option.GUI_SCALE,
		Option.ATTACK_INDICATOR,
		Option.GAMMA,
		Option.CLOUDS,
		Option.FULLSCREEN,
		Option.PARTICLES,
		Option.MIPMAP_LEVELS,
		Option.ENTITY_SHADOWS
	};
	private int mipmapLevels;

	public VideoOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("options.videoTitle"));
	}

	@Override
	protected void init() {
		this.mipmapLevels = this.gameOptions.mipmapLevels;
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addSingleOptionEntry(new FullScreenOption(this.client.getWindow()));
		this.list.addSingleOptionEntry(Option.BIOME_BLEND_RADIUS);
		this.list.addAll(OPTIONS);
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), button -> {
			this.client.options.write();
			this.client.getWindow().applyVideoMode();
			this.client.openScreen(this.parent);
		}));
	}

	@Override
	public void removed() {
		if (this.gameOptions.mipmapLevels != this.mipmapLevels) {
			this.client.resetMipmapLevels(this.gameOptions.mipmapLevels);
			this.client.reloadResourcesConcurrently();
		}

		super.removed();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int i = this.gameOptions.guiScale;
		if (super.mouseClicked(mouseX, mouseY, button)) {
			if (this.gameOptions.guiScale != i) {
				this.client.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		int i = this.gameOptions.guiScale;
		if (super.mouseReleased(mouseX, mouseY, button)) {
			return true;
		} else if (this.list.mouseReleased(mouseX, mouseY, button)) {
			if (this.gameOptions.guiScale != i) {
				this.client.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 5, 16777215);
		super.render(mouseX, mouseY, delta);
	}
}
