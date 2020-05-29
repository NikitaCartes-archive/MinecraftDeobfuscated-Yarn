package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelInfo;

@Environment(EnvType.CLIENT)
public class DatapackFailureScreen extends Screen {
	private final String name;
	private final List<class_5348> wrappedText = Lists.<class_5348>newArrayList();
	@Nullable
	private final LevelInfo levelInfo;

	public DatapackFailureScreen(String name, @Nullable LevelInfo levelInfo) {
		super(new TranslatableText("datapackFailure.title"));
		this.name = name;
		this.levelInfo = levelInfo;
	}

	@Override
	protected void init() {
		super.init();
		this.wrappedText.clear();
		this.wrappedText.addAll(this.textRenderer.wrapLines(this.getTitle(), this.width - 50));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 96,
				150,
				20,
				new TranslatableText("datapackFailure.safeMode"),
				buttonWidget -> this.client.startIntegratedServer(this.name, this.levelInfo, true)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableText("gui.toTitle"), buttonWidget -> this.client.openScreen(null)
			)
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		int i = 70;

		for (class_5348 lv : this.wrappedText) {
			this.drawCenteredText(matrices, this.textRenderer, lv, this.width / 2, i, 16777215);
			i += 9;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
