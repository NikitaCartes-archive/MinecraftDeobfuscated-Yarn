package net.minecraft.scoreboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.HoverEvent;

public class ScoreboardObjective {
	private final Scoreboard scoreboard;
	private final String name;
	private final ScoreboardCriterion criterion;
	private TextComponent displayName;
	private ScoreboardCriterion.RenderType field_1403;

	public ScoreboardObjective(
		Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion, TextComponent textComponent, ScoreboardCriterion.RenderType renderType
	) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.criterion = scoreboardCriterion;
		this.displayName = textComponent;
		this.field_1403 = renderType;
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

	public TextComponent getDisplayName() {
		return this.displayName;
	}

	public TextComponent getTextComponent() {
		return TextFormatter.bracketed(
			this.displayName.copy().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getName()))))
		);
	}

	public void setDisplayName(TextComponent textComponent) {
		this.displayName = textComponent;
		this.scoreboard.updateExistingObjective(this);
	}

	public ScoreboardCriterion.RenderType method_1118() {
		return this.field_1403;
	}

	public void method_1115(ScoreboardCriterion.RenderType renderType) {
		this.field_1403 = renderType;
		this.scoreboard.updateExistingObjective(this);
	}
}
