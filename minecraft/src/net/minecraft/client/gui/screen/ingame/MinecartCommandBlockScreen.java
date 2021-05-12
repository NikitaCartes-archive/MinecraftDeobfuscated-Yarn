package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.world.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class MinecartCommandBlockScreen extends AbstractCommandBlockScreen {
	private final CommandBlockExecutor commandExecutor;

	public MinecartCommandBlockScreen(CommandBlockExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

	@Override
	public CommandBlockExecutor getCommandExecutor() {
		return this.commandExecutor;
	}

	@Override
	int getTrackOutputButtonHeight() {
		return 150;
	}

	@Override
	protected void init() {
		super.init();
		this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
	}

	@Override
	protected void syncSettingsToServer(CommandBlockExecutor commandExecutor) {
		if (commandExecutor instanceof CommandBlockMinecartEntity.CommandExecutor commandExecutor2) {
			this.client
				.getNetworkHandler()
				.sendPacket(
					new UpdateCommandBlockMinecartC2SPacket(commandExecutor2.getMinecart().getId(), this.consoleCommandTextField.getText(), commandExecutor.isTrackingOutput())
				);
		}
	}
}
