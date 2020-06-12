package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5405;
import net.minecraft.class_5407;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.FullScreenOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends GameOptionsScreen {
	private static final Text field_25682 = new TranslatableText("options.graphics.fabulous").formatted(Formatting.ITALIC);
	private static final Text field_25683 = new TranslatableText("options.graphics.warning.message", field_25682, field_25682);
	private static final Text field_25684 = new TranslatableText("options.graphics.warning.title").formatted(Formatting.RED);
	private static final Text field_25685 = new TranslatableText("options.graphics.warning.accept");
	private static final Text field_25686 = new TranslatableText("options.graphics.warning.cancel");
	private static final Text field_25687 = new LiteralText("\n");
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
	@Nullable
	private List<StringRenderable> field_25453;
	private ButtonListWidget list;
	private final class_5407 field_25688;
	private final int mipmapLevels;

	public VideoOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, new TranslatableText("options.videoTitle"));
		this.field_25688 = parent.client.method_30049();
		this.mipmapLevels = gameOptions.mipmapLevels;
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
		GraphicsMode graphicsMode = this.gameOptions.graphicsMode;
		if (super.mouseClicked(mouseX, mouseY, button)) {
			if (this.gameOptions.guiScale != i) {
				this.client.onResolutionChanged();
			}

			if (this.gameOptions.graphicsMode != graphicsMode && this.gameOptions.graphicsMode == GraphicsMode.FABULOUS && this.field_25688.method_30055()) {
				this.gameOptions.graphicsMode = GraphicsMode.FANCY;
				List<StringRenderable> list = Lists.<StringRenderable>newArrayList(field_25683, field_25687);
				String string = this.field_25688.method_30060();
				if (string != null) {
					list.add(field_25687);
					list.add(new TranslatableText("options.graphics.warning.renderer", string).formatted(Formatting.GRAY));
				}

				String string2 = this.field_25688.method_30063();
				if (string2 != null) {
					list.add(field_25687);
					list.add(new TranslatableText("options.graphics.warning.vendor", string2).formatted(Formatting.GRAY));
				}

				String string3 = this.field_25688.method_30062();
				if (string3 != null) {
					list.add(field_25687);
					list.add(new TranslatableText("options.graphics.warning.version", string3).formatted(Formatting.GRAY));
				}

				this.client.openScreen(new class_5405(field_25684, list, ImmutableList.of(new class_5405.class_5406(field_25685, buttonWidget -> {
					this.gameOptions.graphicsMode = GraphicsMode.FABULOUS;
					MinecraftClient.getInstance().worldRenderer.reload();
					this.client.openScreen(this);
				}), new class_5405.class_5406(field_25686, buttonWidget -> {
					this.gameOptions.graphicsMode = GraphicsMode.FAST;
					MinecraftClient.getInstance().worldRenderer.reload();
					this.client.openScreen(this);
				}))));
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
			Optional<List<StringRenderable>> optional2 = ((OptionButtonWidget)optional.get()).method_29623().method_29619();
			optional2.ifPresent(list -> this.field_25453 = list);
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
