package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1355 {
	private static final Logger field_6466 = LogManager.getLogger();
	private final Set<class_1355.class_1356> field_6461 = Sets.<class_1355.class_1356>newLinkedHashSet();
	private final Set<class_1355.class_1356> field_6460 = Sets.<class_1355.class_1356>newLinkedHashSet();
	private final class_3695 field_6463;
	private int field_6465;
	private int field_6464 = 3;
	private int field_6462;

	public class_1355(class_3695 arg) {
		this.field_6463 = arg;
	}

	public void method_6277(int i, class_1352 arg) {
		this.field_6461.add(new class_1355.class_1356(i, arg));
	}

	public void method_6280(class_1352 arg) {
		Iterator<class_1355.class_1356> iterator = this.field_6461.iterator();

		while (iterator.hasNext()) {
			class_1355.class_1356 lv = (class_1355.class_1356)iterator.next();
			class_1352 lv2 = lv.field_6467;
			if (lv2 == arg) {
				if (lv.field_6469) {
					lv.field_6469 = false;
					lv.field_6467.method_6270();
					this.field_6460.remove(lv);
				}

				iterator.remove();
				return;
			}
		}
	}

	public void method_6275() {
		this.field_6463.method_15396("goalSetup");
		if (this.field_6465++ % this.field_6464 == 0) {
			for (class_1355.class_1356 lv : this.field_6461) {
				if (lv.field_6469) {
					if (!this.method_6281(lv) || !this.method_6278(lv)) {
						lv.field_6469 = false;
						lv.field_6467.method_6270();
						this.field_6460.remove(lv);
					}
				} else if (this.method_6281(lv) && lv.field_6467.method_6264()) {
					lv.field_6469 = true;
					lv.field_6467.method_6269();
					this.field_6460.add(lv);
				}
			}
		} else {
			Iterator<class_1355.class_1356> iterator = this.field_6460.iterator();

			while (iterator.hasNext()) {
				class_1355.class_1356 lvx = (class_1355.class_1356)iterator.next();
				if (!this.method_6278(lvx)) {
					lvx.field_6469 = false;
					lvx.field_6467.method_6270();
					iterator.remove();
				}
			}
		}

		this.field_6463.method_15407();
		if (!this.field_6460.isEmpty()) {
			this.field_6463.method_15396("goalTick");

			for (class_1355.class_1356 lvx : this.field_6460) {
				lvx.field_6467.method_6268();
			}

			this.field_6463.method_15407();
		}
	}

	private boolean method_6278(class_1355.class_1356 arg) {
		return arg.field_6467.method_6266();
	}

	private boolean method_6281(class_1355.class_1356 arg) {
		if (this.field_6460.isEmpty()) {
			return true;
		} else if (this.method_6279(arg.field_6467.method_6271())) {
			return false;
		} else {
			for (class_1355.class_1356 lv : this.field_6460) {
				if (lv != arg) {
					if (arg.field_6468 >= lv.field_6468) {
						if (!this.method_6272(arg, lv)) {
							return false;
						}
					} else if (!lv.field_6467.method_6267()) {
						return false;
					}
				}
			}

			return true;
		}
	}

	private boolean method_6272(class_1355.class_1356 arg, class_1355.class_1356 arg2) {
		return (arg.field_6467.method_6271() & arg2.field_6467.method_6271()) == 0;
	}

	public boolean method_6279(int i) {
		return (this.field_6462 & i) > 0;
	}

	public void method_6274(int i) {
		this.field_6462 |= i;
	}

	public void method_6273(int i) {
		this.field_6462 &= ~i;
	}

	public void method_6276(int i, boolean bl) {
		if (bl) {
			this.method_6273(i);
		} else {
			this.method_6274(i);
		}
	}

	class class_1356 {
		public final class_1352 field_6467;
		public final int field_6468;
		public boolean field_6469;

		public class_1356(int i, class_1352 arg2) {
			this.field_6468 = i;
			this.field_6467 = arg2;
		}

		public boolean equals(@Nullable Object object) {
			if (this == object) {
				return true;
			} else {
				return object != null && this.getClass() == object.getClass() ? this.field_6467.equals(((class_1355.class_1356)object).field_6467) : false;
			}
		}

		public int hashCode() {
			return this.field_6467.hashCode();
		}
	}
}
