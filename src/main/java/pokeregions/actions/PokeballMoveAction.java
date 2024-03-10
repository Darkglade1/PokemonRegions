package pokeregions.actions;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.patches.Act3PreventOtherRewards;
import pokeregions.relics.AbstractEasyRelic;
import pokeregions.relics.PokeballBelt;
import pokeregions.util.PokeballMove;
import pokeregions.util.PokemonReward;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.att;

public class PokeballMoveAction extends AbstractGameAction {

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokeballMove"));
    private static final String[] TEXT = uiStrings.TEXT;

    AbstractPokemonMonster target;
    PokeballMove pokeball;

    public PokeballMoveAction(AbstractPokemonMonster target, PokeballMove pokeball) {
        this.actionType = ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_FAST;
        this.target = target;
        this.pokeball = pokeball;
    }

    @Override
    public void update() {
        if (target.captured) {
            isDone = true;
            return;
        }
        AbstractDungeon.player.loseEnergy(1);
        AbstractRelic pokeBallBelt = adp().getRelic(PokeballBelt.ID);
        if (pokeBallBelt != null) {
            pokeBallBelt.counter--;
            if (pokeBallBelt instanceof AbstractEasyRelic) {
                ((AbstractEasyRelic) pokeBallBelt).fixDescription();
            }
        }
        int roll = AbstractDungeon.miscRng.random(1, 100);
        int chance = pokeball.calculateCaptureChance(target);
        if (roll <= chance || Settings.isDebug) {
            target.captured = true;
            target.currentBlock = 0;
            att(new SuicideAction(target));
            AbstractCard pokemonCard = target.getAssociatedPokemonCard();
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemonCard.cardID));
            AbstractDungeon.effectList.add(new BlockedWordEffect(target, target.hb.cX, target.hb.cY, TEXT[5]));
            if (Act3PreventOtherRewards.isHoennBoss()) {
                ((CustomDungeon)CardCrawlGame.dungeon).preventFinalActRewards = false;
            }
        } else {
            AbstractDungeon.effectList.add(new BlockedWordEffect(target, target.hb.cX, target.hb.cY, TEXT[6]));
        }
        isDone = true;
    }
}
