package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RiteEmpaticLink extends Rite {
    
    public RiteEmpaticLink() {
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepEmpaticLink(this, initialStage));
    }

    private static class StepEmpaticLink extends RitualStep {
        private final RiteEmpaticLink rite;

        public StepEmpaticLink(RiteEmpaticLink rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    AxisAlignedBB bounds1 = AxisAlignedBB.getBoundingBox((double)(posX - 4), (double)posY, (double)(posZ - 2), (double)(posX - 2), (double)(posY + 3), (double)(posZ + 2));
                    AxisAlignedBB bounds2 = AxisAlignedBB.getBoundingBox((double)(posX + 2), (double)posY, (double)(posZ - 2), (double)(posX + 4), (double)(posY + 3), (double)(posZ + 2));
                    
                    List<EntityPlayer> players1 = world.getEntitiesWithinAABB(EntityPlayer.class, bounds1);
                    List<EntityPlayer> players2 = world.getEntitiesWithinAABB(EntityPlayer.class, bounds2);
                    
                    if (players1.size() == 1 && players2.size() == 1) {
                        EntityPlayer p1 = players1.get(0);
                        EntityPlayer p2 = players2.get(0);

                        syncPotions(p1, p2);
                        syncPotions(p2, p1);

                        if (world.rand.nextInt(10) == 0) {
                            ParticleEffect.HEART.send(SoundEffect.NONE, world, posX + 0.5, posY + 1.0, posZ + 0.5, 1.0, 1.0, 16);
                        }
                    }
                }
                return RitualStep.Result.UPKEEP;
            }
        }

        private void syncPotions(EntityPlayer source, EntityPlayer target) {
            Collection<PotionEffect> effects = source.getActivePotionEffects();
            for (PotionEffect effect : effects) {
                if (!target.isPotionActive(effect.getPotionID())) {
                    target.addPotionEffect(new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(), true));
                }
            }
        }
    }
}
