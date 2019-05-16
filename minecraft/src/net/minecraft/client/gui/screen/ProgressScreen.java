package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ProgressListener;

@Environment(EnvType.CLIENT)
public class ProgressScreen extends Screen implements ProgressListener {
	private String title = "";
	private String task = "";
	private int progress;
	private boolean done;

	public ProgressScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void method_15412(Component component) {
		this.method_15413(component);
	}

	@Override
	public void method_15413(Component component) {
		this.title = component.getFormattedText();
		this.method_15414(new TranslatableComponent("progress.working"));
	}

	@Override
	public void method_15414(Component component) {
		this.task = component.getFormattedText();
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
