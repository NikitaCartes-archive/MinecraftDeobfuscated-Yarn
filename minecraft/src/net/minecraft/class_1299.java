package net.minecraft;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Collections;
import java.util.Optional;
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
	public static final class_1299<class_1295> field_6083 = method_5895(
		"area_effect_cloud", class_1299.class_1300.<class_1295>method_5903(class_1295::new, class_1311.field_17715).method_19947().method_17687(6.0F, 0.5F)
	);
	public static final class_1299<class_1531> field_6131 = method_5895(
		"armor_stand", class_1299.class_1300.<class_1531>method_5903(class_1531::new, class_1311.field_17715).method_17687(0.5F, 1.975F)
	);
	public static final class_1299<class_1667> field_6122 = method_5895(
		"arrow", class_1299.class_1300.<class_1667>method_5903(class_1667::new, class_1311.field_17715).method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1420> field_6108 = method_5895(
		"bat", class_1299.class_1300.method_5903(class_1420::new, class_1311.field_6303).method_17687(0.5F, 0.9F)
	);
	public static final class_1299<class_1545> field_6099 = method_5895(
		"blaze", class_1299.class_1300.method_5903(class_1545::new, class_1311.field_6302).method_19947().method_17687(0.6F, 1.8F)
	);
	public static final class_1299<class_1690> field_6121 = method_5895(
		"boat", class_1299.class_1300.<class_1690>method_5903(class_1690::new, class_1311.field_17715).method_17687(1.375F, 0.5625F)
	);
	public static final class_1299<class_1451> field_16281 = method_5895(
		"cat", class_1299.class_1300.method_5903(class_1451::new, class_1311.field_6294).method_17687(0.6F, 0.7F)
	);
	public static final class_1299<class_1549> field_6084 = method_5895(
		"cave_spider", class_1299.class_1300.method_5903(class_1549::new, class_1311.field_6302).method_17687(0.7F, 0.5F)
	);
	public static final class_1299<class_1428> field_6132 = method_5895(
		"chicken", class_1299.class_1300.method_5903(class_1428::new, class_1311.field_6294).method_17687(0.4F, 0.7F)
	);
	public static final class_1299<class_1431> field_6070 = method_5895(
		"cod", class_1299.class_1300.method_5903(class_1431::new, class_1311.field_6300).method_17687(0.5F, 0.3F)
	);
	public static final class_1299<class_1430> field_6085 = method_5895(
		"cow", class_1299.class_1300.method_5903(class_1430::new, class_1311.field_6294).method_17687(0.9F, 1.4F)
	);
	public static final class_1299<class_1548> field_6046 = method_5895(
		"creeper", class_1299.class_1300.method_5903(class_1548::new, class_1311.field_6302).method_17687(0.6F, 1.7F)
	);
	public static final class_1299<class_1495> field_6067 = method_5895(
		"donkey", class_1299.class_1300.method_5903(class_1495::new, class_1311.field_6294).method_17687(1.3964844F, 1.5F)
	);
	public static final class_1299<class_1433> field_6087 = method_5895(
		"dolphin", class_1299.class_1300.method_5903(class_1433::new, class_1311.field_6300).method_17687(0.9F, 0.6F)
	);
	public static final class_1299<class_1670> field_6129 = method_5895(
		"dragon_fireball", class_1299.class_1300.<class_1670>method_5903(class_1670::new, class_1311.field_17715).method_17687(1.0F, 1.0F)
	);
	public static final class_1299<class_1551> field_6123 = method_5895(
		"drowned", class_1299.class_1300.method_5903(class_1551::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1550> field_6086 = method_5895(
		"elder_guardian", class_1299.class_1300.method_5903(class_1550::new, class_1311.field_6302).method_17687(1.9975F, 1.9975F)
	);
	public static final class_1299<class_1511> field_6110 = method_5895(
		"end_crystal", class_1299.class_1300.<class_1511>method_5903(class_1511::new, class_1311.field_17715).method_17687(2.0F, 2.0F)
	);
	public static final class_1299<class_1510> field_6116 = method_5895(
		"ender_dragon", class_1299.class_1300.method_5903(class_1510::new, class_1311.field_6302).method_19947().method_17687(16.0F, 8.0F)
	);
	public static final class_1299<class_1560> field_6091 = method_5895(
		"enderman", class_1299.class_1300.method_5903(class_1560::new, class_1311.field_6302).method_17687(0.6F, 2.9F)
	);
	public static final class_1299<class_1559> field_6128 = method_5895(
		"endermite", class_1299.class_1300.method_5903(class_1559::new, class_1311.field_6302).method_17687(0.4F, 0.3F)
	);
	public static final class_1299<class_1669> field_6060 = method_5895(
		"evoker_fangs", class_1299.class_1300.<class_1669>method_5903(class_1669::new, class_1311.field_17715).method_17687(0.5F, 0.8F)
	);
	public static final class_1299<class_1564> field_6090 = method_5895(
		"evoker", class_1299.class_1300.method_5903(class_1564::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1303> field_6044 = method_5895(
		"experience_orb", class_1299.class_1300.<class_1303>method_5903(class_1303::new, class_1311.field_17715).method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1672> field_6061 = method_5895(
		"eye_of_ender", class_1299.class_1300.<class_1672>method_5903(class_1672::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1540> field_6089 = method_5895(
		"falling_block", class_1299.class_1300.<class_1540>method_5903(class_1540::new, class_1311.field_17715).method_17687(0.98F, 0.98F)
	);
	public static final class_1299<class_1671> field_6133 = method_5895(
		"firework_rocket", class_1299.class_1300.<class_1671>method_5903(class_1671::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_4019> field_17943 = method_5895(
		"fox", class_1299.class_1300.method_5903(class_4019::new, class_1311.field_6294).method_17687(0.6F, 0.7F)
	);
	public static final class_1299<class_1571> field_6107 = method_5895(
		"ghast", class_1299.class_1300.method_5903(class_1571::new, class_1311.field_6302).method_19947().method_17687(4.0F, 4.0F)
	);
	public static final class_1299<class_1570> field_6095 = method_5895(
		"giant", class_1299.class_1300.method_5903(class_1570::new, class_1311.field_6302).method_17687(3.6F, 12.0F)
	);
	public static final class_1299<class_1577> field_6118 = method_5895(
		"guardian", class_1299.class_1300.method_5903(class_1577::new, class_1311.field_6302).method_17687(0.85F, 0.85F)
	);
	public static final class_1299<class_1498> field_6139 = method_5895(
		"horse", class_1299.class_1300.method_5903(class_1498::new, class_1311.field_6294).method_17687(1.3964844F, 1.6F)
	);
	public static final class_1299<class_1576> field_6071 = method_5895(
		"husk", class_1299.class_1300.method_5903(class_1576::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1581> field_6065 = method_5895(
		"illusioner", class_1299.class_1300.method_5903(class_1581::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1542> field_6052 = method_5895(
		"item", class_1299.class_1300.<class_1542>method_5903(class_1542::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1533> field_6043 = method_5895(
		"item_frame", class_1299.class_1300.<class_1533>method_5903(class_1533::new, class_1311.field_17715).method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1674> field_6066 = method_5895(
		"fireball", class_1299.class_1300.<class_1674>method_5903(class_1674::new, class_1311.field_17715).method_17687(1.0F, 1.0F)
	);
	public static final class_1299<class_1532> field_6138 = method_5895(
		"leash_knot", class_1299.class_1300.<class_1532>method_5903(class_1532::new, class_1311.field_17715).method_5904().method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1501> field_6074 = method_5895(
		"llama", class_1299.class_1300.method_5903(class_1501::new, class_1311.field_6294).method_17687(0.9F, 1.87F)
	);
	public static final class_1299<class_1673> field_6124 = method_5895(
		"llama_spit", class_1299.class_1300.<class_1673>method_5903(class_1673::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1589> field_6102 = method_5895(
		"magma_cube", class_1299.class_1300.method_5903(class_1589::new, class_1311.field_6302).method_19947().method_17687(2.04F, 2.04F)
	);
	public static final class_1299<class_1695> field_6096 = method_5895(
		"minecart", class_1299.class_1300.<class_1695>method_5903(class_1695::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1694> field_6126 = method_5895(
		"chest_minecart", class_1299.class_1300.<class_1694>method_5903(class_1694::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1697> field_6136 = method_5895(
		"command_block_minecart", class_1299.class_1300.<class_1697>method_5903(class_1697::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1696> field_6080 = method_5895(
		"furnace_minecart", class_1299.class_1300.<class_1696>method_5903(class_1696::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1700> field_6058 = method_5895(
		"hopper_minecart", class_1299.class_1300.<class_1700>method_5903(class_1700::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1699> field_6142 = method_5895(
		"spawner_minecart", class_1299.class_1300.<class_1699>method_5903(class_1699::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1701> field_6053 = method_5895(
		"tnt_minecart", class_1299.class_1300.<class_1701>method_5903(class_1701::new, class_1311.field_17715).method_17687(0.98F, 0.7F)
	);
	public static final class_1299<class_1500> field_6057 = method_5895(
		"mule", class_1299.class_1300.method_5903(class_1500::new, class_1311.field_6294).method_17687(1.3964844F, 1.6F)
	);
	public static final class_1299<class_1438> field_6143 = method_5895(
		"mooshroom", class_1299.class_1300.method_5903(class_1438::new, class_1311.field_6294).method_17687(0.9F, 1.4F)
	);
	public static final class_1299<class_3701> field_6081 = method_5895(
		"ocelot", class_1299.class_1300.method_5903(class_3701::new, class_1311.field_6294).method_17687(0.6F, 0.7F)
	);
	public static final class_1299<class_1534> field_6120 = method_5895(
		"painting", class_1299.class_1300.<class_1534>method_5903(class_1534::new, class_1311.field_17715).method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1440> field_6146 = method_5895(
		"panda", class_1299.class_1300.method_5903(class_1440::new, class_1311.field_6294).method_17687(1.3F, 1.25F)
	);
	public static final class_1299<class_1453> field_6104 = method_5895(
		"parrot", class_1299.class_1300.method_5903(class_1453::new, class_1311.field_6294).method_17687(0.5F, 0.9F)
	);
	public static final class_1299<class_1452> field_6093 = method_5895(
		"pig", class_1299.class_1300.method_5903(class_1452::new, class_1311.field_6294).method_17687(0.9F, 0.9F)
	);
	public static final class_1299<class_1454> field_6062 = method_5895(
		"pufferfish", class_1299.class_1300.method_5903(class_1454::new, class_1311.field_6300).method_17687(0.7F, 0.7F)
	);
	public static final class_1299<class_1590> field_6050 = method_5895(
		"zombie_pigman", class_1299.class_1300.method_5903(class_1590::new, class_1311.field_6302).method_19947().method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1456> field_6042 = method_5895(
		"polar_bear", class_1299.class_1300.method_5903(class_1456::new, class_1311.field_6294).method_17687(1.4F, 1.4F)
	);
	public static final class_1299<class_1541> field_6063 = method_5895(
		"tnt", class_1299.class_1300.<class_1541>method_5903(class_1541::new, class_1311.field_17715).method_19947().method_17687(0.98F, 0.98F)
	);
	public static final class_1299<class_1463> field_6140 = method_5895(
		"rabbit", class_1299.class_1300.method_5903(class_1463::new, class_1311.field_6294).method_17687(0.4F, 0.5F)
	);
	public static final class_1299<class_1462> field_6073 = method_5895(
		"salmon", class_1299.class_1300.method_5903(class_1462::new, class_1311.field_6300).method_17687(0.7F, 0.4F)
	);
	public static final class_1299<class_1472> field_6115 = method_5895(
		"sheep", class_1299.class_1300.method_5903(class_1472::new, class_1311.field_6294).method_17687(0.9F, 1.3F)
	);
	public static final class_1299<class_1606> field_6109 = method_5895(
		"shulker", class_1299.class_1300.method_5903(class_1606::new, class_1311.field_6302).method_19947().method_17687(1.0F, 1.0F)
	);
	public static final class_1299<class_1678> field_6100 = method_5895(
		"shulker_bullet", class_1299.class_1300.<class_1678>method_5903(class_1678::new, class_1311.field_17715).method_17687(0.3125F, 0.3125F)
	);
	public static final class_1299<class_1614> field_6125 = method_5895(
		"silverfish", class_1299.class_1300.method_5903(class_1614::new, class_1311.field_6302).method_17687(0.4F, 0.3F)
	);
	public static final class_1299<class_1613> field_6137 = method_5895(
		"skeleton", class_1299.class_1300.method_5903(class_1613::new, class_1311.field_6302).method_17687(0.6F, 1.99F)
	);
	public static final class_1299<class_1506> field_6075 = method_5895(
		"skeleton_horse", class_1299.class_1300.method_5903(class_1506::new, class_1311.field_6294).method_17687(1.3964844F, 1.6F)
	);
	public static final class_1299<class_1621> field_6069 = method_5895(
		"slime", class_1299.class_1300.method_5903(class_1621::new, class_1311.field_6302).method_17687(2.04F, 2.04F)
	);
	public static final class_1299<class_1677> field_6049 = method_5895(
		"small_fireball", class_1299.class_1300.<class_1677>method_5903(class_1677::new, class_1311.field_17715).method_17687(0.3125F, 0.3125F)
	);
	public static final class_1299<class_1473> field_6047 = method_5895(
		"snow_golem", class_1299.class_1300.method_5903(class_1473::new, class_1311.field_6294).method_17687(0.7F, 1.9F)
	);
	public static final class_1299<class_1680> field_6068 = method_5895(
		"snowball", class_1299.class_1300.<class_1680>method_5903(class_1680::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1679> field_6135 = method_5895(
		"spectral_arrow", class_1299.class_1300.<class_1679>method_5903(class_1679::new, class_1311.field_17715).method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1628> field_6079 = method_5895(
		"spider", class_1299.class_1300.method_5903(class_1628::new, class_1311.field_6302).method_17687(1.4F, 0.9F)
	);
	public static final class_1299<class_1477> field_6114 = method_5895(
		"squid", class_1299.class_1300.method_5903(class_1477::new, class_1311.field_6300).method_17687(0.8F, 0.8F)
	);
	public static final class_1299<class_1627> field_6098 = method_5895(
		"stray", class_1299.class_1300.method_5903(class_1627::new, class_1311.field_6302).method_17687(0.6F, 1.99F)
	);
	public static final class_1299<class_3986> field_17714 = method_5895(
		"trader_llama", class_1299.class_1300.method_5903(class_3986::new, class_1311.field_6294).method_17687(0.9F, 1.87F)
	);
	public static final class_1299<class_1474> field_6111 = method_5895(
		"tropical_fish", class_1299.class_1300.method_5903(class_1474::new, class_1311.field_6300).method_17687(0.5F, 0.4F)
	);
	public static final class_1299<class_1481> field_6113 = method_5895(
		"turtle", class_1299.class_1300.method_5903(class_1481::new, class_1311.field_6294).method_17687(1.2F, 0.4F)
	);
	public static final class_1299<class_1681> field_6144 = method_5895(
		"egg", class_1299.class_1300.<class_1681>method_5903(class_1681::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1684> field_6082 = method_5895(
		"ender_pearl", class_1299.class_1300.<class_1684>method_5903(class_1684::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1683> field_6064 = method_5895(
		"experience_bottle", class_1299.class_1300.<class_1683>method_5903(class_1683::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1686> field_6045 = method_5895(
		"potion", class_1299.class_1300.<class_1686>method_5903(class_1686::new, class_1311.field_17715).method_17687(0.25F, 0.25F)
	);
	public static final class_1299<class_1685> field_6127 = method_5895(
		"trident", class_1299.class_1300.<class_1685>method_5903(class_1685::new, class_1311.field_17715).method_17687(0.5F, 0.5F)
	);
	public static final class_1299<class_1634> field_6059 = method_5895(
		"vex", class_1299.class_1300.method_5903(class_1634::new, class_1311.field_6302).method_19947().method_17687(0.4F, 0.8F)
	);
	public static final class_1299<class_1646> field_6077 = method_5895(
		"villager", class_1299.class_1300.<class_1646>method_5903(class_1646::new, class_1311.field_6294).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1439> field_6147 = method_5895(
		"iron_golem", class_1299.class_1300.method_5903(class_1439::new, class_1311.field_6294).method_17687(1.4F, 2.7F)
	);
	public static final class_1299<class_1632> field_6117 = method_5895(
		"vindicator", class_1299.class_1300.method_5903(class_1632::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1604> field_6105 = method_5895(
		"pillager", class_1299.class_1300.method_5903(class_1604::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_3989> field_17713 = method_5895(
		"wandering_trader", class_1299.class_1300.method_5903(class_3989::new, class_1311.field_6294).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1640> field_6145 = method_5895(
		"witch", class_1299.class_1300.method_5903(class_1640::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1528> field_6119 = method_5895(
		"wither", class_1299.class_1300.method_5903(class_1528::new, class_1311.field_6302).method_19947().method_17687(0.9F, 3.5F)
	);
	public static final class_1299<class_1639> field_6076 = method_5895(
		"wither_skeleton", class_1299.class_1300.method_5903(class_1639::new, class_1311.field_6302).method_19947().method_17687(0.7F, 2.4F)
	);
	public static final class_1299<class_1687> field_6130 = method_5895(
		"wither_skull", class_1299.class_1300.<class_1687>method_5903(class_1687::new, class_1311.field_17715).method_17687(0.3125F, 0.3125F)
	);
	public static final class_1299<class_1493> field_6055 = method_5895(
		"wolf", class_1299.class_1300.method_5903(class_1493::new, class_1311.field_6294).method_17687(0.6F, 0.85F)
	);
	public static final class_1299<class_1642> field_6051 = method_5895(
		"zombie", class_1299.class_1300.<class_1642>method_5903(class_1642::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1507> field_6048 = method_5895(
		"zombie_horse", class_1299.class_1300.method_5903(class_1507::new, class_1311.field_6294).method_17687(1.3964844F, 1.6F)
	);
	public static final class_1299<class_1641> field_6054 = method_5895(
		"zombie_villager", class_1299.class_1300.method_5903(class_1641::new, class_1311.field_6302).method_17687(0.6F, 1.95F)
	);
	public static final class_1299<class_1593> field_6078 = method_5895(
		"phantom", class_1299.class_1300.method_5903(class_1593::new, class_1311.field_6302).method_17687(0.9F, 0.5F)
	);
	public static final class_1299<class_1584> field_6134 = method_5895(
		"ravager", class_1299.class_1300.method_5903(class_1584::new, class_1311.field_6302).method_17687(1.95F, 2.2F)
	);
	public static final class_1299<class_1538> field_6112 = method_5895(
		"lightning_bolt", class_1299.class_1300.<class_1538>method_5902(class_1311.field_17715).method_5904().method_17687(0.0F, 0.0F)
	);
	public static final class_1299<class_1657> field_6097 = method_5895(
		"player", class_1299.class_1300.<class_1657>method_5902(class_1311.field_17715).method_5904().method_5901().method_17687(0.6F, 1.8F)
	);
	public static final class_1299<class_1536> field_6103 = method_5895(
		"fishing_bobber", class_1299.class_1300.<class_1536>method_5902(class_1311.field_17715).method_5904().method_5901().method_17687(0.25F, 0.25F)
	);
	private final class_1299.class_4049<T> field_6101;
	private final class_1311 field_6094;
	private final boolean field_6056;
	private final boolean field_6072;
	private final boolean field_18981;
	@Nullable
	private String field_6106;
	@Nullable
	private class_2561 field_6092;
	@Nullable
	private class_2960 field_16526;
	@Nullable
	private final Type<?> field_6141;
	private final class_4048 field_18070;

	private static <T extends class_1297> class_1299<T> method_5895(String string, class_1299.class_1300<T> arg) {
		return class_2378.method_10226(class_2378.field_11145, string, arg.method_5905(string));
	}

	public static class_2960 method_5890(class_1299<?> arg) {
		return class_2378.field_11145.method_10221(arg);
	}

	public static Optional<class_1299<?>> method_5898(String string) {
		return class_2378.field_11145.method_17966(class_2960.method_12829(string));
	}

	public class_1299(class_1299.class_4049<T> arg, class_1311 arg2, boolean bl, boolean bl2, boolean bl3, @Nullable Type<?> type, class_4048 arg3) {
		this.field_6101 = arg;
		this.field_6094 = arg2;
		this.field_6056 = bl;
		this.field_6072 = bl2;
		this.field_18981 = bl3;
		this.field_6141 = type;
		this.field_18070 = arg3;
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

		Stream<class_265> stream = arg.method_8600(null, lv, Collections.emptySet());
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

	public boolean method_19946() {
		return this.field_18981;
	}

	public class_1311 method_5891() {
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

	public float method_17685() {
		return this.field_18070.field_18067;
	}

	public float method_17686() {
		return this.field_18070.field_18068;
	}

	@Nullable
	public T method_5883(class_1937 arg) {
		return this.field_6101.create(this, arg);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1297 method_5889(int i, class_1937 arg) {
		return method_5886(arg, class_2378.field_11145.method_10200(i));
	}

	public static Optional<class_1297> method_5892(class_2487 arg, class_1937 arg2) {
		return class_156.method_17974(
			method_17684(arg).map(arg2x -> arg2x.method_5883(arg2)),
			arg2x -> arg2x.method_5651(arg),
			() -> field_6088.warn("Skipping Entity with id {}", arg.method_10558("id"))
		);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private static class_1297 method_5886(class_1937 arg, @Nullable class_1299<?> arg2) {
		return arg2 == null ? null : arg2.method_5883(arg);
	}

	public class_238 method_17683(double d, double e, double f) {
		float g = this.method_17685() / 2.0F;
		return new class_238(d - (double)g, e, f - (double)g, d + (double)g, e + (double)this.method_17686(), f + (double)g);
	}

	public class_4048 method_18386() {
		return this.field_18070;
	}

	public static Optional<class_1299<?>> method_17684(class_2487 arg) {
		return class_2378.field_11145.method_17966(new class_2960(arg.method_10558("id")));
	}

	@Nullable
	public static class_1297 method_17842(class_2487 arg, class_1937 arg2, Function<class_1297, class_1297> function) {
		return (class_1297)method_17848(arg, arg2).map(function).map(arg3 -> {
			if (arg.method_10573("Passengers", 9)) {
				class_2499 lv = arg.method_10554("Passengers", 10);

				for (int i = 0; i < lv.size(); i++) {
					class_1297 lv2 = method_17842(lv.method_10602(i), arg2, function);
					if (lv2 != null) {
						lv2.method_5873(arg3, true);
					}
				}
			}

			return arg3;
		}).orElse(null);
	}

	private static Optional<class_1297> method_17848(class_2487 arg, class_1937 arg2) {
		try {
			return method_5892(arg, arg2);
		} catch (RuntimeException var3) {
			field_6088.warn("Exception loading entity: ", (Throwable)var3);
			return Optional.empty();
		}
	}

	public int method_18387() {
		if (this == field_6097) {
			return 32;
		} else if (this == field_6110) {
			return 16;
		} else if (this == field_6116
			|| this == field_6063
			|| this == field_6089
			|| this == field_6043
			|| this == field_6138
			|| this == field_6120
			|| this == field_6131
			|| this == field_6044
			|| this == field_6083
			|| this == field_6060) {
			return 10;
		} else {
			return this != field_6103
					&& this != field_6122
					&& this != field_6135
					&& this != field_6127
					&& this != field_6049
					&& this != field_6129
					&& this != field_6066
					&& this != field_6130
					&& this != field_6068
					&& this != field_6124
					&& this != field_6082
					&& this != field_6061
					&& this != field_6144
					&& this != field_6045
					&& this != field_6064
					&& this != field_6133
					&& this != field_6052
				? 5
				: 4;
		}
	}

	public int method_18388() {
		if (this == field_6097 || this == field_6060) {
			return 2;
		} else if (this == field_6061) {
			return 4;
		} else if (this == field_6103) {
			return 5;
		} else if (this == field_6049
			|| this == field_6129
			|| this == field_6066
			|| this == field_6130
			|| this == field_6068
			|| this == field_6124
			|| this == field_6082
			|| this == field_6144
			|| this == field_6045
			|| this == field_6064
			|| this == field_6133
			|| this == field_6063) {
			return 10;
		} else if (this == field_6122 || this == field_6135 || this == field_6127 || this == field_6052 || this == field_6089 || this == field_6044) {
			return 20;
		} else {
			return this != field_6043 && this != field_6138 && this != field_6120 && this != field_6083 && this != field_6110 ? 3 : Integer.MAX_VALUE;
		}
	}

	public boolean method_18389() {
		return this != field_6097
			&& this != field_6124
			&& this != field_6119
			&& this != field_6108
			&& this != field_6043
			&& this != field_6138
			&& this != field_6120
			&& this != field_6110
			&& this != field_6060;
	}

	public boolean method_20210(class_3494<class_1299<?>> arg) {
		return arg.method_15141(this);
	}

	public static class class_1300<T extends class_1297> {
		private final class_1299.class_4049<T> field_6148;
		private final class_1311 field_6149;
		private boolean field_6151 = true;
		private boolean field_6150 = true;
		private boolean field_18982 = false;
		private class_4048 field_18071 = class_4048.method_18384(0.6F, 1.8F);

		private class_1300(class_1299.class_4049<T> arg, class_1311 arg2) {
			this.field_6148 = arg;
			this.field_6149 = arg2;
		}

		public static <T extends class_1297> class_1299.class_1300<T> method_5903(class_1299.class_4049<T> arg, class_1311 arg2) {
			return new class_1299.class_1300<>(arg, arg2);
		}

		public static <T extends class_1297> class_1299.class_1300<T> method_5902(class_1311 arg) {
			return new class_1299.class_1300<>((argx, arg2) -> null, arg);
		}

		public class_1299.class_1300<T> method_17687(float f, float g) {
			this.field_18071 = class_4048.method_18384(f, g);
			return this;
		}

		public class_1299.class_1300<T> method_5901() {
			this.field_6150 = false;
			return this;
		}

		public class_1299.class_1300<T> method_5904() {
			this.field_6151 = false;
			return this;
		}

		public class_1299.class_1300<T> method_19947() {
			this.field_18982 = true;
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

			return new class_1299<>(this.field_6148, this.field_6149, this.field_6151, this.field_6150, this.field_18982, type, this.field_18071);
		}
	}

	public interface class_4049<T extends class_1297> {
		T create(class_1299<T> arg, class_1937 arg2);
	}
}
