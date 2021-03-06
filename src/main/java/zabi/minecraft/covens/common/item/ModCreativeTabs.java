package zabi.minecraft.covens.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import zabi.minecraft.covens.common.block.ModBlocks;
import zabi.minecraft.covens.common.lib.Log;
import zabi.minecraft.covens.common.lib.Reference;

public class ModCreativeTabs extends CreativeTabs {
	
	private ItemStack icon;
	
	public ModCreativeTabs(String label) {
		super(Reference.MID+"."+label);
	}

	public static ModCreativeTabs herbs, machines, products, brews;
	
	public static void registerTabs() {
		Log.i("Creating tabs");
		herbs = new ModCreativeTabs("herbs");
		machines = new ModCreativeTabs("machines");
		products = new ModCreativeTabs("products");
		brews = new ModCreativeTabs("brews");
	}
	
	public static void registerIcons() {
		herbs.setTabIconItem(new ItemStack(ModItems.flowers));
		machines.setTabIconItem(new ItemStack(ModBlocks.chimney));
		products.setTabIconItem(new ItemStack(ModItems.misc));
		brews.setTabIconItem(new ItemStack(ModItems.brew_drinkable));
	}

	@Override
	public ItemStack getTabIconItem() {
		return icon;
	}
	
	public void setTabIconItem(ItemStack stack) {
		icon = stack;
	}
	
}
