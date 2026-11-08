package com.emoniph.witchery.brewing;

import com.emoniph.witchery.brewing.Dispersal;
import com.emoniph.witchery.brewing.ModifiersEffect;
import com.emoniph.witchery.brewing.ModifiersImpact;
import com.emoniph.witchery.brewing.ModifiersRitual;
import com.emoniph.witchery.brewing.RitualStatus;
import com.emoniph.witchery.brewing.WitcheryBrewRegistry;
import com.emoniph.witchery.util.EntityPosition;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class DispersalMantle extends Dispersal {

   @Override
   public void onImpactSplashPotion(World world, NBTTagCompound nbtBrew, MovingObjectPosition mop, ModifiersImpact modifiers) {
      if (!world.isRemote) {
         EntityLivingBase recipient = null;
         if (modifiers.thrower instanceof EntityLivingBase) {
            recipient = (EntityLivingBase) modifiers.thrower;
         } else if (mop != null && mop.entityHit instanceof EntityLivingBase) {
            recipient = (EntityLivingBase) mop.entityHit;
         }

         if (recipient != null) {
            recipient.getEntityData().setTag("WITCMantleBrew", nbtBrew.copy());
            recipient.getEntityData().setInteger("WITCMantleTicks", 1200); // 60 segundos
            world.playSoundAtEntity(recipient, "witchery:random.hypnosis", 1.0F, 1.2F);
            ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_ORB, recipient, 1.0D, 1.5D, 25);
            if (recipient instanceof EntityPlayer) {
               ((EntityPlayer)recipient).addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "¡Un manto de represalia mágica envuelve tu cuerpo!"));
            }
         }
      }
   }

   @Override
   public String getUnlocalizedName() {
      return "witchery:brew.dispersal.mantle";
   }

   @Override
   public RitualStatus onUpdateRitual(World world, int x, int y, int z, NBTTagCompound nbtBrew, ModifiersRitual modifiers, ModifiersImpact impactModifiers) {
      return modifiers.getRitualStatus();
   }
}

