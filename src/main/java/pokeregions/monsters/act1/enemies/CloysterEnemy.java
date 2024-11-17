package pokeregions.monsters.act1.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Cloyster;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;
import pokeregions.vfx.ColoredThrowDaggerEffect;
import pokeregions.vfx.WaitEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class CloysterEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(CloysterEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SHELL_SMASH = 0;
    private static final byte RAZOR_SHELL = 1;
    private static final byte ICICLE_SPEAR = 2;

    public final int METALLICIZE = 10;
    public final int STR = calcAscensionSpecialSmall(3);
    public final int DEBUFF = 1;
    public final int STATUS = 1;

    public CloysterEnemy() {
        this(0.0f, 0.0f);
    }

    public CloysterEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Cloyster/Cloyster.scml"));
        setHp(calcAscensionTankiness(90));
        addMove(SHELL_SMASH, Intent.BUFF);
        addMove(RAZOR_SHELL, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(ICICLE_SPEAR, Intent.ATTACK, calcAscensionDamage(6), 3);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        CustomDungeon.playTempMusicInstantly("WildPokemon");
        applyToTarget(this, this, new MetallicizePower(this, METALLICIZE));
        block(this, METALLICIZE);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SHELL_SMASH: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                applyToTarget(this, this, new VulnerablePower(this, 1, true));
                break;
            }
            case RAZOR_SHELL: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                if (AbstractDungeon.ascensionLevel >= 18) {
                    intoDrawMo(new Wound(), STATUS);
                }
                intoDiscardMo(new Wound(), STATUS);
                break;
            }
            case ICICLE_SPEAR: {
                runAnim("Spear");
                atb(new VFXAction(new WaitEffect(), 0.2f));
                for (int i = 0; i < multiplier; i++) {
                    atb(new VFXAction(new ColoredThrowDaggerEffect(adp().hb.cX, adp().hb.cY, Color.CYAN.cpy(), true)));
                    dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(ICICLE_SPEAR)) {
            setMoveShortcut(SHELL_SMASH, MOVES[SHELL_SMASH]);
        } else if (lastMove(RAZOR_SHELL)) {
            setMoveShortcut(ICICLE_SPEAR, MOVES[ICICLE_SPEAR]);
        } else {
            setMoveShortcut(RAZOR_SHELL, MOVES[RAZOR_SHELL]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SHELL_SMASH: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details vulnerableDetail = new Details(this, 1, VULNERABLE_TEXTURE, Details.TargetType.SELF);
                details.add(vulnerableDetail);
                break;
            }
            case RAZOR_SHELL: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail2);
                if (AbstractDungeon.ascensionLevel >= 18) {
                    Details statusDetails = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DRAW_PILE);
                    details.add(statusDetails);
                }
                Details statusDetails2 = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetails2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Cloyster();
    }

}