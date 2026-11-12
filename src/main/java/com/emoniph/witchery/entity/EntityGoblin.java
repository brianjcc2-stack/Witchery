package com.emoniph.witchery.entity;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityVillageGuard;
import com.emoniph.witchery.entity.ai.EntityAIAvoidEntityConditionally;
import com.emoniph.witchery.entity.ai.EntityAIDigBlocks;
import com.emoniph.witchery.entity.ai.EntityAIDropOffBlocks;
import com.emoniph.witchery.entity.ai.EntityAIGoblinMate;
import com.emoniph.witchery.entity.ai.EntityAILookAtTradePlayerGeneric;
import com.emoniph.witchery.entity.ai.EntityAIMoveIndoorsLeashAware;
import com.emoniph.witchery.entity.ai.EntityAIPickUpBlocks;
import com.emoniph.witchery.entity.ai.EntityAITradePlayerGeneric;
import com.emoniph.witchery.entity.ai.EntityAIWorship;
import com.emoniph.witchery.util.Config;
import com.emoniph.witchery.util.TimeUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import com.emoniph.witchery.blocks.BlockWitchesOven;
import com.emoniph.witchery.blocks.BlockDistillery;
import com.emoniph.witchery.blocks.BlockKettle;
import com.emoniph.witchery.crafting.DistilleryRecipes;
import com.emoniph.witchery.crafting.KettleRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.nbt.NBTTagString;
import java.util.ArrayList;
import java.util.List;

public class EntityGoblin extends EntityAgeable implements IMerchant, INpc, IEntitySelector, EntityAIAvoidEntityConditionally.IAvoidEntities {

   private int randomTickDivider;
   private boolean isMating;
   Village villageObj;
   private EntityPlayer buyingPlayer;
   private MerchantRecipeList buyingList;
   private int timeUntilReset;
   private boolean needsInitilization;
   private int wealth;
   private String lastBuyingPlayer;
   private boolean isLookingForHome;
   private float field_82191_bN;
   public static final Map villagersSellingList = new HashMap();
   public static final Map blacksmithSellingList = new HashMap();
   private EntityAIWorship aiWorship;
   private boolean preventDespawn;
   private static final double KOBOLDITE_HARVEST_CHANCE = 0.02D;
   private boolean testingLeashRange;
   public String tamedOwnerName = "";
   public boolean isTamed = false;
   public boolean isSitting = false;
   public boolean isFollowing = false;
   public boolean isWorking = false;
   public boolean isSuccessor = false;
   public long lastBarrierTime = 0L;
   public long lastChatterTime = 0L;
   public String successorRole = "follow";
   public InventoryBasic goblinInventory = new InventoryBasic("Mochila de Hobgoblin", false, 9);


   public EntityGoblin(World par1World) {
      this(par1World, 0);
   }

