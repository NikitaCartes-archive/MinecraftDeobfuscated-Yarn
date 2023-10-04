package net.minecraft.scoreboard;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;

public class ScoreboardState extends PersistentState {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String SCOREBOARD_KEY = "scoreboard";
	private final Scoreboard scoreboard;

	public ScoreboardState(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	public ScoreboardState readNbt(NbtCompound nbt) {
		this.readObjectivesNbt(nbt.getList("Objectives", NbtElement.COMPOUND_TYPE));
		this.scoreboard.readNbt(nbt.getList("PlayerScores", NbtElement.COMPOUND_TYPE));
		if (nbt.contains("DisplaySlots", NbtElement.COMPOUND_TYPE)) {
			this.readDisplaySlotsNbt(nbt.getCompound("DisplaySlots"));
		}

		if (nbt.contains("Teams", NbtElement.LIST_TYPE)) {
			this.readTeamsNbt(nbt.getList("Teams", NbtElement.COMPOUND_TYPE));
		}

		return this;
	}

	private void readTeamsNbt(NbtList nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			NbtCompound nbtCompound = nbt.getCompound(i);
			String string = nbtCompound.getString("Name");
			Team team = this.scoreboard.addTeam(string);
			Text text = Text.Serialization.fromJson(nbtCompound.getString("DisplayName"));
			if (text != null) {
				team.setDisplayName(text);
			}

			if (nbtCompound.contains("TeamColor", NbtElement.STRING_TYPE)) {
				team.setColor(Formatting.byName(nbtCompound.getString("TeamColor")));
			}

			if (nbtCompound.contains("AllowFriendlyFire", NbtElement.NUMBER_TYPE)) {
				team.setFriendlyFireAllowed(nbtCompound.getBoolean("AllowFriendlyFire"));
			}

			if (nbtCompound.contains("SeeFriendlyInvisibles", NbtElement.NUMBER_TYPE)) {
				team.setShowFriendlyInvisibles(nbtCompound.getBoolean("SeeFriendlyInvisibles"));
			}

			if (nbtCompound.contains("MemberNamePrefix", NbtElement.STRING_TYPE)) {
				Text text2 = Text.Serialization.fromJson(nbtCompound.getString("MemberNamePrefix"));
				if (text2 != null) {
					team.setPrefix(text2);
				}
			}

			if (nbtCompound.contains("MemberNameSuffix", NbtElement.STRING_TYPE)) {
				Text text2 = Text.Serialization.fromJson(nbtCompound.getString("MemberNameSuffix"));
				if (text2 != null) {
					team.setSuffix(text2);
				}
			}

			if (nbtCompound.contains("NameTagVisibility", NbtElement.STRING_TYPE)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(nbtCompound.getString("NameTagVisibility"));
				if (visibilityRule != null) {
					team.setNameTagVisibilityRule(visibilityRule);
				}
			}

			if (nbtCompound.contains("DeathMessageVisibility", NbtElement.STRING_TYPE)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(nbtCompound.getString("DeathMessageVisibility"));
				if (visibilityRule != null) {
					team.setDeathMessageVisibilityRule(visibilityRule);
				}
			}

			if (nbtCompound.contains("CollisionRule", NbtElement.STRING_TYPE)) {
				AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(nbtCompound.getString("CollisionRule"));
				if (collisionRule != null) {
					team.setCollisionRule(collisionRule);
				}
			}

			this.readTeamPlayersNbt(team, nbtCompound.getList("Players", NbtElement.STRING_TYPE));
		}
	}

