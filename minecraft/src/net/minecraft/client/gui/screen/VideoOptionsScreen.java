package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.FullScreenOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends GameOptionsScreen {
	@Nullable
	private List<class_5348> field_25453;
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
		Option.ENTITY_DISTANCE_SCALING
	};
	private int mipmapLevels;

	public VideoOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("options.videoTitle"));
		this.mipmapLevels = options.mipmapLevels;
	}

	@Override
	protected void init() {
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addSingleOptionEntry(new FullScreenOption(this.client.getWindow()));
		this.list.addSingleOptionEntry(Option.BIOME_BLEND_RADIUS);
		this.list.addAll(OPTIONS);
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> {
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.field_25453 = null;
		Optional<AbstractButtonWidget> optional = this.list.method_29624((double)mouseX, (double)mouseY);
		if (optional.isPresent() && optional.get() instanceof OptionButtonWidget) {
			Optional<TranslatableText> optional2 = ((OptionButtonWidget)optional.get()).method_29623().method_29619();
			if (optional2.isPresent()) {
				Builder<class_5348> builder = ImmutableList.builder();
				this.textRenderer.wrapLines((class_5348)optional2.get(), 200).forEach(builder::add);
				this.field_25453 = builder.build();
			}
		}

		this.renderBackground(matrices);
		this.list.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		if (this.field_25453 != null) {
			this.renderTooltip(matrices, this.field_25453, mouseX, mouseY);
		}
	}
}
