package net.minecraft;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_270 {
	public boolean method_1206(@Nullable class_270 arg) {
		return arg == null ? false : this == arg;
	}

	public abstract String method_1197();

	public abstract class_2561 method_1198(class_2561 arg);

	@Environment(EnvType.CLIENT)
	public abstract boolean method_1199();

	public abstract boolean method_1205();

	@Environment(EnvType.CLIENT)
	public abstract class_270.class_272 method_1201();

	public abstract class_124 method_1202();

	public abstract Collection<String> method_1204();

	public abstract class_270.class_272 method_1200();

	public abstract class_270.class_271 method_1203();

	public static enum class_271 {
		field_1437("always", 0),
		field_1435("never", 1),
		field_1434("pushOtherTeams", 2),
		field_1440("pushOwnTeam", 3);

		private static final Map<String, class_270.class_271> field_1438 = (Map<String, class_270.class_271>)Arrays.stream(values())
			.collect(Collectors.toMap(arg -> arg.field_1436, arg -> arg));
		public final String field_1436;
		public final int field_1433;

		@Nullable
		public static class_270.class_271 method_1210(String string) {
			return (class_270.class_271)field_1438.get(string);
		}

		private class_271(String string2, int j) {
			this.field_1436 = string2;
			this.field_1433 = j;
		}

		public class_2561 method_1209() {
			return new class_2588("team.collision." + this.field_1436);
		}
	}

	public static enum class_272 {
		field_1442("always", 0),
		field_1443("never", 1),
		field_1444("hideForOtherTeams", 2),
		field_1446("hideForOwnTeam", 3);

		private static final Map<String, class_270.class_272> field_1447 = (Map<String, class_270.class_272>)Arrays.stream(values())
			.collect(Collectors.toMap(arg -> arg.field_1445, arg -> arg));
		public final String field_1445;
		public final int field_1441;

		@Nullable
		public static class_270.class_272 method_1213(String string) {
			return (class_270.class_272)field_1447.get(string);
		}

		private class_272(String string2, int j) {
			this.field_1445 = string2;
			this.field_1441 = j;
		}

		public class_2561 method_1214() {
			return new class_2588("team.visibility." + this.field_1445);
		}
	}
}
