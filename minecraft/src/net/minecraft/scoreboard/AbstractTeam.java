package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class AbstractTeam {
	public boolean isEqual(@Nullable AbstractTeam abstractTeam) {
		return abstractTeam == null ? false : this == abstractTeam;
	}

	public abstract String getName();

	public abstract Text method_1198(Text text);

	@Environment(EnvType.CLIENT)
	public abstract boolean shouldShowFriendlyInvisibles();

	public abstract boolean isFriendlyFireAllowed();

	@Environment(EnvType.CLIENT)
	public abstract AbstractTeam.VisibilityRule getNameTagVisibilityRule();

	public abstract Formatting getColor();

	public abstract Collection<String> getPlayerList();

	public abstract AbstractTeam.VisibilityRule getDeathMessageVisibilityRule();

	public abstract AbstractTeam.CollisionRule getCollisionRule();

	public static enum CollisionRule {
		field_1437("always", 0),
		field_1435("never", 1),
		field_1434("pushOtherTeams", 2),
		field_1440("pushOwnTeam", 3);

		private static final Map<String, AbstractTeam.CollisionRule> COLLISION_RULES = (Map<String, AbstractTeam.CollisionRule>)Arrays.stream(values())
			.collect(Collectors.toMap(collisionRule -> collisionRule.name, collisionRule -> collisionRule));
		public final String name;
		public final int value;

		@Nullable
		public static AbstractTeam.CollisionRule getRule(String string) {
			return (AbstractTeam.CollisionRule)COLLISION_RULES.get(string);
		}

		private CollisionRule(String string2, int j) {
			this.name = string2;
			this.value = j;
		}

		public Text method_1209() {
			return new TranslatableText("team.collision." + this.name);
		}
	}

	public static enum VisibilityRule {
		field_1442("always", 0),
		field_1443("never", 1),
		field_1444("hideForOtherTeams", 2),
		field_1446("hideForOwnTeam", 3);

		private static final Map<String, AbstractTeam.VisibilityRule> VISIBILITY_RULES = (Map<String, AbstractTeam.VisibilityRule>)Arrays.stream(values())
			.collect(Collectors.toMap(visibilityRule -> visibilityRule.name, visibilityRule -> visibilityRule));
		public final String name;
		public final int value;

		@Nullable
		public static AbstractTeam.VisibilityRule getRule(String string) {
			return (AbstractTeam.VisibilityRule)VISIBILITY_RULES.get(string);
		}

		private VisibilityRule(String string2, int j) {
			this.name = string2;
			this.value = j;
		}

		public Text method_1214() {
			return new TranslatableText("team.visibility." + this.name);
		}
	}
}
