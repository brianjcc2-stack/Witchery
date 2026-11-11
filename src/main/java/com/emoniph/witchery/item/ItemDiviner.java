package com.emoniph.witchery.item;

import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.infusion.infusions.InfusionOtherwhere;
import com.emoniph.witchery.item.ItemBase;
import com.emoniph.witchery.util.BlockSide;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkPosition;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemDiviner extends ItemBase {

   public static final int MODE_ELEMENTS = 0;
   public static final int MODE_COVEN = 1;
   public static final int MODE_STRUCTURES = 2;

   private static final int MAX_DAMAGE = 50;
   private static final int DAMAGE_PER_USE = 1;
   private final Block blockToDetect;

   public ItemDiviner(Block blockToDetect) {
      this.blockToDetect = blockToDetect;
      this.setMaxStackSize(1);
      this.setMaxDamage(50);
   }

   public int getMode(ItemStack stack) {
      if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("DivinerMode")) {
         return stack.getTagCompound().getInteger("DivinerMode");
      }
      return MODE_ELEMENTS;
   }

   public void setMode(ItemStack stack, int mode) {
      if (stack != null) {
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }
         stack.getTagCompound().setInteger("DivinerMode", mode);
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      int mode = this.getMode(stack);
      String modeName = StatCollector.translateToLocal("witchery.diviner.mode." + mode);
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.diviner.mode.label") + ": " + EnumChatFormatting.AQUA + modeName);
      list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("witchery.diviner.modeswitch.tip"));
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return EnumRarity.uncommon;
   }

   public EnumAction getItemUseAction(ItemStack itemstack) {
      return EnumAction.bow;
   }

   public int getMaxItemUseDuration(ItemStack itemstack) {
      return 400;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
      if (player.isSneaking()) {
         if (!world.isRemote) {
            int nextMode = (this.getMode(itemstack) + 1) % 3;
            this.setMode(itemstack, nextMode);
            String modeName = StatCollector.translateToLocal("witchery.diviner.mode." + nextMode);
            ChatUtil.sendTranslated(EnumChatFormatting.LIGHT_PURPLE, player, "witchery.diviner.mode.changed", modeName);
            SoundEffect.RANDOM_ORB.playOnlyTo(player, 1.0F, 1.4F);
         }
         return itemstack;
      }

      player.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
      return itemstack;
   }

   @Override
   public void onUsingTick(ItemStack itemstack, EntityPlayer player, int countdown) {
      World world = player.worldObj;
      if (!world.isRemote) {
         int elapsedTicks = this.getMaxItemUseDuration(itemstack) - countdown;
         int mode = this.getMode(itemstack);

         if (mode == MODE_COVEN) {
            this.doCovenDivination(itemstack, player, world, elapsedTicks);
         } else if (mode == MODE_STRUCTURES) {
            this.doStructureDivination(itemstack, player, world, elapsedTicks);
         } else {
            this.doElementsDivination(itemstack, player, world, elapsedTicks);
         }
      }
   }

   private void doElementsDivination(ItemStack itemstack, EntityPlayer player, World world, int elapsedTicks) {
      MovingObjectPosition mop = InfusionOtherwhere.doCustomRayTrace(world, player, true, 6.0D);
      if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK || !BlockSide.TOP.isEqual(mop.sideHit)) {
         SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
         player.clearItemInUse();
         return;
      }

      int posX = MathHelper.floor_double((double) mop.blockX);
      int posY = MathHelper.floor_double((double) mop.blockY) - elapsedTicks;
      int posZ = MathHelper.floor_double((double) mop.blockZ);
      Block block = world.getBlock(posX, posY, posZ);
      boolean foundBlock = false;
      boolean foundSomething = false;
      if (block == this.blockToDetect) {
         foundBlock = true;
         foundSomething = true;
      } else if (block == Blocks.bedrock) {
         foundBlock = false;
         foundSomething = true;
      }

      if (foundSomething || posY <= 1) {
         if (foundBlock) {
            ParticleEffect.MAGIC_CRIT.send(SoundEffect.RANDOM_ORB, world, 0.5D + (double) mop.blockX, (double) (mop.blockY + 1), 0.5D + (double) mop.blockZ, 0.5D, 0.5D, 8);
         } else {
            ParticleEffect.SMOKE.send(SoundEffect.NOTE_SNARE, world, 0.5D + (double) mop.blockX, (double) (mop.blockY + 1), 0.5D + (double) mop.blockZ, 0.5D, 0.5D, 8);
         }

         player.clearItemInUse();
         itemstack.damageItem(1, player);
      }
   }

   private void doCovenDivination(ItemStack itemstack, EntityPlayer player, World world, int elapsedTicks) {
      if (elapsedTicks % 5 == 0) {
         ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 3);
      }

      if (elapsedTicks >= 25) {
         Entity targetWitch = null;
         double minDistanceSq = Double.MAX_VALUE;

         for (Object obj : world.loadedEntityList) {
            if (obj instanceof EntityCovenWitch) {
               EntityCovenWitch cw = (EntityCovenWitch) obj;
               if (!cw.isTamed()) {
                  double dSq = player.getDistanceSqToEntity(cw);
                  if (dSq < minDistanceSq) {
                     minDistanceSq = dSq;
                     targetWitch = cw;
                  }
               }
            }
         }

         if (targetWitch == null) {
            for (Object obj : world.loadedEntityList) {
               if (obj instanceof EntityWitch && !(obj instanceof EntityCovenWitch)) {
                  EntityWitch w = (EntityWitch) obj;
                  double dSq = player.getDistanceSqToEntity(w);
                  if (dSq < minDistanceSq) {
                     minDistanceSq = dSq;
                     targetWitch = w;
                  }
               }
            }
         }

         if (targetWitch != null && minDistanceSq <= 256.0D * 256.0D) {
            double dist = Math.sqrt(minDistanceSq);
            double dx = targetWitch.posX - player.posX;
            double dz = targetWitch.posZ - player.posZ;
            String dir = getDirectionName(dx, dz);

            double nx = dist > 0.001D ? dx / dist : 0.0D;
            double nz = dist > 0.001D ? dz / dist : 0.0D;
            for (int i = 1; i <= 6; ++i) {
               ParticleEffect.SPELL.send(SoundEffect.NONE, world, player.posX + nx * (double) i * 0.8D, player.posY + 1.2D, player.posZ + nz * (double) i * 0.8D, 0.1D, 0.1D, 2);
            }
            SoundEffect.RANDOM_ORB.playOnlyTo(player, 1.0F, 1.2F);
            ChatUtil.sendTranslated(EnumChatFormatting.LIGHT_PURPLE, player, "witchery.diviner.coven.found", (int) dist, dir);
         } else {
            ParticleEffect.SMOKE.send(SoundEffect.NOTE_SNARE, world, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 6);
            ChatUtil.sendTranslated(EnumChatFormatting.GRAY, player, "witchery.diviner.coven.notfound");
         }

         player.clearItemInUse();
         itemstack.damageItem(1, player);
      }
   }

   private void doStructureDivination(ItemStack itemstack, EntityPlayer player, World world, int elapsedTicks) {
      if (elapsedTicks % 5 == 0) {
         ParticleEffect.MAGIC_CRIT.send(SoundEffect.NONE, world, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 3);
      }

      if (elapsedTicks >= 30) {
         ChunkPosition pos = world.findClosestStructure("SwampHut", (int) player.posX, (int) player.posY, (int) player.posZ);
         if (pos != null) {
            double dx = (double) pos.chunkPosX - player.posX;
            double dz = (double) pos.chunkPosZ - player.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            String dir = getDirectionName(dx, dz);

            double nx = dist > 0.001D ? dx / dist : 0.0D;
            double nz = dist > 0.001D ? dz / dist : 0.0D;
            for (int i = 1; i <= 6; ++i) {
               ParticleEffect.MAGIC_CRIT.send(SoundEffect.NONE, world, player.posX + nx * (double) i * 0.8D, player.posY + 1.2D, player.posZ + nz * (double) i * 0.8D, 0.1D, 0.1D, 2);
            }
            SoundEffect.RANDOM_ORB.playOnlyTo(player, 1.0F, 1.0F);
            ChatUtil.sendTranslated(EnumChatFormatting.GOLD, player, "witchery.diviner.structure.found", (int) dist, dir);
         } else {
            ParticleEffect.SMOKE.send(SoundEffect.NOTE_SNARE, world, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 6);
            ChatUtil.sendTranslated(EnumChatFormatting.GRAY, player, "witchery.diviner.structure.notfound");
         }

         player.clearItemInUse();
         itemstack.damageItem(1, player);
      }
   }

   public static String getDirectionName(double dx, double dz) {
      double angle = Math.toDegrees(Math.atan2(dz, dx));
      if (angle >= -22.5D && angle < 22.5D) {
         return StatCollector.translateToLocal("witchery.dir.east");
      } else if (angle >= 22.5D && angle < 67.5D) {
         return StatCollector.translateToLocal("witchery.dir.southeast");
      } else if (angle >= 67.5D && angle < 112.5D) {
         return StatCollector.translateToLocal("witchery.dir.south");
      } else if (angle >= 112.5D && angle < 157.5D) {
         return StatCollector.translateToLocal("witchery.dir.southwest");
      } else if (angle >= -67.5D && angle < -22.5D) {
         return StatCollector.translateToLocal("witchery.dir.northeast");
      } else if (angle >= -112.5D && angle < -67.5D) {
         return StatCollector.translateToLocal("witchery.dir.north");
      } else if (angle >= -157.5D && angle < -112.5D) {
         return StatCollector.translateToLocal("witchery.dir.northwest");
      } else {
         return StatCollector.translateToLocal("witchery.dir.west");
      }
   }
}
