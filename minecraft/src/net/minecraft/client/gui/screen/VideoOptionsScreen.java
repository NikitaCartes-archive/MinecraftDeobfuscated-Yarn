package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends Screen {
	private final Screen parent;
	private final GameOptions options;
	private ButtonListWidget list;
	private static final Option[] OPTIONS = new Option[]{
		Option.GRAPHICS,
		Option.field_1933,
		Option.AO,
		Option.field_1935,
		Option.VSYNC,
		Option.VIEW_BOBBING,
		Option.GUI_SCALE,
		Option.ATTACK_INDICATOR,
		Option.field_1945,
		Option.CLOUDS,
		Option.FULLSCREEN,
		Option.PARTICLES,
		Option.field_18190,
		Option.ENTITY_SHADOWS,
		Option.field_18189
	};
	private int mipmapLevels;

	public VideoOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableText("options.videoTitle"));
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	protected void init() {
		this.mipmapLevels = this.options.mipmapLevels;
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.list.addSingleOptionEntry(Option.field_1931);
		this.list.addAll(OPTIONS);
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.minecraft.field_1690.write();
			this.minecraft.window.method_4475();
			this.minecraft.method_1507(this.parent);
		}));
	}

	@Override
	public void removed() {
		if (this.options.mipmapLevels != this.mipmapLevels) {
			this.minecraft.method_1549().setMipLevel(this.options.mipmapLevels);
			this.minecraft.method_1531().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.minecraft.method_1549().setFilter(false, this.options.mipmapLevels > 0);
			this.minecraft.reloadResourcesConcurrently();
		}

		this.minecraft.field_1690.write();
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
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 5, 16777215);
		super.render(i, j, f);
	}
}
