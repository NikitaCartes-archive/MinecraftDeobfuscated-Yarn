package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.task.LongRunningTask;

@Environment(EnvType.CLIENT)
public class RealmsLongRunningTickableTaskScreen extends RealmsLongRunningMcoTaskScreen {
	private final LongRunningTask tickableTask;

	public RealmsLongRunningTickableTaskScreen(Screen parent, LongRunningTask tickableTask) {
		super(parent, tickableTask);
		this.tickableTask = tickableTask;
	}

	@Override
	public void tick() {
		super.tick();
		this.tickableTask.tick();
	}

	@Override
	protected void onCancel() {
		this.tickableTask.abortTask();
		super.onCancel();
	}
}
