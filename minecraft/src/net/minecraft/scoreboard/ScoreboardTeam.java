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
	private final Scoreboard field_1420;
	private final String name;
	private final Set<String> playerList = Sets.<String>newHashSet();
	private TextComponent field_1414;
	private TextComponent field_1418 = new StringTextComponent("");
	private TextComponent field_1419 = new StringTextComponent("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractScoreboardTeam.VisibilityRule field_1423 = AbstractScoreboardTeam.VisibilityRule.ALWAYS;
	private AbstractScoreboardTeam.VisibilityRule field_1422 = AbstractScoreboardTeam.VisibilityRule.ALWAYS;
	private TextFormat color = TextFormat.field_1070;
	private AbstractScoreboardTeam.CollisionRule field_1425 = AbstractScoreboardTeam.CollisionRule.field_1437;

	public ScoreboardTeam(Scoreboard scoreboard, String string) {
		this.field_1420 = scoreboard;
		this.name = string;
		this.field_1414 = new StringTextComponent(string);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public TextComponent method_1140() {
		return this.field_1414;
	}

	public TextComponent method_1148() {
		TextComponent textComponent = TextFormatter.bracketed(
			this.field_1414
				.copy()
				.modifyStyle(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.name))))
		);
		TextFormat textFormat = this.getColor();
		if (textFormat != TextFormat.field_1070) {
			textComponent.applyFormat(textFormat);
		}

		return textComponent;
	}

	public void method_1137(TextComponent textComponent) {
		if (textComponent == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.field_1414 = textComponent;
			this.field_1420.updateScoreboardTeam(this);
		}
	}

	public void method_1138(@Nullable TextComponent textComponent) {
		this.field_1418 = (TextComponent)(textComponent == null ? new StringTextComponent("") : textComponent.copy());
		this.field_1420.updateScoreboardTeam(this);
	}

	public TextComponent method_1144() {
		return this.field_1418;
	}

	public void method_1139(@Nullable TextComponent textComponent) {
		this.field_1419 = (TextComponent)(textComponent == null ? new StringTextComponent("") : textComponent.copy());
		this.field_1420.updateScoreboardTeam(this);
	}

	public TextComponent method_1136() {
		return this.field_1419;
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Override
	public TextComponent method_1198(TextComponent textComponent) {
		TextComponent textComponent2 = new StringTextComponent("").append(this.field_1418).append(textComponent).append(this.field_1419);
		TextFormat textFormat = this.getColor();
		if (textFormat != TextFormat.field_1070) {
			textComponent2.applyFormat(textFormat);
		}

		return textComponent2;
	}

	public static TextComponent method_1142(@Nullable AbstractScoreboardTeam abstractScoreboardTeam, TextComponent textComponent) {
		return abstractScoreboardTeam == null ? textComponent.copy() : abstractScoreboardTeam.method_1198(textComponent);
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return this.friendlyFire;
	}

	public void setFriendlyFireAllowed(boolean bl) {
		this.friendlyFire = bl;
		this.field_1420.updateScoreboardTeam(this);
	}

	@Override
	public boolean shouldShowFriendlyInvisibles() {
		return this.showFriendlyInvisibles;
	}

	public void setShowFriendlyInvisibles(boolean bl) {
		this.showFriendlyInvisibles = bl;
		this.field_1420.updateScoreboardTeam(this);
	}

	@Override
	public AbstractScoreboardTeam.VisibilityRule getNameTagVisibilityRule() {
		return this.field_1423;
	}

	@Override
	public AbstractScoreboardTeam.VisibilityRule getDeathMessageVisibilityRule() {
		return this.field_1422;
	}

	public void method_1149(AbstractScoreboardTeam.VisibilityRule visibilityRule) {
		this.field_1423 = visibilityRule;
		this.field_1420.updateScoreboardTeam(this);
	}

	public void method_1133(AbstractScoreboardTeam.VisibilityRule visibilityRule) {
		this.field_1422 = visibilityRule;
		this.field_1420.updateScoreboardTeam(this);
	}

	@Override
	public AbstractScoreboardTeam.CollisionRule getCollisionRule() {
		return this.field_1425;
	}

	public void method_1145(AbstractScoreboardTeam.CollisionRule collisionRule) {
		this.field_1425 = collisionRule;
		this.field_1420.updateScoreboardTeam(this);
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
		this.field_1420.updateScoreboardTeam(this);
	}

	@Override
	public TextFormat getColor() {
		return this.color;
	}
}
