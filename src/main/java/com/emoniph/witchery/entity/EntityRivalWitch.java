package com.emoniph.witchery.entity;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.item.ItemGeneral;
import com.emoniph.witchery.util.IHandleDT;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

/**
 * EntityRivalWitch: Brujas rivales especializadas con IA táctica de combate arcano.
 * 
 * Arquetipos:
 * 0 - ARCHETYPE_HERBOLOGIST: Maestra de plantas, espinas, mandrágoras vivas y control de masas.
 * 1 - ARCHETYPE_ALCHEMIST: Artillería de caldero, gases venenosos, pociones ácidas y debilitantes.
 * 2 - ARCHETYPE_NECROMANCER: Magia negra con Sectumsempra, invocación de espectros/no-muertos y vudú poppet.
 * 3 - ARCHETYPE_MATRIARCH: Boss - Aspirante al Trono de Bruja Suprema. Usa varita con hechizos avanzados
 *     (Flipendo, Incendio, Stupefy, Impedimenta, Glacius), escudo reflectante Protego y evasión en sombras.
 */
public class EntityRivalWitch extends EntityMob implements IRangedAttackMob, IBossDisplayData, IEntitySelector, IHandleDT {

    public static final int ARCHETYPE_HERBOLOGIST = 0;
    public static final int ARCHETYPE_ALCHEMIST = 1;
    public static final int ARCHETYPE_NECROMANCER = 2;
    public static final int ARCHETYPE_MATRIARCH = 3;

    private static final String[] MATRIARCH_NAMES = new String[]{
            "Morwenna la Devoradora", "Morgana del Vacío", "Alcina de la Luna Sangrienta",
            "Lilith la Hereje", "Desdemona la Espina Negra", "Grimhilda la Inmortal",
            "Vespera la Sin Piedad", "Hecate de las Ruinas", "Carmilla la Tenebrosa"
    };

    private int attackCooldown = 0;
    private int shieldCooldown = 0;
    private int shieldDuration = 0;
    private int teleportCooldown = 0;
    private int summonCooldown = 0;
    private int poppetCurseTimer = 0;

