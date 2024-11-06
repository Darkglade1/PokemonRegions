package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Lanturn;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.applyToTarget;

public class LanturnAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Lanturn.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public LanturnAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 130.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lanturn/Lanturn.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.MAGIC;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(target, this, new WeakPower(target, Lanturn.MOVE_1_EFFECT, false));
                applyToTarget(target, this, new VulnerablePower(target, Lanturn.MOVE_1_EFFECT, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
            case MOVE_2: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new VulnerablePower(mo, Lanturn.MOVE_2_EFFECT, AbstractDungeon.actionManager.turnHasEnded));
                }
                break;
            }
        }
        postTurn();
    }

}