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
	private final Scoreboard field_1420;
	private final String name;
	private final Set<String> playerList = Sets.<String>newHashSet();
	private Text displayName;
	private Text prefix = new LiteralText("");
	private Text suffix = new LiteralText("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.field_1442;
	private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.field_1442;
	private Formatting color = Formatting.field_1070;
	private AbstractTeam.CollisionRule field_1425 = AbstractTeam.CollisionRule.field_1437;

	public Team(Scoreboard scoreboard, String string) {
		this.field_1420 = scoreboard;
		this.name = string;
		this.displayName = new LiteralText(string);
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
				.styled(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText(this.name))))
		);
		Formatting formatting = this.getColor();
		if (formatting != Formatting.field_1070) {
			text.formatted(formatting);
		}

		return text;
	}

	public void setDisplayName(Text text) {
		if (text == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.displayName = text;
			this.field_1420.updateScoreboardTeam(this);
		}
	}

	public void setPrefix(@Nullable Text text) {
		this.prefix = (Text)(text == null ? new LiteralText("") : text.deepCopy());
		this.field_1420.updateScoreboardTeam(this);
	}

	public Text getPrefix() {
		return this.prefix;
	}

	public void setSuffix(@Nullable Text text) {
		this.suffix = (Text)(text == null ? new LiteralText("") : text.deepCopy());
		this.field_1420.updateScoreboardTeam(this);
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
		if (formatting != Formatting.field_1070) {
			text2.formatted(formatting);
		}

		return text2;
	}

	public static Text method_1142(@Nullable AbstractTeam abstractTeam, Text text) {
		return abstractTeam == null ? text.deepCopy() : abstractTeam.modifyText(text);
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
	public AbstractTeam.VisibilityRule getNameTagVisibilityRule() {
		return this.nameTagVisibilityRule;
	}

	@Override
	public AbstractTeam.VisibilityRule getDeathMessageVisibilityRule() {
		return this.deathMessageVisibilityRule;
	}

	public void setNameTagVisibilityRule(AbstractTeam.VisibilityRule visibilityRule) {
		this.nameTagVisibilityRule = visibilityRule;
		this.field_1420.updateScoreboardTeam(this);
	}

	public void setDeathMessageVisibilityRule(AbstractTeam.VisibilityRule visibilityRule) {
		this.deathMessageVisibilityRule = visibilityRule;
		this.field_1420.updateScoreboardTeam(this);
	}

	@Override
	public AbstractTeam.CollisionRule getCollisionRule() {
		return this.field_1425;
	}

	public void method_1145(AbstractTeam.CollisionRule collisionRule) {
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

	public void setColor(Formatting formatting) {
		this.color = formatting;
		this.field_1420.updateScoreboardTeam(this);
	}

	@Override
	public Formatting getColor() {
		return this.color;
	}
}
