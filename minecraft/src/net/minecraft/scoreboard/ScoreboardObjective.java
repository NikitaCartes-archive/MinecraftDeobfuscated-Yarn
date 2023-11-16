package net.minecraft.scoreboard;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScoreboardObjective {
	private final Scoreboard scoreboard;
	private final String name;
	private final ScoreboardCriterion criterion;
	private Text displayName;
	private Text bracketedDisplayName;
	private ScoreboardCriterion.RenderType renderType;
	private boolean displayAutoUpdate;
	@Nullable
	private NumberFormat numberFormat;

	public ScoreboardObjective(
		Scoreboard scoreboard,
		String name,
		ScoreboardCriterion criterion,
		Text displayName,
		ScoreboardCriterion.RenderType renderType,
		boolean displayAutoUpdate,
		@Nullable NumberFormat numberFormat
	) {
		this.scoreboard = scoreboard;
		this.name = name;
		this.criterion = criterion;
		this.displayName = displayName;
		this.bracketedDisplayName = this.generateBracketedDisplayName();
		this.renderType = renderType;
		this.displayAutoUpdate = displayAutoUpdate;
		this.numberFormat = numberFormat;
	}

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

	public boolean shouldDisplayAutoUpdate() {
		return this.displayAutoUpdate;
	}

	@Nullable
	public NumberFormat getNumberFormat() {
		return this.numberFormat;
	}

	public NumberFormat getNumberFormatOr(NumberFormat format) {
		return (NumberFormat)Objects.requireNonNullElse(this.numberFormat, format);
	}

	private Text generateBracketedDisplayName() {
		return Texts.bracketed(this.displayName.copy().styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(this.name)))));
	}

	public Text toHoverableText() {
		return this.bracketedDisplayName;
	}

	public void setDisplayName(Text name) {
		this.displayName = name;
		this.bracketedDisplayName = this.generateBracketedDisplayName();
		this.scoreboard.updateExistingObjective(this);
	}

	public ScoreboardCriterion.RenderType getRenderType() {
		return this.renderType;
	}

	public void setRenderType(ScoreboardCriterion.RenderType renderType) {
		this.renderType = renderType;
		this.scoreboard.updateExistingObjective(this);
	}

	public void setDisplayAutoUpdate(boolean displayAutoUpdate) {
		this.displayAutoUpdate = displayAutoUpdate;
		this.scoreboard.updateExistingObjective(this);
	}

	public void setNumberFormat(@Nullable NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
		this.scoreboard.updateExistingObjective(this);
	}
}
