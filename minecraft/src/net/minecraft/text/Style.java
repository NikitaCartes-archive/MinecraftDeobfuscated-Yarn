package net.minecraft.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;

public class Style {
	private Style parent;
	private Formatting color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underline;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private ClickEvent clickEvent;
	private HoverEvent hoverEvent;
	private String insertion;
	private static final Style ROOT = new Style() {
		@Nullable
		@Override
		public Formatting getColor() {
			return null;
		}

		@Override
		public boolean isBold() {
			return false;
		}

		@Override
		public boolean isItalic() {
			return false;
		}

		@Override
		public boolean isStrikethrough() {
			return false;
		}

		@Override
		public boolean isUnderlined() {
			return false;
		}

		@Override
		public boolean isObfuscated() {
			return false;
		}

		@Nullable
		@Override
		public ClickEvent getClickEvent() {
			return null;
		}

		@Nullable
		@Override
		public HoverEvent getHoverEvent() {
			return null;
		}

		@Nullable
		@Override
		public String getInsertion() {
			return null;
		}

		@Override
		public Style setColor(Formatting color) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setBold(Boolean bold) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setItalic(Boolean italic) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setStrikethrough(Boolean strikethrough) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setUnderline(Boolean underline) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setObfuscated(Boolean obfuscated) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setClickEvent(ClickEvent clickEvent) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setHoverEvent(HoverEvent clickEvent) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Style setParent(Style parent) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Style.ROOT";
		}

		@Override
		public Style deepCopy() {
			return this;
		}

		@Override
		public Style copy() {
			return this;
		}

