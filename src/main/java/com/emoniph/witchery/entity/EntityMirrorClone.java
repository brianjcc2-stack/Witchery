package com.emoniph.witchery.entity;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityMirrorClone extends EntityCreature {

   private int ticksRemaining = 300; // 15 seconds
   private String ownerName = "";

   public EntityMirrorClone(World world) {
      super(world);
      this.setSize(0.6F, 1.8F);
      this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.experienceValue = 0;
   }

   public EntityMirrorClone(World world, EntityPlayer owner) {
      this(world);
      this.setPosition(owner.posX, owner.posY, owner.posZ);
      this.rotationYaw = owner.rotationYaw;
      this.rotationPitch = owner.rotationPitch;
      this.ownerName = owner.getCommandSenderName();
      this.setCustomNameTag(owner.getCommandSenderName() + " (Reflejo)");

      // Copy equipment from player
      for (int i = 0; i < 5; ++i) {
         ItemStack stack = owner.getEquipmentInSlot(i);
         if (stack != null) {
            this.setCurrentItemOrArmor(i, stack.copy());
         }
      }
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2D);
   }

   @Override
   public boolean isAIEnabled() {
      return true;
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();

      if (!this.worldObj.isRemote) {
         --this.ticksRemaining;
         if (this.ticksRemaining <= 0) {
            this.shatter();
            return;
         }

         // Periodic taunt: divert nearby hostile mob targets onto this clone
         if (this.ticksExisted % 10 == 0) {
            double r = 16.0D;
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(this.posX - r, this.posY - 8.0D, this.posZ - r, this.posX + r, this.posY + 8.0D, this.posZ + r);
            List mobs = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, bb);
            for (Object obj : mobs) {
               EntityLiving mob = (EntityLiving) obj;
               if (mob instanceof IMob && mob != this) {
                  EntityLivingBase target = mob.getAttackTarget();
                  if (target == null || (target instanceof EntityPlayer && target.getCommandSenderName().equals(this.ownerName))) {
                     mob.setAttackTarget(this);
                  }
               }
            }
         }
      } else {
         // Subtle glass glimmer particles
         if (this.rand.nextInt(3) == 0) {
            this.worldObj.spawnParticle("magicCrit", this.posX + (this.rand.nextDouble() - 0.5D) * 0.8D, this.posY + this.rand.nextDouble() * 1.8D, this.posZ + (this.rand.nextDouble() - 0.5D) * 0.8D, 0.0D, 0.05D, 0.0D);
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable()) {
         return false;
      }

      Entity attacker = source.getEntity();
      if (attacker != null && attacker != this && !this.worldObj.isRemote) {
         // Reflect 40% damage back to attacker
         float reflected = Math.max(1.0F, amount * 0.4F);
         attacker.attackEntityFrom(DamageSource.causeThornsDamage(this), reflected);
         ParticleEffect.CRIT.send(SoundEffect.RANDOM_GLASS, this.worldObj, attacker.posX, attacker.posY + 1.0D, attacker.posZ, 0.5D, 0.5D, 16);
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (!this.worldObj.isRemote) {
         this.shatter();
      }
   }

   private void shatter() {
      ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_GLASS, this.worldObj, this.posX, this.posY + 1.0D, this.posZ, 0.8D, 1.2D, 16, 0x88CCFF);
      this.setDead();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound nbt) {
      super.writeEntityToNBT(nbt);
      nbt.setInteger("TicksRemaining", this.ticksRemaining);
      nbt.setString("OwnerName", this.ownerName);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound nbt) {
      super.readEntityFromNBT(nbt);
      this.ticksRemaining = nbt.getInteger("TicksRemaining");
      this.ownerName = nbt.getString("OwnerName");
   }

   @Override
   protected void dropFewItems(boolean par1, int par2) {}
}
