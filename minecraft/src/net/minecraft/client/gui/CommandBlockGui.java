package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateCommandBlockServerPacket;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class CommandBlockGui extends AbstractCommandBlockGui {
	private final CommandBlockBlockEntity field_2865;
	private ButtonWidget typeButton;
	private ButtonWidget conditionalModeButton;
	private ButtonWidget redstoneTriggerButton;
	private CommandBlockBlockEntity.Type type = CommandBlockBlockEntity.Type.NORMAL;
	private boolean conditional;
	private boolean alwaysActive;

	public CommandBlockGui(CommandBlockBlockEntity commandBlockBlockEntity) {
		this.field_2865 = commandBlockBlockEntity;
	}

	@Override
	CommandBlockExecutor getCommandExecutor() {
		return this.field_2865.getCommandExecutor();
	}

	@Override
	int method_2364() {
		return 135;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.typeButton = this.addButton(new ButtonWidget(5, this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.translate("advMode.mode.sequence")) {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockGui.this.method_2450();
				CommandBlockGui.this.method_2451();
			}
		});
		this.conditionalModeButton = this.addButton(new ButtonWidget(6, this.width / 2 - 50, 165, 100, 20, I18n.translate("advMode.mode.unconditional")) {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockGui.this.conditional = !CommandBlockGui.this.conditional;
				CommandBlockGui.this.method_2452();
			}
		});
		this.redstoneTriggerButton = this.addButton(new ButtonWidget(7, this.width / 2 + 50 + 4, 165, 100, 20, I18n.translate("advMode.mode.redstoneTriggered")) {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockGui.this.alwaysActive = !CommandBlockGui.this.alwaysActive;
				CommandBlockGui.this.method_2454();
			}
		});
		this.doneButton.enabled = false;
		this.toggleTrackingOutputButton.enabled = false;
		this.typeButton.enabled = false;
		this.conditionalModeButton.enabled = false;
		this.redstoneTriggerButton.enabled = false;
	}

	public void method_2457() {
		CommandBlockExecutor commandBlockExecutor = this.field_2865.getCommandExecutor();
		this.consoleCommandTextField.setText(commandBlockExecutor.getCommand());
		this.field_2752 = commandBlockExecutor.isTrackingOutput();
		this.type = this.field_2865.getType();
		this.conditional = this.field_2865.isConditionalCommandBlock();
		this.alwaysActive = this.field_2865.isAuto();
		this.method_2368();
		this.method_2451();
		this.method_2452();
		this.method_2454();
		this.doneButton.enabled = true;
		this.toggleTrackingOutputButton.enabled = true;
		this.typeButton.enabled = true;
		this.conditionalModeButton.enabled = true;
		this.redstoneTriggerButton.enabled = true;
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		super.onScaleChanged(minecraftClient, i, j);
		this.method_2368();
		this.method_2451();
		this.method_2452();
		this.method_2454();
		this.doneButton.enabled = true;
		this.toggleTrackingOutputButton.enabled = true;
		this.typeButton.enabled = true;
		this.conditionalModeButton.enabled = true;
		this.redstoneTriggerButton.enabled = true;
	}

	@Override
	protected void method_2352(CommandBlockExecutor commandBlockExecutor) {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new UpdateCommandBlockServerPacket(
					new BlockPos(commandBlockExecutor.getPos()),
					this.consoleCommandTextField.getText(),
					this.type,
					commandBlockExecutor.isTrackingOutput(),
					this.conditional,
					this.alwaysActive
				)
			);
	}

	private void method_2451() {
		switch (this.type) {
			case CHAIN:
				this.typeButton.text = I18n.translate("advMode.mode.sequence");
				break;
			case REPEATING:
				this.typeButton.text = I18n.translate("advMode.mode.auto");
				break;
			case NORMAL:
				this.typeButton.text = I18n.translate("advMode.mode.redstone");
		}
	}

	private void method_2450() {
		switch (this.type) {
			case CHAIN:
				this.type = CommandBlockBlockEntity.Type.REPEATING;
				break;
			case REPEATING:
				this.type = CommandBlockBlockEntity.Type.NORMAL;
				break;
			case NORMAL:
				this.type = CommandBlockBlockEntity.Type.CHAIN;
		}
	}

	private void method_2452() {
		if (this.conditional) {
			this.conditionalModeButton.text = I18n.translate("advMode.mode.conditional");
		} else {
			this.conditionalModeButton.text = I18n.translate("advMode.mode.unconditional");
		}
	}

	private void method_2454() {
		if (this.alwaysActive) {
			this.redstoneTriggerButton.text = I18n.translate("advMode.mode.autoexec.bat");
		} else {
			this.redstoneTriggerButton.text = I18n.translate("advMode.mode.redstoneTriggered");
		}
	}
}
