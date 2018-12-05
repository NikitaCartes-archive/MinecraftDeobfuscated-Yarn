package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

public abstract class AbstractScoreboardTeam {
	public boolean isEqual(@Nullable AbstractScoreboardTeam abstractScoreboardTeam) {
		return abstractScoreboardTeam == null ? false : this == abstractScoreboardTeam;
	}

	public abstract String getName();

	public abstract TextComponent method_1198(TextComponent textComponent);

	@Environment(EnvType.CLIENT)
	public abstract boolean shouldShowFriendlyInvisibles();

	public abstract boolean isFriendlyFireAllowed();

	@Environment(EnvType.CLIENT)
	public abstract AbstractScoreboardTeam.VisibilityRule getNameTagVisibilityRule();

	public abstract TextFormat getColor();

	public abstract Collection<String> getPlayerList();

	public abstract AbstractScoreboardTeam.VisibilityRule getDeathMessageVisibilityRule();

	public abstract AbstractScoreboardTeam.CollisionRule getCollisionRule();

	public static enum CollisionRule {
		field_1437("always", 0),
		field_1435("never", 1),
		field_1434("pushOtherTeams", 2),
		field_1440("pushOwnTeam", 3);

		private static final Map<String, AbstractScoreboardTeam.CollisionRule> field_1438 = (Map<String, AbstractScoreboardTeam.CollisionRule>)Arrays.stream(values())
			.collect(Collectors.toMap(collisionRule -> collisionRule.field_1436, collisionRule -> collisionRule));
		public final String field_1436;
		public final int field_1433;

		@Nullable
		public static AbstractScoreboardTeam.CollisionRule method_1210(String string) {
			return (AbstractScoreboardTeam.CollisionRule)field_1438.get(string);
		}

		private CollisionRule(String string2, int j) {
			this.field_1436 = string2;
			this.field_1433 = j;
		}

		public TextComponent method_1209() {
			return new TranslatableTextComponent("team.collision." + this.field_1436);
		}
	}

	public static enum VisibilityRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		HIDDEN_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
		HIDDEN_FOR_TEAM("hideForOwnTeam", 3);

		private static final Map<String, AbstractScoreboardTeam.VisibilityRule> field_1447 = (Map<String, AbstractScoreboardTeam.VisibilityRule>)Arrays.stream(
				values()
			)
			.collect(Collectors.toMap(visibilityRule -> visibilityRule.field_1445, visibilityRule -> visibilityRule));
		public final String field_1445;
		public final int field_1441;

		@Nullable
		public static AbstractScoreboardTeam.VisibilityRule method_1213(String string) {
			return (AbstractScoreboardTeam.VisibilityRule)field_1447.get(string);
		}

		private VisibilityRule(String string2, int j) {
			this.field_1445 = string2;
			this.field_1441 = j;
		}

		public TextComponent method_1214() {
			return new TranslatableTextComponent("team.visibility." + this.field_1445);
		}
	}
}
