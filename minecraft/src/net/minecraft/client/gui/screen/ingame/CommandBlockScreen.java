package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.text.Text;
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
		this.modeButton = this.addDrawableChild(
			CyclingButtonWidget.<CommandBlockBlockEntity.Type>builder(value -> {
					return switch (value) {
						case SEQUENCE -> Text.translatable("advMode.mode.sequence");
						case AUTO -> Text.translatable("advMode.mode.auto");
						case REDSTONE -> Text.translatable("advMode.mode.redstone");
					};
				})
				.values(CommandBlockBlockEntity.Type.values())
				.omitKeyText()
				.initially(this.mode)
				.build(this.width / 2 - 50 - 100 - 4, 165, 100, 20, Text.translatable("advMode.mode"), (button, mode) -> this.mode = mode)
		);
		this.conditionalModeButton = this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(Text.translatable("advMode.mode.conditional"), Text.translatable("advMode.mode.unconditional"))
				.omitKeyText()
				.initially(this.conditional)
				.build(this.width / 2 - 50, 165, 100, 20, Text.translatable("advMode.type"), (button, conditional) -> this.conditional = conditional)
		);
		this.redstoneTriggerButton = this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(Text.translatable("advMode.mode.autoexec.bat"), Text.translatable("advMode.mode.redstoneTriggered"))
				.omitKeyText()
				.initially(this.autoActivate)
				.build(this.width / 2 + 50 + 4, 165, 100, 20, Text.translatable("advMode.triggering"), (button, autoActivate) -> this.autoActivate = autoActivate)
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
		this.setPreviousOutputText(bl);
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
					BlockPos.ofFloored(commandExecutor.getPos()),
					this.consoleCommandTextField.getText(),
					this.mode,
					commandExecutor.isTrackingOutput(),
					this.conditional,
					this.autoActivate
				)
			);
	}
}
