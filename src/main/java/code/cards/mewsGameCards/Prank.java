package code.cards.mewsGameCards;

import basemod.AutoAdd;
import code.PokemonRegions;
import com.megacrit.cardcrawl.cards.curses.Clumsy;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
@AutoAdd.Ignore
public class Prank extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Prank.class.getSimpleName());

    public Prank() {
        super(ID, CardType.CURSE, CardColor.CURSE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onMatched() {
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Clumsy(), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
    }
}
