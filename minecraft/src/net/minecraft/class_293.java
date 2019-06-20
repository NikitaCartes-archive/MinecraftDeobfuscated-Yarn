package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_293 {
	private static final Logger field_1601 = LogManager.getLogger();
	private final List<class_296> field_1602 = Lists.<class_296>newArrayList();
	private final List<Integer> field_1597 = Lists.<Integer>newArrayList();
	private int field_1600;
	private int field_1599 = -1;
	private final List<Integer> field_1598 = Lists.<Integer>newArrayList();
	private int field_1596 = -1;

	public class_293(class_293 arg) {
		this();

		for (int i = 0; i < arg.method_1363(); i++) {
			this.method_1361(arg.method_1364(i));
		}

		this.field_1600 = arg.method_1362();
	}

	public class_293() {
	}

	public void method_1366() {
		this.field_1602.clear();
		this.field_1597.clear();
		this.field_1599 = -1;
		this.field_1598.clear();
		this.field_1596 = -1;
		this.field_1600 = 0;
	}

	public class_293 method_1361(class_296 arg) {
		if (arg.method_1388() && this.method_1371()) {
			field_1601.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
			return this;
		} else {
			this.field_1602.add(arg);
			this.field_1597.add(this.field_1600);
			switch (arg.method_1382()) {
				case field_1635:
					this.field_1596 = this.field_1600;
					break;
				case field_1632:
					this.field_1599 = this.field_1600;
					break;
				case field_1636:
					this.field_1598.add(arg.method_1385(), this.field_1600);
			}

			this.field_1600 = this.field_1600 + arg.method_1387();
			return this;
		}
	}

	public boolean method_1368() {
		return this.field_1596 >= 0;
	}

	public int method_1358() {
		return this.field_1596;
	}

	public boolean method_1369() {
		return this.field_1599 >= 0;
	}

	public int method_1360() {
		return this.field_1599;
	}

	public boolean method_1367(int i) {
		return this.field_1598.size() - 1 >= i;
	}

	public int method_1370(int i) {
		return (Integer)this.field_1598.get(i);
	}

	public String toString() {
		String string = "format: " + this.field_1602.size() + " elements: ";

		for (int i = 0; i < this.field_1602.size(); i++) {
			string = string + ((class_296)this.field_1602.get(i)).toString();
			if (i != this.field_1602.size() - 1) {
				string = string + " ";
			}
		}

		return string;
	}

	private boolean method_1371() {
		int i = 0;

		for (int j = this.field_1602.size(); i < j; i++) {
			class_296 lv = (class_296)this.field_1602.get(i);
			if (lv.method_1388()) {
				return true;
			}
		}

		return false;
	}

	public int method_1359() {
		return this.method_1362() / 4;
	}

	public int method_1362() {
		return this.field_1600;
	}

	public List<class_296> method_1357() {
		return this.field_1602;
	}

	public int method_1363() {
		return this.field_1602.size();
	}

	public class_296 method_1364(int i) {
		return (class_296)this.field_1602.get(i);
	}

	public int method_1365(int i) {
		return (Integer)this.field_1597.get(i);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_293 lv = (class_293)object;
			if (this.field_1600 != lv.field_1600) {
				return false;
			} else {
				return !this.field_1602.equals(lv.field_1602) ? false : this.field_1597.equals(lv.field_1597);
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.field_1602.hashCode();
		i = 31 * i + this.field_1597.hashCode();
		return 31 * i + this.field_1600;
	}
}
