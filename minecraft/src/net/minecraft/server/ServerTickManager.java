package net.minecraft.server;

import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TimeHelper;
import net.minecraft.world.tick.TickManager;

public class ServerTickManager extends TickManager {
	private long sprintTicks = 0L;
	private long sprintStartTime = 0L;
	private long sprintTime = 0L;
	private long scheduledSprintTicks = 0L;
	private boolean wasFrozen = false;
	private final MinecraftServer server;

	public ServerTickManager(MinecraftServer server) {
		this.server = server;
	}

	public boolean isSprinting() {
		return this.scheduledSprintTicks > 0L;
	}

	@Override
	public void setFrozen(boolean frozen) {
		super.setFrozen(frozen);
		this.sendUpdateTickRatePacket();
	}

	private void sendUpdateTickRatePacket() {
		this.server.getPlayerManager().sendToAll(UpdateTickRateS2CPacket.create(this));
	}

	private void sendStepPacket() {
		this.server.getPlayerManager().sendToAll(TickStepS2CPacket.create(this));
	}

	public boolean step(int ticks) {
		if (!this.isFrozen()) {
			return false;
		} else {
			this.stepTicks = ticks;
			this.sendStepPacket();
			return true;
		}
	}

	public boolean stopStepping() {
		if (this.stepTicks > 0) {
			this.stepTicks = 0;
			this.sendStepPacket();
			return true;
		} else {
			return false;
		}
	}

	public boolean stopSprinting() {
		if (this.sprintTicks > 0L) {
			this.finishSprinting();
			return true;
		} else {
			return false;
		}
	}

	public boolean startSprint(int ticks) {
		boolean bl = this.sprintTicks > 0L;
		this.sprintTime = 0L;
		this.scheduledSprintTicks = (long)ticks;
		this.sprintTicks = (long)ticks;
		this.wasFrozen = this.isFrozen();
		this.setFrozen(false);
		return bl;
	}

	private void finishSprinting() {
		long l = this.scheduledSprintTicks - this.sprintTicks;
		double d = Math.max(1.0, (double)this.sprintTime) / (double)TimeHelper.MILLI_IN_NANOS;
		int i = (int)((double)(TimeHelper.SECOND_IN_MILLIS * l) / d);
		String string = String.format("%.2f", l == 0L ? (double)this.getMillisPerTick() : d / (double)l);
		this.scheduledSprintTicks = 0L;
		this.sprintTime = 0L;
		this.server.getCommandSource().sendFeedback(() -> Text.translatable("commands.tick.sprint.report", i, string), true);
		this.sprintTicks = 0L;
		this.setFrozen(this.wasFrozen);
		this.server.updateAutosaveTicks();
	}

	public boolean sprint() {
		if (!this.shouldTick) {
			return false;
		} else if (this.sprintTicks > 0L) {
			this.sprintStartTime = System.nanoTime();
			this.sprintTicks--;
			return true;
		} else {
			this.finishSprinting();
			return false;
		}
	}

	public void updateSprintTime() {
		this.sprintTime = this.sprintTime + (System.nanoTime() - this.sprintStartTime);
	}

	@Override
	public void setTickRate(float tickRate) {
		super.setTickRate(tickRate);
		this.server.updateAutosaveTicks();
		this.sendUpdateTickRatePacket();
	}

	public void sendPackets(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(UpdateTickRateS2CPacket.create(this));
		player.networkHandler.sendPacket(TickStepS2CPacket.create(this));
	}
}
