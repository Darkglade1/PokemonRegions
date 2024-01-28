package pokeregions.monsters.act1.enemies;

import basemod.ReflectionHacks;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Vulpix;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.Burn;
import pokeregions.util.Details;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class VulpixEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(VulpixEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte WISP = 0;
    private static final byte FIRE_SPIN = 1;
    private static final byte FLAME_CHARGE = 2;

    public final int STATUS = 1;
    public final int DEBUFF = 1;

    public VulpixEnemy() {
        this(0.0f, 0.0f);
    }

    public VulpixEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Vulpix/Vulpix.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(62), calcAscensionTankiness(66));
        addMove(WISP, Intent.DEBUFF);
        addMove(FIRE_SPIN, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(FLAME_CHARGE, Intent.ATTACK, calcAscensionDamage(5), 2);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case WISP: {
                if (AbstractDungeon.ascensionLevel >= 17) {
                    intoDrawMo(new com.megacrit.cardcrawl.cards.status.Burn(), STATUS);
                } else {
                    intoDiscardMo(new com.megacrit.cardcrawl.cards.status.Burn(), STATUS);
                }
                applyToTarget(adp(), this, new Burn(adp(), DEBUFF));
                break;
            }
            case FIRE_SPIN: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                applyToTarget(adp(), this, new Burn(adp(), DEBUFF));
                break;
            }
            case FLAME_CHARGE: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(WISP)) {
            possibilities.add(WISP);
        }
        if (!this.lastMove(FIRE_SPIN)) {
            possibilities.add(FIRE_SPIN);
        }
        if (adp().hasPower(Burn.POWER_ID) && !this.lastMove(FLAME_CHARGE)) {
            possibilities.add(FLAME_CHARGE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);

        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case WISP: {
                Details statusDetail;
                if (AbstractDungeon.ascensionLevel >= 17) {
                    statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DRAW_PILE);
                } else {
                    statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                }
                details.add(statusDetail);

                Details powerDetail = new Details(this, DEBUFF, BURN_DEBUFF_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case FIRE_SPIN: {
                Details powerDetail2 = new Details(this, DEBUFF, BURN_DEBUFF_TEXTURE);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Vulpix();
    }

}