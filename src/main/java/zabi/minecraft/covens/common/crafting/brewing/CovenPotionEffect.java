package zabi.minecraft.covens.common.crafting.brewing;

import java.util.Collections;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class CovenPotionEffect {
	private String potion = "";
	private int length = 0;
	private int strength = 0;
	private int persistency = 0;
	private boolean showParticle = true;
	private boolean isAmbient = true;
	private boolean isCurable = true;
	
	private CovenPotionEffect() {
		
	}
	
	public CovenPotionEffect(Potion potion, int duration, int level) {
		this.potion = potion.getRegistryName().toString();
		this.length = duration;
		this.strength = level;
	}
	
	public PotionEffect getPotionEffect() {
		PotionEffect pe = new PotionEffect(Potion.getPotionFromResourceLocation(potion), length, strength, isAmbient, showParticle);
		if (isCurable) pe.setCurativeItems(Collections.singletonList(new ItemStack(Items.MILK_BUCKET)));
		else pe.setCurativeItems(Collections.<ItemStack>emptyList());
		return pe;
	}

	public int getPersistency() {
		return persistency;
	}

	public CovenPotionEffect setPersistency(int persistency) {
		this.persistency = persistency;
		return this;
	}

	public boolean isCurable() {
		return isCurable;
	}

	public CovenPotionEffect setCurable(boolean isCurable) {
		this.isCurable = isCurable;
		return this;
	}

	public boolean isAmbient() {
		return isAmbient;
	}

	public CovenPotionEffect setAmbient(boolean isAmbient) {
		this.isAmbient = isAmbient;
		return this;
	}

	public boolean doesShowParticle() {
		return showParticle;
	}

	public CovenPotionEffect setShowParticle(boolean showParticle) {
		this.showParticle = showParticle;
		return this;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("potion", potion);
		tag.setIntArray("data_int", new int[] {length, strength, persistency});
		tag.setBoolean("showParticle", showParticle);
		tag.setBoolean("isAmbient", isAmbient);
		tag.setBoolean("isCurable", isCurable);
		return tag;
	}
	
	public static CovenPotionEffect loadFromNBT(NBTTagCompound tag) {
		CovenPotionEffect cpe = new CovenPotionEffect();
		cpe.potion = tag.getString("potion");
		int[] data = tag.getIntArray("data_int");
		cpe.length = data[0];
		cpe.strength = data[1];
		cpe.persistency = data[2];
		cpe.showParticle = tag.getBoolean("showParticle");
		cpe.isAmbient = tag.getBoolean("isAmbient");
		cpe.isCurable = tag.getBoolean("isCurable");
		return cpe;
	}
	
}
