package zabi.minecraft.covens.client.jei.categories;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import zabi.minecraft.covens.common.lib.Reference;

public class RitualCategory implements IRecipeCategory<RitualWrapper> {
	
	private IDrawable bg;
	
	public static final String UID = Reference.MID+":rituals";
	
	public RitualCategory(IGuiHelper igh) {
		bg=igh.createBlankDrawable(140, 120);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.category.rituals");
	}

	@Override
	public String getModName() {
		return Reference.NAME;
	}

	@Override
	public IDrawable getBackground() {
		return bg;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RitualWrapper recipeWrapper, IIngredients ingredients) {
		List<ItemStack> stacksIn = recipeWrapper.input;
		for (int i=0;i<stacksIn.size();i++) {
			recipeLayout.getItemStacks().init(i, true, 18*i+(140-18*stacksIn.size())/2, 15);
			recipeLayout.getItemStacks().set(i, stacksIn.get(i));
		}
		List<ItemStack> stacksOut = recipeWrapper.output;
		for (int i=0;i<stacksOut.size();i++) {
			recipeLayout.getItemStacks().init(i + stacksIn.size(), false, 18*i+(140-18*stacksOut.size())/2, 70);
			recipeLayout.getItemStacks().set(i + stacksIn.size(), stacksOut.get(i));
		}
	}
}
