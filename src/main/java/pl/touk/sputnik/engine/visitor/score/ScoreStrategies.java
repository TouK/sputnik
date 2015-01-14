package pl.touk.sputnik.engine.visitor.score;

import pl.touk.sputnik.configuration.GeneralOption;

/**
 * Defines types of the strategy used for Gerrit scoring. Names shall match values supported by
 * {@link GeneralOption#SCORE_STRATEGY}.
 */
public enum ScoreStrategies {
    NoScore,
    ScoreAlwaysPass,
    ScorePassIfEmpty,
    ScorePassIfNoErrors;
}
