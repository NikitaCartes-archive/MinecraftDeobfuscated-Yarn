package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerScoreboard extends Scoreboard {
	private final MinecraftServer server;
	private final Set<ScoreboardObjective> objectives = Sets.<ScoreboardObjective>newHashSet();
	private Runnable[] updateListeners = new Runnable[0];

	public ServerScoreboard(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Override
	public void updateScore(ScoreboardPlayerScore scoreboardPlayerScore) {
		super.updateScore(scoreboardPlayerScore);
		if (this.objectives.contains(scoreboardPlayerScore.getObjective())) {
			this.server
				.getPlayerManager()
				.sendToAll(
					new ScoreboardPlayerUpdateS2CPacket(
						ServerScoreboard.UpdateMode.field_13431,
						scoreboardPlayerScore.getObjective().getName(),
						scoreboardPlayerScore.getPlayerName(),
						scoreboardPlayerScore.getScore()
					)
				);
		}

		this.runUpdateListeners();
	}

	@Override
	public void updatePlayerScore(String string) {
		super.updatePlayerScore(string);
		this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.field_13430, null, string, 0));
		this.runUpdateListeners();
	}

	@Override
	public void updatePlayerScore(String string, ScoreboardObjective scoreboardObjective) {
		super.updatePlayerScore(string, scoreboardObjective);
		if (this.objectives.contains(scoreboardObjective)) {
			this.server
				.getPlayerManager()
				.sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.field_13430, scoreboardObjective.getName(), string, 0));
		}

		this.runUpdateListeners();
	}

	@Override
	public void setObjectiveSlot(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		ScoreboardObjective scoreboardObjective2 = this.getObjectiveForSlot(i);
		super.setObjectiveSlot(i, scoreboardObjective);
		if (scoreboardObjective2 != scoreboardObjective && scoreboardObjective2 != null) {
			if (this.getSlot(scoreboardObjective2) > 0) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));
			} else {
				this.removeScoreboardObjective(scoreboardObjective2);
			}
		}

		if (scoreboardObjective != null) {
			if (this.objectives.contains(scoreboardObjective)) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));
			} else {
				this.addScoreboardObjective(scoreboardObjective);
			}
		}

		this.runUpdateListeners();
	}

	@Override
	public boolean addPlayerToTeam(String string, Team team) {
		if (super.addPlayerToTeam(string, team)) {
			this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.asList(string), 3));
			this.runUpdateListeners();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removePlayerFromTeam(String string, Team team) {
		super.removePlayerFromTeam(string, team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.asList(string), 4));
		this.runUpdateListeners();
	}

	@Override
	public void updateObjective(ScoreboardObjective scoreboardObjective) {
		super.updateObjective(scoreboardObjective);
		this.runUpdateListeners();
	}

	@Override
	public void updateExistingObjective(ScoreboardObjective scoreboardObjective) {
		super.updateExistingObjective(scoreboardObjective);
		if (this.objectives.contains(scoreboardObjective)) {
			this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(scoreboardObjective, 2));
		}

		this.runUpdateListeners();
	}

	@Override
	public void updateRemovedObjective(ScoreboardObjective scoreboardObjective) {
		super.updateRemovedObjective(scoreboardObjective);
		if (this.objectives.contains(scoreboardObjective)) {
			this.removeScoreboardObjective(scoreboardObjective);
		}

		this.runUpdateListeners();
	}

	@Override
	public void updateScoreboardTeamAndPlayers(Team team) {
		super.updateScoreboardTeamAndPlayers(team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 0));
		this.runUpdateListeners();
	}

	@Override
	public void updateScoreboardTeam(Team team) {
		super.updateScoreboardTeam(team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 2));
		this.runUpdateListeners();
	}

	@Override
	public void updateRemovedTeam(Team team) {
		super.updateRemovedTeam(team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 1));
		this.runUpdateListeners();
	}

	public void addUpdateListener(Runnable runnable) {
		this.updateListeners = (Runnable[])Arrays.copyOf(this.updateListeners, this.updateListeners.length + 1);
		this.updateListeners[this.updateListeners.length - 1] = runnable;
	}

	protected void runUpdateListeners() {
		for (Runnable runnable : this.updateListeners) {
			runnable.run();
		}
	}

	public List<Packet<?>> createChangePackets(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = Lists.<Packet<?>>newArrayList();
		list.add(new ScoreboardObjectiveUpdateS2CPacket(scoreboardObjective, 0));

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == scoreboardObjective) {
				list.add(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));
			}
		}

		for (ScoreboardPlayerScore scoreboardPlayerScore : this.getAllPlayerScores(scoreboardObjective)) {
			list.add(
				new ScoreboardPlayerUpdateS2CPacket(
					ServerScoreboard.UpdateMode.field_13431,
					scoreboardPlayerScore.getObjective().getName(),
					scoreboardPlayerScore.getPlayerName(),
					scoreboardPlayerScore.getScore()
				)
			);
		}

		return list;
	}

	public void addScoreboardObjective(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = this.createChangePackets(scoreboardObjective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.objectives.add(scoreboardObjective);
	}

	public List<Packet<?>> createRemovePackets(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = Lists.<Packet<?>>newArrayList();
		list.add(new ScoreboardObjectiveUpdateS2CPacket(scoreboardObjective, 1));

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == scoreboardObjective) {
				list.add(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));
			}
		}

		return list;
	}

	public void removeScoreboardObjective(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = this.createRemovePackets(scoreboardObjective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.objectives.remove(scoreboardObjective);
	}

	public int getSlot(ScoreboardObjective scoreboardObjective) {
		int i = 0;

		for (int j = 0; j < 19; j++) {
			if (this.getObjectiveForSlot(j) == scoreboardObjective) {
				i++;
			}
		}

		return i;
	}

	public static enum UpdateMode {
		field_13431,
		field_13430;
	}
}
