package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3470 extends class_3443 {
	private static final Logger field_16586 = LogManager.getLogger();
	protected class_3499 field_15433;
	protected class_3492 field_15434;
	protected class_2338 field_15432;

	public class_3470(class_3773 arg, int i) {
		super(arg, i);
	}

	public class_3470(class_3773 arg, class_2487 arg2) {
		super(arg, arg2);
		this.field_15432 = new class_2338(arg2.method_10550("TPX"), arg2.method_10550("TPY"), arg2.method_10550("TPZ"));
	}

	protected void method_15027(class_3499 arg, class_2338 arg2, class_3492 arg3) {
		this.field_15433 = arg;
		this.method_14926(class_2350.field_11043);
		this.field_15432 = arg2;
		this.field_15434 = arg3;
		this.field_15315 = arg.method_16187(arg3, arg2);
	}

	@Override
	protected void method_14943(class_2487 arg) {
		arg.method_10569("TPX", this.field_15432.method_10263());
		arg.method_10569("TPY", this.field_15432.method_10264());
		arg.method_10569("TPZ", this.field_15432.method_10260());
	}

	@Override
	public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
		this.field_15434.method_15126(arg2);
		this.field_15315 = this.field_15433.method_16187(this.field_15434, this.field_15432);
		if (this.field_15433.method_15172(arg, this.field_15432, this.field_15434, 2)) {
			for (class_3499.class_3501 lv : this.field_15433.method_16445(this.field_15432, this.field_15434, class_2246.field_10465)) {
				if (lv.field_15595 != null) {
					class_2776 lv2 = class_2776.valueOf(lv.field_15595.method_10558("mode"));
					if (lv2 == class_2776.field_12696) {
						this.method_15026(lv.field_15595.method_10558("metadata"), lv.field_15597, arg, random, arg2);
					}
				}
			}

			for (class_3499.class_3501 lv3 : this.field_15433.method_16445(this.field_15432, this.field_15434, class_2246.field_16540)) {
				if (lv3.field_15595 != null) {
					String string = lv3.field_15595.method_10558("final_state");
					class_2259 lv4 = new class_2259(new StringReader(string), false);
					class_2680 lv5 = class_2246.field_10124.method_9564();

					try {
						lv4.method_9678(true);
						class_2680 lv6 = lv4.method_9669();
						if (lv6 != null) {
							lv5 = lv6;
						} else {
							field_16586.error("Error while parsing blockstate {} in jigsaw block @ {}", string, lv3.field_15597);
						}
					} catch (CommandSyntaxException var13) {
						field_16586.error("Error while parsing blockstate {} in jigsaw block @ {}", string, lv3.field_15597);
					}

					arg.method_8652(lv3.field_15597, lv5, 3);
				}
			}
		}

		return true;
	}

	protected abstract void method_15026(String string, class_2338 arg, class_1936 arg2, Random random, class_3341 arg3);

	@Override
	public void method_14922(int i, int j, int k) {
		super.method_14922(i, j, k);
		this.field_15432 = this.field_15432.method_10069(i, j, k);
	}

	@Override
	public class_2470 method_16888() {
		return this.field_15434.method_15113();
	}
}
