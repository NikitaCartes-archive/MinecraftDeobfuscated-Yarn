package net.minecraft.world;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;

public abstract class CommandBlockExecutor implements CommandOutput {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final Text DEFAULT_NAME = new LiteralText("@");
	private long lastExecution = -1L;
	private boolean updateLastExecution = true;
	private int successCount;
	private boolean trackOutput = true;
	private Text lastOutput;
	private String command = "";
	private Text customName = DEFAULT_NAME;

	public int getSuccessCount() {
		return this.successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public Text getLastOutput() {
		return (Text)(this.lastOutput == null ? new LiteralText("") : this.lastOutput);
	}

	public CompoundTag serialize(CompoundTag tag) {
		tag.putString("Command", this.command);
		tag.putInt("SuccessCount", this.successCount);
		tag.putString("CustomName", Text.Serializer.toJson(this.customName));
		tag.putBoolean("TrackOutput", this.trackOutput);
		if (this.lastOutput != null && this.trackOutput) {
			tag.putString("LastOutput", Text.Serializer.toJson(this.lastOutput));
		}

		tag.putBoolean("UpdateLastExecution", this.updateLastExecution);
		if (this.updateLastExecution && this.lastExecution > 0L) {
			tag.putLong("LastExecution", this.lastExecution);
		}

		return tag;
	}

	public void deserialize(CompoundTag tag) {
		this.command = tag.getString("Command");
		this.successCount = tag.getInt("SuccessCount");
		if (tag.contains("CustomName", 8)) {
			this.setCustomName(Text.Serializer.fromJson(tag.getString("CustomName")));
		}

		if (tag.contains("TrackOutput", 1)) {
			this.trackOutput = tag.getBoolean("TrackOutput");
		}

		if (tag.contains("LastOutput", 8) && this.trackOutput) {
			try {
				this.lastOutput = Text.Serializer.fromJson(tag.getString("LastOutput"));
			} catch (Throwable var3) {
				this.lastOutput = new LiteralText(var3.getMessage());
			}
		} else {
			this.lastOutput = null;
		}

		if (tag.contains("UpdateLastExecution")) {
			this.updateLastExecution = tag.getBoolean("UpdateLastExecution");
		}

		if (this.updateLastExecution && tag.contains("LastExecution")) {
			this.lastExecution = tag.getLong("LastExecution");
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
			this.lastOutput = new LiteralText("#itzlipofutzli");
			this.successCount = 1;
			return true;
		} else {
			this.successCount = 0;
			MinecraftServer minecraftServer = this.getWorld().getServer();
			if (minecraftServer != null && minecraftServer.areCommandBlocksEnabled() && !ChatUtil.isEmpty(this.command)) {
				try {
					this.lastOutput = null;
					ServerCommandSource serverCommandSource = this.getSource().withConsumer((commandContext, bl, i) -> {
						if (bl) {
							this.successCount++;
						}
					});
					minecraftServer.getCommandManager().execute(serverCommandSource, this.command);
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
		return this.customName;
	}

	public void setCustomName(@Nullable Text name) {
		if (name != null) {
			this.customName = name;
		} else {
			this.customName = DEFAULT_NAME;
		}
	}

	@Override
	public void sendMessage(Text message) {
		if (this.trackOutput) {
			this.lastOutput = new LiteralText("[" + DATE_FORMAT.format(new Date()) + "] ").append(message);
			this.markDirty();
		}
	}

	public abstract ServerWorld getWorld();

	public abstract void markDirty();

	public void setLastOutput(@Nullable Text lastOutput) {
		this.lastOutput = lastOutput;
	}

	public void shouldTrackOutput(boolean trackOutput) {
		this.trackOutput = trackOutput;
	}

	@Environment(EnvType.CLIENT)
	public boolean isTrackingOutput() {
		return this.trackOutput;
	}

	public boolean interact(PlayerEntity player) {
		if (!player.isCreativeLevelTwoOp()) {
			return false;
		} else {
			if (player.getEntityWorld().isClient) {
				player.openCommandBlockMinecartScreen(this);
			}

			return true;
		}
	}

	@Environment(EnvType.CLIENT)
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
}
