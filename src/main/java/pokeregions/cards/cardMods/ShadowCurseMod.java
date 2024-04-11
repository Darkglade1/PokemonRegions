package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.PokemonRegions;
import pokeregions.vfx.CardBurningEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class ShadowCurseMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(ShadowCurseMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final float BLOCK_MOD = 1.5F;
    private final float DAMAGE_MOD = 0.5F;
    private ArrayList<AbstractGameEffect> flameEffect = new ArrayList<>();
    private float fireTimer;

    @Override
    public AbstractCardModifier makeCopy() {
        return new ShadowCurseMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
        if (card.baseBlock >= 0) {
            card.baseBlock = (int)((float)card.baseBlock * BLOCK_MOD);
        }
        if (card.baseDamage >= 0) {
            card.baseDamage = (int)((float)card.baseDamage * DAMAGE_MOD);
        }
        flameEffect.add(new CardBurningEffect(card));
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }

    @Override
    public void onUpdate(AbstractCard card) {
        this.fireTimer -= Gdx.graphics.getDeltaTime();
        if (this.fireTimer < 0.0F) {
            this.fireTimer = 0.05F;
            flameEffect.add(new CardBurningEffect(card));
        }

        Iterator<AbstractGameEffect> i = this.flameEffect.iterator();
        while(i.hasNext()) {
            AbstractGameEffect fires = i.next();
            fires.update();
            if (fires.isDone) {
                i.remove();
            }
        }
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        for (AbstractGameEffect effect : flameEffect) {
            effect.render(sb);
        }
    }
}
