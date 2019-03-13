package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.sortme.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class CommandBlockMinecartScreen extends AbstractCommandBlockScreen {
	private final CommandBlockExecutor commandExecutor;

	public CommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
		this.commandExecutor = commandBlockExecutor;
	}

	@Override
	public CommandBlockExecutor getCommandExecutor() {
		return this.commandExecutor;
	}

	@Override
	int method_2364() {
		return 150;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.trackingOutput = this.getCommandExecutor().isTrackingOutput();
		this.updateTrackedOutput();
		this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
	}

	@Override
	protected void syncSettingsToServer(CommandBlockExecutor commandBlockExecutor) {
		if (commandBlockExecutor instanceof CommandBlockMinecartEntity.class_1698) {
			CommandBlockMinecartEntity.class_1698 lv = (CommandBlockMinecartEntity.class_1698)commandBlockExecutor;
			this.client
				.method_1562()
				.method_2883(
					new UpdateCommandBlockMinecartC2SPacket(lv.method_7569().getEntityId(), this.consoleCommandTextField.getText(), commandBlockExecutor.isTrackingOutput())
				);
		}
	}
}
