package net.minecraft.scoreboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;

public class ScoreboardObjective {
	private final Scoreboard scoreboard;
	private final String name;
	private final ScoreboardCriterion criterion;
	private Component displayName;
	private ScoreboardCriterion.RenderType renderType;

	public ScoreboardObjective(
		Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion, Component component, ScoreboardCriterion.RenderType renderType
	) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.criterion = scoreboardCriterion;
		this.displayName = component;
		this.renderType = renderType;
	}

	@Environment(EnvType.CLIENT)
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public String getName() {
		return this.name;
	}

	public ScoreboardCriterion getCriterion() {
		return this.criterion;
	}

	public Component getDisplayName() {
		return this.displayName;
	}

	public Component getTextComponent() {
		return Components.bracketed(
			this.displayName.copy().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TextComponent(this.getName()))))
		);
	}

	public void setDisplayName(Component component) {
		this.displayName = component;
		this.scoreboard.updateExistingObjective(this);
	}

	public ScoreboardCriterion.RenderType getRenderType() {
		return this.renderType;
	}

	public void setRenderType(ScoreboardCriterion.RenderType renderType) {
		this.renderType = renderType;
		this.scoreboard.updateExistingObjective(this);
	}
}
