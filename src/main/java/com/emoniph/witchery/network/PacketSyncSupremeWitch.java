package com.emoniph.witchery.network;

import com.emoniph.witchery.common.SupremeWitchData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncSupremeWitch implements IMessage {

    private String supremeUUID = "";
    private String supremeName = "";

    public PacketSyncSupremeWitch() {
    }

    public PacketSyncSupremeWitch(String supremeUUID, String supremeName) {
        this.supremeUUID = supremeUUID != null ? supremeUUID : "";
        this.supremeName = supremeName != null ? supremeName : "";
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.supremeUUID);
        ByteBufUtils.writeUTF8String(buffer, this.supremeName);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.supremeUUID = ByteBufUtils.readUTF8String(buffer);
        this.supremeName = ByteBufUtils.readUTF8String(buffer);
    }

    public static class Handler implements IMessageHandler<PacketSyncSupremeWitch, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncSupremeWitch message, MessageContext ctx) {
            SupremeWitchData.clientSupremeUUID = message.supremeUUID;
            SupremeWitchData.clientSupremeName = message.supremeName;
            return null;
        }
    }
}
