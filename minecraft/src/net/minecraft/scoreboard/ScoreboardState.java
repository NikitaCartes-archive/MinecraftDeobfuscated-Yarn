package net.minecraft.scoreboard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.PersistedState;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardState extends PersistedState {
	private static final Logger LOGGER = LogManager.getLogger();
	private Scoreboard scoreboard;
	private CompoundTag tag;

	public ScoreboardState() {
		this("scoreboard");
	}

	public ScoreboardState(String string) {
		super(string);
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
		if (this.tag != null) {
			this.deserialize(this.tag);
		}
	}

	@Override
	public void deserialize(CompoundTag compoundTag) {
		if (this.scoreboard == null) {
			this.tag = compoundTag;
		} else {
			this.deserializeObjectives(compoundTag.getList("Objectives", 10));
			this.scoreboard.fromTag(compoundTag.getList("PlayerScores", 10));
			if (compoundTag.containsKey("DisplaySlots", 10)) {
				this.deserializeDisplaySlots(compoundTag.getCompound("DisplaySlots"));
			}

			if (compoundTag.containsKey("Teams", 9)) {
				this.deserializeTeams(compoundTag.getList("Teams", 10));
			}
		}
	}

	protected void deserializeTeams(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			String string = compoundTag.getString("Name");
			if (string.length() > 16) {
				string = string.substring(0, 16);
			}

			ScoreboardTeam scoreboardTeam = this.scoreboard.addTeam(string);
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(compoundTag.getString("DisplayName"));
			if (textComponent != null) {
				scoreboardTeam.method_1137(textComponent);
			}

			if (compoundTag.containsKey("TeamColor", 8)) {
				scoreboardTeam.setColor(TextFormat.getFormatByName(compoundTag.getString("TeamColor")));
			}

			if (compoundTag.containsKey("AllowFriendlyFire", 99)) {
				scoreboardTeam.setFriendlyFireAllowed(compoundTag.getBoolean("AllowFriendlyFire"));
			}

			if (compoundTag.containsKey("SeeFriendlyInvisibles", 99)) {
				scoreboardTeam.setShowFriendlyInvisibles(compoundTag.getBoolean("SeeFriendlyInvisibles"));
			}

			if (compoundTag.containsKey("MemberNamePrefix", 8)) {
				TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(compoundTag.getString("MemberNamePrefix"));
				if (textComponent2 != null) {
					scoreboardTeam.method_1138(textComponent2);
				}
			}

			if (compoundTag.containsKey("MemberNameSuffix", 8)) {
				TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(compoundTag.getString("MemberNameSuffix"));
				if (textComponent2 != null) {
					scoreboardTeam.method_1139(textComponent2);
				}
			}

			if (compoundTag.containsKey("NameTagVisibility", 8)) {
				AbstractScoreboardTeam.VisibilityRule visibilityRule = AbstractScoreboardTeam.VisibilityRule.method_1213(compoundTag.getString("NameTagVisibility"));
				if (visibilityRule != null) {
					scoreboardTeam.setNameTagVisibilityRule(visibilityRule);
				}
			}

			if (compoundTag.containsKey("DeathMessageVisibility", 8)) {
				AbstractScoreboardTeam.VisibilityRule visibilityRule = AbstractScoreboardTeam.VisibilityRule.method_1213(compoundTag.getString("DeathMessageVisibility"));
				if (visibilityRule != null) {
					scoreboardTeam.setDeathMessageVisibilityRule(visibilityRule);
				}
			}

			if (compoundTag.containsKey("CollisionRule", 8)) {
				AbstractScoreboardTeam.CollisionRule collisionRule = AbstractScoreboardTeam.CollisionRule.method_1210(compoundTag.getString("CollisionRule"));
				if (collisionRule != null) {
					scoreboardTeam.setCollisionRule(collisionRule);
				}
			}

			this.deserializeTeamPlayers(scoreboardTeam, compoundTag.getList("Players", 8));
		}
	}

	protected void deserializeTeamPlayers(ScoreboardTeam scoreboardTeam, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			this.scoreboard.addPlayerToTeam(listTag.getString(i), scoreboardTeam);
		}
	}

	protected void deserializeDisplaySlots(CompoundTag compoundTag) {
		for (int i = 0; i < 19; i++) {
			if (compoundTag.containsKey("slot_" + i, 8)) {
				String string = compoundTag.getString("slot_" + i);
				ScoreboardObjective scoreboardObjective = this.scoreboard.method_1170(string);
				this.scoreboard.setObjectiveSlot(i, scoreboardObjective);
			}
		}
	}

	protected void deserializeObjectives(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			ScoreboardCriterion scoreboardCriterion = ScoreboardCriterion.method_1224(compoundTag.getString("CriteriaName"));
			if (scoreboardCriterion != null) {
				String string = compoundTag.getString("Name");
				if (string.length() > 16) {
					string = string.substring(0, 16);
				}

				TextComponent textComponent = TextComponent.Serializer.fromJsonString(compoundTag.getString("DisplayName"));
				ScoreboardCriterion.Type type = ScoreboardCriterion.Type.method_1229(compoundTag.getString("RenderType"));
				this.scoreboard.method_1168(string, scoreboardCriterion, textComponent, type);
			}
		}
	}

	@Override
	public CompoundTag serialize(CompoundTag compoundTag) {
		if (this.scoreboard == null) {
			LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
			return compoundTag;
		} else {
			compoundTag.put("Objectives", this.serializeObjectives());
			compoundTag.put("PlayerScores", this.scoreboard.toTag());
			compoundTag.put("Teams", this.serializeTeams());
			this.serializeSlots(compoundTag);
			return compoundTag;
		}
	}

	protected ListTag serializeTeams() {
		ListTag listTag = new ListTag();

		for (ScoreboardTeam scoreboardTeam : this.scoreboard.getTeams()) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("Name", scoreboardTeam.getName());
			compoundTag.putString("DisplayName", TextComponent.Serializer.toJsonString(scoreboardTeam.getDisplayName()));
			if (scoreboardTeam.getColor().getId() >= 0) {
				compoundTag.putString("TeamColor", scoreboardTeam.getColor().getFormatName());
			}

			compoundTag.putBoolean("AllowFriendlyFire", scoreboardTeam.isFriendlyFireAllowed());
			compoundTag.putBoolean("SeeFriendlyInvisibles", scoreboardTeam.shouldShowFriendlyInvisibles());
			compoundTag.putString("MemberNamePrefix", TextComponent.Serializer.toJsonString(scoreboardTeam.method_1144()));
			compoundTag.putString("MemberNameSuffix", TextComponent.Serializer.toJsonString(scoreboardTeam.method_1136()));
			compoundTag.putString("NameTagVisibility", scoreboardTeam.getNameTagVisibilityRule().field_1445);
			compoundTag.putString("DeathMessageVisibility", scoreboardTeam.getDeathMessageVisibilityRule().field_1445);
			compoundTag.putString("CollisionRule", scoreboardTeam.getCollisionRule().field_1436);
			ListTag listTag2 = new ListTag();

			for (String string : scoreboardTeam.getPlayerList()) {
				listTag2.add((Tag)(new StringTag(string)));
			}

			compoundTag.put("Players", listTag2);
			listTag.add((Tag)compoundTag);
		}

		return listTag;
	}

	protected void serializeSlots(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = new CompoundTag();
		boolean bl = false;

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null) {
				compoundTag2.putString("slot_" + i, scoreboardObjective.getName());
				bl = true;
			}
		}

		if (bl) {
			compoundTag.put("DisplaySlots", compoundTag2);
		}
	}

	protected ListTag serializeObjectives() {
		ListTag listTag = new ListTag();

		for (ScoreboardObjective scoreboardObjective : this.scoreboard.getObjectives()) {
			if (scoreboardObjective.getCriterion() != null) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("Name", scoreboardObjective.getName());
				compoundTag.putString("CriteriaName", scoreboardObjective.getCriterion().getName());
				compoundTag.putString("DisplayName", TextComponent.Serializer.toJsonString(scoreboardObjective.getDisplayName()));
				compoundTag.putString("RenderType", scoreboardObjective.getCriterionType().getName());
				listTag.add((Tag)compoundTag);
			}
		}

		return listTag;
	}
}
