package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

public class ServerScoreboard extends Scoreboard {
	private final MinecraftServer server;
	private final Set<ScoreboardObjective> syncableObjectives = Sets.<ScoreboardObjective>newHashSet();
	private final List<Runnable> updateListeners = Lists.<Runnable>newArrayList();

	public ServerScoreboard(MinecraftServer server) {
		this.server = server;
	}

	@Override
	protected void updateScore(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score) {
		super.updateScore(scoreHolder, objective, score);
		if (this.syncableObjectives.contains(objective)) {
			this.server
				.getPlayerManager()
				.sendToAll(
					new ScoreboardScoreUpdateS2CPacket(
						scoreHolder.getNameForScoreboard(),
						objective.getName(),
						score.getScore(),
						Optional.ofNullable(score.getDisplayText()),
						Optional.ofNullable(score.getNumberFormat())
					)
				);
		}

		this.runUpdateListeners();
	}

	@Override
	protected void resetScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
		super.resetScore(scoreHolder, objective);
		this.runUpdateListeners();
	}

	@Override
	public void onScoreHolderRemoved(ScoreHolder scoreHolder) {
		super.onScoreHolderRemoved(scoreHolder);
		this.server.getPlayerManager().sendToAll(new ScoreboardScoreResetS2CPacket(scoreHolder.getNameForScoreboard(), null));
		this.runUpdateListeners();
	}

	@Override
	public void onScoreRemoved(ScoreHolder scoreHolder, ScoreboardObjective objective) {
		super.onScoreRemoved(scoreHolder, objective);
		if (this.syncableObjectives.contains(objective)) {
			this.server.getPlayerManager().sendToAll(new ScoreboardScoreResetS2CPacket(scoreHolder.getNameForScoreboard(), objective.getName()));
		}

		this.runUpdateListeners();
	}

	@Override
	public void setObjectiveSlot(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective) {
		ScoreboardObjective scoreboardObjective = this.getObjectiveForSlot(slot);
		super.setObjectiveSlot(slot, objective);
		if (scoreboardObjective != objective && scoreboardObjective != null) {
			if (this.countDisplaySlots(scoreboardObjective) > 0) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
			} else {
				this.stopSyncing(scoreboardObjective);
			}
		}

		if (objective != null) {
			if (this.syncableObjectives.contains(objective)) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
			} else {
				this.startSyncing(objective);
			}
		}

		this.runUpdateListeners();
	}

	@Override
	public boolean addScoreHolderToTeam(String scoreHolderName, Team team) {
		if (super.addScoreHolderToTeam(scoreHolderName, team)) {
			this.server.getPlayerManager().sendToAll(TeamS2CPacket.changePlayerTeam(team, scoreHolderName, TeamS2CPacket.Operation.ADD));
			this.runUpdateListeners();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removeScoreHolderFromTeam(String scoreHolderName, Team team) {
		super.removeScoreHolderFromTeam(scoreHolderName, team);
		this.server.getPlayerManager().sendToAll(TeamS2CPacket.changePlayerTeam(team, scoreHolderName, TeamS2CPacket.Operation.REMOVE));
		this.runUpdateListeners();
	}

	@Override
	public void updateObjective(ScoreboardObjective objective) {
		super.updateObjective(objective);
		this.runUpdateListeners();
	}

	@Override
	public void updateExistingObjective(ScoreboardObjective objective) {
		super.updateExistingObjective(objective);
		if (this.syncableObjectives.contains(objective)) {
			this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(objective, ScoreboardObjectiveUpdateS2CPacket.UPDATE_MODE));
		}

		this.runUpdateListeners();
	}

	@Override
	public void updateRemovedObjective(ScoreboardObjective objective) {
		super.updateRemovedObjective(objective);
		if (this.syncableObjectives.contains(objective)) {
			this.stopSyncing(objective);
		}

		this.runUpdateListeners();
	}

	@Override
	public void updateScoreboardTeamAndPlayers(Team team) {
		super.updateScoreboardTeamAndPlayers(team);
		this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateTeam(team, true));
		this.runUpdateListeners();
	}

	@Override
	public void updateScoreboardTeam(Team team) {
		super.updateScoreboardTeam(team);
		this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateTeam(team, false));
		this.runUpdateListeners();
	}

	@Override
	public void updateRemovedTeam(Team team) {
		super.updateRemovedTeam(team);
		this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateRemovedTeam(team));
		this.runUpdateListeners();
	}

	public void addUpdateListener(Runnable listener) {
		this.updateListeners.add(listener);
	}

	protected void runUpdateListeners() {
		for (Runnable runnable : this.updateListeners) {
			runnable.run();
		}
	}

	public List<Packet<?>> createChangePackets(ScoreboardObjective objective) {
		List<Packet<?>> list = Lists.<Packet<?>>newArrayList();
		list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, ScoreboardObjectiveUpdateS2CPacket.ADD_MODE));

		for (ScoreboardDisplaySlot scoreboardDisplaySlot : ScoreboardDisplaySlot.values()) {
			if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
				list.add(new ScoreboardDisplayS2CPacket(scoreboardDisplaySlot, objective));
			}
		}

		for (ScoreboardEntry scoreboardEntry : this.getScoreboardEntries(objective)) {
			list.add(
				new ScoreboardScoreUpdateS2CPacket(
					scoreboardEntry.owner(),
					objective.getName(),
					scoreboardEntry.value(),
					Optional.ofNullable(scoreboardEntry.display()),
					Optional.ofNullable(scoreboardEntry.numberFormatOverride())
				)
			);
		}

		return list;
	}

	public void startSyncing(ScoreboardObjective objective) {
		List<Packet<?>> list = this.createChangePackets(objective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.syncableObjectives.add(objective);
	}

	public List<Packet<?>> createRemovePackets(ScoreboardObjective objective) {
		List<Packet<?>> list = Lists.<Packet<?>>newArrayList();
		list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, ScoreboardObjectiveUpdateS2CPacket.REMOVE_MODE));

		for (ScoreboardDisplaySlot scoreboardDisplaySlot : ScoreboardDisplaySlot.values()) {
			if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
				list.add(new ScoreboardDisplayS2CPacket(scoreboardDisplaySlot, objective));
			}
		}

		return list;
	}

	public void stopSyncing(ScoreboardObjective objective) {
		List<Packet<?>> list = this.createRemovePackets(objective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.syncableObjectives.remove(objective);
	}

	public int countDisplaySlots(ScoreboardObjective objective) {
		int i = 0;

		for (ScoreboardDisplaySlot scoreboardDisplaySlot : ScoreboardDisplaySlot.values()) {
			if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
				i++;
			}
		}

		return i;
	}

	public PersistentState.Type<ScoreboardState> getPersistentStateType() {
		return new PersistentState.Type<>(this::createState, this::stateFromNbt, DataFixTypes.SAVED_DATA_SCOREBOARD);
	}

	private ScoreboardState createState() {
		ScoreboardState scoreboardState = new ScoreboardState(this);
		this.addUpdateListener(scoreboardState::markDirty);
		return scoreboardState;
	}

	private ScoreboardState stateFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		return this.createState().readNbt(nbt, registries);
	}

	public static enum UpdateMode {
		CHANGE,
		REMOVE;
	}
}
