package net.minecraft.scoreboard;

import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;

public class ScoreboardState extends PersistentState {
	private final Scoreboard scoreboard;

	public ScoreboardState(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	public ScoreboardState readNbt(NbtCompound tag) {
		this.readObjectivesFromNbt(tag.getList("Objectives", NbtTypeIds.COMPOUND));
		this.scoreboard.readNbt(tag.getList("PlayerScores", NbtTypeIds.COMPOUND));
		if (tag.contains("DisplaySlots", NbtTypeIds.COMPOUND)) {
			this.readDisplaySlotsFromNbt(tag.getCompound("DisplaySlots"));
		}

		if (tag.contains("Teams", NbtTypeIds.LIST)) {
			this.readTeamsFromNbt(tag.getList("Teams", NbtTypeIds.COMPOUND));
		}

		return this;
	}

	private void readTeamsFromNbt(NbtList tag) {
		for (int i = 0; i < tag.size(); i++) {
			NbtCompound nbtCompound = tag.getCompound(i);
			String string = nbtCompound.getString("Name");
			if (string.length() > 16) {
				string = string.substring(0, 16);
			}

			Team team = this.scoreboard.addTeam(string);
			Text text = Text.Serializer.fromJson(nbtCompound.getString("DisplayName"));
			if (text != null) {
				team.setDisplayName(text);
			}

			if (nbtCompound.contains("TeamColor", NbtTypeIds.STRING)) {
				team.setColor(Formatting.byName(nbtCompound.getString("TeamColor")));
			}

			if (nbtCompound.contains("AllowFriendlyFire", NbtTypeIds.NUMBER)) {
				team.setFriendlyFireAllowed(nbtCompound.getBoolean("AllowFriendlyFire"));
			}

			if (nbtCompound.contains("SeeFriendlyInvisibles", NbtTypeIds.NUMBER)) {
				team.setShowFriendlyInvisibles(nbtCompound.getBoolean("SeeFriendlyInvisibles"));
			}

			if (nbtCompound.contains("MemberNamePrefix", NbtTypeIds.STRING)) {
				Text text2 = Text.Serializer.fromJson(nbtCompound.getString("MemberNamePrefix"));
				if (text2 != null) {
					team.setPrefix(text2);
				}
			}

			if (nbtCompound.contains("MemberNameSuffix", NbtTypeIds.STRING)) {
				Text text2 = Text.Serializer.fromJson(nbtCompound.getString("MemberNameSuffix"));
				if (text2 != null) {
					team.setSuffix(text2);
				}
			}

			if (nbtCompound.contains("NameTagVisibility", NbtTypeIds.STRING)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(nbtCompound.getString("NameTagVisibility"));
				if (visibilityRule != null) {
					team.setNameTagVisibilityRule(visibilityRule);
				}
			}

			if (nbtCompound.contains("DeathMessageVisibility", NbtTypeIds.STRING)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(nbtCompound.getString("DeathMessageVisibility"));
				if (visibilityRule != null) {
					team.setDeathMessageVisibilityRule(visibilityRule);
				}
			}

			if (nbtCompound.contains("CollisionRule", NbtTypeIds.STRING)) {
				AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(nbtCompound.getString("CollisionRule"));
				if (collisionRule != null) {
					team.setCollisionRule(collisionRule);
				}
			}

			this.readTeamPlayersFromNbt(team, nbtCompound.getList("Players", NbtTypeIds.STRING));
		}
	}

	private void readTeamPlayersFromNbt(Team team, NbtList tag) {
		for (int i = 0; i < tag.size(); i++) {
			this.scoreboard.addPlayerToTeam(tag.getString(i), team);
		}
	}

	private void readDisplaySlotsFromNbt(NbtCompound tag) {
		for (int i = 0; i < 19; i++) {
			if (tag.contains("slot_" + i, NbtTypeIds.STRING)) {
				String string = tag.getString("slot_" + i);
				ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
				this.scoreboard.setObjectiveSlot(i, scoreboardObjective);
			}
		}
	}

	private void readObjectivesFromNbt(NbtList tag) {
		for (int i = 0; i < tag.size(); i++) {
			NbtCompound nbtCompound = tag.getCompound(i);
			ScoreboardCriterion.getOrCreateStatCriterion(nbtCompound.getString("CriteriaName")).ifPresent(scoreboardCriterion -> {
				String string = nbtCompound.getString("Name");
				if (string.length() > 16) {
					string = string.substring(0, 16);
				}

				Text text = Text.Serializer.fromJson(nbtCompound.getString("DisplayName"));
				ScoreboardCriterion.RenderType renderType = ScoreboardCriterion.RenderType.getType(nbtCompound.getString("RenderType"));
				this.scoreboard.addObjective(string, scoreboardCriterion, text, renderType);
			});
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		tag.put("Objectives", this.objectivesToNbt());
		tag.put("PlayerScores", this.scoreboard.toNbt());
		tag.put("Teams", this.teamsToNbt());
		this.writeDisplaySlotsToNbt(tag);
		return tag;
	}

	private NbtList teamsToNbt() {
		NbtList nbtList = new NbtList();

		for (Team team : this.scoreboard.getTeams()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("Name", team.getName());
			nbtCompound.putString("DisplayName", Text.Serializer.toJson(team.getDisplayName()));
			if (team.getColor().getColorIndex() >= 0) {
				nbtCompound.putString("TeamColor", team.getColor().getName());
			}

			nbtCompound.putBoolean("AllowFriendlyFire", team.isFriendlyFireAllowed());
			nbtCompound.putBoolean("SeeFriendlyInvisibles", team.shouldShowFriendlyInvisibles());
			nbtCompound.putString("MemberNamePrefix", Text.Serializer.toJson(team.getPrefix()));
			nbtCompound.putString("MemberNameSuffix", Text.Serializer.toJson(team.getSuffix()));
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

	private void writeDisplaySlotsToNbt(NbtCompound tag) {
		NbtCompound nbtCompound = new NbtCompound();
		boolean bl = false;

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null) {
				nbtCompound.putString("slot_" + i, scoreboardObjective.getName());
				bl = true;
			}
		}

		if (bl) {
			tag.put("DisplaySlots", nbtCompound);
		}
	}

	private NbtList objectivesToNbt() {
		NbtList nbtList = new NbtList();

		for (ScoreboardObjective scoreboardObjective : this.scoreboard.getObjectives()) {
			if (scoreboardObjective.getCriterion() != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putString("Name", scoreboardObjective.getName());
				nbtCompound.putString("CriteriaName", scoreboardObjective.getCriterion().getName());
				nbtCompound.putString("DisplayName", Text.Serializer.toJson(scoreboardObjective.getDisplayName()));
				nbtCompound.putString("RenderType", scoreboardObjective.getRenderType().getName());
				nbtList.add(nbtCompound);
			}
		}

		return nbtList;
	}
}
