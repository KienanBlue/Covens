package zabi.minecraft.covens.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import zabi.minecraft.covens.common.crafting.brewing.BrewData;
import zabi.minecraft.covens.common.crafting.brewing.CovenPotionEffect;
import zabi.minecraft.covens.common.item.ItemBrewDrinkable;
import zabi.minecraft.covens.common.item.ModCreativeTabs;
import zabi.minecraft.covens.common.item.ModItems;
import zabi.minecraft.covens.common.lib.Reference;
import zabi.minecraft.covens.common.tileentity.TileEntityCauldron;

public class BlockCauldron extends Block implements ITileEntityProvider {
	
	public static final PropertyBool FULL = PropertyBool.create("filled");

	public BlockCauldron() {
		super(Material.IRON);
		this.setUnlocalizedName("cauldron");
		this.setCreativeTab(ModCreativeTabs.machines);
		this.setRegistryName(Reference.MID, "cauldron");
		this.setDefaultState(blockState.getBaseState().withProperty(FULL, false));
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1);
		
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCauldron();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;//state.getValue(FULL);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FULL);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FULL, meta==1);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FULL)?1:0;
	}
	
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && hand==EnumHand.MAIN_HAND && worldIn.getBlockState(pos.down()).getBlock().equals(Blocks.FIRE)) {//Check if full too
			if (playerIn.getHeldItem(hand).getItem().equals(Items.GLASS_BOTTLE)) {
				TileEntityCauldron cauldron = (TileEntityCauldron) worldIn.getTileEntity(pos);
				if (!cauldron.canTakePotion()) {
					int cost = cauldron.getResult(false).getCost();
					if (!cauldron.consumePower(cost)) {
						playerIn.sendStatusMessage(new TextComponentTranslation("brew.failure.power", cost), true);
					}
					return false;
				}
				BrewData data = cauldron.getResult(true); //TODO make it dependent on player level instead of always true
				NonNullList<CovenPotionEffect> list = data.getEffects();
				if (!playerIn.isCreative()) playerIn.getHeldItem(hand).setCount(playerIn.getHeldItem(hand).getCount()-1);
				if (list.isEmpty()) {
					worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ModItems.brew_drinkable,1,1)));//TODO change based on brew type
					return true;
				}
				worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, ItemBrewDrinkable.getBrewStackWithData(ModItems.brew_drinkable, data)));//TODO change based on brew type
				return true;
			} else if (playerIn.getHeldItem(hand).isEmpty()) {
				TileEntityCauldron cauldron = (TileEntityCauldron) worldIn.getTileEntity(pos);
				if (cauldron.getHasItems()) {
					int cost = cauldron.getResult(false).getCost();
					playerIn.sendStatusMessage(new TextComponentTranslation("brew.success.power", cost), true);
				}
			}
		}
		return false;
	}
}
