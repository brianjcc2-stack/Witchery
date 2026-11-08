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
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class DispersalChain extends Dispersal {

   @Override
   public void onImpactSplashPotion(World world, NBTTagCompound nbtBrew, MovingObjectPosition mop, ModifiersImpact modifiers) {
      if (!world.isRemote) {
         double posX = modifiers.impactPosition.x;
         double posY = modifiers.impactPosition.y;
         double posZ = modifiers.impactPosition.z;

         EntityLivingBase direct = null;
         if (mop != null && mop.entityHit instanceof EntityLivingBase) {
            direct = (EntityLivingBase) mop.entityHit;
            WitcheryBrewRegistry.INSTANCE.applyToEntity(world, direct, nbtBrew, new ModifiersEffect(1.0D, 1.0D, false, modifiers.impactPosition, modifiers));
         }

         double R = 8.0D;
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(posX - R, posY - R, posZ - R, posX + R, posY + R, posZ + R);
         List list = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
         int chainCount = 0;
         double prevX = posX;
         double prevY = posY;
         double prevZ = posZ;

         Iterator it = list.iterator();
         while (it.hasNext() && chainCount < 5) {
            EntityLivingBase target = (EntityLivingBase) it.next();
            if (target != direct && target != modifiers.thrower && !target.isDead && target.getHealth() > 0.0F) {
               WitcheryBrewRegistry.INSTANCE.applyToEntity(world, target, nbtBrew, new ModifiersEffect(0.85D, 0.85D, true, new EntityPosition(target), modifiers));

               // Trace particle arc from previous point to target
               double dx = (target.posX - prevX) / 5.0D;
               double dy = (target.posY + (double)target.getEyeHeight() * 0.5D - prevY) / 5.0D;
               double dz = (target.posZ - prevZ) / 5.0D;
               for (int step = 0; step < 5; step++) {
                  ParticleEffect.MAGIC_CRIT.send(SoundEffect.NONE, world, prevX + dx * step, prevY + dy * step, prevZ + dz * step, 0.1D, 0.1D, 2);
               }

               prevX = target.posX;
               prevY = target.posY + (double)target.getEyeHeight() * 0.5D;
               prevZ = target.posZ;
               chainCount++;
            }
         }

         world.playSoundEffect(posX, posY, posZ, "witchery:mob.spectre.spectresay", 0.8F, 1.4F);
         ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, world, posX, posY, posZ, 1.0D, 1.0D, 15);
      }
   }

   @Override
   public String getUnlocalizedName() {
      return "witchery:brew.dispersal.chain";
   }

   @Override
   public RitualStatus onUpdateRitual(World world, int x, int y, int z, NBTTagCompound nbtBrew, ModifiersRitual modifiers, ModifiersImpact impactModifiers) {
      return modifiers.getRitualStatus();
   }
}

