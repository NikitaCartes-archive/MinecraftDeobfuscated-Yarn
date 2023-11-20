package net.minecraft.scoreboard;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public interface ScoreHolder {
	String WILDCARD_NAME = "*";
	ScoreHolder WILDCARD = new ScoreHolder() {
		@Override
		public String getNameForScoreboard() {
			return "*";
		}
	};

	/**
	 * {@return the name uniquely identifying the score holder}
	 * 
	 * <p>Unlike {@link net.minecraft.entity.Entity#getName}, this is guaranteed to be unique. This is the UUID
	 * for all entities except players (which use the player's username).
	 * 
	 * @see net.minecraft.entity.Entity#getName
	 * @see net.minecraft.entity.Entity#getUuidAsString
	 */
	String getNameForScoreboard();

	@Nullable
	default Text getDisplayName() {
		return null;
	}

	default Text getStyledDisplayName() {
		Text text = this.getDisplayName();
		return text != null
			? text.copy().styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(this.getNameForScoreboard()))))
			: Text.literal(this.getNameForScoreboard());
	}

	static ScoreHolder fromName(String name) {
		if (name.equals("*")) {
			return WILDCARD;
		} else {
			final Text text = Text.literal(name);
			return new ScoreHolder() {
				@Override
				public String getNameForScoreboard() {
					return name;
				}

				@Override
				public Text getStyledDisplayName() {
					return text;
				}
			};
		}
	}

	static ScoreHolder fromProfile(GameProfile gameProfile) {
		final String string = gameProfile.getName();
		return new ScoreHolder() {
			@Override
			public String getNameForScoreboard() {
				return string;
			}
		};
	}
}
