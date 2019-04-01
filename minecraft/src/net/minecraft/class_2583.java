package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;

public class class_2583 {
	private class_2583 field_11860;
	private class_124 field_11855;
	private Boolean field_11856;
	private Boolean field_11852;
	private Boolean field_11851;
	private Boolean field_11857;
	private Boolean field_11861;
	private class_2558 field_11853;
	private class_2568 field_11858;
	private String field_11859;
	private static final class_2583 field_11854 = new class_2583() {
		@Nullable
		@Override
		public class_124 method_10973() {
			return null;
		}

		@Override
		public boolean method_10984() {
			return false;
		}

		@Override
		public boolean method_10966() {
			return false;
		}

		@Override
		public boolean method_10986() {
			return false;
		}

		@Override
		public boolean method_10965() {
			return false;
		}

		@Override
		public boolean method_10987() {
			return false;
		}

		@Nullable
		@Override
		public class_2558 method_10970() {
			return null;
		}

		@Nullable
		@Override
		public class_2568 method_10969() {
			return null;
		}

		@Nullable
		@Override
		public String method_10955() {
			return null;
		}

		@Override
		public class_2583 method_10977(class_124 arg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10982(Boolean boolean_) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10978(Boolean boolean_) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10959(Boolean boolean_) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10968(Boolean boolean_) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10948(Boolean boolean_) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10958(class_2558 arg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10949(class_2568 arg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public class_2583 method_10985(class_2583 arg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Style.ROOT";
		}

		@Override
		public class_2583 method_10976() {
			return this;
		}

		@Override
		public class_2583 method_10960() {
			return this;
		}

		@Override
		public String method_10953() {
			return "";
		}
	};

	@Nullable
	public class_124 method_10973() {
		return this.field_11855 == null ? this.method_10989().method_10973() : this.field_11855;
	}

	public boolean method_10984() {
		return this.field_11856 == null ? this.method_10989().method_10984() : this.field_11856;
	}

	public boolean method_10966() {
		return this.field_11852 == null ? this.method_10989().method_10966() : this.field_11852;
	}

	public boolean method_10986() {
		return this.field_11857 == null ? this.method_10989().method_10986() : this.field_11857;
	}

	public boolean method_10965() {
		return this.field_11851 == null ? this.method_10989().method_10965() : this.field_11851;
	}

	public boolean method_10987() {
		return this.field_11861 == null ? this.method_10989().method_10987() : this.field_11861;
	}

	public boolean method_10967() {
		return this.field_11856 == null
			&& this.field_11852 == null
			&& this.field_11857 == null
			&& this.field_11851 == null
			&& this.field_11861 == null
			&& this.field_11855 == null
			&& this.field_11853 == null
			&& this.field_11858 == null
			&& this.field_11859 == null;
	}

	@Nullable
	public class_2558 method_10970() {
		return this.field_11853 == null ? this.method_10989().method_10970() : this.field_11853;
	}

	@Nullable
	public class_2568 method_10969() {
		return this.field_11858 == null ? this.method_10989().method_10969() : this.field_11858;
	}

	@Nullable
	public String method_10955() {
		return this.field_11859 == null ? this.method_10989().method_10955() : this.field_11859;
	}

	public class_2583 method_10977(class_124 arg) {
		this.field_11855 = arg;
		return this;
	}

	public class_2583 method_10982(Boolean boolean_) {
		this.field_11856 = boolean_;
		return this;
	}

	public class_2583 method_10978(Boolean boolean_) {
		this.field_11852 = boolean_;
		return this;
	}

	public class_2583 method_10959(Boolean boolean_) {
		this.field_11857 = boolean_;
		return this;
	}

	public class_2583 method_10968(Boolean boolean_) {
		this.field_11851 = boolean_;
		return this;
	}

	public class_2583 method_10948(Boolean boolean_) {
		this.field_11861 = boolean_;
		return this;
	}

	public class_2583 method_10958(class_2558 arg) {
		this.field_11853 = arg;
		return this;
	}

	public class_2583 method_10949(class_2568 arg) {
		this.field_11858 = arg;
		return this;
	}

	public class_2583 method_10975(String string) {
		this.field_11859 = string;
		return this;
	}

	public class_2583 method_10985(class_2583 arg) {
		this.field_11860 = arg;
		return this;
	}

	public String method_10953() {
		if (this.method_10967()) {
			return this.field_11860 != null ? this.field_11860.method_10953() : "";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			if (this.method_10973() != null) {
				stringBuilder.append(this.method_10973());
			}

			if (this.method_10984()) {
				stringBuilder.append(class_124.field_1067);
			}

			if (this.method_10966()) {
				stringBuilder.append(class_124.field_1056);
			}

			if (this.method_10965()) {
				stringBuilder.append(class_124.field_1073);
			}

			if (this.method_10987()) {
				stringBuilder.append(class_124.field_1051);
			}

			if (this.method_10986()) {
				stringBuilder.append(class_124.field_1055);
			}

			return stringBuilder.toString();
		}
	}

	private class_2583 method_10989() {
		return this.field_11860 == null ? field_11854 : this.field_11860;
	}

	public String toString() {
		return "Style{hasParent="
			+ (this.field_11860 != null)
			+ ", color="
			+ this.field_11855
			+ ", bold="
			+ this.field_11856
			+ ", italic="
			+ this.field_11852
			+ ", underlined="
			+ this.field_11851
			+ ", obfuscated="
			+ this.field_11861
			+ ", clickEvent="
			+ this.method_10970()
			+ ", hoverEvent="
			+ this.method_10969()
			+ ", insertion="
			+ this.method_10955()
			+ '}';
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2583)) {
			return false;
		} else {
			class_2583 lv = (class_2583)object;
			return this.method_10984() == lv.method_10984()
				&& this.method_10973() == lv.method_10973()
				&& this.method_10966() == lv.method_10966()
				&& this.method_10987() == lv.method_10987()
				&& this.method_10986() == lv.method_10986()
				&& this.method_10965() == lv.method_10965()
				&& (this.method_10970() != null ? this.method_10970().equals(lv.method_10970()) : lv.method_10970() == null)
				&& (this.method_10969() != null ? this.method_10969().equals(lv.method_10969()) : lv.method_10969() == null)
				&& (this.method_10955() != null ? this.method_10955().equals(lv.method_10955()) : lv.method_10955() == null);
		}
	}

	public int hashCode() {
		return Objects.hash(
			new Object[]{
				this.field_11855,
				this.field_11856,
				this.field_11852,
				this.field_11851,
				this.field_11857,
				this.field_11861,
				this.field_11853,
				this.field_11858,
				this.field_11859
			}
		);
	}

	public class_2583 method_10976() {
		class_2583 lv = new class_2583();
		lv.field_11856 = this.field_11856;
		lv.field_11852 = this.field_11852;
		lv.field_11857 = this.field_11857;
		lv.field_11851 = this.field_11851;
		lv.field_11861 = this.field_11861;
		lv.field_11855 = this.field_11855;
		lv.field_11853 = this.field_11853;
		lv.field_11858 = this.field_11858;
		lv.field_11860 = this.field_11860;
		lv.field_11859 = this.field_11859;
		return lv;
	}

	public class_2583 method_10960() {
		class_2583 lv = new class_2583();
		lv.method_10982(this.method_10984());
		lv.method_10978(this.method_10966());
		lv.method_10959(this.method_10986());
		lv.method_10968(this.method_10965());
		lv.method_10948(this.method_10987());
		lv.method_10977(this.method_10973());
		lv.method_10958(this.method_10970());
		lv.method_10949(this.method_10969());
		lv.method_10975(this.method_10955());
		return lv;
	}

	public static class class_2584 implements JsonDeserializer<class_2583>, JsonSerializer<class_2583> {
		@Nullable
		public class_2583 method_10991(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				class_2583 lv = new class_2583();
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				if (jsonObject == null) {
					return null;
				} else {
					if (jsonObject.has("bold")) {
						lv.field_11856 = jsonObject.get("bold").getAsBoolean();
					}

					if (jsonObject.has("italic")) {
						lv.field_11852 = jsonObject.get("italic").getAsBoolean();
					}

					if (jsonObject.has("underlined")) {
						lv.field_11851 = jsonObject.get("underlined").getAsBoolean();
					}

					if (jsonObject.has("strikethrough")) {
						lv.field_11857 = jsonObject.get("strikethrough").getAsBoolean();
					}

					if (jsonObject.has("obfuscated")) {
						lv.field_11861 = jsonObject.get("obfuscated").getAsBoolean();
					}

					if (jsonObject.has("color")) {
						lv.field_11855 = jsonDeserializationContext.deserialize(jsonObject.get("color"), class_124.class);
					}

					if (jsonObject.has("insertion")) {
						lv.field_11859 = jsonObject.get("insertion").getAsString();
					}

					if (jsonObject.has("clickEvent")) {
						JsonObject jsonObject2 = jsonObject.getAsJsonObject("clickEvent");
						if (jsonObject2 != null) {
							JsonPrimitive jsonPrimitive = jsonObject2.getAsJsonPrimitive("action");
							class_2558.class_2559 lv2 = jsonPrimitive == null ? null : class_2558.class_2559.method_10848(jsonPrimitive.getAsString());
							JsonPrimitive jsonPrimitive2 = jsonObject2.getAsJsonPrimitive("value");
							String string = jsonPrimitive2 == null ? null : jsonPrimitive2.getAsString();
							if (lv2 != null && string != null && lv2.method_10847()) {
								lv.field_11853 = new class_2558(lv2, string);
							}
						}
					}

					if (jsonObject.has("hoverEvent")) {
						JsonObject jsonObject2 = jsonObject.getAsJsonObject("hoverEvent");
						if (jsonObject2 != null) {
							JsonPrimitive jsonPrimitive = jsonObject2.getAsJsonPrimitive("action");
							class_2568.class_2569 lv3 = jsonPrimitive == null ? null : class_2568.class_2569.method_10896(jsonPrimitive.getAsString());
							class_2561 lv4 = jsonDeserializationContext.deserialize(jsonObject2.get("value"), class_2561.class);
							if (lv3 != null && lv4 != null && lv3.method_10895()) {
								lv.field_11858 = new class_2568(lv3, lv4);
							}
						}
					}

					return lv;
				}
			} else {
				return null;
			}
		}

		@Nullable
		public JsonElement method_10990(class_2583 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			if (arg.method_10967()) {
				return null;
			} else {
				JsonObject jsonObject = new JsonObject();
				if (arg.field_11856 != null) {
					jsonObject.addProperty("bold", arg.field_11856);
				}

				if (arg.field_11852 != null) {
					jsonObject.addProperty("italic", arg.field_11852);
				}

				if (arg.field_11851 != null) {
					jsonObject.addProperty("underlined", arg.field_11851);
				}

				if (arg.field_11857 != null) {
					jsonObject.addProperty("strikethrough", arg.field_11857);
				}

				if (arg.field_11861 != null) {
					jsonObject.addProperty("obfuscated", arg.field_11861);
				}

				if (arg.field_11855 != null) {
					jsonObject.add("color", jsonSerializationContext.serialize(arg.field_11855));
				}

				if (arg.field_11859 != null) {
					jsonObject.add("insertion", jsonSerializationContext.serialize(arg.field_11859));
				}

				if (arg.field_11853 != null) {
					JsonObject jsonObject2 = new JsonObject();
					jsonObject2.addProperty("action", arg.field_11853.method_10845().method_10846());
					jsonObject2.addProperty("value", arg.field_11853.method_10844());
					jsonObject.add("clickEvent", jsonObject2);
				}

				if (arg.field_11858 != null) {
					JsonObject jsonObject2 = new JsonObject();
					jsonObject2.addProperty("action", arg.field_11858.method_10892().method_10893());
					jsonObject2.add("value", jsonSerializationContext.serialize(arg.field_11858.method_10891()));
					jsonObject.add("hoverEvent", jsonObject2);
				}

				return jsonObject;
			}
		}
	}
}