		@Override
		public String asString() {
			return "";
		}
	};

	@Nullable
	public Formatting getColor() {
		return this.color == null ? this.getParent().getColor() : this.color;
	}

	public boolean isBold() {
		return this.bold == null ? this.getParent().isBold() : this.bold;
	}

	public boolean isItalic() {
		return this.italic == null ? this.getParent().isItalic() : this.italic;
	}

	public boolean isStrikethrough() {
		return this.strikethrough == null ? this.getParent().isStrikethrough() : this.strikethrough;
	}

	public boolean isUnderlined() {
		return this.underline == null ? this.getParent().isUnderlined() : this.underline;
	}

	public boolean isObfuscated() {
		return this.obfuscated == null ? this.getParent().isObfuscated() : this.obfuscated;
	}

	public boolean isEmpty() {
		return this.bold == null
			&& this.italic == null
			&& this.strikethrough == null
			&& this.underline == null
			&& this.obfuscated == null
			&& this.color == null
			&& this.clickEvent == null
			&& this.hoverEvent == null
			&& this.insertion == null;
	}

	@Nullable
	public ClickEvent getClickEvent() {
		return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
	}

	@Nullable
	public HoverEvent getHoverEvent() {
		return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
	}

	@Nullable
	public String getInsertion() {
		return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
	}

	public Style setColor(Formatting color) {
		this.color = color;
		return this;
	}

	public Style setBold(Boolean bold) {
		this.bold = bold;
		return this;
	}

	public Style setItalic(Boolean italic) {
		this.italic = italic;
		return this;
	}

	public Style setStrikethrough(Boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}

	public Style setUnderline(Boolean underline) {
		this.underline = underline;
		return this;
	}

	public Style setObfuscated(Boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}

	public Style setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
		return this;
	}

	public Style setHoverEvent(HoverEvent clickEvent) {
		this.hoverEvent = clickEvent;
		return this;
	}

	public Style setInsertion(String insertion) {
		this.insertion = insertion;
		return this;
	}

	public Style setParent(Style parent) {
		this.parent = parent;
		return this;
	}

	public String asString() {
		if (this.isEmpty()) {
			return this.parent != null ? this.parent.asString() : "";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			if (this.getColor() != null) {
				stringBuilder.append(this.getColor());
			}

			if (this.isBold()) {
				stringBuilder.append(Formatting.BOLD);
			}

			if (this.isItalic()) {
				stringBuilder.append(Formatting.ITALIC);
			}

			if (this.isUnderlined()) {
				stringBuilder.append(Formatting.UNDERLINE);
			}

			if (this.isObfuscated()) {
				stringBuilder.append(Formatting.OBFUSCATED);
			}

			if (this.isStrikethrough()) {
				stringBuilder.append(Formatting.STRIKETHROUGH);
			}

			return stringBuilder.toString();
		}
	}

	private Style getParent() {
		return this.parent == null ? ROOT : this.parent;
	}

	public String toString() {
		return "Style{hasParent="
			+ (this.parent != null)
			+ ", color="
			+ this.color
			+ ", bold="
			+ this.bold
			+ ", italic="
			+ this.italic
			+ ", underlined="
			+ this.underline
			+ ", obfuscated="
			+ this.obfuscated
			+ ", clickEvent="
			+ this.getClickEvent()
			+ ", hoverEvent="
			+ this.getHoverEvent()
			+ ", insertion="
			+ this.getInsertion()
			+ '}';
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Style)) {
			return false;
		} else {
			Style style = (Style)obj;
			return this.isBold() == style.isBold()
				&& this.getColor() == style.getColor()
				&& this.isItalic() == style.isItalic()
				&& this.isObfuscated() == style.isObfuscated()
				&& this.isStrikethrough() == style.isStrikethrough()
				&& this.isUnderlined() == style.isUnderlined()
				&& (this.getClickEvent() != null ? this.getClickEvent().equals(style.getClickEvent()) : style.getClickEvent() == null)
				&& (this.getHoverEvent() != null ? this.getHoverEvent().equals(style.getHoverEvent()) : style.getHoverEvent() == null)
				&& (this.getInsertion() != null ? this.getInsertion().equals(style.getInsertion()) : style.getInsertion() == null);
		}
	}

	public int hashCode() {
		return Objects.hash(
			new Object[]{this.color, this.bold, this.italic, this.underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion}
		);
	}

	public Style deepCopy() {
		Style style = new Style();
		style.bold = this.bold;
		style.italic = this.italic;
		style.strikethrough = this.strikethrough;
		style.underline = this.underline;
		style.obfuscated = this.obfuscated;
		style.color = this.color;
		style.clickEvent = this.clickEvent;
		style.hoverEvent = this.hoverEvent;
		style.parent = this.parent;
		style.insertion = this.insertion;
		return style;
	}

	public Style copy() {
		Style style = new Style();
		style.setBold(this.isBold());
		style.setItalic(this.isItalic());
		style.setStrikethrough(this.isStrikethrough());
		style.setUnderline(this.isUnderlined());
		style.setObfuscated(this.isObfuscated());
		style.setColor(this.getColor());
		style.setClickEvent(this.getClickEvent());
		style.setHoverEvent(this.getHoverEvent());
		style.setInsertion(this.getInsertion());
		return style;
	}

	public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
		@Nullable
		public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				Style style = new Style();
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				if (jsonObject == null) {
					return null;
				} else {
					if (jsonObject.has("bold")) {
						style.bold = jsonObject.get("bold").getAsBoolean();
					}

					if (jsonObject.has("italic")) {
						style.italic = jsonObject.get("italic").getAsBoolean();
					}

					if (jsonObject.has("underlined")) {
						style.underline = jsonObject.get("underlined").getAsBoolean();
					}

					if (jsonObject.has("strikethrough")) {
						style.strikethrough = jsonObject.get("strikethrough").getAsBoolean();
					}

					if (jsonObject.has("obfuscated")) {
						style.obfuscated = jsonObject.get("obfuscated").getAsBoolean();
					}

					if (jsonObject.has("color")) {
						style.color = jsonDeserializationContext.deserialize(jsonObject.get("color"), Formatting.class);
					}

					if (jsonObject.has("insertion")) {
						style.insertion = jsonObject.get("insertion").getAsString();
					}

					if (jsonObject.has("clickEvent")) {
						JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "clickEvent");
						String string = JsonHelper.getString(jsonObject2, "action", null);
						ClickEvent.Action action = string == null ? null : ClickEvent.Action.byName(string);
						String string2 = JsonHelper.getString(jsonObject2, "value", null);
						if (action != null && string2 != null && action.isUserDefinable()) {
							style.clickEvent = new ClickEvent(action, string2);
						}
					}

					if (jsonObject.has("hoverEvent")) {
						JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "hoverEvent");
						String string = JsonHelper.getString(jsonObject2, "action", null);
						HoverEvent.Action action2 = string == null ? null : HoverEvent.Action.byName(string);
						Text text = jsonDeserializationContext.deserialize(jsonObject2.get("value"), Text.class);
						if (action2 != null && text != null && action2.isUserDefinable()) {
							style.hoverEvent = new HoverEvent(action2, text);
						}
					}

					return style;
				}
			} else {
				return null;
			}
		}

		@Nullable
		public JsonElement serialize(Style style, Type type, JsonSerializationContext jsonSerializationContext) {
			if (style.isEmpty()) {
				return null;
			} else {
				JsonObject jsonObject = new JsonObject();
				if (style.bold != null) {
					jsonObject.addProperty("bold", style.bold);
				}

				if (style.italic != null) {
					jsonObject.addProperty("italic", style.italic);
				}

				if (style.underline != null) {
					jsonObject.addProperty("underlined", style.underline);
				}

				if (style.strikethrough != null) {
					jsonObject.addProperty("strikethrough", style.strikethrough);
				}

				if (style.obfuscated != null) {
					jsonObject.addProperty("obfuscated", style.obfuscated);
				}

				if (style.color != null) {
					jsonObject.add("color", jsonSerializationContext.serialize(style.color));
				}

				if (style.insertion != null) {
					jsonObject.add("insertion", jsonSerializationContext.serialize(style.insertion));
				}

				if (style.clickEvent != null) {
					JsonObject jsonObject2 = new JsonObject();
					jsonObject2.addProperty("action", style.clickEvent.getAction().getName());
					jsonObject2.addProperty("value", style.clickEvent.getValue());
					jsonObject.add("clickEvent", jsonObject2);
				}

				if (style.hoverEvent != null) {
					JsonObject jsonObject2 = new JsonObject();
					jsonObject2.addProperty("action", style.hoverEvent.getAction().getName());
					jsonObject2.add("value", jsonSerializationContext.serialize(style.hoverEvent.getValue()));
					jsonObject.add("hoverEvent", jsonObject2);
				}

				return jsonObject;
			}
		}
	}
}
