package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Skarmory;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;
import pokeregions.util.ProAudio;
import pokeregions.util.TexLoader;

import java.util.ArrayList;
import java.util.function.BiFunction;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class SkarmoryEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SkarmoryEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SPIKES = 0;
    private static final byte METAL_CLAW = 1;
    private static final byte BRAVE_BIRD = 2;

    public final int STATUS = calcAscensionSpecial(2);
    public final int BUFF = calcAscensionSpecial(2);
    public final int METALLICIZE = 12;
    public final int BLOCK = 14;

    public SkarmoryEnemy() {
        this(0.0f, 0.0f);
    }

    public SkarmoryEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 180.0f, 170.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Skarmory/Skarmory.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(93), calcAscensionTankiness(105));
        addMove(SPIKES, Intent.DEBUFF);
        addMove(METAL_CLAW, Intent.ATTACK_DEFEND, calcAscensionDamage(16));
        addMove(BRAVE_BIRD, Intent.ATTACK, calcAscensionDamage(7), 3);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        block(this, METALLICIZE);
        applyToTarget(this, this, new MetallicizePower(this, METALLICIZE));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SPIKES: {
                useFastAttackAnimation();
                Texture spike = TexLoader.getTexture(makeVfxPath("Spike.png"));
                float spikeDuration = 1.2f;
                float vfxInternal = 0.3f;
                int numSpikes = 5;
                float totalDuration = spikeDuration + (vfxInternal * numSpikes);
                VfxBuilder builder = new VfxBuilder(spike, this.hb.cX, this.hb.cY, spikeDuration)
                        .arc(this.hb.cX, this.hb.cY, adp().hb.cX - (100.0f * Settings.scale), AbstractDungeon.floorY, 700.0f * Settings.scale)
                        .rotateTo(0, 270, VfxBuilder.Interpolations.LINEAR)
                        .triggerVfxAt(spikeDuration, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                            @Override
                            public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                                playAudio(ProAudio.ROCK_THUD);
                                return new VfxBuilder(spike, adp().hb.cX - (100.0f * Settings.scale), AbstractDungeon.floorY, spikeDuration).setAngle(270).build();
                            }
                        });
                for (int i = 0; i < numSpikes; i++) {
                    float randX = adp().hb.cX + (MathUtils.random(-250, 250) * Settings.scale);
                    builder.triggerVfxAt(vfxInternal * (i + 1), 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                        @Override
                        public AbstractGameEffect apply(Float aFloat, Float aFloat2) {

                            return new VfxBuilder(spike, SkarmoryEnemy.this.hb.cX, SkarmoryEnemy.this.hb.cY, spikeDuration)
                                    .arc(SkarmoryEnemy.this.hb.cX, SkarmoryEnemy.this.hb.cY, randX, AbstractDungeon.floorY, 700.0f * Settings.scale)
                                    .rotateTo(0, 270, VfxBuilder.Interpolations.LINEAR)
                                    .triggerVfxAt(spikeDuration, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                                        @Override
                                        public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                                            playAudio(ProAudio.ROCK_THUD);
                                            return new VfxBuilder(spike, randX, AbstractDungeon.floorY, spikeDuration).setAngle(270).build();
                                        }
                                    }).build();
                        }
                    });
                }

                atb(new VFXAction(builder.build(), totalDuration));
                intoDiscardMo(new Wound(), STATUS);
                applyToTarget(this, this, new ThornsPower(this, BUFF));
                break;
            }
            case METAL_CLAW: {
                useFastAttackAnimation();
                block(this, BLOCK);
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case BRAVE_BIRD: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    if (i % 3 == 0) {
                        dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
                    } else if (i % 2 == 0) {
                        dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                    } else {
                        dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                    }
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove && AbstractDungeon.ascensionLevel >= 17) {
            setMoveShortcut(SPIKES, MOVES[SPIKES]);
        } else if (lastMove(BRAVE_BIRD)) {
            setMoveShortcut(METAL_CLAW, MOVES[METAL_CLAW]);
        } else if (lastMove(METAL_CLAW)) {
            setMoveShortcut(SPIKES, MOVES[SPIKES]);
        } else {
            setMoveShortcut(BRAVE_BIRD, MOVES[BRAVE_BIRD]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SPIKES: {
                Details statusDetail = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                Details powerDetail = new Details(this, BUFF, THORNS_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case METAL_CLAW: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Skarmory();
    }

}