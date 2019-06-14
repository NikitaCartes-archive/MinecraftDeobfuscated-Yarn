package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.world.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class MinecartCommandBlockScreen extends AbstractCommandBlockScreen {
	private final CommandBlockExecutor commandExecutor;

	public MinecartCommandBlockScreen(CommandBlockExecutor commandBlockExecutor) {
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
	protected void init() {
		super.init();
		this.trackingOutput = this.getCommandExecutor().isTrackingOutput();
		this.updateTrackedOutput();
		this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
	}

	@Override
	protected void syncSettingsToServer(CommandBlockExecutor commandBlockExecutor) {
		if (commandBlockExecutor instanceof CommandBlockMinecartEntity.CommandExecutor) {
			CommandBlockMinecartEntity.CommandExecutor commandExecutor = (CommandBlockMinecartEntity.CommandExecutor)commandBlockExecutor;
			this.minecraft
				.method_1562()
				.sendPacket(
					new UpdateCommandBlockMinecartC2SPacket(
						commandExecutor.getMinecart().getEntityId(), this.consoleCommandTextField.getText(), commandBlockExecutor.isTrackingOutput()
					)
				);
		}
	}
}
