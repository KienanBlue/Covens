package zabi.minecraft.covens.common.network.messages;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zabi.minecraft.covens.common.capability.CovensData;

public class SyncDataResponse implements IMessage {
	
	NBTTagCompound data;
	String id;
	
	public SyncDataResponse() {
	}
	
	public SyncDataResponse(String uid, NBTTagCompound data) {
		this.data = data;
		id = uid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
		id = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
		ByteBufUtils.writeUTF8String(buf, id);
	}
	
	public static class Handler implements IMessageHandler<SyncDataResponse, IMessage> {

		@Override
		public IMessage onMessage(SyncDataResponse message, MessageContext ctx) {
			List<Entity> list = new ArrayList<Entity>(Minecraft.getMinecraft().world.loadedEntityList);
			list.parallelStream().filter(e -> e.getUniqueID().toString().equals(message.id)).forEach(e -> {
				CovensData.CAPABILITY.getStorage().readNBT(CovensData.CAPABILITY, e.getCapability(CovensData.CAPABILITY, null), null, message.data);
			});
			return null;
		}

	}

}
