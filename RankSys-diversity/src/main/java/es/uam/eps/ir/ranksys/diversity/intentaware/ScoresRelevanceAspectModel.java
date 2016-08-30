/*
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.List;

/**
 * Relevance-based aspect model for eXplicit Query Aspect Diversification re-ranker.
 *
 * S. Vargas, P. Castells and D. Vallet. Explicit relevance models in intent-oriented Information Retrieval diversification. SIGIR 2012.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public class ScoresRelevanceAspectModel<U, I, F> extends AspectModel<U, I, F> {

    /**
     * Constructor taking an intent model and scores data.
     *
     * @param intentModel intent mode
     */
    public ScoresRelevanceAspectModel(IntentModel<U, I, F> intentModel) {
        super(intentModel);
    }

    @Override
    protected UserAspectModel get(U u) {
        return new ScoresUserRelevanceAspectModel(u);
    }

    /**
     * User aspect model for {@link ScoresRelevanceAspectModel}.
     */
    public class ScoresUserRelevanceAspectModel extends UserAspectModel {

        private final Object2DoubleOpenHashMap<F> probNorm;

        /**
         * Constructor.
         *
         * @param user user
         */
        public ScoresUserRelevanceAspectModel(U user) {
            super(user);
            this.probNorm = new Object2DoubleOpenHashMap<>();
        }

        @Override
        public void initializeWithItems(List<Tuple2od<I>> items) {
            probNorm.clear();
            items.forEach(iv -> {
                getItemIntents(iv.v1).sequential().forEach(f -> {
                    if (iv.v2 > probNorm.getOrDefault(f, 0.0)) {
                        probNorm.put(f, iv.v2);
                    }
                });
            });
        }

        @Override
        public double pi_f(Tuple2od<I> iv, F f) {
            return (Math.pow(2, iv.v2 / probNorm.getDouble(f)) - 1) / 2.0;
        }
    }
}