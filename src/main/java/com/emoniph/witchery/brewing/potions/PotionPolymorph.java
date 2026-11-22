package com.emoniph.witchery.brewing.potions;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityOwl;
import com.emoniph.witchery.entity.EntityToad;
import com.emoniph.witchery.entity.EntityWitchCat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.UUID;

public class PotionPolymorph extends PotionBase implements IHandlePreRenderLiving, IHandleRenderLiving, IHandleLivingAttack, IHandleLivingUpdate, IHandleLivingJump {

    public static final int FORM_PIG = 0;
    public static final int FORM_CHICKEN = 1;
    public static final int FORM_TOAD = 2;
    public static final int FORM_OWL = 3;
    public static final int FORM_CAT = 4;

    public static final UUID CAT_SPEED_UUID = UUID.fromString("7a8b9c0d-1234-4567-89ab-cdef01234567");
    public static final AttributeModifier CAT_SPEED_MODIFIER = new AttributeModifier(CAT_SPEED_UUID, "Witchery Cat Polymorph Speed", 0.45D, 2).setSaved(false);

    @SideOnly(Side.CLIENT)
    private static EntityPig dummyPig;
    @SideOnly(Side.CLIENT)
    private static EntityChicken dummyChicken;
    @SideOnly(Side.CLIENT)
    private static EntityToad dummyToad;
    @SideOnly(Side.CLIENT)
    private static EntityOwl dummyOwl;
    @SideOnly(Side.CLIENT)
    private static EntityWitchCat dummyCat;

