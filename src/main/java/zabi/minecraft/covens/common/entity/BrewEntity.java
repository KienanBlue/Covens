package zabi.minecraft.covens.common.entity;

import java.util.List;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import zabi.minecraft.covens.common.capability.CovensData;
import zabi.minecraft.covens.common.item.ModItems;
import zabi.minecraft.covens.common.lib.Log;
import zabi.minecraft.covens.common.potion.ModPotions;
import zabi.minecraft.covens.common.registries.brewing.BrewData;
import zabi.minecraft.covens.common.registries.brewing.CovenPotionEffect;
import zabi.minecraft.covens.common.registries.brewing.environmental.EnvironmentalPotionEffect;

public class BrewEntity extends EntityThrowable {
	
	private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityPotion.class, DataSerializers.ITEM_STACK);

	private ItemStack potion;
	
	public BrewEntity(World worldIn) {
		super(worldIn);
		setItem(ItemStack.EMPTY);
	}
	
	public BrewEntity(World worldIn, ItemStack potion) {
		super(worldIn);
		this.setItem(potion);
	}
	
	public BrewEntity(World worldIn, EntityLivingBase throwerIn, ItemStack potion) {
        super(worldIn, throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.10000000149011612D, throwerIn.posZ);
        this.setItem(potion);
    }
	
	public BrewEntity(World worldIn, double x, double y, double z, ItemStack potion) {
        this(worldIn, potion);
        this.setPosition(x, y, z);
        this.setItem(potion);
    }
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(ITEM, ItemStack.EMPTY);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit!=RayTraceResult.Type.MISS && result.getBlockPos()!=null) {
			ItemStack itemst = getDataManager().get(ITEM);
			Item i = itemst.getItem();
			BrewData data = new BrewData(i.equals(ModItems.brew_splash)?1:(i.equals(ModItems.brew_lingering)?2:(i.equals(ModItems.brew_gas)?3:0)));
			data.readFromNBT(itemst.getOrCreateSubCompound("brewdata"));
			Item type = itemst.getItem();
			if (type==ModItems.brew_splash) {
				splash(result);
			} else if (type==ModItems.brew_gas) {
				gas(result);
			} else if (type==ModItems.brew_lingering) {
				linger(result, false);
			} else {
				Log.e("Invalid Brew type, please report to Covens' author");
			}
			setDead();
		}
	}

	private void linger(RayTraceResult result, boolean fromGas) {
		if (!world.isRemote) {
			EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, result.hitVec.x, result.hitVec.y, result.hitVec.z);
			entityareaeffectcloud.setOwner(this.getThrower());
			entityareaeffectcloud.setRadiusOnUse(0);
			entityareaeffectcloud.setWaitTime(10);
			entityareaeffectcloud.setRadiusPerTick(0);

			int persistency = 0;

			BrewData data = new BrewData();
			data.readFromNBT(getDataManager().get(ITEM).getOrCreateSubCompound("brewdata"));

			NonNullList<CovenPotionEffect> effects = data.getEffects();

			for (CovenPotionEffect pe:effects) {
				if (pe.getPotionEffect().getPotion().equals(ModPotions.tinting)) continue;
				if (fromGas) pe.setDiminished();
				entityareaeffectcloud.addEffect(pe.getPotionEffect());
				persistency += pe.getPersistency();
			}

			persistency /= effects.size();
			entityareaeffectcloud.setRadius(1+persistency);
			entityareaeffectcloud.setColor(data.getColor());
			entityareaeffectcloud.setDuration(80+persistency*60);
			this.world.spawnEntity(entityareaeffectcloud);
			this.world.playEvent(2007, new BlockPos(this), data.getColor());
		}
	}

	private void gas(RayTraceResult result) {
		BrewData data = new BrewData();
		data.readFromNBT(getDataManager().get(ITEM).getOrCreateSubCompound("brewdata"));
		for (CovenPotionEffect cpe:data.getEffects()) {
			EnvironmentalPotionEffect epe = EnvironmentalPotionEffect.getEffectForPotion(cpe.getPotionEffect().getPotion());
			if (epe!=null) {
				epe.splashedOn(world, new BlockPos(result.hitVec), this.getThrower(), cpe);
			}
		}
		linger(result, true);
	}

	private void splash(RayTraceResult result) {
		BrewData data = new BrewData();
		data.readFromNBT(getDataManager().get(ITEM).getOrCreateSubCompound("brewdata"));
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> list = this.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
        if (!list.isEmpty()) {
            for (EntityLivingBase entitylivingbase : list) {
            	boolean isTinting = false;
                if (entitylivingbase.canBeHitWithPotion()) {
                    double distance = this.getDistanceSqToEntity(entitylivingbase);
                    if (distance < 16.0D) {
                        double falloffCoefficent = (1.0D - Math.sqrt(distance) / 4.0D);
                        if (entitylivingbase == result.entityHit) falloffCoefficent = 1.0D;
                        for (CovenPotionEffect cpotioneffect : data.getEffects()) {
                        	PotionEffect potioneffect = cpotioneffect.getPotionEffect();
                            Potion potion = potioneffect.getPotion();
                            isTinting = potion.equals(ModPotions.tinting);
                            if (potion.isInstant()) {
                                potion.affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), falloffCoefficent);
                            } else {
                                int i = (int)(cpotioneffect.getMultiplier() * falloffCoefficent * (double)potioneffect.getDuration() + 0.5D);
                                if (i > 20) {
                                    entitylivingbase.addPotionEffect(new PotionEffect(potion, i, potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
                                }
                            }
                        }
                    }
                }
                if (isTinting) {
                	entitylivingbase.getCapability(CovensData.CAPABILITY, null).setTint(data.getColor());
                }
            }
        }
        if (!world.isRemote) this.world.playEvent(2007, new BlockPos(this), data.getColor());
	}

	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		potion = new ItemStack(compound.getCompoundTag("potion"));
		setItem(potion);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		NBTTagCompound item = new NBTTagCompound();
		potion.writeToNBT(item);
		tag.setTag("potion", item);
		return tag;
	}
	
	public ItemStack getItemStack() {
		return potion;
	}

	public void setItem(ItemStack stack) {
		this.getDataManager().set(ITEM, stack);
		this.getDataManager().setDirty(ITEM);
		potion = stack;
	}
}
