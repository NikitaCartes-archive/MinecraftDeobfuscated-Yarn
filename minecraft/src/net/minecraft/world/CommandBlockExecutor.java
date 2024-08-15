package net.minecraft.world;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringHelper;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;

/**
 * A common logic for command-block behaviors shared by
 * {@linkplain net.minecraft.block.entity.CommandBlockBlockEntity
 * command blocks} and {@linkplain net.minecraft.entity.vehicle.CommandBlockMinecartEntity
 * command block minecarts}.
 */
public abstract class CommandBlockExecutor implements CommandOutput {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final Text DEFAULT_NAME = Text.literal("@");
	private long lastExecution = -1L;
	private boolean updateLastExecution = true;
	private int successCount;
	private boolean trackOutput = true;
	@Nullable
	private Text lastOutput;
	private String command = "";
	@Nullable
	private Text customName;

	public int getSuccessCount() {
		return this.successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public Text getLastOutput() {
		return this.lastOutput == null ? ScreenTexts.EMPTY : this.lastOutput;
	}

	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		nbt.putString("Command", this.command);
		nbt.putInt("SuccessCount", this.successCount);
		if (this.customName != null) {
			nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registries));
		}

		nbt.putBoolean("TrackOutput", this.trackOutput);
		if (this.lastOutput != null && this.trackOutput) {
			nbt.putString("LastOutput", Text.Serialization.toJsonString(this.lastOutput, registries));
		}

		nbt.putBoolean("UpdateLastExecution", this.updateLastExecution);
		if (this.updateLastExecution && this.lastExecution > 0L) {
			nbt.putLong("LastExecution", this.lastExecution);
		}

		return nbt;
	}

	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		this.command = nbt.getString("Command");
		this.successCount = nbt.getInt("SuccessCount");
		if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
			this.setCustomName(BlockEntity.tryParseCustomName(nbt.getString("CustomName"), registries));
		} else {
			this.setCustomName(null);
		}

		if (nbt.contains("TrackOutput", NbtElement.BYTE_TYPE)) {
			this.trackOutput = nbt.getBoolean("TrackOutput");
		}

		if (nbt.contains("LastOutput", NbtElement.STRING_TYPE) && this.trackOutput) {
			try {
				this.lastOutput = Text.Serialization.fromJson(nbt.getString("LastOutput"), registries);
			} catch (Throwable var4) {
				this.lastOutput = Text.literal(var4.getMessage());
			}
		} else {
			this.lastOutput = null;
		}

		if (nbt.contains("UpdateLastExecution")) {
			this.updateLastExecution = nbt.getBoolean("UpdateLastExecution");
		}

		if (this.updateLastExecution && nbt.contains("LastExecution")) {
			this.lastExecution = nbt.getLong("LastExecution");
		} else {
			this.lastExecution = -1L;
		}
	}

	public void setCommand(String command) {
		this.command = command;
		this.successCount = 0;
	}

	public String getCommand() {
		return this.command;
	}

	public boolean execute(World world) {
		if (world.isClient || world.getTime() == this.lastExecution) {
			return false;
		} else if ("Searge".equalsIgnoreCase(this.command)) {
			this.lastOutput = Text.literal("#itzlipofutzli");
			this.successCount = 1;
			return true;
		} else {
			this.successCount = 0;
			MinecraftServer minecraftServer = this.getWorld().getServer();
			if (minecraftServer.areCommandBlocksEnabled() && !StringHelper.isEmpty(this.command)) {
				try {
					this.lastOutput = null;
					ServerCommandSource serverCommandSource = this.getSource().withReturnValueConsumer((successful, returnValue) -> {
						if (successful) {
							this.successCount++;
						}
					});
					minecraftServer.getCommandManager().executeWithPrefix(serverCommandSource, this.command);
				} catch (Throwable var6) {
					CrashReport crashReport = CrashReport.create(var6, "Executing command block");
					CrashReportSection crashReportSection = crashReport.addElement("Command to be executed");
					crashReportSection.add("Command", this::getCommand);
					crashReportSection.add("Name", (CrashCallable<String>)(() -> this.getCustomName().getString()));
					throw new CrashException(crashReport);
				}
			}

			if (this.updateLastExecution) {
				this.lastExecution = world.getTime();
			} else {
				this.lastExecution = -1L;
			}

			return true;
		}
	}

	public Text getCustomName() {
		return this.customName != null ? this.customName : DEFAULT_NAME;
	}

	@Nullable
	public Text getCustomNameNullable() {
		return this.customName;
	}

	public void setCustomName(@Nullable Text customName) {
		this.customName = customName;
	}

	@Override
	public void sendMessage(Text message) {
		if (this.trackOutput) {
			this.lastOutput = Text.literal("[" + DATE_FORMAT.format(new Date()) + "] ").append(message);
			this.markDirty();
		}
	}

	public abstract ServerWorld getWorld();

	public abstract void markDirty();

	public void setLastOutput(@Nullable Text lastOutput) {
		this.lastOutput = lastOutput;
	}

	public void setTrackOutput(boolean trackOutput) {
		this.trackOutput = trackOutput;
	}

	public boolean isTrackingOutput() {
		return this.trackOutput;
	}

	public ActionResult interact(PlayerEntity player) {
		if (!player.isCreativeLevelTwoOp()) {
			return ActionResult.PASS;
		} else {
			if (player.getEntityWorld().isClient) {
				player.openCommandBlockMinecartScreen(this);
			}

			return ActionResult.SUCCESS;
		}
	}

	public abstract Vec3d getPos();

	public abstract ServerCommandSource getSource();

	@Override
	public boolean shouldReceiveFeedback() {
		return this.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK) && this.trackOutput;
	}

	@Override
	public boolean shouldTrackOutput() {
		return this.trackOutput;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return this.getWorld().getGameRules().getBoolean(GameRules.COMMAND_BLOCK_OUTPUT);
	}

	public abstract boolean isEditable();
}