    public PotionPolymorph(int id, int color) {
        super(id, true, color);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLivingRender(World world, EntityLivingBase entity, Pre event, int amplifier) {
        if (world == null) {
            return;
        }

        if (dummyPig == null || dummyPig.worldObj != world) {
            dummyPig = new EntityPig(world);
        }
        if (dummyChicken == null || dummyChicken.worldObj != world) {
            dummyChicken = new EntityChicken(world);
        }
        if (dummyToad == null || dummyToad.worldObj != world) {
            dummyToad = new EntityToad(world);
        }
        if (dummyOwl == null || dummyOwl.worldObj != world) {
            dummyOwl = new EntityOwl(world);
        }
        if (dummyCat == null || dummyCat.worldObj != world) {
            dummyCat = new EntityWitchCat(world);
        }

        event.setCanceled(true);

        int form = Math.abs(amplifier) % 5;
        EntityLivingBase dummyTarget;
        switch (form) {
            case FORM_PIG:
                dummyTarget = dummyPig;
                break;
            case FORM_CHICKEN:
                dummyTarget = dummyChicken;
                break;
            case FORM_TOAD:
                dummyTarget = dummyToad;
                break;
            case FORM_OWL:
                dummyTarget = dummyOwl;
                break;
            case FORM_CAT:
                dummyTarget = dummyCat;
                break;
            default:
                dummyTarget = dummyPig;
                break;
        }

        dummyTarget.copyDataFrom(entity, true);
        dummyTarget.renderYawOffset = entity.renderYawOffset;
        dummyTarget.rotationYawHead = entity.rotationYawHead;
        dummyTarget.prevRenderYawOffset = entity.prevRenderYawOffset;
        dummyTarget.prevRotationYawHead = entity.prevRotationYawHead;

        GL11.glPushMatrix();
        RenderManager.instance.renderEntityWithPosYaw(dummyTarget, event.x, event.y, event.z, 0, 0.0f);
        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLivingRender(World world, EntityLivingBase entity, Post event, int amplifier) {
    }

    public boolean handleAllHurtEvents() {
        return false;
    }

    @Override
    public void onLivingAttack(World world, EntityLivingBase entity, LivingAttackEvent event, int amplifier) {
        if (!world.isRemote && event.source != null && event.source.getEntity() != null) {
            if (event.source.getEntity() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();
                if (attacker.isPotionActive(this)) {
                    // Transformed creatures cannot attack
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void onLivingUpdate(World world, EntityLivingBase entity, LivingUpdateEvent event, int amplifier, int duration) {
        int form = Math.abs(amplifier) % 5;

        // Sound effects
        if (world.getTotalWorldTime() % 70L == 12L && world.rand.nextInt(3) == 0) {
            switch (form) {
                case FORM_PIG:
                    world.playSoundAtEntity(entity, "mob.pig.say", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                    break;
                case FORM_CHICKEN:
                    world.playSoundAtEntity(entity, "mob.chicken.say", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                    break;
                case FORM_TOAD:
                    world.playSoundAtEntity(entity, "witchery:mob.toad.toad_croak", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                    break;
                case FORM_OWL:
                    world.playSoundAtEntity(entity, "witchery:mob.owl.owl_hoot", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                    break;
                case FORM_CAT:
                    world.playSoundAtEntity(entity, "mob.cat.meow", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                    break;
            }
        }

        // Adjust eye height for players
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.getEntityData().setBoolean("WITCPolymorphEye", true);
            switch (form) {
                case FORM_PIG:
                case FORM_CAT:
                    player.eyeHeight = 0.8F;
                    break;
                case FORM_CHICKEN:
                case FORM_TOAD:
                    player.eyeHeight = 0.4F;
                    break;
                case FORM_OWL:
                    player.eyeHeight = 0.5F;
                    break;
            }
        }

        // Mechanics per form
        IAttributeInstance moveSpeedAttr = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (moveSpeedAttr != null) {
            if (form == FORM_CAT) {
                if (moveSpeedAttr.getModifier(CAT_SPEED_UUID) == null) {
                    moveSpeedAttr.applyModifier(CAT_SPEED_MODIFIER);
                }
            } else if (moveSpeedAttr.getModifier(CAT_SPEED_UUID) != null) {
                moveSpeedAttr.removeModifier(CAT_SPEED_MODIFIER);
            }
        }

        if (entity instanceof EntityPlayer && form != FORM_OWL) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getEntityData().getBoolean("WITCPolymorphFlight")) {
                player.getEntityData().removeTag("WITCPolymorphFlight");
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    if (!world.isRemote) {
                        player.sendPlayerAbilities();
                    }
                }
            }
        }

        if (form == FORM_CHICKEN) {
            // Feather falling / slow fall
            if (entity.motionY < -0.08D) {
                entity.motionY = -0.08D;
            }
            entity.fallDistance = 0.0F;
        } else if (form == FORM_OWL) {
            // Owl flight + feather falling
            entity.fallDistance = 0.0F;
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                player.getEntityData().setBoolean("WITCPolymorphFlight", true);
                if (!player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                    if (!world.isRemote) {
                        player.sendPlayerAbilities();
                    }
                }
                if (!player.capabilities.isFlying && entity.motionY < -0.08D) {
                    entity.motionY = -0.08D;
                }
            } else if (entity.motionY < -0.08D) {
                entity.motionY = -0.08D;
            }
        } else if (form == FORM_TOAD) {
            // Better water movement + reduced fall damage
            entity.fallDistance = 0.0F;
            if (entity.isInWater()) {
                entity.motionX *= 1.15D;
                entity.motionZ *= 1.15D;
            }
        } else if (form == FORM_CAT) {
            // Cats take no fall damage and scare away creepers
            entity.fallDistance = 0.0F;
            if (!world.isRemote) {
                List creepers = world.getEntitiesWithinAABB(EntityCreeper.class, entity.boundingBox.expand(6.0D, 3.0D, 6.0D));
                for (Object obj : creepers) {
                    if (obj instanceof EntityCreeper) {
                        EntityCreeper creeper = (EntityCreeper) obj;
                        if (creeper.getAttackTarget() == entity) {
                            creeper.setAttackTarget(null);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onLivingJump(World world, EntityLivingBase entity, LivingJumpEvent event, int amplifier) {
        int form = Math.abs(amplifier) % 5;
        if (form == FORM_TOAD) {
            entity.motionY += 0.28D;
        } else if (form == FORM_CAT) {
            entity.motionY += 0.14D;
        } else if (form == FORM_OWL && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.capabilities.isFlying) {
                entity.motionY += 0.18D;
            }
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, BaseAttributeMap attributes, int amplifier) {
        IAttributeInstance moveSpeedAttr = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (moveSpeedAttr != null && moveSpeedAttr.getModifier(CAT_SPEED_UUID) != null) {
            moveSpeedAttr.removeModifier(CAT_SPEED_MODIFIER);
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.eyeHeight = player.getDefaultEyeHeight();
            if (player.getEntityData().getBoolean("WITCPolymorphFlight")) {
                player.getEntityData().removeTag("WITCPolymorphFlight");
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    if (!player.worldObj.isRemote) {
                        player.sendPlayerAbilities();
                    }
                }
            }
        }
        super.removeAttributesModifiersFromEntity(entity, attributes, amplifier);
    }
}
