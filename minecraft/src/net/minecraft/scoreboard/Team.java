package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;

public class Team extends AbstractTeam {
	private final Scoreboard scoreboard;
	private final String name;
	private final Set<String> playerList = Sets.<String>newHashSet();
	private Component displayName;
	private Component prefix = new TextComponent("");
	private Component suffix = new TextComponent("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.field_1442;
	private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.field_1442;
	private ChatFormat color = ChatFormat.field_1070;
	private AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.field_1437;

	public Team(Scoreboard scoreboard, String string) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.displayName = new TextComponent(string);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public Component getDisplayName() {
		return this.displayName;
	}

	public Component getFormattedName() {
		Component component = Components.bracketed(
			this.displayName
				.copy()
				.modifyStyle(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TextComponent(this.name))))
		);
		ChatFormat chatFormat = this.getColor();
		if (chatFormat != ChatFormat.field_1070) {
			component.applyFormat(chatFormat);
		}

		return component;
	}

	public void setDisplayName(Component component) {
		if (component == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.displayName = component;
			this.scoreboard.updateScoreboardTeam(this);
		}
	}

	public void setPrefix(@Nullable Component component) {
		this.prefix = (Component)(component == null ? new TextComponent("") : component.copy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public Component getPrefix() {
		return this.prefix;
	}

	public void setSuffix(@Nullable Component component) {
		this.suffix = (Component)(component == null ? new TextComponent("") : component.copy());
		this.scoreboard.updateScoreboardTeam(this);
	}

	public Component getSuffix() {
		return this.suffix;
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Override
	public Component modifyText(Component component) {
		Component component2 = new TextComponent("").append(this.prefix).append(component).append(this.suffix);
		ChatFormat chatFormat = this.getColor();
		if (chatFormat != ChatFormat.field_1070) {
			component2.applyFormat(chatFormat);
		}

		return component2;
	}

	public static Component modifyText(@Nullable AbstractTeam abstractTeam, Component component) {
		return abstractTeam == null ? component.copy() : abstractTeam.modifyText(component);
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

	public void setColor(ChatFormat chatFormat) {
		this.color = chatFormat;
		this.scoreboard.updateScoreboardTeam(this);
	}

	@Override
	public ChatFormat getColor() {
		return this.color;
	}
}
