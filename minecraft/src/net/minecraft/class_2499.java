package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class class_2499 extends class_2483<class_2520> {
	private List<class_2520> field_11550 = Lists.<class_2520>newArrayList();
	private byte field_11551 = 0;

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		if (this.field_11550.isEmpty()) {
			this.field_11551 = 0;
		} else {
			this.field_11551 = ((class_2520)this.field_11550.get(0)).method_10711();
		}

		dataOutput.writeByte(this.field_11551);
		dataOutput.writeInt(this.field_11550.size());

		for (class_2520 lv : this.field_11550) {
			lv.method_10713(dataOutput);
		}
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(296L);
		if (i > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.field_11551 = dataInput.readByte();
			int j = dataInput.readInt();
			if (this.field_11551 == 0 && j > 0) {
				throw new RuntimeException("Missing type on ListTag");
			} else {
				arg.method_10623(32L * (long)j);
				this.field_11550 = Lists.<class_2520>newArrayListWithCapacity(j);

				for (int k = 0; k < j; k++) {
					class_2520 lv = class_2520.method_10708(this.field_11551);
					lv.method_10709(dataInput, i + 1, arg);
					this.field_11550.add(lv);
				}
			}
		}
	}

	@Override
	public byte method_10711() {
		return 9;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[");

		for (int i = 0; i < this.field_11550.size(); i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.field_11550.get(i));
		}

		return stringBuilder.append(']').toString();
	}

	private void method_17809() {
		if (this.field_11550.isEmpty()) {
			this.field_11551 = 0;
		}
	}

	@Override
	public class_2520 method_10536(int i) {
		class_2520 lv = (class_2520)this.field_11550.remove(i);
		this.method_17809();
		return lv;
	}

	public boolean isEmpty() {
		return this.field_11550.isEmpty();
	}

	public class_2487 method_10602(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 10) {
				return (class_2487)lv;
			}
		}

		return new class_2487();
	}

	public class_2499 method_10603(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 9) {
				return (class_2499)lv;
			}
		}

		return new class_2499();
	}

	public short method_10609(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 2) {
				return ((class_2516)lv).method_10696();
			}
		}

		return 0;
	}

	public int method_10600(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 3) {
				return ((class_2497)lv).method_10701();
			}
		}

		return 0;
	}

	public int[] method_10610(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 11) {
				return ((class_2495)lv).method_10588();
			}
		}

		return new int[0];
	}

	public double method_10611(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 6) {
				return ((class_2489)lv).method_10697();
			}
		}

		return 0.0;
	}

	public float method_10604(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			if (lv.method_10711() == 5) {
				return ((class_2494)lv).method_10700();
			}
		}

		return 0.0F;
	}

	public String method_10608(int i) {
		if (i >= 0 && i < this.field_11550.size()) {
			class_2520 lv = (class_2520)this.field_11550.get(i);
			return lv.method_10711() == 8 ? lv.method_10714() : lv.toString();
		} else {
			return "";
		}
	}

	public int size() {
		return this.field_11550.size();
	}

	public class_2520 method_10534(int i) {
		return (class_2520)this.field_11550.get(i);
	}

	@Override
	public class_2520 method_10606(int i, class_2520 arg) {
		class_2520 lv = this.method_10534(i);
		if (!this.method_10535(i, arg)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", arg.method_10711(), this.field_11551));
		} else {
			return lv;
		}
	}

	@Override
	public void method_10531(int i, class_2520 arg) {
		if (!this.method_10533(i, arg)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", arg.method_10711(), this.field_11551));
		}
	}

	@Override
	public boolean method_10535(int i, class_2520 arg) {
		if (this.method_10605(arg)) {
			this.field_11550.set(i, arg);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_10533(int i, class_2520 arg) {
		if (this.method_10605(arg)) {
			this.field_11550.add(i, arg);
			return true;
		} else {
			return false;
		}
	}

	private boolean method_10605(class_2520 arg) {
		if (arg.method_10711() == 0) {
			return false;
		} else if (this.field_11551 == 0) {
			this.field_11551 = arg.method_10711();
			return true;
		} else {
			return this.field_11551 == arg.method_10711();
		}
	}

	public class_2499 method_10612() {
		class_2499 lv = new class_2499();
		lv.field_11551 = this.field_11551;

		for (class_2520 lv2 : this.field_11550) {
			class_2520 lv3 = lv2.method_10707();
			lv.field_11550.add(lv3);
		}

		return lv;
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2499 && Objects.equals(this.field_11550, ((class_2499)object).field_11550);
	}

	public int hashCode() {
		return this.field_11550.hashCode();
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		if (this.isEmpty()) {
			return new class_2585("[]");
		} else {
			class_2561 lv = new class_2585("[");
			if (!string.isEmpty()) {
				lv.method_10864("\n");
			}

			for (int j = 0; j < this.field_11550.size(); j++) {
				class_2561 lv2 = new class_2585(Strings.repeat(string, i + 1));
				lv2.method_10852(((class_2520)this.field_11550.get(j)).method_10710(string, i + 1));
				if (j != this.field_11550.size() - 1) {
					lv2.method_10864(String.valueOf(',')).method_10864(string.isEmpty() ? " " : "\n");
				}

				lv.method_10852(lv2);
			}

			if (!string.isEmpty()) {
				lv.method_10864("\n").method_10864(Strings.repeat(string, i));
			}

			lv.method_10864("]");
			return lv;
		}
	}

	public int method_10601() {
		return this.field_11551;
	}

	public void clear() {
		this.field_11550.clear();
		this.field_11551 = 0;
	}
}
