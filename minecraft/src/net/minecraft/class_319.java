package net.minecraft;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@Environment(EnvType.CLIENT)
public final class class_319 {
	private final int field_1980;
	private final int field_1979;
	private final int field_1978;
	private final int field_1977;
	private final int field_1976;
	private final int field_1975;
	private static final Pattern field_1981 = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

	public class_319(int i, int j, int k, int l, int m, int n) {
		this.field_1980 = i;
		this.field_1979 = j;
		this.field_1978 = k;
		this.field_1977 = l;
		this.field_1976 = m;
		this.field_1975 = n;
	}

	public class_319(Buffer buffer) {
		this.field_1980 = buffer.width();
		this.field_1979 = buffer.height();
		this.field_1978 = buffer.redBits();
		this.field_1977 = buffer.greenBits();
		this.field_1976 = buffer.blueBits();
		this.field_1975 = buffer.refreshRate();
	}

	public class_319(GLFWVidMode gLFWVidMode) {
		this.field_1980 = gLFWVidMode.width();
		this.field_1979 = gLFWVidMode.height();
		this.field_1978 = gLFWVidMode.redBits();
		this.field_1977 = gLFWVidMode.greenBits();
		this.field_1976 = gLFWVidMode.blueBits();
		this.field_1975 = gLFWVidMode.refreshRate();
	}

	public int method_1668() {
		return this.field_1980;
	}

	public int method_1669() {
		return this.field_1979;
	}

	public int method_1666() {
		return this.field_1978;
	}

	public int method_1667() {
		return this.field_1977;
	}

	public int method_1672() {
		return this.field_1976;
	}

	public int method_1671() {
		return this.field_1975;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_319 lv = (class_319)object;
			return this.field_1980 == lv.field_1980
				&& this.field_1979 == lv.field_1979
				&& this.field_1978 == lv.field_1978
				&& this.field_1977 == lv.field_1977
				&& this.field_1976 == lv.field_1976
				&& this.field_1975 == lv.field_1975;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_1980, this.field_1979, this.field_1978, this.field_1977, this.field_1976, this.field_1975});
	}

	public String toString() {
		return String.format("%sx%s@%s (%sbit)", this.field_1980, this.field_1979, this.field_1975, this.field_1978 + this.field_1977 + this.field_1976);
	}

	public static Optional<class_319> method_1665(String string) {
		try {
			Matcher matcher = field_1981.matcher(string);
			if (matcher.matches()) {
				int i = Integer.parseInt(matcher.group(1));
				int j = Integer.parseInt(matcher.group(2));
				String string2 = matcher.group(3);
				int k;
				if (string2 == null) {
					k = 60;
				} else {
					k = Integer.parseInt(string2);
				}

				String string3 = matcher.group(4);
				int l;
				if (string3 == null) {
					l = 24;
				} else {
					l = Integer.parseInt(string3);
				}

				int m = l / 3;
				return Optional.of(new class_319(i, j, m, m, m, k));
			}
		} catch (Exception var9) {
		}

		return Optional.empty();
	}

	public String method_1670() {
		return String.format("%sx%s@%s:%s", this.field_1980, this.field_1979, this.field_1975, this.field_1978 + this.field_1977 + this.field_1976);
	}
}
