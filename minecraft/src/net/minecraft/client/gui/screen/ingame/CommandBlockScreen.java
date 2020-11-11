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
			CyclingButtonWidget.<CommandBlockBlockEntity.Type>method_32606(type -> {
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
				.method_32624(CommandBlockBlockEntity.Type.values())
				.method_32616()
				.value(this.mode)
				.build(this.width / 2 - 50 - 100 - 4, 165, 100, 20, new TranslatableText("advMode.mode"), (cyclingButtonWidget, type) -> this.mode = type)
		);
		this.conditionalModeButton = this.addButton(
			CyclingButtonWidget.method_32607(new TranslatableText("advMode.mode.conditional"), new TranslatableText("advMode.mode.unconditional"))
				.method_32616()
				.value(this.conditional)
				.build(this.width / 2 - 50, 165, 100, 20, new TranslatableText("advMode.type"), (cyclingButtonWidget, boolean_) -> this.conditional = boolean_)
		);
		this.redstoneTriggerButton = this.addButton(
			CyclingButtonWidget.method_32607(new TranslatableText("advMode.mode.autoexec.bat"), new TranslatableText("advMode.mode.redstoneTriggered"))
				.method_32616()
				.value(this.autoActivate)
				.build(this.width / 2 + 50 + 4, 165, 100, 20, new TranslatableText("advMode.triggering"), (cyclingButtonWidget, boolean_) -> this.autoActivate = boolean_)
		);
		this.method_32647(false);
	}

	private void method_32647(boolean bl) {
		this.doneButton.active = bl;
		this.toggleTrackingOutputButton.active = bl;
		this.modeButton.active = bl;
		this.conditionalModeButton.active = bl;
		this.redstoneTriggerButton.active = bl;
	}

	public void updateCommandBlock() {
		CommandBlockExecutor commandBlockExecutor = this.blockEntity.getCommandExecutor();
		this.consoleCommandTextField.setText(commandBlockExecutor.getCommand());
		this.trackingOutput = commandBlockExecutor.isTrackingOutput();
		this.mode = this.blockEntity.getCommandBlockType();
		this.conditional = this.blockEntity.isConditionalCommandBlock();
		this.autoActivate = this.blockEntity.isAuto();
		this.toggleTrackingOutputButton.method_32605(this.trackingOutput);
		this.modeButton.method_32605(this.mode);
		this.conditionalModeButton.method_32605(this.conditional);
		this.redstoneTriggerButton.method_32605(this.autoActivate);
		this.method_32647(true);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.method_32647(true);
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
