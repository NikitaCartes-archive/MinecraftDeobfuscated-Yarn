package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_2578 extends class_2554 implements class_2566 {
	private final String field_11787;
	@Nullable
	private final class_2300 field_11786;
	private final String field_11785;
	private String field_11788 = "";

	public class_2578(String string, String string2) {
		this.field_11787 = string;
		this.field_11785 = string2;
		class_2300 lv = null;

		try {
			class_2303 lv2 = new class_2303(new StringReader(string));
			lv = lv2.method_9882();
		} catch (CommandSyntaxException var5) {
		}

		this.field_11786 = lv;
	}

	public String method_10930() {
		return this.field_11787;
	}

	public String method_10928() {
		return this.field_11785;
	}

	public void method_10927(String string) {
		this.field_11788 = string;
	}

	@Override
	public String method_10851() {
		return this.field_11788;
	}

	private void method_10926(class_2168 arg) {
		MinecraftServer minecraftServer = arg.method_9211();
		if (minecraftServer != null && minecraftServer.method_3814() && class_3544.method_15438(this.field_11788)) {
			class_269 lv = minecraftServer.method_3845();
			class_266 lv2 = lv.method_1170(this.field_11785);
			if (lv.method_1183(this.field_11787, lv2)) {
				class_267 lv3 = lv.method_1180(this.field_11787, lv2);
				this.method_10927(String.format("%d", lv3.method_1126()));
			} else {
				this.field_11788 = "";
			}
		}
	}

	public class_2578 method_10929() {
		class_2578 lv = new class_2578(this.field_11787, this.field_11785);
		lv.method_10927(this.field_11788);
		return lv;
	}

	@Override
	public class_2561 method_10890(@Nullable class_2168 arg, @Nullable class_1297 arg2) throws CommandSyntaxException {
		if (arg == null) {
			return this.method_10929();
		} else {
			String string;
			if (this.field_11786 != null) {
				List<? extends class_1297> list = this.field_11786.method_9816(arg);
				if (list.isEmpty()) {
					string = this.field_11787;
				} else {
					if (list.size() != 1) {
						throw class_2186.field_9860.create();
					}

					string = ((class_1297)list.get(0)).method_5820();
				}
			} else {
				string = this.field_11787;
			}

			String string2 = arg2 != null && string.equals("*") ? arg2.method_5820() : string;
			class_2578 lv = new class_2578(string2, this.field_11785);
			lv.method_10927(this.field_11788);
			lv.method_10926(arg);
			return lv;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2578)) {
			return false;
		} else {
			class_2578 lv = (class_2578)object;
			return this.field_11787.equals(lv.field_11787) && this.field_11785.equals(lv.field_11785) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "ScoreComponent{name='"
			+ this.field_11787
			+ '\''
			+ "objective='"
			+ this.field_11785
			+ '\''
			+ ", siblings="
			+ this.field_11729
			+ ", style="
			+ this.method_10866()
			+ '}';
	}
}
