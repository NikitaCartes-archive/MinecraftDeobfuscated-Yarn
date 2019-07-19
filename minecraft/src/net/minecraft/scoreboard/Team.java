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
	private Text displayName;
	private Text prefix = new LiteralText("");
	private Text suffix = new LiteralText("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
	private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
	private Formatting color = Formatting.RESET;
	private AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.ALWAYS;

	public Team(Scoreboard scoreboard, String name) {
		this.scoreboard = scoreboard;
		this.name = name;
		this.displayName = new LiteralText(name);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public Text getFormattedName() {
		Text text = Texts.bracketed(
			this.displayName
				.deepCopy()
				.styled(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.name))))
		);
		Formatting formatting = this.getColor();
		if (formatting != Formatting.RESET) {
			text.formatted(formatting);
		}

		return text;
	}

	public void setDisplayName(Text text) {
		if (text == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.displayName = text;
			this.scoreboard.updateScoreboardTeam(this);
		}
	}

	public void setPrefix(@Nullable Text text) {
		this.prefix = (Text)(text == null ? new LiteralText("") : text.deepCopy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public Text getPrefix() {
		return this.prefix;
	}

	public void setSuffix(@Nullable Text text) {
		this.suffix = (Text)(text == null ? new LiteralText("") : text.deepCopy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public Text getSuffix() {
		return this.suffix;
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Override
	public Text modifyText(Text text) {
		Text text2 = new LiteralText("").append(this.prefix).append(text).append(this.suffix);
		Formatting formatting = this.getColor();
		if (formatting != Formatting.RESET) {
			text2.formatted(formatting);
		}

		return text2;
	}

	public static Text modifyText(@Nullable AbstractTeam abstractTeam, Text text) {
		return abstractTeam == null ? text.deepCopy() : abstractTeam.modifyText(text);
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return this.friendlyFire;
	}

	public void setFriendlyFireAllowed(boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public boolean shouldShowFriendlyInvisibles() {
		return this.showFriendlyInvisibles;
	}

	public void setShowFriendlyInvisibles(boolean showFriendlyInvisible) {
		this.showFriendlyInvisibles = showFriendlyInvisible;
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

	public void setColor(Formatting color) {
		this.color = color;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public Formatting getColor() {
		return this.color;
	}
}
