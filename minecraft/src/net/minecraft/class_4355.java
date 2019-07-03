package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4355 extends Exception {
	public final int field_19604;
	public final String field_19605;
	public final int field_19606;
	public final String field_19607;

	public class_4355(int i, String string, class_4345 arg) {
		super(string);
		this.field_19604 = i;
		this.field_19605 = string;
		this.field_19606 = arg.method_21037();
		this.field_19607 = arg.method_21036();
	}

	public class_4355(int i, String string, int j, String string2) {
		super(string);
		this.field_19604 = i;
		this.field_19605 = string;
		this.field_19606 = j;
		this.field_19607 = string2;
	}

	public String toString() {
		if (this.field_19606 == -1) {
			return "Realms (" + this.field_19604 + ") " + this.field_19605;
		} else {
			String string = "mco.errorMessage." + this.field_19606;
			String string2 = RealmsScreen.getLocalizedString(string);
			return (string2.equals(string) ? this.field_19607 : string2) + " - " + this.field_19606;
		}
	}
}
