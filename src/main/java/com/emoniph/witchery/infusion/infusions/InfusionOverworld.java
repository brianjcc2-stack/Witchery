package com.emoniph.witchery.infusion.infusions;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.brewing.potions.PotionEnslaved;
import com.emoniph.witchery.common.ServerTickEvents;
import com.emoniph.witchery.entity.EntityTreefyd;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.InfusionOtherwhere;
import com.emoniph.witchery.util.Coord;
import com.emoniph.witchery.util.Dye;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class InfusionOverworld extends Infusion {

   public InfusionOverworld(int infusionID) {
      super(infusionID);
   }

   @Override
   public IIcon getPowerBarIcon(EntityPlayer player, int index) {
      return Blocks.leaves.getIcon(0, 0);
   }

   @Override
   public void onUpdate(ItemStack itemstack, World world, EntityPlayer player, int par4, boolean par5) {
      if(!world.isRemote && player.onGround && !player.isInWater() && !player.isRiding()) {
         int blockX = MathHelper.floor_double(player.posX);
         int blockY = MathHelper.floor_double(player.posY - 0.5D);
         int blockZ = MathHelper.floor_double(player.posZ);
         Block b = world.getBlock(blockX, blockY, blockZ);
         if(b == Blocks.grass || b == Blocks.dirt || b == Blocks.mycelium || b.getMaterial() == Material.leaves || b.getMaterial() == Material.plants || b.getMaterial() == Material.wood) {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 25, 0, true));
         }
      }
   }

   @Override
   public void onFalling(World world, EntityPlayer player, LivingFallEvent event) {
      if(event.distance > 3.0F) {
         int blockX = MathHelper.floor_double(player.posX);
         int blockY = MathHelper.floor_double(player.posY) - 1;
         int blockZ = MathHelper.floor_double(player.posZ);
         Block blockID = world.getBlock(blockX, blockY, blockZ);
         if(blockID == Blocks.grass || blockID == Blocks.dirt || blockID == Blocks.mycelium || blockID.getMaterial() == Material.leaves || blockID.getMaterial() == Material.plants || blockID == Blocks.hay_block) {
            if(this.consumeCharges(world, player, 2, false)) {
               event.distance = 0.0F;
               event.setCanceled(true);
               ParticleEffect.INSTANT_SPELL.send(SoundEffect.DIG_CLOTH, world, player.posX, player.posY, player.posZ, 0.5D, 0.5D, 8);
            }
         }
      }
   }

   @Override
   public void onLeftClickEntity(ItemStack itemstack, World world, EntityPlayer player, Entity otherEntity) {
      if(!world.isRemote && otherEntity instanceof EntityLivingBase) {
         EntityLivingBase target = (EntityLivingBase)otherEntity;
         if(!(target instanceof IBossDisplayData)) {
            long lastRoot = target.getEntityData().getLong("WITCLastRootTime");
            long now = world.getTotalWorldTime();
            if(now - lastRoot >= 120L) { // 6 segundos de cooldown anti-spam por objetivo
               if(this.consumeCharges(world, player, 4, true)) {
                  target.getEntityData().setLong("WITCLastRootTime", now);

                  // Efectos en la victima
                  target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 4, true));
                  target.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 80, 1, true));

                  // Deteccion del bioma local
                  int tx = MathHelper.floor_double(target.posX);
                  int ty = MathHelper.floor_double(target.posY);
                  int tz = MathHelper.floor_double(target.posZ);
                  BiomeGenBase biome = world.getBiomeGenForCoords(tx, tz);

                  Block leafBlock = Blocks.leaves;
                  int leafMeta = 0; // Oak
                  String biomeName = biome != null && biome.biomeName != null ? biome.biomeName.toLowerCase() : "";
                  if(biome != null && (biome.temperature < 0.3F || biomeName.contains("taiga") || biomeName.contains("spruce") || biomeName.contains("cold"))) {
                     leafMeta = 1; // Spruce
                  } else if(biomeName.contains("birch")) {
                     leafMeta = 2; // Birch
                  } else if(biomeName.contains("jungle")) {
                     leafMeta = 3; // Jungle
                  } else if(biomeName.contains("roof") || biomeName.contains("dark")) {
                     leafBlock = Blocks.leaves2;
                     leafMeta = 1; // Dark oak
                  } else if(biomeName.contains("savanna") || biomeName.contains("mesa")) {
                     leafBlock = Blocks.leaves2;
                     leafMeta = 0; // Acacia
                  }

                  // Colocar hojas temporales alrededor
                  List<Coord> placedLeaves = new ArrayList<Coord>();
                  int[][] offsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {0, 1, 0}};
                  for(int[] off : offsets) {
                     int lx = tx + off[0];
                     int ly = ty + off[1];
                     int lz = tz + off[2];
                     if(world.isAirBlock(lx, ly, lz) || world.getBlock(lx, ly, lz).getMaterial().isReplaceable()) {
                        world.setBlock(lx, ly, lz, leafBlock, leafMeta | 4, 3);
                        placedLeaves.add(new Coord(lx, ly, lz));
                     }
                  }

                  if(!placedLeaves.isEmpty()) {
                     ServerTickEvents.TASKS.add(new InfusionOverworld.TemporaryLeavesTask(world, placedLeaves, leafBlock, 80));
                  }

                  ParticleEffect.INSTANT_SPELL.send(SoundEffect.DIG_CLOTH, target, 1.0D, 1.0D, 16);
               }
            } else {
               SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
            }
         }
      }
   }

   @Override
   public void onUsingItemTick(ItemStack itemstack, World world, EntityPlayer player, int countdown) {
      if(!world.isRemote) {
         int elapsedTicks = this.getMaxItemUseDuration(itemstack) - countdown;
         if(elapsedTicks >= 20 && elapsedTicks % 10 == 0) {
            this.playSound(world, player, "random.orb");
         }
      }
   }

   @Override
   public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer player, int countdown) {
      if(!world.isRemote) {
         int elapsedTicks = this.getMaxItemUseDuration(itemstack) - countdown;
         MovingObjectPosition hit = InfusionOtherwhere.doCustomRayTrace(world, player, true, 16.0D);

         if(hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
            // Caso 1: Toque de Vida (Shift + Clic en cultivo/planta)
            if(player.isSneaking()) {
               if(ItemDye.applyBonemeal(Dye.BONE_MEAL.createStack(), world, hit.blockX, hit.blockY, hit.blockZ, player)) {
                  if(this.consumeCharges(world, player, 2, false)) {
                     world.playAuxSFX(2005, hit.blockX, hit.blockY, hit.blockZ, 0);
                     ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_POP, world, (double)hit.blockX + 0.5D, (double)hit.blockY + 0.5D, (double)hit.blockZ + 0.5D, 0.5D, 0.5D, 8);
                  }
                  return;
               }
            } else {
               Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);

               // Caso 2: Despertar Aliado de Madera (Canalizar >= 30 ticks en madera)
               if(elapsedTicks >= 30 && (block.getMaterial() == Material.wood)) {
                  List<EntityTreefyd> existing = world.getEntitiesWithinAABB(EntityTreefyd.class, AxisAlignedBB.getBoundingBox(player.posX - 40.0D, player.posY - 20.0D, player.posZ - 40.0D, player.posX + 40.0D, player.posY + 20.0D, player.posZ + 40.0D));
                  boolean hasAlly = false;
                  for(EntityTreefyd t : existing) {
                     if(t.isEntityAlive() && player.getCommandSenderName().equals(t.getOwnerName())) {
                        hasAlly = true;
                        break;
                     }
                  }

                  if(hasAlly) {
                     SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                     return;
                  }

                  if(this.consumeCharges(world, player, 10, true)) {
                     EntityTreefyd ally = new EntityTreefyd(world);
                     ally.setPosition((double)hit.blockX + 0.5D, (double)hit.blockY + 1.0D, (double)hit.blockZ + 0.5D);
                     ally.setOwner(player.getCommandSenderName());
                     ally.getEntityData().setInteger("WITCLifetime", 900); // 45 segundos
                     PotionEnslaved.setEnslaverForMob(ally, player);
                     ally.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
                     ally.setHealth(20.0F);
                     ally.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
                     world.spawnEntityInWorld(ally);
                     ParticleEffect.EXPLODE.send(SoundEffect.MOB_SILVERFISH_KILL, ally, 0.8D, 1.5D, 16);
                     return;
                  }
               }

               // Caso 3: Columna de Enredaderas Vertical (Paredes laterales)
               if(hit.sideHit >= 2 && hit.sideHit <= 5) {
                  long lastVine = player.getEntityData().getLong("WITCLastVineTime");
                  long now = world.getTotalWorldTime();
                  if(now - lastVine >= 30L) { // 1.5s cooldown anti-spam
                     int dx = 0;
                     int dz = 0;
                     byte meta = 0;
                     switch(hit.sideHit) {
                     case 2: // North
                        meta = 1;
                        dz = -1;
                        break;
                     case 3: // South
                        meta = 4;
                        dz = 1;
                        break;
                     case 4: // West
                        meta = 8;
                        dx = -1;
                        break;
                     case 5: // East
                        meta = 2;
                        dx = 1;
                        break;
                     }

                     if(this.consumeCharges(world, player, 2, true)) {
                        player.getEntityData().setLong("WITCLastVineTime", now);
                        int maxHeight = 24;
                        for(int h = 0; h < maxHeight; ++h) {
                           int ty = hit.blockY + h;
                           if(ty >= 256 || !world.getBlock(hit.blockX, ty, hit.blockZ).getMaterial().isSolid()) {
                              break;
                           }
                           int vx = hit.blockX + dx;
                           int vz = hit.blockZ + dz;
                           if(world.isAirBlock(vx, ty, vz) || world.getBlock(vx, ty, vz) == Blocks.vine) {
                              world.setBlock(vx, ty, vz, Blocks.vine, meta, 3);
                           } else {
                              break;
                           }
                        }
                        ParticleEffect.INSTANT_SPELL.send(SoundEffect.DIG_CLOTH, world, (double)(hit.blockX + dx) + 0.5D, (double)hit.blockY + 1.0D, (double)(hit.blockZ + dz) + 0.5D, 0.5D, 2.0D, 12);
                        return;
                     }
                  } else {
                     SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                     return;
                  }
               }
            }
         } else {
            this.playFailSound(world, player);
         }
      }
   }

   private static class TemporaryLeavesTask extends ServerTickEvents.ServerTickTask {
      final List<Coord> leaves;
      final Block leafBlock;
      final int maxTicks;
      int elapsed = 0;

      public TemporaryLeavesTask(World world, List<Coord> leaves, Block leafBlock, int maxTicks) {
         super(world);
         this.leaves = leaves;
         this.leafBlock = leafBlock;
         this.maxTicks = maxTicks;
      }

      @Override
      public boolean process() {
         ++this.elapsed;
         if(this.elapsed >= this.maxTicks) {
            for(Coord c : this.leaves) {
               if(super.world.getBlock(c.x, c.y, c.z) == this.leafBlock) {
                  super.world.setBlockToAir(c.x, c.y, c.z);
                  ParticleEffect.INSTANT_SPELL.send(SoundEffect.DIG_CLOTH, super.world, (double)c.x + 0.5D, (double)c.y + 0.5D, (double)c.z + 0.5D, 0.3D, 0.3D, 4);
               }
            }
            return true;
         }
         return false;
      }
   }
}
