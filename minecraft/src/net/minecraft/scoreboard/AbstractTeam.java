package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class AbstractTeam {
	public boolean isEqual(@Nullable AbstractTeam team) {
		return team == null ? false : this == team;
	}

	public abstract String getName();

	/**
	 * Decorates the name of an entity with the prefix, suffix and color of this team.
	 * 
	 * @param name the name to be decorated
	 */
	public abstract MutableText decorateName(Text name);

	public abstract boolean shouldShowFriendlyInvisibles();

	public abstract boolean isFriendlyFireAllowed();

	public abstract AbstractTeam.VisibilityRule getNameTagVisibilityRule();

	public abstract Formatting getColor();

	public abstract Collection<String> getPlayerList();

	public abstract AbstractTeam.VisibilityRule getDeathMessageVisibilityRule();

	public abstract AbstractTeam.CollisionRule getCollisionRule();

	public static enum CollisionRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		PUSH_OTHER_TEAMS("pushOtherTeams", 2),
		PUSH_OWN_TEAM("pushOwnTeam", 3);

		private static final Map<String, AbstractTeam.CollisionRule> COLLISION_RULES = (Map<String, AbstractTeam.CollisionRule>)Arrays.stream(values())
			.collect(Collectors.toMap(collisionRule -> collisionRule.name, collisionRule -> collisionRule));
		public final String name;
		public final int value;

		@Nullable
		public static AbstractTeam.CollisionRule getRule(String name) {
			return (AbstractTeam.CollisionRule)COLLISION_RULES.get(name);
		}

		private CollisionRule(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public Text getDisplayName() {
			return new TranslatableText("team.collision." + this.name);
		}
	}

	public static enum VisibilityRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

		private static final Map<String, AbstractTeam.VisibilityRule> VISIBILITY_RULES = (Map<String, AbstractTeam.VisibilityRule>)Arrays.stream(values())
			.collect(Collectors.toMap(visibilityRule -> visibilityRule.name, visibilityRule -> visibilityRule));
		public final String name;
		public final int value;

		public static String[] method_35595() {
			return (String[])VISIBILITY_RULES.keySet().toArray(new String[VISIBILITY_RULES.size()]);
		}

		@Nullable
		public static AbstractTeam.VisibilityRule getRule(String name) {
			return (AbstractTeam.VisibilityRule)VISIBILITY_RULES.get(name);
		}

		private VisibilityRule(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public Text getDisplayName() {
			return new TranslatableText("team.visibility." + this.name);
		}
	}
}
