package net.minecraft;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1299<T extends class_1297> {
	private static final Logger field_6088 = LogManager.getLogger();
	public static final class_1299<class_1295> field_6083 = method_5895("area_effect_cloud", class_1299.class_1300.method_5903(class_1295.class, class_1295::new));
	public static final class_1299<class_1531> field_6131 = method_5895("armor_stand", class_1299.class_1300.method_5903(class_1531.class, class_1531::new));
	public static final class_1299<class_1667> field_6122 = method_5895("arrow", class_1299.class_1300.method_5903(class_1667.class, class_1667::new));
	public static final class_1299<class_1420> field_6108 = method_5895("bat", class_1299.class_1300.method_5903(class_1420.class, class_1420::new));
	public static final class_1299<class_1545> field_6099 = method_5895("blaze", class_1299.class_1300.method_5903(class_1545.class, class_1545::new));
	public static final class_1299<class_1690> field_6121 = method_5895("boat", class_1299.class_1300.method_5903(class_1690.class, class_1690::new));
	public static final class_1299<class_1451> field_16281 = method_5895("cat", class_1299.class_1300.method_5903(class_1451.class, class_1451::new));
	public static final class_1299<class_1549> field_6084 = method_5895("cave_spider", class_1299.class_1300.method_5903(class_1549.class, class_1549::new));
	public static final class_1299<class_1428> field_6132 = method_5895("chicken", class_1299.class_1300.method_5903(class_1428.class, class_1428::new));
	public static final class_1299<class_1431> field_6070 = method_5895("cod", class_1299.class_1300.method_5903(class_1431.class, class_1431::new));
	public static final class_1299<class_1430> field_6085 = method_5895("cow", class_1299.class_1300.method_5903(class_1430.class, class_1430::new));
	public static final class_1299<class_1548> field_6046 = method_5895("creeper", class_1299.class_1300.method_5903(class_1548.class, class_1548::new));
	public static final class_1299<class_1495> field_6067 = method_5895("donkey", class_1299.class_1300.method_5903(class_1495.class, class_1495::new));
	public static final class_1299<class_1433> field_6087 = method_5895("dolphin", class_1299.class_1300.method_5903(class_1433.class, class_1433::new));
	public static final class_1299<class_1670> field_6129 = method_5895("dragon_fireball", class_1299.class_1300.method_5903(class_1670.class, class_1670::new));
	public static final class_1299<class_1551> field_6123 = method_5895("drowned", class_1299.class_1300.method_5903(class_1551.class, class_1551::new));
	public static final class_1299<class_1550> field_6086 = method_5895("elder_guardian", class_1299.class_1300.method_5903(class_1550.class, class_1550::new));
	public static final class_1299<class_1511> field_6110 = method_5895("end_crystal", class_1299.class_1300.method_5903(class_1511.class, class_1511::new));
	public static final class_1299<class_1510> field_6116 = method_5895("ender_dragon", class_1299.class_1300.method_5903(class_1510.class, class_1510::new));
	public static final class_1299<class_1560> field_6091 = method_5895("enderman", class_1299.class_1300.method_5903(class_1560.class, class_1560::new));
	public static final class_1299<class_1559> field_6128 = method_5895("endermite", class_1299.class_1300.method_5903(class_1559.class, class_1559::new));
	public static final class_1299<class_1669> field_6060 = method_5895("evoker_fangs", class_1299.class_1300.method_5903(class_1669.class, class_1669::new));
	public static final class_1299<class_1564> field_6090 = method_5895("evoker", class_1299.class_1300.method_5903(class_1564.class, class_1564::new));
	public static final class_1299<class_1303> field_6044 = method_5895("experience_orb", class_1299.class_1300.method_5903(class_1303.class, class_1303::new));
	public static final class_1299<class_1672> field_6061 = method_5895("eye_of_ender", class_1299.class_1300.method_5903(class_1672.class, class_1672::new));
	public static final class_1299<class_1540> field_6089 = method_5895("falling_block", class_1299.class_1300.method_5903(class_1540.class, class_1540::new));
	public static final class_1299<class_1671> field_6133 = method_5895("firework_rocket", class_1299.class_1300.method_5903(class_1671.class, class_1671::new));
	public static final class_1299<class_1571> field_6107 = method_5895("ghast", class_1299.class_1300.method_5903(class_1571.class, class_1571::new));
	public static final class_1299<class_1570> field_6095 = method_5895("giant", class_1299.class_1300.method_5903(class_1570.class, class_1570::new));
	public static final class_1299<class_1577> field_6118 = method_5895("guardian", class_1299.class_1300.method_5903(class_1577.class, class_1577::new));
	public static final class_1299<class_1498> field_6139 = method_5895("horse", class_1299.class_1300.method_5903(class_1498.class, class_1498::new));
	public static final class_1299<class_1576> field_6071 = method_5895("husk", class_1299.class_1300.method_5903(class_1576.class, class_1576::new));
	public static final class_1299<class_1581> field_6065 = method_5895("illusioner", class_1299.class_1300.method_5903(class_1581.class, class_1581::new));
	public static final class_1299<class_1542> field_6052 = method_5895("item", class_1299.class_1300.method_5903(class_1542.class, class_1542::new));
	public static final class_1299<class_1533> field_6043 = method_5895("item_frame", class_1299.class_1300.method_5903(class_1533.class, class_1533::new));
	public static final class_1299<class_1674> field_6066 = method_5895("fireball", class_1299.class_1300.method_5903(class_1674.class, class_1674::new));
	public static final class_1299<class_1532> field_6138 = method_5895(
		"leash_knot", class_1299.class_1300.<class_1532>method_5903(class_1532.class, class_1532::new).method_5904()
	);
	public static final class_1299<class_1501> field_6074 = method_5895("llama", class_1299.class_1300.method_5903(class_1501.class, class_1501::new));
	public static final class_1299<class_1673> field_6124 = method_5895("llama_spit", class_1299.class_1300.method_5903(class_1673.class, class_1673::new));
	public static final class_1299<class_1589> field_6102 = method_5895("magma_cube", class_1299.class_1300.method_5903(class_1589.class, class_1589::new));
	public static final class_1299<class_1695> field_6096 = method_5895("minecart", class_1299.class_1300.method_5903(class_1695.class, class_1695::new));
	public static final class_1299<class_1694> field_6126 = method_5895("chest_minecart", class_1299.class_1300.method_5903(class_1694.class, class_1694::new));
	public static final class_1299<class_1697> field_6136 = method_5895(
		"command_block_minecart", class_1299.class_1300.method_5903(class_1697.class, class_1697::new)
	);
	public static final class_1299<class_1696> field_6080 = method_5895("furnace_minecart", class_1299.class_1300.method_5903(class_1696.class, class_1696::new));
	public static final class_1299<class_1700> field_6058 = method_5895("hopper_minecart", class_1299.class_1300.method_5903(class_1700.class, class_1700::new));
	public static final class_1299<class_1699> field_6142 = method_5895("spawner_minecart", class_1299.class_1300.method_5903(class_1699.class, class_1699::new));
	public static final class_1299<class_1701> field_6053 = method_5895("tnt_minecart", class_1299.class_1300.method_5903(class_1701.class, class_1701::new));
	public static final class_1299<class_1500> field_6057 = method_5895("mule", class_1299.class_1300.method_5903(class_1500.class, class_1500::new));
	public static final class_1299<class_1438> field_6143 = method_5895("mooshroom", class_1299.class_1300.method_5903(class_1438.class, class_1438::new));
	public static final class_1299<class_3701> field_6081 = method_5895("ocelot", class_1299.class_1300.method_5903(class_3701.class, class_3701::new));
	public static final class_1299<class_1534> field_6120 = method_5895("painting", class_1299.class_1300.method_5903(class_1534.class, class_1534::new));
	public static final class_1299<class_1440> field_6146 = method_5895("panda", class_1299.class_1300.method_5903(class_1440.class, class_1440::new));
	public static final class_1299<class_1453> field_6104 = method_5895("parrot", class_1299.class_1300.method_5903(class_1453.class, class_1453::new));
	public static final class_1299<class_1452> field_6093 = method_5895("pig", class_1299.class_1300.method_5903(class_1452.class, class_1452::new));
	public static final class_1299<class_1454> field_6062 = method_5895("pufferfish", class_1299.class_1300.method_5903(class_1454.class, class_1454::new));
	public static final class_1299<class_1590> field_6050 = method_5895("zombie_pigman", class_1299.class_1300.method_5903(class_1590.class, class_1590::new));
	public static final class_1299<class_1456> field_6042 = method_5895("polar_bear", class_1299.class_1300.method_5903(class_1456.class, class_1456::new));
	public static final class_1299<class_1541> field_6063 = method_5895("tnt", class_1299.class_1300.method_5903(class_1541.class, class_1541::new));
	public static final class_1299<class_1463> field_6140 = method_5895("rabbit", class_1299.class_1300.method_5903(class_1463.class, class_1463::new));
	public static final class_1299<class_1462> field_6073 = method_5895("salmon", class_1299.class_1300.method_5903(class_1462.class, class_1462::new));
	public static final class_1299<class_1472> field_6115 = method_5895("sheep", class_1299.class_1300.method_5903(class_1472.class, class_1472::new));
	public static final class_1299<class_1606> field_6109 = method_5895("shulker", class_1299.class_1300.method_5903(class_1606.class, class_1606::new));
	public static final class_1299<class_1678> field_6100 = method_5895("shulker_bullet", class_1299.class_1300.method_5903(class_1678.class, class_1678::new));
	public static final class_1299<class_1614> field_6125 = method_5895("silverfish", class_1299.class_1300.method_5903(class_1614.class, class_1614::new));
	public static final class_1299<class_1613> field_6137 = method_5895("skeleton", class_1299.class_1300.method_5903(class_1613.class, class_1613::new));
	public static final class_1299<class_1506> field_6075 = method_5895("skeleton_horse", class_1299.class_1300.method_5903(class_1506.class, class_1506::new));
	public static final class_1299<class_1621> field_6069 = method_5895("slime", class_1299.class_1300.method_5903(class_1621.class, class_1621::new));
	public static final class_1299<class_1677> field_6049 = method_5895("small_fireball", class_1299.class_1300.method_5903(class_1677.class, class_1677::new));
	public static final class_1299<class_1473> field_6047 = method_5895("snow_golem", class_1299.class_1300.method_5903(class_1473.class, class_1473::new));
	public static final class_1299<class_1680> field_6068 = method_5895("snowball", class_1299.class_1300.method_5903(class_1680.class, class_1680::new));
	public static final class_1299<class_1679> field_6135 = method_5895("spectral_arrow", class_1299.class_1300.method_5903(class_1679.class, class_1679::new));
	public static final class_1299<class_1628> field_6079 = method_5895("spider", class_1299.class_1300.method_5903(class_1628.class, class_1628::new));
	public static final class_1299<class_1477> field_6114 = method_5895("squid", class_1299.class_1300.method_5903(class_1477.class, class_1477::new));
	public static final class_1299<class_1627> field_6098 = method_5895("stray", class_1299.class_1300.method_5903(class_1627.class, class_1627::new));
	public static final class_1299<class_1474> field_6111 = method_5895("tropical_fish", class_1299.class_1300.method_5903(class_1474.class, class_1474::new));
	public static final class_1299<class_1481> field_6113 = method_5895("turtle", class_1299.class_1300.method_5903(class_1481.class, class_1481::new));
	public static final class_1299<class_1681> field_6144 = method_5895("egg", class_1299.class_1300.method_5903(class_1681.class, class_1681::new));
	public static final class_1299<class_1684> field_6082 = method_5895("ender_pearl", class_1299.class_1300.method_5903(class_1684.class, class_1684::new));
	public static final class_1299<class_1683> field_6064 = method_5895("experience_bottle", class_1299.class_1300.method_5903(class_1683.class, class_1683::new));
	public static final class_1299<class_1686> field_6045 = method_5895("potion", class_1299.class_1300.method_5903(class_1686.class, class_1686::new));
	public static final class_1299<class_1634> field_6059 = method_5895("vex", class_1299.class_1300.method_5903(class_1634.class, class_1634::new));
	public static final class_1299<class_1646> field_6077 = method_5895("villager", class_1299.class_1300.method_5903(class_1646.class, class_1646::new));
	public static final class_1299<class_1439> field_6147 = method_5895("iron_golem", class_1299.class_1300.method_5903(class_1439.class, class_1439::new));
	public static final class_1299<class_1632> field_6117 = method_5895("vindicator", class_1299.class_1300.method_5903(class_1632.class, class_1632::new));
	public static final class_1299<class_1604> field_6105 = method_5895("pillager", class_1299.class_1300.method_5903(class_1604.class, class_1604::new));
	public static final class_1299<class_1640> field_6145 = method_5895("witch", class_1299.class_1300.method_5903(class_1640.class, class_1640::new));
	public static final class_1299<class_1528> field_6119 = method_5895("wither", class_1299.class_1300.method_5903(class_1528.class, class_1528::new));
	public static final class_1299<class_1639> field_6076 = method_5895("wither_skeleton", class_1299.class_1300.method_5903(class_1639.class, class_1639::new));
	public static final class_1299<class_1687> field_6130 = method_5895("wither_skull", class_1299.class_1300.method_5903(class_1687.class, class_1687::new));
	public static final class_1299<class_1493> field_6055 = method_5895("wolf", class_1299.class_1300.method_5903(class_1493.class, class_1493::new));
	public static final class_1299<class_1642> field_6051 = method_5895("zombie", class_1299.class_1300.method_5903(class_1642.class, class_1642::new));
	public static final class_1299<class_1507> field_6048 = method_5895("zombie_horse", class_1299.class_1300.method_5903(class_1507.class, class_1507::new));
	public static final class_1299<class_1641> field_6054 = method_5895("zombie_villager", class_1299.class_1300.method_5903(class_1641.class, class_1641::new));
	public static final class_1299<class_1593> field_6078 = method_5895("phantom", class_1299.class_1300.method_5903(class_1593.class, class_1593::new));
	public static final class_1299<class_1584> field_6134 = method_5895("illager_beast", class_1299.class_1300.method_5903(class_1584.class, class_1584::new));
	public static final class_1299<class_1538> field_6112 = method_5895(
		"lightning_bolt", class_1299.class_1300.<class_1538>method_5902(class_1538.class).method_5904()
	);
	public static final class_1299<class_1657> field_6097 = method_5895(
		"player", class_1299.class_1300.<class_1657>method_5902(class_1657.class).method_5904().method_5901()
	);
	public static final class_1299<class_1536> field_6103 = method_5895(
		"fishing_bobber", class_1299.class_1300.<class_1536>method_5902(class_1536.class).method_5904().method_5901()
	);
	public static final class_1299<class_1685> field_6127 = method_5895("trident", class_1299.class_1300.method_5903(class_1685.class, class_1685::new));
	private final Class<? extends T> field_6094;
	private final Function<? super class_1937, ? extends T> field_6101;
	private final boolean field_6056;
	private final boolean field_6072;
	@Nullable
	private String field_6106;
	@Nullable
	private class_2561 field_6092;
	@Nullable
	private class_2960 field_16526;
	@Nullable
	private final Type<?> field_6141;

	private static <T extends class_1297> class_1299<T> method_5895(String string, class_1299.class_1300<T> arg) {
		return class_2378.method_10226(class_2378.field_11145, string, arg.method_5905(string));
	}

	@Nullable
	public static class_2960 method_5890(class_1299<?> arg) {
		return class_2378.field_11145.method_10221(arg);
	}

	@Nullable
	public static class_1299<?> method_5898(String string) {
		return class_2378.field_11145.method_10223(class_2960.method_12829(string));
	}

	public class_1299(Class<? extends T> class_, Function<? super class_1937, ? extends T> function, boolean bl, boolean bl2, @Nullable Type<?> type) {
		this.field_6094 = class_;
		this.field_6101 = function;
		this.field_6056 = bl;
		this.field_6072 = bl2;
		this.field_6141 = type;
	}

	@Nullable
	public class_1297 method_5894(class_1937 arg, @Nullable class_1799 arg2, @Nullable class_1657 arg3, class_2338 arg4, class_3730 arg5, boolean bl, boolean bl2) {
		return this.method_5899(
			arg, arg2 == null ? null : arg2.method_7969(), arg2 != null && arg2.method_7938() ? arg2.method_7964() : null, arg3, arg4, arg5, bl, bl2
		);
	}

	@Nullable
	public T method_5899(
		class_1937 arg, @Nullable class_2487 arg2, @Nullable class_2561 arg3, @Nullable class_1657 arg4, class_2338 arg5, class_3730 arg6, boolean bl, boolean bl2
	) {
		T lv = this.method_5888(arg, arg2, arg3, arg4, arg5, arg6, bl, bl2);
		arg.method_8649(lv);
		return lv;
	}

	@Nullable
	public T method_5888(
		class_1937 arg, @Nullable class_2487 arg2, @Nullable class_2561 arg3, @Nullable class_1657 arg4, class_2338 arg5, class_3730 arg6, boolean bl, boolean bl2
	) {
		T lv = this.method_5883(arg);
		if (lv == null) {
			return null;
		} else {
			double d;
			if (bl) {
				lv.method_5814((double)arg5.method_10263() + 0.5, (double)(arg5.method_10264() + 1), (double)arg5.method_10260() + 0.5);
				d = method_5884(arg, arg5, bl2, lv.method_5829());
			} else {
				d = 0.0;
			}

			lv.method_5808(
				(double)arg5.method_10263() + 0.5,
				(double)arg5.method_10264() + d,
				(double)arg5.method_10260() + 0.5,
				class_3532.method_15393(arg.field_9229.nextFloat() * 360.0F),
				0.0F
			);
			if (lv instanceof class_1308) {
				class_1308 lv2 = (class_1308)lv;
				lv2.field_6241 = lv2.field_6031;
				lv2.field_6283 = lv2.field_6031;
				lv2.method_5943(arg, arg.method_8404(new class_2338(lv2)), arg6, null, arg2);
				lv2.method_5966();
			}

			if (arg3 != null && lv instanceof class_1309) {
				lv.method_5665(arg3);
			}

			method_5881(arg, arg4, lv, arg2);
			return lv;
		}
	}

	protected static double method_5884(class_1941 arg, class_2338 arg2, boolean bl, class_238 arg3) {
		class_238 lv = new class_238(arg2);
		if (bl) {
			lv = lv.method_1012(0.0, -1.0, 0.0);
		}

		Stream<class_265> stream = arg.method_8607(null, lv);
		return 1.0 + class_259.method_1085(class_2350.class_2351.field_11052, arg3, stream, bl ? -2.0 : -1.0);
	}

	public static void method_5881(class_1937 arg, @Nullable class_1657 arg2, @Nullable class_1297 arg3, @Nullable class_2487 arg4) {
		if (arg4 != null && arg4.method_10573("EntityTag", 10)) {
			MinecraftServer minecraftServer = arg.method_8503();
			if (minecraftServer != null && arg3 != null) {
				if (arg.field_9236 || !arg3.method_5833() || arg2 != null && minecraftServer.method_3760().method_14569(arg2.method_7334())) {
					class_2487 lv = arg3.method_5647(new class_2487());
					UUID uUID = arg3.method_5667();
					lv.method_10543(arg4.method_10562("EntityTag"));
					arg3.method_5826(uUID);
					arg3.method_5651(lv);
				}
			}
		}
	}

	public boolean method_5893() {
		return this.field_6056;
	}

	public boolean method_5896() {
		return this.field_6072;
	}

	public Class<? extends T> method_5891() {
		return this.field_6094;
	}

	public String method_5882() {
		if (this.field_6106 == null) {
			this.field_6106 = class_156.method_646("entity", class_2378.field_11145.method_10221(this));
		}

		return this.field_6106;
	}

	public class_2561 method_5897() {
		if (this.field_6092 == null) {
			this.field_6092 = new class_2588(this.method_5882());
		}

		return this.field_6092;
	}

	public class_2960 method_16351() {
		if (this.field_16526 == null) {
			class_2960 lv = class_2378.field_11145.method_10221(this);
			this.field_16526 = new class_2960(lv.method_12836(), "entities/" + lv.method_12832());
		}

		return this.field_16526;
	}

	@Nullable
	public T method_5883(class_1937 arg) {
		return (T)this.field_6101.apply(arg);
	}

	@Nullable
	public static class_1297 method_5887(class_1937 arg, class_2960 arg2) {
		return method_5886(arg, class_2378.field_11145.method_10223(arg2));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1297 method_5889(int i, class_1937 arg) {
		return method_5886(arg, class_2378.field_11145.method_10200(i));
	}

	@Nullable
	public static class_1297 method_5892(class_2487 arg, class_1937 arg2) {
		class_2960 lv = new class_2960(arg.method_10558("id"));
		class_1297 lv2 = method_5887(arg2, lv);
		if (lv2 == null) {
			field_6088.warn("Skipping Entity with id {}", lv);
		} else {
			lv2.method_5651(arg);
		}

		return lv2;
	}

	@Nullable
	private static class_1297 method_5886(class_1937 arg, @Nullable class_1299<?> arg2) {
		return arg2 == null ? null : arg2.method_5883(arg);
	}

	public static class class_1300<T extends class_1297> {
		private final Class<? extends T> field_6149;
		private final Function<? super class_1937, ? extends T> field_6148;
		private boolean field_6151 = true;
		private boolean field_6150 = true;

		private class_1300(Class<? extends T> class_, Function<? super class_1937, ? extends T> function) {
			this.field_6149 = class_;
			this.field_6148 = function;
		}

		public static <T extends class_1297> class_1299.class_1300<T> method_5903(Class<? extends T> class_, Function<? super class_1937, ? extends T> function) {
			return new class_1299.class_1300<>(class_, function);
		}

		public static <T extends class_1297> class_1299.class_1300<T> method_5902(Class<? extends T> class_) {
			return new class_1299.class_1300<>(class_, arg -> null);
		}

		public class_1299.class_1300<T> method_5901() {
			this.field_6150 = false;
			return this;
		}

		public class_1299.class_1300<T> method_5904() {
			this.field_6151 = false;
			return this;
		}

		public class_1299<T> method_5905(String string) {
			Type<?> type = null;
			if (this.field_6151) {
				try {
					type = class_3551.method_15450().getSchema(DataFixUtils.makeKey(class_155.method_16673().getWorldVersion())).getChoiceType(class_1208.field_5723, string);
				} catch (IllegalStateException var4) {
					if (class_155.field_1125) {
						throw var4;
					}

					class_1299.field_6088.warn("No data fixer registered for entity {}", string);
				}
			}

			return new class_1299<>(this.field_6149, this.field_6148, this.field_6151, this.field_6150, type);
		}
	}
}
