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
	protected void onInitialized() {
		super.onInitialized();
		this.modeButton = this.addButton(new ButtonWidget(this.screenWidth / 2 - 50 - 100 - 4, 165, 100, 20, I18n.translate("advMode.mode.sequence")) {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockScreen.this.cycleType();
				CommandBlockScreen.this.updateMode();
			}
		});
		this.conditionalModeButton = this.addButton(new ButtonWidget(this.screenWidth / 2 - 50, 165, 100, 20, I18n.translate("advMode.mode.unconditional")) {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockScreen.this.conditional = !CommandBlockScreen.this.conditional;
				CommandBlockScreen.this.updateConditionalMode();
			}
		});
		this.redstoneTriggerButton = this.addButton(new ButtonWidget(this.screenWidth / 2 + 50 + 4, 165, 100, 20, I18n.translate("advMode.mode.redstoneTriggered")) {
			@Override
			public void onPressed(double d, double e) {
				CommandBlockScreen.this.autoActivate = !CommandBlockScreen.this.autoActivate;
				CommandBlockScreen.this.updateActivationMode();
			}
		});
		this.doneButton.enabled = false;
		this.toggleTrackingOutputButton.enabled = false;
		this.modeButton.enabled = false;
		this.conditionalModeButton.enabled = false;
		this.redstoneTriggerButton.enabled = false;
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
		this.doneButton.enabled = true;
		this.toggleTrackingOutputButton.enabled = true;
		this.modeButton.enabled = true;
		this.conditionalModeButton.enabled = true;
		this.redstoneTriggerButton.enabled = true;
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		super.onScaleChanged(minecraftClient, i, j);
		this.updateTrackedOutput();
		this.updateMode();
		this.updateConditionalMode();
		this.updateActivationMode();
		this.doneButton.enabled = true;
		this.toggleTrackingOutputButton.enabled = true;
		this.modeButton.enabled = true;
		this.conditionalModeButton.enabled = true;
		this.redstoneTriggerButton.enabled = true;
	}

	@Override
	protected void syncSettingsToServer(CommandBlockExecutor commandBlockExecutor) {
		this.client
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
				this.modeButton.setText(I18n.translate("advMode.mode.sequence"));
				break;
			case field_11923:
				this.modeButton.setText(I18n.translate("advMode.mode.auto"));
				break;
			case field_11924:
				this.modeButton.setText(I18n.translate("advMode.mode.redstone"));
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
			this.conditionalModeButton.setText(I18n.translate("advMode.mode.conditional"));
		} else {
			this.conditionalModeButton.setText(I18n.translate("advMode.mode.unconditional"));
		}
	}

	private void updateActivationMode() {
		if (this.autoActivate) {
			this.redstoneTriggerButton.setText(I18n.translate("advMode.mode.autoexec.bat"));
		} else {
			this.redstoneTriggerButton.setText(I18n.translate("advMode.mode.redstoneTriggered"));
		}
	}
}
