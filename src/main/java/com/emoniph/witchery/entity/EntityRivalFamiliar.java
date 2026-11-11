package com.emoniph.witchery.entity;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Familiar de combate hostil perteneciente a aquelarres rivales.
 * Especializado en tres formas:
 * 0 - Sapo Tóxico: Aplica veneno y lentitud; salta hacia la víctima.
 * 1 - Búho de Asalto: Muy veloz, ataca desde el aire cegando y mareando.
 * 2 - Gato de Sombras: Sigiloso y letal; drena energía de infusión del jugador con cada golpe.
 */
public class EntityRivalFamiliar extends EntityMob {

    public static final int TYPE_TOAD = 0;
    public static final int TYPE_OWL = 1;
    public static final int TYPE_CAT = 2;

    public EntityRivalFamiliar(World world) {
        super(world);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);

        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.25D, true));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityTameable.class, 0, false));

        this.experienceValue = 15;
    }

    public EntityRivalFamiliar(World world, int type) {
        this(world);
        this.setFamiliarType(type);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
    }

    public int getFamiliarType() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    public void setFamiliarType(int type) {
        this.dataWatcher.updateObject(16, Byte.valueOf((byte) MathHelper.clamp_int(type, 0, 2)));
        this.updateAttributesForType();
    }

    private void updateAttributesForType() {
        int type = getFamiliarType();
        switch (type) {
            case TYPE_TOAD:
                this.setSize(0.8F, 0.8F);
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
                break;
            case TYPE_OWL:
                this.setSize(0.5F, 0.6F);
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(18.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
                break;
            case TYPE_CAT:
            default:
                this.setSize(0.6F, 0.7F);
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(22.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.38D);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
                break;
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(22.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.32D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean success = super.attackEntityAsMob(target);
        if (success && target instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) target;
            int type = getFamiliarType();

            if (type == TYPE_TOAD) {
                living.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));
                living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 1));
                ParticleEffect.SPELL_COLORED.send(SoundEffect.MOB_SLIME_SMALL, living, 0.8D, 0.8D, 16, 0x33AA33);
            } else if (type == TYPE_OWL) {
                living.addPotionEffect(new PotionEffect(Potion.blindness.id, 80, 0));
                living.addPotionEffect(new PotionEffect(Potion.confusion.id, 120, 0));
                ParticleEffect.SMOKE.send(SoundEffect.RANDOM_BREATH, living, 0.8D, 1.2D, 12);
            } else if (type == TYPE_CAT) {
                if (living instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) living;
                    int curEnergy = Infusion.getCurrentEnergy(p);
                    if (curEnergy > 0) {
                        Infusion.setCurrentEnergy(p, Math.max(0, curEnergy - 120));
                        ParticleEffect.PORTAL.send(SoundEffect.MOB_OCELOT_DEATH, p, 1.0D, 1.5D, 20);
                    }
                }
                living.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 0));
            }
        }
        return success;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote) {
            EntityLivingBase target = this.getAttackTarget();
            int type = getFamiliarType();

            // Salto agresivo del sapo hacia el objetivo
            if (type == TYPE_TOAD && target != null && this.onGround && this.rand.nextInt(15) == 0) {
                double dx = target.posX - this.posX;
                double dz = target.posZ - this.posZ;
                float dist = MathHelper.sqrt_double(dx * dx + dz * dz);
                if (dist > 2.0F && dist < 8.0F) {
                    this.motionX = (dx / dist) * 0.7D;
                    this.motionY = 0.45D;
                    this.motionZ = (dz / dist) * 0.7D;
                    this.worldObj.playSoundAtEntity(this, "mob.slime.small", 1.0F, 0.8F);
                }
            }

            // Flotación y planeo rápido del búho
            if (type == TYPE_OWL && this.motionY < 0.0D) {
                this.motionY *= 0.6D;
                this.fallDistance = 0.0F;
            }
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!this.worldObj.isRemote) {
            int type = getFamiliarType();
            if (type == TYPE_TOAD) {
                ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_POP, this, 1.5D, 1.0D, 30, 0x228822);
            } else if (type == TYPE_OWL) {
                ParticleEffect.SMOKE.send(SoundEffect.RANDOM_BREATH, this, 1.0D, 1.0D, 20);
            } else if (type == TYPE_CAT) {
                ParticleEffect.PORTAL.send(SoundEffect.WITCHERY_RANDOM_POOF, this, 1.0D, 1.0D, 20);
            }
        }
    }

    @Override
    protected void dropFewItems(boolean hitRecently, int looting) {
        int type = getFamiliarType();
        int count = 1 + this.rand.nextInt(2 + looting);

        if (type == TYPE_TOAD) {
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, count, Witchery.Items.GENERIC.itemToeOfFrog.damageValue), 0.0F);
        } else if (type == TYPE_OWL) {
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, count, Witchery.Items.GENERIC.itemOwletsWing.damageValue), 0.0F);
            this.dropItem(Items.feather, count);
        } else if (type == TYPE_CAT) {
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, count, Witchery.Items.GENERIC.itemBatWool.damageValue), 0.0F);
            this.dropItem(Items.string, count);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("FamiliarType", (byte) getFamiliarType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setFamiliarType(nbt.getByte("FamiliarType"));
    }

    @Override
    public String getCommandSenderName() {
        if (this.hasCustomNameTag()) {
            return this.getCustomNameTag();
        }
        int type = getFamiliarType();
        switch (type) {
            case TYPE_TOAD: return "Sapo de Aquelarre";
            case TYPE_OWL: return "Búho Vigía Oscuro";
            case TYPE_CAT: default: return "Gato de las Sombras";
        }
    }
}
