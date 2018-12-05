package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2871;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.sortme.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class CommandBlockMinecartGui extends AbstractCommandBlockGui {
	private final CommandBlockExecutor field_2976;

	public CommandBlockMinecartGui(CommandBlockExecutor commandBlockExecutor) {
		this.field_2976 = commandBlockExecutor;
	}

	@Override
	public CommandBlockExecutor method_2351() {
		return this.field_2976;
	}

	@Override
	int method_2364() {
		return 150;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.field_2752 = this.method_2351().isTrackingOutput();
		this.method_2368();
		this.consoleCommandTextField.setText(this.method_2351().getCommand());
	}

	@Override
	protected void method_2352(CommandBlockExecutor commandBlockExecutor) {
		if (commandBlockExecutor instanceof CommandBlockMinecartEntity.class_1698) {
			CommandBlockMinecartEntity.class_1698 lv = (CommandBlockMinecartEntity.class_1698)commandBlockExecutor;
			this.client
				.getNetworkHandler()
				.sendPacket(new class_2871(lv.method_7569().getEntityId(), this.consoleCommandTextField.getText(), commandBlockExecutor.isTrackingOutput()));
		}
	}
}
