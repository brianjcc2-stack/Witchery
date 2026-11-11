package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.entity.EntityRivalWitch;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderRivalWitch extends RenderLiving {

    private static final ResourceLocation TEXTURE_MATRIARCH = new ResourceLocation("witchery", "textures/entities/covenwitch1.png");
    private static final ResourceLocation TEXTURE_HERBOLOGIST = new ResourceLocation("witchery", "textures/entities/covenwitch3.png");
    private static final ResourceLocation TEXTURE_ALCHEMIST = new ResourceLocation("witchery", "textures/entities/apothecary.png");
    private static final ResourceLocation TEXTURE_NECROMANCER = new ResourceLocation("witchery", "textures/entities/covenwitch4.png");

    public RenderRivalWitch() {
        super(new ModelWitch(0.0F), 0.5F);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof EntityRivalWitch) {
            EntityRivalWitch witch = (EntityRivalWitch) entity;
            if (witch.isMatriarch()) {
                BossStatus.setBossStatus(witch, true);
            }
        }
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
        super.preRenderCallback(entity, partialTicks);
        if (entity instanceof EntityRivalWitch) {
            EntityRivalWitch witch = (EntityRivalWitch) entity;
            if (witch.isMatriarch()) {
                // Matriarca ligeramente más imponente
                GL11.glScalef(1.15F, 1.15F, 1.15F);
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityRivalWitch) {
            EntityRivalWitch witch = (EntityRivalWitch) entity;
            switch (witch.getArchetype()) {
                case EntityRivalWitch.ARCHETYPE_MATRIARCH:
                    return TEXTURE_MATRIARCH;
                case EntityRivalWitch.ARCHETYPE_HERBOLOGIST:
                    return TEXTURE_HERBOLOGIST;
                case EntityRivalWitch.ARCHETYPE_ALCHEMIST:
                    return TEXTURE_ALCHEMIST;
                case EntityRivalWitch.ARCHETYPE_NECROMANCER:
                default:
                    return TEXTURE_NECROMANCER;
            }
        }
        return TEXTURE_MATRIARCH;
    }
}
