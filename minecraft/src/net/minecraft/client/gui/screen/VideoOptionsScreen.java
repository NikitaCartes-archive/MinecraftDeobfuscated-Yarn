package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.FullScreenOption;
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
		Option.ENTITY_SHADOWS,
		Option.BIOME_BLEND_RADIUS
	};
	private int mipmapLevels;

	public VideoOptionsScreen(Screen parent, GameOptions options) {
		super(new TranslatableText("options.videoTitle"));
		this.parent = parent;
		this.options = options;
	}

	@Override
	protected void init() {
		this.mipmapLevels = this.options.mipmapLevels;
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.list.addSingleOptionEntry(new FullScreenOption(this.minecraft.window));
		this.list.addAll(OPTIONS);
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), button -> {
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int i = this.options.guiScale;
		if (super.mouseClicked(mouseX, mouseY, button)) {
			if (this.options.guiScale != i) {
				this.minecraft.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		int i = this.options.guiScale;
		if (super.mouseReleased(mouseX, mouseY, button)) {
			return true;
		} else if (this.list.mouseReleased(mouseX, mouseY, button)) {
			if (this.options.guiScale != i) {
				this.minecraft.onResolutionChanged();
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
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 5, 16777215);
		super.render(mouseX, mouseY, delta);
	}
}
