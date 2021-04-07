package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public class CommandBlockScreen extends AbstractCommandBlockScreen {
	private final CommandBlockBlockEntity blockEntity;
	private CyclingButtonWidget<CommandBlockBlockEntity.Type> modeButton;
	private CyclingButtonWidget<Boolean> conditionalModeButton;
	private CyclingButtonWidget<Boolean> redstoneTriggerButton;
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
		this.modeButton = this.addButton(
			CyclingButtonWidget.<CommandBlockBlockEntity.Type>builder(type -> {
					switch (type) {
						case SEQUENCE:
							return new TranslatableText("advMode.mode.sequence");
						case AUTO:
							return new TranslatableText("advMode.mode.auto");
						case REDSTONE:
						default:
							return new TranslatableText("advMode.mode.redstone");
					}
				})
				.values(CommandBlockBlockEntity.Type.values())
				.omitKeyText()
				.initially(this.mode)
				.build(this.width / 2 - 50 - 100 - 4, 165, 100, 20, new TranslatableText("advMode.mode"), (cyclingButtonWidget, type) -> this.mode = type)
		);
		this.conditionalModeButton = this.addButton(
			CyclingButtonWidget.onOffBuilder(new TranslatableText("advMode.mode.conditional"), new TranslatableText("advMode.mode.unconditional"))
				.omitKeyText()
				.initially(this.conditional)
				.build(this.width / 2 - 50, 165, 100, 20, new TranslatableText("advMode.type"), (cyclingButtonWidget, boolean_) -> this.conditional = boolean_)
		);
		this.redstoneTriggerButton = this.addButton(
			CyclingButtonWidget.onOffBuilder(new TranslatableText("advMode.mode.autoexec.bat"), new TranslatableText("advMode.mode.redstoneTriggered"))
				.omitKeyText()
				.initially(this.autoActivate)
				.build(this.width / 2 + 50 + 4, 165, 100, 20, new TranslatableText("advMode.triggering"), (cyclingButtonWidget, boolean_) -> this.autoActivate = boolean_)
		);
		this.setButtonsActive(false);
	}

	private void setButtonsActive(boolean active) {
		this.doneButton.active = active;
		this.toggleTrackingOutputButton.active = active;
		this.modeButton.active = active;
		this.conditionalModeButton.active = active;
		this.redstoneTriggerButton.active = active;
	}

	public void updateCommandBlock() {
		CommandBlockExecutor commandBlockExecutor = this.blockEntity.getCommandExecutor();
		this.consoleCommandTextField.setText(commandBlockExecutor.getCommand());
		boolean bl = commandBlockExecutor.isTrackingOutput();
		this.mode = this.blockEntity.getCommandBlockType();
		this.conditional = this.blockEntity.isConditionalCommandBlock();
		this.autoActivate = this.blockEntity.isAuto();
		this.toggleTrackingOutputButton.setValue(bl);
		this.modeButton.setValue(this.mode);
		this.conditionalModeButton.setValue(this.conditional);
		this.redstoneTriggerButton.setValue(this.autoActivate);
		this.method_32642(bl);
		this.setButtonsActive(true);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.setButtonsActive(true);
	}

	@Override
	protected void syncSettingsToServer(CommandBlockExecutor commandExecutor) {
		this.client
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
}
