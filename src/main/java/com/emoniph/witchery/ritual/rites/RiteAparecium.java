package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RiteAparecium extends Rite {
    private final int radius;
    private final int duration;

    public RiteAparecium(int radius, int duration) {
        this.radius = radius;
        this.duration = duration;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepAparecium(this, initialStage));
    }

    private static class StepAparecium extends RitualStep {
        private final RiteAparecium rite;

        public StepAparecium(RiteAparecium rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)(posX - this.rite.radius), (double)posY, (double)(posZ - this.rite.radius), (double)(posX + this.rite.radius + 1), (double)(posY + 3), (double)(posZ + this.rite.radius + 1));
                    List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);
                    
                    for (EntityLivingBase entity : entities) {
                        if (entity.isPotionActive(Potion.invisibility)) {
                            entity.removePotionEffect(Potion.invisibility.id);
                        }
                        entity.addPotionEffect(new PotionEffect(Potion.nightVision.id, this.rite.duration, 0));
                        ParticleEffect.SPELL.send(SoundEffect.MOB_ZOMBIE_INFECT, entity, 1.0, 2.0, 16);
                    }
                }
                return RitualStep.Result.COMPLETED;
            }
        }
    }
}
