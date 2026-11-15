package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSpiritChimes extends ModelBase {

    public ModelRenderer ceilingMount;
    public ModelRenderer cord1;
    public ModelRenderer cord2;
    public ModelRenderer cord3;
    public ModelRenderer cord4;
    public ModelRenderer chime1;
    public ModelRenderer chime2;
    public ModelRenderer chime3;
    public ModelRenderer chime4;
    public ModelRenderer centerCord;
    public ModelRenderer pendant;

    public ModelSpiritChimes() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.ceilingMount = new ModelRenderer(this, 0, 0);
        this.ceilingMount.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6);

        // Suspension cords
        this.cord1 = new ModelRenderer(this, 24, 0);
        this.cord1.addBox(-2.0F, 1.0F, -2.0F, 1, 6, 1);

        this.cord2 = new ModelRenderer(this, 24, 0);
        this.cord2.addBox(1.0F, 1.0F, -2.0F, 1, 8, 1);

        this.cord3 = new ModelRenderer(this, 24, 0);
        this.cord3.addBox(-2.0F, 1.0F, 1.0F, 1, 9, 1);

        this.cord4 = new ModelRenderer(this, 24, 0);
        this.cord4.addBox(1.0F, 1.0F, 1.0F, 1, 7, 1);

        // Chimes
        this.chime1 = new ModelRenderer(this, 0, 10);
        this.chime1.addBox(-2.5F, 7.0F, -2.5F, 2, 7, 2);

        this.chime2 = new ModelRenderer(this, 0, 10);
        this.chime2.addBox(0.5F, 9.0F, -2.5F, 2, 9, 2);

        this.chime3 = new ModelRenderer(this, 0, 10);
        this.chime3.addBox(-2.5F, 10.0F, 0.5F, 2, 8, 2);

        this.chime4 = new ModelRenderer(this, 0, 10);
        this.chime4.addBox(0.5F, 8.0F, 0.5F, 2, 6, 2);

        // Center cord and pendant
        this.centerCord = new ModelRenderer(this, 28, 0);
        this.centerCord.addBox(-0.5F, 1.0F, -0.5F, 1, 14, 1);

        this.pendant = new ModelRenderer(this, 10, 10);
        this.pendant.addBox(-1.5F, 15.0F, -1.5F, 3, 4, 3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.ceilingMount.render(f5);
        this.cord1.render(f5);
        this.cord2.render(f5);
        this.cord3.render(f5);
        this.cord4.render(f5);
        this.chime1.render(f5);
        this.chime2.render(f5);
        this.chime3.render(f5);
        this.chime4.render(f5);
        this.centerCord.render(f5);
        this.pendant.render(f5);
    }
}

