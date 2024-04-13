package pokeregions.monsters.act2.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.dmg;

public class Lugia extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Lugia.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Lugia(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lugia/Lugia.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act2.Lugia.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                float multiplier = (float)info.output / info.base;
                info.output = (int) (info.output * multiplier);
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case MOVE_2: {
                applyToTarget(target, this, new StrengthPower(target, -pokeregions.cards.pokemonAllyCards.act2.Lugia.MOVE_2_EFFECT));
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers(AbstractCreature target) {
        DamageInfo info = getInfoFromMove(this.nextMove);
        info.applyPowers(this, target);
        float multiplier = (float)info.output / info.base;
        applyPowers(target, multiplier);
    }

}