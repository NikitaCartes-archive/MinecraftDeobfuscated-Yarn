package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3174 extends class_3324 {
	private static final Logger field_13804 = LogManager.getLogger();

	public class_3174(class_3176 arg) {
		super(arg, arg.method_16705().field_16814);
		class_3806 lv = arg.method_16705();
		this.method_14608(lv.field_16844, lv.field_16844 - 2);
		super.method_14557(lv.field_16804.get());
		if (!arg.method_3724()) {
			this.method_14563().method_14637(true);
			this.method_14585().method_14637(true);
		}

		this.method_13933();
		this.method_13930();
		this.method_13931();
		this.method_13932();
		this.method_13934();
		this.method_13936();
		this.method_13935();
		if (!this.method_14590().method_14643().exists()) {
			this.method_13937();
		}
	}

	@Override
	public void method_14557(boolean bl) {
		super.method_14557(bl);
		this.method_13938().method_16712(bl);
	}

	@Override
	public void method_14582(GameProfile gameProfile) {
		super.method_14582(gameProfile);
		this.method_13935();
	}

	@Override
	public void method_14604(GameProfile gameProfile) {
		super.method_14604(gameProfile);
		this.method_13935();
	}

	@Override
	public void method_14599() {
		this.method_13936();
	}

	private void method_13932() {
		try {
			this.method_14585().method_14629();
		} catch (IOException var2) {
			field_13804.warn("Failed to save ip banlist: ", (Throwable)var2);
		}
	}

	private void method_13930() {
		try {
			this.method_14563().method_14629();
		} catch (IOException var2) {
			field_13804.warn("Failed to save user banlist: ", (Throwable)var2);
		}
	}

	private void method_13931() {
		try {
			this.method_14585().method_14630();
		} catch (IOException var2) {
			field_13804.warn("Failed to load ip banlist: ", (Throwable)var2);
		}
	}

	private void method_13933() {
		try {
			this.method_14563().method_14630();
		} catch (IOException var2) {
			field_13804.warn("Failed to load user banlist: ", (Throwable)var2);
		}
	}

	private void method_13934() {
		try {
			this.method_14603().method_14630();
		} catch (Exception var2) {
			field_13804.warn("Failed to load operators list: ", (Throwable)var2);
		}
	}

	private void method_13935() {
		try {
			this.method_14603().method_14629();
		} catch (Exception var2) {
			field_13804.warn("Failed to save operators list: ", (Throwable)var2);
		}
	}

	private void method_13936() {
		try {
			this.method_14590().method_14630();
		} catch (Exception var2) {
			field_13804.warn("Failed to load white-list: ", (Throwable)var2);
		}
	}

	private void method_13937() {
		try {
			this.method_14590().method_14629();
		} catch (Exception var2) {
			field_13804.warn("Failed to save white-list: ", (Throwable)var2);
		}
	}

	@Override
	public boolean method_14587(GameProfile gameProfile) {
		return !this.method_14614() || this.method_14569(gameProfile) || this.method_14590().method_14653(gameProfile);
	}

	public class_3176 method_13938() {
		return (class_3176)super.method_14561();
	}

	@Override
	public boolean method_14609(GameProfile gameProfile) {
		return this.method_14603().method_14620(gameProfile);
	}
}