	private void readTeamPlayersNbt(Team team, NbtList nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			this.scoreboard.addPlayerToTeam(nbt.getString(i), team);
		}
	}

	private void readDisplaySlotsNbt(NbtCompound nbt) {
		for (String string : nbt.getKeys()) {
			ScoreboardDisplaySlot scoreboardDisplaySlot = (ScoreboardDisplaySlot)ScoreboardDisplaySlot.CODEC.byId(string);
			if (scoreboardDisplaySlot != null) {
				String string2 = nbt.getString(string);
				ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string2);
				this.scoreboard.setObjectiveSlot(scoreboardDisplaySlot, scoreboardObjective);
			}
		}
	}

	private void readObjectivesNbt(NbtList nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			NbtCompound nbtCompound = nbt.getCompound(i);
			String string = nbtCompound.getString("CriteriaName");
			ScoreboardCriterion scoreboardCriterion = (ScoreboardCriterion)ScoreboardCriterion.getOrCreateStatCriterion(string).orElseGet(() -> {
				LOGGER.warn("Unknown scoreboard criteria {}, replacing with {}", string, ScoreboardCriterion.DUMMY.getName());
				return ScoreboardCriterion.DUMMY;
			});
			String string2 = nbtCompound.getString("Name");
			Text text = Text.Serialization.fromJson(nbtCompound.getString("DisplayName"));
			ScoreboardCriterion.RenderType renderType = ScoreboardCriterion.RenderType.getType(nbtCompound.getString("RenderType"));
			this.scoreboard.addObjective(string2, scoreboardCriterion, text, renderType);
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.put("Objectives", this.objectivesToNbt());
		nbt.put("PlayerScores", this.scoreboard.toNbt());
		nbt.put("Teams", this.teamsToNbt());
		this.writeDisplaySlotsNbt(nbt);
		return nbt;
	}

	private NbtList teamsToNbt() {
		NbtList nbtList = new NbtList();

		for (Team team : this.scoreboard.getTeams()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("Name", team.getName());
			nbtCompound.putString("DisplayName", Text.Serialization.toJsonString(team.getDisplayName()));
			if (team.getColor().getColorIndex() >= 0) {
				nbtCompound.putString("TeamColor", team.getColor().getName());
			}

			nbtCompound.putBoolean("AllowFriendlyFire", team.isFriendlyFireAllowed());
			nbtCompound.putBoolean("SeeFriendlyInvisibles", team.shouldShowFriendlyInvisibles());
			nbtCompound.putString("MemberNamePrefix", Text.Serialization.toJsonString(team.getPrefix()));
			nbtCompound.putString("MemberNameSuffix", Text.Serialization.toJsonString(team.getSuffix()));
			nbtCompound.putString("NameTagVisibility", team.getNameTagVisibilityRule().name);
			nbtCompound.putString("DeathMessageVisibility", team.getDeathMessageVisibilityRule().name);
			nbtCompound.putString("CollisionRule", team.getCollisionRule().name);
			NbtList nbtList2 = new NbtList();

			for (String string : team.getPlayerList()) {
				nbtList2.add(NbtString.of(string));
			}

			nbtCompound.put("Players", nbtList2);
			nbtList.add(nbtCompound);
		}

		return nbtList;
	}

	private void writeDisplaySlotsNbt(NbtCompound nbt) {
		NbtCompound nbtCompound = new NbtCompound();

		for (ScoreboardDisplaySlot scoreboardDisplaySlot : ScoreboardDisplaySlot.values()) {
			ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
			if (scoreboardObjective != null) {
				nbtCompound.putString(scoreboardDisplaySlot.asString(), scoreboardObjective.getName());
			}
		}

		if (!nbtCompound.isEmpty()) {
			nbt.put("DisplaySlots", nbtCompound);
		}
	}

	private NbtList objectivesToNbt() {
		NbtList nbtList = new NbtList();

		for (ScoreboardObjective scoreboardObjective : this.scoreboard.getObjectives()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("Name", scoreboardObjective.getName());
			nbtCompound.putString("CriteriaName", scoreboardObjective.getCriterion().getName());
			nbtCompound.putString("DisplayName", Text.Serialization.toJsonString(scoreboardObjective.getDisplayName()));
			nbtCompound.putString("RenderType", scoreboardObjective.getRenderType().getName());
			nbtList.add(nbtCompound);
		}

		return nbtList;
	}
}
