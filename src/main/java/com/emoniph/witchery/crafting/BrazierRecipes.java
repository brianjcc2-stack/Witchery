package com.emoniph.witchery.crafting;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBrazier;
import com.emoniph.witchery.blocks.BlockWitchCrop;
import com.emoniph.witchery.entity.EntityBanshee;
import com.emoniph.witchery.entity.EntityPoltergeist;
import com.emoniph.witchery.entity.EntitySpectre;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.BlockUtil;
import com.emoniph.witchery.util.Const;
import com.emoniph.witchery.util.CreatureUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.util.Coord;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class BrazierRecipes {

   private static final double POLTERGEIST_CHANCE = 0.05D;
   private static final BrazierRecipes INSTANCE = new BrazierRecipes();
   public final ArrayList recipes = new ArrayList();
   public static final BrazierRecipes.BrazierRecipe SPECTRES = new BrazierRecipes.BrazierRecipe("witchery.brazier.spectre", true, TimeUtil.secsToTicks(30), new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), Witchery.Items.GENERIC.itemBatWool.createStack(), Witchery.Items.GENERIC.itemGraveyardDust.createStack()}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(TimeUtil.secondsElapsed(5, ticks)) {
            ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, 0.5D + (double)x, 1.0D + (double)y, 0.5D + (double)z, 0.5D, 1.0D, 16);
         }

         return 0;
      }
      public void onBurnt(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         EntityCreature creature = Infusion.spawnCreature(world, EntitySpectre.class, x, y, z, (EntityLivingBase)null, 1, 2, ParticleEffect.INSTANT_SPELL, SoundEffect.NOTE_HARP);
         CreatureUtil.spawnWithEgg(creature, true);
         if(world.rand.nextDouble() < 0.05D) {
            EntityCreature poltergeist = Infusion.spawnCreature(world, EntityPoltergeist.class, x, y, z, (EntityLivingBase)null, 6, 10, ParticleEffect.MOB_SPELL, (SoundEffect)null);
            CreatureUtil.spawnWithEgg(poltergeist, true);
         }

      }
   };
   public static final BrazierRecipes.BrazierRecipe BANSHEES = new BrazierRecipes.BrazierRecipe("witchery.brazier.banshee", true, TimeUtil.secsToTicks(30), new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), Witchery.Items.GENERIC.itemCondensedFear.createStack(), Witchery.Items.GENERIC.itemGraveyardDust.createStack()}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(TimeUtil.secondsElapsed(5, ticks)) {
            ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, 0.5D + (double)x, 1.0D + (double)y, 0.5D + (double)z, 0.5D, 1.0D, 16);
         }

         return 0;
      }
      public void onBurnt(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         EntityCreature creature = Infusion.spawnCreature(world, EntityBanshee.class, x, y, z, (EntityLivingBase)null, 1, 2, ParticleEffect.INSTANT_SPELL, SoundEffect.NOTE_HARP);
         CreatureUtil.spawnWithEgg(creature, true);
         if(world.rand.nextDouble() < 0.05D) {
            EntityCreature poltergeist = Infusion.spawnCreature(world, EntityPoltergeist.class, x, y, z, (EntityLivingBase)null, 6, 10, ParticleEffect.MOB_SPELL, (SoundEffect)null);
            CreatureUtil.spawnWithEgg(poltergeist, true);
         }

      }
   };
   public static final BrazierRecipes.BrazierRecipe POLTERGEIST = new BrazierRecipes.BrazierRecipe("witchery.brazier.poltergeist", false, TimeUtil.secsToTicks(45), new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), Witchery.Items.GENERIC.itemRefinedEvil.createStack(), Witchery.Items.GENERIC.itemFocusedWill.createStack()}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(TimeUtil.secondsElapsed(5, ticks)) {
            ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, 0.5D + (double)x, 1.0D + (double)y, 0.5D + (double)z, 0.5D, 1.0D, 16);
         }

         return 0;
      }
      public void onBurnt(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         EntityCreature creature = Infusion.spawnCreature(world, EntityPoltergeist.class, x, y, z, (EntityLivingBase)null, 1, 2, ParticleEffect.INSTANT_SPELL, SoundEffect.NOTE_HARP);
         CreatureUtil.spawnWithEgg(creature, true);
         if(world.rand.nextDouble() < 0.05D) {
            EntityCreature poltergeist = Infusion.spawnCreature(world, EntityPoltergeist.class, x, y, z, (EntityLivingBase)null, 6, 10, ParticleEffect.MOB_SPELL, (SoundEffect)null);
            CreatureUtil.spawnWithEgg(poltergeist, true);
         }

      }
   };
   public static final BrazierRecipes.BrazierRecipe SMOKE = new BrazierRecipes.BrazierRecipe("witchery.brazier.smoke", true, -1, new ItemStack[]{new ItemStack(Items.gunpowder), Witchery.Items.GENERIC.itemQuicklime.createStack(), new ItemStack(Items.glowstone_dust)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         ParticleEffect.EXPLODE.send(SoundEffect.NONE, world, 0.5D + (double)x, (double)y, 0.5D + (double)z, 16.0D, 4.0D, 64);
         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe STRONG = new BrazierRecipes.BrazierRecipe("witchery.brazier.strong", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack(), new ItemStack(Items.bone), new ItemStack(Items.blaze_powder)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(TimeUtil.secondsElapsed(3, ticks)) {
            boolean radius = true;
            boolean radiusSq = true;
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(0.5D + (double)x - 4.0D, (double)(y - 4), 0.5D + (double)z - 4.0D, 0.5D + (double)x + 4.0D, (double)(y + 4), 0.5D + (double)z + 4.0D);
            List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            Iterator i$ = entities.iterator();

            while(i$.hasNext()) {
               Object obj = i$.next();
               EntityLivingBase entity = (EntityLivingBase)obj;
               if(entity.getDistanceSq(0.5D + (double)x, (double)y, 0.5D + (double)z) <= 16.0D) {
                  entity.addPotionEffect(new PotionEffect(Potion.damageBoost.id, TimeUtil.secsToTicks(6), 0));
               }
            }
         }

         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe TOUGH = new BrazierRecipes.BrazierRecipe("witchery.brazier.tough", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack(), new ItemStack(Items.rotten_flesh), new ItemStack(Items.blaze_powder)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(TimeUtil.secondsElapsed(3, ticks)) {
            boolean radius = true;
            boolean radiusSq = true;
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(0.5D + (double)x - 4.0D, (double)(y - 4), 0.5D + (double)z - 4.0D, 0.5D + (double)x + 4.0D, (double)(y + 4), 0.5D + (double)z + 4.0D);
            List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            Iterator i$ = entities.iterator();

            while(i$.hasNext()) {
               Object obj = i$.next();
               EntityLivingBase entity = (EntityLivingBase)obj;
               if(entity.getDistanceSq(0.5D + (double)x, (double)y, 0.5D + (double)z) <= 16.0D) {
                  entity.addPotionEffect(new PotionEffect(Potion.resistance.id, TimeUtil.secsToTicks(6), 0));
               }
            }
         }

         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe INVISIBLE = new BrazierRecipes.BrazierRecipe("witchery.brazier.invisible", true, -1, new ItemStack[]{new ItemStack(Items.ender_pearl), new ItemStack(Items.spider_eye), new ItemStack(Items.blaze_rod)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(TimeUtil.secondsElapsed(3, ticks)) {
            boolean radius = true;
            boolean radiusSq = true;
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(0.5D + (double)x - 6.0D, (double)(y - 6), 0.5D + (double)z - 6.0D, 0.5D + (double)x + 6.0D, (double)(y + 6), 0.5D + (double)z + 6.0D);
            List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            Iterator i$ = entities.iterator();

            while(i$.hasNext()) {
               Object obj = i$.next();
               EntityLivingBase entity = (EntityLivingBase)obj;
               if(entity.getDistanceSq(0.5D + (double)x, (double)y, 0.5D + (double)z) <= 36.0D) {
                  entity.addPotionEffect(new PotionEffect(Potion.invisibility.id, TimeUtil.secsToTicks(6), 0));
               }
            }
         }

         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe WILTING = new BrazierRecipes.BrazierRecipe("witchery.brazier.wilting", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemCondensedFear.createStack(), Witchery.Items.GENERIC.itemWormyApple.createStack(), Witchery.Items.GENERIC.itemGraveyardDust.createStack()}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(ticks % 5L == 0L) {
            int offsetY = (int)(ticks % 30L) / 5;
            boolean radius = true;
            boolean diameter = true;
            int posX = x - 3 + world.rand.nextInt(7);
            int posZ = z - 3 + world.rand.nextInt(7);
            int posY = y - 2 + offsetY;
            Block block = BlockUtil.getBlock(world, posX, posY, posZ);
            if(block != null && block instanceof IPlantable) {
               IPlantable plantable = (IPlantable)block;
               if(block instanceof BlockWitchCrop || plantable.getPlantType(world, posX, posY, posZ) == EnumPlantType.Crop) {
                  int currentMeta = plantable.getPlantMetadata(world, posX, posY, posZ);
                  if(currentMeta > 0) {
                     BlockUtil.setMetadata(world, posX, posY, posZ, currentMeta - 1);
                     ParticleEffect.MOB_SPELL.send(SoundEffect.NONE, world, 0.5D + (double)x, 1.0D + (double)y, 0.5D + (double)z, 0.3D, 0.5D, 8);
                     this.healNearbyUndead(world, x, y, z, 3);
                     return 2;
                  }
               }
            }
         }

         return 0;
      }
      private void healNearbyUndead(World world, int x, int y, int z, int radius) {
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - radius), (double)(y - radius), (double)(z - radius), (double)(x + radius), (double)(y + radius), (double)(z + radius));
         List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
         Iterator i$ = entities.iterator();

         while(i$.hasNext()) {
            Object obj = i$.next();
            EntityLivingBase entity = (EntityLivingBase)obj;
            if(CreatureUtil.isUndead(entity)) {
               float maxHealth = entity.getMaxHealth();
               if(entity.getHealth() < maxHealth) {
                  entity.heal(maxHealth * 0.1F);
                  ParticleEffect.HEART.send(SoundEffect.NONE, entity, 0.5D, 1.0D, 8);
               }
            }
         }

      }
      public boolean getNeedsPower() {
         return false;
      }
   };
   public static final BrazierRecipes.BrazierRecipe INFUSION = new BrazierRecipes.BrazierRecipe("witchery.brazier.infusion", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemAttunedStoneCharged.createStack(), new ItemStack(Items.ghast_tear), Witchery.Items.GENERIC.itemGraveyardDust.createStack()}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(ticks % 10L == 0L) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 4), (double)(y - 4), (double)(z - 4), (double)(x + 4), (double)(y + 4), (double)(z + 4));
            List entities = world.getEntitiesWithinAABB(EntityPlayer.class, bb);
            Iterator i$ = entities.iterator();

            while(i$.hasNext()) {
               Object obj = i$.next();
               EntityPlayer player = (EntityPlayer)obj;
               if(Infusion.getInfusionID(player) > 0) {
                  int currentEnergy = Infusion.getCurrentEnergy(player);
                  int maxEnergy = Infusion.getMaxEnergy(player);
                  if(currentEnergy < maxEnergy) {
                     Infusion.setCurrentEnergy(player, Math.min(currentEnergy + 15, maxEnergy));
                     ParticleEffect.SPELL.send(SoundEffect.NONE, player, 0.5D, 1.0D, 8);
                  }
               }
            }
         }
         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe GROWTH = new BrazierRecipes.BrazierRecipe("witchery.brazier.growth", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.apple)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(ticks % 10L == 0L) {
            int px = x - 5 + world.rand.nextInt(11);
            int pz = z - 5 + world.rand.nextInt(11);
            int py = y - 2 + world.rand.nextInt(5);
            Block block = world.getBlock(px, py, pz);
            if(block != null && block instanceof net.minecraft.block.IGrowable) {
               net.minecraft.block.IGrowable growable = (net.minecraft.block.IGrowable)block;
               if(growable.func_149851_a(world, px, py, pz, world.isRemote)) {
                  growable.func_149853_b(world, world.rand, px, py, pz);
                  world.playAuxSFX(2005, px, py, pz, 0);
               }
            }
         }
         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe MAGNET = new BrazierRecipes.BrazierRecipe("witchery.brazier.magnet", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), new ItemStack(Items.iron_ingot), new ItemStack(Items.redstone)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 8), (double)(y - 8), (double)(z - 8), (double)(x + 8), (double)(y + 8), (double)(z + 8));
         List entities = world.getEntitiesWithinAABB(net.minecraft.entity.item.EntityItem.class, bb);
         for(Object obj : entities) {
            net.minecraft.entity.item.EntityItem item = (net.minecraft.entity.item.EntityItem)obj;
            double dx = ((double)x + 0.5D) - item.posX;
            double dy = ((double)y + 0.5D) - item.posY;
            double dz = ((double)z + 0.5D) - item.posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if(dist > 1.0D && dist < 8.0D) {
               item.motionX += dx / dist * 0.05D;
               item.motionY += dy / dist * 0.05D;
               item.motionZ += dz / dist * 0.05D;
            }
         }
         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe STORM = new BrazierRecipes.BrazierRecipe("witchery.brazier.storm", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), new ItemStack(Items.ghast_tear), new ItemStack(Items.water_bucket)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if(!world.isRemote && ticks % 100L == 0L) {
             net.minecraft.world.storage.WorldInfo info = world.getWorldInfo();
             info.setRaining(true);
             info.setThundering(true);
             info.setRainTime(1200);
             info.setThunderTime(1200);
         }
         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe REPELLENT = new BrazierRecipes.BrazierRecipe("witchery.brazier.repellent", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), new ItemStack(Items.rotten_flesh), new ItemStack(Items.bone)}, null) {
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 8), (double)(y - 8), (double)(z - 8), (double)(x + 8), (double)(y + 8), (double)(z + 8));
         List entities = world.getEntitiesWithinAABB(net.minecraft.entity.monster.IMob.class, bb);
         for(Object obj : entities) {
            EntityLivingBase mob = (EntityLivingBase)obj;
            double dx = mob.posX - ((double)x + 0.5D);
            double dz = mob.posZ - ((double)z + 0.5D);
            double dist = Math.sqrt(dx * dx + dz * dz);
            if(dist < 8.0D && dist > 0.1D) {
               mob.motionX += dx / dist * 0.1D;
               mob.motionZ += dz / dist * 0.1D;
            }
         }
         return 0;
      }
   };
   public static final BrazierRecipes.BrazierRecipe POTION_AURA = new BrazierRecipes.BrazierRecipe("witchery.brazier.potionaura", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemWormwood.createStack(), Witchery.Items.GENERIC.itemGraveyardDust.createStack(), new ItemStack(Items.potionitem)}, null) {
      @Override
      protected boolean isMatch(ItemStack[] availableItems) {
         boolean hasWormwood = false;
         boolean hasDust = false;
         boolean hasPotion = false;
         for (ItemStack item : availableItems) {
             if (item != null) {
                 if (item.isItemEqual(Witchery.Items.GENERIC.itemWormwood.createStack())) hasWormwood = true;
                 else if (item.isItemEqual(Witchery.Items.GENERIC.itemGraveyardDust.createStack())) hasDust = true;
                 else if ((item.getItem() == Items.potionitem && item.getItemDamage() > 0) || item.getItem() == Witchery.Items.BREW) hasPotion = true;
             }
         }
         return hasWormwood && hasDust && hasPotion;
      }

      @Override
      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         if (ticks % 60L == 0L) {
             ItemStack potionStack = null;
             for (int i = 0; i < 3; i++) {
                 ItemStack stack = tile.getStackInSlot(i);
                 if (stack != null && (stack.getItem() == Items.potionitem || stack.getItem() == Witchery.Items.BREW)) {
                     potionStack = stack;
                     break;
                 }
             }
             if (potionStack != null) {
                 AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 6), (double)(y - 6), (double)(z - 6), (double)(x + 6), (double)(y + 6), (double)(z + 6));
                 List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
                 if (potionStack.getItem() == Items.potionitem) {
                     List effects = Items.potionitem.getEffects(potionStack);
                     if (effects != null && !effects.isEmpty()) {
                         PotionEffect effect = (PotionEffect)effects.get(0);
                         for (Object obj : entities) {
                             EntityLivingBase entity = (EntityLivingBase)obj;
                             entity.addPotionEffect(new PotionEffect(effect.getPotionID(), TimeUtil.secsToTicks(60), effect.getAmplifier()));
                         }
                     }
                 } else if (potionStack.getItem() == Witchery.Items.BREW) {
                     com.emoniph.witchery.brewing.ModifiersEffect modifiers = new com.emoniph.witchery.brewing.ModifiersEffect(1.0D, 1.0D, false, new com.emoniph.witchery.util.EntityPosition(x, y, z), false, 0, (EntityPlayer)null);
                     for (Object obj : entities) {
                         EntityLivingBase entity = (EntityLivingBase)obj;
                         com.emoniph.witchery.brewing.WitcheryBrewRegistry.INSTANCE.applyToEntity(world, entity, potionStack.getTagCompound(), modifiers);
                     }
                 }
             }
         }
         return 0;
      }
   };


    public static final BrazierRecipes.BrazierRecipe WARDING = new BrazierRecipes.BrazierRecipe("witchery.brazier.warding", true, -1, new ItemStack[]{Witchery.Items.GENERIC.itemRowanBerries.createStack(), Witchery.Items.GENERIC.itemWormwood.createStack(), Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack()}, null) {
       @Override
       protected boolean isMatch(ItemStack[] availableItems) {
          boolean hasBerries = false;
          boolean hasWormwood = false;
          boolean hasPurity = false;
          for (ItemStack item : availableItems) {
             if (item != null) {
                if (item.isItemEqual(Witchery.Items.GENERIC.itemRowanBerries.createStack()) || item.isItemEqual(Witchery.Items.GENERIC.itemBranchEnt.createStack())) hasBerries = true;
                else if (item.isItemEqual(Witchery.Items.GENERIC.itemWormwood.createStack())) hasWormwood = true;
                else if (item.isItemEqual(Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack()) || item.getItem() == Items.ghast_tear || item.isItemEqual(Witchery.Items.GENERIC.itemPurifiedMilk.createStack())) hasPurity = true;
             }
          }
          return hasBerries && hasWormwood && hasPurity;
       }

       @Override
       public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
          BlockBrazier.TileEntityBrazier.WARDED_BRAZIERS.add(new Coord(x, y, z));
          if (!world.isRemote) {
             if (ticks % 20L == 0L) {
                for (int dx = -16; dx <= 16; dx += 4) {
                   for (int dy = -4; dy <= 6; dy += 2) {
                      for (int dz = -16; dz <= 16; dz += 4) {
                         if (world.getBlock(x + dx, y + dy, z + dz) == Blocks.fire) {
                            world.setBlockToAir(x + dx, y + dy, z + dz);
                         }
                      }
                   }
                }
             }

             AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 24), (double)(y - 10), (double)(z - 24), (double)(x + 24), (double)(y + 10), (double)(z + 24));

             List creepers = world.getEntitiesWithinAABB(EntityCreeper.class, bb);
             for (Object obj : creepers) {
                EntityCreeper c = (EntityCreeper)obj;
                c.setCreeperState(-1);
             }

             List mobs = world.getEntitiesWithinAABB(net.minecraft.entity.monster.IMob.class, bb);
             for (Object obj : mobs) {
                if (obj instanceof EntityLivingBase && !(obj instanceof EntityCovenWitch)) {
                   EntityLivingBase mob = (EntityLivingBase)obj;
                   mob.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 2));
                   mob.addPotionEffect(new PotionEffect(Potion.blindness.id, 60, 0));
                   mob.addPotionEffect(new PotionEffect(Potion.weakness.id, 60, 1));
                   if (ticks % 40L == 0L) {
                      mob.attackEntityFrom(DamageSource.magic, 2.0F);
                      ParticleEffect.FLAME.send(SoundEffect.FIRE_IGNITE, world, mob.posX, mob.posY + 1.0D, mob.posZ, 0.5D, 0.5D, 5);
                   }
                }
             }

             if (ticks % 100L == 0L && tile.owner != null && !tile.owner.isEmpty()) {
                List players = world.getEntitiesWithinAABB(EntityPlayer.class, bb);
                for (Object obj : players) {
                   EntityPlayer p = (EntityPlayer)obj;
                   if (!p.getCommandSenderName().equals(tile.owner) && !p.capabilities.isCreativeMode) {
                      EntityPlayer ownerPlayer = world.getPlayerEntityByName(tile.owner);
                      if (ownerPlayer != null) {
                         ownerPlayer.addChatMessage(new ChatComponentText("\u00a7c\u00a7l[Santuario]\u00a7r \u00a7e\u00a1Intruso detectado en tu base por el humo del brasero: \u00a7f" + p.getCommandSenderName() + "\u00a7e!\u00a7r"));
                         SoundEffect.NOTE_HARP.playAtPlayer(world, ownerPlayer);
                      }
                      p.addChatMessage(new ChatComponentText("\u00a74\u00a7oHas entrado a un santuario protegido por un brasero m\u00edstico...\u00a7r"));
                      SoundEffect.NOTE_SNARE.playAtPlayer(world, p);
                   }
                }
             }
          } else if (ticks % 10L == 0L) {
             ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, (double)x + 0.5D, (double)y + 1.2D, (double)z + 0.5D, 0.5D, 1.0D, 6);
          }
          return 0;
       }
    };

    public static final BrazierRecipes.BrazierRecipe LUNAR_MIST = new BrazierRecipes.BrazierRecipe("witchery.brazier.lunarmist", true, -1, new ItemStack[]{new ItemStack(Witchery.Blocks.MOON_ROSE), Witchery.Items.GENERIC.itemWormwood.createStack(), Witchery.Items.GENERIC.itemEnderDew.createStack()}, null) {
       @Override
       public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
          if(ticks % 20L == 0L) {
             AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 16), (double)(y - 8), (double)(z - 16), (double)(x + 16), (double)(y + 8), (double)(z + 16));
             List players = world.getEntitiesWithinAABB(EntityPlayer.class, bb);
             for(Object obj : players) {
                EntityPlayer player = (EntityPlayer)obj;
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 240, 0, true));
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100, 0, true));
             }
             ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, (double)x + 0.5D, (double)y + 1.2D, (double)z + 0.5D, 0.5D, 1.0D, 8);
          }
          return 0;
       }
    };

    public static final BrazierRecipes.BrazierRecipe SOLAR_PURGE = new BrazierRecipes.BrazierRecipe("witchery.brazier.solarpurge", true, -1, new ItemStack[]{new ItemStack(Witchery.Blocks.SUN_ROSE), Witchery.Items.GENERIC.itemRowanBerries.createStack(), new ItemStack(Items.glowstone_dust)}, null) {
       @Override
       public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
          if(ticks % 20L == 0L) {
             AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 16), (double)(y - 8), (double)(z - 16), (double)(x + 16), (double)(y + 8), (double)(z + 16));
             List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
             for(Object obj : entities) {
                EntityLivingBase living = (EntityLivingBase)obj;
                if(living.getCreatureAttribute() == net.minecraft.entity.EnumCreatureAttribute.UNDEAD) {
                   living.setFire(3);
                   living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 1));
                }
             }
             ParticleEffect.FLAME.send(SoundEffect.NONE, world, (double)x + 0.5D, (double)y + 1.2D, (double)z + 0.5D, 0.5D, 1.0D, 10);
          }
          return 0;
       }
    };

    public static final BrazierRecipes.BrazierRecipe SOUL_REAP = new BrazierRecipes.BrazierRecipe("witchery.brazier.soulreap", true, -1, new ItemStack[]{new ItemStack(Witchery.Blocks.GRAVE_ROSE), Witchery.Items.GENERIC.itemGraveyardDust.createStack(), Witchery.Items.GENERIC.itemFoulFume.createStack()}, null) {
       @Override
       public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
          if(ticks % 20L == 0L) {
             AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 16), (double)(y - 8), (double)(z - 16), (double)(x + 16), (double)(y + 8), (double)(z + 16));
             List mobs = world.getEntitiesWithinAABB(net.minecraft.entity.monster.IMob.class, bb);
             for(Object obj : mobs) {
                if(obj instanceof EntityLivingBase) {
                   EntityLivingBase living = (EntityLivingBase)obj;
                   living.addPotionEffect(new PotionEffect(Potion.weakness.id, 60, 0));
                   living.addPotionEffect(new PotionEffect(Potion.wither.id, 60, 0));
                }
             }
             ParticleEffect.PORTAL.send(SoundEffect.NONE, world, (double)x + 0.5D, (double)y + 1.2D, (double)z + 0.5D, 0.5D, 1.0D, 8);
          }
          return 0;
       }
    };


   public static BrazierRecipes instance() {
      return INSTANCE;
   }

   public BrazierRecipes.BrazierRecipe getRecipe(ItemStack[] availableItems) {
      Iterator i$ = this.recipes.iterator();

      BrazierRecipes.BrazierRecipe recipe;
      do {
         if(!i$.hasNext()) {
            return null;
         }

         recipe = (BrazierRecipes.BrazierRecipe)i$.next();
      } while(!recipe.isMatch(availableItems));

      return recipe;
   }


   public static class BrazierRecipe {

      public final int burnTicks;
      public final ItemStack[] items;
      public final boolean inBook;
      public String unlocalizedName;


      private BrazierRecipe(String unlocalizedName, boolean inBook, int burnTicks, ItemStack ... ingredients) {
         this.items = ingredients;
         this.burnTicks = burnTicks;
         this.unlocalizedName = unlocalizedName;
         this.inBook = inBook;
         BrazierRecipes.INSTANCE.recipes.add(this);
      }

      public boolean getNeedsPower() {
         return true;
      }

      public ArrayList getMutableModifiersList() {
         ArrayList available = new ArrayList();
         ItemStack[] arr$ = this.items;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ItemStack item = arr$[i$];
            if(item != null) {
               available.add(item);
            }
         }

         return available;
      }

      protected boolean isMatch(ItemStack[] availableItems) {
         ArrayList availableItemList = new ArrayList();
         ItemStack[] arr$ = availableItems;
         int len$ = availableItems.length;

         int i$;
         ItemStack neededItem;
         for(i$ = 0; i$ < len$; ++i$) {
            neededItem = arr$[i$];
            if(neededItem != null) {
               availableItemList.add(neededItem);
            }
         }

         arr$ = this.items;
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            neededItem = arr$[i$];
            int index = this.indexOf(availableItemList, neededItem);
            if(index == -1) {
               return false;
            }

            availableItemList.remove(index);
         }

         return true;
      }

      private int indexOf(ArrayList list, ItemStack item) {
         for(int i = 0; i < list.size(); ++i) {
            if(((ItemStack)list.get(i)).isItemEqual(item)) {
               return i;
            }
         }

         return -1;
      }

      public boolean uses(ItemStack ingredient) {
         if(ingredient == null) {
            return false;
         } else {
            ItemStack[] arr$ = this.items;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               ItemStack item = arr$[i$];
               if(item != null && item.isItemEqual(ingredient)) {
                  return true;
               }
            }

            return false;
         }
      }

      public String getDescription() {
         StringBuffer sb = new StringBuffer();
         sb.append("§n");
         sb.append(Witchery.resource(this.unlocalizedName + ".name"));
         sb.append("§r");
         sb.append(Const.BOOK_NEWLINE);
         sb.append(Const.BOOK_NEWLINE);
         String description = Witchery.resource(this.unlocalizedName + ".desc");
         if(!description.equals(this.unlocalizedName + ".desc")) {
            sb.append(description);
            sb.append(Const.BOOK_NEWLINE);
            sb.append(Const.BOOK_NEWLINE);
         }

         ItemStack[] arr$ = this.items;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ItemStack stack = arr$[i$];
            sb.append("§8>§0 ");
            if(stack.getItem() == Item.getItemFromBlock(Blocks.red_mushroom)) {
               sb.append(Witchery.resource("witchery.book.mushroomred"));
            } else if(stack.getItem() == Item.getItemFromBlock(Blocks.brown_mushroom)) {
               sb.append(Witchery.resource("witchery.book.mushroombrown"));
            } else if(stack.getItem() == Items.potionitem) {
               List list = Items.potionitem.getEffects(stack);
               if(list != null && !list.isEmpty()) {
                  PotionEffect effect = (PotionEffect)list.get(0);
                  String s = stack.getDisplayName();
                  if(effect.getAmplifier() > 0) {
                     s = s + " " + StatCollector.translateToLocal("potion.potency." + effect.getAmplifier()).trim();
                  }

                  if(effect.getDuration() > 20) {
                     s = s + " (" + Potion.getDurationString(effect) + ")";
                  }

                  sb.append(s);
               } else {
                  sb.append(stack.getDisplayName());
               }
            } else {
               sb.append(stack.getDisplayName());
            }

            sb.append(Const.BOOK_NEWLINE);
         }

         return sb.toString();
      }

      public int onBurning(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {
         return 0;
      }

      public void onBurnt(World world, int x, int y, int z, long ticks, BlockBrazier.TileEntityBrazier tile) {}

      // $FF: synthetic method
      BrazierRecipe(String x0, boolean x1, int x2, ItemStack[] x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }
}