   public EntityGoblin(World par1World, int par2) {
      super(par1World);
      this.setProfession(super.rand.nextInt(4));
      this.setSize(0.6F, 0.95F);
      this.getNavigator().setBreakDoors(true);
      this.getNavigator().setAvoidsWater(true);
      super.tasks.addTask(0, new EntityAISwimming(this));
      super.tasks.addTask(1, this.aiWorship = new EntityAIWorship(this, (double)(TimeUtil.secsToTicks(30) + super.rand.nextInt(10))));
      super.tasks.addTask(1, new net.minecraft.entity.ai.EntityAIBase() {
         {
            this.setMutexBits(5);
         }
         public boolean shouldExecute() {
            return EntityGoblin.this.isSitting;
         }
         public void startExecuting() {
            EntityGoblin.this.getNavigator().clearPathEntity();
         }
         public void updateTask() {
            EntityGoblin.this.getNavigator().clearPathEntity();
         }
      });
      super.tasks.addTask(5, new net.minecraft.entity.ai.EntityAIBase() {
         private EntityLivingBase owner;
         private int timeToRecalcPath;
         {
            this.setMutexBits(3);
         }
         public boolean shouldExecute() {
            if (!EntityGoblin.this.isFollowing || EntityGoblin.this.isSitting || EntityGoblin.this.tamedOwnerName == null || EntityGoblin.this.tamedOwnerName.isEmpty()) return false;
            this.owner = net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().func_152612_a(EntityGoblin.this.tamedOwnerName);
            return this.owner != null && EntityGoblin.this.getDistanceSqToEntity(this.owner) > 16.0D;
         }
         public boolean continueExecuting() {
            return EntityGoblin.this.isFollowing && !EntityGoblin.this.isSitting && !EntityGoblin.this.getNavigator().noPath() && EntityGoblin.this.getDistanceSqToEntity(this.owner) > 4.0D;
         }
         public void startExecuting() {
            this.timeToRecalcPath = 0;
         }
         public void resetTask() {
            this.owner = null;
            EntityGoblin.this.getNavigator().clearPathEntity();
         }
         public void updateTask() {
            EntityGoblin.this.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)EntityGoblin.this.getVerticalFaceSpeed());
            if (--this.timeToRecalcPath <= 0) {
               this.timeToRecalcPath = 10;
               EntityGoblin.this.getNavigator().tryMoveToEntityLiving(this.owner, 0.6D);
            }
         }
      });
      super.tasks.addTask(2, new EntityAIPickUpBlocks(this, 24.0D));
      super.tasks.addTask(2, new EntityAIDropOffBlocks(this, 24.0D));
      super.tasks.addTask(2, new EntityAIDigBlocks(this, 16.0D, 0.02D));
      super.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0F, 0.6D, 0.6D));
      super.tasks.addTask(3, new EntityAIAvoidEntityConditionally(this, EntityVillageGuard.class, 12.0F, 0.8D, 0.8D, this));
      super.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, true));
      super.tasks.addTask(4, new EntityAITradePlayerGeneric(this, this));
      super.tasks.addTask(4, new EntityAILookAtTradePlayerGeneric(this, this));
      super.tasks.addTask(5, new EntityAIMoveIndoorsLeashAware(this));
      super.tasks.addTask(5, new EntityAIRestrictOpenDoor(this));
      super.tasks.addTask(6, new EntityAIOpenDoor(this, true));
      super.tasks.addTask(7, new EntityAIMoveTowardsRestriction(this, 0.6D));
      super.tasks.addTask(8, new EntityAIGoblinMate(this));
      super.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      super.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityGoblin.class, 5.0F, 0.02F));
      super.tasks.addTask(9, new EntityAIWander(this, 0.6D) {
         public boolean shouldExecute() {
            return !EntityGoblin.this.isFollowing && !EntityGoblin.this.isSitting && super.shouldExecute();
         }
      });
      super.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      super.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      super.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true, true, this));
   }

   public boolean isEntityApplicable(Entity entity) {
      double R = 8.0D;
      return entity instanceof EntityVillager?super.worldObj.getEntitiesWithinAABB(EntityGoblin.class, AxisAlignedBB.getBoundingBox(super.posX - 8.0D, super.posY - 8.0D, super.posZ - 8.0D, super.posX + 8.0D, super.posY + 8.0D, super.posZ + 8.0D)).size() >= 3:true;
   }

   public boolean shouldAvoid() {
      double R = 8.0D;
      return super.worldObj.getEntitiesWithinAABB(EntityGoblin.class, AxisAlignedBB.getBoundingBox(super.posX - 8.0D, super.posY - 8.0D, super.posZ - 8.0D, super.posX + 8.0D, super.posY + 8.0D, super.posZ + 8.0D)).size() >= 3;
   }

   public String getCommandSenderName() {
      return this.hasCustomNameTag()?this.getCustomNameTag():StatCollector.translateToLocal("entity.witchery.goblin.name");
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
   }

   public boolean attackEntityAsMob(Entity targetEntity) {
      float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      int i = 0;
      if(targetEntity instanceof EntityLivingBase) {
         f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)targetEntity);
         i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)targetEntity);
      }

      boolean flag = targetEntity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
      if(flag) {
         if(i > 0) {
            targetEntity.addVelocity((double)(-MathHelper.sin(super.rotationYaw * 3.1415927F / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(super.rotationYaw * 3.1415927F / 180.0F) * (float)i * 0.5F));
            super.motionX *= 0.6D;
            super.motionZ *= 0.6D;
         }

         int j = EnchantmentHelper.getFireAspectModifier(this);
         if(j > 0) {
            targetEntity.setFire(j * 4);
         }

         if(targetEntity instanceof EntityLivingBase) {
            EnchantmentHelper.func_151384_a((EntityLivingBase)targetEntity, this);
         }

         EnchantmentHelper.func_151385_b(this, targetEntity);
      }

      return flag;
   }

   public boolean isAIEnabled() {
      return true;
   }

   protected void updateAITick() {
      if(--this.randomTickDivider <= 0) {
         super.worldObj.villageCollectionObj.addVillagerPosition(MathHelper.floor_double(super.posX), MathHelper.floor_double(super.posY), MathHelper.floor_double(super.posZ));
         this.randomTickDivider = 70 + super.rand.nextInt(50);
         this.villageObj = super.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(super.posX), MathHelper.floor_double(super.posY), MathHelper.floor_double(super.posZ), 32);
         if(this.villageObj == null) {
            this.detachHome();
         } else {
            this.preventDespawn = true;
            ChunkCoordinates iterator = this.villageObj.getCenter();
            this.setHomeArea(iterator.posX, iterator.posY, iterator.posZ, (int)((float)this.villageObj.getVillageRadius() * 0.6F));
            if(this.isLookingForHome) {
               this.isLookingForHome = false;
               this.villageObj.setDefaultPlayerReputation(5);
            }
         }
      }

      if(!this.isTrading() && this.timeUntilReset > 0) {
         --this.timeUntilReset;
         if(this.timeUntilReset <= 0) {
            if(this.needsInitilization) {
               if(this.buyingList.size() > 1) {
                  Iterator var3 = this.buyingList.iterator();

                  while(var3.hasNext()) {
                     MerchantRecipe merchantrecipe = (MerchantRecipe)var3.next();
                     if(merchantrecipe.isRecipeDisabled()) {
                        merchantrecipe.func_82783_a(super.rand.nextInt(6) + super.rand.nextInt(6) + 2);
                     }
                  }
               }

               this.addDefaultEquipmentAndRecipies(1);
               this.needsInitilization = false;
               if(this.villageObj != null && this.lastBuyingPlayer != null) {
                  super.worldObj.setEntityState(this, (byte)14);
                  this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
               }
            }

            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
         }
      }

      if(this.isTamed && this.tamedOwnerName != null && !this.tamedOwnerName.isEmpty()) {
         EntityPlayerMP owner = net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.tamedOwnerName);
         if(owner != null) {
            if(this.isSuccessor()) {
               this.performSuccessorTick(owner);
            } else if(!this.isSitting && TimeUtil.secondsElapsed(2, super.ticksExisted)) {
               int currentEnergy = com.emoniph.witchery.infusion.Infusion.getCurrentEnergy(owner);
               int maxEnergy = com.emoniph.witchery.infusion.Infusion.getMaxEnergy(owner);
               if(currentEnergy < maxEnergy) {
                  com.emoniph.witchery.infusion.Infusion.setCurrentEnergy(owner, Math.min(currentEnergy + 40, maxEnergy));
                  com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.NOTE_PLING, owner, 1.0D, 2.0D, 8);
               }
            }
         }
      }

      if(this.isTamed && this.isWorking && !this.isSitting && !super.worldObj.isRemote) {
         if(super.ticksExisted % 25 == 0) {
            this.performHobgoblinWork();
         }
      }

      super.updateAITick();
   }

   private void performHobgoblinWork() {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);

      // 1. Recoger items del suelo
      AxisAlignedBB itemBB = AxisAlignedBB.getBoundingBox(super.posX - 3.0D, super.posY - 1.5D, super.posZ - 3.0D, super.posX + 3.0D, super.posY + 1.5D, super.posZ + 3.0D);
      java.util.List items = super.worldObj.getEntitiesWithinAABB(EntityItem.class, itemBB);
      for (Object obj : items) {
         EntityItem eItem = (EntityItem)obj;
         if (eItem != null && !eItem.isDead) {
            ItemStack stack = eItem.getEntityItem();
            if (stack != null) {
               for (int slot = 0; slot < this.goblinInventory.getSizeInventory(); slot++) {
                  ItemStack inSlot = this.goblinInventory.getStackInSlot(slot);
                  if (inSlot == null) {
                     this.goblinInventory.setInventorySlotContents(slot, stack.copy());
                     eItem.setDead();
                     super.worldObj.playSoundAtEntity(this, "random.pop", 0.5F, 1.2F);
                     break;
                  } else if (inSlot.isItemEqual(stack) && inSlot.stackSize < inSlot.getMaxStackSize()) {
                     int canAdd = Math.min(stack.stackSize, inSlot.getMaxStackSize() - inSlot.stackSize);
                     inSlot.stackSize += canAdd;
                     stack.stackSize -= canAdd;
                     if (stack.stackSize <= 0) eItem.setDead();
                     super.worldObj.playSoundAtEntity(this, "random.pop", 0.5F, 1.2F);
                     break;
                  }
               }
            }
         }
      }

      // 2. Depositar en cofre cercano si hay cosechas
      boolean hasHarvest = false;
      for (int slot = 0; slot < this.goblinInventory.getSizeInventory(); slot++) {
         ItemStack inSlot = this.goblinInventory.getStackInSlot(slot);
         if (inSlot != null && !(inSlot.getItem() instanceof net.minecraft.item.ItemTool)) {
            hasHarvest = true;
            break;
         }
      }

      if (hasHarvest) {
         for (int dy = -2; dy <= 2; dy++) {
            for (int dz = -5; dz <= 5; dz++) {
               for (int dx = -5; dx <= 5; dx++) {
                  TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
                  if (te instanceof IInventory && !(te instanceof com.emoniph.witchery.blocks.BlockKettle.TileEntityKettle) && !(te instanceof com.emoniph.witchery.blocks.BlockBrazier.TileEntityBrazier)) {
                     IInventory chest = (IInventory)te;
                     for (int slot = 0; slot < this.goblinInventory.getSizeInventory(); slot++) {
                        ItemStack inSlot = this.goblinInventory.getStackInSlot(slot);
                        if (inSlot != null && !(inSlot.getItem() instanceof net.minecraft.item.ItemTool)) {
                           for (int cSlot = 0; cSlot < chest.getSizeInventory(); cSlot++) {
                              ItemStack cItem = chest.getStackInSlot(cSlot);
                              if (cItem == null) {
                                 chest.setInventorySlotContents(cSlot, inSlot.copy());
                                 this.goblinInventory.setInventorySlotContents(slot, null);
                                 break;
                              } else if (cItem.isItemEqual(inSlot) && cItem.stackSize < cItem.getMaxStackSize()) {
                                 int add = Math.min(inSlot.stackSize, cItem.getMaxStackSize() - cItem.stackSize);
                                 cItem.stackSize += add;
                                 inSlot.stackSize -= add;
                                 if (inSlot.stackSize <= 0) {
                                    this.goblinInventory.setInventorySlotContents(slot, null);
                                    break;
                                 }
                              }
                           }
                        }
                     }
                     chest.markDirty();
                     this.swingItem();
                     super.worldObj.playSoundAtEntity(this, "random.chestclosed", 0.5F, 1.0F);
                     return;
                  }
               }
            }
         }
      }

      // 3. Cosechar cultivos maduros en radio 10
      int r = 10;
      for (int dy = -2; dy <= 2; dy++) {
         for (int dz = -r; dz <= r; dz++) {
            for (int dx = -r; dx <= r; dx++) {
               int bx = gx + dx;
               int by = gy + dy;
               int bz = gz + dz;
               Block block = super.worldObj.getBlock(bx, by, bz);
               int meta = super.worldObj.getBlockMetadata(bx, by, bz);
               boolean ripe = false;

               if (block instanceof BlockCrops && meta >= 7) ripe = true;
               else if (block == Blocks.carrots && meta >= 7) ripe = true;
               else if (block == Blocks.potatoes && meta >= 7) ripe = true;
               else if (block == Blocks.nether_wart && meta >= 3) ripe = true;
               else if ((block == Witchery.Blocks.CROP_BELLADONNA || block == Witchery.Blocks.CROP_MANDRAKE || block == Witchery.Blocks.CROP_ARTICHOKE || block == Witchery.Blocks.CROP_SNOWBELL) && meta >= 4) ripe = true;

               if (ripe) {
                  double distSq = this.getDistanceSq((double)bx + 0.5D, (double)by, (double)bz + 0.5D);
                  if (distSq > 4.5D) {
                     this.getNavigator().tryMoveToXYZ((double)bx + 0.5D, (double)by, (double)bz + 0.5D, 0.8D);
                     return;
                  } else {
                     this.swingItem();
                     super.worldObj.func_147480_a(bx, by, bz, true);
                     if (super.worldObj.getBlock(bx, by - 1, bz) == Blocks.farmland) {
                        super.worldObj.setBlock(bx, by, bz, block, 0, 3);
                     }
                     com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_POP, super.worldObj, (double)bx + 0.5D, (double)by + 0.5D, (double)bz + 0.5D, 0.5D, 0.5D, 6);
                     return;
                  }
               }
            }
         }
      }

      // 4. Mineria si tiene pico en mano o inventario
      ItemStack held = this.getHeldItem();
      if (held != null && held.getItem() instanceof net.minecraft.item.ItemPickaxe) {
         for (int dy = -1; dy <= 2; dy++) {
            for (int dz = -2; dz <= 2; dz++) {
               for (int dx = -2; dx <= 2; dx++) {
                  int bx = gx + dx;
                  int by = gy + dy;
                  int bz = gz + dz;
                  Block block = super.worldObj.getBlock(bx, by, bz);
                  if (block == Blocks.coal_ore || block == Blocks.iron_ore || block == Blocks.gold_ore || block == Blocks.stone) {
                     this.swingItem();
                     super.worldObj.func_147480_a(bx, by, bz, true);
                     if (super.rand.nextFloat() < 0.10F) {
                        this.entityDropItem(Witchery.Items.GENERIC.itemKobolditeDust.createStack(), 0.0F);
                     }
                     return;
                  }
               }
            }
         }
      }
   }

   public boolean isSuccessor() {
      return (super.dataWatcher.getWatchableObjectByte(19) & 1) != 0;
   }

   public void setSuccessor(boolean val) {
      this.isSuccessor = val;
      byte b = super.dataWatcher.getWatchableObjectByte(19);
      if (val) b = (byte)(b | 1);
      else b = (byte)(b & ~1);
      super.dataWatcher.updateObject(19, Byte.valueOf(b));
      if (val) {
         if (this.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
            this.setHealth(60.0F);
         }
         this.setCustomNameTag("§d★ Sucesor Arcano");
         this.setAlwaysRenderNameTag(true);
      }
   }

   public void sendWhisper(EntityPlayer player, String message) {
      if (player != null) {
         player.addChatMessage(new ChatComponentText(
            EnumChatFormatting.DARK_GRAY + "[" + 
            EnumChatFormatting.LIGHT_PURPLE + "★ Sucesor" + 
            EnumChatFormatting.DARK_GRAY + " ➔ " + 
            EnumChatFormatting.WHITE + "Tú" + 
            EnumChatFormatting.DARK_GRAY + "] " + 
            EnumChatFormatting.YELLOW + message
         ));
         com.emoniph.witchery.util.SoundEffect.NOTE_HARP.playOnlyTo(player);
      }
   }

   public void awakenAsSuccessor(EntityPlayer player) {
      this.isTamed = true;
      this.tamedOwnerName = player.getCommandSenderName();
      this.setSuccessor(true);
      this.preventDespawn = true;
      this.isFollowing = true;
      this.isSitting = false;
      this.successorRole = "follow";

      com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_LEVELUP, this, 1.5D, 2.0D, 32);
      com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_HYPNOSIS, this, 1.5D, 2.0D, 40);
      com.emoniph.witchery.util.ParticleEffect.ENCHANTMENT_TABLE.send(com.emoniph.witchery.util.SoundEffect.NOTE_HARP, this, 2.0D, 2.0D, 40);

      this.sendWhisper(player, "§6¡Maestro! §eMi mente se ha elevado. El poder puro de Koboldite ha transformado mi cuerpo en un tono §fpálido y eterno§e. No volverás a estar solo: seré tu aprendiz, tu escudo y tu sucesor. Escribe §b/hobgoblin ayuda §epara conocer todas mis habilidades.");
   }

   public static boolean checkAndTriggerDefensiveBarrier(EntityPlayer player, LivingHurtEvent event) {
      if (player.worldObj.isRemote) return false;
      AxisAlignedBB bb = player.boundingBox.expand(20.0D, 10.0D, 20.0D);
      java.util.List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);
      for (EntityGoblin goblin : goblins) {
         if (goblin != null && goblin.isEntityAlive() && goblin.isTamed && goblin.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(goblin.tamedOwnerName)) {
            if (goblin.triggerDefensiveBarrier(player, event)) {
               return true;
            }
         }
      }
      return false;
   }

   public boolean triggerDefensiveBarrier(EntityPlayer master, LivingHurtEvent event) {
      long now = super.worldObj.getTotalWorldTime();
      if (now - this.lastBarrierTime < 160L) {
         return false;
      }
      this.lastBarrierTime = now;

      event.ammount = Math.max(0.0F, event.ammount * 0.15F);

      com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_HYPNOSIS, master, 1.5D, 2.0D, 36);
      com.emoniph.witchery.util.ParticleEffect.ENCHANTMENT_TABLE.send(com.emoniph.witchery.util.SoundEffect.RANDOM_LEVELUP, master, 2.0D, 2.0D, 48);

      master.addPotionEffect(new PotionEffect(Potion.resistance.id, 140, 1));
      master.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 240, 1));
      master.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, 1));

      AxisAlignedBB repelBox = master.boundingBox.expand(8.0D, 4.0D, 8.0D);
      java.util.List entities = super.worldObj.getEntitiesWithinAABBExcludingEntity(master, repelBox);
      for (Object obj : entities) {
         if (obj instanceof EntityLivingBase && !(obj instanceof EntityPlayer) && !(obj instanceof EntityGoblin && ((EntityGoblin)obj).isTamed)) {
            EntityLivingBase enemy = (EntityLivingBase) obj;
            double dx = enemy.posX - master.posX;
            double dz = enemy.posZ - master.posZ;
            double dist = Math.max(0.1D, Math.sqrt(dx * dx + dz * dz));
            enemy.motionX = (dx / dist) * 1.6D;
            enemy.motionY = 0.45D;
            enemy.motionZ = (dz / dist) * 1.6D;
            enemy.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120, 2));
            enemy.addPotionEffect(new PotionEffect(Potion.weakness.id, 120, 1));
            this.setAttackTarget(enemy);
         }
      }

      this.sendWhisper(master, "§c¡PELIGRO! §eHe levantado una §d★ Barrera Arcana §epara protegerte. ¡Repele a los agresores!");
      return true;
   }

   public boolean triggerSelfBarrier(LivingHurtEvent event) {
      if (!this.isSuccessor()) return false;
      long now = super.worldObj.getTotalWorldTime();
      if (now - this.lastBarrierTime < 160L) return false;
      this.lastBarrierTime = now;

      event.ammount = Math.max(0.0F, event.ammount * 0.20F);
      this.addPotionEffect(new PotionEffect(Potion.resistance.id, 160, 2));
      this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 120, 1));
      com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_HYPNOSIS, this, 1.0D, 1.5D, 24);
      return true;
   }

   public void dropItemInCircle(double x, double y, double z, ItemStack stack) {
      if (stack != null && stack.stackSize > 0) {
         EntityItem entityItem = new EntityItem(super.worldObj, x, y, z, stack);
         entityItem.delayBeforeCanPickup = 20;
         entityItem.motionX = 0.0D;
         entityItem.motionY = 0.1D;
         entityItem.motionZ = 0.0D;
         super.worldObj.spawnEntityInWorld(entityItem);
         com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_POP, entityItem, 0.5D, 0.5D, 8);
      }
   }

   public boolean loadAndActivateCircle(EntityPlayer player, String ritualName) {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      int range = 16;

      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -range; dz <= range; dz++) {
            for (int dx = -range; dx <= range; dx++) {
               int bx = gx + dx;
               int by = gy + dy;
               int bz = gz + dz;
               Block block = super.worldObj.getBlock(bx, by, bz);
               if (block == Witchery.Blocks.CIRCLE) {
                  TileEntity te = super.worldObj.getTileEntity(bx, by, bz);
                  if (te instanceof com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle) {
                     com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle tileCircle = (com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle)te;
                     double cx = (double)bx + 0.5D;
                     double cy = (double)by + 0.1D;
                     double cz = (double)bz + 0.5D;

                     this.getNavigator().tryMoveToXYZ(cx, (double)by, cz, 0.9D);

                     java.util.List<IInventory> nearbyChests = this.getNearbyChests(10);
                     int itemsLoaded = 0;

                     if (ritualName != null && !ritualName.isEmpty()) {
                        String rName = ritualName.toLowerCase();
                        java.util.List<ItemStack> neededItems = new java.util.ArrayList<ItemStack>();

                        if (rName.contains("tormenta") || rName.contains("lluvia") || rName.contains("storm")) {
                           neededItems.add(Witchery.Items.GENERIC.itemArtichoke.createStack());
                           neededItems.add(Witchery.Items.GENERIC.itemBelladonnaFlower.createStack());
                           neededItems.add(new ItemStack(Items.gunpowder));
                        } else if (rName.contains("dia") || rName.contains("sol") || rName.contains("dawn")) {
                           neededItems.add(new ItemStack(Witchery.Blocks.SUN_ROSE));
                           neededItems.add(new ItemStack(Items.gold_nugget));
                        } else if (rName.contains("noche") || rName.contains("luna") || rName.contains("night")) {
                           neededItems.add(Witchery.Items.GENERIC.itemMandrakeRoot.createStack());
                           neededItems.add(new ItemStack(Items.bone));
                        } else if (rName.contains("bendicion") || rName.contains("vida") || rName.contains("heal")) {
                           neededItems.add(new ItemStack(Items.apple));
                           neededItems.add(new ItemStack(Items.wheat_seeds));
                           neededItems.add(Witchery.Items.GENERIC.itemMandrakeRoot.createStack());
                        } else if (rName.contains("proteccion") || rName.contains("barrera") || rName.contains("escudo")) {
                           neededItems.add(new ItemStack(Witchery.Blocks.SAPLING, 1, 0));
                           neededItems.add(new ItemStack(Items.iron_ingot));
                        }

                        for (ItemStack req : neededItems) {
                           for (IInventory ch : nearbyChests) {
                              for (int s = 0; s < ch.getSizeInventory(); s++) {
                                 ItemStack inSlot = ch.getStackInSlot(s);
                                 if (inSlot != null && inSlot.isItemEqual(req)) {
                                    ItemStack taken = ch.decrStackSize(s, 1);
                                    ch.markDirty();
                                    this.dropItemInCircle(cx, cy, cz, taken);
                                    itemsLoaded++;
                                    break;
                                 }
                              }
                           }
                        }
                     } else {
                        IInventory recipeChest = this.getClosestChest(bx, by, bz, 8);
                        if (recipeChest != null) {
                           for (int s = 0; s < recipeChest.getSizeInventory(); s++) {
                              ItemStack inSlot = recipeChest.getStackInSlot(s);
                              if (inSlot != null && !(inSlot.getItem() instanceof net.minecraft.item.ItemTool)) {
                                 ItemStack taken = recipeChest.decrStackSize(s, 1);
                                 recipeChest.markDirty();
                                 this.dropItemInCircle(cx, cy, cz, taken);
                                 itemsLoaded++;
                              }
                           }
                        } else {
                           for (IInventory ch : nearbyChests) {
                              TileEntity chestTe = (TileEntity)ch;
                              double distSq = (chestTe.xCoord - bx) * (chestTe.xCoord - bx) + (chestTe.zCoord - bz) * (chestTe.zCoord - bz);
                              if (distSq <= 20.0D) {
                                 for (int s = 0; s < ch.getSizeInventory(); s++) {
                                    ItemStack inSlot = ch.getStackInSlot(s);
                                    if (inSlot != null && !(inSlot.getItem() instanceof net.minecraft.item.ItemTool)) {
                                       ItemStack taken = ch.decrStackSize(s, 1);
                                       ch.markDirty();
                                       this.dropItemInCircle(cx, cy, cz, taken);
                                       itemsLoaded++;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     Witchery.Blocks.CIRCLE.onBlockActivated(super.worldObj, bx, by, bz, player, 1, 0.5F, 0.5F, 0.5F);
                     com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_CHALK, super.worldObj, cx, cy + 0.2D, cz, 1.0D, 1.0D, 24);

                     if (tileCircle.isRitualActive()) {
                        this.sendWhisper(player, "§a¡Círculo cargado con " + itemsLoaded + " ingredientes! El ritual en (" + bx + ", " + by + ", " + bz + ") ha comenzado con éxito, Maestro.");
                     } else if (itemsLoaded > 0) {
                        this.sendWhisper(player, "§eHe cargado " + itemsLoaded + " ingredientes en el círculo (" + bx + ", " + by + ", " + bz + "). Asegúrate de que el altar tenga suficiente poder para completarlo.");
                     } else {
                        this.sendWhisper(player, "§eHe activado los glifos en (" + bx + ", " + by + ", " + bz + "). Coloca ingredientes en un cofre cercano o dime qué rito preparar (ej: §b/hobgoblin rito tormenta§e).");
                     }
                     return true;
                  }
               }
            }
         }
      }
      this.sendWhisper(player, "§cNo he detectado ningún círculo de glifos en un radio de 16 bloques, Maestro.");
      return false;
   }

   public boolean activateNearestCircle(EntityPlayer player) {
      return this.loadAndActivateCircle(player, "");
   }

   public java.util.List<IInventory> getNearbyChests(int radius) {
      java.util.List<IInventory> list = new java.util.ArrayList<IInventory>();
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      for (int dy = -2; dy <= 2; dy++) {
         for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
               TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
               if (te instanceof IInventory && !(te instanceof BlockWitchesOven.TileEntityWitchesOven) 
                   && !(te instanceof BlockDistillery.TileEntityDistillery)
                   && !(te instanceof BlockKettle.TileEntityKettle)
                   && !(te instanceof com.emoniph.witchery.blocks.BlockBrazier.TileEntityBrazier)) {
                  list.add((IInventory)te);
               }
            }
         }
      }
      return list;
   }

   /** Retorna el cofre/inventario más cercano a las coordenadas dadas dentro del radio. */
   public IInventory getClosestChest(int targetX, int targetY, int targetZ, int maxDist) {
      java.util.List<IInventory> chests = this.getNearbyChests(maxDist);
      IInventory best = null;
      double bestDistSq = Double.MAX_VALUE;
      for (IInventory inv : chests) {
         if (inv instanceof TileEntity) {
            TileEntity te = (TileEntity)inv;
            double dSq = (te.xCoord - targetX) * (te.xCoord - targetX) + (te.yCoord - targetY) * (te.yCoord - targetY) + (te.zCoord - targetZ) * (te.zCoord - targetZ);
            if (dSq < bestDistSq) {
               bestDistSq = dSq;
               best = inv;
            }
         }
      }
      return best;
   }

   public boolean depositStackToChests(ItemStack stack, java.util.List<IInventory> chests) {
      if (stack == null || stack.stackSize <= 0) return true;
      for (IInventory chest : chests) {
         for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            ItemStack inSlot = chest.getStackInSlot(slot);
            if (inSlot == null) {
               chest.setInventorySlotContents(slot, stack.copy());
               stack.stackSize = 0;
               chest.markDirty();
               return true;
            } else if (inSlot.isItemEqual(stack) && inSlot.stackSize < inSlot.getMaxStackSize()) {
               int add = Math.min(stack.stackSize, inSlot.getMaxStackSize() - inSlot.stackSize);
               inSlot.stackSize += add;
               stack.stackSize -= add;
               chest.markDirty();
               if (stack.stackSize <= 0) return true;
            }
         }
      }
      return stack.stackSize <= 0;
   }

   public ItemStack pullClayJarsFromChests(java.util.List<IInventory> chests, int maxCount) {
      for (IInventory chest : chests) {
         for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            ItemStack inSlot = chest.getStackInSlot(slot);
            if (inSlot != null && Witchery.Items.GENERIC.itemEmptyClayJar.isMatch(inSlot)) {
               int take = Math.min(maxCount, inSlot.stackSize);
               ItemStack res = inSlot.splitStack(take);
               if (inSlot.stackSize <= 0) chest.setInventorySlotContents(slot, null);
               chest.markDirty();
               return res;
            }
         }
      }
      return null;
   }

   public ItemStack pullFuelFromChests(java.util.List<IInventory> chests, int maxCount) {
      for (IInventory chest : chests) {
         for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            ItemStack inSlot = chest.getStackInSlot(slot);
            if (inSlot != null && TileEntityFurnace.isItemFuel(inSlot)) {
               int take = Math.min(maxCount, inSlot.stackSize);
               ItemStack res = inSlot.splitStack(take);
               if (inSlot.stackSize <= 0) chest.setInventorySlotContents(slot, null);
               chest.markDirty();
               return res;
            }
         }
      }
      return null;
   }

   public ItemStack pullOvenMaterialFromChests(java.util.List<IInventory> chests, int maxCount) {
      for (IInventory chest : chests) {
         for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            ItemStack inSlot = chest.getStackInSlot(slot);
            if (inSlot != null) {
               Item item = inSlot.getItem();
               if (item == Item.getItemFromBlock(Blocks.sapling) 
                   || item == Item.getItemFromBlock(Blocks.log)
                   || item == Item.getItemFromBlock(Blocks.log2)
                   || item == Item.getItemFromBlock(Witchery.Blocks.LOG)
                   || item == Item.getItemFromBlock(Witchery.Blocks.SAPLING)
                   || item == Items.clay_ball
                   || item == Item.getItemFromBlock(Blocks.clay)) {
                  int take = Math.min(maxCount, inSlot.stackSize);
                  ItemStack res = inSlot.splitStack(take);
                  if (inSlot.stackSize <= 0) chest.setInventorySlotContents(slot, null);
                  chest.markDirty();
                  return res;
               }
            }
         }
      }
      return null;
   }

   public ItemStack pullItemFromChests(java.util.List<IInventory> chests, Item item, int meta, int maxQuantity) {
      for (IInventory chest : chests) {
         for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            ItemStack inSlot = chest.getStackInSlot(slot);
            if (inSlot != null && inSlot.getItem() == item && (meta < 0 || inSlot.getItemDamage() == meta)) {
               int take = Math.min(maxQuantity, inSlot.stackSize);
               ItemStack result = inSlot.splitStack(take);
               if (inSlot.stackSize <= 0) {
                  chest.setInventorySlotContents(slot, null);
               }
               chest.markDirty();
               return result;
            }
         }
      }
      return null;
   }

   /**
    * Lee un libro de receta de destilería del cofre.
    * Si encuentra un libro/papel cuyo displayName coincide con el output de una receta,
    * carga esa receta. Si no, busca la primera receta con ingredientes disponibles.
    */
   public boolean feedDistilleryRecipeFromChests(BlockDistillery.TileEntityDistillery dist, java.util.List<IInventory> chests) {
      // Paso 1: buscar libro de receta en cofres con nombre de producto
      DistilleryRecipes.DistilleryRecipe targetRecipe = null;
      ItemStack recipeBook = null;
      int bookChestIdx = -1, bookSlotIdx = -1;

      for (int c = 0; c < chests.size() && targetRecipe == null; c++) {
         IInventory chest = chests.get(c);
         for (int s = 0; s < chest.getSizeInventory() && targetRecipe == null; s++) {
            ItemStack stack = chest.getStackInSlot(s);
            if (stack != null && (stack.getItem() == Items.written_book || stack.getItem() == Items.writable_book || stack.getItem() == Items.paper)) {
               String bookName = stack.hasDisplayName() ? stack.getDisplayName().toLowerCase().trim() : "";
               if (!bookName.isEmpty()) {
                  // Buscar receta cuyo output coincida con el nombre del libro
                  for (Object obj : DistilleryRecipes.instance().recipes) {
                     if (obj instanceof DistilleryRecipes.DistilleryRecipe) {
                        DistilleryRecipes.DistilleryRecipe rec = (DistilleryRecipes.DistilleryRecipe)obj;
                        for (ItemStack out : rec.outputs) {
                           if (out != null) {
                              String outName = out.getDisplayName().toLowerCase().trim();
                              if (outName.contains(bookName) || bookName.contains(outName)) {
                                 targetRecipe = rec;
                                 recipeBook = stack;
                                 bookChestIdx = c;
                                 bookSlotIdx = s;
                                 break;
                              }
                           }
                        }
                        if (targetRecipe != null) break;
                     }
                  }
               }
            }
         }
      }

      // Paso 2: si encontramos receta por libro, intentar cargar esa receta
      if (targetRecipe != null) {
         if (this.loadDistilleryRecipe(dist, chests, targetRecipe)) {
            return true;
         }
         // Si no hay ingredientes para la receta del libro, seguimos buscando
      }

      // Paso 3: modo auto - primera receta con ingredientes disponibles
      for (Object obj : DistilleryRecipes.instance().recipes) {
         if (obj instanceof DistilleryRecipes.DistilleryRecipe) {
            DistilleryRecipes.DistilleryRecipe rec = (DistilleryRecipes.DistilleryRecipe)obj;
            if (this.loadDistilleryRecipe(dist, chests, rec)) {
               return true;
            }
         }
      }
      return false;
   }

   private boolean loadDistilleryRecipe(BlockDistillery.TileEntityDistillery dist, java.util.List<IInventory> chests, DistilleryRecipes.DistilleryRecipe rec) {
      ItemStack in0 = rec.inputs[0];
      ItemStack in1 = rec.inputs[1];
      if (in0 == null) return false;

      int slot0ChestIdx = -1, slot0Idx = -1;
      int slot1ChestIdx = -1, slot1Idx = -1;

      for (int c = 0; c < chests.size(); c++) {
         IInventory chest = chests.get(c);
         for (int s = 0; s < chest.getSizeInventory(); s++) {
            ItemStack stack = chest.getStackInSlot(s);
            if (stack != null) {
               if (slot0ChestIdx == -1 && stack.isItemEqual(in0)) {
                  slot0ChestIdx = c; slot0Idx = s;
               } else if (in1 != null && slot1ChestIdx == -1 && stack.isItemEqual(in1)) {
                  slot1ChestIdx = c; slot1Idx = s;
               }
            }
         }
      }

      if (slot0ChestIdx != -1 && (in1 == null || slot1ChestIdx != -1)) {
         ItemStack stack0 = chests.get(slot0ChestIdx).decrStackSize(slot0Idx, 4);
         chests.get(slot0ChestIdx).markDirty();
         dist.setInventorySlotContents(0, stack0);
         if (in1 != null && slot1ChestIdx != -1) {
            ItemStack stack1 = chests.get(slot1ChestIdx).decrStackSize(slot1Idx, 4);
            chests.get(slot1ChestIdx).markDirty();
            dist.setInventorySlotContents(1, stack1);
         }
         return true;
      }
      return false;
   }

   public int manageNearbyOvens() {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      int ovensServiced = 0;
      java.util.List<IInventory> chests = this.getNearbyChests(14);

      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -12; dz <= 12; dz++) {
            for (int dx = -12; dx <= 12; dx++) {
               TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
               if (te instanceof BlockWitchesOven.TileEntityWitchesOven) {
                  BlockWitchesOven.TileEntityWitchesOven oven = (BlockWitchesOven.TileEntityWitchesOven)te;
                  boolean touched = false;

                  ItemStack cooked = oven.getStackInSlot(2);
                  if (cooked != null) {
                     this.depositStackToChests(cooked, chests);
                     oven.setInventorySlotContents(2, null);
                     touched = true;
                  }
                  ItemStack byProduct = oven.getStackInSlot(3);
                  if (byProduct != null) {
                     this.depositStackToChests(byProduct, chests);
                     oven.setInventorySlotContents(3, null);
                     touched = true;
                  }

                  ItemStack jars = oven.getStackInSlot(4);
                  if (jars == null || jars.stackSize < 16) {
                     int needed = jars == null ? 16 : 16 - jars.stackSize;
                     ItemStack foundJars = this.pullClayJarsFromChests(chests, needed);
                     if (foundJars != null) {
                        if (jars == null) oven.setInventorySlotContents(4, foundJars);
                        else jars.stackSize += foundJars.stackSize;
                        touched = true;
                     }
                  }

                  ItemStack fuel = oven.getStackInSlot(1);
                  if (fuel == null && oven.furnaceBurnTime < 200) {
                     ItemStack foundFuel = this.pullFuelFromChests(chests, 8);
                     if (foundFuel != null) {
                        oven.setInventorySlotContents(1, foundFuel);
                        touched = true;
                     }
                  }

                  ItemStack toCook = oven.getStackInSlot(0);
                  if (toCook == null) {
                     ItemStack foundMaterial = this.pullOvenMaterialFromChests(chests, 16);
                     if (foundMaterial != null) {
                        oven.setInventorySlotContents(0, foundMaterial);
                        touched = true;
                     }
                  }

                  if (touched) {
                     oven.markDirty();
                     ovensServiced++;
                  }
               }
            }
         }
      }
      if (ovensServiced > 0) {
         this.swingItem();
         super.worldObj.playSoundAtEntity(this, "random.pop", 0.5F, 1.0F);
      }
      return ovensServiced;
   }

   public int manageNearbyDistilleries() {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      int distilleriesServiced = 0;
      java.util.List<IInventory> chests = this.getNearbyChests(14);

      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -12; dz <= 12; dz++) {
            for (int dx = -12; dx <= 12; dx++) {
               TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
               if (te instanceof BlockDistillery.TileEntityDistillery) {
                  BlockDistillery.TileEntityDistillery dist = (BlockDistillery.TileEntityDistillery)te;
                  boolean touched = false;

                  for (int outSlot = 3; outSlot <= 6; outSlot++) {
                     ItemStack out = dist.getStackInSlot(outSlot);
                     if (out != null) {
                        this.depositStackToChests(out, chests);
                        dist.setInventorySlotContents(outSlot, null);
                        touched = true;
                     }
                  }

                  ItemStack jars = dist.getStackInSlot(2);
                  if (jars == null || jars.stackSize < 16) {
                     int needed = jars == null ? 16 : 16 - jars.stackSize;
                     ItemStack foundJars = this.pullClayJarsFromChests(chests, needed);
                     if (foundJars != null) {
                        if (jars == null) dist.setInventorySlotContents(2, foundJars);
                        else jars.stackSize += foundJars.stackSize;
                        touched = true;
                     }
                  }

                  if (dist.getStackInSlot(0) == null && dist.getStackInSlot(1) == null) {
                     // Prioridad 1: Si hay un cofre cerca de la destilería con ingredientes en orden (slots 0 y 1)
                     IInventory recipeChest = this.getClosestChest(gx + dx, gy + dy, gz + dz, 6);
                     boolean loaded = false;
                     if (recipeChest != null) {
                        int distSlot = 0;
                        for (int s = 0; s < recipeChest.getSizeInventory() && distSlot < 2; s++) {
                           ItemStack stack = recipeChest.getStackInSlot(s);
                           if (stack != null && stack.stackSize > 0) {
                              Item item = stack.getItem();
                              if (Witchery.Items.GENERIC.itemEmptyClayJar.isMatch(stack) || item == Items.glass_bottle || item == Items.water_bucket) {
                                 continue;
                              }
                              ItemStack taken = recipeChest.decrStackSize(s, Math.min(stack.stackSize, 4));
                              recipeChest.markDirty();
                              dist.setInventorySlotContents(distSlot, taken);
                              distSlot++;
                              loaded = true;
                           }
                        }
                     }
                     if (loaded) {
                        touched = true;
                     } else if (this.feedDistilleryRecipeFromChests(dist, chests)) {
                        touched = true;
                     }
                  }

                  if (touched) {
                     dist.markDirty();
                     distilleriesServiced++;
                  }
               }
            }
         }
      }
      if (distilleriesServiced > 0) {
         this.swingItem();
         super.worldObj.playSoundAtEntity(this, "random.pop", 0.5F, 1.2F);
      }
      return distilleriesServiced;
   }

   public int manageNearbyKettles() {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      int kettlesServiced = 0;
      java.util.List<IInventory> chests = this.getNearbyChests(14);

      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -12; dz <= 12; dz++) {
            for (int dx = -12; dx <= 12; dx++) {
               TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
               if (te instanceof BlockKettle.TileEntityKettle) {
                  BlockKettle.TileEntityKettle kettle = (BlockKettle.TileEntityKettle)te;
                  boolean touched = false;

                  // 1. Si hay resultado listo en slot 6, sacarlo
                  ItemStack result = kettle.getStackInSlot(6);
                  if (result != null) {
                     // Asegurarse de que haya botellas en slot 7 para la siguiente tanda
                     ItemStack bottles = kettle.getStackInSlot(7);
                     if (bottles == null) {
                        ItemStack foundBottles = this.pullItemFromChests(chests, Items.glass_bottle, 0, 8);
                        if (foundBottles != null) {
                           kettle.setInventorySlotContents(7, foundBottles);
                        }
                     }
                     this.depositStackToChests(result, chests);
                     kettle.setInventorySlotContents(6, null);
                     touched = true;
                  }

                  // 2. Si el calderito no tiene ingredientes (slots 0-5 vacíos) y está lleno de agua, cargar receta
                  boolean hasIngredients = false;
                  for (int slot = 0; slot < 6; slot++) {
                     if (kettle.getStackInSlot(slot) != null) { hasIngredients = true; break; }
                  }

                  if (!hasIngredients && kettle.isFilled()) {
                     boolean loaded = false;
                     // Prioridad 1: Si hay un cofre cerca del calderito con ingredientes en orden (slots 0..5), cargarlos directamente
                     IInventory recipeChest = this.getClosestChest(gx + dx, gy + dy, gz + dz, 6);
                     if (recipeChest != null) {
                        int kettleSlot = 0;
                        for (int s = 0; s < recipeChest.getSizeInventory() && kettleSlot < 6; s++) {
                           ItemStack stack = recipeChest.getStackInSlot(s);
                           if (stack != null && stack.stackSize > 0) {
                              Item item = stack.getItem();
                              if (item == Items.glass_bottle || item == Items.water_bucket || item == Items.bucket || item == Items.flint_and_steel) {
                                 continue;
                              }
                              ItemStack single = recipeChest.decrStackSize(s, 1);
                              recipeChest.markDirty();
                              kettle.setInventorySlotContents(kettleSlot, single);
                              kettleSlot++;
                              loaded = true;
                           }
                        }
                     }

                     // Prioridad 2: Buscar libro de receta o recetas conocidas
                     if (!loaded) {
                        KettleRecipes.KettleRecipe targetKettleRecipe = this.findKettleRecipeFromChests(chests);
                        if (targetKettleRecipe != null) {
                           loaded = this.loadKettleRecipe(kettle, chests, targetKettleRecipe);
                        } else {
                           // Auto: buscar primera receta con ingredientes disponibles
                           for (Object obj : KettleRecipes.instance().recipes) {
                              if (obj instanceof KettleRecipes.KettleRecipe) {
                                 KettleRecipes.KettleRecipe rec = (KettleRecipes.KettleRecipe)obj;
                                 if (this.loadKettleRecipe(kettle, chests, rec)) {
                                    loaded = true;
                                    break;
                                 }
                              }
                           }
                        }
                     }

                     if (loaded) touched = true;

                     // Poner botellas en slot 7 si no hay
                     if (kettle.getStackInSlot(7) == null) {
                        ItemStack foundBottles = this.pullItemFromChests(chests, Items.glass_bottle, 0, 8);
                        if (foundBottles != null) {
                           kettle.setInventorySlotContents(7, foundBottles);
                           touched = true;
                        }
                     }
                  }

                  if (touched) {
                     kettle.markDirty();
                     kettlesServiced++;
                  }
               }
            }
         }
      }
      if (kettlesServiced > 0) {
         this.swingItem();
         super.worldObj.playSoundAtEntity(this, "random.pop", 0.5F, 1.1F);
         EntityPlayerMP owner = this.getSuccessorOwner();
         if (owner != null) this.sendWhisper(owner, "§b[Calderito] §7Receta cargada y botellas listas.");
      }
      return kettlesServiced;
   }

   /** Busca un libro/papel con nombre de receta Kettle en los cofres. Retorna la receta si la encuentra. */
   private KettleRecipes.KettleRecipe findKettleRecipeFromChests(java.util.List<IInventory> chests) {
      for (IInventory chest : chests) {
         for (int s = 0; s < chest.getSizeInventory(); s++) {
            ItemStack stack = chest.getStackInSlot(s);
            if (stack != null && (stack.getItem() == Items.written_book || stack.getItem() == Items.writable_book || stack.getItem() == Items.paper)) {
               String bookName = stack.hasDisplayName() ? stack.getDisplayName().toLowerCase().trim() : "";
               if (!bookName.isEmpty()) {
                  for (Object obj : KettleRecipes.instance().recipes) {
                     if (obj instanceof KettleRecipes.KettleRecipe) {
                        KettleRecipes.KettleRecipe rec = (KettleRecipes.KettleRecipe)obj;
                        ItemStack out = rec.getOutput(null, false);
                        if (out != null) {
                           String outName = out.getDisplayName().toLowerCase().trim();
                           if (outName.contains(bookName) || bookName.contains(outName)) {
                              return rec;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      return null;
   }

   /** Carga los ingredientes de una KettleRecipe desde los cofres al Kettle. */
   private boolean loadKettleRecipe(BlockKettle.TileEntityKettle kettle, java.util.List<IInventory> chests, KettleRecipes.KettleRecipe rec) {
      ItemStack[] inputs = rec.inputs;
      if (inputs == null || inputs.length == 0) return false;
      // Verificar que todos los ingredientes estén disponibles
      java.util.List<ItemStack> needed = new java.util.ArrayList<ItemStack>();
      for (ItemStack inp : inputs) { if (inp != null) needed.add(inp.copy()); }
      for (ItemStack need : needed) {
         boolean found = false;
         for (IInventory chest : chests) {
            for (int s = 0; s < chest.getSizeInventory(); s++) {
               ItemStack cs = chest.getStackInSlot(s);
               if (cs != null && cs.isItemEqual(need) && cs.stackSize >= need.stackSize) { found = true; break; }
            }
            if (found) break;
         }
         if (!found) return false;
      }
      // Cargar ingredientes en slots libres del kettle (0-5)
      int emptySlot = 0;
      boolean anyLoaded = false;
      for (ItemStack need : needed) {
         // Buscar slot libre en kettle
         while (emptySlot < 6 && kettle.getStackInSlot(emptySlot) != null) emptySlot++;
         if (emptySlot >= 6) break;
         ItemStack pulled = this.pullItemFromChests(chests, need.getItem(), need.getItemDamage(), need.stackSize);
         if (pulled != null) {
            kettle.setInventorySlotContents(emptySlot, pulled);
            emptySlot++;
            anyLoaded = true;
         }
      }
      return anyLoaded;
   }

   public int manageNearbyCauldrons() {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      int cauldronsServiced = 0;
      java.util.List<IInventory> chests = this.getNearbyChests(14);

      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -12; dz <= 12; dz++) {
            for (int dx = -12; dx <= 12; dx++) {
               int bx = gx + dx;
               int by = gy + dy;
               int bz = gz + dz;
               Block block = super.worldObj.getBlock(bx, by, bz);
               if (block == Witchery.Blocks.CAULDRON) {
                  TileEntity te = super.worldObj.getTileEntity(bx, by, bz);
                  if (te instanceof com.emoniph.witchery.brewing.TileEntityCauldron) {
                     com.emoniph.witchery.brewing.TileEntityCauldron cauldron = (com.emoniph.witchery.brewing.TileEntityCauldron)te;
                     boolean touched = false;

                     // 1. Si no tiene agua, buscar cubos de agua en cofres y llenar
                     if (!cauldron.isFilled()) {
                        ItemStack waterBucket = this.pullItemFromChests(chests, Items.water_bucket, 0, 1);
                        if (waterBucket != null) {
                           Witchery.Blocks.CAULDRON.tryFillWith(super.worldObj, bx, by, bz, new net.minecraftforge.fluids.FluidStack(net.minecraftforge.fluids.FluidRegistry.WATER, 3000));
                           this.depositStackToChests(new ItemStack(Items.bucket), chests);
                           touched = true;
                        }
                     }

                     // 2. Si tiene agua pero abajo no hay fuego, encender fuego si hay mechero en cofres
                     if (cauldron.isFilled() && super.worldObj.getBlock(bx, by - 1, bz) != Blocks.fire) {
                        if (super.worldObj.isAirBlock(bx, by - 1, bz) || super.worldObj.getBlock(bx, by - 1, bz) == Blocks.netherrack) {
                           ItemStack flint = this.pullItemFromChests(chests, Items.flint_and_steel, -1, 1);
                           if (flint != null) {
                              super.worldObj.setBlock(bx, by - 1, bz, Blocks.fire);
                              this.depositStackToChests(flint, chests);
                              touched = true;
                           }
                        }
                     }

                     // 3. Si está hirviendo pero NO tiene efectos aún: echar ingredientes en orden desde el cofre
                     net.minecraftforge.fluids.FluidStack fluid = cauldron.getTankInfo(net.minecraftforge.common.util.ForgeDirection.UNKNOWN)[0].fluid;
                     boolean hasEffects = fluid != null && fluid.tag != null && fluid.tag.hasKey("EffectCount") && fluid.tag.getInteger("EffectCount") > 0;

                     if (cauldron.isFilled() && cauldron.isBoiling() && !hasEffects) {
                        EntityPlayerMP owner = this.getSuccessorOwner();
                        int itemsAdded = 0;

                        // Prioridad 1: Leer ingredientes directamente en orden de slots desde el cofre más cercano
                        IInventory recipeChest = this.getClosestChest(bx, by, bz, 6);
                        if (recipeChest != null) {
                           for (int s = 0; s < recipeChest.getSizeInventory(); s++) {
                              ItemStack stackInSlot = recipeChest.getStackInSlot(s);
                              if (stackInSlot != null && stackInSlot.stackSize > 0) {
                                 Item itm = stackInSlot.getItem();
                                 if (itm == Items.water_bucket || itm == Items.bucket || itm == Items.flint_and_steel || itm == Items.glass_bottle) {
                                    continue;
                                 }

                                 ItemStack single = recipeChest.decrStackSize(s, 1);
                                 recipeChest.markDirty();

                                 EntityItem dropEntity = new EntityItem(super.worldObj, (double)bx + 0.5D, (double)by + 0.6D, (double)bz + 0.5D, single);
                                 super.worldObj.spawnEntityInWorld(dropEntity);
                                 Witchery.Blocks.CAULDRON.onEntityCollidedWithBlock(super.worldObj, bx, by, bz, dropEntity);

                                 if (dropEntity.isDead) {
                                    touched = true;
                                    itemsAdded++;
                                 } else {
                                    // Si no fue aceptado, regresarlo al cofre intacto
                                    if (recipeChest.getStackInSlot(s) == null) {
                                       recipeChest.setInventorySlotContents(s, single);
                                    } else {
                                       recipeChest.getStackInSlot(s).stackSize += single.stackSize;
                                    }
                                    dropEntity.setDead();
                                    recipeChest.markDirty();
                                    break;
                                 }
                              }
                           }
                           if (itemsAdded > 0 && owner != null) {
                              this.sendWhisper(owner, "§b[Caldero] §7He arrojado " + itemsAdded + " ingredientes en orden desde el cofre.");
                           }
                        }

                        // Prioridad 2: Si no hubo ingredientes en el cofre directo, intentar por libro de recetas
                        if (itemsAdded == 0) {
                           java.util.List<String> ingredientNames = this.readCauldronRecipeBook(chests);
                           if (!ingredientNames.isEmpty()) {
                              for (String ingName : ingredientNames) {
                                 ItemStack pulled = this.pullItemFromChestsByName(chests, ingName.toLowerCase().trim(), 1);
                                 if (pulled != null) {
                                    EntityItem dropEntity = new EntityItem(super.worldObj, (double)bx + 0.5D, (double)by + 0.6D, (double)bz + 0.5D, pulled);
                                    super.worldObj.spawnEntityInWorld(dropEntity);
                                    Witchery.Blocks.CAULDRON.onEntityCollidedWithBlock(super.worldObj, bx, by, bz, dropEntity);
                                    if (dropEntity.isDead) {
                                       touched = true;
                                       itemsAdded++;
                                    } else {
                                       dropEntity.setDead();
                                       this.depositStackToChests(pulled, chests);
                                       break;
                                    }
                                 } else if (owner != null) {
                                    this.sendWhisper(owner, "§c[Caldero] §7No encuentro §e" + ingName + "§7 en los cofres.");
                                 }
                              }
                              if (itemsAdded > 0 && owner != null) {
                                 this.sendWhisper(owner, "§b[Caldero] §7Ingredientes del libro arrojados. Esperando brebaje...");
                              }
                           }
                        }
                     }

                     // 4. Si está hirviendo y tiene un brebaje listo con efectos: embotellar
                     // Refrescar fluid tras posibles cambios
                     fluid = cauldron.getTankInfo(net.minecraftforge.common.util.ForgeDirection.UNKNOWN)[0].fluid;
                     if (fluid != null && fluid.tag != null && fluid.tag.hasKey("EffectCount") && fluid.tag.getInteger("EffectCount") > 0 && cauldron.isBoiling() && cauldron.isPowered()) {
                        ItemStack bottles = this.pullItemFromChests(chests, Items.glass_bottle, 0, 3);
                        if (bottles != null) {
                           int count = bottles.stackSize;
                           for (int b = 0; b < count; b++) {
                              if (cauldron.getLiquidQuantity() >= 1000) {
                                 cauldron.drain(net.minecraftforge.common.util.ForgeDirection.UNKNOWN, 1000, true);
                                 ItemStack brew = new ItemStack(Witchery.Items.BREW);
                                 brew.setTagCompound((NBTTagCompound)fluid.tag.copy());
                                 this.depositStackToChests(brew, chests);
                                 touched = true;
                              }
                           }
                           cauldron.markBlockForUpdate(true);
                        }
                     }

                     // 5. Recoger productos transmutados o caídos alrededor del caldero
                     AxisAlignedBB floorBB = AxisAlignedBB.getBoundingBox((double)bx - 1.5D, (double)by, (double)bz - 1.5D, (double)bx + 2.5D, (double)by + 2.0D, (double)bz + 2.5D);
                     java.util.List floorItems = super.worldObj.getEntitiesWithinAABB(EntityItem.class, floorBB);
                     for (Object obj : floorItems) {
                        EntityItem item = (EntityItem)obj;
                        if (item != null && !item.isDead) {
                           ItemStack stack = item.getEntityItem();
                           if (stack != null) {
                              this.depositStackToChests(stack, chests);
                              if (stack.stackSize <= 0) {
                                 item.setDead();
                                 touched = true;
                              }
                           }
                        }
                     }

                     if (touched) {
                        cauldron.markBlockForUpdate(true);
                        cauldronsServiced++;
                     }
                  }
               }
            }
         }
      }
      if (cauldronsServiced > 0) {
         this.swingItem();
         super.worldObj.playSoundAtEntity(this, "random.pop", 0.5F, 1.1F);
      }
      return cauldronsServiced;
   }

   /**
    * Lee el primer libro escrito (ItemWrittenBook) en los cofres que tenga el display name "caldero"
    * o "receta caldero". Cada línea de la primera página es el nombre de un ingrediente.
    * Formato del libro: título = "caldero" o "receta caldero", cada página = 1 ingrediente por línea.
    */
   private java.util.List<String> readCauldronRecipeBook(java.util.List<IInventory> chests) {
      java.util.List<String> ingredients = new java.util.ArrayList<String>();
      for (IInventory chest : chests) {
         for (int s = 0; s < chest.getSizeInventory(); s++) {
            ItemStack stack = chest.getStackInSlot(s);
            if (stack != null && (stack.getItem() == Items.written_book || stack.getItem() == Items.writable_book)) {
               String title = stack.hasDisplayName() ? stack.getDisplayName().toLowerCase().trim() : "";
               // El libro puede llamarse "caldero", "receta caldero", "brew", "brebaje", etc.
               if (title.contains("caldero") || title.contains("brew") || title.contains("brebaje") || title.contains("pocion") || title.contains("poción")) {
                  NBTTagCompound tag = stack.getTagCompound();
                  if (tag != null && tag.hasKey("pages")) {
                     net.minecraft.nbt.NBTTagList pages = tag.getTagList("pages", 8); // 8 = TAG_String
                     for (int p = 0; p < pages.tagCount(); p++) {
                        String page = pages.getStringTagAt(p);
                        // Cada línea es un ingrediente
                        String[] lines = page.split("[\\n\\r]+");
                        for (String line : lines) {
                           line = line.trim();
                           if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) {
                              ingredients.add(line);
                           }
                        }
                     }
                  }
                  if (!ingredients.isEmpty()) return ingredients;
               }
            }
         }
      }
      return ingredients;
   }

   /**
    * Busca un item por nombre (display name) en los cofres y lo extrae.
    * Búsqueda parcial, case-insensitive.
    */
   private ItemStack pullItemFromChestsByName(java.util.List<IInventory> chests, String nameLower, int quantity) {
      for (IInventory chest : chests) {
         for (int s = 0; s < chest.getSizeInventory(); s++) {
            ItemStack stack = chest.getStackInSlot(s);
            if (stack != null) {
               String itemName = stack.getDisplayName().toLowerCase().trim();
               // Nombre del item registrado también
               String regName = stack.getItem().getUnlocalizedName(stack).toLowerCase().replace("item.", "").replace("tile.", "").replace("witchery:", "");
               if (itemName.contains(nameLower) || nameLower.contains(itemName) || regName.contains(nameLower) || nameLower.contains(regName)) {
                  int take = Math.min(quantity, stack.stackSize);
                  ItemStack result = chest.decrStackSize(s, take);
                  chest.markDirty();
                  if (result != null && result.stackSize > 0) return result;
               }
            }
         }
      }
      return null;
   }

   public boolean transmuteInCauldron(EntityPlayer player) {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      java.util.List<IInventory> chests = this.getNearbyChests(14);

      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -10; dz <= 10; dz++) {
            for (int dx = -10; dx <= 10; dx++) {
               int bx = gx + dx;
               int by = gy + dy;
               int bz = gz + dz;
               Block block = super.worldObj.getBlock(bx, by, bz);
               if (block == Witchery.Blocks.CAULDRON) {
                  TileEntity te = super.worldObj.getTileEntity(bx, by, bz);
                  if (te instanceof com.emoniph.witchery.brewing.TileEntityCauldron) {
                     com.emoniph.witchery.brewing.TileEntityCauldron cauldron = (com.emoniph.witchery.brewing.TileEntityCauldron)te;
                     if (cauldron.isBoiling() && cauldron.isPowered()) {
                        // Buscar cobblestone, carne podrida o saplings en cofres
                        ItemStack transmutable = null;
                        for (IInventory ch : chests) {
                           for (int s = 0; s < ch.getSizeInventory(); s++) {
                              ItemStack inSlot = ch.getStackInSlot(s);
                              if (inSlot != null) {
                                 Item it = inSlot.getItem();
                                 if (it == Item.getItemFromBlock(Blocks.cobblestone) || it == Items.rotten_flesh || it == Item.getItemFromBlock(Blocks.sapling)) {
                                    transmutable = ch.decrStackSize(s, 1);
                                    ch.markDirty();
                                    break;
                                 }
                              }
                           }
                           if (transmutable != null) break;
                        }

                        if (transmutable != null) {
                           this.dropItemInCircle((double)bx + 0.5D, (double)by + 0.8D, (double)bz + 0.5D, transmutable);
                           this.sendWhisper(player, "§a¡He arrojado " + transmutable.getDisplayName() + " al caldero hirviendo para transmutarlo en minerales y esencias!");
                           return true;
                        } else {
                           this.sendWhisper(player, "§eEl caldero está listo e hirviendo, pero no encontré adoquín, carne ni brotes en los cofres para transmutar.");
                           return true;
                        }
                     } else {
                        this.sendWhisper(player, "§eEl caldero en (" + bx + ", " + by + ", " + bz + ") no está hirviendo o no tiene poder del altar.");
                        return true;
                     }
                  }
               }
            }
         }
      }
      this.sendWhisper(player, "§cNo he detectado ningún caldero cerca, Maestro.");
      return false;
   }

   public int storeAllToChests() {
      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      int storedCount = 0;

      for (int dy = -2; dy <= 2; dy++) {
         for (int dz = -8; dz <= 8; dz++) {
            for (int dx = -8; dx <= 8; dx++) {
               TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
               if (te instanceof IInventory && !(te instanceof BlockWitchesOven.TileEntityWitchesOven) 
                   && !(te instanceof BlockDistillery.TileEntityDistillery)
                   && !(te instanceof BlockKettle.TileEntityKettle)
                   && !(te instanceof com.emoniph.witchery.blocks.BlockBrazier.TileEntityBrazier)) {
                  IInventory chest = (IInventory)te;
                  for (int slot = 0; slot < this.goblinInventory.getSizeInventory(); slot++) {
                     ItemStack inSlot = this.goblinInventory.getStackInSlot(slot);
                     if (inSlot != null && !(inSlot.getItem() instanceof net.minecraft.item.ItemTool)) {
                        for (int cSlot = 0; cSlot < chest.getSizeInventory(); cSlot++) {
                           ItemStack cItem = chest.getStackInSlot(cSlot);
                           if (cItem == null) {
                              chest.setInventorySlotContents(cSlot, inSlot.copy());
                              storedCount += inSlot.stackSize;
                              this.goblinInventory.setInventorySlotContents(slot, null);
                              break;
                           } else if (cItem.isItemEqual(inSlot) && cItem.stackSize < cItem.getMaxStackSize()) {
                              int add = Math.min(inSlot.stackSize, cItem.getMaxStackSize() - cItem.stackSize);
                              cItem.stackSize += add;
                              inSlot.stackSize -= add;
                              storedCount += add;
                              if (inSlot.stackSize <= 0) {
                                 this.goblinInventory.setInventorySlotContents(slot, null);
                                 break;
                              }
                           }
                        }
                     }
                  }
                  chest.markDirty();
               }
            }
         }
      }
      if (storedCount > 0) {
         this.swingItem();
         super.worldObj.playSoundAtEntity(this, "random.chestclosed", 0.6F, 1.0F);
      }
      return storedCount;
   }

   private static final String[] COMPANION_QUOTES = new String[]{
      "Estoy vigilando las sombras, Maestro. Nadie se acercará sin ser detectado.",
      "El poder del Koboldite resuena con tu magia. Juntos somos invencibles.",
      "Me siento honrado de caminar a tu lado, Maestro.",
      "Si algún enemigo se atreve a atacarte, mi barrera arcana se alzará al instante.",
      "Los espíritus susurran tu nombre con respeto y misterio...",
      "¿Necesitas que active un ritual o revise los hornos y destilerías? Solo ordénamelo.",
      "Siempre estaré aquí, Maestro. La soledad no volverá a tocarte jamás."
   };

   /** Retorna el dueño del Sucesor como EntityPlayerMP, o null si no está en línea o no existe. */
   public EntityPlayerMP getSuccessorOwner() {
      if (this.tamedOwnerName == null || this.tamedOwnerName.isEmpty()) return null;
      return net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.tamedOwnerName);
   }

   private void performSuccessorTick(EntityPlayerMP owner) {
      if (super.ticksExisted % 20 == 0) {
         int currentEnergy = com.emoniph.witchery.infusion.Infusion.getCurrentEnergy(owner);
         int maxEnergy = com.emoniph.witchery.infusion.Infusion.getMaxEnergy(owner);
         if (currentEnergy < maxEnergy) {
            com.emoniph.witchery.infusion.Infusion.setCurrentEnergy(owner, Math.min(currentEnergy + 60, maxEnergy));
            com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.NOTE_PLING, owner, 1.0D, 2.0D, 6);
         }
      }

      if (super.ticksExisted % 60 == 0 && owner.getHealth() < owner.getMaxHealth() * 0.35F) {
         owner.heal(5.0F);
         owner.addPotionEffect(new PotionEffect(Potion.regeneration.id, 120, 1));
         this.sendWhisper(owner, "§c¡Resiste Maestro! §aHe canalizado energía vital de emergencia hacia ti.");
         com.emoniph.witchery.util.ParticleEffect.HEART.send(com.emoniph.witchery.util.SoundEffect.RANDOM_ORB, owner, 1.0D, 1.5D, 8);
      }

      if (this.successorRole.equals("alquimia") || this.successorRole.equals("lab") || this.isWorking) {
         if (super.ticksExisted % 100 == 0) {
            this.manageNearbyOvens();
            this.manageNearbyDistilleries();
            this.manageNearbyKettles();
            this.manageNearbyCauldrons();
         }
      }

      if (super.ticksExisted - this.lastChatterTime > 3600) {
         this.lastChatterTime = super.ticksExisted;
         if (this.getDistanceToEntity(owner) < 14.0D) {
            String quote = COMPANION_QUOTES[super.rand.nextInt(COMPANION_QUOTES.length)];
            this.sendWhisper(owner, "§f\"" + quote + "\"");
         }
      }
   }

   public void reportStatusToOwner(EntityPlayer player) {
      int infCurrent = com.emoniph.witchery.infusion.Infusion.getCurrentEnergy(player);
      int infMax = com.emoniph.witchery.infusion.Infusion.getMaxEnergy(player);
      int healthInt = (int)this.getHealth();
      int maxHealthInt = (int)this.getMaxHealth();

      this.sendWhisper(player, "§a[Estado de tu Sucesor] §eSalud: §c" + healthInt + "§7/§c" + maxHealthInt + " HP §7| §eRol: §b" + this.successorRole + " §7| §eTu Maná: §d" + infCurrent + "§7/§d" + infMax);

      int gx = MathHelper.floor_double(super.posX);
      int gy = MathHelper.floor_double(super.posY);
      int gz = MathHelper.floor_double(super.posZ);
      for (int dy = -3; dy <= 3; dy++) {
         for (int dz = -16; dz <= 16; dz++) {
            for (int dx = -16; dx <= 16; dx++) {
               TileEntity te = super.worldObj.getTileEntity(gx + dx, gy + dy, gz + dz);
               if (te instanceof com.emoniph.witchery.blocks.BlockAltar.TileEntityAltar) {
                  com.emoniph.witchery.blocks.BlockAltar.TileEntityAltar altar = (com.emoniph.witchery.blocks.BlockAltar.TileEntityAltar)te;
                  this.sendWhisper(player, "§5Altar místico cercano detectado: Poder " + (int)altar.getCorePower() + "/" + (int)altar.getCoreMaxPower());
                  return;
               }
            }
         }
      }
   }

   protected void updateLeashedState() {
      try {
         this.testingLeashRange = true;
         if(this.getLeashed()) {
            this.preventDespawn = true;
         }

         super.updateLeashedState();
      } finally {
         this.testingLeashRange = false;
      }

   }

   public float getDistanceToEntity(Entity par1Entity) {
      float distance = super.getDistanceToEntity(par1Entity);
      if(this.testingLeashRange && distance < 9.0F) {
         distance *= 0.5F;
      }

      return distance;
   }

   public void onUpdate() {
      super.onUpdate();
      if(!super.worldObj.isRemote) {
         this.setBesideClimbableBlock(super.isCollidedHorizontally);
      }

   }

   public boolean isOnLadder() {
      return super.isOnLadder();
   }

   public boolean isWorking() {
      return super.dataWatcher.getWatchableObjectByte(18) == 1;
   }

   public void setWorking(boolean par1) {
      byte b0 = super.dataWatcher.getWatchableObjectByte(18);
      if(par1 && b0 != 1 || !par1 && b0 == 1) {
         super.dataWatcher.updateObject(18, Byte.valueOf((byte)(par1?1:0)));
      }

   }

   public boolean isWorshipping() {
      return super.dataWatcher.getWatchableObjectByte(18) == 2;
   }

   public void setWorshipping(boolean worshiping) {
      byte b0 = super.dataWatcher.getWatchableObjectByte(18);
      if(worshiping && b0 != 2 || !worshiping && b0 == 2) {
         super.dataWatcher.updateObject(18, Byte.valueOf((byte)(worshiping?2:0)));
      }

   }

   public void beginWorship(TileEntity tile) {
      this.aiWorship.begin(tile);
   }

   public boolean isBesideClimbableBlock() {
      return (super.dataWatcher.getWatchableObjectByte(17) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean par1) {
      byte b0 = super.dataWatcher.getWatchableObjectByte(17);
      if(par1) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 &= -2;
      }

      super.dataWatcher.updateObject(17, Byte.valueOf(b0));
   }

   public boolean interact(EntityPlayer player) {
      ItemStack stack = player.inventory.getCurrentItem();
      boolean heldSpawnEgg = stack != null && stack.getItem() == Items.spawn_egg;

      if (!heldSpawnEgg && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()) {
         if (!this.isTamed && stack != null && stack.getItem() == Items.emerald) {
            if (!super.worldObj.isRemote) {
               if (!player.capabilities.isCreativeMode) {
                  --stack.stackSize;
                  if (stack.stackSize <= 0) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                  }
               }
               this.isTamed = true;
               this.tamedOwnerName = player.getCommandSenderName();
               this.isFollowing = true;
               this.preventDespawn = true;
               super.worldObj.setEntityState(this, (byte)7); // Heart particles
            }
            return true;
         }

         if (this.isTamed && !this.isSuccessor() && stack != null && Witchery.Items.GENERIC.itemKobolditePentacle.isMatch(stack)) {
            if (!super.worldObj.isRemote) {
               if (!player.capabilities.isCreativeMode) {
                  --stack.stackSize;
                  if (stack.stackSize <= 0) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                  }
               }
               this.awakenAsSuccessor(player);
            }
            return true;
         }

         if (this.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(this.tamedOwnerName) && stack == null) {
            if (!super.worldObj.isRemote) {
               this.reportStatusToOwner(player);
            }
            return true;
         }

         if(this.getLeashed()) {
            if(this.getHeldItem() == null) {
               if(stack != null && stack.getItem() instanceof ItemPickaxe) {
                  this.setCurrentItemOrArmor(0, stack);
                  player.setCurrentItemOrArmor(0, (ItemStack)null);
               }
            } else {
               if(!super.worldObj.isRemote) {
                  ItemStack goblinItem = this.getHeldItem();
                  if(!player.inventory.addItemStackToInventory(goblinItem)) {
                     this.entityDropItem(goblinItem, 1.0F);
                  } else if(player instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                  }
               }

               this.setCurrentItemOrArmor(0, (ItemStack)null);
            }
         } else if(!this.isWorking() && !this.isWorshipping() && this.villageObj != null && !super.worldObj.isRemote) {
            this.setCustomer(player);
            player.displayGUIMerchant(this, this.getCommandSenderName());
         }

         return true;
      } else {
         return super.interact(player);
      }
   }

   protected void entityInit() {
      super.entityInit();
      super.dataWatcher.addObject(16, Integer.valueOf(0));
      super.dataWatcher.addObject(17, Byte.valueOf((byte)0));
      super.dataWatcher.addObject(18, Byte.valueOf((byte)0));
      super.dataWatcher.addObject(19, Byte.valueOf((byte)0));
   }

   public void writeEntityToNBT(NBTTagCompound nbtRoot) {
      super.writeEntityToNBT(nbtRoot);
      nbtRoot.setInteger("Profession", this.getProfession());
      nbtRoot.setInteger("Riches", this.wealth);
      nbtRoot.setBoolean("Worshipping", this.isWorshipping());
      if(this.buyingList != null) {
         nbtRoot.setTag("Offers", this.buyingList.getRecipiesAsTags());
      }

      nbtRoot.setBoolean("PreventDespawn", this.preventDespawn);
      nbtRoot.setString("TamedOwnerName", this.tamedOwnerName != null ? this.tamedOwnerName : "");
      nbtRoot.setBoolean("IsTamed", this.isTamed);
      nbtRoot.setBoolean("IsSitting", this.isSitting);
      nbtRoot.setBoolean("IsFollowing", this.isFollowing);
      nbtRoot.setBoolean("IsWorking", this.isWorking);
      nbtRoot.setBoolean("IsSuccessor", this.isSuccessor());
      nbtRoot.setString("SuccessorRole", this.successorRole != null ? this.successorRole : "follow");
      NBTTagList invList = new NBTTagList();
      for (int i = 0; i < this.goblinInventory.getSizeInventory(); ++i) {
         ItemStack stack = this.goblinInventory.getStackInSlot(i);
         if (stack != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte)i);
            stack.writeToNBT(itemTag);
            invList.appendTag(itemTag);
         }
      }
      nbtRoot.setTag("GoblinInv", invList);
   }

   public void readEntityFromNBT(NBTTagCompound nbtRoot) {
      super.readEntityFromNBT(nbtRoot);
      this.setProfession(nbtRoot.getInteger("Profession"));
      this.wealth = nbtRoot.getInteger("Riches");
      if(nbtRoot.getBoolean("Worshipping") && !super.worldObj.isRemote) {
         this.setWorshipping(true);
      }

      if(nbtRoot.hasKey("Offers", 10)) {
         NBTTagCompound nbttagcompound1 = nbtRoot.getCompoundTag("Offers");
         this.buyingList = new MerchantRecipeList(nbttagcompound1);
      }

      this.preventDespawn = nbtRoot.getBoolean("PreventDespawn");
      this.tamedOwnerName = nbtRoot.getString("TamedOwnerName");
      this.isTamed = nbtRoot.getBoolean("IsTamed");
      this.isSitting = nbtRoot.getBoolean("IsSitting");
      this.isFollowing = nbtRoot.getBoolean("IsFollowing");
      this.isWorking = nbtRoot.getBoolean("IsWorking");
      this.setSuccessor(nbtRoot.getBoolean("IsSuccessor"));
      if (nbtRoot.hasKey("SuccessorRole")) {
         this.successorRole = nbtRoot.getString("SuccessorRole");
      }
      if (nbtRoot.hasKey("GoblinInv", 9)) {
         NBTTagList invList = nbtRoot.getTagList("GoblinInv", 10);
         for (int i = 0; i < invList.tagCount(); ++i) {
            NBTTagCompound itemTag = invList.getCompoundTagAt(i);
            byte slot = itemTag.getByte("Slot");
            if (slot >= 0 && slot < this.goblinInventory.getSizeInventory()) {
               this.goblinInventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
            }
         }
      }
   }

   protected float getSoundPitch() {
      return 1.2F;
   }

   protected boolean canDespawn() {
      return !Config.instance().goblinDespawnBlock && this.villageObj == null && !this.preventDespawn && !this.isWorshipping();
   }

   protected String getLivingSound() {
      return this.isTrading()?"witchery:mob.goblin.haggle":"witchery:mob.goblin.idle";
   }

   protected String getHurtSound() {
      return "witchery:mob.goblin.hit";
   }

   protected String getDeathSound() {
      return "witchery:mob.goblin.death";
   }

   public void setProfession(int par1) {
      super.dataWatcher.updateObject(16, Integer.valueOf(par1));
   }

   public int getProfession() {
      return super.dataWatcher.getWatchableObjectInt(16);
   }

   public boolean isMating() {
      return this.isMating;
   }

   public void setMating(boolean par1) {
      this.isMating = par1;
   }

   public void setRevengeTarget(EntityLivingBase par1EntityLivingBase) {
      super.setRevengeTarget(par1EntityLivingBase);
      if(this.villageObj != null && par1EntityLivingBase != null) {
         this.villageObj.addOrRenewAgressor(par1EntityLivingBase);
         if(par1EntityLivingBase instanceof EntityPlayer) {
            if(this.isChild()) {
               this.villageObj.setReputationForPlayer(par1EntityLivingBase.getCommandSenderName(), -3);
            }

            if(this.isEntityAlive()) {
               super.worldObj.setEntityState(this, (byte)13);
            }
         }
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      if(this.villageObj != null) {
         Entity entity = par1DamageSource.getEntity();
         if(entity != null) {
            if(!(entity instanceof EntityPlayer) && entity instanceof IMob) {
               this.villageObj.endMatingSeason();
            }
         } else if(entity == null) {
            EntityPlayer entityplayer = super.worldObj.getClosestPlayerToEntity(this, 16.0D);
            if(entityplayer != null) {
               this.villageObj.endMatingSeason();
            }
         }
      }

      super.onDeath(par1DamageSource);
   }

   public boolean getCanSpawnHere() {
      int i = MathHelper.floor_double(super.posX);
      int j = MathHelper.floor_double(super.boundingBox.minY);
      int k = MathHelper.floor_double(super.posZ);
      return super.worldObj.getBlock(i, j - 1, k) == Blocks.grass && super.worldObj.getFullBlockLightValue(i, j, k) > 8 && super.getCanSpawnHere();
   }

   public void setCustomer(EntityPlayer par1EntityPlayer) {
      this.buyingPlayer = par1EntityPlayer;
   }

   public EntityPlayer getCustomer() {
      return this.buyingPlayer;
   }

   public boolean isTrading() {
      return this.buyingPlayer != null;
   }

   public void useRecipe(MerchantRecipe par1MerchantRecipe) {
      par1MerchantRecipe.incrementToolUses();
      super.livingSoundTime = -this.getTalkInterval();
      this.playSound("witchery:mob.goblin.yes", this.getSoundVolume(), this.getSoundPitch());
      if(par1MerchantRecipe.hasSameIDsAs((MerchantRecipe)this.buyingList.get(this.buyingList.size() - 1))) {
         this.timeUntilReset = 40;
         this.needsInitilization = true;
         if(this.buyingPlayer != null) {
            this.lastBuyingPlayer = this.buyingPlayer.getCommandSenderName();
         } else {
            this.lastBuyingPlayer = null;
         }
      }

      if(par1MerchantRecipe.getItemToBuy().getItem() == Items.emerald) {
         this.wealth += par1MerchantRecipe.getItemToBuy().stackSize;
      }

   }

   public void func_110297_a_(ItemStack par1ItemStack) {
      if(!super.worldObj.isRemote && super.livingSoundTime > -this.getTalkInterval() + 20) {
         super.livingSoundTime = -this.getTalkInterval();
         if(par1ItemStack != null) {
            this.playSound("witchery:mob.goblin.yes", this.getSoundVolume(), this.getSoundPitch());
         } else {
            this.playSound("witchery:mob.goblin.no", this.getSoundVolume(), this.getSoundPitch());
         }
      }

   }

   public MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer) {
      if(this.buyingList == null) {
         this.addDefaultEquipmentAndRecipies(1);
      }

      return this.buyingList;
   }

   private float adjustProbability(float par1) {
      float f1 = par1 + this.field_82191_bN;
      return f1 > 0.9F?0.9F - (f1 - 0.9F):f1;
   }

   private void addDefaultEquipmentAndRecipies(int par1) {
      if(this.buyingList != null) {
         this.field_82191_bN = MathHelper.sqrt_float((float)this.buyingList.size()) * 0.2F;
      } else {
         this.field_82191_bN = 0.0F;
      }

      MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
      boolean shuffle = true;
      switch(this.getProfession()) {
      case 0:
         addItemToSwapForAnEmerald(merchantrecipelist, Items.wheat, super.rand, this.adjustProbability(0.9F));
         addItemToSwapForAnEmerald(merchantrecipelist, Item.getItemFromBlock(Blocks.wool), super.rand, this.adjustProbability(0.5F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.chicken, super.rand, this.adjustProbability(0.5F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.cooked_fished, super.rand, this.adjustProbability(0.4F));
         addItemToBuyOrSell(merchantrecipelist, Items.bread, super.rand, this.adjustProbability(0.9F));
         addItemToBuyOrSell(merchantrecipelist, Items.melon, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.apple, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.cookie, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.shears, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.flint_and_steel, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.cooked_chicken, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.arrow, super.rand, this.adjustProbability(0.5F));
         if(super.rand.nextFloat() < this.adjustProbability(0.5F)) {
            merchantrecipelist.add(new MerchantRecipe(new ItemStack(Blocks.gravel, 10), new ItemStack(Items.emerald), new ItemStack(Items.flint, 4 + super.rand.nextInt(2), 0)));
         }
         break;
      case 1:
      case 2:
         shuffle = false;
         if(this.buyingList == null) {
            merchantrecipelist.add(new MerchantRecipe(Witchery.Items.GENERIC.itemKobolditeDust.createStack(9), new ItemStack(Items.gold_nugget, 5), Witchery.Items.GENERIC.itemKobolditeNugget.createStack()));
         } else if(this.buyingList.size() == 1) {
            merchantrecipelist.add(new MerchantRecipe(Witchery.Items.GENERIC.itemKobolditeDust.createStack(16), new ItemStack(Items.gold_ingot), Witchery.Items.GENERIC.itemKobolditeNugget.createStack(2)));
         } else if(this.buyingList.size() == 2) {
            merchantrecipelist.add(new MerchantRecipe(Witchery.Items.GENERIC.itemKobolditeNugget.createStack(9), new ItemStack(Items.emerald), Witchery.Items.GENERIC.itemKobolditeIngot.createStack()));
         }
         break;
      case 3:
         addItemToSwapForAnEmerald(merchantrecipelist, Items.coal, super.rand, this.adjustProbability(0.7F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.iron_ingot, super.rand, this.adjustProbability(0.5F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.gold_ingot, super.rand, this.adjustProbability(0.5F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.diamond, super.rand, this.adjustProbability(0.5F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_sword, super.rand, this.adjustProbability(0.5F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_sword, super.rand, this.adjustProbability(0.5F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_axe, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_axe, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_pickaxe, super.rand, this.adjustProbability(0.5F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_pickaxe, super.rand, this.adjustProbability(0.5F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_shovel, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_shovel, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_hoe, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_hoe, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_boots, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_boots, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_helmet, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_helmet, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_chestplate, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_chestplate, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.iron_leggings, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.diamond_leggings, super.rand, this.adjustProbability(0.2F));
         addItemToBuyOrSell(merchantrecipelist, Items.chainmail_boots, super.rand, this.adjustProbability(0.1F));
         addItemToBuyOrSell(merchantrecipelist, Items.chainmail_helmet, super.rand, this.adjustProbability(0.1F));
         addItemToBuyOrSell(merchantrecipelist, Items.chainmail_chestplate, super.rand, this.adjustProbability(0.1F));
         addItemToBuyOrSell(merchantrecipelist, Items.chainmail_leggings, super.rand, this.adjustProbability(0.1F));
         break;
      case 4:
         addItemToSwapForAnEmerald(merchantrecipelist, Items.coal, super.rand, this.adjustProbability(0.7F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.porkchop, super.rand, this.adjustProbability(0.5F));
         addItemToSwapForAnEmerald(merchantrecipelist, Items.beef, super.rand, this.adjustProbability(0.5F));
         addItemToBuyOrSell(merchantrecipelist, Items.saddle, super.rand, this.adjustProbability(0.1F));
         addItemToBuyOrSell(merchantrecipelist, Items.leather_chestplate, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.leather_boots, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.leather_helmet, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.leather_leggings, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.cooked_porkchop, super.rand, this.adjustProbability(0.3F));
         addItemToBuyOrSell(merchantrecipelist, Items.cooked_beef, super.rand, this.adjustProbability(0.3F));
      }

      if(merchantrecipelist.isEmpty()) {
         merchantrecipelist.add(new MerchantRecipe(getItemStackToSwapForAnEmerald(Items.gold_ingot, super.rand), Items.emerald));
      }

      if(shuffle) {
         Collections.shuffle(merchantrecipelist);
      }

      if(this.buyingList == null) {
         this.buyingList = new MerchantRecipeList();
      }

      for(int l = 0; l < par1 && l < merchantrecipelist.size(); ++l) {
         this.buyingList.addToListWithCheck((MerchantRecipe)merchantrecipelist.get(l));
      }

   }

   @SideOnly(Side.CLIENT)
   public void setRecipes(MerchantRecipeList par1MerchantRecipeList) {}

   public static void addItemToSwapForAnEmerald(MerchantRecipeList buyList, Item item, Random rand, float probability) {
      if(rand.nextFloat() < probability) {
         if(rand.nextInt(3) == 0) {
            buyList.add(new MerchantRecipe(getItemStackToSwapForAnEmerald(item, rand), Witchery.Items.GENERIC.itemKobolditeDust.createStack()));
         } else {
            buyList.add(new MerchantRecipe(getItemStackToSwapForAnEmerald(item, rand), Items.emerald));
         }
      }

   }

   private static ItemStack getItemStackToSwapForAnEmerald(Item item, Random rand) {
      return new ItemStack(item, getQuantityToSwapForAnEmerald(item, rand), 0);
   }

   private static int getQuantityToSwapForAnEmerald(Item item, Random rand) {
      Tuple tuple = (Tuple)villagersSellingList.get(item);
      return tuple == null?1:(((Integer)tuple.getFirst()).intValue() >= ((Integer)tuple.getSecond()).intValue()?((Integer)tuple.getFirst()).intValue():((Integer)tuple.getFirst()).intValue() + rand.nextInt(((Integer)tuple.getSecond()).intValue() - ((Integer)tuple.getFirst()).intValue()));
   }

   public static void addItemToBuyOrSell(MerchantRecipeList list, Item item, Random rand, float probability) {
      if(rand.nextFloat() < probability) {
         int i = quantityToBuyOrSell(item, rand);
         ItemStack itemstack;
         ItemStack itemstack1;
         if(i < 0) {
            itemstack = Witchery.Items.GENERIC.itemKobolditeNugget.createStack(1);
            itemstack1 = new ItemStack(item, -i, 0);
         } else {
            itemstack = Witchery.Items.GENERIC.itemKobolditeNugget.createStack(i);
            itemstack1 = new ItemStack(item, 1, 0);
         }

         list.add(new MerchantRecipe(itemstack, itemstack1));
      }

   }

   private static int quantityToBuyOrSell(Item item, Random rand) {
      Tuple tuple = (Tuple)blacksmithSellingList.get(item);
      return tuple == null?1:(((Integer)tuple.getFirst()).intValue() >= ((Integer)tuple.getSecond()).intValue()?((Integer)tuple.getFirst()).intValue():((Integer)tuple.getFirst()).intValue() + rand.nextInt(((Integer)tuple.getSecond()).intValue() - ((Integer)tuple.getFirst()).intValue()));
   }

   @SideOnly(Side.CLIENT)
   public void handleHealthUpdate(byte par1) {
      if(par1 == 12) {
         this.generateRandomParticles("heart");
      } else if(par1 == 13) {
         this.generateRandomParticles("angryVillager");
      } else if(par1 == 14) {
         this.generateRandomParticles("happyVillager");
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData) {
      par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
      int trade = super.rand.nextInt(5);
      this.setProfession(trade);
      return par1EntityLivingData;
   }

   @SideOnly(Side.CLIENT)
   private void generateRandomParticles(String par1Str) {
      for(int i = 0; i < 5; ++i) {
         double d0 = super.rand.nextGaussian() * 0.02D;
         double d1 = super.rand.nextGaussian() * 0.02D;
         double d2 = super.rand.nextGaussian() * 0.02D;
         super.worldObj.spawnParticle(par1Str, super.posX + (double)(super.rand.nextFloat() * super.width * 2.0F) - (double)super.width, super.posY + 1.0D + (double)(super.rand.nextFloat() * super.height), super.posZ + (double)(super.rand.nextFloat() * super.width * 2.0F) - (double)super.width, d0, d1, d2);
      }

   }

   public void setLookingForHome() {
      this.isLookingForHome = true;
   }

   public EntityGoblin createChild(EntityAgeable par1EntityAgeable) {
      EntityGoblin entityvillager = new EntityGoblin(super.worldObj);
      entityvillager.onSpawnWithEgg((IEntityLivingData)null);
      return entityvillager;
   }

   static {
      villagersSellingList.put(Items.coal, new Tuple(Integer.valueOf(16), Integer.valueOf(24)));
      villagersSellingList.put(Items.iron_ingot, new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
      villagersSellingList.put(Items.gold_ingot, new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
      villagersSellingList.put(Items.diamond, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
      villagersSellingList.put(Items.paper, new Tuple(Integer.valueOf(24), Integer.valueOf(36)));
      villagersSellingList.put(Items.book, new Tuple(Integer.valueOf(11), Integer.valueOf(13)));
      villagersSellingList.put(Items.written_book, new Tuple(Integer.valueOf(1), Integer.valueOf(1)));
      villagersSellingList.put(Items.ender_pearl, new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
      villagersSellingList.put(Items.ender_eye, new Tuple(Integer.valueOf(2), Integer.valueOf(3)));
      villagersSellingList.put(Items.porkchop, new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
      villagersSellingList.put(Items.beef, new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
      villagersSellingList.put(Items.chicken, new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
      villagersSellingList.put(Items.cooked_fished, new Tuple(Integer.valueOf(9), Integer.valueOf(13)));
      villagersSellingList.put(Items.wheat_seeds, new Tuple(Integer.valueOf(34), Integer.valueOf(48)));
      villagersSellingList.put(Items.melon_seeds, new Tuple(Integer.valueOf(30), Integer.valueOf(38)));
      villagersSellingList.put(Items.pumpkin_seeds, new Tuple(Integer.valueOf(30), Integer.valueOf(38)));
      villagersSellingList.put(Items.wheat, new Tuple(Integer.valueOf(18), Integer.valueOf(22)));
      villagersSellingList.put(Item.getItemFromBlock(Blocks.wool), new Tuple(Integer.valueOf(14), Integer.valueOf(22)));
      villagersSellingList.put(Items.rotten_flesh, new Tuple(Integer.valueOf(36), Integer.valueOf(64)));
      blacksmithSellingList.put(Items.flint_and_steel, new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
      blacksmithSellingList.put(Items.shears, new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
      blacksmithSellingList.put(Items.iron_sword, new Tuple(Integer.valueOf(7), Integer.valueOf(11)));
      blacksmithSellingList.put(Items.diamond_sword, new Tuple(Integer.valueOf(12), Integer.valueOf(14)));
      blacksmithSellingList.put(Items.iron_axe, new Tuple(Integer.valueOf(6), Integer.valueOf(8)));
      blacksmithSellingList.put(Items.diamond_axe, new Tuple(Integer.valueOf(9), Integer.valueOf(12)));
      blacksmithSellingList.put(Items.iron_pickaxe, new Tuple(Integer.valueOf(7), Integer.valueOf(9)));
      blacksmithSellingList.put(Items.diamond_pickaxe, new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
      blacksmithSellingList.put(Items.iron_shovel, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
      blacksmithSellingList.put(Items.diamond_shovel, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
      blacksmithSellingList.put(Items.iron_hoe, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
      blacksmithSellingList.put(Items.diamond_hoe, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
      blacksmithSellingList.put(Items.iron_boots, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
      blacksmithSellingList.put(Items.diamond_boots, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
      blacksmithSellingList.put(Items.iron_helmet, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
      blacksmithSellingList.put(Items.diamond_helmet, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
      blacksmithSellingList.put(Items.iron_chestplate, new Tuple(Integer.valueOf(10), Integer.valueOf(14)));
      blacksmithSellingList.put(Items.diamond_chestplate, new Tuple(Integer.valueOf(16), Integer.valueOf(19)));
      blacksmithSellingList.put(Items.iron_leggings, new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
      blacksmithSellingList.put(Items.diamond_leggings, new Tuple(Integer.valueOf(11), Integer.valueOf(14)));
      blacksmithSellingList.put(Items.chainmail_boots, new Tuple(Integer.valueOf(5), Integer.valueOf(7)));
      blacksmithSellingList.put(Items.chainmail_helmet, new Tuple(Integer.valueOf(5), Integer.valueOf(7)));
      blacksmithSellingList.put(Items.chainmail_chestplate, new Tuple(Integer.valueOf(11), Integer.valueOf(15)));
      blacksmithSellingList.put(Items.chainmail_leggings, new Tuple(Integer.valueOf(9), Integer.valueOf(11)));
      blacksmithSellingList.put(Items.bread, new Tuple(Integer.valueOf(-4), Integer.valueOf(-2)));
      blacksmithSellingList.put(Items.melon, new Tuple(Integer.valueOf(-8), Integer.valueOf(-4)));
      blacksmithSellingList.put(Items.apple, new Tuple(Integer.valueOf(-8), Integer.valueOf(-4)));
      blacksmithSellingList.put(Items.cookie, new Tuple(Integer.valueOf(-10), Integer.valueOf(-7)));
      blacksmithSellingList.put(Item.getItemFromBlock(Blocks.glass), new Tuple(Integer.valueOf(-5), Integer.valueOf(-3)));
      blacksmithSellingList.put(Item.getItemFromBlock(Blocks.bookshelf), new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
      blacksmithSellingList.put(Items.leather_chestplate, new Tuple(Integer.valueOf(4), Integer.valueOf(5)));
      blacksmithSellingList.put(Items.leather_boots, new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
      blacksmithSellingList.put(Items.leather_helmet, new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
      blacksmithSellingList.put(Items.leather_leggings, new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
      blacksmithSellingList.put(Items.saddle, new Tuple(Integer.valueOf(6), Integer.valueOf(8)));
      blacksmithSellingList.put(Items.experience_bottle, new Tuple(Integer.valueOf(-4), Integer.valueOf(-1)));
      blacksmithSellingList.put(Items.redstone, new Tuple(Integer.valueOf(-4), Integer.valueOf(-1)));
      blacksmithSellingList.put(Items.compass, new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
      blacksmithSellingList.put(Items.clock, new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
      blacksmithSellingList.put(Item.getItemFromBlock(Blocks.glowstone), new Tuple(Integer.valueOf(-3), Integer.valueOf(-1)));
      blacksmithSellingList.put(Items.cooked_porkchop, new Tuple(Integer.valueOf(-7), Integer.valueOf(-5)));
      blacksmithSellingList.put(Items.cooked_beef, new Tuple(Integer.valueOf(-7), Integer.valueOf(-5)));
      blacksmithSellingList.put(Items.cooked_chicken, new Tuple(Integer.valueOf(-8), Integer.valueOf(-6)));
      blacksmithSellingList.put(Items.ender_eye, new Tuple(Integer.valueOf(7), Integer.valueOf(11)));
      blacksmithSellingList.put(Items.arrow, new Tuple(Integer.valueOf(-12), Integer.valueOf(-8)));
   }
}
