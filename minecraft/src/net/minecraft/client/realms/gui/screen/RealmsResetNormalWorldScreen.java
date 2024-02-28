package net.minecraft.client.realms.gui.screen;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.ExperimentsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
	private static final Text RESET_SEED_TEXT = Text.translatable("mco.reset.world.seed");
	public static final Text TITLE = Text.translatable("mco.reset.world.generate");
	private static final int field_45278 = 10;
	private static final int field_45279 = 210;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final Consumer<ResetWorldInfo> callback;
	private TextFieldWidget seedEdit;
	private RealmsWorldGeneratorType generatorType = RealmsWorldGeneratorType.DEFAULT;
	private boolean mapFeatures = true;
	private final Set<String> experiments = new HashSet();
	private final Text parentTitle;

	public RealmsResetNormalWorldScreen(Consumer<ResetWorldInfo> callback, Text parentTitle) {
		super(TITLE);
		this.callback = callback;
		this.parentTitle = parentTitle;
	}

	@Override
	public void init() {
		this.seedEdit = new TextFieldWidget(this.textRenderer, 210, 20, Text.translatable("mco.reset.world.seed"));
		this.seedEdit.setMaxLength(32);
		this.layout.addHeader(this.title, this.textRenderer);
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical()).spacing(10);
		directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.seedEdit, RESET_SEED_TEXT));
		directionalLayoutWidget.add(
			CyclingButtonWidget.<RealmsWorldGeneratorType>builder(RealmsWorldGeneratorType::getText)
				.values(RealmsWorldGeneratorType.values())
				.initially(this.generatorType)
				.build(0, 0, 210, 20, Text.translatable("selectWorld.mapType"), (button, generatorType) -> this.generatorType = generatorType)
		);
		directionalLayoutWidget.add(
			CyclingButtonWidget.onOffBuilder(this.mapFeatures)
				.build(0, 0, 210, 20, Text.translatable("selectWorld.mapFeatures"), (button, mapFeatures) -> this.mapFeatures = mapFeatures)
		);
		this.addExperimentsButton(directionalLayoutWidget);
		DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(10));
		directionalLayoutWidget2.add(ButtonWidget.builder(this.parentTitle, button -> this.callback.accept(this.createResetWorldInfo())).build());
		directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.seedEdit);
	}

	private void addExperimentsButton(DirectionalLayoutWidget layout) {
		ResourcePackManager resourcePackManager = VanillaDataPackProvider.createClientManager();
		resourcePackManager.scanPacks();
		layout.add(
			ButtonWidget.builder(
					Text.translatable("selectWorld.experiments"), button -> this.client.setScreen(new ExperimentsScreen(this, resourcePackManager, packManager -> {
							this.experiments.clear();

							for (ResourcePackProfile resourcePackProfile : packManager.getEnabledProfiles()) {
								if (resourcePackProfile.getSource() == ResourcePackSource.FEATURE) {
									this.experiments.add(resourcePackProfile.getId());
								}
							}

							this.client.setScreen(this);
						}))
				)
				.width(210)
				.build()
		);
	}

	private ResetWorldInfo createResetWorldInfo() {
		return new ResetWorldInfo(this.seedEdit.getText(), this.generatorType, this.mapFeatures, this.experiments);
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	@Override
	public void close() {
		this.callback.accept(null);
	}
}
