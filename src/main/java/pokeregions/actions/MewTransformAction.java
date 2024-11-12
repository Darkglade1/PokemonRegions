package pokeregions.actions;

import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Mew;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.powers.AbstractEasyPower;
import pokeregions.relics.OnPokemonSwitchRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class MewTransformAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MewTransformAction"));
    private static final String[] TEXT = uiStrings.TEXT;
    private final AbstractAllyPokemonCard mewCard;

    public MewTransformAction(AbstractAllyPokemonCard mewCard) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.mewCard = mewCard;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            CardGroup availablePokemon =  new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
                if (card instanceof AbstractAllyPokemonCard) {
                    if (!card.cardID.equals(Mew.ID)) {
                        availablePokemon.addToTop(card);
                    }
                }
            }
            if (availablePokemon.size() > 0) {
                AbstractDungeon.gridSelectScreen.open(availablePokemon, 1, TEXT[0], false);
                this.tickDuration();
            } else {
                this.isDone = true;
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard selectedPokemon = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                if (selectedPokemon instanceof AbstractAllyPokemonCard) {
                    AbstractAllyPokemonCard pokemonCard = (AbstractAllyPokemonCard)selectedPokemon;
                    AbstractPokemonAlly pokemon = pokemonCard.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
                    if (pokemon != null) {
                        mewCard.move1Name = pokemon.allyCard.move1Name;
                        mewCard.move2Name = pokemon.allyCard.move2Name;
                        mewCard.move1Description = pokemon.allyCard.move1Description;
                        mewCard.move2Description = pokemon.allyCard.move2Description;
                        mewCard.move1isLimited = pokemon.allyCard.move1isLimited;
                        mewCard.move2isLimited = pokemon.allyCard.move2isLimited;
                        if (mewCard instanceof Mew) {
                            ((Mew) mewCard).setUsedLimitedMoves(pokemon.allyCard);
                        }
                        pokemon.allyCard = mewCard;
                        pokemon.name = mewCard.name;
                        pokemon.currentHealth = mewCard.currentStamina;
                        pokemon.maxHealth = mewCard.maxStamina;
                        atb(new RemoveMonsterAction(PlayerSpireFields.activePokemon.get(adp())));
                        PlayerSpireFields.activePokemon.set(adp(), pokemon);
                        PlayerSpireFields.mostRecentlyUsedPokemonCardID.set(adp(), mewCard.cardID);
                        atb(new SpawnMonsterAction(pokemon, false));
                        atb(new UsePreBattleActionAction(pokemon));
                        for (AbstractRelic relic : adp().relics) {
                            if (relic instanceof OnPokemonSwitchRelic) {
                                ((OnPokemonSwitchRelic) relic).onPokemonSwitch(pokemon);
                            }
                        }
                        for (AbstractPower power : adp().powers) {
                            if (power instanceof AbstractEasyPower) {
                                ((AbstractEasyPower) power).onPokemonSwitch(pokemon);
                            }
                        }
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.tickDuration();
        }
    }
}
