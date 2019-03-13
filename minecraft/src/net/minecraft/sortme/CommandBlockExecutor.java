package net.minecraft.sortme;

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
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class CommandBlockExecutor implements CommandOutput {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private long lastExecution = -1L;
	private boolean updateLastExecution = true;
	private int successCount;
	private boolean trackOutput = true;
	private TextComponent field_9165;
	private String command = "";
	private TextComponent field_9162 = new StringTextComponent("@");

	public int getSuccessCount() {
		return this.successCount;
	}

	public void setSuccessCount(int i) {
		this.successCount = i;
	}

	public TextComponent method_8292() {
		return (TextComponent)(this.field_9165 == null ? new StringTextComponent("") : this.field_9165);
	}

	public CompoundTag method_8297(CompoundTag compoundTag) {
		compoundTag.putString("Command", this.command);
		compoundTag.putInt("SuccessCount", this.successCount);
		compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.field_9162));
		compoundTag.putBoolean("TrackOutput", this.trackOutput);
		if (this.field_9165 != null && this.trackOutput) {
			compoundTag.putString("LastOutput", TextComponent.Serializer.toJsonString(this.field_9165));
		}

		compoundTag.putBoolean("UpdateLastExecution", this.updateLastExecution);
		if (this.updateLastExecution && this.lastExecution > 0L) {
			compoundTag.putLong("LastExecution", this.lastExecution);
		}

		return compoundTag;
	}

	public void method_8305(CompoundTag compoundTag) {
		this.command = compoundTag.getString("Command");
		this.successCount = compoundTag.getInt("SuccessCount");
		if (compoundTag.containsKey("CustomName", 8)) {
			this.field_9162 = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		if (compoundTag.containsKey("TrackOutput", 1)) {
			this.trackOutput = compoundTag.getBoolean("TrackOutput");
		}

		if (compoundTag.containsKey("LastOutput", 8) && this.trackOutput) {
			try {
				this.field_9165 = TextComponent.Serializer.fromJsonString(compoundTag.getString("LastOutput"));
			} catch (Throwable var3) {
				this.field_9165 = new StringTextComponent(var3.getMessage());
			}
		} else {
			this.field_9165 = null;
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

	public boolean method_8301(World world) {
		if (world.isClient || world.getTime() == this.lastExecution) {
			return false;
		} else if ("Searge".equalsIgnoreCase(this.command)) {
			this.field_9165 = new StringTextComponent("#itzlipofutzli");
			this.successCount = 1;
			return true;
		} else {
			this.successCount = 0;
			MinecraftServer minecraftServer = this.method_8293().getServer();
			if (minecraftServer != null && minecraftServer.method_3814() && minecraftServer.areCommandBlocksEnabled() && !ChatUtil.isEmpty(this.command)) {
				try {
					this.field_9165 = null;
					ServerCommandSource serverCommandSource = this.method_8303().withConsumer((commandContext, bl, i) -> {
						if (bl) {
							this.successCount++;
						}
					});
					minecraftServer.getCommandManager().execute(serverCommandSource, this.command);
				} catch (Throwable var6) {
					CrashReport crashReport = CrashReport.create(var6, "Executing command block");
					CrashReportSection crashReportSection = crashReport.method_562("Command to be executed");
					crashReportSection.method_577("Command", this::getCommand);
					crashReportSection.method_577("Name", () -> this.method_8299().getString());
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

	public TextComponent method_8299() {
		return this.field_9162;
	}

	public void method_8290(TextComponent textComponent) {
		this.field_9162 = textComponent;
	}

	@Override
	public void method_9203(TextComponent textComponent) {
		if (this.trackOutput) {
			this.field_9165 = new StringTextComponent("[" + DATE_FORMAT.format(new Date()) + "] ").append(textComponent);
			this.method_8295();
		}
	}

	public abstract ServerWorld method_8293();

	public abstract void method_8295();

	public void method_8291(@Nullable TextComponent textComponent) {
		this.field_9165 = textComponent;
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
			if (playerEntity.method_5770().isClient) {
				playerEntity.method_7257(this);
			}

			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract Vec3d method_8300();

	public abstract ServerCommandSource method_8303();

	@Override
	public boolean sendCommandFeedback() {
		return this.method_8293().getGameRules().getBoolean("sendCommandFeedback") && this.trackOutput;
	}

	@Override
	public boolean shouldTrackOutput() {
		return this.trackOutput;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return this.method_8293().getGameRules().getBoolean("commandBlockOutput");
	}
}
