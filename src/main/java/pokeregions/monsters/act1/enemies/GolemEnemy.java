package pokeregions.monsters.act1.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Golem;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.ProAudio;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;
import pokeregions.vfx.WaitEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class GolemEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GolemEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ROCK_POLISH = 0;
    private static final byte STEALTH_ROCK = 1;
    private static final byte EARTHQUAKE = 2;

    public final int STR = calcAscensionSpecial(5);
    public final int BLOCK = 6;
    public final int DEBUFF = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("StealthRock");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GolemEnemy() {
        this(0.0f, 0.0f);
    }

    public GolemEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Golem/Golem.scml"));
        setHp(calcAscensionTankiness(130), calcAscensionTankiness(136));
        addMove(ROCK_POLISH, Intent.DEFEND_BUFF);
        addMove(STEALTH_ROCK, Intent.STRONG_DEBUFF);
        addMove(EARTHQUAKE, Intent.ATTACK, calcAscensionDamage(16));
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
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case ROCK_POLISH: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                block(this, BLOCK);
                break;
            }
            case STEALTH_ROCK: {
                useFastAttackAnimation();
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.DEBUFF, false, adp(), DEBUFF) {

                    @Override
                    public void atEndOfTurn(boolean isPlayer) {
                        applyToTarget(owner, owner, new DexterityPower(owner, -amount));
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                });
                break;
            }
            case EARTHQUAKE: {
                Wiz.playAudio(ProAudio.EARTHQUAKE);
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.XLONG, false);
                atb(new VFXAction(new WaitEffect(), 0.3f));
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(STEALTH_ROCK, MOVES[STEALTH_ROCK]);
        } else {
            if (lastTwoMoves(EARTHQUAKE)) {
                setMoveShortcut(ROCK_POLISH, MOVES[ROCK_POLISH]);
            } else {
                setMoveShortcut(EARTHQUAKE, MOVES[EARTHQUAKE]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ROCK_POLISH: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
            case STEALTH_ROCK: {
                String textureString = makePowerPath("StealthRock32.png");
                Texture texture = TexLoader.getTexture(textureString);
                Details powerDetail = new Details(this, DEBUFF, texture);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Golem();
    }

}