package zabi.minecraft.covens.common.registries.fermenting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import zabi.minecraft.covens.common.lib.Reference;

public abstract class BarrelRecipe extends IForgeRegistryEntry.Impl<BarrelRecipe> {

	public static final IForgeRegistry<BarrelRecipe> REGISTRY = new RegistryBuilder<BarrelRecipe>().setName(new ResourceLocation(Reference.MID, "barrel")).setType(BarrelRecipe.class).setIDRange(0, 200).create();
	
	private ItemStack result, retrivial;
	private int ticks, power;
	
	public BarrelRecipe(ItemStack output, ItemStack retrivial, int ticks, int power) {
		result = output;
		this.ticks = ticks;
		this.power = power;
		this.retrivial = retrivial;
	}
	
	public BarrelRecipe(ItemStack output, int ticks, int power) {
		this(output, ItemStack.EMPTY, ticks, power);
	}
	
	public abstract boolean isValidRecipe(World world, NonNullList<ItemStack> stacks, BlockPos pos, FluidStack fluid);
	
	@Nonnull
	public ItemStack getResult() {
		return result;
	}
	
	public int getRequiredTime() {
		return ticks;
	}
	
	public void onFinish(World world, NonNullList<ItemStack> stacks, BlockPos pos, FluidStack fluid) {}
	
	@Nullable
	public static BarrelRecipe getRecipe(World world, NonNullList<ItemStack> stacks, BlockPos pos, FluidStack fluid) {
		for (BarrelRecipe r:REGISTRY) {
			if (r.isValidRecipe(world, stacks, pos, fluid)) return r;
		}
		return null;
	}

	public int getPower() {
		return power;
	}

	public ItemStack getRetrivialItem() {
		return retrivial.copy();
	}
	
}
