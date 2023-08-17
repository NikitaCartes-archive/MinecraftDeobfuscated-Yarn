package net.minecraft.client.realms.gui.screen;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
	private static final Text RESET_SEED_TEXT = Text.translatable("mco.reset.world.seed");
	private static final int field_45278 = 10;
	private static final int field_45279 = 210;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final Consumer<ResetWorldInfo> callback;
	private TextFieldWidget seedEdit;
	private RealmsWorldGeneratorType generatorType = RealmsWorldGeneratorType.DEFAULT;
	private boolean mapFeatures = true;
	private final Text parentTitle;

	public RealmsResetNormalWorldScreen(Consumer<ResetWorldInfo> callback, Text parentTitle) {
		super(Text.translatable("mco.reset.world.generate"));
		this.callback = callback;
		this.parentTitle = parentTitle;
	}

	@Override
	public void init() {
		this.seedEdit = new TextFieldWidget(this.textRenderer, 210, 20, Text.translatable("mco.reset.world.seed"));
		this.seedEdit.setMaxLength(32);
		this.setInitialFocus(this.seedEdit);
		this.layout.addHeader(new TextWidget(this.title, this.textRenderer));
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
		DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(10));
		directionalLayoutWidget2.add(ButtonWidget.builder(this.parentTitle, button -> this.callback.accept(this.createResetWorldInfo())).build());
		directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	private ResetWorldInfo createResetWorldInfo() {
		return new ResetWorldInfo(this.seedEdit.getText(), this.generatorType, this.mapFeatures);
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
