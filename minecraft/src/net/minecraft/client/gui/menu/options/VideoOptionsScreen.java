package net.minecraft.client.gui.menu.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends Screen {
	private final Screen parent;
	private final GameOptions options;
	private ButtonListWidget list;
	private static final GameOption[] OPTIONS = new GameOption[]{
		GameOption.GRAPHICS,
		GameOption.RENDER_DISTANCE,
		GameOption.AO,
		GameOption.FRAMERATE_LIMIT,
		GameOption.VSYNC,
		GameOption.VIEW_BOBBING,
		GameOption.GUI_SCALE,
		GameOption.ATTACK_INDICATOR,
		GameOption.GAMMA,
		GameOption.CLOUDS,
		GameOption.FULLSCREEN,
		GameOption.PARTICLES,
		GameOption.MIPMAP_LEVELS,
		GameOption.ENTITY_SHADOWS,
		GameOption.BIOME_BLEND_RADIUS
	};
	private int mipmapLevels;

	public VideoOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableTextComponent("options.videoTitle"));
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	protected void init() {
		this.mipmapLevels = this.options.mipmapLevels;
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.list.method_20406(GameOption.FULLSCREEN_RESOLUTION);
		this.list.addAll(OPTIONS);
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.minecraft.options.write();
			this.minecraft.window.method_4475();
			this.minecraft.openScreen(this.parent);
		}));
	}

	@Override
	public void removed() {
		if (this.options.mipmapLevels != this.mipmapLevels) {
			this.minecraft.getSpriteAtlas().setMipLevel(this.options.mipmapLevels);
			this.minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.minecraft.getSpriteAtlas().setFilter(false, this.options.mipmapLevels > 0);
			this.minecraft.reloadResourcesConcurrently();
		}

		this.minecraft.options.write();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = this.options.guiScale;
		if (super.mouseClicked(d, e, i)) {
			if (this.options.guiScale != j) {
				this.minecraft.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		int j = this.options.guiScale;
		if (super.mouseReleased(d, e, i)) {
			return true;
		} else if (this.list.mouseReleased(d, e, i)) {
			if (this.options.guiScale != j) {
				this.minecraft.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.list.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
		super.render(i, j, f);
	}
}