    public EntityRivalWitch(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);

        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 35, 18.0F));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 12.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, true, this));

        this.experienceValue = 35;
    }

    public EntityRivalWitch(World world, int archetype) {
        this(world);
        this.setArchetype(archetype);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, Byte.valueOf((byte) 0)); // Archetype
        this.dataWatcher.addObject(21, Byte.valueOf((byte) 0)); // IsShieldActive (Protego)
        this.dataWatcher.addObject(22, "");                    // Custom Matriarch Name
    }

    public int getArchetype() {
        return this.dataWatcher.getWatchableObjectByte(20);
    }

    public void setArchetype(int archetype) {
        int clamped = MathHelper.clamp_int(archetype, 0, 3);
        this.dataWatcher.updateObject(20, Byte.valueOf((byte) clamped));

        if (clamped == ARCHETYPE_MATRIARCH) {
            String name = MATRIARCH_NAMES[this.rand.nextInt(MATRIARCH_NAMES.length)];
            this.setMatriarchName(name);
            this.experienceValue = 120;
        }

        this.applyArchetypeAttributes();
    }

    public boolean isShieldActive() {
        return this.dataWatcher.getWatchableObjectByte(21) == 1;
    }

    public void setShieldActive(boolean active) {
        this.dataWatcher.updateObject(21, Byte.valueOf((byte) (active ? 1 : 0)));
    }

    public String getMatriarchName() {
        return this.dataWatcher.getWatchableObjectString(22);
    }

    public void setMatriarchName(String name) {
        this.dataWatcher.updateObject(22, name != null ? name : "");
    }

    public boolean isMatriarch() {
        return getArchetype() == ARCHETYPE_MATRIARCH;
    }

    private void applyArchetypeAttributes() {
        int arch = getArchetype();
        switch (arch) {
            case ARCHETYPE_MATRIARCH:
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.32D);
                this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
                break;
            case ARCHETYPE_HERBOLOGIST:
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
                this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(28.0D);
                break;
            case ARCHETYPE_ALCHEMIST:
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
                this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30.0D);
                break;
            case ARCHETYPE_NECROMANCER:
            default:
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(65.0D);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.29D);
                this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);
                break;
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote) {
            // Manejo del escudo Protego
            if (isShieldActive()) {
                if (--this.shieldDuration <= 0) {
                    this.setShieldActive(false);
                    this.shieldCooldown = 160 + this.rand.nextInt(80); // 8-12 seg cooldown
                } else if (this.ticksExisted % 4 == 0) {
                    ParticleEffect.SPELL_COLORED.send(SoundEffect.NONE, this, 0.8D, 1.8D, 8, 0x8844FF);
                }
            } else if (this.shieldCooldown > 0) {
                this.shieldCooldown--;
            }

            if (this.teleportCooldown > 0) this.teleportCooldown--;
            if (this.summonCooldown > 0) this.summonCooldown--;

            EntityLivingBase target = this.getAttackTarget();
            if (target != null && !target.isDead) {
                double distSq = this.getDistanceSqToEntity(target);

                // Auto-curación con poción cuando baja de vida
                if (this.getHealth() < this.getMaxHealth() * 0.35F && this.rand.nextInt(100) == 0) {
                    this.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 1));
                    this.worldObj.playSoundAtEntity(this, "witchery:random.drink", 1.0F, 1.0F);
                    ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_POP, this, 0.6D, 1.5D, 12, 0xFF2222);
                }

                // Matriarca: Activar Protego reactivamente si el objetivo apunta o está casteando
                if (isMatriarch() && !isShieldActive() && this.shieldCooldown <= 0 && this.rand.nextInt(40) == 0) {
                    this.setShieldActive(true);
                    this.shieldDuration = 60; // 3 segundos de escudo invulnerable
                    this.worldObj.playSoundAtEntity(this, "witchery:random.hypnosis", 1.0F, 1.2F);
                    ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERDRAGON_GROWL, this, 1.0D, 2.0D, 25);
                }

                // Matriarca: Evasión en sombras si el jugador está muy cerca
                if (isMatriarch() && distSq < 16.0D && this.teleportCooldown <= 0) {
                    this.teleportCooldown = 100;
                    this.teleportAwayFrom(target);
                }

                // Habilidades especiales por arquetipo
                int arch = getArchetype();

                // 1. Herbolaria: Invoca Mandrágora rabiosa
                if (arch == ARCHETYPE_HERBOLOGIST && this.summonCooldown <= 0 && this.rand.nextInt(120) == 0) {
                    this.summonCooldown = 250;
                    EntityMandrake mandrake = new EntityMandrake(this.worldObj);
                    mandrake.setLocationAndAngles(this.posX + (rand.nextDouble() - 0.5D) * 2.0D, this.posY, this.posZ + (rand.nextDouble() - 0.5D) * 2.0D, 0.0F, 0.0F);
                    mandrake.setAttackTarget(target);
                    this.worldObj.spawnEntityInWorld(mandrake);
                    ParticleEffect.SMOKE.send(SoundEffect.WITCHERY_RANDOM_POOF, mandrake, 0.8D, 1.0D, 16);
                }

                // 2. Nigromante: Invoca no-muertos / arañas de cueva y usa Poppet Curse
                if (arch == ARCHETYPE_NECROMANCER) {
                    if (this.summonCooldown <= 0 && this.rand.nextInt(140) == 0) {
                        this.summonCooldown = 280;
                        EntityCaveSpider spider = new EntityCaveSpider(this.worldObj);
                        spider.setLocationAndAngles(this.posX + (rand.nextDouble() - 0.5D) * 2.0D, this.posY, this.posZ + (rand.nextDouble() - 0.5D) * 2.0D, 0.0F, 0.0F);
                        spider.setAttackTarget(target);
                        this.worldObj.spawnEntityInWorld(spider);
                        ParticleEffect.PORTAL.send(SoundEffect.MOB_ZOMBIE_SAY, spider, 0.8D, 1.0D, 16);
                    }

                    // Vudú Poppet: si el jugador no la ataca en 7 segundos, inflige daño directo perforante
                    if (++this.poppetCurseTimer >= 140) {
                        this.poppetCurseTimer = 0;
                        target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, target), 6.0F);
                        this.worldObj.playSoundAtEntity(target, "witchery:mob.imp.laugh", 1.0F, 0.9F);
                        ParticleEffect.PORTAL.send(SoundEffect.WITCHERY_RANDOM_CLICK, target, 1.0D, 1.8D, 24);
                        if (target instanceof EntityPlayer) {
                            ((EntityPlayer) target).addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "¡La Bruja Nigromante ha clavado un alfiler en tu muñeco vudú!"));
                        }
                    }
                } else {
                    this.poppetCurseTimer = 0;
                }
            }
        }
    }

    private void teleportAwayFrom(Entity target) {
        double dx = this.posX - target.posX;
        double dz = this.posZ - target.posZ;
        double len = MathHelper.sqrt_double(dx * dx + dz * dz);
        if (len < 0.001D) {
            dx = 1.0D;
            dz = 0.0D;
            len = 1.0D;
        }

        double destX = this.posX + (dx / len) * (8.0D + this.rand.nextDouble() * 6.0D);
        double destZ = this.posZ + (dz / len) * (8.0D + this.rand.nextDouble() * 6.0D);
        double destY = this.posY;

        int blockX = MathHelper.floor_double(destX);
        int blockY = MathHelper.floor_double(destY);
        int blockZ = MathHelper.floor_double(destZ);

        if (this.worldObj.blockExists(blockX, blockY, blockZ)) {
            ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, this, 1.0D, 2.0D, 20);
            this.setPosition(destX, destY, destZ);
            ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, this, 1.0D, 2.0D, 20);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        // Escudo Protego activo: Bloquea o refleja casi todo el daño
        if (isShieldActive()) {
            if (source.getEntity() != null) {
                source.getEntity().attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, source.getEntity()), damage * 0.4F);
            }
            this.worldObj.playSoundAtEntity(this, "random.anvil_land", 0.8F, 1.6F);
            ParticleEffect.SPELL_COLORED.send(SoundEffect.NONE, this, 1.0D, 2.0D, 20, 0xAA00FF);
            return false;
        }

        // Resistencia natural a magia de bruja
        if (source.isMagicDamage()) {
            damage *= 0.35F;
        }

        boolean result = super.attackEntityFrom(source, Math.min(damage, isMatriarch() ? 25.0F : 40.0F));

        // Interrumpir poppet curse si recibe daño
        this.poppetCurseTimer = Math.max(0, this.poppetCurseTimer - 30);

        return result;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float range) {
        if (this.worldObj.isRemote || isShieldActive()) return;

        double dX = target.posX - this.posX;
        double dY = target.posY + (double) target.getEyeHeight() - 1.1D - this.posY;
        double dZ = target.posZ - this.posZ;
        float dist = MathHelper.sqrt_double(dX * dX + dZ * dZ);

        int arch = getArchetype();

        switch (arch) {
            case ARCHETYPE_HERBOLOGIST: {
                // Lanza brebajes botánicos (espinas, redes de enredaderas, etc.)
                ItemGeneral.SubItem brew = (this.rand.nextInt(3) == 0) ?
                        Witchery.Items.GENERIC.itemBrewOfThorns :
                        Witchery.Items.GENERIC.itemBrewOfWebs;
                EntityWitchProjectile proj = new EntityWitchProjectile(this.worldObj, this, brew);
                proj.setThrowableHeading(dX, dY + (double) (dist * 0.18F), dZ, 0.8F, 6.0F);
                this.worldObj.spawnEntityInWorld(proj);
                this.worldObj.playSoundAtEntity(this, "random.bow", 0.5F, 0.4F / (this.rand.nextFloat() * 0.4F + 0.8F));
                break;
            }

            case ARCHETYPE_ALCHEMIST: {
                // Lanza brebajes de lengua de sapo, de desgaste o pociones de daño/veneno
                if (this.rand.nextBoolean()) {
                    ItemGeneral.SubItem brew = (this.rand.nextInt(3) == 0) ?
                            Witchery.Items.GENERIC.itemBrewOfWasting :
                            Witchery.Items.GENERIC.itemBrewOfFrogsTongue;
                    EntityWitchProjectile proj = new EntityWitchProjectile(this.worldObj, this, brew);
                    proj.setThrowableHeading(dX, dY + (double) (dist * 0.18F), dZ, 0.85F, 5.0F);
                    this.worldObj.spawnEntityInWorld(proj);
                } else {
                    EntityPotion potion = new EntityPotion(this.worldObj, this, 32660); // Poison / Harming
                    potion.setThrowableHeading(dX, dY + (double) (dist * 0.18F), dZ, 0.8F, 6.0F);
                    this.worldObj.spawnEntityInWorld(potion);
                }
                this.worldObj.playSoundAtEntity(this, "random.bow", 0.5F, 0.6F);
                break;
            }

            case ARCHETYPE_NECROMANCER: {
                // Dispara proyectil de hechizo oscuro: Sectumsempra o Crucio
                SymbolEffect spell = (this.rand.nextInt(3) == 0) ? EffectRegistry.Crucio : EffectRegistry.Sectumsempra;
                EntitySpellEffect spellProj = new EntitySpellEffect(this.worldObj, this, dX, dY, dZ, spell, 1);
                this.worldObj.spawnEntityInWorld(spellProj);
                this.worldObj.playSoundAtEntity(this, "witchery:mob.spectre.spectre_say", 1.0F, 1.2F);
                break;
            }

            case ARCHETYPE_MATRIARCH:
            default: {
                // Duelo de hechizos con varita mágica: Flipendo, Incendio, Stupefy, Impedimenta, Glacius
                SymbolEffect spell;
                int roll = this.rand.nextInt(5);
                switch (roll) {
                    case 0: spell = EffectRegistry.Flipendo; break;
                    case 1: spell = EffectRegistry.Incendio; break;
                    case 2: spell = EffectRegistry.Glacius; break;
                    case 3: spell = EffectRegistry.Impedimenta; break;
                    case 4: default: spell = EffectRegistry.Stupefy; break;
                }

                EntitySpellEffect spellProj = new EntitySpellEffect(this.worldObj, this, dX, dY, dZ, spell, 2);
                this.worldObj.spawnEntityInWorld(spellProj);
                this.worldObj.playSoundAtEntity(this, "witchery:random.hypnosis", 0.9F, 1.4F);

                // Disparo secundario rápido ocasional
                if (this.rand.nextInt(3) == 0) {
                    EntityWitchProjectile brew = new EntityWitchProjectile(this.worldObj, this, Witchery.Items.GENERIC.itemBrewOfThorns);
                    brew.setThrowableHeading(dX + (rand.nextDouble() - 0.5D), dY, dZ + (rand.nextDouble() - 0.5D), 0.75F, 8.0F);
                    this.worldObj.spawnEntityInWorld(brew);
                }
                break;
            }
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.worldObj.isRemote) {
            ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, this, 1.2D, 2.2D, 35);

            if (isMatriarch()) {
                Entity killer = source.getEntity();
                String killerName = (killer instanceof EntityPlayer) ? ((EntityPlayer) killer).getCommandSenderName() : "Un brujo audaz";
                String matriarchName = getMatriarchName();

                MinecraftServer server = MinecraftServer.getServer();
                if (server != null && server.getConfigurationManager() != null) {
                    server.getConfigurationManager().sendChatMsg(new ChatComponentText(
                            EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "★ [Victoria Arcana] " +
                            EnumChatFormatting.LIGHT_PURPLE + "¡" + EnumChatFormatting.YELLOW + killerName +
                            EnumChatFormatting.LIGHT_PURPLE + " ha derrotado a la " + EnumChatFormatting.DARK_PURPLE + matriarchName +
                            EnumChatFormatting.LIGHT_PURPLE + " y salvaguardado el trono de la Bruja Suprema!"));
                }

                if (killer instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) killer;
                    SupremeWitchData.updateSupremeStatus(p.worldObj);
                }
            }
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entity) {
        super.onKillEntity(entity);

        // Si la matriarca mata al jugador: ¡Destronamiento y humillación!
        if (!this.worldObj.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            String matriarchName = isMatriarch() ? getMatriarchName() : "El Aquelarre Rival";

            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.getConfigurationManager() != null) {
                server.getConfigurationManager().sendChatMsg(new ChatComponentText(
                        EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "☠ [Destronamiento] " +
                        EnumChatFormatting.DARK_PURPLE + matriarchName +
                        EnumChatFormatting.YELLOW + " ha doblegado a " + player.getCommandSenderName() +
                        EnumChatFormatting.LIGHT_PURPLE + ". ¡Las brujas oscuras reclaman el dominio del bosque!"));
            }

            this.worldObj.playSoundAtEntity(this, "witchery:mob.imp.laugh", 1.5F, 0.8F);

            // Si el jugador caído era la Bruja Suprema, se le despoja del trono
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data != null && data.isSupreme(player)) {
                data.clearSupreme();
            }
        }
    }

    @Override
    protected void dropFewItems(boolean hitRecently, int looting) {
        int arch = getArchetype();

        if (arch == ARCHETYPE_MATRIARCH) {
            // Recompensas legendarias de boss
            this.entityDropItem(new ItemStack(Witchery.Items.BABAS_HAT), 0.0F);
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 1 + rand.nextInt(2), Witchery.Items.GENERIC.itemDemonHeart.damageValue), 0.0F);
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 2 + rand.nextInt(3), Witchery.Items.GENERIC.itemNecroStone.damageValue), 0.0F);
            this.entityDropItem(new ItemStack(Items.emerald, 3 + rand.nextInt(4 + looting)), 0.0F);
            this.entityDropItem(new ItemStack(Items.diamond, 1 + rand.nextInt(2 + looting)), 0.0F);
        } else if (arch == ARCHETYPE_HERBOLOGIST) {
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 2 + rand.nextInt(3), Witchery.Items.GENERIC.itemMandrakeRoot.damageValue), 0.0F);
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 2 + rand.nextInt(3), Witchery.Items.GENERIC.itemBelladonnaFlower.damageValue), 0.0F);
        } else if (arch == ARCHETYPE_ALCHEMIST) {
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 1 + rand.nextInt(2), Witchery.Items.GENERIC.itemBrewGrotesque.damageValue), 0.0F);
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 2 + rand.nextInt(3), Witchery.Items.GENERIC.itemTearOfTheGoddess.damageValue), 0.0F);
            this.dropItem(Items.glowstone_dust, 2 + rand.nextInt(3));
        } else {
            this.entityDropItem(new ItemStack(Witchery.Items.GENERIC, 1 + rand.nextInt(2), Witchery.Items.GENERIC.itemSpectralDust.damageValue), 0.0F);
            this.dropItem(Items.bone, 3 + rand.nextInt(4));
        }
    }

    @Override
    public float getCapDT(DamageSource damageSource, float damage) {
        return isMatriarch() ? 25.0F : 40.0F;
    }

    @Override
    public boolean isEntityApplicable(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    @Override
    public String getCommandSenderName() {
        if (this.hasCustomNameTag()) {
            return this.getCustomNameTag();
        }
        int arch = getArchetype();
        switch (arch) {
            case ARCHETYPE_MATRIARCH:
                String name = getMatriarchName();
                return (name != null && !name.isEmpty()) ? name : "Matriarca del Aquelarre";
            case ARCHETYPE_HERBOLOGIST:
                return "Bruja Herbolaria Hostil";
            case ARCHETYPE_ALCHEMIST:
                return "Bruja Alquimista Hostil";
            case ARCHETYPE_NECROMANCER:
            default:
                return "Bruja Nigromante Hostil";
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("Archetype", (byte) getArchetype());
        nbt.setString("MatriarchName", getMatriarchName());
        nbt.setInteger("ShieldCooldown", this.shieldCooldown);
        nbt.setInteger("SummonCooldown", this.summonCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setArchetype(nbt.getByte("Archetype"));
        if (nbt.hasKey("MatriarchName")) {
            this.setMatriarchName(nbt.getString("MatriarchName"));
        }
        this.shieldCooldown = nbt.getInteger("ShieldCooldown");
        this.summonCooldown = nbt.getInteger("SummonCooldown");
    }
}
