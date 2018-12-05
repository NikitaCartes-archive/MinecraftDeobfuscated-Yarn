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
	private TextComponent field_1402;
	private ScoreboardCriterion.Type criterionType;

	public ScoreboardObjective(
		Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion, TextComponent textComponent, ScoreboardCriterion.Type type
	) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.criterion = scoreboardCriterion;
		this.field_1402 = textComponent;
		this.criterionType = type;
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
		return this.field_1402;
	}

	public TextComponent getTextComponent() {
		return TextFormatter.bracketed(
			this.field_1402.clone().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getName()))))
		);
	}

	public void method_1121(TextComponent textComponent) {
		this.field_1402 = textComponent;
		this.scoreboard.updateExistingObjective(this);
	}

	public ScoreboardCriterion.Type getCriterionType() {
		return this.criterionType;
	}

	public void setCriterionType(ScoreboardCriterion.Type type) {
		this.criterionType = type;
		this.scoreboard.updateExistingObjective(this);
	}
}
