package pokeregions.monsters.act2.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.UsePreBattleActionAction;
import pokeregions.cards.pokemonAllyCards.act2.HoOh;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.Burn;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class HoOhEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(HoOhEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SACRED_FIRE = 0;
    private static final byte OVERHEAT = 1;
    private static final byte SUMMON = 2;

    public final int BURN_DEBUFF = calcAscensionSpecial(2);
    public final int STATUS = calcAscensionSpecial(1);
    public final int REGEN = calcAscensionSpecial(10);

    private final ArrayList<AbstractMonster> minionList = new ArrayList<>();

    public HoOhEnemy() {
        this(100.0f, 0.0f);
    }

    public HoOhEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("HoOh/HoOh.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(300));
        addMove(SACRED_FIRE, Intent.ATTACK_DEBUFF, calcAscensionDamage(18));
        addMove(OVERHEAT, Intent.ATTACK_DEBUFF, calcAscensionDamage(8), 3);
        addMove(SUMMON, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new RegenerateMonsterPower(this, REGEN));
        CustomDungeon.playTempMusicInstantly("Lysandre");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SACRED_FIRE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                applyToTarget(adp(), this, new Burn(adp(), BURN_DEBUFF));
                break;
            }
            case OVERHEAT: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                }
                intoDiscardMo(new com.megacrit.cardcrawl.cards.status.Burn(), STATUS);
                break;
            }
            case SUMMON: {
                summonMinions(2);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    private void summonMinions(int amount) {
        for (int i = 0; i < amount; i++) {
            summon();
        }
    }

    private void summon() {
        for (int i = 0; i < minionList.size(); i++) {
            AbstractMonster feather = minionList.get(i);
            if (feather.isDeadOrEscaped()) {
                feather = new FlameSpirit(getMinionXCoord(i), getMinionYCoord(i), this);
                minionList.set(i, feather);
                atb(new SpawnMonsterAction(feather, true));
                atb(new UsePreBattleActionAction(feather));
                return;
            }
        }
        FlameSpirit feather = new FlameSpirit(getMinionXCoord(minionList.size()), getMinionYCoord(minionList.size()), this);
        minionList.add(feather);
        atb(new SpawnMonsterAction(feather, true));
    }

    private float getMinionXCoord(int index) {
        int result = index % 2;
        if (result == 0) {
            return -250.0F;
        } else {
            return -500.0F;
        }
    }

    private float getMinionYCoord(int index) {
        return (index / 2) * 200.0F;
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(SUMMON, MOVES[SUMMON]);
        } else if (lastMove(SACRED_FIRE)) {
            setMoveShortcut(OVERHEAT, MOVES[OVERHEAT]);
        } else if (lastMove(OVERHEAT)) {
            setMoveShortcut(SUMMON, MOVES[SUMMON]);
        } else {
            setMoveShortcut(SACRED_FIRE, MOVES[SACRED_FIRE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SACRED_FIRE: {
                Details powerDetail = new Details(this, BURN_DEBUFF, BURN_DEBUFF_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case OVERHEAT: {
                Details statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
            case SUMMON: {
                Details powerDetail = new Details(this, Details.SUMMON);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof FlameSpirit) {
                if (!mo.isDeadOrEscaped()) {
                    atb(new SuicideAction(mo));
                }
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new HoOh();
    }

}