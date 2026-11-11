package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.client.model.ModelOwl;
import com.emoniph.witchery.client.model.ModelToad;
import com.emoniph.witchery.entity.EntityRivalFamiliar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderRivalFamiliar extends RenderLiving {

    private static final ResourceLocation TEXTURE_TOAD = new ResourceLocation("witchery", "textures/entities/toad.png");
    private static final ResourceLocation TEXTURE_OWL = new ResourceLocation("witchery", "textures/entities/owl.png");
    private static final ResourceLocation TEXTURE_CAT = new ResourceLocation("witchery", "textures/entities/blackcat.png");

    private final ModelToad modelToad = new ModelToad();
    private final ModelOwl modelOwl = new ModelOwl();
    private final ModelOcelot modelCat = new ModelOcelot();

    public RenderRivalFamiliar() {
        super(new ModelToad(), 0.4F);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof EntityRivalFamiliar) {
            EntityRivalFamiliar familiar = (EntityRivalFamiliar) entity;
            switch (familiar.getFamiliarType()) {
                case EntityRivalFamiliar.TYPE_TOAD:
                    this.mainModel = this.modelToad;
                    this.shadowSize = 0.4F;
                    break;
                case EntityRivalFamiliar.TYPE_OWL:
                    this.mainModel = this.modelOwl;
                    this.shadowSize = 0.3F;
                    break;
                case EntityRivalFamiliar.TYPE_CAT:
                default:
                    this.mainModel = this.modelCat;
                    this.shadowSize = 0.4F;
                    break;
            }
        }
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityRivalFamiliar) {
            EntityRivalFamiliar familiar = (EntityRivalFamiliar) entity;
            switch (familiar.getFamiliarType()) {
                case EntityRivalFamiliar.TYPE_TOAD:
                    return TEXTURE_TOAD;
                case EntityRivalFamiliar.TYPE_OWL:
                    return TEXTURE_OWL;
                case EntityRivalFamiliar.TYPE_CAT:
                default:
                    return TEXTURE_CAT;
            }
        }
        return TEXTURE_TOAD;
    }
}
