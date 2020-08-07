package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class AbstractTeam {
	public boolean isEqual(@Nullable AbstractTeam abstractTeam) {
		return abstractTeam == null ? false : this == abstractTeam;
	}

	public abstract String getName();

	public abstract MutableText modifyText(Text text);

	@Environment(EnvType.CLIENT)
	public abstract boolean shouldShowFriendlyInvisibles();

	public abstract boolean isFriendlyFireAllowed();

	@Environment(EnvType.CLIENT)
	public abstract AbstractTeam.VisibilityRule getNameTagVisibilityRule();

	@Environment(EnvType.CLIENT)
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
		public static AbstractTeam.CollisionRule getRule(String name) {
			return (AbstractTeam.CollisionRule)COLLISION_RULES.get(name);
		}

		private CollisionRule(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public Text getTranslationKey() {
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
		public static AbstractTeam.VisibilityRule getRule(String name) {
			return (AbstractTeam.VisibilityRule)VISIBILITY_RULES.get(name);
		}

		private VisibilityRule(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public Text getTranslationKey() {
			return new TranslatableText("team.visibility." + this.name);
		}
	}
}
