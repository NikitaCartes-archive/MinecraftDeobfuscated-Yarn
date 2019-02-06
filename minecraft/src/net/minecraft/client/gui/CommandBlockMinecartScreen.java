package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.sortme.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class CommandBlockMinecartScreen extends AbstractCommandBlockScreen {
	private final CommandBlockExecutor field_2976;

	public CommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
		this.field_2976 = commandBlockExecutor;
	}

	@Override
	public CommandBlockExecutor getCommandExecutor() {
		return this.field_2976;
	}

	@Override
	int method_2364() {
		return 150;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.field_2752 = this.getCommandExecutor().isTrackingOutput();
		this.method_2368();
		this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
	}

	@Override
	protected void method_2352(CommandBlockExecutor commandBlockExecutor) {
		if (commandBlockExecutor instanceof CommandBlockMinecartEntity.class_1698) {
			CommandBlockMinecartEntity.class_1698 lv = (CommandBlockMinecartEntity.class_1698)commandBlockExecutor;
			this.client
				.getNetworkHandler()
				.sendPacket(
					new UpdateCommandBlockMinecartC2SPacket(lv.method_7569().getEntityId(), this.consoleCommandTextField.getText(), commandBlockExecutor.isTrackingOutput())
				);
		}
	}
}
