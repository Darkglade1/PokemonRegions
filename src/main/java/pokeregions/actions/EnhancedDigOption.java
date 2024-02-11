package pokeregions.actions;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import static pokeregions.PokemonRegions.makeID;

public class EnhancedDigOption extends AbstractCampfireOption {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    public EnhancedDigOption() {
        this.label = TEXT[0];
        this.description = TEXT[1];
        this.img = ImageMaster.CAMPFIRE_DIG_BUTTON;
    }

    public void useOption() {
        AbstractDungeon.effectList.add(new EnhancedCampfireDigEffect());
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("EnhancedDigOption"));
        TEXT = uiStrings.TEXT;
    }
}
