package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DatapackFailureScreen extends Screen {
	private final List<StringRenderable> wrappedText = Lists.<StringRenderable>newArrayList();
	private final Runnable field_25452;

	public DatapackFailureScreen(Runnable runnable) {
		super(new TranslatableText("datapackFailure.title"));
		this.field_25452 = runnable;
	}

	@Override
	protected void init() {
		super.init();
		this.wrappedText.clear();
		this.wrappedText.addAll(this.textRenderer.wrapLines(this.getTitle(), this.width - 50));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableText("datapackFailure.safeMode"), buttonWidget -> this.field_25452.run()
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

		for (StringRenderable stringRenderable : this.wrappedText) {
			this.drawCenteredText(matrices, this.textRenderer, stringRenderable, this.width / 2, i, 16777215);
			i += 9;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
