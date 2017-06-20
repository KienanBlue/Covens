package zabi.minecraft.covens.common.block;

public class ModBlocks {
	
	public static BlockCircleGlyph glyphs;
	public static BlockWitchAltar altar;
	public static BlockModCrop hellebore, aconitum, sagebrush, chrysanthemum;
	
	public static void registerAll() {
		glyphs = new BlockCircleGlyph();
		altar = new BlockWitchAltar();
		hellebore = new BlockModCrop("hellebore");
		aconitum = new BlockModCrop("aconitum");
		sagebrush = new BlockModCrop("sagebrush");
		chrysanthemum = new BlockModCrop("chrysanthemum");
	}
}
