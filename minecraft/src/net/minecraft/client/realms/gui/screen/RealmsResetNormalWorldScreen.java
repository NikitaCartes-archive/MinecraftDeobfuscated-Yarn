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
	private final Consumer<ResetWorldInfo> field_27938;
	private RealmsLabel titleLabel;
	private TextFieldWidget seedEdit;
	private RealmsWorldGeneratorType field_27939 = RealmsWorldGeneratorType.DEFAULT;
	private boolean field_27940 = true;
	private final Text field_24206;

	public RealmsResetNormalWorldScreen(Consumer<ResetWorldInfo> consumer, Text text) {
		this.field_27938 = consumer;
		this.field_24206 = text;
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
			CyclingButtonWidget.<RealmsWorldGeneratorType>method_32606(RealmsWorldGeneratorType::getText)
				.method_32624(RealmsWorldGeneratorType.values())
				.value(this.field_27939)
				.build(
					this.width / 2 - 102,
					row(4),
					205,
					20,
					new TranslatableText("selectWorld.mapType"),
					(cyclingButtonWidget, realmsWorldGeneratorType) -> this.field_27939 = realmsWorldGeneratorType
				)
		);
		this.addButton(
			CyclingButtonWidget.method_32613(this.field_27940)
				.build(
					this.width / 2 - 102, row(6) - 2, 205, 20, new TranslatableText("selectWorld.mapFeatures"), (cyclingButtonWidget, boolean_) -> this.field_27940 = boolean_
				)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				row(12),
				97,
				20,
				this.field_24206,
				buttonWidget -> this.field_27938.accept(new ResetWorldInfo(this.seedEdit.getText(), this.field_27939, this.field_27940))
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
		this.field_27938.accept(null);
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
