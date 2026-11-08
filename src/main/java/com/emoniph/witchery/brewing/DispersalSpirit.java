package com.emoniph.witchery.brewing;

import com.emoniph.witchery.brewing.Dispersal;
import com.emoniph.witchery.brewing.ModifiersEffect;
import com.emoniph.witchery.brewing.ModifiersImpact;
import com.emoniph.witchery.brewing.ModifiersRitual;
import com.emoniph.witchery.brewing.RitualStatus;
import com.emoniph.witchery.brewing.WitcheryBrewRegistry;
import com.emoniph.witchery.entity.EntityLostSoul;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.EntityPosition;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class DispersalSpirit extends Dispersal {

   @Override
   public void onImpactSplashPotion(World world, NBTTagCompound nbtBrew, MovingObjectPosition mop, ModifiersImpact modifiers) {
      if (!world.isRemote) {
         double posX = modifiers.impactPosition.x;
         double posY = modifiers.impactPosition.y;
         double posZ = modifiers.impactPosition.z;

         if (mop != null && mop.entityHit instanceof EntityLivingBase) {
            EntityLivingBase direct = (EntityLivingBase) mop.entityHit;
            WitcheryBrewRegistry.INSTANCE.applyToEntity(world, direct, nbtBrew, new ModifiersEffect(1.0D, 1.0D, false, modifiers.impactPosition, modifiers));
         }

         EntityLivingBase targetEnemy = null;
         AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(posX - 16.0D, posY - 8.0D, posZ - 16.0D, posX + 16.0D, posY + 8.0D, posZ + 16.0D);
         List enemies = world.getEntitiesWithinAABB(EntityMob.class, aabb);
         if (!enemies.isEmpty()) {
            targetEnemy = (EntityLivingBase) enemies.get(world.rand.nextInt(enemies.size()));
         }

         int count = 2;
         for (int i = 0; i < count; i++) {
            double sx = posX + (world.rand.nextDouble() - 0.5D) * 2.0D;
            double sy = posY + 0.5D + world.rand.nextDouble();
            double sz = posZ + (world.rand.nextDouble() - 0.5D) * 2.0D;

            EntityLostSoul soul = (EntityLostSoul) Infusion.spawnCreature(world, EntityLostSoul.class, (int)sx, (int)sy, (int)sz, null, 0, 0, ParticleEffect.INSTANT_SPELL, SoundEffect.RANDOM_POP);
            if (soul != null) {
               soul.getEntityData().setTag("WITCBrewCarrier", nbtBrew.copy());
               if (modifiers.thrower instanceof EntityPlayer) {
                  soul.setSummoner((EntityPlayer)modifiers.thrower);
               }
               soul.setSoulType(i % 3);
               soul.setTimeToLive(TimeUtil.secsToTicks(40));
               if (targetEnemy != null) {
                  soul.setAttackTarget(targetEnemy);
               }
            }
         }

         world.playSoundEffect(posX, posY, posZ, "witchery:mob.spirit.say", 1.0F, 1.2F);
         ParticleEffect.PORTAL.send(SoundEffect.RANDOM_ORB, world, posX, posY, posZ, 1.0D, 1.0D, 20);
      }
   }

   @Override
   public String getUnlocalizedName() {
      return "witchery:brew.dispersal.spirit";
   }

   @Override
   public RitualStatus onUpdateRitual(World world, int x, int y, int z, NBTTagCompound nbtBrew, ModifiersRitual modifiers, ModifiersImpact impactModifiers) {
      return modifiers.getRitualStatus();
   }
}

