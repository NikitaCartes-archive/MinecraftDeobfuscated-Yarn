package net.minecraft.client.gui.screen.world;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ExperimentsScreen extends Screen {
	private static final int INFO_WIDTH = 310;
	private final ThreePartsLayoutWidget experimentToggleList = new ThreePartsLayoutWidget(this);
	private final Screen parent;
	private final ResourcePackManager resourcePackManager;
	private final Consumer<ResourcePackManager> applier;
	private final Object2BooleanMap<ResourcePackProfile> experiments = new Object2BooleanLinkedOpenHashMap<>();

	protected ExperimentsScreen(Screen parent, ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier) {
		super(Text.translatable("experiments_screen.title"));
		this.parent = parent;
		this.resourcePackManager = resourcePackManager;
		this.applier = applier;

		for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
			if (resourcePackProfile.getSource() == ResourcePackSource.FEATURE) {
				this.experiments.put(resourcePackProfile, resourcePackManager.getEnabledProfiles().contains(resourcePackProfile));
			}
		}
	}

	@Override
	protected void init() {
		this.experimentToggleList.addHeader(new TextWidget(Text.translatable("selectWorld.experiments"), this.textRenderer));
		GridWidget.Adder adder = this.experimentToggleList.addFooter(new GridWidget()).createAdder(1);
		adder.add(
			new MultilineTextWidget(Text.translatable("selectWorld.experiments.info").formatted(Formatting.RED), this.textRenderer).setMaxWidth(310),
			adder.copyPositioner().marginBottom(15)
		);
		WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(310).withTooltipBox(2, true).setRowSpacing(4);
		this.experiments
			.forEach(
				(pack, enabled) -> builder.add(
							getDataPackName(pack), () -> this.experiments.getBoolean(pack), enabledx -> this.experiments.put(pack, enabledx.booleanValue())
						)
						.tooltip(pack.getDescription())
			);
		builder.build(adder::add);
		GridWidget.Adder adder2 = this.experimentToggleList.addBody(new GridWidget().setColumnSpacing(10)).createAdder(2);
		adder2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.applyAndClose()).build());
		adder2.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
		this.experimentToggleList.forEachChild(widget -> {
			ClickableWidget var10000 = this.addDrawableChild(widget);
		});
		this.initTabNavigation();
	}

	private static Text getDataPackName(ResourcePackProfile packProfile) {
		String string = "dataPack." + packProfile.getName() + ".name";
		return (Text)(I18n.hasTranslation(string) ? Text.translatable(string) : packProfile.getDisplayName());
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	private void applyAndClose() {
		List<ResourcePackProfile> list = new ArrayList(this.resourcePackManager.getEnabledProfiles());
		List<ResourcePackProfile> list2 = new ArrayList();
		this.experiments.forEach((pack, enabled) -> {
			list.remove(pack);
			if (enabled) {
				list2.add(pack);
			}
		});
		list.addAll(Lists.reverse(list2));
		this.resourcePackManager.setEnabledProfiles(list.stream().map(ResourcePackProfile::getName).toList());
		this.applier.accept(this.resourcePackManager);
	}

	@Override
	protected void initTabNavigation() {
		this.experimentToggleList.refreshPositions();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
		int i = 32;
		drawTexture(
			matrices,
			0,
			this.experimentToggleList.getHeaderHeight(),
			0.0F,
			0.0F,
			this.width,
			this.height - this.experimentToggleList.getHeaderHeight() - this.experimentToggleList.getFooterHeight(),
			32,
			32
		);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
