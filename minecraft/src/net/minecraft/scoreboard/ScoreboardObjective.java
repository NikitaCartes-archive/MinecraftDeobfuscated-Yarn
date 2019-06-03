package net.minecraft.scoreboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScoreboardObjective {
	private final Scoreboard scoreboard;
	private final String name;
	private final ScoreboardCriterion criterion;
	private Text field_1402;
	private ScoreboardCriterion.RenderType renderType;

	public ScoreboardObjective(Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion, Text text, ScoreboardCriterion.RenderType renderType) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.criterion = scoreboardCriterion;
		this.field_1402 = text;
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

	public Text method_1114() {
		return this.field_1402;
	}

	public Text method_1120() {
		return Texts.bracketed(
			this.field_1402.deepCopy().styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText(this.getName()))))
		);
	}

	public void method_1121(Text text) {
		this.field_1402 = text;
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
