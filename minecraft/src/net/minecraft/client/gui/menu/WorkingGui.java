package net.minecraft.client.gui.menu;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ProgressListener;

@Environment(EnvType.CLIENT)
public class WorkingGui extends Gui implements ProgressListener {
	private String title = "";
	private String task = "";
	private int progress;
	private boolean done;

	@Override
	public boolean canClose() {
		return false;
	}

	@Override
	public void method_15412(TextComponent textComponent) {
		this.method_15413(textComponent);
	}

	@Override
	public void method_15413(TextComponent textComponent) {
		this.title = textComponent.getFormattedText();
		this.method_15414(new TranslatableTextComponent("progress.working"));
	}

	@Override
	public void method_15414(TextComponent textComponent) {
		this.task = textComponent.getFormattedText();
		this.progressStagePercentage(0);
	}

	@Override
	public void progressStagePercentage(int i) {
		this.progress = i;
	}

	@Override
	public void method_15411() {
		this.done = true;
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.done) {
			if (!this.client.isConnectedToRealms()) {
				this.client.openGui(null);
			}
		} else {
			this.drawBackground();
			this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
			if (!Objects.equals(this.task, "")) {
				if (this.progress == 0) {
					this.drawStringCentered(this.fontRenderer, this.task, this.width / 2, 90, 16777215);
				} else {
					this.drawStringCentered(this.fontRenderer, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
				}
			}

			super.draw(i, j, f);
		}
	}
}
