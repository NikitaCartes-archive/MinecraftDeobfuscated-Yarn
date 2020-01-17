package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class CommandBlockScreen extends AbstractCommandBlockScreen {
	private final CommandBlockBlockEntity blockEntity;
	private ButtonWidget modeButton;
	private ButtonWidget conditionalModeButton;
	private ButtonWidget redstoneTriggerButton;
	private CommandBlockBlockEntity.Type mode = CommandBlockBlockEntity.Type.REDSTONE;
	private boolean conditional;
	private boolean autoActivate;

	public CommandBlockScreen(CommandBlockBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}

	@Override
	CommandBlockExecutor getCommandExecutor() {
		return this.blockEntity.getCommandExecutor();
	}

	@Override
	int getTrackOutputButtonHeight() {
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

	public void updateCommandBlock() {
		CommandBlockExecutor commandBlockExecutor = this.blockEntity.getCommandExecutor();
		this.consoleCommandTextField.setText(commandBlockExecutor.getCommand());
		this.trackingOutput = commandBlockExecutor.isTrackingOutput();
		this.mode = this.blockEntity.getCommandBlockType();
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
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
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
	protected void syncSettingsToServer(CommandBlockExecutor commandExecutor) {
		this.minecraft
			.getNetworkHandler()
			.sendPacket(
				new UpdateCommandBlockC2SPacket(
					new BlockPos(commandExecutor.getPos()),
					this.consoleCommandTextField.getText(),
					this.mode,
					commandExecutor.isTrackingOutput(),
					this.conditional,
					this.autoActivate
				)
			);
	}

	private void updateMode() {
		switch (this.mode) {
			case SEQUENCE:
				this.modeButton.setMessage(I18n.translate("advMode.mode.sequence"));
				break;
			case AUTO:
				this.modeButton.setMessage(I18n.translate("advMode.mode.auto"));
				break;
			case REDSTONE:
				this.modeButton.setMessage(I18n.translate("advMode.mode.redstone"));
		}
	}

	private void cycleType() {
		switch (this.mode) {
			case SEQUENCE:
				this.mode = CommandBlockBlockEntity.Type.AUTO;
				break;
			case AUTO:
				this.mode = CommandBlockBlockEntity.Type.REDSTONE;
				break;
			case REDSTONE:
				this.mode = CommandBlockBlockEntity.Type.SEQUENCE;
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
