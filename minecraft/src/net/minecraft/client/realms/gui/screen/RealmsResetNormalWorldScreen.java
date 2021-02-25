package net.minecraft.client.realms.gui.screen;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
	private static final Text RESET_SEED_TEXT = new TranslatableText("mco.reset.world.seed");
	private final Consumer<ResetWorldInfo> callback;
	private RealmsLabel titleLabel;
	private TextFieldWidget seedEdit;
	private RealmsWorldGeneratorType generatorType = RealmsWorldGeneratorType.DEFAULT;
	private boolean mapFeatures = true;
	private final Text parentTitle;

	public RealmsResetNormalWorldScreen(Consumer<ResetWorldInfo> callback, Text parentTitle) {
		this.callback = callback;
		this.parentTitle = parentTitle;
	}

	@Override
	public void tick() {
		this.seedEdit.tick();
		super.tick();
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.titleLabel = new RealmsLabel(new TranslatableText("mco.reset.world.generate"), this.width / 2, 17, 16777215);
		this.addChild(this.titleLabel);
		this.seedEdit = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, new TranslatableText("mco.reset.world.seed"));
		this.seedEdit.setMaxLength(32);
		this.addChild(this.seedEdit);
		this.setInitialFocus(this.seedEdit);
		this.addButton(
			CyclingButtonWidget.<RealmsWorldGeneratorType>builder(RealmsWorldGeneratorType::getText)
				.values(RealmsWorldGeneratorType.values())
				.initially(this.generatorType)
				.build(
					this.width / 2 - 102,
					row(4),
					205,
					20,
					new TranslatableText("selectWorld.mapType"),
					(cyclingButtonWidget, generatorType) -> this.generatorType = generatorType
				)
		);
		this.addButton(
			CyclingButtonWidget.onOffBuilder(this.mapFeatures)
				.build(
					this.width / 2 - 102,
					row(6) - 2,
					205,
					20,
					new TranslatableText("selectWorld.mapFeatures"),
					(cyclingButtonWidget, mapFeatures) -> this.mapFeatures = mapFeatures
				)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				row(12),
				97,
				20,
				this.parentTitle,
				buttonWidget -> this.callback.accept(new ResetWorldInfo(this.seedEdit.getText(), this.generatorType, this.mapFeatures))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 8, row(12), 97, 20, ScreenTexts.BACK, buttonWidget -> this.onClose()));
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public void onClose() {
		this.callback.accept(null);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.titleLabel.render(this, matrices);
		this.textRenderer.draw(matrices, RESET_SEED_TEXT, (float)(this.width / 2 - 100), (float)row(1), 10526880);
		this.seedEdit.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
