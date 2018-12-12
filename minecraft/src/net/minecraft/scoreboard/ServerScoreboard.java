package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.ScoreboardDisplayClientPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateClientPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateClientPacket;
import net.minecraft.client.network.packet.TeamClientPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerScoreboard extends Scoreboard {
	private final MinecraftServer server;
	private final Set<ScoreboardObjective> field_13427 = Sets.<ScoreboardObjective>newHashSet();
	private Runnable[] field_13426 = new Runnable[0];

	public ServerScoreboard(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Override
	public void updateScore(ScoreboardPlayerScore scoreboardPlayerScore) {
		super.updateScore(scoreboardPlayerScore);
		if (this.field_13427.contains(scoreboardPlayerScore.getObjective())) {
			this.server
				.getPlayerManager()
				.sendToAll(
					new ScoreboardPlayerUpdateClientPacket(
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
		this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateClientPacket(ServerScoreboard.UpdateMode.field_13430, null, string, 0));
		this.method_12941();
	}

	@Override
	public void updatePlayerScore(String string, ScoreboardObjective scoreboardObjective) {
		super.updatePlayerScore(string, scoreboardObjective);
		if (this.field_13427.contains(scoreboardObjective)) {
			this.server
				.getPlayerManager()
				.sendToAll(new ScoreboardPlayerUpdateClientPacket(ServerScoreboard.UpdateMode.field_13430, scoreboardObjective.getName(), string, 0));
		}

		this.method_12941();
	}

	@Override
	public void setObjectiveSlot(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		ScoreboardObjective scoreboardObjective2 = this.getObjectiveForSlot(i);
		super.setObjectiveSlot(i, scoreboardObjective);
		if (scoreboardObjective2 != scoreboardObjective && scoreboardObjective2 != null) {
			if (this.method_12936(scoreboardObjective2) > 0) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayClientPacket(i, scoreboardObjective));
			} else {
				this.method_12938(scoreboardObjective2);
			}
		}

		if (scoreboardObjective != null) {
			if (this.field_13427.contains(scoreboardObjective)) {
				this.server.getPlayerManager().sendToAll(new ScoreboardDisplayClientPacket(i, scoreboardObjective));
			} else {
				this.method_12939(scoreboardObjective);
			}
		}

		this.method_12941();
	}

	@Override
	public boolean addPlayerToTeam(String string, ScoreboardTeam scoreboardTeam) {
		if (super.addPlayerToTeam(string, scoreboardTeam)) {
			this.server.getPlayerManager().sendToAll(new TeamClientPacket(scoreboardTeam, Arrays.asList(string), 3));
			this.method_12941();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removePlayerFromTeam(String string, ScoreboardTeam scoreboardTeam) {
		super.removePlayerFromTeam(string, scoreboardTeam);
		this.server.getPlayerManager().sendToAll(new TeamClientPacket(scoreboardTeam, Arrays.asList(string), 4));
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
		if (this.field_13427.contains(scoreboardObjective)) {
			this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateClientPacket(scoreboardObjective, 2));
		}

		this.method_12941();
	}

	@Override
	public void updateRemovedObjective(ScoreboardObjective scoreboardObjective) {
		super.updateRemovedObjective(scoreboardObjective);
		if (this.field_13427.contains(scoreboardObjective)) {
			this.method_12938(scoreboardObjective);
		}

		this.method_12941();
	}

	@Override
	public void updateScoreboardTeamAndPlayers(ScoreboardTeam scoreboardTeam) {
		super.updateScoreboardTeamAndPlayers(scoreboardTeam);
		this.server.getPlayerManager().sendToAll(new TeamClientPacket(scoreboardTeam, 0));
		this.method_12941();
	}

	@Override
	public void updateScoreboardTeam(ScoreboardTeam scoreboardTeam) {
		super.updateScoreboardTeam(scoreboardTeam);
		this.server.getPlayerManager().sendToAll(new TeamClientPacket(scoreboardTeam, 2));
		this.method_12941();
	}

	@Override
	public void updateRemovedTeam(ScoreboardTeam scoreboardTeam) {
		super.updateRemovedTeam(scoreboardTeam);
		this.server.getPlayerManager().sendToAll(new TeamClientPacket(scoreboardTeam, 1));
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
		list.add(new ScoreboardObjectiveUpdateClientPacket(scoreboardObjective, 0));

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == scoreboardObjective) {
				list.add(new ScoreboardDisplayClientPacket(i, scoreboardObjective));
			}
		}

		for (ScoreboardPlayerScore scoreboardPlayerScore : this.getAllPlayerScores(scoreboardObjective)) {
			list.add(
				new ScoreboardPlayerUpdateClientPacket(
					ServerScoreboard.UpdateMode.field_13431,
					scoreboardPlayerScore.getObjective().getName(),
					scoreboardPlayerScore.getPlayerName(),
					scoreboardPlayerScore.getScore()
				)
			);
		}

		return list;
	}

	public void method_12939(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = this.method_12937(scoreboardObjective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.field_13427.add(scoreboardObjective);
	}

	public List<Packet<?>> method_12940(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = Lists.<Packet<?>>newArrayList();
		list.add(new ScoreboardObjectiveUpdateClientPacket(scoreboardObjective, 1));

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == scoreboardObjective) {
				list.add(new ScoreboardDisplayClientPacket(i, scoreboardObjective));
			}
		}

		return list;
	}

	public void method_12938(ScoreboardObjective scoreboardObjective) {
		List<Packet<?>> list = this.method_12940(scoreboardObjective);

		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
			for (Packet<?> packet : list) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		this.field_13427.remove(scoreboardObjective);
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
