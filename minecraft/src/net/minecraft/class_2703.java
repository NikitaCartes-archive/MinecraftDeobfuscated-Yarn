package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2703 implements class_2596<class_2602> {
	private class_2703.class_2704 field_12368;
	private final List<class_2703.class_2705> field_12369 = Lists.<class_2703.class_2705>newArrayList();

	public class_2703() {
	}

	public class_2703(class_2703.class_2704 arg, class_3222... args) {
		this.field_12368 = arg;

		for (class_3222 lv : args) {
			this.field_12369.add(new class_2703.class_2705(lv.method_7334(), lv.field_13967, lv.field_13974.method_14257(), lv.method_14206()));
		}
	}

	public class_2703(class_2703.class_2704 arg, Iterable<class_3222> iterable) {
		this.field_12368 = arg;

		for (class_3222 lv : iterable) {
			this.field_12369.add(new class_2703.class_2705(lv.method_7334(), lv.field_13967, lv.field_13974.method_14257(), lv.method_14206()));
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12368 = arg.method_10818(class_2703.class_2704.class);
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			GameProfile gameProfile = null;
			int k = 0;
			class_1934 lv = null;
			class_2561 lv2 = null;
			switch (this.field_12368) {
				case field_12372:
					gameProfile = new GameProfile(arg.method_10790(), arg.method_10800(16));
					int l = arg.method_10816();
					int m = 0;

					for (; m < l; m++) {
						String string = arg.method_10800(32767);
						String string2 = arg.method_10800(32767);
						if (arg.readBoolean()) {
							gameProfile.getProperties().put(string, new Property(string, string2, arg.method_10800(32767)));
						} else {
							gameProfile.getProperties().put(string, new Property(string, string2));
						}
					}

					lv = class_1934.method_8384(arg.method_10816());
					k = arg.method_10816();
					if (arg.readBoolean()) {
						lv2 = arg.method_10808();
					}
					break;
				case field_12375:
					gameProfile = new GameProfile(arg.method_10790(), null);
					lv = class_1934.method_8384(arg.method_10816());
					break;
				case field_12371:
					gameProfile = new GameProfile(arg.method_10790(), null);
					k = arg.method_10816();
					break;
				case field_12374:
					gameProfile = new GameProfile(arg.method_10790(), null);
					if (arg.readBoolean()) {
						lv2 = arg.method_10808();
					}
					break;
				case field_12376:
					gameProfile = new GameProfile(arg.method_10790(), null);
			}

			this.field_12369.add(new class_2703.class_2705(gameProfile, k, lv, lv2));
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12368);
		arg.method_10804(this.field_12369.size());

		for (class_2703.class_2705 lv : this.field_12369) {
			switch (this.field_12368) {
				case field_12372:
					arg.method_10797(lv.method_11726().getId());
					arg.method_10814(lv.method_11726().getName());
					arg.method_10804(lv.method_11726().getProperties().size());

					for (Property property : lv.method_11726().getProperties().values()) {
						arg.method_10814(property.getName());
						arg.method_10814(property.getValue());
						if (property.hasSignature()) {
							arg.writeBoolean(true);
							arg.method_10814(property.getSignature());
						} else {
							arg.writeBoolean(false);
						}
					}

					arg.method_10804(lv.method_11725().method_8379());
					arg.method_10804(lv.method_11727());
					if (lv.method_11724() == null) {
						arg.writeBoolean(false);
					} else {
						arg.writeBoolean(true);
						arg.method_10805(lv.method_11724());
					}
					break;
				case field_12375:
					arg.method_10797(lv.method_11726().getId());
					arg.method_10804(lv.method_11725().method_8379());
					break;
				case field_12371:
					arg.method_10797(lv.method_11726().getId());
					arg.method_10804(lv.method_11727());
					break;
				case field_12374:
					arg.method_10797(lv.method_11726().getId());
					if (lv.method_11724() == null) {
						arg.writeBoolean(false);
					} else {
						arg.writeBoolean(true);
						arg.method_10805(lv.method_11724());
					}
					break;
				case field_12376:
					arg.method_10797(lv.method_11726().getId());
			}
		}
	}

	public void method_11721(class_2602 arg) {
		arg.method_11113(this);
	}

	@Environment(EnvType.CLIENT)
	public List<class_2703.class_2705> method_11722() {
		return this.field_12369;
	}

	@Environment(EnvType.CLIENT)
	public class_2703.class_2704 method_11723() {
		return this.field_12368;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("action", this.field_12368).add("entries", this.field_12369).toString();
	}

	public static enum class_2704 {
		field_12372,
		field_12375,
		field_12371,
		field_12374,
		field_12376;
	}

	public class class_2705 {
		private final int field_12378;
		private final class_1934 field_12379;
		private final GameProfile field_12380;
		private final class_2561 field_12377;

		public class_2705(GameProfile gameProfile, int i, @Nullable class_1934 arg2, @Nullable class_2561 arg3) {
			this.field_12380 = gameProfile;
			this.field_12378 = i;
			this.field_12379 = arg2;
			this.field_12377 = arg3;
		}

		public GameProfile method_11726() {
			return this.field_12380;
		}

		public int method_11727() {
			return this.field_12378;
		}

		public class_1934 method_11725() {
			return this.field_12379;
		}

		@Nullable
		public class_2561 method_11724() {
			return this.field_12377;
		}

		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("latency", this.field_12378)
				.add("gameMode", this.field_12379)
				.add("profile", this.field_12380)
				.add("displayName", this.field_12377 == null ? null : class_2561.class_2562.method_10867(this.field_12377))
				.toString();
		}
	}
}
