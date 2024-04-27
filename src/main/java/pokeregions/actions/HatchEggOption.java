package pokeregions.actions;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import pokeregions.util.TexLoader;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeUIPath;

public class HatchEggOption extends AbstractCampfireOption {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    public HatchEggOption() {
        this.label = TEXT[0];
        this.description = TEXT[1];
        this.img = TexLoader.getTexture(makeUIPath("Hatch.png"));
    }

    public void useOption() {
        AbstractDungeon.effectList.add(new HatchEggEffect());
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("HatchEggOption"));
        TEXT = uiStrings.TEXT;
    }
}
