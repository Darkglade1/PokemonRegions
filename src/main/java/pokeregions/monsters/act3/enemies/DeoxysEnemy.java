package pokeregions.monsters.act3.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Deoxys;
import pokeregions.monsters.AbstractMultiIntentMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.AdditionalIntent;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class DeoxysEnemy extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(DeoxysEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte POWER_UP_PUNCH = 0;
    private static final byte EXPOSE_WEAKNESS = 1;
    private static final byte PSYCHO_BOOST = 2;

    private static final byte COSMIC_POWER = 3;
    private static final byte IRON_DEFENSE = 4;
    private static final byte HEAVY_SLAM = 5;

    private static final byte SWIFT = 6;
    private static final byte NASTY_PLOT = 7;
    private static final byte EXTREME_SPEED = 8;

    public final int STR = calcAscensionSpecialSmall(3);
    public final int DEBUFF = 2;

    public final int BLOCK = 20;
    public final int BIG_BLOCK = 30;
    public final int METALICIZE = calcAscensionSpecialSmall(10);

    public final int SLIMES = calcAscensionSpecial(2);
    public final int PLOT = calcAscensionSpecial(2);
    public final int DAZES = calcAscensionSpecial(2);
    public final int HEAVY_SLAM_BASE_DAMAGE = calcAscensionDamage(35);
    public final int HEAVY_SLAM_DAMAGE_INCREASE = calcAscensionDamage(15);

    private Form currentForm = Form.ATTACK;
    private int turnCount = 1;

    public static final String POWER_ID = makeID("Adapt");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DeoxysEnemy() {
        this(0.0f, 0.0f);
    }

    public DeoxysEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 210.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Deoxys/Deoxys.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.25f);
        setHp(calcAscensionTankiness(400));
        addMove(POWER_UP_PUNCH, Intent.ATTACK_BUFF, calcAscensionDamage(8));
        //addMove(EXPOSE_WEAKNESS, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(PSYCHO_BOOST, Intent.ATTACK, calcAscensionDamage(14), 3);
        addMove(COSMIC_POWER, Intent.DEFEND_BUFF);
        //addMove(IRON_DEFENSE, Intent.DEFEND_BUFF);
        addMove(HEAVY_SLAM, Intent.ATTACK_DEFEND, HEAVY_SLAM_BASE_DAMAGE);
        addMove(SWIFT, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        addMove(NASTY_PLOT, Intent.BUFF);
        addMove(EXTREME_SPEED, Intent.ATTACK_DEBUFF, calcAscensionDamage(18), 2);
    }

    public enum Form {
        ATTACK, DEFENSE, SPEED
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
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0, "evolve") {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                switch (card.type) {
                    case ATTACK:
                        if (owner instanceof DeoxysEnemy) {
                            ((DeoxysEnemy) owner).changeForm(Form.ATTACK);
                            ((DeoxysEnemy) owner).runAnim("Attack");
                        }
                        break;
                    case SKILL:
                        if (owner instanceof DeoxysEnemy) {
                            ((DeoxysEnemy) owner).changeForm(Form.DEFENSE);
                            ((DeoxysEnemy) owner).runAnim("Defense");
                        }
                        break;
                    case POWER:
                        if (owner instanceof DeoxysEnemy) {
                            ((DeoxysEnemy) owner).changeForm(Form.SPEED);
                            ((DeoxysEnemy) owner).runAnim("Speed");
                        }
                        break;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case POWER_UP_PUNCH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case PSYCHO_BOOST: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                }
                break;
            }
            case COSMIC_POWER: {
                block(this, BLOCK);
                applyToTarget(this, this, new MetallicizePower(this, METALICIZE));
                break;
            }
            case HEAVY_SLAM: {
                useFastAttackAnimation();
                block(this, BIG_BLOCK);
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                int newDamage = moves.get(HEAVY_SLAM).baseDamage += HEAVY_SLAM_DAMAGE_INCREASE;
                addMove(HEAVY_SLAM, Intent.ATTACK_DEFEND, newDamage);
                break;
            }
            case SWIFT: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                intoDiscardMo(new Slimed(), SLIMES);
                break;
            }
            case NASTY_PLOT: {
                applyToTarget(this, this, new NastyPlot(this, PLOT));
                break;
            }
            case EXTREME_SPEED: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                intoDrawMo(new Dazed(), DAZES);
                break;
            }
        }
        turnCount++;
        if (turnCount > 3) {
            turnCount = 1;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        byte nextMove = 0;
        switch (this.currentForm) {
            case ATTACK:
                nextMove = getAttackFormMove();
                setAdditionalMoveShortcut(getDefenseFormMove(), moveHistory, 1);
                setAdditionalMoveShortcut(getSpeedFormMove(), moveHistory, 2);
                break;
            case DEFENSE:
                setAdditionalMoveShortcut(getAttackFormMove(), moveHistory, -1);
                nextMove = getDefenseFormMove();
                setAdditionalMoveShortcut(getSpeedFormMove(), moveHistory, 1);
                break;
            case SPEED:
                setAdditionalMoveShortcut(getAttackFormMove(), moveHistory, -2);
                setAdditionalMoveShortcut(getDefenseFormMove(), moveHistory, -1);
                nextMove = getSpeedFormMove();
                break;
        }
        setMoveShortcut(nextMove, MOVES[nextMove]);
        for (AdditionalIntent additionalIntent : additionalIntents) {
            additionalIntent.transparent = true;
            additionalIntent.usePrimaryIntentsColor = false;
        }
        super.postGetMove();
    }

    public void changeForm(Form newForm) {
        this.currentForm = newForm;
        rollMove();
        createIntent();
    }

    private byte getAttackFormMove() {
        if (turnCount == 3) {
            return PSYCHO_BOOST;
        } else if (turnCount == 2) {
            return PSYCHO_BOOST;
        } else {
            return POWER_UP_PUNCH;
        }
    }

    private byte getDefenseFormMove() {
        if (turnCount == 3) {
            return HEAVY_SLAM;
        } else if (turnCount == 2) {
            return COSMIC_POWER;
        } else {
            return HEAVY_SLAM;
        }
    }

    private byte getSpeedFormMove() {
        if (turnCount == 3) {
            return EXTREME_SPEED;
        } else if (turnCount == 2) {
            return NASTY_PLOT;
        } else {
            return SWIFT;
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent);
            }
        }
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case POWER_UP_PUNCH: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case COSMIC_POWER: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, METALICIZE, METALLICIZE_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case HEAVY_SLAM: {
                Details blockDetail = new Details(this, BIG_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
            case SWIFT: {
                Details statusDetail = new Details(this, SLIMES, SLIMED_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
            case NASTY_PLOT: {
                Details powerDetail = new Details(this, PLOT, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case EXTREME_SPEED: {
                Details statusDetail = new Details(this, DAZES, DAZED_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Deoxys();
    }

}