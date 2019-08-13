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
	private long lastExecution = -1L;
	private boolean updateLastExecution = true;
	private int successCount;
	private boolean trackOutput = true;
	private Text lastOutput;
	private String command = "";
	private Text customName = new LiteralText("@");

	public int getSuccessCount() {
		return this.successCount;
	}

	public void setSuccessCount(int i) {
		this.successCount = i;
	}

	public Text getLastOutput() {
		return (Text)(this.lastOutput == null ? new LiteralText("") : this.lastOutput);
	}

	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putString("Command", this.command);
		compoundTag.putInt("SuccessCount", this.successCount);
		compoundTag.putString("CustomName", Text.Serializer.toJson(this.customName));
		compoundTag.putBoolean("TrackOutput", this.trackOutput);
		if (this.lastOutput != null && this.trackOutput) {
			compoundTag.putString("LastOutput", Text.Serializer.toJson(this.lastOutput));
		}

		compoundTag.putBoolean("UpdateLastExecution", this.updateLastExecution);
		if (this.updateLastExecution && this.lastExecution > 0L) {
			compoundTag.putLong("LastExecution", this.lastExecution);
		}

		return compoundTag;
	}

	public void deserialize(CompoundTag compoundTag) {
		this.command = compoundTag.getString("Command");
		this.successCount = compoundTag.getInt("SuccessCount");
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = Text.Serializer.fromJson(compoundTag.getString("CustomName"));
		}

		if (compoundTag.containsKey("TrackOutput", 1)) {
			this.trackOutput = compoundTag.getBoolean("TrackOutput");
		}

		if (compoundTag.containsKey("LastOutput", 8) && this.trackOutput) {
			try {
				this.lastOutput = Text.Serializer.fromJson(compoundTag.getString("LastOutput"));
			} catch (Throwable var3) {
				this.lastOutput = new LiteralText(var3.getMessage());
			}
		} else {
			this.lastOutput = null;
		}

		if (compoundTag.containsKey("UpdateLastExecution")) {
			this.updateLastExecution = compoundTag.getBoolean("UpdateLastExecution");
		}

		if (this.updateLastExecution && compoundTag.containsKey("LastExecution")) {
			this.lastExecution = compoundTag.getLong("LastExecution");
		} else {
			this.lastExecution = -1L;
		}
	}

	public void setCommand(String string) {
		this.command = string;
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
			if (minecraftServer != null && minecraftServer.hasGameDir() && minecraftServer.areCommandBlocksEnabled() && !ChatUtil.isEmpty(this.command)) {
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

	public void setCustomName(Text text) {
		this.customName = text;
	}

	@Override
	public void sendMessage(Text text) {
		if (this.trackOutput) {
			this.lastOutput = new LiteralText("[" + DATE_FORMAT.format(new Date()) + "] ").append(text);
			this.markDirty();
		}
	}

	public abstract ServerWorld getWorld();

	public abstract void markDirty();

	public void setLastOutput(@Nullable Text text) {
		this.lastOutput = text;
	}

	public void shouldTrackOutput(boolean bl) {
		this.trackOutput = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isTrackingOutput() {
		return this.trackOutput;
	}

	public boolean interact(PlayerEntity playerEntity) {
		if (!playerEntity.isCreativeLevelTwoOp()) {
			return false;
		} else {
			if (playerEntity.getEntityWorld().isClient) {
				playerEntity.openCommandBlockMinecartScreen(this);
			}

			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract Vec3d getPos();

	public abstract ServerCommandSource getSource();

	@Override
	public boolean sendCommandFeedback() {
		return this.getWorld().getGameRules().getBoolean(GameRules.field_19400) && this.trackOutput;
	}

	@Override
	public boolean shouldTrackOutput() {
		return this.trackOutput;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return this.getWorld().getGameRules().getBoolean(GameRules.field_19394);
	}
}
