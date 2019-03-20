package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.HoverEvent;

public class ScoreboardTeam extends AbstractScoreboardTeam {
	private final Scoreboard scoreboard;
	private final String name;
	private final Set<String> playerList = Sets.<String>newHashSet();
	private TextComponent displayName;
	private TextComponent prefix = new StringTextComponent("");
	private TextComponent suffix = new StringTextComponent("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractScoreboardTeam.VisibilityRule nameTagVisibilityRule = AbstractScoreboardTeam.VisibilityRule.ALWAYS;
	private AbstractScoreboardTeam.VisibilityRule deathMessageVisibilityRule = AbstractScoreboardTeam.VisibilityRule.ALWAYS;
	private TextFormat color = TextFormat.field_1070;
	private AbstractScoreboardTeam.CollisionRule collisionRule = AbstractScoreboardTeam.CollisionRule.field_1437;

	public ScoreboardTeam(Scoreboard scoreboard, String string) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.displayName = new StringTextComponent(string);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public TextComponent getDisplayName() {
		return this.displayName;
	}

	public TextComponent method_1148() {
		TextComponent textComponent = TextFormatter.bracketed(
			this.displayName
				.copy()
				.modifyStyle(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.name))))
		);
		TextFormat textFormat = this.getColor();
		if (textFormat != TextFormat.field_1070) {
			textComponent.applyFormat(textFormat);
		}

		return textComponent;
	}

	public void setDisplayName(TextComponent textComponent) {
		if (textComponent == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.displayName = textComponent;
			this.scoreboard.updateScoreboardTeam(this);
		}
	}

	public void setPrefix(@Nullable TextComponent textComponent) {
		this.prefix = (TextComponent)(textComponent == null ? new StringTextComponent("") : textComponent.copy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public TextComponent getPrefix() {
		return this.prefix;
	}

	public void setSuffix(@Nullable TextComponent textComponent) {
		this.suffix = (TextComponent)(textComponent == null ? new StringTextComponent("") : textComponent.copy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public TextComponent getSuffix() {
		return this.suffix;
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Override
	public TextComponent modifyText(TextComponent textComponent) {
		TextComponent textComponent2 = new StringTextComponent("").append(this.prefix).append(textComponent).append(this.suffix);
		TextFormat textFormat = this.getColor();
		if (textFormat != TextFormat.field_1070) {
			textComponent2.applyFormat(textFormat);
		}

		return textComponent2;
	}

	public static TextComponent modifyText(@Nullable AbstractScoreboardTeam abstractScoreboardTeam, TextComponent textComponent) {
		return abstractScoreboardTeam == null ? textComponent.copy() : abstractScoreboardTeam.modifyText(textComponent);
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return this.friendlyFire;
	}

	public void setFriendlyFireAllowed(boolean bl) {
		this.friendlyFire = bl;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public boolean shouldShowFriendlyInvisibles() {
		return this.showFriendlyInvisibles;
	}

	public void setShowFriendlyInvisibles(boolean bl) {
		this.showFriendlyInvisibles = bl;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public AbstractScoreboardTeam.VisibilityRule getNameTagVisibilityRule() {
		return this.nameTagVisibilityRule;
	}

	@Override
	public AbstractScoreboardTeam.VisibilityRule getDeathMessageVisibilityRule() {
		return this.deathMessageVisibilityRule;
	}

	public void setNameTagVisibilityRule(AbstractScoreboardTeam.VisibilityRule visibilityRule) {
		this.nameTagVisibilityRule = visibilityRule;
		this.scoreboard.updateScoreboardTeam(this);
	}

	public void setDeathMessageVisibilityRule(AbstractScoreboardTeam.VisibilityRule visibilityRule) {
		this.deathMessageVisibilityRule = visibilityRule;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public AbstractScoreboardTeam.CollisionRule getCollisionRule() {
		return this.collisionRule;
	}

	public void setCollisionRule(AbstractScoreboardTeam.CollisionRule collisionRule) {
		this.collisionRule = collisionRule;
		this.scoreboard.updateScoreboardTeam(this);
	}

	public int getFriendlyFlagsBitwise() {
		int i = 0;
		if (this.isFriendlyFireAllowed()) {
			i |= 1;
		}

		if (this.shouldShowFriendlyInvisibles()) {
			i |= 2;
		}

		return i;
	}

	@Environment(EnvType.CLIENT)
	public void setFriendlyFlagsBitwise(int i) {
		this.setFriendlyFireAllowed((i & 1) > 0);
		this.setShowFriendlyInvisibles((i & 2) > 0);
	}

	public void setColor(TextFormat textFormat) {
		this.color = textFormat;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public TextFormat getColor() {
		return this.color;
	}
}
