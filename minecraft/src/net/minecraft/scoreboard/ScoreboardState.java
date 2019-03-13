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
	private CompoundTag field_1450;

	public ScoreboardState() {
		super("scoreboard");
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
		if (this.field_1450 != null) {
			this.method_77(this.field_1450);
		}
	}

	@Override
	public void method_77(CompoundTag compoundTag) {
		if (this.scoreboard == null) {
			this.field_1450 = compoundTag;
		} else {
			this.method_1220(compoundTag.method_10554("Objectives", 10));
			this.scoreboard.method_1188(compoundTag.method_10554("PlayerScores", 10));
			if (compoundTag.containsKey("DisplaySlots", 10)) {
				this.method_1221(compoundTag.getCompound("DisplaySlots"));
			}

			if (compoundTag.containsKey("Teams", 9)) {
				this.method_1219(compoundTag.method_10554("Teams", 10));
			}
		}
	}

	protected void method_1219(ListTag listTag) {
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
					scoreboardTeam.method_1149(visibilityRule);
				}
			}

			if (compoundTag.containsKey("DeathMessageVisibility", 8)) {
				AbstractScoreboardTeam.VisibilityRule visibilityRule = AbstractScoreboardTeam.VisibilityRule.method_1213(compoundTag.getString("DeathMessageVisibility"));
				if (visibilityRule != null) {
					scoreboardTeam.method_1133(visibilityRule);
				}
			}

			if (compoundTag.containsKey("CollisionRule", 8)) {
				AbstractScoreboardTeam.CollisionRule collisionRule = AbstractScoreboardTeam.CollisionRule.method_1210(compoundTag.getString("CollisionRule"));
				if (collisionRule != null) {
					scoreboardTeam.method_1145(collisionRule);
				}
			}

			this.method_1215(scoreboardTeam, compoundTag.method_10554("Players", 8));
		}
	}

	protected void method_1215(ScoreboardTeam scoreboardTeam, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			this.scoreboard.addPlayerToTeam(listTag.getString(i), scoreboardTeam);
		}
	}

	protected void method_1221(CompoundTag compoundTag) {
		for (int i = 0; i < 19; i++) {
			if (compoundTag.containsKey("slot_" + i, 8)) {
				String string = compoundTag.getString("slot_" + i);
				ScoreboardObjective scoreboardObjective = this.scoreboard.method_1170(string);
				this.scoreboard.setObjectiveSlot(i, scoreboardObjective);
			}
		}
	}

	protected void method_1220(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			ScoreboardCriterion.method_1224(compoundTag.getString("CriteriaName")).ifPresent(scoreboardCriterion -> {
				String string = compoundTag.getString("Name");
				if (string.length() > 16) {
					string = string.substring(0, 16);
				}

				TextComponent textComponent = TextComponent.Serializer.fromJsonString(compoundTag.getString("DisplayName"));
				ScoreboardCriterion.Type type = ScoreboardCriterion.Type.method_1229(compoundTag.getString("RenderType"));
				this.scoreboard.method_1168(string, scoreboardCriterion, textComponent, type);
			});
		}
	}

	@Override
	public CompoundTag method_75(CompoundTag compoundTag) {
		if (this.scoreboard == null) {
			LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
			return compoundTag;
		} else {
			compoundTag.method_10566("Objectives", this.method_1216());
			compoundTag.method_10566("PlayerScores", this.scoreboard.method_1169());
			compoundTag.method_10566("Teams", this.method_1217());
			this.method_1222(compoundTag);
			return compoundTag;
		}
	}

	protected ListTag method_1217() {
		ListTag listTag = new ListTag();

		for (ScoreboardTeam scoreboardTeam : this.scoreboard.getTeams()) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("Name", scoreboardTeam.getName());
			compoundTag.putString("DisplayName", TextComponent.Serializer.toJsonString(scoreboardTeam.method_1140()));
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
				listTag2.add(new StringTag(string));
			}

			compoundTag.method_10566("Players", listTag2);
			listTag.add(compoundTag);
		}

		return listTag;
	}

	protected void method_1222(CompoundTag compoundTag) {
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
			compoundTag.method_10566("DisplaySlots", compoundTag2);
		}
	}

	protected ListTag method_1216() {
		ListTag listTag = new ListTag();

		for (ScoreboardObjective scoreboardObjective : this.scoreboard.getObjectives()) {
			if (scoreboardObjective.method_1116() != null) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("Name", scoreboardObjective.getName());
				compoundTag.putString("CriteriaName", scoreboardObjective.method_1116().getName());
				compoundTag.putString("DisplayName", TextComponent.Serializer.toJsonString(scoreboardObjective.method_1114()));
				compoundTag.putString("RenderType", scoreboardObjective.method_1118().getName());
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}
}
