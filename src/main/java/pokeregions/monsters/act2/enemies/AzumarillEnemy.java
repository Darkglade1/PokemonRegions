package pokeregions.monsters.act2.enemies;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.cards.pokemonAllyCards.act2.Azumarill;
import pokeregions.monsters.AbstractPokemonMonster;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class AzumarillEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(AzumarillEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte FOCUS = 0;
    private static final byte PLAY_ROUGH = 1;
    public final int REGEN = 3;

    public AzumarillEnemy() {
        this(0.0f, 0.0f);
    }

    public AzumarillEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 140.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Azumarill/Azumarill.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(110), calcAscensionTankiness(122));
        addMove(FOCUS, Intent.UNKNOWN);
        addMove(PLAY_ROUGH, Intent.ATTACK, calcAscensionDamage(30));

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        if (AbstractDungeon.ascensionLevel >= 17) {
            applyToTarget(this, this, new RegenerateMonsterPower(this, REGEN));
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case FOCUS: {
                break;
            }
            case PLAY_ROUGH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(FOCUS)) {
            setMoveShortcut(PLAY_ROUGH, MOVES[PLAY_ROUGH]);
        } else {
            setMoveShortcut(FOCUS, MOVES[FOCUS]);
        }
        super.postGetMove();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Azumarill();
    }

}