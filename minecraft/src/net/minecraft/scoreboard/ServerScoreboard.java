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
	private final Set<ScoreboardObjective> objectiveList = Sets.<ScoreboardObjective>newHashSet();
	private Runnable[] field_13426 = new Runnable[0];

	public ServerScoreboard(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Override
	public void updateScore(ScoreboardPlayerScore scoreboardPlayerScore) {
		super.updateScore(scoreboardPlayerScore);
		if (this.objectiveList.contains(scoreboardPlayerScore.getObjective())) {
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

		this.method_12941();
	}

	@Override
	public void updatePlayerScore(String string) {
		super.updatePlayerScore(string);
		this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.field_13430, null, string, 0));
		this.method_12941();
	}

	@Override
	public void updatePlayerScore(String string, ScoreboardObjective scoreboardObjective) {
		super.updatePlayerScore(string, scoreboardObjective);
		if (this.objectiveList.contains(scoreboardObjective)) {
			this.server
				.getPlayerManager()
				.sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.field_13430, scoreboardObjective.getName(), string, 0));
		}

		this.method_12941();
	}

	@Override
	public void setObjectiveSlot(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		ScoreboardObjective scoreboardObjective2 = this.getObjectiveForSlot(i);
		super.setObjectiveSlot(i, scoreboardObjective);
		if (scoreboardObjective2 != scoreboardObjective && scoreboardObjective2 != null) {
			if (this.method_12936(scoreboardObjective2) > 0) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));
			} else {
				this.removeScoreboardObjective(scoreboardObjective2);
			}
		}

		if (scoreboardObjective != null) {
			if (this.objectiveList.contains(scoreboardObjective)) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));
			} else {
				this.addScoreboardObjective(scoreboardObjective);
			}
		}

		this.method_12941();
	}

	@Override
	public boolean addPlayerToTeam(String string, Team team) {
		if (super.addPlayerToTeam(string, team)) {
			this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.asList(string), 3));
			this.method_12941();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removePlayerFromTeam(String string, Team team) {
		super.removePlayerFromTeam(string, team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.asList(string), 4));
		this.method_12941();
	}

	@Override
	public void updateObjective(ScoreboardObjective scoreboardObjective) {
		super.updateObjective(scoreboardObjective);
		this.method_12941();
	}

	@Override
	public void updateExistingObjective(ScoreboardObjective scoreboardObjective) {
		super.updateExistingObjective(scoreboardObjective);
		if (this.objectiveList.contains(scoreboardObjective)) {
			this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(scoreboardObjective, 2));
		}

		this.method_12941();
	}

	@Override
	public void updateRemovedObjective(ScoreboardObjective scoreboardObjective) {
		super.updateRemovedObjective(scoreboardObjective);
		if (this.objectiveList.contains(scoreboardObjective)) {
			this.removeScoreboardObjective(scoreboardObjective);
		}

		this.method_12941();
	}

	@Override
	public void updateScoreboardTeamAndPlayers(Team team) {
		super.updateScoreboardTeamAndPlayers(team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 0));
		this.method_12941();
	}

	@Override
	public void updateScoreboardTeam(Team team) {
		super.updateScoreboardTeam(team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 2));
		this.method_12941();
	}

	@Override
	public void updateRemovedTeam(Team team) {
		super.updateRemovedTeam(team);
		this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 1));
		this.method_12941();
	}

	public void method_12935(Runnable runnable) {
		this.field_13426 = (Runnable[])Arrays.copyOf(this.field_13426, this.field_13426.length + 1);
		this.field_13426[this.field_13426.length - 1] = runnable;
	}

	protected void method_12941() {
		for (Runnable runnable : this.field_13426) {
			runnable.run();
		}
	}

	public List<Packet<?>> method_12937(ScoreboardObjective scoreboardObjective) {
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
		List<Packet<?>> list = this.method_12937(scoreboardObjective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.objectiveList.add(scoreboardObjective);
	}

	public List<Packet<?>> method_12940(ScoreboardObjective scoreboardObjective) {
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
		List<Packet<?>> list = this.method_12940(scoreboardObjective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.objectiveList.remove(scoreboardObjective);
	}

	public int method_12936(ScoreboardObjective scoreboardObjective) {
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
