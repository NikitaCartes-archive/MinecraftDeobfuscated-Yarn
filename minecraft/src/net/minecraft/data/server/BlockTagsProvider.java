package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockTagsProvider extends AbstractTagProvider<Block> {
	public BlockTagsProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.BLOCK);
	}

	@Override
	protected void configure() {
		this.method_10512(BlockTags.field_15481)
			.add(
				Blocks.field_10446,
				Blocks.field_10095,
				Blocks.field_10215,
				Blocks.field_10294,
				Blocks.field_10490,
				Blocks.field_10028,
				Blocks.field_10459,
				Blocks.field_10423,
				Blocks.field_10222,
				Blocks.field_10619,
				Blocks.field_10259,
				Blocks.field_10514,
				Blocks.field_10113,
				Blocks.field_10170,
				Blocks.field_10314,
				Blocks.field_10146
			);
		this.method_10512(BlockTags.field_15471)
			.add(Blocks.field_10161, Blocks.field_9975, Blocks.field_10148, Blocks.field_10334, Blocks.field_10218, Blocks.field_10075);
		this.method_10512(BlockTags.field_15465).add(Blocks.field_10056, Blocks.field_10065, Blocks.field_10416, Blocks.field_10552);
		this.method_10512(BlockTags.field_15499)
			.add(Blocks.field_10057, Blocks.field_10066, Blocks.field_10417, Blocks.field_10553, Blocks.field_10278, Blocks.field_10493);
		this.method_10512(BlockTags.field_15493).add(BlockTags.field_15499).add(Blocks.field_10494);
		this.method_10512(BlockTags.field_15479)
			.add(
				Blocks.field_10466,
				Blocks.field_9977,
				Blocks.field_10482,
				Blocks.field_10290,
				Blocks.field_10512,
				Blocks.field_10040,
				Blocks.field_10393,
				Blocks.field_10591,
				Blocks.field_10209,
				Blocks.field_10433,
				Blocks.field_10510,
				Blocks.field_10043,
				Blocks.field_10473,
				Blocks.field_10338,
				Blocks.field_10536,
				Blocks.field_10106
			);
		this.method_10512(BlockTags.field_15494)
			.add(Blocks.field_10149, Blocks.field_10521, Blocks.field_10352, Blocks.field_10627, Blocks.field_10232, Blocks.field_10403);
		this.method_10512(BlockTags.field_15502)
			.add(Blocks.field_10563, Blocks.field_10569, Blocks.field_10408, Blocks.field_10122, Blocks.field_10256, Blocks.field_10616);
		this.method_10512(BlockTags.field_15468)
			.add(Blocks.field_10119, Blocks.field_10071, Blocks.field_10257, Blocks.field_10617, Blocks.field_10031, Blocks.field_10500);
		this.method_10512(BlockTags.field_17619)
			.add(Blocks.field_10620, Blocks.field_10144, Blocks.field_10132, Blocks.field_10020, Blocks.field_10299, Blocks.field_10319);
		this.method_10512(BlockTags.field_15495).add(BlockTags.field_15494).add(Blocks.field_9973);
		this.method_10512(BlockTags.field_15462)
			.add(Blocks.field_10394, Blocks.field_10217, Blocks.field_10575, Blocks.field_10276, Blocks.field_10385, Blocks.field_10160);
		this.method_10512(BlockTags.field_15485).add(Blocks.field_10010, Blocks.field_10178, Blocks.field_10244, Blocks.field_10374);
		this.method_10512(BlockTags.field_15482).add(Blocks.field_10431, Blocks.field_10126, Blocks.field_10519, Blocks.field_10250);
		this.method_10512(BlockTags.field_15458).add(Blocks.field_10533, Blocks.field_9999, Blocks.field_10622, Blocks.field_10103);
		this.method_10512(BlockTags.field_15498).add(Blocks.field_10511, Blocks.field_10307, Blocks.field_10366, Blocks.field_10204);
		this.method_10512(BlockTags.field_15474).add(Blocks.field_10306, Blocks.field_10303, Blocks.field_10254, Blocks.field_10084);
		this.method_10512(BlockTags.field_15489).add(Blocks.field_10037, Blocks.field_10155, Blocks.field_10436, Blocks.field_10558);
		this.method_10512(BlockTags.field_15475)
			.add(BlockTags.field_15485)
			.add(BlockTags.field_15482)
			.add(BlockTags.field_15458)
			.add(BlockTags.field_15498)
			.add(BlockTags.field_15474)
			.add(BlockTags.field_15489);
		this.method_10512(BlockTags.field_15486).add(Blocks.field_10535, Blocks.field_10105, Blocks.field_10414);
		this.method_10512(BlockTags.field_15480)
			.add(
				Blocks.field_10182,
				Blocks.field_10449,
				Blocks.field_10086,
				Blocks.field_10226,
				Blocks.field_10573,
				Blocks.field_10270,
				Blocks.field_10048,
				Blocks.field_10156,
				Blocks.field_10315,
				Blocks.field_10554,
				Blocks.field_9995,
				Blocks.field_10548,
				Blocks.field_10606
			);
		this.method_10512(BlockTags.field_15460)
			.add(BlockTags.field_15480)
			.add(
				Blocks.field_10219,
				Blocks.field_10566,
				Blocks.field_10253,
				Blocks.field_10520,
				Blocks.field_10102,
				Blocks.field_10534,
				Blocks.field_10255,
				Blocks.field_10251,
				Blocks.field_10559,
				Blocks.field_10375,
				Blocks.field_10029,
				Blocks.field_10460,
				Blocks.field_10261,
				Blocks.field_10147,
				Blocks.field_10545,
				Blocks.field_10402,
				Blocks.field_10515
			);
		this.method_10512(BlockTags.field_15470)
			.add(
				Blocks.field_10495,
				Blocks.field_10151,
				Blocks.field_9981,
				Blocks.field_10162,
				Blocks.field_10365,
				Blocks.field_10598,
				Blocks.field_10249,
				Blocks.field_10400,
				Blocks.field_10061,
				Blocks.field_10074,
				Blocks.field_10354,
				Blocks.field_10468,
				Blocks.field_10192,
				Blocks.field_10577,
				Blocks.field_10304,
				Blocks.field_10564,
				Blocks.field_10076,
				Blocks.field_10138,
				Blocks.field_10324,
				Blocks.field_10487,
				Blocks.field_10128,
				Blocks.field_10018,
				Blocks.field_10358,
				Blocks.field_10273,
				Blocks.field_9998,
				Blocks.field_10586
			);
		this.method_10512(BlockTags.field_15501)
			.add(
				Blocks.field_10154,
				Blocks.field_10045,
				Blocks.field_10438,
				Blocks.field_10452,
				Blocks.field_10547,
				Blocks.field_10229,
				Blocks.field_10612,
				Blocks.field_10185,
				Blocks.field_9985,
				Blocks.field_10165,
				Blocks.field_10368,
				Blocks.field_10281,
				Blocks.field_10602,
				Blocks.field_10198,
				Blocks.field_10406,
				Blocks.field_10062,
				Blocks.field_10202,
				Blocks.field_10599,
				Blocks.field_10274,
				Blocks.field_10050,
				Blocks.field_10139,
				Blocks.field_10318,
				Blocks.field_10531,
				Blocks.field_10267,
				Blocks.field_10604,
				Blocks.field_10372,
				Blocks.field_10054,
				Blocks.field_10067,
				Blocks.field_10370,
				Blocks.field_10594,
				Blocks.field_10279,
				Blocks.field_10537
			);
		this.method_10512(BlockTags.field_15477)
			.add(Blocks.field_10484, Blocks.field_10332, Blocks.field_10592, Blocks.field_10026, Blocks.field_10397, Blocks.field_10470);
		this.method_10512(BlockTags.field_15459)
			.add(
				Blocks.field_10563,
				Blocks.field_10596,
				Blocks.field_10569,
				Blocks.field_10142,
				Blocks.field_10256,
				Blocks.field_10122,
				Blocks.field_10408,
				Blocks.field_10616,
				Blocks.field_10159,
				Blocks.field_10392,
				Blocks.field_10089,
				Blocks.field_9992,
				Blocks.field_10451,
				Blocks.field_10420,
				Blocks.field_10190,
				Blocks.field_10350,
				Blocks.field_10130,
				Blocks.field_10435,
				Blocks.field_10039,
				Blocks.field_10173,
				Blocks.field_10310,
				Blocks.field_10207,
				Blocks.field_10012,
				Blocks.field_10440,
				Blocks.field_10549,
				Blocks.field_10245,
				Blocks.field_10607,
				Blocks.field_10386,
				Blocks.field_10497,
				Blocks.field_9994,
				Blocks.field_10216
			);
		this.method_10512(BlockTags.field_15469)
			.add(
				Blocks.field_10454,
				Blocks.field_10136,
				Blocks.field_10131,
				Blocks.field_10007,
				Blocks.field_10031,
				Blocks.field_10257,
				Blocks.field_10500,
				Blocks.field_10617,
				Blocks.field_10119,
				Blocks.field_10071,
				Blocks.field_10175,
				Blocks.field_10237,
				Blocks.field_10624,
				Blocks.field_10191,
				Blocks.field_10351,
				Blocks.field_10390,
				Blocks.field_10298,
				Blocks.field_10389,
				Blocks.field_10236,
				Blocks.field_10623,
				Blocks.field_10329,
				Blocks.field_10283,
				Blocks.field_10024,
				Blocks.field_10412,
				Blocks.field_10405,
				Blocks.field_10064,
				Blocks.field_10262,
				Blocks.field_10601,
				Blocks.field_10189,
				Blocks.field_10016,
				Blocks.field_10478,
				Blocks.field_10322,
				Blocks.field_10507,
				Blocks.field_18890,
				Blocks.field_18891
			);
		this.method_10512(BlockTags.field_15504)
			.add(
				Blocks.field_10625,
				Blocks.field_9990,
				Blocks.field_10269,
				Blocks.field_10530,
				Blocks.field_10413,
				Blocks.field_10059,
				Blocks.field_10072,
				Blocks.field_10252,
				Blocks.field_10127,
				Blocks.field_10489,
				Blocks.field_10311,
				Blocks.field_10630,
				Blocks.field_10001,
				Blocks.field_10517
			);
		this.method_10512(BlockTags.field_15483).add(Blocks.field_10125, Blocks.field_10339, Blocks.field_10134, Blocks.field_10618, Blocks.field_10169);
		this.method_10512(BlockTags.field_15488)
			.add(BlockTags.field_15483)
			.add(Blocks.field_10053, Blocks.field_10079, Blocks.field_10427, Blocks.field_10551, Blocks.field_10005);
		this.method_10512(BlockTags.field_15476).add(Blocks.field_10584, Blocks.field_10186, Blocks.field_10447, Blocks.field_10498, Blocks.field_9976);
		this.method_10512(BlockTags.field_15466).add(Blocks.field_10102, Blocks.field_10534);
		this.method_10512(BlockTags.field_15463).add(Blocks.field_10167, Blocks.field_10425, Blocks.field_10025, Blocks.field_10546);
		this.method_10512(BlockTags.field_15461).add(Blocks.field_10309, Blocks.field_10629, Blocks.field_10000, Blocks.field_10516, Blocks.field_10464);
		this.method_10512(BlockTags.field_15467).add(Blocks.field_10295, Blocks.field_10225, Blocks.field_10384, Blocks.field_10110);
		this.method_10512(BlockTags.field_15478).add(Blocks.field_10219, Blocks.field_10520);
		this.method_10512(BlockTags.field_15503)
			.add(Blocks.field_10335, Blocks.field_10503, Blocks.field_9988, Blocks.field_10035, Blocks.field_10098, Blocks.field_10539);
		this.method_10512(BlockTags.field_15490)
			.add(
				Blocks.field_10033,
				Blocks.field_10087,
				Blocks.field_10227,
				Blocks.field_10574,
				Blocks.field_10271,
				Blocks.field_10049,
				Blocks.field_10157,
				Blocks.field_10317,
				Blocks.field_10555,
				Blocks.field_9996,
				Blocks.field_10248,
				Blocks.field_10399,
				Blocks.field_10060,
				Blocks.field_10073,
				Blocks.field_10357,
				Blocks.field_10272,
				Blocks.field_9997
			);
		this.method_10512(BlockTags.field_15491)
			.add(Blocks.field_10608, Blocks.field_10486, Blocks.field_10246, Blocks.field_10017, Blocks.field_10137, Blocks.field_10323);
		this.method_10512(BlockTags.field_15487).add(BlockTags.field_15491).add(Blocks.field_10453);
		this.method_10512(BlockTags.field_15496).add(Blocks.field_10376).add(BlockTags.field_15488).add(BlockTags.field_15476);
		this.method_10512(BlockTags.field_15464)
			.add(Blocks.field_10566)
			.add(Blocks.field_10219)
			.add(Blocks.field_10520)
			.add(Blocks.field_10253)
			.add(Blocks.field_10402);
		this.method_10512(BlockTags.field_15497)
			.add(Blocks.field_10211)
			.add(Blocks.field_10108)
			.add(Blocks.field_10255)
			.add(BlockTags.field_15466)
			.add(BlockTags.field_15464);
		this.method_10512(BlockTags.field_15472)
			.add(Blocks.field_10121, Blocks.field_10411, Blocks.field_10231, Blocks.field_10284, Blocks.field_10544, Blocks.field_10330);
		this.method_10512(BlockTags.field_15492)
			.add(Blocks.field_10187, Blocks.field_10088, Blocks.field_10391, Blocks.field_10401, Blocks.field_10587, Blocks.field_10265);
		this.method_10512(BlockTags.field_15500).add(BlockTags.field_15472).add(BlockTags.field_15492);
		this.method_10512(BlockTags.field_16443)
			.add(
				Blocks.field_10069,
				Blocks.field_10461,
				Blocks.field_10527,
				Blocks.field_10288,
				Blocks.field_10109,
				Blocks.field_10141,
				Blocks.field_10561,
				Blocks.field_10621,
				Blocks.field_10326,
				Blocks.field_10180,
				Blocks.field_10230,
				Blocks.field_10410,
				Blocks.field_10610,
				Blocks.field_10019,
				Blocks.field_10120,
				Blocks.field_10356
			);
		this.method_10512(BlockTags.field_16584).add(BlockTags.field_17619).add(Blocks.field_10364);
		this.method_10512(BlockTags.field_17753)
			.add(
				Blocks.field_10499,
				Blocks.field_9987,
				Blocks.field_10027,
				Blocks.field_10398,
				Blocks.field_10613,
				Blocks.field_10525,
				Blocks.field_10263,
				Blocks.field_10395,
				Blocks.field_10465,
				Blocks.field_16540,
				Blocks.field_10008,
				Blocks.field_10540,
				Blocks.field_10471,
				Blocks.field_10576
			);
		this.method_10512(BlockTags.field_17754)
			.add(
				Blocks.field_10499,
				Blocks.field_9987,
				Blocks.field_10027,
				Blocks.field_10398,
				Blocks.field_10613,
				Blocks.field_10525,
				Blocks.field_10263,
				Blocks.field_10395,
				Blocks.field_10465,
				Blocks.field_16540,
				Blocks.field_10008
			);
	}

	@Override
	protected Path getOutput(Identifier identifier) {
		return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/blocks/" + identifier.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Block Tags";
	}

	@Override
	protected void method_10511(TagContainer<Block> tagContainer) {
		BlockTags.setContainer(tagContainer);
	}
}
