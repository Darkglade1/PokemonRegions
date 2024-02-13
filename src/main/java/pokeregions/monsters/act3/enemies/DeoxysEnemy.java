package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Deoxys;
import pokeregions.monsters.AbstractMultiIntentMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.IronDefense;
import pokeregions.powers.NastyPlot;
import pokeregions.powers.VisibleBarricadePower;
import pokeregions.util.AdditionalIntent;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;

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

    public final int BLOCK = 12;
    public final int BIG_BLOCK = 30;
    public final int METALICIZE = calcAscensionSpecial(20);

    public final int SLIMES = calcAscensionSpecial(2);
    public final int PLOT = calcAscensionSpecialSmall(3);
    public final int DAZES = calcAscensionSpecial(2);
    public final int EXTREME_SPEED_DAMAGE = calcAscensionDamage(20);
    private int numExtremeSpeedHits = 2;

    private Form currentForm = Form.ATTACK;
    private int attackFormIntent = 1;
    private int defenseFormIntent = 1;
    private int speedFormIntent = 1;

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
        addMove(EXPOSE_WEAKNESS, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(PSYCHO_BOOST, Intent.ATTACK_BUFF, calcAscensionDamage(13), 3);
        addMove(COSMIC_POWER, Intent.DEFEND);
        addMove(IRON_DEFENSE, Intent.DEFEND_BUFF);
        addMove(HEAVY_SLAM, Intent.ATTACK_DEFEND, calcAscensionDamage(16));
        addMove(SWIFT, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        addMove(NASTY_PLOT, Intent.BUFF);
        addMove(EXTREME_SPEED, Intent.ATTACK_DEBUFF, EXTREME_SPEED_DAMAGE, numExtremeSpeedHits);
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
                attackFormIntent++;
                break;
            }
            case EXPOSE_WEAKNESS: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                attackFormIntent++;
                break;
            }
            case PSYCHO_BOOST: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                }
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case COSMIC_POWER: {
                block(this, BLOCK);
                applyToTarget(this, this, new VisibleBarricadePower(this));
                defenseFormIntent++;
                break;
            }
            case IRON_DEFENSE: {
                block(this, BIG_BLOCK);
                applyToTarget(this, this, new IronDefense(this));
                defenseFormIntent++;
                break;
            }
            case HEAVY_SLAM: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                block(this, BIG_BLOCK);
                applyToTarget(this, this, new MetallicizePower(this, METALICIZE));
                break;
            }
            case SWIFT: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                intoDiscardMo(new Slimed(), SLIMES);
                speedFormIntent++;
                break;
            }
            case NASTY_PLOT: {
                applyToTarget(this, this, new NastyPlot(this, PLOT));
                speedFormIntent++;
                break;
            }
            case EXTREME_SPEED: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                for (int i = 0; i < multiplier - 1; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                intoDrawMo(new Dazed(), DAZES);
                numExtremeSpeedHits++;
                addMove(EXTREME_SPEED, Intent.ATTACK_DEBUFF, EXTREME_SPEED_DAMAGE, numExtremeSpeedHits);
                break;
            }
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
        if (this.attackFormIntent == 1) {
            return POWER_UP_PUNCH;
        } else if (this.attackFormIntent == 2) {
            return EXPOSE_WEAKNESS;
        } else {
            return PSYCHO_BOOST;
        }
    }

    private byte getDefenseFormMove() {
        if (this.defenseFormIntent == 1) {
            return COSMIC_POWER;
        } else if (this.defenseFormIntent == 2) {
            return IRON_DEFENSE;
        } else {
            return HEAVY_SLAM;
        }
    }

    private byte getSpeedFormMove() {
        if (this.speedFormIntent == 1) {
            return SWIFT;
        } else if (this.speedFormIntent == 2) {
            return NASTY_PLOT;
        } else {
            return EXTREME_SPEED;
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
        String textureString = makeUIPath("Barricade.png");
        Texture texture1 = TexLoader.getTexture(textureString);
        String textureString2 = makePowerPath("IronDefense32.png");
        Texture texture2 = TexLoader.getTexture(textureString2);
        switch (move.nextMove) {
            case POWER_UP_PUNCH:
            case PSYCHO_BOOST: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case EXPOSE_WEAKNESS: {
                Details powerDetail = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case COSMIC_POWER: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, 1, texture1);
                details.add(powerDetail);
                break;
            }
            case IRON_DEFENSE: {
                Details blockDetail = new Details(this, BIG_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, 1, texture2);
                details.add(powerDetail);
                break;
            }
            case HEAVY_SLAM: {
                Details blockDetail = new Details(this, BIG_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, METALICIZE, METALLICIZE_TEXTURE);
                details.add(powerDetail);
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
    public void damage(DamageInfo info) {
        super.damage(info);
        AbstractDungeon.onModifyPower();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Deoxys();
    }

}