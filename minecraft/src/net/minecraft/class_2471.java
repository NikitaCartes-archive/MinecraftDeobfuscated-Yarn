package net.minecraft;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2471 extends class_2474<class_1792> {
	private static final Logger field_11469 = LogManager.getLogger();

	public class_2471(class_2403 arg) {
		super(arg, class_2378.field_11142);
	}

	@Override
	protected void method_10514() {
		this.method_10505(class_3481.field_15481, class_3489.field_15544);
		this.method_10505(class_3481.field_15471, class_3489.field_15537);
		this.method_10505(class_3481.field_15465, class_3489.field_15531);
		this.method_10505(class_3481.field_15499, class_3489.field_15555);
		this.method_10505(class_3481.field_15493, class_3489.field_15551);
		this.method_10505(class_3481.field_15479, class_3489.field_15542);
		this.method_10505(class_3481.field_15494, class_3489.field_15552);
		this.method_10505(class_3481.field_15502, class_3489.field_15557);
		this.method_10505(class_3481.field_15468, class_3489.field_15534);
		this.method_10505(class_3481.field_17619, class_3489.field_17620);
		this.method_10505(class_3481.field_15477, class_3489.field_15540);
		this.method_10505(class_3481.field_15495, class_3489.field_15553);
		this.method_10505(class_3481.field_15462, class_3489.field_15528);
		this.method_10505(class_3481.field_15482, class_3489.field_15545);
		this.method_10505(class_3481.field_15485, class_3489.field_15546);
		this.method_10505(class_3481.field_15498, class_3489.field_15554);
		this.method_10505(class_3481.field_15458, class_3489.field_15525);
		this.method_10505(class_3481.field_15489, class_3489.field_15549);
		this.method_10505(class_3481.field_15474, class_3489.field_15538);
		this.method_10505(class_3481.field_15475, class_3489.field_15539);
		this.method_10505(class_3481.field_15466, class_3489.field_15532);
		this.method_10505(class_3481.field_15469, class_3489.field_15535);
		this.method_10505(class_3481.field_15504, class_3489.field_15560);
		this.method_10505(class_3481.field_15459, class_3489.field_15526);
		this.method_10505(class_3481.field_15486, class_3489.field_15547);
		this.method_10505(class_3481.field_15463, class_3489.field_15529);
		this.method_10505(class_3481.field_15503, class_3489.field_15558);
		this.method_10505(class_3481.field_15491, class_3489.field_15550);
		this.method_10505(class_3481.field_15487, class_3489.field_15548);
		this.method_10505(class_3481.field_15480, class_3489.field_15543);
		this.method_10505(class_3481.field_16443, class_3489.field_16444);
		this.method_10505(class_3481.field_16584, class_3489.field_16585);
		this.method_10512(class_3489.field_15556)
			.method_15150(
				class_1802.field_8539,
				class_1802.field_8824,
				class_1802.field_8671,
				class_1802.field_8379,
				class_1802.field_8049,
				class_1802.field_8778,
				class_1802.field_8329,
				class_1802.field_8617,
				class_1802.field_8855,
				class_1802.field_8629,
				class_1802.field_8405,
				class_1802.field_8128,
				class_1802.field_8124,
				class_1802.field_8295,
				class_1802.field_8586,
				class_1802.field_8572
			);
		this.method_10512(class_3489.field_15536)
			.method_15150(class_1802.field_8533, class_1802.field_8486, class_1802.field_8442, class_1802.field_8730, class_1802.field_8094, class_1802.field_8138);
		this.method_10512(class_3489.field_15527)
			.method_15150(class_1802.field_8429, class_1802.field_8373, class_1802.field_8209, class_1802.field_8509, class_1802.field_8323, class_1802.field_8846);
		this.method_10505(class_3481.field_15472, class_3489.field_15533);
		this.method_10512(class_3489.field_15541)
			.method_15150(
				class_1802.field_8144,
				class_1802.field_8075,
				class_1802.field_8425,
				class_1802.field_8623,
				class_1802.field_8502,
				class_1802.field_8534,
				class_1802.field_8344,
				class_1802.field_8834,
				class_1802.field_8065,
				class_1802.field_8355,
				class_1802.field_8731,
				class_1802.field_8806
			);
		this.method_10512(class_3489.field_17487).method_15150(class_1802.field_8713, class_1802.field_8665);
		this.method_10512(class_3489.field_18317).method_15150(class_1802.field_8107, class_1802.field_8087, class_1802.field_8236);
	}

	protected void method_10505(class_3494<class_2248> arg, class_3494<class_1792> arg2) {
		class_3494.class_3495<class_1792> lv = this.method_10512(arg2);

		for (class_3494.class_3496<class_2248> lv2 : arg.method_15139()) {
			class_3494.class_3496<class_1792> lv3 = this.method_10504(lv2);
			lv.method_15149(lv3);
		}
	}

	private class_3494.class_3496<class_1792> method_10504(class_3494.class_3496<class_2248> arg) {
		if (arg instanceof class_3494.class_3497) {
			return new class_3494.class_3497<>(((class_3494.class_3497)arg).method_15158());
		} else if (arg instanceof class_3494.class_3498) {
			List<class_1792> list = Lists.<class_1792>newArrayList();

			for (class_2248 lv : ((class_3494.class_3498)arg).method_15159()) {
				class_1792 lv2 = lv.method_8389();
				if (lv2 == class_1802.field_8162) {
					field_11469.warn("Itemless block copied to item tag: {}", class_2378.field_11146.method_10221(lv));
				} else {
					list.add(lv2);
				}
			}

			return new class_3494.class_3498<>(list);
		} else {
			throw new UnsupportedOperationException("Unknown tag entry " + arg);
		}
	}

	@Override
	protected Path method_10510(class_2960 arg) {
		return this.field_11483.method_10313().resolve("data/" + arg.method_12836() + "/tags/items/" + arg.method_12832() + ".json");
	}

	@Override
	public String method_10321() {
		return "Item Tags";
	}

	@Override
	protected void method_10511(class_3503<class_1792> arg) {
		class_3489.method_15103(arg);
	}
}
