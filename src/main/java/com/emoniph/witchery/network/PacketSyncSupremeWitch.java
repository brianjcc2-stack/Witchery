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
    private boolean auraEnabled = true;
    private int auraColor = 0x33CC00;
    private int auraAmount = 2;

    public PacketSyncSupremeWitch() {
    }

    public PacketSyncSupremeWitch(String supremeUUID, String supremeName) {
        this(supremeUUID, supremeName, true, 0x33CC00, 2);
    }

    public PacketSyncSupremeWitch(String supremeUUID, String supremeName, boolean auraEnabled) {
        this(supremeUUID, supremeName, auraEnabled, 0x33CC00, 2);
    }

    public PacketSyncSupremeWitch(String supremeUUID, String supremeName, boolean auraEnabled, int auraColor, int auraAmount) {
        this.supremeUUID = supremeUUID != null ? supremeUUID : "";
        this.supremeName = supremeName != null ? supremeName : "";
        this.auraEnabled = auraEnabled;
        this.auraColor = auraColor;
        this.auraAmount = Math.max(1, Math.min(10, auraAmount));
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.supremeUUID);
        ByteBufUtils.writeUTF8String(buffer, this.supremeName);
        buffer.writeBoolean(this.auraEnabled);
        buffer.writeInt(this.auraColor);
        buffer.writeInt(this.auraAmount);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.supremeUUID = ByteBufUtils.readUTF8String(buffer);
        this.supremeName = ByteBufUtils.readUTF8String(buffer);
        if (buffer.readableBytes() > 0) {
            this.auraEnabled = buffer.readBoolean();
        } else {
            this.auraEnabled = true;
        }
        if (buffer.readableBytes() >= 4) {
            this.auraColor = buffer.readInt();
        } else {
            this.auraColor = 0x33CC00;
        }
        if (buffer.readableBytes() >= 4) {
            this.auraAmount = buffer.readInt();
        } else {
            this.auraAmount = 2;
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncSupremeWitch, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncSupremeWitch message, MessageContext ctx) {
            SupremeWitchData.clientSupremeUUID = message.supremeUUID;
            SupremeWitchData.clientSupremeName = message.supremeName;
            SupremeWitchData.clientAuraEnabled = message.auraEnabled;
            SupremeWitchData.clientAuraColor = message.auraColor;
            SupremeWitchData.clientAuraAmount = message.auraAmount;
            return null;
        }
    }
}
