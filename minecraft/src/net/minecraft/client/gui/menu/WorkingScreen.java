package net.minecraft.client.gui.menu;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ProgressListener;

@Environment(EnvType.CLIENT)
public class WorkingScreen extends Screen implements ProgressListener {
	private String title = "";
	private String task = "";
	private int progress;
	private boolean done;

	public WorkingScreen() {
		super(NarratorManager.field_18967);
	}

	@Override
	public boolean shouldCloseOnEsc() {
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
	public void setDone() {
		this.done = true;
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.done) {
			if (!this.minecraft.isConnectedToRealms()) {
				this.minecraft.openScreen(null);
			}
		} else {
			this.renderBackground();
			this.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
			if (!Objects.equals(this.task, "") && this.progress != 0) {
				this.drawCenteredString(this.font, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
			}

			super.render(i, j, f);
		}
	}
}
