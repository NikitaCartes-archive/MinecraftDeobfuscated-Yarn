package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_2430 implements Consumer<BiConsumer<class_2960, class_52.class_53>> {
	private static final class_209.class_210 field_11336 = class_223.method_945(
		class_2073.class_2074.method_8973().method_8978(new class_2035(class_1893.field_9099, class_2096.class_2100.method_9053(1)))
	);
	private static final class_209.class_210 field_11337 = field_11336.method_16780();
	private static final class_209.class_210 field_11343 = class_223.method_945(class_2073.class_2074.method_8973().method_8977(class_1802.field_8868));
	private static final class_209.class_210 field_11342 = field_11343.method_893(field_11336);
	private static final class_209.class_210 field_11341 = field_11342.method_16780();
	private static final Set<class_1792> field_11340 = (Set<class_1792>)Stream.of(
			class_2246.field_10081,
			class_2246.field_10327,
			class_2246.field_10502,
			class_2246.field_10481,
			class_2246.field_10177,
			class_2246.field_10432,
			class_2246.field_10241,
			class_2246.field_10042,
			class_2246.field_10337,
			class_2246.field_10603,
			class_2246.field_10371,
			class_2246.field_10605,
			class_2246.field_10373,
			class_2246.field_10532,
			class_2246.field_10140,
			class_2246.field_10055,
			class_2246.field_10203,
			class_2246.field_10320,
			class_2246.field_10275,
			class_2246.field_10063,
			class_2246.field_10407,
			class_2246.field_10051,
			class_2246.field_10268,
			class_2246.field_10068,
			class_2246.field_10199,
			class_2246.field_10600
		)
		.map(class_1935::method_8389)
		.collect(ImmutableSet.toImmutableSet());
	private static final float[] field_11339 = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] field_11338 = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
	private final Map<class_2960, class_52.class_53> field_16493 = Maps.<class_2960, class_52.class_53>newHashMap();

	private static <T> T method_10393(class_1935 arg, class_116<T> arg2) {
		return !field_11340.contains(arg.method_8389()) ? arg2.method_511(class_104.method_478()) : arg2.method_512();
	}

	private static <T> T method_10392(class_1935 arg, class_192<T> arg2) {
		return !field_11340.contains(arg.method_8389()) ? arg2.method_840(class_201.method_871()) : arg2.method_512();
	}

	private static class_52.class_53 method_10394(class_1935 arg) {
		return class_52.method_324().method_336(method_10392(arg, class_55.method_347().method_352(class_44.method_289(1)).method_351(class_77.method_411(arg))));
	}

	private static class_52.class_53 method_10381(class_2248 arg, class_209.class_210 arg2, class_79.class_80<?> arg3) {
		return class_52.method_324()
			.method_336(class_55.method_347().method_352(class_44.method_289(1)).method_351(class_77.method_411(arg).method_421(arg2).method_417(arg3)));
	}

	private static class_52.class_53 method_10397(class_2248 arg, class_79.class_80<?> arg2) {
		return method_10381(arg, field_11336, arg2);
	}

	private static class_52.class_53 method_10380(class_2248 arg, class_79.class_80<?> arg2) {
		return method_10381(arg, field_11343, arg2);
	}

	private static class_52.class_53 method_10388(class_2248 arg, class_79.class_80<?> arg2) {
		return method_10381(arg, field_11342, arg2);
	}

	private static class_52.class_53 method_10382(class_2248 arg, class_1935 arg2) {
		return method_10397(arg, (class_79.class_80<?>)method_10392(arg, class_77.method_411(arg2)));
	}

	private static class_52.class_53 method_10384(class_1935 arg, class_59 arg2) {
		return class_52.method_324()
			.method_336(
				class_55.method_347()
					.method_352(class_44.method_289(1))
					.method_351((class_79.class_80<?>)method_10393(arg, class_77.method_411(arg).method_438(class_141.method_621(arg2))))
			);
	}

	private static class_52.class_53 method_10386(class_2248 arg, class_1935 arg2, class_59 arg3) {
		return method_10397(arg, (class_79.class_80<?>)method_10393(arg, class_77.method_411(arg2).method_438(class_141.method_621(arg3))));
	}

	private static class_52.class_53 method_10373(class_1935 arg) {
		return class_52.method_324()
			.method_336(class_55.method_347().method_356(field_11336).method_352(class_44.method_289(1)).method_351(class_77.method_411(arg)));
	}

	private static class_52.class_53 method_10389(class_1935 arg) {
		return class_52.method_324()
			.method_336(
				method_10392(class_2246.field_10495, class_55.method_347().method_352(class_44.method_289(1)).method_351(class_77.method_411(class_2246.field_10495)))
			)
			.method_336(method_10392(arg, class_55.method_347().method_352(class_44.method_289(1)).method_351(class_77.method_411(arg))));
	}

	private static class_52.class_53 method_10383(class_2248 arg) {
		return class_52.method_324()
			.method_336(
				class_55.method_347()
					.method_352(class_44.method_289(1))
					.method_351(
						(class_79.class_80<?>)method_10393(
							arg,
							class_77.method_411(arg)
								.method_438(
									class_141.method_621(class_44.method_289(2)).method_524(class_212.method_900(arg).method_907(class_2482.field_11501, class_2771.field_12682))
								)
						)
					)
			);
	}

	private static <T extends Comparable<T>> class_52.class_53 method_10375(class_2248 arg, class_2769<T> arg2, T comparable) {
		return class_52.method_324()
			.method_336(
				method_10392(
					arg,
					class_55.method_347()
						.method_352(class_44.method_289(1))
						.method_351(class_77.method_411(arg).method_421(class_212.method_900(arg).method_907(arg2, comparable)))
				)
			);
	}

	private static class_52.class_53 method_10396(class_2248 arg) {
		return class_52.method_324()
			.method_336(
				method_10392(
					arg,
					class_55.method_347()
						.method_352(class_44.method_289(1))
						.method_351(class_77.method_411(arg).method_438(class_101.method_473(class_101.class_102.field_1023)))
				)
			);
	}

	private static class_52.class_53 method_16876(class_2248 arg) {
		return class_52.method_324()
			.method_336(
				method_10392(
					arg,
					class_55.method_347()
						.method_352(class_44.method_289(1))
						.method_351(
							class_77.method_411(arg)
								.method_438(class_101.method_473(class_101.class_102.field_1023))
								.method_438(
									class_3837.method_16848(class_3837.class_3840.field_17027)
										.method_16856("Lock", "BlockEntityTag.Lock")
										.method_16856("LootTable", "BlockEntityTag.LootTable")
										.method_16856("LootTableSeed", "BlockEntityTag.LootTableSeed")
								)
								.method_438(class_134.method_601().method_602(class_67.method_390(class_2480.field_11495)))
						)
				)
			);
	}

	private static class_52.class_53 method_16877(class_2248 arg) {
		return class_52.method_324()
			.method_336(
				method_10392(
					arg,
					class_55.method_347()
						.method_352(class_44.method_289(1))
						.method_351(
							class_77.method_411(arg)
								.method_438(class_101.method_473(class_101.class_102.field_1023))
								.method_438(class_3837.method_16848(class_3837.class_3840.field_17027).method_16856("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static class_52.class_53 method_10377(class_2248 arg, class_1792 arg2) {
		return method_10397(arg, (class_79.class_80<?>)method_10393(arg, class_77.method_411(arg2).method_438(class_94.method_455(class_1893.field_9130))));
	}

	private static class_52.class_53 method_10385(class_2248 arg, class_1935 arg2) {
		return method_10397(
			arg,
			(class_79.class_80<?>)method_10393(
				arg, class_77.method_411(arg2).method_438(class_141.method_621(class_61.method_377(-6.0F, 2.0F))).method_438(class_114.method_506(class_42.method_280(0)))
			)
		);
	}

	private static class_52.class_53 method_10371(class_2248 arg) {
		return method_10380(
			arg,
			(class_79.class_80<?>)method_10393(
				arg, class_77.method_411(class_1802.field_8317).method_421(class_219.method_932(0.125F)).method_438(class_94.method_461(class_1893.field_9130, 2))
			)
		);
	}

	private static class_52.class_53 method_10387(class_2248 arg, class_1792 arg2) {
		return class_52.method_324()
			.method_336(
				method_10393(
					arg,
					class_55.method_347()
						.method_352(class_44.method_289(1))
						.method_351(
							class_77.method_411(arg2)
								.method_438(class_141.method_621(class_40.method_273(3, 0.06666667F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 0)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.13333334F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 1)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.2F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 2)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.26666668F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 3)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.33333334F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 4)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.4F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 5)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.46666667F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 6)))
								.method_438(class_141.method_621(class_40.method_273(3, 0.53333336F)).method_524(class_212.method_900(arg).method_907(class_2513.field_11584, 7)))
						)
				)
			);
	}

	private static class_52.class_53 method_10372(class_1935 arg) {
		return class_52.method_324()
			.method_336(class_55.method_347().method_352(class_44.method_289(1)).method_356(field_11343).method_351(class_77.method_411(arg)));
	}

	private static class_52.class_53 method_10390(class_2248 arg, class_2248 arg2, float... fs) {
		return method_10388(arg, ((class_85.class_86)method_10392(arg, class_77.method_411(arg2))).method_421(class_182.method_800(class_1893.field_9130, fs)))
			.method_336(
				class_55.method_347()
					.method_352(class_44.method_289(1))
					.method_356(field_11341)
					.method_351(
						((class_85.class_86)method_10393(arg, class_77.method_411(class_1802.field_8600).method_438(class_141.method_621(class_61.method_377(1.0F, 2.0F)))))
							.method_421(class_182.method_800(class_1893.field_9130, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static class_52.class_53 method_10378(class_2248 arg, class_2248 arg2, float... fs) {
		return method_10390(arg, arg2, fs)
			.method_336(
				class_55.method_347()
					.method_352(class_44.method_289(1))
					.method_356(field_11341)
					.method_351(
						((class_85.class_86)method_10392(arg, class_77.method_411(class_1802.field_8279)))
							.method_421(class_182.method_800(class_1893.field_9130, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static class_52.class_53 method_10391(class_2248 arg, class_1792 arg2, class_1792 arg3, class_209.class_210 arg4) {
		return method_10393(
			arg,
			class_52.method_324()
				.method_336(class_55.method_347().method_351(class_77.method_411(arg2).method_421(arg4).method_417(class_77.method_411(arg3))))
				.method_336(
					class_55.method_347().method_356(arg4).method_351(class_77.method_411(arg3).method_438(class_94.method_463(class_1893.field_9130, 0.5714286F, 3)))
				)
		);
	}

	public static class_52.class_53 method_10395() {
		return class_52.method_324();
	}

	public void method_10379(BiConsumer<class_2960, class_52.class_53> biConsumer) {
		this.method_16329(class_2246.field_10474);
		this.method_16329(class_2246.field_10289);
		this.method_16329(class_2246.field_10508);
		this.method_16329(class_2246.field_10346);
		this.method_16329(class_2246.field_10115);
		this.method_16329(class_2246.field_10093);
		this.method_16329(class_2246.field_10566);
		this.method_16329(class_2246.field_10253);
		this.method_16329(class_2246.field_10445);
		this.method_16329(class_2246.field_10161);
		this.method_16329(class_2246.field_9975);
		this.method_16329(class_2246.field_10148);
		this.method_16329(class_2246.field_10334);
		this.method_16329(class_2246.field_10218);
		this.method_16329(class_2246.field_10075);
		this.method_16329(class_2246.field_10394);
		this.method_16329(class_2246.field_10217);
		this.method_16329(class_2246.field_10575);
		this.method_16329(class_2246.field_10276);
		this.method_16329(class_2246.field_10385);
		this.method_16329(class_2246.field_10160);
		this.method_16329(class_2246.field_10102);
		this.method_16329(class_2246.field_10534);
		this.method_16329(class_2246.field_10571);
		this.method_16329(class_2246.field_10212);
		this.method_16329(class_2246.field_10431);
		this.method_16329(class_2246.field_10037);
		this.method_16329(class_2246.field_10511);
		this.method_16329(class_2246.field_10306);
		this.method_16329(class_2246.field_10533);
		this.method_16329(class_2246.field_10010);
		this.method_16329(class_2246.field_10436);
		this.method_16329(class_2246.field_10366);
		this.method_16329(class_2246.field_10254);
		this.method_16329(class_2246.field_10622);
		this.method_16329(class_2246.field_10244);
		this.method_16329(class_2246.field_10519);
		this.method_16329(class_2246.field_10126);
		this.method_16329(class_2246.field_10155);
		this.method_16329(class_2246.field_10307);
		this.method_16329(class_2246.field_10303);
		this.method_16329(class_2246.field_9999);
		this.method_16329(class_2246.field_10178);
		this.method_16329(class_2246.field_10250);
		this.method_16329(class_2246.field_10558);
		this.method_16329(class_2246.field_10204);
		this.method_16329(class_2246.field_10084);
		this.method_16329(class_2246.field_10103);
		this.method_16329(class_2246.field_10374);
		this.method_16329(class_2246.field_10258);
		this.method_16329(class_2246.field_10562);
		this.method_16329(class_2246.field_10441);
		this.method_16329(class_2246.field_9979);
		this.method_16329(class_2246.field_10292);
		this.method_16329(class_2246.field_10361);
		this.method_16329(class_2246.field_10179);
		this.method_16329(class_2246.field_10425);
		this.method_16329(class_2246.field_10025);
		this.method_16329(class_2246.field_10615);
		this.method_16329(class_2246.field_10560);
		this.method_16329(class_2246.field_10446);
		this.method_16329(class_2246.field_10095);
		this.method_16329(class_2246.field_10215);
		this.method_16329(class_2246.field_10294);
		this.method_16329(class_2246.field_10490);
		this.method_16329(class_2246.field_10028);
		this.method_16329(class_2246.field_10459);
		this.method_16329(class_2246.field_10423);
		this.method_16329(class_2246.field_10222);
		this.method_16329(class_2246.field_10619);
		this.method_16329(class_2246.field_10259);
		this.method_16329(class_2246.field_10514);
		this.method_16329(class_2246.field_10113);
		this.method_16329(class_2246.field_10170);
		this.method_16329(class_2246.field_10314);
		this.method_16329(class_2246.field_10146);
		this.method_16329(class_2246.field_10182);
		this.method_16329(class_2246.field_10449);
		this.method_16329(class_2246.field_10086);
		this.method_16329(class_2246.field_10226);
		this.method_16329(class_2246.field_10573);
		this.method_16329(class_2246.field_10270);
		this.method_16329(class_2246.field_10048);
		this.method_16329(class_2246.field_10156);
		this.method_16329(class_2246.field_10315);
		this.method_16329(class_2246.field_10554);
		this.method_16329(class_2246.field_9995);
		this.method_16329(class_2246.field_10606);
		this.method_16329(class_2246.field_10548);
		this.method_16329(class_2246.field_10251);
		this.method_16329(class_2246.field_10559);
		this.method_16329(class_2246.field_10205);
		this.method_16329(class_2246.field_10085);
		this.method_16329(class_2246.field_10104);
		this.method_16329(class_2246.field_9989);
		this.method_16329(class_2246.field_10540);
		this.method_16329(class_2246.field_10336);
		this.method_16329(class_2246.field_10563);
		this.method_16329(class_2246.field_10091);
		this.method_16329(class_2246.field_10201);
		this.method_16329(class_2246.field_9980);
		this.method_16329(class_2246.field_10121);
		this.method_16329(class_2246.field_10411);
		this.method_16329(class_2246.field_10231);
		this.method_16329(class_2246.field_10284);
		this.method_16329(class_2246.field_10544);
		this.method_16329(class_2246.field_10330);
		this.method_16329(class_2246.field_9983);
		this.method_16329(class_2246.field_10167);
		this.method_16329(class_2246.field_10596);
		this.method_16329(class_2246.field_10363);
		this.method_16329(class_2246.field_10158);
		this.method_16329(class_2246.field_10484);
		this.method_16329(class_2246.field_10332);
		this.method_16329(class_2246.field_10592);
		this.method_16329(class_2246.field_10026);
		this.method_16329(class_2246.field_10397);
		this.method_16329(class_2246.field_10470);
		this.method_16329(class_2246.field_10523);
		this.method_16329(class_2246.field_10494);
		this.method_16329(class_2246.field_10029);
		this.method_16329(class_2246.field_10424);
		this.method_16329(class_2246.field_10223);
		this.method_16329(class_2246.field_10620);
		this.method_16329(class_2246.field_10261);
		this.method_16329(class_2246.field_10515);
		this.method_16329(class_2246.field_10114);
		this.method_16329(class_2246.field_10147);
		this.method_16329(class_2246.field_10009);
		this.method_16329(class_2246.field_10450);
		this.method_16329(class_2246.field_10137);
		this.method_16329(class_2246.field_10323);
		this.method_16329(class_2246.field_10486);
		this.method_16329(class_2246.field_10017);
		this.method_16329(class_2246.field_10608);
		this.method_16329(class_2246.field_10246);
		this.method_16329(class_2246.field_10056);
		this.method_16329(class_2246.field_10065);
		this.method_16329(class_2246.field_10416);
		this.method_16329(class_2246.field_10552);
		this.method_16329(class_2246.field_10576);
		this.method_16329(class_2246.field_10188);
		this.method_16329(class_2246.field_10089);
		this.method_16329(class_2246.field_10392);
		this.method_16329(class_2246.field_10588);
		this.method_16329(class_2246.field_10266);
		this.method_16329(class_2246.field_10364);
		this.method_16329(class_2246.field_10159);
		this.method_16329(class_2246.field_10593);
		this.method_16329(class_2246.field_10471);
		this.method_16329(class_2246.field_10524);
		this.method_16329(class_2246.field_10142);
		this.method_16329(class_2246.field_10348);
		this.method_16329(class_2246.field_10234);
		this.method_16329(class_2246.field_10569);
		this.method_16329(class_2246.field_10408);
		this.method_16329(class_2246.field_10122);
		this.method_16329(class_2246.field_10625);
		this.method_16329(class_2246.field_9990);
		this.method_16329(class_2246.field_10495);
		this.method_16329(class_2246.field_10057);
		this.method_16329(class_2246.field_10066);
		this.method_16329(class_2246.field_10417);
		this.method_16329(class_2246.field_10553);
		this.method_16329(class_2246.field_10278);
		this.method_16329(class_2246.field_10493);
		this.method_16329(class_2246.field_10481);
		this.method_16329(class_2246.field_10177);
		this.method_16329(class_2246.field_10241);
		this.method_16329(class_2246.field_10042);
		this.method_16329(class_2246.field_10337);
		this.method_16329(class_2246.field_10535);
		this.method_16329(class_2246.field_10105);
		this.method_16329(class_2246.field_10414);
		this.method_16329(class_2246.field_10224);
		this.method_16329(class_2246.field_10582);
		this.method_16329(class_2246.field_10377);
		this.method_16329(class_2246.field_10429);
		this.method_16329(class_2246.field_10002);
		this.method_16329(class_2246.field_10153);
		this.method_16329(class_2246.field_10044);
		this.method_16329(class_2246.field_10437);
		this.method_16329(class_2246.field_10451);
		this.method_16329(class_2246.field_10546);
		this.method_16329(class_2246.field_10611);
		this.method_16329(class_2246.field_10184);
		this.method_16329(class_2246.field_10015);
		this.method_16329(class_2246.field_10325);
		this.method_16329(class_2246.field_10143);
		this.method_16329(class_2246.field_10014);
		this.method_16329(class_2246.field_10444);
		this.method_16329(class_2246.field_10349);
		this.method_16329(class_2246.field_10590);
		this.method_16329(class_2246.field_10235);
		this.method_16329(class_2246.field_10570);
		this.method_16329(class_2246.field_10409);
		this.method_16329(class_2246.field_10123);
		this.method_16329(class_2246.field_10526);
		this.method_16329(class_2246.field_10328);
		this.method_16329(class_2246.field_10626);
		this.method_16329(class_2246.field_10256);
		this.method_16329(class_2246.field_10616);
		this.method_16329(class_2246.field_10030);
		this.method_16329(class_2246.field_10453);
		this.method_16329(class_2246.field_10135);
		this.method_16329(class_2246.field_10006);
		this.method_16329(class_2246.field_10297);
		this.method_16329(class_2246.field_10350);
		this.method_16329(class_2246.field_10190);
		this.method_16329(class_2246.field_10130);
		this.method_16329(class_2246.field_10359);
		this.method_16329(class_2246.field_10466);
		this.method_16329(class_2246.field_9977);
		this.method_16329(class_2246.field_10482);
		this.method_16329(class_2246.field_10290);
		this.method_16329(class_2246.field_10512);
		this.method_16329(class_2246.field_10040);
		this.method_16329(class_2246.field_10393);
		this.method_16329(class_2246.field_10591);
		this.method_16329(class_2246.field_10209);
		this.method_16329(class_2246.field_10433);
		this.method_16329(class_2246.field_10510);
		this.method_16329(class_2246.field_10043);
		this.method_16329(class_2246.field_10473);
		this.method_16329(class_2246.field_10338);
		this.method_16329(class_2246.field_10536);
		this.method_16329(class_2246.field_10106);
		this.method_16329(class_2246.field_10415);
		this.method_16329(class_2246.field_10381);
		this.method_16329(class_2246.field_10344);
		this.method_16329(class_2246.field_10117);
		this.method_16329(class_2246.field_10518);
		this.method_16329(class_2246.field_10420);
		this.method_16329(class_2246.field_10360);
		this.method_16329(class_2246.field_10467);
		this.method_16329(class_2246.field_9978);
		this.method_16329(class_2246.field_10483);
		this.method_16329(class_2246.field_10291);
		this.method_16329(class_2246.field_10513);
		this.method_16329(class_2246.field_10041);
		this.method_16329(class_2246.field_10457);
		this.method_16329(class_2246.field_10196);
		this.method_16329(class_2246.field_10020);
		this.method_16329(class_2246.field_10299);
		this.method_16329(class_2246.field_10319);
		this.method_16329(class_2246.field_10144);
		this.method_16329(class_2246.field_10132);
		this.method_16329(class_2246.field_10455);
		this.method_16329(class_2246.field_10286);
		this.method_16329(class_2246.field_10505);
		this.method_16329(class_2246.field_9992);
		this.method_16329(class_2246.field_10462);
		this.method_16329(class_2246.field_10092);
		this.method_16329(class_2246.field_10541);
		this.method_16329(class_2246.field_9986);
		this.method_16329(class_2246.field_10166);
		this.method_16329(class_2246.field_10282);
		this.method_16329(class_2246.field_10595);
		this.method_16329(class_2246.field_10280);
		this.method_16329(class_2246.field_10538);
		this.method_16329(class_2246.field_10345);
		this.method_16329(class_2246.field_10096);
		this.method_16329(class_2246.field_10046);
		this.method_16329(class_2246.field_10567);
		this.method_16329(class_2246.field_10220);
		this.method_16329(class_2246.field_10052);
		this.method_16329(class_2246.field_10078);
		this.method_16329(class_2246.field_10426);
		this.method_16329(class_2246.field_10550);
		this.method_16329(class_2246.field_10004);
		this.method_16329(class_2246.field_10475);
		this.method_16329(class_2246.field_10383);
		this.method_16329(class_2246.field_10501);
		this.method_16329(class_2246.field_10107);
		this.method_16329(class_2246.field_10210);
		this.method_16329(class_2246.field_10585);
		this.method_16329(class_2246.field_10242);
		this.method_16329(class_2246.field_10542);
		this.method_16329(class_2246.field_10421);
		this.method_16329(class_2246.field_10434);
		this.method_16329(class_2246.field_10038);
		this.method_16329(class_2246.field_10172);
		this.method_16329(class_2246.field_10308);
		this.method_16329(class_2246.field_10206);
		this.method_16329(class_2246.field_10011);
		this.method_16329(class_2246.field_10439);
		this.method_16329(class_2246.field_10367);
		this.method_16329(class_2246.field_10058);
		this.method_16329(class_2246.field_10458);
		this.method_16329(class_2246.field_10197);
		this.method_16329(class_2246.field_10022);
		this.method_16329(class_2246.field_10300);
		this.method_16329(class_2246.field_10321);
		this.method_16329(class_2246.field_10145);
		this.method_16329(class_2246.field_10133);
		this.method_16329(class_2246.field_10522);
		this.method_16329(class_2246.field_10353);
		this.method_16329(class_2246.field_10628);
		this.method_16329(class_2246.field_10233);
		this.method_16329(class_2246.field_10404);
		this.method_16329(class_2246.field_10456);
		this.method_16329(class_2246.field_10023);
		this.method_16329(class_2246.field_10529);
		this.method_16329(class_2246.field_10287);
		this.method_16329(class_2246.field_10506);
		this.method_16329(class_2246.field_9993);
		this.method_16329(class_2246.field_10342);
		this.method_16329(class_2246.field_10614);
		this.method_16329(class_2246.field_10264);
		this.method_16329(class_2246.field_10396);
		this.method_16329(class_2246.field_10111);
		this.method_16329(class_2246.field_10488);
		this.method_16329(class_2246.field_10502);
		this.method_16329(class_2246.field_10081);
		this.method_16329(class_2246.field_10211);
		this.method_16329(class_2246.field_10435);
		this.method_16329(class_2246.field_10039);
		this.method_16329(class_2246.field_10173);
		this.method_16329(class_2246.field_10310);
		this.method_16329(class_2246.field_10207);
		this.method_16329(class_2246.field_10012);
		this.method_16329(class_2246.field_10440);
		this.method_16329(class_2246.field_10549);
		this.method_16329(class_2246.field_10245);
		this.method_16329(class_2246.field_10607);
		this.method_16329(class_2246.field_10386);
		this.method_16329(class_2246.field_10497);
		this.method_16329(class_2246.field_9994);
		this.method_16329(class_2246.field_10216);
		this.method_16329(class_2246.field_10269);
		this.method_16329(class_2246.field_10530);
		this.method_16329(class_2246.field_10413);
		this.method_16329(class_2246.field_10059);
		this.method_16329(class_2246.field_10072);
		this.method_16329(class_2246.field_10252);
		this.method_16329(class_2246.field_10127);
		this.method_16329(class_2246.field_10489);
		this.method_16329(class_2246.field_10311);
		this.method_16329(class_2246.field_10630);
		this.method_16329(class_2246.field_10001);
		this.method_16329(class_2246.field_10517);
		this.method_16329(class_2246.field_10083);
		this.method_16329(class_2246.field_16492);
		this.method_16256(class_2246.field_10362, class_2246.field_10566);
		this.method_16256(class_2246.field_10589, class_1802.field_8276);
		this.method_16256(class_2246.field_10194, class_2246.field_10566);
		this.method_16256(class_2246.field_10463, class_2246.field_9993);
		this.method_16256(class_2246.field_10108, class_2246.field_10211);
		this.method_16293(class_2246.field_10340, arg -> method_10382(arg, class_2246.field_10445));
		this.method_16293(class_2246.field_10219, arg -> method_10382(arg, class_2246.field_10566));
		this.method_16293(class_2246.field_10520, arg -> method_10382(arg, class_2246.field_10566));
		this.method_16293(class_2246.field_10402, arg -> method_10382(arg, class_2246.field_10566));
		this.method_16293(class_2246.field_10309, arg -> method_10382(arg, class_2246.field_10614));
		this.method_16293(class_2246.field_10629, arg -> method_10382(arg, class_2246.field_10264));
		this.method_16293(class_2246.field_10000, arg -> method_10382(arg, class_2246.field_10396));
		this.method_16293(class_2246.field_10516, arg -> method_10382(arg, class_2246.field_10111));
		this.method_16293(class_2246.field_10464, arg -> method_10382(arg, class_2246.field_10488));
		this.method_16293(class_2246.field_10504, arg -> method_10386(arg, class_1802.field_8529, class_44.method_289(3)));
		this.method_16293(class_2246.field_10460, arg -> method_10386(arg, class_1802.field_8696, class_44.method_289(4)));
		this.method_16293(class_2246.field_10443, arg -> method_10386(arg, class_2246.field_10540, class_44.method_289(8)));
		this.method_16293(class_2246.field_10491, arg -> method_10386(arg, class_1802.field_8543, class_44.method_289(4)));
		this.method_16258(class_2246.field_10021, method_10384(class_1802.field_8233, class_61.method_377(0.0F, 1.0F)));
		this.method_16285(class_2246.field_10468);
		this.method_16285(class_2246.field_10192);
		this.method_16285(class_2246.field_10577);
		this.method_16285(class_2246.field_10304);
		this.method_16285(class_2246.field_10564);
		this.method_16285(class_2246.field_10076);
		this.method_16285(class_2246.field_10128);
		this.method_16285(class_2246.field_10354);
		this.method_16285(class_2246.field_10151);
		this.method_16285(class_2246.field_9981);
		this.method_16285(class_2246.field_10162);
		this.method_16285(class_2246.field_10365);
		this.method_16285(class_2246.field_10598);
		this.method_16285(class_2246.field_10249);
		this.method_16285(class_2246.field_10400);
		this.method_16285(class_2246.field_10061);
		this.method_16285(class_2246.field_10074);
		this.method_16285(class_2246.field_10358);
		this.method_16285(class_2246.field_10273);
		this.method_16285(class_2246.field_9998);
		this.method_16285(class_2246.field_10138);
		this.method_16285(class_2246.field_10324);
		this.method_16285(class_2246.field_10487);
		this.method_16285(class_2246.field_10018);
		this.method_16285(class_2246.field_10586);
		this.method_16293(class_2246.field_10031, class_2430::method_10383);
		this.method_16293(class_2246.field_10257, class_2430::method_10383);
		this.method_16293(class_2246.field_10191, class_2430::method_10383);
		this.method_16293(class_2246.field_10351, class_2430::method_10383);
		this.method_16293(class_2246.field_10500, class_2430::method_10383);
		this.method_16293(class_2246.field_10623, class_2430::method_10383);
		this.method_16293(class_2246.field_10617, class_2430::method_10383);
		this.method_16293(class_2246.field_10390, class_2430::method_10383);
		this.method_16293(class_2246.field_10119, class_2430::method_10383);
		this.method_16293(class_2246.field_10298, class_2430::method_10383);
		this.method_16293(class_2246.field_10236, class_2430::method_10383);
		this.method_16293(class_2246.field_10389, class_2430::method_10383);
		this.method_16293(class_2246.field_10175, class_2430::method_10383);
		this.method_16293(class_2246.field_10237, class_2430::method_10383);
		this.method_16293(class_2246.field_10624, class_2430::method_10383);
		this.method_16293(class_2246.field_10007, class_2430::method_10383);
		this.method_16293(class_2246.field_18891, class_2430::method_10383);
		this.method_16293(class_2246.field_18890, class_2430::method_10383);
		this.method_16293(class_2246.field_10071, class_2430::method_10383);
		this.method_16293(class_2246.field_10131, class_2430::method_10383);
		this.method_16293(class_2246.field_10454, class_2430::method_10383);
		this.method_16293(class_2246.field_10136, class_2430::method_10383);
		this.method_16293(class_2246.field_10329, class_2430::method_10383);
		this.method_16293(class_2246.field_10283, class_2430::method_10383);
		this.method_16293(class_2246.field_10024, class_2430::method_10383);
		this.method_16293(class_2246.field_10412, class_2430::method_10383);
		this.method_16293(class_2246.field_10405, class_2430::method_10383);
		this.method_16293(class_2246.field_10064, class_2430::method_10383);
		this.method_16293(class_2246.field_10262, class_2430::method_10383);
		this.method_16293(class_2246.field_10601, class_2430::method_10383);
		this.method_16293(class_2246.field_10189, class_2430::method_10383);
		this.method_16293(class_2246.field_10016, class_2430::method_10383);
		this.method_16293(class_2246.field_10478, class_2430::method_10383);
		this.method_16293(class_2246.field_10322, class_2430::method_10383);
		this.method_16293(class_2246.field_10507, class_2430::method_10383);
		this.method_16293(class_2246.field_10232, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_10352, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_10403, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_9973, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_10627, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_10149, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_10521, arg -> method_10375(arg, class_2323.field_10946, class_2756.field_12607));
		this.method_16293(class_2246.field_10461, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10527, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10288, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10109, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10141, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10561, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10621, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10326, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10180, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10230, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10019, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10410, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10610, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10069, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10120, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10356, arg -> method_10375(arg, class_2244.field_9967, class_2742.field_12560));
		this.method_16293(class_2246.field_10378, arg -> method_10375(arg, class_2320.field_10929, class_2756.field_12607));
		this.method_16293(class_2246.field_10583, arg -> method_10375(arg, class_2320.field_10929, class_2756.field_12607));
		this.method_16293(class_2246.field_10003, arg -> method_10375(arg, class_2320.field_10929, class_2756.field_12607));
		this.method_16293(class_2246.field_10430, arg -> method_10375(arg, class_2320.field_10929, class_2756.field_12607));
		this.method_16293(class_2246.field_10375, arg -> method_10375(arg, class_2530.field_11621, false));
		this.method_16293(
			class_2246.field_10302,
			arg -> class_52.method_324()
					.method_336(
						class_55.method_347()
							.method_352(class_44.method_289(1))
							.method_351(
								(class_79.class_80<?>)method_10393(
									arg,
									class_77.method_411(class_1802.field_8116)
										.method_438(class_141.method_621(class_44.method_289(3)).method_524(class_212.method_900(arg).method_907(class_2282.field_10779, 2)))
								)
							)
					)
		);
		this.method_16293(
			class_2246.field_10476,
			arg -> class_52.method_324()
					.method_336(
						class_55.method_347()
							.method_352(class_44.method_289(1))
							.method_351(
								(class_79.class_80<?>)method_10393(
									arg,
									class_77.method_411(arg)
										.method_438(class_141.method_621(class_44.method_289(2)).method_524(class_212.method_900(arg).method_907(class_2472.field_11472, 2)))
										.method_438(class_141.method_621(class_44.method_289(3)).method_524(class_212.method_900(arg).method_907(class_2472.field_11472, 3)))
										.method_438(class_141.method_621(class_44.method_289(4)).method_524(class_212.method_900(arg).method_907(class_2472.field_11472, 4)))
								)
							)
					)
		);
		this.method_16293(
			class_2246.field_17563,
			arg -> class_52.method_324()
					.method_336(class_55.method_347().method_351((class_79.class_80<?>)method_10393(arg, class_77.method_411(class_1802.field_17530))))
					.method_336(
						class_55.method_347().method_351(class_77.method_411(class_1802.field_8324)).method_356(class_212.method_900(arg).method_907(class_3962.field_17565, 8))
					)
		);
		this.method_16293(class_2246.field_10327, class_2430::method_10396);
		this.method_16293(class_2246.field_10333, class_2430::method_10396);
		this.method_16293(class_2246.field_10034, class_2430::method_10396);
		this.method_16293(class_2246.field_10200, class_2430::method_10396);
		this.method_16293(class_2246.field_10228, class_2430::method_10396);
		this.method_16293(class_2246.field_10485, class_2430::method_10396);
		this.method_16293(class_2246.field_10181, class_2430::method_10396);
		this.method_16293(class_2246.field_10312, class_2430::method_10396);
		this.method_16293(class_2246.field_10380, class_2430::method_10396);
		this.method_16293(class_2246.field_16334, class_2430::method_10396);
		this.method_16293(class_2246.field_16333, class_2430::method_10396);
		this.method_16293(class_2246.field_16328, class_2430::method_10396);
		this.method_16293(class_2246.field_16336, class_2430::method_10396);
		this.method_16293(class_2246.field_16331, class_2430::method_10396);
		this.method_16293(class_2246.field_16337, class_2430::method_10396);
		this.method_16293(class_2246.field_16330, class_2430::method_10396);
		this.method_16293(class_2246.field_16329, class_2430::method_10396);
		this.method_16293(class_2246.field_16335, class_2430::method_10396);
		this.method_16293(class_2246.field_16332, class_2430::method_10394);
		this.method_16293(class_2246.field_16541, class_2430::method_10394);
		this.method_16293(class_2246.field_10603, class_2430::method_16876);
		this.method_16293(class_2246.field_10371, class_2430::method_16876);
		this.method_16293(class_2246.field_10605, class_2430::method_16876);
		this.method_16293(class_2246.field_10373, class_2430::method_16876);
		this.method_16293(class_2246.field_10532, class_2430::method_16876);
		this.method_16293(class_2246.field_10140, class_2430::method_16876);
		this.method_16293(class_2246.field_10055, class_2430::method_16876);
		this.method_16293(class_2246.field_10203, class_2430::method_16876);
		this.method_16293(class_2246.field_10320, class_2430::method_16876);
		this.method_16293(class_2246.field_10275, class_2430::method_16876);
		this.method_16293(class_2246.field_10063, class_2430::method_16876);
		this.method_16293(class_2246.field_10407, class_2430::method_16876);
		this.method_16293(class_2246.field_10051, class_2430::method_16876);
		this.method_16293(class_2246.field_10268, class_2430::method_16876);
		this.method_16293(class_2246.field_10068, class_2430::method_16876);
		this.method_16293(class_2246.field_10199, class_2430::method_16876);
		this.method_16293(class_2246.field_10600, class_2430::method_16876);
		this.method_16293(class_2246.field_10062, class_2430::method_16877);
		this.method_16293(class_2246.field_10281, class_2430::method_16877);
		this.method_16293(class_2246.field_10602, class_2430::method_16877);
		this.method_16293(class_2246.field_10165, class_2430::method_16877);
		this.method_16293(class_2246.field_10185, class_2430::method_16877);
		this.method_16293(class_2246.field_10198, class_2430::method_16877);
		this.method_16293(class_2246.field_10452, class_2430::method_16877);
		this.method_16293(class_2246.field_9985, class_2430::method_16877);
		this.method_16293(class_2246.field_10229, class_2430::method_16877);
		this.method_16293(class_2246.field_10438, class_2430::method_16877);
		this.method_16293(class_2246.field_10045, class_2430::method_16877);
		this.method_16293(class_2246.field_10612, class_2430::method_16877);
		this.method_16293(class_2246.field_10368, class_2430::method_16877);
		this.method_16293(class_2246.field_10406, class_2430::method_16877);
		this.method_16293(class_2246.field_10154, class_2430::method_16877);
		this.method_16293(class_2246.field_10547, class_2430::method_16877);
		this.method_16293(
			class_2246.field_10432,
			arg -> class_52.method_324()
					.method_336(
						method_10392(
							arg,
							class_55.method_347()
								.method_352(class_44.method_289(1))
								.method_351(class_77.method_411(arg).method_438(class_3837.method_16848(class_3837.class_3840.field_17027).method_16856("Owner", "SkullOwner")))
						)
					)
		);
		this.method_16293(class_2246.field_10539, arg -> method_10390(arg, class_2246.field_10575, field_11339));
		this.method_16293(class_2246.field_10098, arg -> method_10390(arg, class_2246.field_10385, field_11339));
		this.method_16293(class_2246.field_10335, arg -> method_10390(arg, class_2246.field_10276, field_11338));
		this.method_16293(class_2246.field_9988, arg -> method_10390(arg, class_2246.field_10217, field_11339));
		this.method_16293(class_2246.field_10503, arg -> method_10378(arg, class_2246.field_10394, field_11339));
		this.method_16293(class_2246.field_10035, arg -> method_10378(arg, class_2246.field_10160, field_11339));
		class_209.class_210 lv = class_212.method_900(class_2246.field_10341).method_907(class_2242.field_9962, 3);
		this.method_16293(class_2246.field_10341, arg2 -> method_10391(arg2, class_1802.field_8186, class_1802.field_8309, lv));
		class_209.class_210 lv2 = class_212.method_900(class_2246.field_10293).method_907(class_2302.field_10835, 7);
		this.method_16293(class_2246.field_10293, arg2 -> method_10391(arg2, class_1802.field_8861, class_1802.field_8317, lv2));
		class_209.class_210 lv3 = class_212.method_900(class_2246.field_10609).method_907(class_2271.field_10835, 7);
		this.method_16293(
			class_2246.field_10609,
			arg2 -> method_10393(
					arg2,
					class_52.method_324()
						.method_336(class_55.method_347().method_351(class_77.method_411(class_1802.field_8179)))
						.method_336(
							class_55.method_347()
								.method_356(lv3)
								.method_351(class_77.method_411(class_1802.field_8179).method_438(class_94.method_463(class_1893.field_9130, 0.5714286F, 3)))
						)
				)
		);
		class_209.class_210 lv4 = class_212.method_900(class_2246.field_10247).method_907(class_2439.field_10835, 7);
		this.method_16293(
			class_2246.field_10247,
			arg2 -> method_10393(
					arg2,
					class_52.method_324()
						.method_336(class_55.method_347().method_351(class_77.method_411(class_1802.field_8567)))
						.method_336(
							class_55.method_347()
								.method_356(lv4)
								.method_351(class_77.method_411(class_1802.field_8567).method_438(class_94.method_463(class_1893.field_9130, 0.5714286F, 3)))
						)
						.method_336(class_55.method_347().method_356(lv4).method_351(class_77.method_411(class_1802.field_8635).method_421(class_219.method_932(0.02F))))
				)
		);
		this.method_16293(
			class_2246.field_16999,
			arg -> method_10393(
					arg,
					class_52.method_324()
						.method_336(
							class_55.method_347()
								.method_356(class_212.method_900(class_2246.field_16999).method_907(class_3830.field_17000, 3))
								.method_351(class_77.method_411(class_1802.field_16998))
								.method_353(class_141.method_621(class_61.method_377(2.0F, 3.0F)))
								.method_353(class_94.method_456(class_1893.field_9130))
						)
						.method_336(
							class_55.method_347()
								.method_356(class_212.method_900(class_2246.field_16999).method_907(class_3830.field_17000, 2))
								.method_351(class_77.method_411(class_1802.field_16998))
								.method_353(class_141.method_621(class_61.method_377(1.0F, 2.0F)))
								.method_353(class_94.method_456(class_1893.field_9130))
						)
				)
		);
		this.method_16293(class_2246.field_10580, arg -> method_10385(arg, class_2246.field_10251));
		this.method_16293(class_2246.field_10240, arg -> method_10385(arg, class_2246.field_10559));
		this.method_16293(class_2246.field_10418, arg -> method_10377(arg, class_1802.field_8713));
		this.method_16293(class_2246.field_10013, arg -> method_10377(arg, class_1802.field_8687));
		this.method_16293(class_2246.field_10213, arg -> method_10377(arg, class_1802.field_8155));
		this.method_16293(class_2246.field_10442, arg -> method_10377(arg, class_1802.field_8477));
		this.method_16293(
			class_2246.field_10090,
			arg -> method_10397(
					arg,
					(class_79.class_80<?>)method_10393(
						arg,
						class_77.method_411(class_1802.field_8759)
							.method_438(class_141.method_621(class_61.method_377(4.0F, 9.0F)))
							.method_438(class_94.method_455(class_1893.field_9130))
					)
				)
		);
		this.method_16293(class_2246.field_10343, arg -> method_10388(arg, (class_79.class_80<?>)method_10392(arg, class_77.method_411(class_1802.field_8276))));
		this.method_16293(
			class_2246.field_10428,
			arg -> method_10380(
					arg, (class_79.class_80<?>)method_10393(arg, class_77.method_411(class_1802.field_8600).method_438(class_141.method_621(class_61.method_377(0.0F, 2.0F))))
				)
		);
		this.method_16293(class_2246.field_10376, class_2430::method_10372);
		this.method_16293(class_2246.field_10597, class_2430::method_10372);
		this.method_16258(class_2246.field_10238, method_10372(class_2246.field_10376));
		this.method_16258(class_2246.field_10313, method_10372(class_2246.field_10112));
		this.method_16293(
			class_2246.field_10214,
			arg -> method_10380(
					class_2246.field_10479,
					((class_85.class_86)((class_85.class_86)method_10392(arg, class_77.method_411(class_1802.field_8317)))
							.method_421(class_212.method_900(arg).method_907(class_2320.field_10929, class_2756.field_12607)))
						.method_421(class_219.method_932(0.125F))
				)
		);
		this.method_16293(class_2246.field_10168, arg -> method_10387(arg, class_1802.field_8188));
		this.method_16293(class_2246.field_9984, arg -> method_10387(arg, class_1802.field_8706));
		this.method_16293(
			class_2246.field_10528,
			arg -> class_52.method_324()
					.method_336(
						class_55.method_347()
							.method_352(class_44.method_289(1))
							.method_351(((class_85.class_86)method_10392(arg, class_77.method_411(arg))).method_421(class_215.method_15972(class_47.class_50.field_935)))
					)
		);
		this.method_16293(class_2246.field_10112, class_2430::method_10371);
		this.method_16293(class_2246.field_10479, class_2430::method_10371);
		this.method_16293(
			class_2246.field_10171,
			arg -> method_10397(
					arg,
					(class_79.class_80<?>)method_10393(
						arg,
						class_77.method_411(class_1802.field_8601)
							.method_438(class_141.method_621(class_61.method_377(2.0F, 4.0F)))
							.method_438(class_94.method_456(class_1893.field_9130))
							.method_438(class_114.method_506(class_42.method_282(1, 4)))
					)
				)
		);
		this.method_16293(
			class_2246.field_10545,
			arg -> method_10397(
					arg,
					(class_79.class_80<?>)method_10393(
						arg,
						class_77.method_411(class_1802.field_8497)
							.method_438(class_141.method_621(class_61.method_377(3.0F, 7.0F)))
							.method_438(class_94.method_456(class_1893.field_9130))
							.method_438(class_114.method_506(class_42.method_277(9)))
					)
				)
		);
		this.method_16293(
			class_2246.field_10080,
			arg -> method_10397(
					arg,
					(class_79.class_80<?>)method_10393(
						arg,
						class_77.method_411(class_1802.field_8725)
							.method_438(class_141.method_621(class_61.method_377(4.0F, 5.0F)))
							.method_438(class_94.method_456(class_1893.field_9130))
					)
				)
		);
		this.method_16293(
			class_2246.field_10174,
			arg -> method_10397(
					arg,
					(class_79.class_80<?>)method_10393(
						arg,
						class_77.method_411(class_1802.field_8434)
							.method_438(class_141.method_621(class_61.method_377(2.0F, 3.0F)))
							.method_438(class_94.method_456(class_1893.field_9130))
							.method_438(class_114.method_506(class_42.method_282(1, 5)))
					)
				)
		);
		this.method_16293(
			class_2246.field_9974,
			arg -> class_52.method_324()
					.method_336(
						method_10393(
							arg,
							class_55.method_347()
								.method_352(class_44.method_289(1))
								.method_351(
									class_77.method_411(class_1802.field_8790)
										.method_438(class_141.method_621(class_61.method_377(2.0F, 4.0F)).method_524(class_212.method_900(arg).method_907(class_2421.field_11306, 3)))
										.method_438(class_94.method_456(class_1893.field_9130).method_524(class_212.method_900(arg).method_907(class_2421.field_11306, 3)))
								)
						)
					)
		);
		this.method_16293(
			class_2246.field_10477,
			arg -> class_52.method_324()
					.method_336(
						class_55.method_347()
							.method_356(class_215.method_15972(class_47.class_50.field_935))
							.method_351(
								class_65.method_386(
									class_65.method_386(
											class_77.method_411(class_1802.field_8543).method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 1)),
											class_77.method_411(class_1802.field_8543)
												.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 2))
												.method_438(class_141.method_621(class_44.method_289(2))),
											class_77.method_411(class_1802.field_8543)
												.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 3))
												.method_438(class_141.method_621(class_44.method_289(3))),
											class_77.method_411(class_1802.field_8543)
												.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 4))
												.method_438(class_141.method_621(class_44.method_289(4))),
											class_77.method_411(class_1802.field_8543)
												.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 5))
												.method_438(class_141.method_621(class_44.method_289(5))),
											class_77.method_411(class_1802.field_8543)
												.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 6))
												.method_438(class_141.method_621(class_44.method_289(6))),
											class_77.method_411(class_1802.field_8543)
												.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 7))
												.method_438(class_141.method_621(class_44.method_289(7))),
											class_77.method_411(class_1802.field_8543).method_438(class_141.method_621(class_44.method_289(8)))
										)
										.method_421(field_11337),
									class_65.method_386(
										class_77.method_411(arg).method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 1)),
										class_77.method_411(arg)
											.method_438(class_141.method_621(class_44.method_289(2)))
											.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 2)),
										class_77.method_411(arg)
											.method_438(class_141.method_621(class_44.method_289(3)))
											.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 3)),
										class_77.method_411(arg)
											.method_438(class_141.method_621(class_44.method_289(4)))
											.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 4)),
										class_77.method_411(arg)
											.method_438(class_141.method_621(class_44.method_289(5)))
											.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 5)),
										class_77.method_411(arg)
											.method_438(class_141.method_621(class_44.method_289(6)))
											.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 6)),
										class_77.method_411(arg)
											.method_438(class_141.method_621(class_44.method_289(7)))
											.method_421(class_212.method_900(arg).method_907(class_2488.field_11518, 7)),
										class_77.method_411(class_2246.field_10491)
									)
								)
							)
					)
		);
		this.method_16293(
			class_2246.field_10255,
			arg -> method_10397(
					arg,
					method_10392(
						arg,
						class_77.method_411(class_1802.field_8145)
							.method_421(class_182.method_800(class_1893.field_9130, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.method_417(class_77.method_411(arg))
					)
				)
		);
		this.method_16293(
			class_2246.field_17350,
			arg -> method_10397(
					arg, (class_79.class_80<?>)method_10392(arg, class_77.method_411(class_1802.field_8665).method_438(class_141.method_621(class_44.method_289(2))))
				)
		);
		this.method_16262(class_2246.field_10033);
		this.method_16262(class_2246.field_10087);
		this.method_16262(class_2246.field_10227);
		this.method_16262(class_2246.field_10574);
		this.method_16262(class_2246.field_10271);
		this.method_16262(class_2246.field_10049);
		this.method_16262(class_2246.field_10157);
		this.method_16262(class_2246.field_10317);
		this.method_16262(class_2246.field_10555);
		this.method_16262(class_2246.field_9996);
		this.method_16262(class_2246.field_10248);
		this.method_16262(class_2246.field_10399);
		this.method_16262(class_2246.field_10060);
		this.method_16262(class_2246.field_10073);
		this.method_16262(class_2246.field_10357);
		this.method_16262(class_2246.field_10272);
		this.method_16262(class_2246.field_9997);
		this.method_16262(class_2246.field_10285);
		this.method_16262(class_2246.field_9991);
		this.method_16262(class_2246.field_10496);
		this.method_16262(class_2246.field_10469);
		this.method_16262(class_2246.field_10193);
		this.method_16262(class_2246.field_10578);
		this.method_16262(class_2246.field_10305);
		this.method_16262(class_2246.field_10565);
		this.method_16262(class_2246.field_10077);
		this.method_16262(class_2246.field_10129);
		this.method_16262(class_2246.field_10355);
		this.method_16262(class_2246.field_10152);
		this.method_16262(class_2246.field_9982);
		this.method_16262(class_2246.field_10163);
		this.method_16262(class_2246.field_10419);
		this.method_16262(class_2246.field_10118);
		this.method_16262(class_2246.field_10070);
		this.method_16262(class_2246.field_10295);
		this.method_16262(class_2246.field_10225);
		this.method_16262(class_2246.field_10384);
		this.method_16262(class_2246.field_10195);
		this.method_16262(class_2246.field_10556);
		this.method_16262(class_2246.field_10082);
		this.method_16262(class_2246.field_10572);
		this.method_16262(class_2246.field_10296);
		this.method_16262(class_2246.field_10579);
		this.method_16262(class_2246.field_10032);
		this.method_16262(class_2246.field_10125);
		this.method_16262(class_2246.field_10339);
		this.method_16262(class_2246.field_10134);
		this.method_16262(class_2246.field_10618);
		this.method_16262(class_2246.field_10169);
		this.method_16262(class_2246.field_10448);
		this.method_16262(class_2246.field_10097);
		this.method_16262(class_2246.field_10047);
		this.method_16262(class_2246.field_10568);
		this.method_16262(class_2246.field_10221);
		this.method_16262(class_2246.field_10053);
		this.method_16262(class_2246.field_10079);
		this.method_16262(class_2246.field_10427);
		this.method_16262(class_2246.field_10551);
		this.method_16262(class_2246.field_10005);
		this.method_16238(class_2246.field_10277, class_2246.field_10340);
		this.method_16238(class_2246.field_10492, class_2246.field_10445);
		this.method_16238(class_2246.field_10387, class_2246.field_10056);
		this.method_16238(class_2246.field_10480, class_2246.field_10065);
		this.method_16238(class_2246.field_10100, class_2246.field_10416);
		this.method_16238(class_2246.field_10176, class_2246.field_10552);
		this.method_16258(class_2246.field_10183, method_10395());
		this.method_16258(class_2246.field_10331, method_10395());
		this.method_16258(class_2246.field_10150, method_10395());
		this.method_16258(class_2246.field_10110, method_10395());
		this.method_16258(class_2246.field_10260, method_10395());
		Set<class_2960> set = Sets.<class_2960>newHashSet();

		for (class_2248 lv5 : class_2378.field_11146) {
			class_2960 lv6 = lv5.method_9580();
			if (lv6 != class_39.field_844 && set.add(lv6)) {
				class_52.class_53 lv7 = (class_52.class_53)this.field_16493.remove(lv6);
				if (lv7 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", lv6, class_2378.field_11146.method_10221(lv5)));
				}

				biConsumer.accept(lv6, lv7);
			}
		}

		if (!this.field_16493.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_16493.keySet());
		}
	}

	public void method_16285(class_2248 arg) {
		this.method_16293(arg, argx -> method_10389(((class_2362)argx).method_16231()));
	}

	public void method_16238(class_2248 arg, class_2248 arg2) {
		this.method_16258(arg, method_10373(arg2));
	}

	public void method_16256(class_2248 arg, class_1935 arg2) {
		this.method_16258(arg, method_10394(arg2));
	}

	public void method_16262(class_2248 arg) {
		this.method_16238(arg, arg);
	}

	public void method_16329(class_2248 arg) {
		this.method_16256(arg, arg);
	}

	private void method_16293(class_2248 arg, Function<class_2248, class_52.class_53> function) {
		this.method_16258(arg, (class_52.class_53)function.apply(arg));
	}

	private void method_16258(class_2248 arg, class_52.class_53 arg2) {
		this.field_16493.put(arg.method_9580(), arg2);
	}
}
