package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2566;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ChatUtil;

public class ScoreTextComponent extends AbstractTextComponent implements class_2566 {
	private final String name;
	@Nullable
	private final EntitySelector selector;
	private final String objective;
	private String text = "";

	public ScoreTextComponent(String string, String string2) {
		this.name = string;
		this.objective = string2;
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var5) {
		}

		this.selector = entitySelector;
	}

	public String getName() {
		return this.name;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setText(String string) {
		this.text = string;
	}

	@Override
	public String getText() {
		return this.text;
	}

	private void method_10926(ServerCommandSource serverCommandSource) {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		if (minecraftServer != null && minecraftServer.method_3814() && ChatUtil.isEmpty(this.text)) {
			Scoreboard scoreboard = minecraftServer.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.method_1170(this.objective);
			if (scoreboard.playerHasObjective(this.name, scoreboardObjective)) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(this.name, scoreboardObjective);
				this.setText(String.format("%d", scoreboardPlayerScore.getScore()));
			} else {
				this.text = "";
			}
		}
	}

	public ScoreTextComponent getTextComponent() {
		ScoreTextComponent scoreTextComponent = new ScoreTextComponent(this.name, this.objective);
		scoreTextComponent.setText(this.text);
		return scoreTextComponent;
	}

	@Override
	public TextComponent method_10890(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		if (serverCommandSource == null) {
			return this.getTextComponent();
		} else {
			String string;
			if (this.selector != null) {
				List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
				if (list.isEmpty()) {
					string = this.name;
				} else {
					if (list.size() != 1) {
						throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
					}

					string = ((Entity)list.get(0)).getEntityName();
				}
			} else {
				string = this.name;
			}

			String string2 = entity != null && string.equals("*") ? entity.getEntityName() : string;
			ScoreTextComponent scoreTextComponent = new ScoreTextComponent(string2, this.objective);
			scoreTextComponent.setText(this.text);
			scoreTextComponent.method_10926(serverCommandSource);
			return scoreTextComponent;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ScoreTextComponent)) {
			return false;
		} else {
			ScoreTextComponent scoreTextComponent = (ScoreTextComponent)object;
			return this.name.equals(scoreTextComponent.name) && this.objective.equals(scoreTextComponent.objective) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "ScoreComponent{name='"
			+ this.name
			+ '\''
			+ "objective='"
			+ this.objective
			+ '\''
			+ ", siblings="
			+ this.children
			+ ", style="
			+ this.getStyle()
			+ '}';
	}
}
