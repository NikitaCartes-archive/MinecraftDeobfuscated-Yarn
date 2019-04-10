package net.minecraft.scoreboard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardState extends PersistentState {
	private static final Logger LOGGER = LogManager.getLogger();
	private Scoreboard scoreboard;
	private CompoundTag tag;

	public ScoreboardState() {
		super("scoreboard");
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
		if (this.tag != null) {
			this.fromTag(this.tag);
		}
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
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

			Team team = this.scoreboard.addTeam(string);
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(compoundTag.getString("DisplayName"));
			if (textComponent != null) {
				team.setDisplayName(textComponent);
			}

			if (compoundTag.containsKey("TeamColor", 8)) {
				team.setColor(TextFormat.getFormatByName(compoundTag.getString("TeamColor")));
			}

			if (compoundTag.containsKey("AllowFriendlyFire", 99)) {
				team.setFriendlyFireAllowed(compoundTag.getBoolean("AllowFriendlyFire"));
			}

			if (compoundTag.containsKey("SeeFriendlyInvisibles", 99)) {
				team.setShowFriendlyInvisibles(compoundTag.getBoolean("SeeFriendlyInvisibles"));
			}

			if (compoundTag.containsKey("MemberNamePrefix", 8)) {
				TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(compoundTag.getString("MemberNamePrefix"));
				if (textComponent2 != null) {
					team.setPrefix(textComponent2);
				}
			}

			if (compoundTag.containsKey("MemberNameSuffix", 8)) {
				TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(compoundTag.getString("MemberNameSuffix"));
				if (textComponent2 != null) {
					team.setSuffix(textComponent2);
				}
			}

			if (compoundTag.containsKey("NameTagVisibility", 8)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(compoundTag.getString("NameTagVisibility"));
				if (visibilityRule != null) {
					team.method_1149(visibilityRule);
				}
			}

			if (compoundTag.containsKey("DeathMessageVisibility", 8)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(compoundTag.getString("DeathMessageVisibility"));
				if (visibilityRule != null) {
					team.method_1133(visibilityRule);
				}
			}

			if (compoundTag.containsKey("CollisionRule", 8)) {
				AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(compoundTag.getString("CollisionRule"));
				if (collisionRule != null) {
					team.method_1145(collisionRule);
				}
			}

			this.deserializeTeamPlayers(team, compoundTag.getList("Players", 8));
		}
	}

	protected void deserializeTeamPlayers(Team team, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			this.scoreboard.addPlayerToTeam(listTag.getString(i), team);
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
			ScoreboardCriterion.method_1224(compoundTag.getString("CriteriaName")).ifPresent(scoreboardCriterion -> {
				String string = compoundTag.getString("Name");
				if (string.length() > 16) {
					string = string.substring(0, 16);
				}

				TextComponent textComponent = TextComponent.Serializer.fromJsonString(compoundTag.getString("DisplayName"));
				ScoreboardCriterion.RenderType renderType = ScoreboardCriterion.RenderType.getType(compoundTag.getString("RenderType"));
				this.scoreboard.method_1168(string, scoreboardCriterion, textComponent, renderType);
			});
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
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

		for (Team team : this.scoreboard.getTeams()) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("Name", team.getName());
			compoundTag.putString("DisplayName", TextComponent.Serializer.toJsonString(team.getDisplayName()));
			if (team.getColor().getId() >= 0) {
				compoundTag.putString("TeamColor", team.getColor().getFormatName());
			}

			compoundTag.putBoolean("AllowFriendlyFire", team.isFriendlyFireAllowed());
			compoundTag.putBoolean("SeeFriendlyInvisibles", team.shouldShowFriendlyInvisibles());
			compoundTag.putString("MemberNamePrefix", TextComponent.Serializer.toJsonString(team.getPrefix()));
			compoundTag.putString("MemberNameSuffix", TextComponent.Serializer.toJsonString(team.getSuffix()));
			compoundTag.putString("NameTagVisibility", team.getNameTagVisibilityRule().name);
			compoundTag.putString("DeathMessageVisibility", team.getDeathMessageVisibilityRule().name);
			compoundTag.putString("CollisionRule", team.getCollisionRule().name);
			ListTag listTag2 = new ListTag();

			for (String string : team.getPlayerList()) {
				listTag2.add(new StringTag(string));
			}

			compoundTag.put("Players", listTag2);
			listTag.add(compoundTag);
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
				compoundTag.putString("RenderType", scoreboardObjective.method_1118().getName());
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}
}
