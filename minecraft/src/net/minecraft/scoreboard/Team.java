package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public class Team extends AbstractTeam {
	private final Scoreboard scoreboard;
	private final String name;
	private final Set<String> playerList = Sets.<String>newHashSet();
	private Text field_1414;
	private Text field_1418 = new LiteralText("");
	private Text field_1419 = new LiteralText("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.field_1442;
	private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.field_1442;
	private Formatting color = Formatting.field_1070;
	private AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.field_1437;

	public Team(Scoreboard scoreboard, String string) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.field_1414 = new LiteralText(string);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public Text method_1140() {
		return this.field_1414;
	}

	public Text method_1148() {
		Text text = Texts.bracketed(
			this.field_1414
				.deepCopy()
				.styled(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText(this.name))))
		);
		Formatting formatting = this.getColor();
		if (formatting != Formatting.field_1070) {
			text.formatted(formatting);
		}

		return text;
	}

	public void method_1137(Text text) {
		if (text == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.field_1414 = text;
			this.scoreboard.updateScoreboardTeam(this);
		}
	}

	public void method_1138(@Nullable Text text) {
		this.field_1418 = (Text)(text == null ? new LiteralText("") : text.deepCopy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public Text method_1144() {
		return this.field_1418;
	}

	public void method_1139(@Nullable Text text) {
		this.field_1419 = (Text)(text == null ? new LiteralText("") : text.deepCopy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public Text method_1136() {
		return this.field_1419;
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Override
	public Text method_1198(Text text) {
		Text text2 = new LiteralText("").append(this.field_1418).append(text).append(this.field_1419);
		Formatting formatting = this.getColor();
		if (formatting != Formatting.field_1070) {
			text2.formatted(formatting);
		}

		return text2;
	}

	public static Text method_1142(@Nullable AbstractTeam abstractTeam, Text text) {
		return abstractTeam == null ? text.deepCopy() : abstractTeam.method_1198(text);
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
	public AbstractTeam.VisibilityRule getNameTagVisibilityRule() {
		return this.nameTagVisibilityRule;
	}

	@Override
	public AbstractTeam.VisibilityRule getDeathMessageVisibilityRule() {
		return this.deathMessageVisibilityRule;
	}

	public void setNameTagVisibilityRule(AbstractTeam.VisibilityRule visibilityRule) {
		this.nameTagVisibilityRule = visibilityRule;
		this.scoreboard.updateScoreboardTeam(this);
	}

	public void setDeathMessageVisibilityRule(AbstractTeam.VisibilityRule visibilityRule) {
		this.deathMessageVisibilityRule = visibilityRule;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public AbstractTeam.CollisionRule getCollisionRule() {
		return this.collisionRule;
	}

	public void setCollisionRule(AbstractTeam.CollisionRule collisionRule) {
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

	public void setColor(Formatting formatting) {
		this.color = formatting;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public Formatting getColor() {
		return this.color;
	}
}
