package pokeregions.monsters.act3.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Dragonite;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.HeavyRain;
import pokeregions.scenes.PokemonScene;
import pokeregions.util.Details;
import pokeregions.util.ProAudio;
import pokeregions.util.TexLoader;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class KyogreEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(KyogreEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte HYDRO_PUMP = 0;
    private static final byte ORIGIN_PULSE = 1;
    private static final byte AQUA_RING = 2;
    private static final byte LIFE_DEW = 3;

    public final int REGEN = 10;
    public final int BLOCK = 25;
    public final int DEBUFF = 2;
    public final int DRAW_DOWN = 1;
    public final int LIFE_DEW_COOLDOWN = 3;
    private int cooldown = LIFE_DEW_COOLDOWN;

    public KyogreEnemy() {
        this(0.0f, 0.0f);
    }

    public KyogreEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 300.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Kyogre/Kyogre.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(550));
        addMove(HYDRO_PUMP, Intent.ATTACK_DEBUFF, calcAscensionDamage(25));
        addMove(ORIGIN_PULSE, Intent.ATTACK, calcAscensionDamage(32));
        addMove(AQUA_RING, Intent.DEFEND_DEBUFF);
        addMove(LIFE_DEW, Intent.BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new HeavyRain(this, 1));
        CustomDungeon.playTempMusicInstantly("HauntedHouse");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case HYDRO_PUMP: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                applyToTarget(adp(), this, new DrawReductionPower(adp(), DRAW_DOWN));
                break;
            }
            case ORIGIN_PULSE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case AQUA_RING: {
                block(this, BLOCK);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case LIFE_DEW: {
                if (AbstractDungeon.ascensionLevel >= 19) {
                    applyToTarget(this, this, new HeavyRain(this, 1));
                }
                applyToTarget(this, this, new RegenerateMonsterPower(this, REGEN));
                break;
            }
        }
        if (this.nextMove == LIFE_DEW) {
            cooldown = LIFE_DEW_COOLDOWN;
        } else {
            cooldown--;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (cooldown <= 0) {
            setMoveShortcut(LIFE_DEW, MOVES[LIFE_DEW]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(HYDRO_PUMP) && !this.lastMoveBefore(HYDRO_PUMP)) {
                possibilities.add(HYDRO_PUMP);
            }
            if (!this.lastMove(ORIGIN_PULSE) && !this.lastMoveBefore(ORIGIN_PULSE)) {
                possibilities.add(ORIGIN_PULSE);
            }
            if (!this.lastMove(AQUA_RING) && !this.lastMoveBefore(AQUA_RING)) {
                possibilities.add(AQUA_RING);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("HeavyRain32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case HYDRO_PUMP: {
                Details powerDetail = new Details(this, DRAW_DOWN, DRAW_DOWN_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case AQUA_RING: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case LIFE_DEW: {
                if (AbstractDungeon.ascensionLevel >= 19) {
                    Details powerDetail2 = new Details(this, 1, texture);
                    details.add(powerDetail2);
                }
                Details powerDetail = new Details(this, REGEN, REGEN_TEXTURE);
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
        onFinalBossVictoryLogic();
        if (PokemonScene.rainSoundId != 0L) {
            CardCrawlGame.sound.stop(makeID(ProAudio.RAIN.name()), PokemonScene.rainSoundId);
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Dragonite();
    }

}