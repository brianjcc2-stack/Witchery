package com.emoniph.witchery.network;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.dimension.WorldProviderDreamWorld;
import com.emoniph.witchery.infusion.Infusion;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PacketPlayerStyle implements IMessage {

   private String username;
   private int grotesqueTicks;
   private int nightmare;
   private boolean ghost;
   private int creatureType;
   private int blood;
   private String playerSkin;
   private int subduedStyle;
   private boolean isAstral;


   public PacketPlayerStyle() {}

   public PacketPlayerStyle(EntityPlayer player) {
      NBTTagCompound nbtPlayer = Infusion.getNBT(player);
      this.username = player.getCommandSenderName();
      this.grotesqueTicks = nbtPlayer.hasKey("witcheryGrotesque")?nbtPlayer.getInteger("witcheryGrotesque"):0;
      this.nightmare = WorldProviderDreamWorld.getPlayerHasNightmare(nbtPlayer);
      this.ghost = WorldProviderDreamWorld.getPlayerIsGhost(nbtPlayer);
      ExtendedPlayer playerEx = ExtendedPlayer.get(player);
      this.creatureType = playerEx.getCreatureTypeOrdinal();
      this.blood = playerEx.getHumanBlood();
      this.playerSkin = playerEx.getOtherPlayerSkin();
      this.subduedStyle = playerEx.getSubduedSpiritStyle();
      this.isAstral = playerEx.isAstralProjecting();
   }

   public void toBytes(ByteBuf buffer) {
      ByteBufUtils.writeUTF8String(buffer, this.username);
      buffer.writeInt(this.grotesqueTicks);
      buffer.writeInt(this.nightmare);
      buffer.writeBoolean(this.ghost);
      buffer.writeInt(this.creatureType);
      buffer.writeInt(this.blood);
      ByteBufUtils.writeUTF8String(buffer, this.playerSkin);
      buffer.writeInt(this.subduedStyle);
      buffer.writeBoolean(this.isAstral);
   }

   public void fromBytes(ByteBuf buffer) {
      this.username = ByteBufUtils.readUTF8String(buffer);
      this.grotesqueTicks = buffer.readInt();
      this.nightmare = buffer.readInt();
      this.ghost = buffer.readBoolean();
      this.creatureType = buffer.readInt();
      this.blood = buffer.readInt();
      this.playerSkin = ByteBufUtils.readUTF8String(buffer);
      if (buffer.readableBytes() >= 4) {
         this.subduedStyle = buffer.readInt();
      } else {
         this.subduedStyle = 0;
      }
      if (buffer.readableBytes() >= 1) {
         this.isAstral = buffer.readBoolean();
      } else {
         this.isAstral = false;
      }
   }

   public static class Handler implements IMessageHandler<PacketPlayerStyle, IMessage> {

      public IMessage onMessage(PacketPlayerStyle message, MessageContext ctx) {
         EntityPlayer player = Witchery.proxy.getPlayer(ctx);
         EntityPlayer otherPlayer = player.worldObj.getPlayerEntityByName(message.username);
         if(otherPlayer != null) {
            NBTTagCompound nbtOtherPlayer = Infusion.getNBT(otherPlayer);
            if(message.grotesqueTicks > 0) {
               nbtOtherPlayer.setInteger("witcheryGrotesque", message.grotesqueTicks);
            } else if(nbtOtherPlayer.hasKey("witcheryGrotesque")) {
               nbtOtherPlayer.removeTag("witcheryGrotesque");
            }

            WorldProviderDreamWorld.setPlayerHasNightmare(nbtOtherPlayer, message.nightmare > 0, message.nightmare > 1);
            WorldProviderDreamWorld.setPlayerIsGhost(nbtOtherPlayer, message.ghost);
            ExtendedPlayer playerEx = ExtendedPlayer.get(otherPlayer);
            playerEx.setCreatureTypeOrdinal(message.creatureType);
            playerEx.setHumanBlood(message.blood);
            playerEx.setOtherPlayerSkin(message.playerSkin);
            playerEx.setSubduedSpiritStyle(message.subduedStyle);
            playerEx.setAstralProjecting(message.isAstral);
            if(!message.isAstral && !message.ghost && message.creatureType == 0 && !otherPlayer.isPotionActive(net.minecraft.potion.Potion.invisibility)) {
               otherPlayer.setInvisible(false);
            }
         }

         return null;
      }
   }
}
