package pokeregions.actions;

import pokeregions.util.TexLoader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.RegalPillow;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepScreenCoverEffect;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.adp;

public class HealPokemonCampfireOption extends AbstractCampfireOption
{
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    public static final float HEAL_PERCENT = 0.15f;
    public static final int STAMINA_HEAL = 3;

    public HealPokemonCampfireOption() {
        this.label = HealPokemonCampfireOption.TEXT[0];
        int healAmt = (int)((float)AbstractDungeon.player.maxHealth * HEAL_PERCENT);

        if (adp().hasRelic(RegalPillow.ID)) {
            this.description = TEXT[1] + (int)(HEAL_PERCENT * 100) + TEXT[2] + healAmt + "+15" + TEXT[3];
        } else {
            this.description = TEXT[1] + (int)(HEAL_PERCENT * 100) + TEXT[2] + healAmt + TEXT[3];
        }
        this.description += TEXT[4] + STAMINA_HEAL + TEXT[5];
        this.img = TexLoader.getTexture(makeUIPath("Sleeping.png"));
        this.updateUsability(true);
    }

    public void updateUsability(boolean canUse) {
        if (!canUse) {
            this.description = TEXT[6];
        }
    }
    
    @Override
    public void useOption() {
        CardCrawlGame.sound.play("SLEEP_BLANKET");
        AbstractDungeon.effectList.add(new PokemonCampfireSleepEffect());
        for(int i = 0; i < 30; ++i) {
            AbstractDungeon.topLevelEffects.add(new CampfireSleepScreenCoverEffect());
        }
    }
    
    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("HealPokemonCampfire"));
        TEXT = HealPokemonCampfireOption.uiStrings.TEXT;
    }
}