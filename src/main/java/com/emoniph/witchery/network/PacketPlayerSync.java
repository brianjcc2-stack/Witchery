package com.emoniph.witchery.network;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.infusion.InfusedBrewEffect;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.creature.CreaturePower;
import com.emoniph.witchery.util.TimeUtil;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PacketPlayerSync implements IMessage {

   private int infusionID;
   private int curEnergy;
   private int maxEnergy;
   private int creatureID;
   private int creatureCharges;
   private int sinkingCurseLevel;
   private int brewEffect;
   private long brewTime;
   private NBTTagCompound spellBookTag;


   public PacketPlayerSync() {}

   public PacketPlayerSync(EntityPlayer player) {
      this.infusionID = Infusion.getInfusionID(player);
      this.curEnergy = Infusion.getCurrentEnergy(player);
      this.maxEnergy = Infusion.getMaxEnergy(player);
      this.creatureID = CreaturePower.getCreaturePowerID(player);
      this.creatureCharges = CreaturePower.getCreaturePowerCharges(player);
      this.sinkingCurseLevel = Infusion.getSinkingCurseLevel(player);
      NBTTagCompound nbtPlayer = Infusion.getNBT(player);
      InfusedBrewEffect brew = InfusedBrewEffect.getActiveBrew(nbtPlayer);
      long time = InfusedBrewEffect.getActiveBrewStartTime(nbtPlayer);
      this.brewEffect = brew != null?brew.id:0;
      this.brewTime = 0L;
      if(brew != null) {
         long remainingTicks = brew.durationTicks - (TimeUtil.getServerTimeInTicks() - time);
         if(remainingTicks > 0L) {
            this.brewTime = (long)((int)Math.ceil((double)remainingTicks / 1200.0D));
         }
      }
      this.spellBookTag = (nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook"))
         ? (NBTTagCompound)nbtPlayer.getCompoundTag("WITCSpellBook").copy()
         : new NBTTagCompound();
      if(player != null && player.worldObj != null && !player.worldObj.isRemote) {
         this.spellBookTag.setBoolean("_IsSupreme", SupremeWitchData.isPlayerSupreme(player));
      }

   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeInt(this.infusionID);
      buffer.writeInt(this.curEnergy);
      buffer.writeInt(this.maxEnergy);
      buffer.writeInt(this.creatureID);
      buffer.writeInt(this.creatureCharges);
      buffer.writeInt(this.sinkingCurseLevel);
      buffer.writeInt(this.brewEffect);
      buffer.writeLong(this.brewTime);
      ByteBufUtils.writeTag(buffer, this.spellBookTag);
   }

   public void fromBytes(ByteBuf buffer) {
      this.infusionID = buffer.readInt();
      this.curEnergy = buffer.readInt();
      this.maxEnergy = buffer.readInt();
      this.creatureID = buffer.readInt();
      this.creatureCharges = buffer.readInt();
      this.sinkingCurseLevel = buffer.readInt();
      this.brewEffect = buffer.readInt();
      this.brewTime = buffer.readLong();
      this.spellBookTag = ByteBufUtils.readTag(buffer);
   }

   public static class Handler implements IMessageHandler<PacketPlayerSync, IMessage> {

      public IMessage onMessage(PacketPlayerSync message, MessageContext ctx) {
         EntityPlayer player = Witchery.proxy.getPlayer(ctx);
         if(player != null && message != null) {
            Infusion.setEnergy(player, message.infusionID, message.curEnergy, message.maxEnergy);
            CreaturePower.setCreaturePowerID(player, message.creatureID, message.creatureCharges);
            Infusion.setSinkingCurseLevel(player, message.sinkingCurseLevel);
            NBTTagCompound nbtPlayer = Infusion.getNBT(player);
            if(message.brewEffect > 0) {
               InfusedBrewEffect.setActiveBrewInfo(nbtPlayer, message.brewEffect, message.brewTime);
            }
            if(message.spellBookTag != null && nbtPlayer != null) {
               nbtPlayer.setTag("WITCSpellBook", message.spellBookTag);
            }
         }

         return null;
      }
   }
}
