package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelGrimoireLectern extends ModelBase {

    public ModelRenderer base;
    public ModelRenderer pillar;
    public ModelRenderer collar;
    public ModelRenderer desk;
    public ModelRenderer ledge;
    public ModelRenderer bookLeft;
    public ModelRenderer bookRight;
    public ModelRenderer bookSpine;

    public ModelGrimoireLectern() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.base = new ModelRenderer(this, 0, 0);
        this.base.addBox(-5.0F, 22.0F, -5.0F, 10, 2, 10);

        this.pillar = new ModelRenderer(this, 0, 22);
        this.pillar.addBox(-2.0F, 8.0F, -2.0F, 4, 14, 4);

        this.collar = new ModelRenderer(this, 16, 22);
        this.collar.addBox(-3.5F, 6.0F, -3.5F, 7, 2, 7);

        // Slanted desk surface
        this.desk = new ModelRenderer(this, 0, 35);
        this.desk.addBox(-6.0F, -0.5F, -5.0F, 12, 1, 10);
        this.desk.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.desk.rotateAngleX = 0.35F;

        this.ledge = new ModelRenderer(this, 0, 48);
        this.ledge.addBox(-6.0F, -1.5F, 4.0F, 12, 2, 1);
        this.ledge.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.ledge.rotateAngleX = 0.35F;

        // Open book
        this.bookLeft = new ModelRenderer(this, 35, 35);
        this.bookLeft.addBox(-5.0F, -1.2F, -4.0F, 5, 1, 8);
        this.bookLeft.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.bookLeft.rotateAngleX = 0.35F;

        this.bookRight = new ModelRenderer(this, 35, 35);
        this.bookRight.addBox(0.0F, -1.2F, -4.0F, 5, 1, 8);
        this.bookRight.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.bookRight.rotateAngleX = 0.35F;

        this.bookSpine = new ModelRenderer(this, 35, 45);
        this.bookSpine.addBox(-0.5F, -0.8F, -4.0F, 1, 1, 8);
        this.bookSpine.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.bookSpine.rotateAngleX = 0.35F;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.base.render(f5);
        this.pillar.render(f5);
        this.collar.render(f5);
        this.desk.render(f5);
        this.ledge.render(f5);
        this.bookLeft.render(f5);
        this.bookRight.render(f5);
        this.bookSpine.render(f5);
    }
}

