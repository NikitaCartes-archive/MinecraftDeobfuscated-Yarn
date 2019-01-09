package net.minecraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_273 extends class_18 {
	private static final Logger field_1451 = LogManager.getLogger();
	private class_269 field_1449;
	private class_2487 field_1450;

	public class_273() {
		this("scoreboard");
	}

	public class_273(String string) {
		super(string);
	}

	public void method_1218(class_269 arg) {
		this.field_1449 = arg;
		if (this.field_1450 != null) {
			this.method_77(this.field_1450);
		}
	}

	@Override
	public void method_77(class_2487 arg) {
		if (this.field_1449 == null) {
			this.field_1450 = arg;
		} else {
			this.method_1220(arg.method_10554("Objectives", 10));
			this.field_1449.method_1188(arg.method_10554("PlayerScores", 10));
			if (arg.method_10573("DisplaySlots", 10)) {
				this.method_1221(arg.method_10562("DisplaySlots"));
			}

			if (arg.method_10573("Teams", 9)) {
				this.method_1219(arg.method_10554("Teams", 10));
			}
		}
	}

	protected void method_1219(class_2499 arg) {
		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			String string = lv.method_10558("Name");
			if (string.length() > 16) {
				string = string.substring(0, 16);
			}

			class_268 lv2 = this.field_1449.method_1171(string);
			class_2561 lv3 = class_2561.class_2562.method_10877(lv.method_10558("DisplayName"));
			if (lv3 != null) {
				lv2.method_1137(lv3);
			}

			if (lv.method_10573("TeamColor", 8)) {
				lv2.method_1141(class_124.method_533(lv.method_10558("TeamColor")));
			}

			if (lv.method_10573("AllowFriendlyFire", 99)) {
				lv2.method_1135(lv.method_10577("AllowFriendlyFire"));
			}

			if (lv.method_10573("SeeFriendlyInvisibles", 99)) {
				lv2.method_1143(lv.method_10577("SeeFriendlyInvisibles"));
			}

			if (lv.method_10573("MemberNamePrefix", 8)) {
				class_2561 lv4 = class_2561.class_2562.method_10877(lv.method_10558("MemberNamePrefix"));
				if (lv4 != null) {
					lv2.method_1138(lv4);
				}
			}

			if (lv.method_10573("MemberNameSuffix", 8)) {
				class_2561 lv4 = class_2561.class_2562.method_10877(lv.method_10558("MemberNameSuffix"));
				if (lv4 != null) {
					lv2.method_1139(lv4);
				}
			}

			if (lv.method_10573("NameTagVisibility", 8)) {
				class_270.class_272 lv5 = class_270.class_272.method_1213(lv.method_10558("NameTagVisibility"));
				if (lv5 != null) {
					lv2.method_1149(lv5);
				}
			}

			if (lv.method_10573("DeathMessageVisibility", 8)) {
				class_270.class_272 lv5 = class_270.class_272.method_1213(lv.method_10558("DeathMessageVisibility"));
				if (lv5 != null) {
					lv2.method_1133(lv5);
				}
			}

			if (lv.method_10573("CollisionRule", 8)) {
				class_270.class_271 lv6 = class_270.class_271.method_1210(lv.method_10558("CollisionRule"));
				if (lv6 != null) {
					lv2.method_1145(lv6);
				}
			}

			this.method_1215(lv2, lv.method_10554("Players", 8));
		}
	}

	protected void method_1215(class_268 arg, class_2499 arg2) {
		for (int i = 0; i < arg2.size(); i++) {
			this.field_1449.method_1172(arg2.method_10608(i), arg);
		}
	}

	protected void method_1221(class_2487 arg) {
		for (int i = 0; i < 19; i++) {
			if (arg.method_10573("slot_" + i, 8)) {
				String string = arg.method_10558("slot_" + i);
				class_266 lv = this.field_1449.method_1170(string);
				this.field_1449.method_1158(i, lv);
			}
		}
	}

	protected void method_1220(class_2499 arg) {
		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			class_274 lv2 = class_274.method_1224(lv.method_10558("CriteriaName"));
			if (lv2 != null) {
				String string = lv.method_10558("Name");
				if (string.length() > 16) {
					string = string.substring(0, 16);
				}

				class_2561 lv3 = class_2561.class_2562.method_10877(lv.method_10558("DisplayName"));
				class_274.class_275 lv4 = class_274.class_275.method_1229(lv.method_10558("RenderType"));
				this.field_1449.method_1168(string, lv2, lv3, lv4);
			}
		}
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		if (this.field_1449 == null) {
			field_1451.warn("Tried to save scoreboard without having a scoreboard...");
			return arg;
		} else {
			arg.method_10566("Objectives", this.method_1216());
			arg.method_10566("PlayerScores", this.field_1449.method_1169());
			arg.method_10566("Teams", this.method_1217());
			this.method_1222(arg);
			return arg;
		}
	}

	protected class_2499 method_1217() {
		class_2499 lv = new class_2499();

		for (class_268 lv2 : this.field_1449.method_1159()) {
			class_2487 lv3 = new class_2487();
			lv3.method_10582("Name", lv2.method_1197());
			lv3.method_10582("DisplayName", class_2561.class_2562.method_10867(lv2.method_1140()));
			if (lv2.method_1202().method_536() >= 0) {
				lv3.method_10582("TeamColor", lv2.method_1202().method_537());
			}

			lv3.method_10556("AllowFriendlyFire", lv2.method_1205());
			lv3.method_10556("SeeFriendlyInvisibles", lv2.method_1199());
			lv3.method_10582("MemberNamePrefix", class_2561.class_2562.method_10867(lv2.method_1144()));
			lv3.method_10582("MemberNameSuffix", class_2561.class_2562.method_10867(lv2.method_1136()));
			lv3.method_10582("NameTagVisibility", lv2.method_1201().field_1445);
			lv3.method_10582("DeathMessageVisibility", lv2.method_1200().field_1445);
			lv3.method_10582("CollisionRule", lv2.method_1203().field_1436);
			class_2499 lv4 = new class_2499();

			for (String string : lv2.method_1204()) {
				lv4.method_10606(new class_2519(string));
			}

			lv3.method_10566("Players", lv4);
			lv.method_10606(lv3);
		}

		return lv;
	}

	protected void method_1222(class_2487 arg) {
		class_2487 lv = new class_2487();
		boolean bl = false;

		for (int i = 0; i < 19; i++) {
			class_266 lv2 = this.field_1449.method_1189(i);
			if (lv2 != null) {
				lv.method_10582("slot_" + i, lv2.method_1113());
				bl = true;
			}
		}

		if (bl) {
			arg.method_10566("DisplaySlots", lv);
		}
	}

	protected class_2499 method_1216() {
		class_2499 lv = new class_2499();

		for (class_266 lv2 : this.field_1449.method_1151()) {
			if (lv2.method_1116() != null) {
				class_2487 lv3 = new class_2487();
				lv3.method_10582("Name", lv2.method_1113());
				lv3.method_10582("CriteriaName", lv2.method_1116().method_1225());
				lv3.method_10582("DisplayName", class_2561.class_2562.method_10867(lv2.method_1114()));
				lv3.method_10582("RenderType", lv2.method_1118().method_1228());
				lv.method_10606(lv3);
			}
		}

		return lv;
	}
}
