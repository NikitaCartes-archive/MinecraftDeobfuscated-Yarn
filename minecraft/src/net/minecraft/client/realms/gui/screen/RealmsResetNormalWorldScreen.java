package net.minecraft.client.realms.gui.screen;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
	private static final Text RESET_SEED_TEXT = Text.translatable("mco.reset.world.seed");
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
	public void tick() {
		this.seedEdit.tick();
		super.tick();
	}

	@Override
	public void init() {
		this.seedEdit = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, Text.translatable("mco.reset.world.seed"));
		this.seedEdit.setMaxLength(32);
		this.addSelectableChild(this.seedEdit);
		this.setInitialFocus(this.seedEdit);
		this.addDrawableChild(
			CyclingButtonWidget.<RealmsWorldGeneratorType>builder(RealmsWorldGeneratorType::getText)
				.values(RealmsWorldGeneratorType.values())
				.initially(this.generatorType)
				.build(this.width / 2 - 102, row(4), 205, 20, Text.translatable("selectWorld.mapType"), (button, generatorType) -> this.generatorType = generatorType)
		);
		this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.mapFeatures)
				.build(this.width / 2 - 102, row(6) - 2, 205, 20, Text.translatable("selectWorld.mapFeatures"), (button, mapFeatures) -> this.mapFeatures = mapFeatures)
		);
		this.addDrawableChild(
			ButtonWidget.builder(this.parentTitle, button -> this.callback.accept(new ResetWorldInfo(this.seedEdit.getText(), this.generatorType, this.mapFeatures)))
				.dimensions(this.width / 2 - 102, row(12), 97, 20)
				.build()
		);
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 + 8, row(12), 97, 20).build());
	}

	@Override
	public void close() {
		this.callback.accept(null);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
		this.textRenderer.draw(matrices, RESET_SEED_TEXT, (float)(this.width / 2 - 100), (float)row(1), 10526880);
		this.seedEdit.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
