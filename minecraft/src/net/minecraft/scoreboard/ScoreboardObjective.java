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
	private Text displayName;
	private Text field_24194;
	private ScoreboardCriterion.RenderType renderType;

	public ScoreboardObjective(Scoreboard scoreboard, String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType) {
		this.scoreboard = scoreboard;
		this.name = name;
		this.criterion = criterion;
		this.displayName = displayName;
		this.field_24194 = this.method_27441();
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

	public Text getDisplayName() {
		return this.displayName;
	}

	private Text method_27441() {
		return Texts.bracketed(
			this.displayName.shallowCopy().styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.name))))
		);
	}

	public Text toHoverableText() {
		return this.field_24194;
	}

	public void setDisplayName(Text name) {
		this.displayName = name;
		this.field_24194 = this.method_27441();
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
