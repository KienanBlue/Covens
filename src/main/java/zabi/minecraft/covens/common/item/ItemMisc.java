package zabi.minecraft.covens.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import zabi.minecraft.covens.common.lib.Reference;

public class ItemMisc extends Item {
	public static final String[] names = new String[] {
			"empty_jar", //0
			"ash", //1
			"oak_spirit", //2 
			"birch_soul", //3
			"acacia_essence", //4
			"spruce_heart", //5
			"cloudy_oil", //6
			"wax_ball", //7
			"talisman", //8
			"talisman_charged" //9
	};
	
	public ItemMisc() {
		this.setCreativeTab(ModCreativeTabs.products);
		this.setHasSubtypes(true);
		this.setRegistryName(Reference.MID, "misc");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.misc."+names[stack.getMetadata()];
	}
	
	@Override
	public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
		if (itemIn.equals(this.getCreativeTab())) {
			for (int i = 0;i<names.length;i++) tab.add(new ItemStack(this,1,i));
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getMetadata()==9;
	}
	
	
}
