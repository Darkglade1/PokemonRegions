package code.cards.mewsGameCards;

import code.cards.AbstractEasyCard;


public abstract class AbstractMatchedCard extends AbstractEasyCard {

    public AbstractMatchedCard(final String cardID, final CardType type, final CardColor color) {
        super(cardID, -2, type, CardRarity.SPECIAL, CardTarget.NONE, color);
    }

    public void onMatched() {

    }
}
