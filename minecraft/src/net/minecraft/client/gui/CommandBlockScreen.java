package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class CommandBlockScreen extends AbstractCommandBlockScreen {
	private final CommandBlockBlockEntity blockEntity;
	private ButtonWidget modeButton;
	private ButtonWidget conditionalModeButton;
	private ButtonWidget redstoneTriggerButton;
	private CommandBlockBlockEntity.Type mode = CommandBlockBlockEntity.Type.field_11924;
	private boolean conditional;
	private boolean autoActivate;

	public CommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
		this.blockEntity = commandBlockBlockEntity;
	}

	@Override
	CommandBlockExecutor getCommandExecutor() {
		return this.blockEntity.getCommandExecutor();
	}

	@Override
	int method_2364() {
		return 135;
	}

	@Override
	protected void init() {
		super.init();
		this.modeButton = this.addButton(new ButtonWidget(this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.translate("advMode.mode.sequence"), buttonWidget -> {
			this.cycleType();
			this.updateMode();
		}));
		this.conditionalModeButton = this.addButton(
			new ButtonWidget(this.width / 2 - 50, 165, 100, 20, I18n.translate("advMode.mode.unconditional"), buttonWidget -> {
				this.conditional = !this.conditional;
				this.updateConditionalMode();
			})
		);
		this.redstoneTriggerButton = this.addButton(
			new ButtonWidget(this.width / 2 + 50 + 4, 165, 100, 20, I18n.translate("advMode.mode.redstoneTriggered"), buttonWidget -> {
				this.autoActivate = !this.autoActivate;
				this.updateActivationMode();
			})
		);
		this.doneButton.active = false;
		this.toggleTrackingOutputButton.active = false;
		this.modeButton.active = false;
		this.conditionalModeButton.active = false;
		this.redstoneTriggerButton.active = false;
	}

	public void method_2457() {
		CommandBlockExecutor commandBlockExecutor = this.blockEntity.getCommandExecutor();
		this.consoleCommandTextField.setText(commandBlockExecutor.getCommand());
		this.trackingOutput = commandBlockExecutor.isTrackingOutput();
		this.mode = this.blockEntity.getType();
		this.conditional = this.blockEntity.isConditionalCommandBlock();
		this.autoActivate = this.blockEntity.isAuto();
		this.updateTrackedOutput();
		this.updateMode();
		this.updateConditionalMode();
		this.updateActivationMode();
		this.doneButton.active = true;
		this.toggleTrackingOutputButton.active = true;
		this.modeButton.active = true;
		this.conditionalModeButton.active = true;
		this.redstoneTriggerButton.active = true;
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		super.resize(minecraftClient, i, j);
		this.updateTrackedOutput();
		this.updateMode();
		this.updateConditionalMode();
		this.updateActivationMode();
		this.doneButton.active = true;
		this.toggleTrackingOutputButton.active = true;
		this.modeButton.active = true;
		this.conditionalModeButton.active = true;
		this.redstoneTriggerButton.active = true;
	}

	@Override
	protected void syncSettingsToServer(CommandBlockExecutor commandBlockExecutor) {
		this.minecraft
			.getNetworkHandler()
			.sendPacket(
				new UpdateCommandBlockC2SPacket(
					new BlockPos(commandBlockExecutor.getPos()),
					this.consoleCommandTextField.getText(),
					this.mode,
					commandBlockExecutor.isTrackingOutput(),
					this.conditional,
					this.autoActivate
				)
			);
	}

	private void updateMode() {
		switch (this.mode) {
			case field_11922:
				this.modeButton.setMessage(I18n.translate("advMode.mode.sequence"));
				break;
			case field_11923:
				this.modeButton.setMessage(I18n.translate("advMode.mode.auto"));
				break;
			case field_11924:
				this.modeButton.setMessage(I18n.translate("advMode.mode.redstone"));
		}
	}

	private void cycleType() {
		switch (this.mode) {
			case field_11922:
				this.mode = CommandBlockBlockEntity.Type.field_11923;
				break;
			case field_11923:
				this.mode = CommandBlockBlockEntity.Type.field_11924;
				break;
			case field_11924:
				this.mode = CommandBlockBlockEntity.Type.field_11922;
		}
	}

	private void updateConditionalMode() {
		if (this.conditional) {
			this.conditionalModeButton.setMessage(I18n.translate("advMode.mode.conditional"));
		} else {
			this.conditionalModeButton.setMessage(I18n.translate("advMode.mode.unconditional"));
		}
	}

	private void updateActivationMode() {
		if (this.autoActivate) {
			this.redstoneTriggerButton.setMessage(I18n.translate("advMode.mode.autoexec.bat"));
		} else {
			this.redstoneTriggerButton.setMessage(I18n.translate("advMode.mode.redstoneTriggered"));
		}
	}
}
