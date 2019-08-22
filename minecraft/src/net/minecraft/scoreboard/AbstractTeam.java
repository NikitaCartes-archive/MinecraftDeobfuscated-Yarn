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

	public abstract Text modifyText(Text text);

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
		ALWAYS("always", 0),
		NEVER("never", 1),
		PUSH_OTHER_TEAMS("pushOtherTeams", 2),
		PUSH_OWN_TEAM("pushOwnTeam", 3);

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

		public Text getTranslationKey() {
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

		@Nullable
		public static AbstractTeam.VisibilityRule getRule(String string) {
			return (AbstractTeam.VisibilityRule)VISIBILITY_RULES.get(string);
		}

		private VisibilityRule(String string2, int j) {
			this.name = string2;
			this.value = j;
		}

		public Text getTranslationKey() {
			return new TranslatableText("team.visibility." + this.name);
		}
	}
}
