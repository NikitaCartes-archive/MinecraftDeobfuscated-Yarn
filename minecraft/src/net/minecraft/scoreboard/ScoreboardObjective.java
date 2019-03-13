package net.minecraft.scoreboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.HoverEvent;

public class ScoreboardObjective {
	private final Scoreboard field_1404;
	private final String name;
	private final ScoreboardCriterion field_1406;
	private TextComponent field_1402;
	private ScoreboardCriterion.Type field_1403;

	public ScoreboardObjective(
		Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion, TextComponent textComponent, ScoreboardCriterion.Type type
	) {
		this.field_1404 = scoreboard;
		this.name = string;
		this.field_1406 = scoreboardCriterion;
		this.field_1402 = textComponent;
		this.field_1403 = type;
	}

	@Environment(EnvType.CLIENT)
	public Scoreboard method_1117() {
		return this.field_1404;
	}

	public String getName() {
		return this.name;
	}

	public ScoreboardCriterion method_1116() {
		return this.field_1406;
	}

	public TextComponent method_1114() {
		return this.field_1402;
	}

	public TextComponent method_1120() {
		return TextFormatter.bracketed(
			this.field_1402.copy().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getName()))))
		);
	}

	public void method_1121(TextComponent textComponent) {
		this.field_1402 = textComponent;
		this.field_1404.updateExistingObjective(this);
	}

	public ScoreboardCriterion.Type method_1118() {
		return this.field_1403;
	}

	public void method_1115(ScoreboardCriterion.Type type) {
		this.field_1403 = type;
		this.field_1404.updateExistingObjective(this);
	}
}
