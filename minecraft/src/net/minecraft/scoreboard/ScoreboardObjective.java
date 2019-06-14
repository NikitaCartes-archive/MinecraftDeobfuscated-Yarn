package net.minecraft.scoreboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScoreboardObjective {
	private final Scoreboard field_1404;
	private final String name;
	private final ScoreboardCriterion field_1406;
	private Text displayName;
	private ScoreboardCriterion.RenderType renderType;

	public ScoreboardObjective(Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion, Text text, ScoreboardCriterion.RenderType renderType) {
		this.field_1404 = scoreboard;
		this.name = string;
		this.field_1406 = scoreboardCriterion;
		this.displayName = text;
		this.renderType = renderType;
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

	public Text getDisplayName() {
		return this.displayName;
	}

	public Text toHoverableText() {
		return Texts.bracketed(
			this.displayName.deepCopy().styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText(this.getName()))))
		);
	}

	public void setDisplayName(Text text) {
		this.displayName = text;
		this.field_1404.updateExistingObjective(this);
	}

	public ScoreboardCriterion.RenderType getRenderType() {
		return this.renderType;
	}

	public void setRenderType(ScoreboardCriterion.RenderType renderType) {
		this.renderType = renderType;
		this.field_1404.updateExistingObjective(this);
	}
}
